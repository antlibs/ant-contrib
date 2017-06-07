package net.sf.antcontrib.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.Path;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class ProjectDelegate extends Project {
    /**
     * Field delegate.
     */
    private final Project delegate;

    /**
     * Field subproject.
     */
    private Project subproject;

    /**
     * Constructor for ProjectDelegate.
     *
     * @param delegate Project
     */
    public ProjectDelegate(Project delegate) {
        super();
        this.delegate = delegate;
    }

    /**
     * Method getSubproject.
     *
     * @return Project
     */
    public Project getSubproject() {
        return subproject;
    }

    /**
     * Method addBuildListener.
     *
     * @param arg0 BuildListener
     */
    public void addBuildListener(BuildListener arg0) {
        delegate.addBuildListener(arg0);
    }

    /**
     * Method addDataTypeDefinition.
     *
     * @param arg0 String
     * @param arg1 Class&lt;?&gt;
     */
    public void addDataTypeDefinition(String arg0, Class<?> arg1) {
        delegate.addDataTypeDefinition(arg0, arg1);
    }

    /**
     * Method addOrReplaceTarget.
     *
     * @param arg0 String
     * @param arg1 Target
     */
    public void addOrReplaceTarget(String arg0, Target arg1) {
        delegate.addOrReplaceTarget(arg0, arg1);
    }

    /**
     * Method addOrReplaceTarget.
     *
     * @param arg0 Target
     */
    public void addOrReplaceTarget(Target arg0) {
        delegate.addOrReplaceTarget(arg0);
    }

    /**
     * Method addReference.
     *
     * @param arg0 String
     * @param arg1 Object
     */
    public void addReference(String arg0, Object arg1) {
        delegate.addReference(arg0, arg1);
    }

    /**
     * Method addTarget.
     *
     * @param arg0 String
     * @param arg1 Target
     * @throws BuildException if something goes wrong
     */
    public void addTarget(String arg0, Target arg1) throws BuildException {
        delegate.addTarget(arg0, arg1);
    }

    /**
     * Method addTarget.
     *
     * @param arg0 Target
     * @throws BuildException if something goes wrong
     */
    public void addTarget(Target arg0) throws BuildException {
        delegate.addTarget(arg0);
    }

    /**
     * Method addTaskDefinition.
     *
     * @param arg0 String
     * @param arg1 Class&lt;?&gt;
     * @throws BuildException if something goes wrong
     */
    public void addTaskDefinition(String arg0, Class<?> arg1) throws BuildException {
        delegate.addTaskDefinition(arg0, arg1);
    }

    /**
     * Method checkTaskClass.
     *
     * @param arg0 Class&lt;?&gt;
     * @throws BuildException if something goes wrong
     */
    public void checkTaskClass(Class<?> arg0) throws BuildException {
        delegate.checkTaskClass(arg0);
    }

    /**
     * Method copyInheritedProperties.
     *
     * @param arg0 Project
     */
    public void copyInheritedProperties(Project arg0) {
        delegate.copyInheritedProperties(arg0);
    }

    /**
     * Method copyUserProperties.
     *
     * @param arg0 Project
     */
    public void copyUserProperties(Project arg0) {
        delegate.copyUserProperties(arg0);
    }

    /**
     * Method createClassLoader.
     *
     * @param arg0 Path
     * @return AntClassLoader
     */
    public AntClassLoader createClassLoader(Path arg0) {
        return delegate.createClassLoader(arg0);
    }

    /**
     * Method createDataType.
     *
     * @param arg0 String
     * @return Object
     * @throws BuildException if something goes wrong
     */
    public Object createDataType(String arg0) throws BuildException {
        return delegate.createDataType(arg0);
    }

    /**
     * Method createTask.
     *
     * @param arg0 String
     * @return Task
     * @throws BuildException if something goes wrong
     */
    public Task createTask(String arg0) throws BuildException {
        return delegate.createTask(arg0);
    }

    /**
     * Method defaultInput.
     *
     * @param arg0 byte[]
     * @param arg1 int
     * @param arg2 int
     * @return int
     * @throws IOException if something goes wrong
     */
    public int defaultInput(byte[] arg0, int arg1, int arg2) throws IOException {
        return delegate.defaultInput(arg0, arg1, arg2);
    }

    /**
     * Method demuxFlush.
     *
     * @param arg0 String
     * @param arg1 boolean
     */
    public void demuxFlush(String arg0, boolean arg1) {
        delegate.demuxFlush(arg0, arg1);
    }

    /**
     * Method demuxInput.
     *
     * @param arg0 byte[]
     * @param arg1 int
     * @param arg2 int
     * @return int
     * @throws IOException if something goes wrong
     */
    public int demuxInput(byte[] arg0, int arg1, int arg2) throws IOException {
        return delegate.demuxInput(arg0, arg1, arg2);
    }

    /**
     * Method demuxOutput.
     *
     * @param arg0 String
     * @param arg1 boolean
     */
    public void demuxOutput(String arg0, boolean arg1) {
        delegate.demuxOutput(arg0, arg1);
    }

    /**
     * Method equals.
     *
     * @param arg0 Object
     * @return boolean
     */
    public boolean equals(Object arg0) {
        return delegate.equals(arg0);
    }

    /**
     * Method executeSortedTargets.
     *
     * @param arg0 Vector&lt;Target&gt;
     * @throws BuildException if something goes wrong
     */
    public void executeSortedTargets(Vector<Target> arg0) throws BuildException {
        delegate.executeSortedTargets(arg0);
    }

    /**
     * Method executeTarget.
     *
     * @param arg0 String
     * @throws BuildException if something goes wrong
     */
    public void executeTarget(String arg0) throws BuildException {
        delegate.executeTarget(arg0);
    }

    /**
     * Method executeTargets.
     *
     * @param arg0 Vector&lt;String&gt;
     * @throws BuildException if something goes wrong
     */
    public void executeTargets(Vector<String> arg0) throws BuildException {
        delegate.executeTargets(arg0);
    }

    /**
     * Method fireBuildFinished.
     *
     * @param arg0 Throwable
     */
    public void fireBuildFinished(Throwable arg0) {
        delegate.fireBuildFinished(arg0);
    }

    /**
     * Method fireBuildStarted.
     */
    public void fireBuildStarted() {
        delegate.fireBuildStarted();
    }

    /**
     * Method fireSubBuildFinished.
     *
     * @param arg0 Throwable
     */
    public void fireSubBuildFinished(Throwable arg0) {
        delegate.fireSubBuildFinished(arg0);
    }

    /**
     * Method fireSubBuildStarted.
     */
    public void fireSubBuildStarted() {
        delegate.fireSubBuildStarted();
    }

    /**
     * Method getBaseDir.
     *
     * @return File
     */
    public File getBaseDir() {
        return delegate.getBaseDir();
    }

    /**
     * Method getBuildListeners.
     *
     * @return Vector&lt;BuildListener&gt;
     */
    public Vector<BuildListener> getBuildListeners() {
        return delegate.getBuildListeners();
    }

    /**
     * Method getCoreLoader.
     *
     * @return ClassLoader
     */
    public ClassLoader getCoreLoader() {
        return delegate.getCoreLoader();
    }

    /**
     * Method getDataTypeDefinitions.
     *
     * @return Hashtable&lt;String,Class&lt;?&gt;&gt;
     */
    public Hashtable<String, Class<?>> getDataTypeDefinitions() {
        return delegate.getDataTypeDefinitions();
    }

    /**
     * Method getDefaultInputStream.
     *
     * @return InputStream
     */
    public InputStream getDefaultInputStream() {
        return delegate.getDefaultInputStream();
    }

    /**
     * Method getDefaultTarget.
     *
     * @return String
     */
    public String getDefaultTarget() {
        return delegate.getDefaultTarget();
    }

    /**
     * Method getDescription.
     *
     * @return String
     */
    public String getDescription() {
        return delegate.getDescription();
    }

    /**
     * Method getElementName.
     *
     * @param arg0 Object
     * @return String
     */
    public String getElementName(Object arg0) {
        return delegate.getElementName(arg0);
    }

    /**
     * Method getExecutor.
     *
     * @return Executor
     */
    public Executor getExecutor() {
        return delegate.getExecutor();
    }

    /**
     * Method getGlobalFilterSet.
     *
     * @return FilterSet
     */
    public FilterSet getGlobalFilterSet() {
        return delegate.getGlobalFilterSet();
    }

    /**
     * Method getInputHandler.
     *
     * @return InputHandler
     */
    public InputHandler getInputHandler() {
        return delegate.getInputHandler();
    }

    /**
     * Method getName.
     *
     * @return String
     */
    public String getName() {
        return delegate.getName();
    }

    /**
     * Method getProperties.
     *
     * @return Hashtable&lt;String,Object&gt;
     */
    public Hashtable<String, Object> getProperties() {
        return delegate.getProperties();
    }

    /**
     * Method getProperty.
     *
     * @param arg0 String
     * @return String
     */
    public String getProperty(String arg0) {
        return delegate.getProperty(arg0);
    }

    /**
     * Method getReference.
     *
     * @param arg0 String
     * @return T
     */
    public <T> T getReference(String arg0) {
        return delegate.getReference(arg0);
    }

    /**
     * Method getReferences.
     *
     * @return Hashtable&lt;String,Object&gt;
     */
    public Hashtable<String, Object> getReferences() {
        return delegate.getReferences();
    }

    /**
     * Method getTargets.
     *
     * @return Hashtable&lt;String,Target&gt;
     */
    public Hashtable<String, Target> getTargets() {
        return delegate.getTargets();
    }

    /**
     * Method getTaskDefinitions.
     *
     * @return Hashtable&lt;String,Class&lt;?&gt;&gt;
     */
    public Hashtable<String, Class<?>> getTaskDefinitions() {
        return delegate.getTaskDefinitions();
    }

    /**
     * Method getThreadTask.
     *
     * @param arg0 Thread
     * @return Task
     */
    public Task getThreadTask(Thread arg0) {
        return delegate.getThreadTask(arg0);
    }

    /**
     * Method getUserProperties.
     *
     * @return Hashtable&lt;String,Object&gt;
     */
    public Hashtable<String, Object> getUserProperties() {
        return delegate.getUserProperties();
    }

    /**
     * Method getUserProperty.
     *
     * @param arg0 String
     * @return String
     */
    public String getUserProperty(String arg0) {
        return delegate.getUserProperty(arg0);
    }

    /**
     * Method hashCode.
     *
     * @return int
     */
    public int hashCode() {
        return delegate.hashCode();
    }

    /**
     * Method init.
     *
     * @throws BuildException if something goes wrong
     */
    public void init() throws BuildException {
        delegate.init();
    }

    /**
     * Method initSubProject.
     *
     * @param arg0 Project
     */
    public void initSubProject(Project arg0) {
        delegate.initSubProject(arg0);
        this.subproject = arg0;
    }

    /**
     * Method isKeepGoingMode.
     *
     * @return boolean
     */
    public boolean isKeepGoingMode() {
        return delegate.isKeepGoingMode();
    }

    /**
     * Method log.
     *
     * @param arg0 String
     * @param arg1 int
     */
    public void log(String arg0, int arg1) {
        delegate.log(arg0, arg1);
    }

    /**
     * Method log.
     *
     * @param arg0 String
     */
    public void log(String arg0) {
        delegate.log(arg0);
    }

    /**
     * Method log.
     *
     * @param arg0 Target
     * @param arg1 String
     * @param arg2 int
     */
    public void log(Target arg0, String arg1, int arg2) {
        delegate.log(arg0, arg1, arg2);
    }

    /**
     * Method log.
     *
     * @param arg0 Task
     * @param arg1 String
     * @param arg2 int
     */
    public void log(Task arg0, String arg1, int arg2) {
        delegate.log(arg0, arg1, arg2);
    }

    /**
     * Method registerThreadTask.
     *
     * @param arg0 Thread
     * @param arg1 Task
     */
    public void registerThreadTask(Thread arg0, Task arg1) {
        delegate.registerThreadTask(arg0, arg1);
    }

    /**
     * Method removeBuildListener.
     *
     * @param arg0 BuildListener
     */
    public void removeBuildListener(BuildListener arg0) {
        delegate.removeBuildListener(arg0);
    }

    /**
     * Method replaceProperties.
     *
     * @param arg0 String
     * @return String
     * @throws BuildException if something goes wrong
     */
    public String replaceProperties(String arg0) throws BuildException {
        return delegate.replaceProperties(arg0);
    }

    /**
     * Method resolveFile.
     *
     * @param arg0 String
     * @return File
     */
    public File resolveFile(String arg0) {
        return delegate.resolveFile(arg0);
    }

    /**
     * Method setBaseDir.
     *
     * @param arg0 File
     * @throws BuildException if something goes wrong
     */
    public void setBaseDir(File arg0) throws BuildException {
        delegate.setBaseDir(arg0);
    }

    /**
     * Method setBasedir.
     *
     * @param arg0 String
     * @throws BuildException if something goes wrong
     */
    public void setBasedir(String arg0) throws BuildException {
        delegate.setBasedir(arg0);
    }

    /**
     * Method setCoreLoader.
     *
     * @param arg0 ClassLoader
     */
    public void setCoreLoader(ClassLoader arg0) {
        delegate.setCoreLoader(arg0);
    }

    /**
     * Method setDefault.
     *
     * @param arg0 String
     */
    public void setDefault(String arg0) {
        delegate.setDefault(arg0);
    }

    /**
     * Method setDefaultInputStream.
     *
     * @param arg0 InputStream
     */
    public void setDefaultInputStream(InputStream arg0) {
        delegate.setDefaultInputStream(arg0);
    }

    /**
     * Method setDescription.
     *
     * @param arg0 String
     */
    public void setDescription(String arg0) {
        delegate.setDescription(arg0);
    }

    /**
     * Method setExecutor.
     *
     * @param arg0 Executor
     */
    public void setExecutor(Executor arg0) {
        delegate.setExecutor(arg0);
    }

    /**
     * Method setInheritedProperty.
     *
     * @param arg0 String
     * @param arg1 String
     */
    public void setInheritedProperty(String arg0, String arg1) {
        delegate.setInheritedProperty(arg0, arg1);
    }

    /**
     * Method setInputHandler.
     *
     * @param arg0 InputHandler
     */
    public void setInputHandler(InputHandler arg0) {
        delegate.setInputHandler(arg0);
    }

    /**
     * Method setJavaVersionProperty.
     *
     * @throws BuildException if something goes wrong
     */
    public void setJavaVersionProperty() throws BuildException {
        delegate.setJavaVersionProperty();
    }

    /**
     * Method setKeepGoingMode.
     *
     * @param arg0 boolean
     */
    public void setKeepGoingMode(boolean arg0) {
        delegate.setKeepGoingMode(arg0);
    }

    /**
     * Method setName.
     *
     * @param arg0 String
     */
    public void setName(String arg0) {
        delegate.setName(arg0);
    }

    /**
     * Method setNewProperty.
     *
     * @param arg0 String
     * @param arg1 String
     */
    public void setNewProperty(String arg0, String arg1) {
        delegate.setNewProperty(arg0, arg1);
    }

    /**
     * Method setProperty.
     *
     * @param arg0 String
     * @param arg1 String
     */
    public void setProperty(String arg0, String arg1) {
        delegate.setProperty(arg0, arg1);
    }

    /**
     * Method setSystemProperties.
     */
    public void setSystemProperties() {
        delegate.setSystemProperties();
    }

    /**
     * Method setUserProperty.
     *
     * @param arg0 String
     * @param arg1 String
     */
    public void setUserProperty(String arg0, String arg1) {
        delegate.setUserProperty(arg0, arg1);
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return delegate.toString();
    }
}
