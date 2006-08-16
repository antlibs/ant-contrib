/*
 * Copyright (c) 2006 Ant-Contrib project.  All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.taskdefs.ImportTask;

/***
 * Task to import a build file from a url.  The build file can be a build.xml,
 * or a .zip/.jar, in which case we download and extract the entire archive, and
 * import the file "build.xml"
 * @author inger
 *
 */
public class URLImportTask
	extends Task {

	private URL url;


	public void setUrl(URL url) {
		this.url = url;
	}


	public void execute()
		throws BuildException {

		if (url == null) {
			throw new BuildException("Missing url.");
		}

		try {
			File tempDir = File.createTempFile("urlimport", "");
			log("Creating directory: " + tempDir.getAbsolutePath(), Project.MSG_INFO);
			tempDir.delete();
			tempDir.mkdirs();

			log("Downloading file: " + url.toExternalForm(), Project.MSG_INFO);

			File destFile = new File(tempDir, new File(url.getPath()).getName());
			Get get = (Get) getProject().createTask("get");
			get.setSrc(url);
			get.setDest(destFile);
			get.perform();

			log("File: " + url.toExternalForm() + " downloaded to " + destFile.getAbsolutePath(),
					Project.MSG_INFO);

			File extractedBuildFile = null;
			if (destFile.getName().endsWith(".jar") ||
					destFile.getName().endsWith(".zip")) {
				log("Extracting compressed file: " + destFile.getAbsolutePath(), Project.MSG_INFO);
				Expand expand = (Expand) getProject().createTask("unjar");
				expand.setSrc(destFile);
				expand.setDest(tempDir);
				expand.perform();
				log("Compressed file extracted", Project.MSG_INFO);
				extractedBuildFile = new File(tempDir, "build.xml");
				if (! extractedBuildFile.exists()) {
					log("No 'build.xml' exists in the extracted file.", Project.MSG_ERR);
					throw new BuildException("Downloaded file does not contain a 'build.xml' file");
				}
			}
			else {
				extractedBuildFile = destFile;
			}

			log("Importing file: " + extractedBuildFile.getAbsolutePath(), Project.MSG_INFO);
			ImportTask importTask = new ImportTask();
			importTask.setProject(getProject());
			importTask.setOwningTarget(getOwningTarget());
			importTask.setLocation(getLocation());
			importTask.setFile(extractedBuildFile.getAbsolutePath());
			importTask.perform();
			log("Import complete.", Project.MSG_INFO);
		}
		catch (IOException e) {
			throw new BuildException(e);
		}
	}
}
