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
package net.sf.antcontrib.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.util.FileUtils;

/**
 * Identical (copy and paste, even) to the 'Ant' task, with the exception that
 * properties from the new project can be copied back into the original project.
 * Further modified to emulate "antcall". Build a sub-project. <pre>
 *  &lt;target name=&quot;foo&quot; depends=&quot;init&quot;&gt;
 *    &lt;ant antfile=&quot;build.xml&quot; target=&quot;bar&quot; &gt;
 *      &lt;property name=&quot;property1&quot; value=&quot;aaaaa&quot; /&gt;
 *      &lt;property name=&quot;foo&quot; value=&quot;baz&quot; /&gt;
 *    &lt;/ant&gt;</SPAN> &lt;/target&gt;</SPAN> &lt;target name=&quot;bar&quot;
 * depends=&quot;init&quot;&gt; &lt;echo message=&quot;prop is ${property1}
 * ${foo}&quot; /&gt; &lt;/target&gt; </pre>
 * <p>Developed for use with Antelope, migrated to ant-contrib Oct 2003.
 * <p>Credit to Costin for the original &lt;ant&gt; task, on which this is based.
 *
 * @author     costin@dnt.ro
 * @author     Dale Anson, danson@germane-software.com
 * @since      Ant 1.1
 * @ant.task   category="control"
 */
public class AntCallBack extends Task {

   /** the basedir where is executed the build file */
   private File dir = null;

   /**
    * the build.xml file (can be absolute) in this case dir will be ignored
    */
   private String antFile = null;

   /** the target to call if any */
   private String target = null;

   /** the output */
   private String output = null;

   /** should we inherit properties from the parent ? */
   private boolean inheritAll = true;

   /** should we inherit references from the parent ? */
   private boolean inheritRefs = false;

   /** the properties to pass to the new project */
   private Vector properties = new Vector();

   /** the references to pass to the new project */
   private Vector references = new Vector();

   /** the temporary project created to run the build file */
   private Project newProject;

   /** The stream to which output is to be written. */
   private PrintStream out = null;

   /** the name of the property to fetch from the new project */
   private String returnName = null;


   /**
    * If true, pass all properties to the new Ant project. Defaults to true.
    *
    * @param value  The new inheritAll value
    */
   public void setInheritAll( boolean value ) {
      inheritAll = value;
   }


   /**
    * If true, pass all references to the new Ant project. Defaults to false.
    *
    * @param value  The new inheritRefs value
    */
   public void setInheritRefs( boolean value ) {
      inheritRefs = value;
   }


   /** Creates a Project instance for the project to call. */
   public void init() {
      newProject = new Project();
      newProject.setJavaVersionProperty();
      newProject.addTaskDefinition( "property",
            (Class)project.getTaskDefinitions()
            .get( "property" ) );
   }


   /**
    * Called in execute or createProperty if newProject is null. <p>
    *
    * This can happen if the same instance of this task is run twice as
    * newProject is set to null at the end of execute (to save memory and help
    * the GC).</p> <p>
    *
    * Sets all properties that have been defined as nested property elements.
    * </p>
    */
   private void reinit() {
      init();
      final int count = properties.size();
      for ( int i = 0; i < count; i++ ) {
         Property p = (Property)properties.elementAt( i );
         Property newP = (Property)newProject.createTask( "property" );
         newP.setName( p.getName() );
         if ( p.getValue() != null ) {
            newP.setValue( p.getValue() );
         }
         if ( p.getFile() != null ) {
            newP.setFile( p.getFile() );
         }
         if ( p.getResource() != null ) {
            newP.setResource( p.getResource() );
         }
         if ( p.getPrefix() != null ) {
            newP.setPrefix( p.getPrefix() );
         }
         if ( p.getRefid() != null ) {
            newP.setRefid( p.getRefid() );
         }
         if ( p.getEnvironment() != null ) {
            newP.setEnvironment( p.getEnvironment() );
         }
         if ( p.getClasspath() != null ) {
            newP.setClasspath( p.getClasspath() );
         }
         properties.setElementAt( newP, i );
      }
   }


