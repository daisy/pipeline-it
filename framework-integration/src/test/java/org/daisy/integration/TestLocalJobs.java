package org.daisy.integration;

import java.io.IOException;

import org.daisy.pipeline.webservice.jabx.base.Alive;
import org.daisy.pipeline.webservice.jabx.script.Scripts;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLocalJobs {

        private static PipelineClient getClient(){
                return new PipelineClient("http://localhost:8181/ws");
        }
        @BeforeClass
        public static void bringUp() throws IOException {
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

       
}
