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
package net.sf.antcontrib.antserver.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import net.sf.antcontrib.antserver.Command;
import net.sf.antcontrib.antserver.Util;

import static org.apache.tools.ant.util.FileUtils.isAbsolutePath;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
@SuppressWarnings("serial")
public class SendFileCommand extends AbstractCommand implements Command {
    /**
     * Field contentLength.
     */
    private long contentLength;

    /**
     * Field todir.
     */
    private String todir;

    /**
     * Field tofile.
     */
    private String tofile;

    /**
     * Field fileBaseName.
     */
    private String fileBaseName;

    /**
     * Field file.
     */
    private transient File file;

    /**
     * Method getFile.
     *
     * @return File
     */
    public File getFile() {
        return file;
    }

    /**
     * Method getContentLength.
     *
     * @return long
     * @see net.sf.antcontrib.antserver.Command#getContentLength()
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Method getContentStream.
     *
     * @return InputStream
     * @throws IOException if something goes wrong
     * @see net.sf.antcontrib.antserver.Command#getContentStream()
     */
    public InputStream getContentStream() throws IOException {
        return new FileInputStream(file);
    }

    /**
     * Method setFile.
     *
     * @param file File
     */
    public void setFile(File file) {
        this.file = file;
        this.fileBaseName = file.getName();
        this.contentLength = file.length();
    }

    /**
     * Method getTofile.
     *
     * @return String
     */
    public String getTofile() {
        return tofile;
    }

    /**
     * Method setTofile.
     *
     * @param tofile String
     */
    public void setTofile(String tofile) {
        this.tofile = tofile;
    }

    /**
     * Method getTodir.
     *
     * @return String
     */
    public String getTodir() {
        return todir;
    }

    /**
     * Method setTodir.
     *
     * @param todir String
     */
    public void setTodir(String todir) {
        this.todir = todir;
    }

    /**
     * Method validate.
     *
     * @param project Project
     * @see net.sf.antcontrib.antserver.Command#validate(Project)
     */
    public void validate(Project project) {
        if (file == null)
            throw new BuildException("Missing required attribute 'file'");

        if (tofile == null && todir == null)
            throw new BuildException("Missing both attributes 'tofile' and 'todir'"
                    + " at least one must be supplied");
    }

    /**
     * Method execute.
     *
     * @param project       Project
     * @param contentLength long
     * @param content       InputStream
     * @return boolean
     * @throws IOException if data transfer fails
     * @see net.sf.antcontrib.antserver.Command#execute(Project, long, InputStream)
     */
    public boolean execute(Project project,
                           long contentLength,
                           InputStream content)
            throws IOException {
        File dest = null;

        if (tofile != null) {
            if (isAbsolutePath(tofile)) {
                if (!new File(tofile).getCanonicalPath().startsWith(project.getBaseDir().getCanonicalPath())) {
                    throw new SecurityException("The requested filename must be in the project file tree.");
                }
                dest = new File(tofile);
            } else {
                dest = new File(project.getBaseDir(), tofile);
            }
        } else {
            if (isAbsolutePath(todir)) {
                if (!new File(todir).getCanonicalPath().startsWith(project.getBaseDir().getCanonicalPath())) {
                    throw new SecurityException("The requested directory must be in the project file tree.");
                }
                dest = new File(todir);
            } else {
                dest = new File(project.getBaseDir(), todir);
            }
            if (!dest.canWrite()) {
                throw new SecurityException("The requested directory is not writable.");
            }
            dest = new File(dest, fileBaseName);
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(dest);
            Util.transferBytes(content, contentLength, fos, false);
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                // gulp
            }
        }
        return false;
    }
}
