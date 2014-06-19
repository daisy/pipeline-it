package org.daisy.integration;

public class PipelineLauncher {
        public static final String NIX_LAUNCHER="pipeline2";
        public static final String WIN_LAUNCHER="pipeline2.bat";
        //Sets an evironment property
        public PipelineLauncher setEnv(String name,String value) {
                return this;
        }
        //Sets a property to be included or overwritten in the system.properties file
        public PipelineLauncher setSystemProperty(String name,String value) {
                return this;
        }
        //creates a new launcher using the pipeline2 script residing on the path 
        public static PipelineLauncher withPath(String path) {
                return new PipelineLauncher();
        }

        //launches the pipeline and waits it to be up
        public boolean launch(){
                return false;
        }
        //stops the running pipeline
        public boolean halt(){
                return false;
        }
        //cleans all the db and data from the pipeline residing in the given path
        public void clean(){
        }
}
