package fish.payara.demos.conference.webapp.rest;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationPath("/rest")
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({"Admin", "Attendee", "Speaker"})
public class SessionApplication extends Application{
}
