package fish.payara.demos.conference.webapp.rest;

import java.util.*;
import java.util.function.Function;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author Fabio Turizo
 */
@Path("/speaker")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"Admin", "Speaker"})
public class SpeakerResource {    

    @Context
    SecurityContext securityContext;
    
    @Inject
    @ConfigProperty(name = "speaker.url", defaultValue = "http://speaker:8080/speaker")
    String speakerUrl;

    private Invocation.Builder serviceTarget(String path) {
        return serviceTarget(path, null);
    }
    
    private Invocation.Builder serviceTarget(String path, Function<WebTarget, WebTarget> webTargetMutator) {
        JsonWebToken token = JsonWebToken.class.cast(securityContext.getUserPrincipal());
        WebTarget webTarget = ClientBuilder.newClient().target(speakerUrl).path(path);
        if (webTargetMutator != null) {
            webTarget = webTargetMutator.apply(webTarget);
        }
        Invocation.Builder result = webTarget.request();
        if (token != null) {
            result = result.header("Authorization", "Bearer "+ token.getRawToken());
        }
        return result;
    }


    @GET
    @Path("/{id}")
    public Response getSpeaker(@PathParam("id") Integer id) {
        return serviceTarget("/" + id).get();
    }
    
    @GET
    @Path("/all")
    public Response allSpeakers() {
        return serviceTarget("/all").get();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSpeaker(String payload) {
        return serviceTarget("")
                .post(Entity.json(payload));
    }
    
    @POST
    @Path("/accept/{id}")
    @RolesAllowed("Admin")
    public Response acceptSpeaker(@PathParam("id") Integer id){
        return serviceTarget("/accept/" + id)
                .post(Entity.json(""));
    }
    
    @HEAD
    @PermitAll
    public Response checkSpeakers(@QueryParam("names") List<String> names){
        return serviceTarget("", webTarget -> webTarget.queryParam("names", names.toArray())).head();
    }
}
