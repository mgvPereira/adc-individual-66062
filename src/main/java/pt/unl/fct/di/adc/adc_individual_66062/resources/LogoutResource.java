package pt.unl.fct.di.adc.adc_individual_66062.resources;

import pt.unl.fct.di.adc.adc_individual_66062.service.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/logout")
public class LogoutResource {

    private final UserService userService = new UserService();
    private static final String COOKIE_NAME = "session::adc";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpHeaders headers) {
        String username = getUsernameFromCookie(headers);
        if (username == null) {
            return Response.status(Response.Status.OK)
                    .entity("{\"status\":9903,\"data\":\"Not authenticated\"}")
                    .build();
        }

        Map<String, Object> result = userService.logout(username);

        NewCookie expireCookie = new NewCookie(
                COOKIE_NAME,
                "",
                "/",
                null,
                "Expired",
                0,
                true,
                true
        );

        return Response.ok(result).cookie(expireCookie).build();
    }

    private String getUsernameFromCookie(HttpHeaders headers) {
        jakarta.ws.rs.core.Cookie cookie = headers.getCookies().get(COOKIE_NAME);
        if (cookie == null) return null;

        try {
            com.auth0.jwt.interfaces.DecodedJWT jwt =
                    pt.unl.fct.di.adc.adc_individual_66062.authentication.JWTToken.verify(cookie.getValue());
            return jwt.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}