   /**
    * Attaches the build listeners of the current project to the new project,
    * configures a possible logfile, transfers task and data-type definitions,
    * transfers properties (either all or just the ones specified as user
    * properties to the current project, depending on inheritall), transfers the
    * input handler.
    */
   private void initializeProject() {
      newProject.setInputHandler( getProject().getInputHandler() );

      Vector listeners = project.getBuildListeners();
      final int count = listeners.size();
      for ( int i = 0; i < count; i++ ) {
         newProject.addBuildListener( (BuildListener)listeners.elementAt( i ) );
      }

      if ( output != null ) {
         File outfile = null;
         if ( dir != null ) {
            outfile = FileUtils.newFileUtils().resolveFile( dir, output );
         }
         else {
            outfile = getProject().resolveFile( output );
         }
         try {
            out = new PrintStream( new FileOutputStream( outfile ) );
            DefaultLogger logger = new DefaultLogger();
            logger.setMessageOutputLevel( Project.MSG_INFO );
            logger.setOutputPrintStream( out );
            logger.setErrorPrintStream( out );
            newProject.addBuildListener( logger );
         }
         catch ( IOException ex ) {
            log( "Ant: Can't set output to " + output );
         }
      }

      Hashtable taskdefs = project.getTaskDefinitions();
      Enumeration et = taskdefs.keys();
      while ( et.hasMoreElements() ) {
         String taskName = (String)et.nextElement();
         if ( taskName.equals( "property" ) ) {
            // we have already added this taskdef in #init
            continue;
         }
         Class taskClass = (Class)taskdefs.get( taskName );
         newProject.addTaskDefinition( taskName, taskClass );
      }

      Hashtable typedefs = project.getDataTypeDefinitions();
      Enumeration e = typedefs.keys();
      while ( e.hasMoreElements() ) {
         String typeName = (String)e.nextElement();
         Class typeClass = (Class)typedefs.get( typeName );
         newProject.addDataTypeDefinition( typeName, typeClass );
      }

      // set user-defined properties
      getProject().copyUserProperties( newProject );

      if ( !inheritAll ) {
         // set Java built-in properties separately,
         // b/c we won't inherit them.
         newProject.setSystemProperties();

      }
      else {
         // set all properties from calling project

         Hashtable props = getProject().getProperties();
         e = props.keys();
         while ( e.hasMoreElements() ) {
            String arg = e.nextElement().toString();
            if ( "basedir".equals( arg ) || "ant.file".equals( arg ) ) {
               // basedir and ant.file get special treatment in execute()
               continue;
            }

            String value = props.get( arg ).toString();
            // don't re-set user properties, avoid the warning message
            if ( newProject.getProperty( arg ) == null ) {
               // no user property
               newProject.setNewProperty( arg, value );
            }
         }
      }
   }


   /**
    * Pass output sent to System.out to the new project.
    *
    * @param line  Description of the Parameter
    * @since       Ant 1.5
    */
   protected void handleOutput( String line ) {
      if ( newProject != null ) {
         newProject.demuxOutput( line, false );
      }
      else {
         super.handleOutput( line );
      }
   }


   /**
    * Pass output sent to System.err to the new project.
    *
    * @param line  Description of the Parameter
    * @since       Ant 1.5
    */
   protected void handleErrorOutput( String line ) {
      if ( newProject != null ) {
         newProject.demuxOutput( line, true );
      }
      else {
         super.handleErrorOutput( line );
      }
   }


