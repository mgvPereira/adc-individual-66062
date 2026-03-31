package pt.unl.fct.di.adc.adc_individual_66062.resources;

import pt.unl.fct.di.adc.adc_individual_66062.service.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/modifyaccount")
public class ModifyAccountResource {

    private final UserService userService = new UserService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyAccount(Map<String, Object> request, @Context HttpHeaders headers) {
        String[] userInfo = getUserInfo(headers);
        if (userInfo == null) {
            return Response.status(Response.Status.OK)
                    .entity("{\"status\":9903,\"data\":\"Not authenticated\"}")
                    .build();
        }

        Map<String, Object> input = (Map<String, Object>) request.get("input");
        String usernameToModify = (String) input.get("username");
        Map<String, Object> attributes = (Map<String, Object>) input.get("attributes");
        String phone = (String) attributes.get("phone");
        String address = (String) attributes.get("address");

        Map<String, Object> result = userService.modifyAccountAttributes(
                userInfo[0], userInfo[1], usernameToModify, phone, address
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