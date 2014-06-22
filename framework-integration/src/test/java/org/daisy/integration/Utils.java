package org.daisy.integration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;

import com.google.common.io.Files;

public class Utils {

        public static void startPipeline(PipelineClient client) throws IOException {
        
                //Just for testing this will disappear soon
                File path=new File("/home/javi/daisy/pipeline-assembly/target/dev-launcher/bin/");
                boolean up=PipelineLauncher.newLauncher(path,client)
                        .setEnv("JAVA_OPTS","-Dgosh.args=--noi")
                        .launch();
                Assert.assertTrue("The pipeline is not up",up);
        }

        public static void stopPipeline(PipelineClient client) throws IOException {
                File keyFile=new File(new File(System.getProperty("java.io.tmpdir")),"dp2key.txt");
                String key=Files.readFirstLine(keyFile,Charset.defaultCharset());
                client.Halt(key);
        }
        
}
