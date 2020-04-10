package com.redhat.labs.omp.resource;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.redhat.labs.omp.exception.ResourceNotFoundException;
import com.redhat.labs.omp.model.Engagement;
import com.redhat.labs.omp.service.EngagementService;

@Path("/engagements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class EngagementResource {

    private static final String USERNAME_CLAIM = "";
    private static final String USER_EMAIL_CLAIM = "";

    public static final String DEFAULT_USERNAME = "omp-user";
    public static final String DEFAULT_EMAIL = "omp-email";

    @Inject
    JsonWebToken jwt;

    @Inject
    EngagementService engagementService;

//	@POST
//	@APIResponses(value = {
//			@APIResponse(responseCode = "201", description = "Engagement created. Location in header", content = @Content(mediaType = "application/json")) })
//	@Operation(summary = "Create an engagement persisted to Gitlab", description = "Engagement creates a new project in Gitlab")
//	public Response createEngagement(Engagement engagement, @Context UriInfo uriInfo) {
//		return engagementService.createEngagement(engagement, uriInfo);
//	}

    @POST
    public Response post(Engagement engagement) {

        // pull user info from token
        engagement.setLastUpdateByName(getUsernameFromToken());
        engagement.setLastUpdateByEmail(getUserEmailFromToken());

        return Response.status(HttpStatus.SC_CREATED).entity(engagementService.create(engagement)).build();

    }

    @PUT
    @Path("/customers/{customerName}/projects/{projectName}")
    public Engagement put(@PathParam("customerName") String customerName, @PathParam("projectName") String projectName,
            Engagement engagement) {

        // pull user info from token
        engagement.setLastUpdateByName(getUsernameFromToken());
        engagement.setLastUpdateByEmail(getUserEmailFromToken());

        return engagementService.update(customerName, projectName, engagement);

    }

    @GET
    @Path("/customers/{customerName}/projects/{projectName}")
    public Engagement get(@PathParam("customerName") String customerName,
            @PathParam("projectName") String projectName) {

        Optional<Engagement> optional = engagementService.get(customerName, projectName);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new ResourceNotFoundException("no resource found.");

    }

    @GET
    public List<Engagement> getAll() {
        return engagementService.getAll();
    }

    @DELETE
    @Path("/customers/{customerName}/projects/{projectName}")
    public Response delete(@PathParam("customerName") String customerName,
            @PathParam("projectName") String projectName) {

        engagementService.delete(customerName, projectName, getUsernameFromToken(), getUserEmailFromToken());
        return Response.status(HttpStatus.SC_NO_CONTENT).build();

    }

    @DELETE
    public Response deleteAll() {
        engagementService.deleteAll();
        return Response.status(HttpStatus.SC_NO_CONTENT).build();
    }

    private String getUsernameFromToken() {
        Optional<String> optional = jwt.claim(USERNAME_CLAIM);
        return optional.isPresent() ? optional.get() : DEFAULT_USERNAME;
    }

    private String getUserEmailFromToken() {
        Optional<String> optional = jwt.claim(USER_EMAIL_CLAIM);
        return optional.isPresent() ? optional.get() : DEFAULT_EMAIL;
    }

}