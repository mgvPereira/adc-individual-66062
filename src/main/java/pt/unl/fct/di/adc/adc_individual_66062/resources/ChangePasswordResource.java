package pt.unl.fct.di.adc.adc_individual_66062.resources;

import pt.unl.fct.di.adc.adc_individual_66062.service.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/changepassword")
public class ChangePasswordResource {

    private final UserService userService = new UserService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(Map<String, Object> request, @Context HttpHeaders headers) {
        String[] userInfo = getUserInfo(headers);
        if (userInfo == null) {
            return Response.status(Response.Status.OK)
                    .entity("{\"status\":9903,\"data\":\"Not authenticated\"}")
                    .build();
        }

        Map<String, Object> input = (Map<String, Object>) request.get("input");
        String currentPassword = (String) input.get("currentPassword");
        String newPassword = (String) input.get("newPassword");
        String confirmation = (String) input.get("confirmation");

        Map<String, Object> result = userService.changeUserPassword(
                userInfo[0], currentPassword, newPassword, confirmation
        );

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