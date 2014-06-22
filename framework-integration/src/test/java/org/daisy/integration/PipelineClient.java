package org.daisy.integration;

import java.util.HashMap;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.daisy.pipeline.webservice.jabx.base.Alive;
import org.daisy.pipeline.webservice.jabx.job.Jobs;
import org.daisy.pipeline.webservice.jabx.script.Scripts;
/**
 * Simple but full-featured pipline2 WS client
 */
public class PipelineClient {
        private static final HashMap<String,EndPoint> endPoints= new HashMap<String,EndPoint>();
        static{
                endPoints.put("alive",new EndPoint("alive",Alive.class));
                endPoints.put("jobs",new EndPoint("jobs",Jobs.class));
                endPoints.put("halt",new EndPoint("halt",Void.class));
        }


        private WebTarget target;

        public PipelineClient(String baseUri) {
                this.target = ClientBuilder.newClient().target(baseUri);
        }
        private <T> T get(String path,Class<T> result) {
                
                return target.path(path).request().get(result);

        }

        public Alive Alive() {
                return this.get("alive",Alive.class);
        }
        public Jobs Jobs() {
                return this.get("jobs",Jobs.class);
        }

        public Scripts Scripts() {
                return this.get("scripts",Scripts.class);
                
        }
        public void Halt(String key) {
                this.get(String.format("admin/halt/%s",key),Void.class);
        }
}

class EndPoint{
        String path;
        Class<?> clazz;

        /**
         * @param path
         * @param clazz
         */
        public EndPoint(String path, Class<?> clazz) {
                this.path = path;
                this.clazz = clazz;
        }

        /**
         * @return the path
         */
        public String getPath() {
                return path;
        }

        /**
         * @return the clazz
         */
        public Class<?> getClazz() {
                return clazz;
        }

}
