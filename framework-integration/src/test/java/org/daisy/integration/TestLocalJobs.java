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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class TestLocalJobs {
        private static final Logger logger = LoggerFactory.getLogger(TestLocalJobs.class);

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
                logger.info(String.format("%s testAlive IN",TestLocalJobs.class));
        
               Alive alive = getClient().Alive(); 
               Assert.assertTrue("The version is empty",alive.getVersion().length()>0);
               Assert.assertTrue("The ws doesn't accept local jobs",alive.getLocalfs().equalsIgnoreCase("true"));
               Assert.assertTrue("The ws needs credentials",alive.getAuthentication().equalsIgnoreCase("false"));
                logger.info(String.format("%s testAlive OUT",TestLocalJobs.class));
        }

        @Test
        public void testScripts() throws Exception {
                logger.info(String.format("%s testScripts IN",TestLocalJobs.class));
                Scripts scripts = getClient().Scripts();
                Assert.assertTrue("There are no scripts in pipeline",scripts.getScript().size()>0);
                logger.info(String.format("%s testScripts OUT",TestLocalJobs.class));
        }

        @Test
        public void testSendJob() throws Exception {
                logger.info(String.format("%s testSendJob IN",TestLocalJobs.class));
                Optional<JobRequest> req = Utils.getJobRequest(getClient());
                
                Assert.assertTrue("Couldn't build the request",req.isPresent());
                Job job=getClient().SendJob(req.get());
                Assert.assertTrue("Job has been sent",job.getId()!=null &&job.getId().length()>0);
                //So we don't over load the pipeline with different jobs
                checkJobInfo(job);
                logger.info(String.format("%s testSendJob OUT",TestLocalJobs.class));

        }

        private void checkJobInfo(Job in) throws Exception {
                Job job = getClient().Job(in.getId());
                ////Check the id
                Assert.assertEquals("Ids are not equal",in.getId(),job.getId());
                Assert.assertEquals("Nice name is set",Utils.NICE_NAME,job.getNicenameOrScriptOrMessages().get(0));
                Assert.assertTrue("Status is set",job.getStatus().value().length()>0);
                Assert.assertEquals("The priority is low","low",job.getPriority().toString().toLowerCase());
                

        }

        @Test
        public void testJobStatusCycle() throws Exception {
                logger.info(String.format("%s testJobStatusCycle IN",TestLocalJobs.class));
                Optional<JobRequest> req = Utils.getJobRequest(getClient());
                //send two jobs
                //TODO: Adjust the number of jobs via properties to be sure
                getClient().SendJob(req.get());
                Job job=getClient().SendJob(req.get());
                Assert.assertEquals("The job status is IDLE",job.getStatus().value(),"IDLE");
                job=Utils.waitForStatusChange("RUNNING",job,100000,getClient());
                Assert.assertEquals("The job status is RUNNING",job.getStatus().value(),"RUNNING");
                job=Utils.waitForStatusChange("DONE",job,100000,getClient());
                Assert.assertEquals("The job status is DONE",job.getStatus().value(),"DONE");
                logger.info(String.format("%s testJobStatusCycle OUT",TestLocalJobs.class));
        }

        @Test
        public void testAfterJob() throws Exception {
                logger.info(String.format("%s testAfterJob IN",TestLocalJobs.class));
                Optional<JobRequest> req = Utils.getJobRequest(getClient());
                Job job=getClient().SendJob(req.get());
                job=Utils.waitForStatusChange("DONE",job,100000,getClient());
                //test results
                //test log
                //test delete
                checkDelete(job);
                logger.info(String.format("%s testAfterJob OUT",TestLocalJobs.class));
        }

        private void checkDelete(Job in) throws Exception {
                PipelineClient client=getClient();
                client.Delete(in.getId());
                try{
                        client.Job(in.getId());
                        Assert.fail("The job shouldn't be here");
                }catch(javax.ws.rs.NotFoundException nfe){

                }

        }


 
       
}