   /**
    * Do the execution.
    *
    * @exception BuildException  Description of the Exception
    */
   public void execute() throws BuildException {
      setAntfile( getProject().getProperty( "ant.file" ) );

      File savedDir = dir;
      String savedAntFile = antFile;
      String savedTarget = target;
      try {
         if ( newProject == null ) {
            reinit();
         }

         if ( ( dir == null ) && ( inheritAll ) ) {
            dir = project.getBaseDir();
         }

         initializeProject();

         if ( dir != null ) {
            newProject.setBaseDir( dir );
            if ( savedDir != null ) {   // has been set explicitly
               newProject.setInheritedProperty( "basedir",
                     dir.getAbsolutePath() );
            }
         }
         else {
            dir = project.getBaseDir();
         }

         overrideProperties();

         if ( antFile == null ) {
            throw new BuildException( "Attribute target is required.",
                  location );
            //antFile = "build.xml";
         }

         File file = FileUtils.newFileUtils().resolveFile( dir, antFile );
         antFile = file.getAbsolutePath();

         log( "calling target " + ( target != null ? target : "[default]" )
                + " in build file " + antFile.toString(),
               Project.MSG_VERBOSE );
         newProject.setUserProperty( "ant.file", antFile );
         ProjectHelper.configureProject( newProject, new File( antFile ) );

         if ( target == null ) {
            target = newProject.getDefaultTarget();
         }

         addReferences();

         // Are we trying to call the target in which we are defined?
         if ( newProject.getBaseDir().equals( project.getBaseDir() ) &&
               newProject.getProperty( "ant.file" ).equals( project.getProperty( "ant.file" ) ) &&
               getOwningTarget() != null &&
               target.equals( this.getOwningTarget().getName() ) ) {

            throw new BuildException( "antcallback task calling its own parent "
                   + "target" );
         }

         newProject.executeTarget( target );
         
         // copy back the props if possible
         if ( returnName != null ) {
            StringTokenizer st = new StringTokenizer( returnName, "," );
            while ( st.hasMoreTokens() ) {
               String name = st.nextToken().trim();
               String value = newProject.getUserProperty( name );
               if ( value != null ) {
                  project.setUserProperty( name, value );
               }
               else {
                  value = newProject.getProperty( name );
                  if ( value != null ) {
                     project.setProperty( name, value );
                  }
               }
            }
         }
      }
      finally {
         // help the gc
         newProject = null;
         if ( output != null && out != null ) {
            try {
               out.close();
            }
            catch ( final Exception e ) {
               //ignore
            }
         }
         dir = savedDir;
         antFile = savedAntFile;
         target = savedTarget;
      }
   }


   /**
    * Override the properties in the new project with the one explicitly defined
    * as nested elements here.
    *
    * @exception BuildException  Description of the Exception
    */
   private void overrideProperties() throws BuildException {
      Enumeration e = properties.elements();
      while ( e.hasMoreElements() ) {
         Property p = (Property)e.nextElement();
         p.setProject( newProject );
         p.execute();
      }
      getProject().copyInheritedProperties( newProject );
   }


   /**
    * Add the references explicitly defined as nested elements to the new
    * project. Also copy over all references that don't override existing
    * references in the new project if inheritrefs has been requested.
    *
    * @exception BuildException  Description of the Exception
    */
   private void addReferences() throws BuildException {
      Hashtable thisReferences = (Hashtable)project.getReferences().clone();
      Hashtable newReferences = newProject.getReferences();
      Enumeration e;
      if ( references.size() > 0 ) {
         for ( e = references.elements(); e.hasMoreElements();  ) {
            Reference ref = (Reference)e.nextElement();
            String refid = ref.getRefId();
            if ( refid == null ) {
               throw new BuildException( "the refid attribute is required"
                      + " for reference elements" );
            }
            if ( !thisReferences.containsKey( refid ) ) {
               log( "Parent project doesn't contain any reference '"
                      + refid + "'",
                     Project.MSG_WARN );
               continue;
            }

            thisReferences.remove( refid );
            String toRefid = ref.getToRefid();
            if ( toRefid == null ) {
               toRefid = refid;
            }
            copyReference( refid, toRefid );
         }
      }

      // Now add all references that are not defined in the
      // subproject, if inheritRefs is true
      if ( inheritRefs ) {
         for ( e = thisReferences.keys(); e.hasMoreElements();  ) {
            String key = (String)e.nextElement();
            if ( newReferences.containsKey( key ) ) {
               continue;
            }
            copyReference( key, key );
         }
      }
   }


