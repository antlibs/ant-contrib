/*
 * Copyright (c) 2001-2006 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.net.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

/**
 */
public class PostMethodTask extends AbstractMethodTask {
    /**
     * Field parts.
     */
    private final List<Object> parts = new ArrayList<Object>();

    /**
     * Field multipart.
     */
    private boolean multipart;

    /**
     * Field stream.
     */
    private transient FileInputStream stream;

    /**
     */
    public static class FilePartType {
        /**
         * Field path.
         */
        private File path;

        /**
         * Field contentType.
         */
        private String contentType = FilePart.DEFAULT_CONTENT_TYPE;

        /**
         * Field charSet.
         */
        private String charSet = FilePart.DEFAULT_CHARSET;

        /**
         * Method getPath.
         *
         * @return File
         */
        public File getPath() {
            return path;
        }

        /**
         * Method setPath.
         *
         * @param path File
         */
        public void setPath(File path) {
            this.path = path;
        }

        /**
         * Method getContentType.
         *
         * @return String
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * Method setContentType.
         *
         * @param contentType String
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * Method getCharSet.
         *
         * @return String
         */
        public String getCharSet() {
            return charSet;
        }

        /**
         * Method setCharSet.
         *
         * @param charSet String
         */
        public void setCharSet(String charSet) {
            this.charSet = charSet;
        }
    }

    /**
     */
    public static class TextPartType {
        /**
         * Field name.
         */
        private String name = "";

        /**
         * Field value.
         */
        private String value = "";

        /**
         * Field charSet.
         */
        private String charSet = StringPart.DEFAULT_CHARSET;

        /**
         * Field contentType.
         */
        private String contentType = StringPart.DEFAULT_CONTENT_TYPE;

        /**
         * Method getValue.
         *
         * @return String
         */
        public String getValue() {
            return value;
        }

        /**
         * Method setValue.
         *
         * @param value String
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Method getName.
         *
         * @return String
         */
        public String getName() {
            return name;
        }

        /**
         * Method setName.
         *
         * @param name String
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Method getCharSet.
         *
         * @return String
         */
        public String getCharSet() {
            return charSet;
        }

        /**
         * Method setCharSet.
         *
         * @param charSet String
         */
        public void setCharSet(String charSet) {
            this.charSet = charSet;
        }

        /**
         * Method getContentType.
         *
         * @return String
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * Method setContentType.
         *
         * @param contentType String
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * Method setText.
         *
         * @param text String
         */
        public void setText(String text) {
            this.value = text;
        }
    }

    /**
     * Method addConfiguredFile.
     *
     * @param file FilePartType
     */
    public void addConfiguredFile(FilePartType file) {
        this.parts.add(file);
    }

    /**
     * Method setMultipart.
     *
     * @param multipart boolean
     */
    public void setMultipart(boolean multipart) {
        this.multipart = multipart;
    }

    /**
     * Method addConfiguredText.
     *
     * @param text TextPartType
     */
    public void addConfiguredText(TextPartType text) {
        this.parts.add(text);
    }

    /**
     * Method setParameters.
     *
     * @param parameters File
     */
    public void setParameters(File parameters) {
        PostMethod post = getPostMethod();
        Properties p = new Properties();
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            post.addParameter(entry.getKey().toString(),
                    entry.getValue().toString());
        }
    }

    /**
     * Method createNewMethod.
     *
     * @return HttpMethodBase
     */
    protected HttpMethodBase createNewMethod() {
        return new PostMethod();
    }

    /**
     * Method getPostMethod.
     *
     * @return PostMethod
     */
    private PostMethod getPostMethod() {
        return ((PostMethod) createMethodIfNecessary());
    }

    /**
     * Method addConfiguredParameter.
     *
     * @param pair NameValuePair
     */
    public void addConfiguredParameter(NameValuePair pair) {
        getPostMethod().setParameter(pair.getName(), pair.getValue());
    }

    /**
     * Method setContentChunked.
     *
     * @param contentChunked boolean
     */
    public void setContentChunked(boolean contentChunked) {
        getPostMethod().setContentChunked(contentChunked);
    }

    /**
     * Method configureMethod.
     *
     * @param method HttpMethodBase
     */
    protected void configureMethod(HttpMethodBase method) {
        PostMethod post = (PostMethod) method;

        if (parts.size() == 1 && !multipart) {
            Object part = parts.get(0);
            if (part instanceof FilePartType) {
                FilePartType filePart = (FilePartType) part;
                try {
                    stream = new FileInputStream(
                            filePart.getPath().getAbsolutePath());
                    post.setRequestEntity(
                            new InputStreamRequestEntity(stream,
                                    filePart.getPath().length(),
                                    filePart.getContentType()));
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            } else if (part instanceof TextPartType) {
                TextPartType textPart = (TextPartType) part;
                try {
                    post.setRequestEntity(
                            new StringRequestEntity(textPart.getValue(),
                                    textPart.getContentType(),
                                    textPart.getCharSet()));
                } catch (UnsupportedEncodingException e) {
                    throw new BuildException(e);
                }
            }
        } else if (!parts.isEmpty()) {
            Part[] partArray = new Part[parts.size()];
            for (int i = 0; i < parts.size(); i++) {
                Object part = parts.get(i);
                if (part instanceof FilePartType) {
                    FilePartType filePart = (FilePartType) part;
                    try {
                        partArray[i] = new FilePart(filePart.getPath().getName(),
                                filePart.getPath().getName(),
                                filePart.getPath(),
                                filePart.getContentType(),
                                filePart.getCharSet());
                    } catch (FileNotFoundException e) {
                        throw new BuildException(e);
                    }
                } else if (part instanceof TextPartType) {
                    TextPartType textPart = (TextPartType) part;
                    partArray[i] = new StringPart(textPart.getName(),
                            textPart.getValue(),
                            textPart.getCharSet());
                    ((StringPart) partArray[i]).setContentType(textPart.getContentType());
                }
            }
            MultipartRequestEntity entity = new MultipartRequestEntity(
                    partArray,
                    post.getParams());
            post.setRequestEntity(entity);
        }
    }

    /**
     * Method cleanupResources.
     *
     * @param method HttpMethodBase
     */
    protected void cleanupResources(HttpMethodBase method) {
        FileUtils.close(stream);
    }
}
