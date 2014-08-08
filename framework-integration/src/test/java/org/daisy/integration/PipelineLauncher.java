package org.daisy.integration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.daisy.pipeline.webservice.jabx.base.Alive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;


public class PipelineLauncher {
        public static final String NIX_LAUNCHER = "pipeline2";
        public static final String WIN_LAUNCHER = "pipeline2.bat";
        private HashMap<String, String> env = new HashMap<String, String>();
        private File path;
        private PipelineClient client;
        private static final Logger logger = LoggerFactory.getLogger(PipelineLauncher.class);

        /**
         * @param path
         */
        private PipelineLauncher(File path, PipelineClient client) {
                this.path = path;
                this.client = client;
        }

        /**
         * Sets an evironment property
         */
        public PipelineLauncher setEnv(String name, String value) {
                this.env.put(name, value);
                return this;
        }

        //Sets a property to be included or overwritten in the system.properties file
        public PipelineLauncher setSystemProperty(String name, String value) {
                return this;
        }

        //creates a new launcher using the pipeline2 script residing on the path and a client
        public static PipelineLauncher newLauncher(File path, PipelineClient client) {
                return new PipelineLauncher(path, client);
        }

        //launches the pipeline and waits it to be up
        public boolean launch() throws IOException {

                ProcessBuilder pb = new ProcessBuilder(
                                new File(this.path, NIX_LAUNCHER).toString());
                pb.environment().putAll(this.env);
                //redirect to tmp files
                File err=File.createTempFile("pipelineErr",".txt");
                err.deleteOnExit();
                File out=File.createTempFile("pipelineOut",".txt");
                err.deleteOnExit();
                pb.redirectError(err);
                pb.redirectOutput(out);
                //start the process
                pb.start();
                logger.info("The process has been launched "
                                + new File(this.path, NIX_LAUNCHER).toString());
                //wait until process is up
                Callable<Boolean> c = new Callable<Boolean>() {
                        //avoid compiler optimisation that
                        //may cause the while run foreva
                        volatile boolean done = false;

                        
                        public Boolean call() {
                                int times=1;
                                while (!done) {
                                        logger.info(String.format("Waiting for the ws(%d)...",times++));
                                        Alive alive = null;
                                        try {
                                                alive = client.Alive();
                                        } catch (Exception e) {}

                                        if (alive != null) {
                                                logger.info("Seems to be up!");
                                                done = true;
                                        }
                                        try {
                                                Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                        }
                                }
                                return done;
                        }

                };
                FutureTask<Boolean> t = new FutureTask<Boolean>(c);
                t.run();
                boolean result = false;

                try {
                        result = t.get(30, TimeUnit.SECONDS);
                } catch (Exception e) {
                        logger.error("Timed out waiting for the pipeline "+e.getMessage());
                }
                
                return result;
        }
        //stops the running pipeline
        public void halt() throws IOException {
                File keyFile=new File(new File(System.getProperty("java.io.tmpdir")),"dp2key.txt");
                String key=Files.readFirstLine(keyFile,Charset.defaultCharset());
                this.client.Halt(key);
        }
        //cleans all the db and data from the pipeline residing in the given path
        public void clean(){
        }
}
