package fish.payara.demos.conference.webapp.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author Fabio Turizo
 */
@Path("/session")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"Admin", "Attendee", "Speaker"})
public class SessionResource {

    @Context
    SecurityContext securityContext;
    
    @Inject
    @ConfigProperty(name = "session.url", defaultValue = "http://session:8080/session")
    String sessionUrl;

    private Invocation.Builder serviceTarget(String path) {
        JsonWebToken token = JsonWebToken.class.cast(securityContext.getUserPrincipal());
        Invocation.Builder result = ClientBuilder.newClient().target(sessionUrl).path(path).request();
        if (token != null) {
            result = result.header("Authorization", "Bearer "+ token.getRawToken());
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String payload) {
        return serviceTarget("")
                .post(Entity.json(payload));
    }

    @GET
    @Path("/all")
    public Response getAll() {
        return serviceTarget("/all")
                .get();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Integer id) {
        return serviceTarget("/" + id).get();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        return serviceTarget("/" + id).delete();
    }

    @GET
    @Path("/date/{date}")
    public Response forDate(@PathParam("date") String date) {
        return serviceTarget("/date/" + date).get();
    }
}
