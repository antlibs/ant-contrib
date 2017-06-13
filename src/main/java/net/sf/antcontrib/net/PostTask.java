/*
 * Copyright (c) 2001-2004 Ant-Contrib project.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.antcontrib.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.property.ResolvePropertyMap;

/**
 * This task does an http post. Name/value pairs for the post can be set in
 * either or both of two ways, by nested Prop elements and/or by a file
 * containing properties. Nested Prop elements are automatically configured by
 * Ant. Properties from a file are configured by code borrowed from Property so
 * all Ant property constructs (like ${somename}) are resolved prior to the
 * post. This means that a file can be set up in advance of running the build
 * and the appropriate property values will be filled in at run time.
 *
 * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
 * @version $Revision: 1.11 $
 */
public class PostTask extends Task {
    /**
     * Storage for name/value pairs to send.
     */
    private final Map<String, String> props = new HashMap<String, String>();

    /**
     * URL to send the name/value pairs to.
     */
    private URL to = null;

    /**
     * File to read name/value pairs from.
     */
    private File propsFile = null;

    /**
     * storage for Ant properties.
     */
    private String textProps = null;

    /**
     * encoding to use for the name/value pairs.
     */
    private String encoding = "UTF-8";

    /**
     * where to store the server response.
     */
    private File log = null;

    /**
     * append to the log.
     */
    private boolean append = true;

    /**
     * verbose.
     */
    private boolean verbose = true;

    /**
     * want to keep the server response.
     */
    private boolean wantResponse = true;

    /**
     * store output in a property.
     */
    private String property = null;

    /**
     * how long to wait for a response from the server in milliseconds.
     */
    private long maxwait = 180000;

    /**
     * fail on error.
     */
    private boolean failOnError = false;

    /**
     * storage for cookies.
     */
    private static final Map<String, Cookie> cookieStorage = new HashMap<String, Cookie>();

    /**
     * connection to the server.
     */
    private URLConnection connection = null;

    /**
     * for thread handling.
     */
    private Thread currentRunner = null;

    /**
     * Set the url to post to. Required.
     *
     * @param name the url to post to.
     */
    public void setTo(URL name) {
        to = name;
    }

    /**
     * Set the name of a file to read a set of properties from.
     *
     * @param f the file
     */
    public void setFile(File f) {
        propsFile = f;
    }

    /**
     * Set the name of a file to save the response to. Optional. Ignored if
     * "want response" is false.
     *
     * @param f the file
     */
    public void setLogfile(File f) {
        log = f;
    }

    /**
     * Should the log file be appended to or overwritten? Default is true,
     * append to the file.
     *
     * @param b append or not
     */
    public void setAppend(boolean b) {
        append = b;
    }

    /**
     * If true, progress messages and returned data from the post will be
     * displayed. Default is true.
     *
     * @param b true = verbose
     */
    public void setVerbose(boolean b) {
        verbose = b;
    }

    /**
     * Default is true, get the response from the post. Can be set to false for
     * "fire and forget" messages.
     *
     * @param b print/log server response
     */
    public void setWantresponse(boolean b) {
        wantResponse = b;
    }

    /**
     * Set the name of a property to save the response to. Optional. Ignored if
     * "wantResponse" is false.
     *
     * @param name the name to use for the property
     */
    public void setProperty(String name) {
        property = name;
    }

    /**
     * Sets the encoding of the outgoing properties, default is UTF-8.
     *
     * @param encoding The new encoding value
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * How long to wait on the remote server. As a post is generally a two part
     * process (sending and receiving), maxwait is applied separately to each
     * part, that is, if 180 is passed as the wait parameter, this task will
     * spend at most 3 minutes to connect to the remote server and at most
     * another 3 minutes waiting on a response after the post has been sent.
     * This means that the wait period could total as much as 6 minutes (or 360
     * seconds).
     * <p>The default wait period is 3 minutes (180 seconds).</p>
     *
     * @param wait time to wait in seconds, set to 0 to wait forever.
     */
    public void setMaxwait(int wait) {
        maxwait = wait * 1000;
    }

