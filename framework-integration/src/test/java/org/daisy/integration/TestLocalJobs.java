package org.daisy.integration;

import java.io.IOException;

import org.daisy.pipeline.webservice.jabx.base.Alive;
import org.daisy.pipeline.webservice.jabx.job.Job;
import org.daisy.pipeline.webservice.jabx.request.JobRequest;
import org.daisy.pipeline.webservice.jabx.script.Scripts;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

public class TestLocalJobs {

        private static PipelineClient getClient(){
                return new PipelineClient("http://localhost:8181/ws");
        }
        @BeforeClass
        public static void bringUp() throws IOException {
                System.setProperty("enableLogging", "true");
                Utils.startPipeline(getClient());
        }

        @AfterClass
        public static void bringDown() throws IOException {
                Utils.stopPipeline(getClient());
                
        }

        @Test
        public void testAlive() throws Exception {
               Alive alive = getClient().Alive(); 
               Assert.assertTrue("The version is empty",alive.getVersion().length()>0);
               Assert.assertTrue("The ws doesn't accept local jobs",alive.getLocalfs().equalsIgnoreCase("true"));
               Assert.assertTrue("The ws needs credentials",alive.getAuthentication().equalsIgnoreCase("false"));
        }

        @Test
        public void testScripts() throws Exception {
                Scripts scripts = getClient().Scripts();
                Assert.assertTrue("There are no scripts in pipeline",scripts.getScript().size()>0);
        }

        @Test
        public void testSendJob() throws Exception {
                Optional<JobRequest> req = Utils.getJobRequest(getClient());
                Assert.assertTrue("Couldn't build the request",req.isPresent());
                Job job=getClient().SendJob(req.get());
                Assert.assertTrue("Job has been sent",job.getId()!=null &&job.getId().length()>0);
                
        }
 
       
}
