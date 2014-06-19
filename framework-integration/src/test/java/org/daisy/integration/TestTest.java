package org.daisy.integration;

import org.daisy.pipeline.webservice.jabx.base.Alive;
import org.junit.Test;


public class TestTest {

        @Test
        public void test(){
        
                PipelineClient client= new PipelineClient("http://localhost:8181/ws");
               Alive alive = client.Alive();
                System.out.println("Alive!"+alive.getVersion());

        }
        
}
