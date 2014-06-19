package org.daisy.integration;

import java.util.HashMap;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.daisy.pipeline.webservice.jabx.base.Alive;
import org.daisy.pipeline.webservice.jabx.job.Jobs;
/**
 * Simple but full-featured pipline2 WS client
 */
public class PipelineClient {
        private static final HashMap<Class<?>,EndPoint> endPoints= new HashMap<Class<?>,EndPoint>();
        static{
                endPoints.put(Alive.class,new EndPoint("alive",Alive.class));
                endPoints.put(Jobs.class,new EndPoint("jobs",Jobs.class));
        }


        private WebTarget target;

        public PipelineClient(String baseUri) {
                this.target = ClientBuilder.newClient().target(baseUri);
        }
        private <T> T get(Class<T> clazz) {
                EndPoint entry=endPoints.get(clazz);
                return target.path(entry.getPath()).request().get(clazz);

        }

        public Alive Alive() {
                return this.get(Alive.class);
        }
        public Jobs Jobs() {
                return this.get(Jobs.class);
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
