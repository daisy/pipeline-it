package org.daisy.integration;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Client;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.daisy.pipeline.webservice.jabx.client.Clients;

public class TestClients {
        private static final Logger logger = LoggerFactory.getLogger(TestClients.class);
        private static PipelineClient CLIENT=Utils.getClient();
        private static PipelineLauncher LAUNCHER;

        List<Client> toDelete;
        @BeforeClass 
        public static void bringUp() throws IOException {
                System.setProperty("enableLogging", "true");
                LAUNCHER=Utils.startPipeline(CLIENT);
                boolean up=LAUNCHER.launch();
                Assert.assertTrue("The pipeline is up",up);
        }

        @AfterClass
        public static void bringDown() throws IOException {
                LAUNCHER.halt();
        }

        @Test
        public void testDefaultClientNotShown() throws Exception {
                logger.info("testDefaultClientNotShown IN");
                Clients clients=CLIENT.clients();
                Assert.assertEquals("Default client is not listed (but is there I swear!)",clients.getClient().size(),0);
                logger.info("testDefaultClientNotShown OUT");
        }
        
}
