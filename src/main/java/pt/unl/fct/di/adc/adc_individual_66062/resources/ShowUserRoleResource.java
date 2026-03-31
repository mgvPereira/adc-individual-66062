package pt.unl.fct.di.adc.adc_individual_66062.resources;

import pt.unl.fct.di.adc.adc_individual_66062.service.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/showuserrole")
public class ShowUserRoleResource {

    private final UserService userService = new UserService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response showUserRole(Map<String, Object> request, @Context HttpHeaders headers) {
        String[] userInfo = getUserInfo(headers);
        if (userInfo == null) {
            return Response.status(Response.Status.OK)
                    .entity("{\"status\":9903,\"data\":\"Not authenticated\"}")
                    .build();
        }

        Map<String, Object> input = (Map<String, Object>) request.get("input");
        String usernameToCheck = (String) input.get("username");

        Map<String, Object> result = userService.showUserRole(userInfo[0], userInfo[1], usernameToCheck);

        if (result.containsKey("status")) {
            return Response.status(Response.Status.OK).entity(result).build();
        }

        return Response.ok(result).build();
    }

    private String[] getUserInfo(HttpHeaders headers) {
        jakarta.ws.rs.core.Cookie cookie = headers.getCookies().get("session::adc");
        if (cookie == null) return null;

        try {
            com.auth0.jwt.interfaces.DecodedJWT jwt =
                    pt.unl.fct.di.adc.adc_individual_66062.authentication.JWTToken.verify(cookie.getValue());
            return new String[]{jwt.getSubject(), jwt.getClaim("role").asString()};
        } catch (Exception e) {
            return null;
        }
    }
}