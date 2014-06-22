package org.daisy.integration;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.daisy.pipeline.webservice.jabx.base.Alive;
import org.daisy.pipeline.webservice.jabx.job.Job;
import org.daisy.pipeline.webservice.jabx.job.Jobs;
import org.daisy.pipeline.webservice.jabx.request.JobRequest;
import org.daisy.pipeline.webservice.jabx.script.Scripts;
/**
 * Simple but full-featured pipline2 WS client
 */
public class PipelineClient {

        private WebTarget target;

        public PipelineClient(String baseUri) {
                this.target = ClientBuilder.newClient().target(baseUri);
        }
        private <T> T get(String path,Class<T> result) {
                
                return target.path(path).request().get(result);

        }
        private <T,U> U post(String path,T payload,Class<U> result) {
                
                return target.path(path).request().post(Entity.xml(payload),result);

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

        public Job SendJob(JobRequest request) throws Exception{
                return this.post("jobs",request,Job.class);
        }

        public Job Job(String id) throws Exception{
                return this.get(String.format("jobs/%s",id),Job.class);
        }
        public void Halt(String key) {
                this.get(String.format("admin/halt/%s",key),Void.class);
        }
}

