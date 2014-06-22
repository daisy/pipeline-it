package org.daisy.integration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.daisy.pipeline.webservice.jabx.request.Input;
import org.daisy.pipeline.webservice.jabx.request.Item;
import org.daisy.pipeline.webservice.jabx.request.JobRequest;
import org.daisy.pipeline.webservice.jabx.request.ObjectFactory;
import org.daisy.pipeline.webservice.jabx.request.Script;
import org.daisy.pipeline.webservice.jabx.script.Scripts;
import org.junit.Assert;

import com.google.common.base.Optional;
import com.google.common.io.Files;

public class Utils {
        public static String SCRIPT="dtbook-to-epub3";
        public static String SOURCE="hauy_valid.xml";
        public static String SAMPLES_PATH="/home/javi/daisy/pipeline-assembly/src/main/resources/samples/";

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

        public static Optional<JobRequest> getJobRequest(PipelineClient client){
                ObjectFactory reqFactory= new ObjectFactory();
                JobRequest req=reqFactory.createJobRequest();
                Script script=reqFactory.createScript();
                File dtbook=new File(new File(SAMPLES_PATH),"dtbook");
                File hauy=new File(dtbook,SOURCE);

                Optional<String> href=Utils.getScriptHref(SCRIPT,client);
                if (!href.isPresent()){
                        return Optional.absent();
                }
                script.setHref(href.get());
                //set the source
                Item source = reqFactory.createItem();
                source.setValue(hauy.toURI().toString());
                Input input = reqFactory.createInput();
                input.getItem().add(source);
                input.setName("source");
                req.getScriptOrNicenameOrInput().add(script);
                req.getScriptOrNicenameOrInput().add(input);
                return Optional.of(req);
        }

        public static Optional<String> getScriptHref(String name,PipelineClient client){
                Scripts scripts=client.Scripts();
                for(org.daisy.pipeline.webservice.jabx.script.Script s : scripts.getScript()){
                        if (s.getId().equals(name)){
                                return Optional.of(s.getHref());
                        }
                }
                return Optional.absent();
        }
        
}
