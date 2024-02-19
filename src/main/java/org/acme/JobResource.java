package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/jobs")
public class JobResource {
    @POST
    @Transactional
    public Job add(Job job) {
        job.persist();
        return job;
    }

    @GET
    @Path("/{jobId}")
    public Job get(long jobId) {
        return Job.findById(jobId);
    }
}