   /**
    * Try to clone and reconfigure the object referenced by oldkey in the parent
    * project and add it to the new project with the key newkey. <p>
    *
    * If we cannot clone it, copy the referenced object itself and keep our
    * fingers crossed.</p>
    *
    * @param oldKey  Description of the Parameter
    * @param newKey  Description of the Parameter
    */
   private void copyReference( String oldKey, String newKey ) {
      Object orig = project.getReference( oldKey );
      Class c = orig.getClass();
      Object copy = orig;
      try {
         Method cloneM = c.getMethod( "clone", new Class[0] );
         if ( cloneM != null ) {
            copy = cloneM.invoke( orig, new Object[0] );
         }
      }
      catch ( Exception e ) {
         // not Clonable
      }

      if ( copy instanceof ProjectComponent ) {
         ( (ProjectComponent)copy ).setProject( newProject );
      }
      else {
         try {
            Method setProjectM =
                  c.getMethod( "setProject", new Class[]{Project.class} );
            if ( setProjectM != null ) {
               setProjectM.invoke( copy, new Object[]{newProject} );
            }
         }
         catch ( NoSuchMethodException e ) {
            // ignore this if the class being referenced does not have
            // a set project method.
         }
         catch ( Exception e2 ) {
            String msg = "Error setting new project instance for "
                   + "reference with id " + oldKey;
            throw new BuildException( msg, e2, location );
         }
      }
      newProject.addReference( newKey, copy );
   }


   /**
    * The directory to use as a base directory for the new Ant project. Defaults
    * to the current project's basedir, unless inheritall has been set to false,
    * in which case it doesn't have a default value. This will override the
    * basedir setting of the called project.
    *
    * @param d  The new dir value
    */
   public void setDir( File d ) {
      this.dir = d;
   }


   /**
    * The build file to use. Defaults to "build.xml". This file is expected to
    * be a filename relative to the dir attribute given.
    *
    * @param s  The new antfile value
    */
   public void setAntfile( String s ) {
      // @note: it is a string and not a file to handle relative/absolute
      // otherwise a relative file will be resolved based on the current
      // basedir.
      this.antFile = s;
   }


   /**
    * The target of the new Ant project to execute. Defaults to the new
    * project's default target.
    *
    * @param s  The new target value
    */
   public void setTarget( String s ) {
      this.target = s;
   }


   /**
    * Filename to write the output to. This is relative to the value of the dir
    * attribute if it has been set or to the base directory of the current
    * project otherwise.
    *
    * @param s  The new output value
    */
   public void setOutput( String s ) {
      this.output = s;
   }


   /**
    * Property to pass to the new project. The property is passed as a 'user
    * property'
    *
    * @return   Description of the Return Value
    */
   public Property createProperty() {
      if ( newProject == null ) {
         reinit();
      }
      /*
       *  Property p = new Property( true, getProject() );
       */
      Property p = new Property();
      p.setProject( newProject );
      p.setTaskName( "property" );
      properties.addElement( p );
      return p;
   }


    /**
     * Property to pass to the invoked target.
     */
    public Property createParam() {
        return createProperty();
    }

   /**
    * Set the property or properties that are set in the new project to be
    * transfered back to the original project. As with all properties, if the
    * property already exists in the original project, it will not be overridden
    * by a different value from the new project.
    *
    * @param r  the name of a property in the new project to set in the original
    *      project. This may be a comma separate list of properties.
    */
   public void setReturn( String r ) {
      returnName = r;
   }


   /**
    * Reference element identifying a data type to carry over to the new
    * project.
    *
    * @param r  The feature to be added to the Reference attribute
    */
   public void addReference( Reference r ) {
      references.addElement( r );
   }


   /**
    * Helper class that implements the nested &lt;reference&gt; element of
    * &lt;ant&gt; and &lt;antcall&gt;.
    *
    * @author   danson
    */
   public static class Reference
          extends org.apache.tools.ant.types.Reference {

      /** Creates a reference to be configured by Ant */
      public Reference() {
         super();
      }


      private String targetid = null;


      /**
       * Set the id that this reference to be stored under in the new project.
       *
       * @param targetid  the id under which this reference will be passed to
       *      the new project
       */
      public void setToRefid( String targetid ) {
         this.targetid = targetid;
      }


      /**
       * Get the id under which this reference will be stored in the new project
       *
       * @return   the id of the reference in the new project.
       */
      public String getToRefid() {
         return targetid;
      }
   }
}

