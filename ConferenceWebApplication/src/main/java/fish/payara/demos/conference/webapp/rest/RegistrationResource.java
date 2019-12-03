package fish.payara.demos.conference.webapp.rest;



import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author fabio
 */
@Path("/register")
@RequestScoped
@RolesAllowed({"Admin", "Attendee", "Speaker"})
public class RegistrationResource {
    
    @Context
    SecurityContext securityContext;

    private Invocation.Builder serviceTarget(String path) {
        JsonWebToken token = JsonWebToken.class.cast(securityContext.getUserPrincipal());
        Invocation.Builder result = ClientBuilder.newClient().target("http://session:8080/register").path(path).request();
        if (token != null) {
            result = result.header("Authorization", "Bearer "+ token.getRawToken());
        }
        return result;
    }

    
    @POST
    @Path("/{sessionId}")
    public Response register(@PathParam("sessionId") Integer sessionId){
        return serviceTarget("/" + sessionId)
                .post(Entity.text(""));
    }
    
    @GET
    @Path("/current")
    public Response currentSessions(){
        return serviceTarget("/current")
                .get();
    }
}