    /**
     * Should the build fail if the post fails.
     *
     * @param fail true = fail the build, default is false
     */
    public void setFailonerror(boolean fail) {
        failOnError = fail;
    }

    /**
     * Adds a name/value pair to post. Optional.
     *
     * @param p A property pair to send as part of the post.
     * @throws BuildException When name and/or value are missing.
     */
    public void addConfiguredProp(Prop p) throws BuildException {
        String name = p.getName();
        if (name == null) {
            throw new BuildException("name is null", getLocation());
        }
        String value = p.getValue();
        if (value == null) {
            value = getProject().getProperty(name);
        }
        if (value == null) {
            throw new BuildException("value is null", getLocation());
        }
        props.put(name, value);
    }

    /**
     * Adds a feature to the Text attribute of the PostTask object.
     *
     * @param text The feature to be added to the Text attribute
     */
    public void addText(String text) {
        textProps = text;
    }

    /**
     * Do the post.
     *
     * @throws BuildException On any error.
     */
    public void execute() throws BuildException {
        if (to == null) {
            throw new BuildException("'to' attribute is required", getLocation());
        }
        final String content = getContent();
        try {
            if (verbose) {
                log("Opening connection for post to " + to.toString() + "...");
            }

            // do the POST
            Thread runner = new Thread() {
                public void run() {
                    DataOutputStream out = null;
                    try {
                        // set the url connection properties
                        connection = to.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        connection.setRequestProperty(
                                "Content-Type",
                                "application/x-www-form-urlencoded");

                        // check if there are cookies to be included
                        for (String key : cookieStorage.keySet()) {
                            if (key != null) {
                                Cookie cookie = cookieStorage.get(key);
                                if (to.getPath().startsWith(cookie.getPath())) {
                                    connection.addRequestProperty("Cookie", cookie.toString());
                                }
                            }
                        }

                        // do the post
                        if (verbose) {
                            log("Connected, sending data...");
                        }
                        out = new DataOutputStream(connection.getOutputStream());
                        if (verbose) {
                            log(content);
                        }
                        out.writeBytes(content);
                        out.flush();
                        if (verbose) {
                            log("Data sent.");
                        }
                    } catch (Exception e) {
                        if (failOnError) {
                            throw new BuildException(e, getLocation());
                        }
                    } finally {
                        try {
                            out.close();
                        } catch (Exception e) {
                            // ignored
                        }
                    }
                }
            };
            runner.start();
            runner.join(maxwait);
            if (runner.isAlive()) {
                runner.interrupt();
                if (failOnError) {
                    throw new BuildException("maxwait exceeded, unable to send data", getLocation());
                }
                return;
            }

            // read the response, if any, optionally writing it to a file
            if (wantResponse) {
                if (verbose) {
                    log("Waiting for response...");
                }
                runner = new Thread() {
                    public void run() {
                        PrintWriter fw = null;
                        StringWriter sw = null;
                        PrintWriter pw = null;
                        BufferedReader in = null;
                        try {
                            if (connection instanceof HttpURLConnection) {
                                // read and store cookies
                                for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                                    if (entry.getKey() != null && entry.getKey().equals("Set-Cookie")) {
                                        for (String raw : entry.getValue()) {
                                            Cookie cookie = new Cookie(raw);
                                            cookieStorage.put(cookie.getId(), cookie);
                                        }
                                    }
                                }

                                // maybe log response headers
                                if (verbose) {
                                    log(String.valueOf(((HttpURLConnection) connection).getResponseCode()));
                                    log(((HttpURLConnection) connection).getResponseMessage());
                                    StringBuilder sb = new StringBuilder();
                                    for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                                        sb.append(entry.getKey()).append("=");
                                        List<String> values = entry.getValue();
                                        if (values != null) {
                                            if (values.size() == 1) {
                                                sb.append(values.get(0));
                                            } else if (values.size() > 1) {
                                                sb.append("[");
                                                for (String value : values) {
                                                    sb.append(value).append(",");
                                                }
                                                sb.append("]");
                                            }
                                        }
                                        sb.append("\n");
                                        log(sb.toString());
                                    }
                                }
                            }
                            in = new BufferedReader(
                                    new InputStreamReader(connection.getInputStream()));
                            if (log != null) {
                                // user wants output stored to a file
                                fw = new PrintWriter(new FileWriter(log, append));
                            }
                            if (property != null) {
                                // user wants output stored in a property
                                sw = new StringWriter();
                                pw = new PrintWriter(sw);
                            }
                            String line;
                            while (null != ((line = in.readLine()))) {
                                if (currentRunner != this) {
                                    break;
                                }
                                if (verbose) {
                                    log(line);
                                }
                                if (fw != null) {
                                    // write response to a file
                                    fw.println(line);
                                }
                                if (pw != null) {
                                    // write response to a property
                                    pw.println(line);
                                }
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                            if (failOnError) {
                                throw new BuildException(e, getLocation());
                            }
                        } finally {
                            try {
                                in.close();
                            } catch (Exception e) {
                                // ignored
                            }
                            try {
                                if (fw != null) {
                                    fw.flush();
                                    fw.close();
                                }
                            } catch (Exception e) {
                                // ignored
                            }
                        }
                        if (property != null && sw != null) {
                            // save property
                            getProject().setProperty(property, sw.toString());
                        }
                    }
                };
                currentRunner = runner;
                runner.start();
                runner.join(maxwait);
                if (runner.isAlive()) {
                    currentRunner = null;
                    runner.interrupt();
                    if (failOnError) {
                        throw new BuildException("maxwait exceeded, unable to receive data",
                                getLocation());
                    }
                }
            }
            if (verbose) {
                log("Post complete.");
            }
        } catch (Exception e) {
            if (failOnError) {
                throw new BuildException(e);
            }
        }
    }

    /**
     * Borrowed from Property -- load variables from a file.
     *
     * @param file file to load
     * @throws BuildException Description of the Exception
     */
    private void loadFile(File file) throws BuildException {
        Properties fileprops = new Properties();
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                try {
                    fileprops.load(fis);
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
                addProperties(fileprops);
            } else {
                log("Unable to find property file: " + file.getAbsolutePath(),
                        Project.MSG_VERBOSE);
            }
            log("Post complete.");
        } catch (Exception e) {
            if (failOnError) {
                throw new BuildException(e);
            }
        }
    }

    /**
     * Builds and formats the message to send to the server. Message is UTF-8
     * encoded unless encoding has been explicitly set.
     *
     * @return the message to send to the server.
     */
    private String getContent() {
        if (propsFile != null) {
            loadFile(propsFile);
        }

        if (textProps != null) {
            loadTextProps(textProps);
        }

        StringBuilder content = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : props.entrySet()) {
                if (content.length() != 0) {
                    content.append("&");
                }
                content.append(URLEncoder.encode(entry.getKey(), encoding));
                content.append("=");
                content.append(URLEncoder.encode(entry.getValue(), encoding));
            }
        } catch (IOException ex) {
            if (failOnError) {
                throw new BuildException(ex, getLocation());
            }
        }
        return content.toString();
    }

    /**
     * loadTextProps() method.
     *
     * @param tp String
     */
    private void loadTextProps(String tp) {
        Properties p = new Properties();
        Project project = getProject();
        StringTokenizer st = new StringTokenizer(tp, "$");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int start = token.indexOf("{");
            int end = token.indexOf("}");
            if (start > -1 && end > -1 && end > start) {
                String name = token.substring(start + 1, end - start);
                String value = project.getProperty(name);
                if (value != null) {
                    p.setProperty(name, value);
                }
            }
        }
        addProperties(p);
    }

    /**
     * Borrowed from Property -- iterate through a set of properties, resolve
     * them, then assign them.
     *
     * @param fileprops The feature to be added to the Properties attribute
     */
    private void addProperties(Properties fileprops) {
        resolveAllProperties(fileprops);
        Enumeration<Object> e = fileprops.keys();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            String value = fileprops.getProperty(name);
            props.put(name, value);
        }
    }

    /**
     * Borrowed from Property -- resolve properties inside a properties
     * hashtable.
     *
     * @param props Description of the Parameter
     * @throws BuildException Description of the Exception
     */
    private void resolveAllProperties(Properties props) throws BuildException {
        PropertyHelper propertyHelper
                = PropertyHelper.getPropertyHelper(getProject());
        Map<String, Object> properties = new HashMap<String, Object>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            properties.put(entry.getKey().toString(), entry.getValue());
        }
        new ResolvePropertyMap(
                getProject(),
                propertyHelper,
                propertyHelper.getExpanders())
                .resolveAllProperties(properties, null, false);
    }

    /**
     * Represents a cookie.  See RFC 2109 and 2965.
     */
    public static class Cookie {
        /**
         * Field name.
         */
        private String name;

        /**
         * Field value.
         */
        private String value;

        /**
         * Field domain.
         */
        private String domain;

        /**
         * Field path.
         */
        private String path = "/";

        /**
         * Field id.
         */
        private String id;

        /**
         * @param raw the raw string abstracted from the header of an http
         *            response for a single cookie.
         */
        public Cookie(String raw) {
            String[] args = raw.split("[;]");
            for (int i = 0; i < args.length; i++) {
                String part = args[i];
                int eq_index = part.indexOf("=");
                if (eq_index == -1) {
                    continue;
                }
                String first_part = part.substring(0, eq_index).trim();
                String second_part = part.substring(eq_index + 1);
                if (i == 0) {
                    name = first_part;
                    value = second_part;
                } else if (first_part.equalsIgnoreCase("Path")) {
                    path = second_part;
                } else if (first_part.equalsIgnoreCase("Domain")) {
                    domain = second_part;
                }
            }
            if (name == null) {
                throw new IllegalArgumentException("Raw cookie does not contain a cookie name.");
            }
            if (path == null) {
                path = "/";
            }
            setId(path, name);
        }

        /**
         * Constructor.
         *
         * @param name  name of the cookie
         * @param value the value of the cookie
         */
        public Cookie(String name, String value) {
            if (name == null) {
                throw new IllegalArgumentException("Cookie name may not be null.");
            }

            this.name = name;
            this.value = value;
            setId(name);
        }

        /**
         * getId() object.
         *
         * @return the id of the cookie, used internally by Post to store
         * the cookie in a hashtable.
         */
        public String getId() {
            if (id == null) {
                setId(path, name);
            }
            return id;
        }

        /**
         * Method setId.
         *
         * @param name String
         */
        private void setId(String name) {
            setId(path, name);
        }

        /**
         * Method setId.
         *
         * @param path String
         * @param name String
         */
        private void setId(String path, String name) {
            if (name == null) {
                name = "";
            }
            id = path + name;
        }

        /**
         * getName() method.
         *
         * @return the name of the cookie
         */
        public String getName() {
            return name;
        }

        /**
         * getValue() method.
         *
         * @return the value of the cookie
         */
        public String getValue() {
            return value;
        }

        /**
         * setDomain() method.
         *
         * @param domain the domain of the cookie
         */
        public void setDomain(String domain) {
            this.domain = domain;
        }

        /**
         * getDomain() method.
         *
         * @return the domain of the cookie
         */
        public String getDomain() {
            return domain;
        }

        /**
         * setPath method().
         *
         * @param path the path of the cookie
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * getPath() method.
         *
         * @return the path of the cookie
         */
        public String getPath() {
            return path;
        }

        /**
         * @return a Cookie formatted as a Cookie Version 1 string.
         * The returned string is suitable for including in an http request.
         */
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append("=").append(value).append(";");
            if (domain != null) {
                sb.append("Domain=").append(domain).append(";");
            }
            if (path != null) {
                sb.append("Path=").append(path).append(";");
            }
            sb.append("Version=\"1\";");
            return sb.toString();
        }
    }
}
