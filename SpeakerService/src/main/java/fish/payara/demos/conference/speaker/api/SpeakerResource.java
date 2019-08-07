package fish.payara.demos.conference.speaker.api;

import fish.payara.demos.conference.speaker.entitites.Speaker;
import fish.payara.demos.conference.speaker.services.SpeakerService;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Fabio Turizo
 */
@Path("/speaker")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SpeakerResource {    

    @Inject
    SpeakerService speakerService;
    
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/{id}")
    public Response getSpeaker(@PathParam("id") Integer id) {
        return Optional.ofNullable(speakerService.get(id))
                .map(Response::ok)
                .orElse(Response.status(Status.NOT_FOUND))
                .build();
    }
    
    @GET
    @Path("/all")
    public List<Speaker> allSpeakers() {
        return speakerService.getAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSpeaker(Speaker speaker, @Context UriInfo uriInfo) {
        if(securityContext.isUserInRole("Speaker")){
            speaker.setIdentity(securityContext.getUserPrincipal().getName());
        }
        Speaker result = speakerService.save(speaker);
        return Response.created(UriBuilder.fromPath(uriInfo.getPath()).path("{id}").build(result.getId()))
                        .entity(speaker).build();
    }
    
    @POST
    @Path("/accept/{id}")
    public Response acceptSpeaker(@PathParam("id") Integer id){
        Speaker speaker = speakerService.get(id).orElseThrow(() -> new NotFoundException("Speaker not found"));
        speakerService.save(speaker.accept());
        return Response.accepted().build();
    }
    
    @HEAD
    public Response checkSpeakers(@QueryParam("names") List<String> names){
        return (speakerService.allNamesExists(names) ? Response.ok() : Response.status(Status.NOT_FOUND)).build();
    }
}
