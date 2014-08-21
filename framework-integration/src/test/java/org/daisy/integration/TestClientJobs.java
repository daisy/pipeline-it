package org.daisy.integration;

import java.util.List;

import org.daisy.pipeline.webservice.jabx.client.Client;
import org.daisy.pipeline.webservice.jabx.client.Priority;
import org.daisy.pipeline.webservice.jabx.job.Job;
import org.daisy.pipeline.webservice.jabx.request.JobRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class TestClientJobs {


        private static final Logger logger = LoggerFactory.getLogger(TestClientJobs.class);
        private static PipelineLauncher LAUNCHER;
        private static Creds CREDS_DEF=new Creds();
        private static Creds CREDS_OTHER=new Creds();
        static {
                CREDS_DEF.clientId="clientId";
                CREDS_DEF.secret="supersecret";
                CREDS_OTHER.clientId="other";
                CREDS_OTHER.secret="nososecret";
        }
        @BeforeClass 
        public static void bringUp() throws Exception {
            System.setProperty("enableLogging", "true");
                PipelineClient clientDef=Utils.getClient(CREDS_DEF.clientId,CREDS_DEF.secret);
                LAUNCHER=Utils.startPipeline(clientDef);
                LAUNCHER.setEnv("PIPELINE2_AUTH","-Dorg.daisy.pipeline.ws.authentication=true");
                LAUNCHER.setProperty("org.daisy.pipeline.ws.authentication.authentication","true");
                LAUNCHER.setProperty("org.daisy.pipeline.ws.authentication.key",CREDS_DEF.clientId);
                LAUNCHER.setProperty("org.daisy.pipeline.ws.authentication.secret",CREDS_DEF.secret);
                
                boolean up=LAUNCHER.launch();
                Client other=new Client();
                other.setId(CREDS_OTHER.clientId);
                other.setRole("CLIENTAPP");
                other.setSecret(CREDS_OTHER.secret);
                other.setContact("admin@daisy.org");
                other.setPriority(Priority.LOW);
                clientDef.addClient(other);
                Assert.assertTrue("The pipeline is up",up);
        }
        @AfterClass
        public static void bringDown() throws Exception {
            PipelineClient clientDef=Utils.getClient(CREDS_DEF.clientId,CREDS_DEF.secret);
            clientDef.deleteClient(CREDS_OTHER.clientId);
            LAUNCHER.halt();
            Utils.cleanUpDb();

        }

        List<Job> toDelete;
        @Before
        public void setUp() {
                toDelete=Lists.newLinkedList();

        }
        @After
        public void tearDown(){
                logger.info(String.format("There are %s jobs to delete", toDelete.size()));
                PipelineClient client=Utils.getClient(CREDS_DEF.clientId,CREDS_DEF.secret);
                for ( Job j:this.toDelete){
                        try{
                                logger.info(String.format("Deleting job %s",j.getId()));
                                client.delete(j.getId());
                        }catch (Exception e){
                                logger.info(e.getMessage());

                        }
                }
                logger.info(String.format("There are %s jobs after the test", client.jobs().getJob().size()));
        }
        @Test
        public void testJobAccess() throws Exception {
                logger.info("job access IN");
                PipelineClient clientDef=Utils.getClient(CREDS_DEF.clientId,CREDS_DEF.secret);
                PipelineClient clientOther=Utils.getClient(CREDS_OTHER.clientId,CREDS_OTHER.secret);
                Optional<JobRequest> req = Utils.getJobRequest(clientDef);

                Job admin=clientDef.sendJob(req.get());
                toDelete.add(admin);
                Assert.assertEquals("Admin has one job", clientDef.jobs().getJob().size(),1);
                Assert.assertEquals("Clientapp hasn't got any", clientOther.jobs().getJob().size(),0);
                req = Utils.getJobRequest(clientOther);
                Job other=clientOther.sendJob(req.get());
                toDelete.add(other);
                Assert.assertEquals("Admin see both jobs", clientDef.jobs().getJob().size(),2);
                Assert.assertEquals("Clientapp only its job", clientOther.jobs().getJob().size(),1);
                try{
                        clientOther.job(admin.getId());
                        Assert.fail("Clientapp accessed the admin job!");
                }catch (Exception e){
                }
                try{
                        clientOther.delete(admin.getId());
                        Assert.fail("Clientapp accessed the admin job!");
                }catch (Exception e){
                }

                Job fromServer=clientDef.job(other.getId());
                Assert.assertNotNull("Admin accessed the client app job",fromServer);


                logger.info("job access OUT");
        }
        
        private static class Creds{
                String clientId;
                String secret;
        }
}
