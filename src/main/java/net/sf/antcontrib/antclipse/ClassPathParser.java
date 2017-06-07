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
package net.sf.antcontrib.antclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Classic tool firing a SAX parser. Must feed the source file and a handler.
 * Nothing really special about it, only probably some special file handling
 * in nasty cases (Windows files containing strange chars, internationalized
 * filenames, but you shouldn't be doing this, anyway :)).
 *
 * @author <a href="mailto:aspinei@myrealbox.com">Adrian Spinei</a>
 * @version $Revision: 1.2 $
 * @since Ant 1.5
 */
public class ClassPathParser {
    /**
     * Method parse.
     *
     * @param file    File
     * @param handler DefaultHandler
     * @throws BuildException when parser fails
     */
    void parse(File file, DefaultHandler handler) throws BuildException {
        String fName = file.getName();
        FileInputStream fileInputStream = null;
        InputSource inputSource = null;
        try {
            //go to UFS if we're on win
            String uri = "file:" + fName.replace('\\', '/');
            fileInputStream = new FileInputStream(file);
            inputSource = new InputSource(fileInputStream);
            inputSource.setSystemId(uri);
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(inputSource);
        } catch (SAXParseException exc) {
            Location location = new Location(fName, exc.getLineNumber(), exc.getColumnNumber());
            Throwable throwable = exc.getException();
            if (throwable instanceof BuildException) {
                BuildException be = (BuildException) throwable;
                if (be.getLocation() == Location.UNKNOWN_LOCATION) {
                    be.setLocation(location);
                }
                throw be;
            }
            throw new BuildException(exc.getMessage(), throwable, location);
        } catch (SAXException exc) {
            Throwable throwable = exc.getException();
            if (throwable instanceof BuildException) {
                throw (BuildException) throwable;
            }
            throw new BuildException(exc.getMessage(), throwable);
        } catch (FileNotFoundException exc) {
            throw new BuildException(exc);
        } catch (IOException exc) {
            throw new BuildException("Error reading file", exc);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ioexception) {
                    //do nothing, should not appear
                }
            }
        }
    }
}
