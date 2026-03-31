package pt.unl.fct.di.adc.adc_individual_66062.resources;

import pt.unl.fct.di.adc.adc_individual_66062.service.UserService;
import pt.unl.fct.di.adc.adc_individual_66062.util.LoginData;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/login")
public class LoginResource {

    private final UserService userService = new UserService();
    private static final String COOKIE_NAME = "session::adc";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginData data) {
        String jwt = userService.login(data.getUsername(), data.getPassword());

        if (jwt == null) {
            return Response.status(Response.Status.OK)
                    .entity("{\"status\":9900,\"data\":\"Invalid credentials\"}")
                    .build();
        }

        NewCookie sessionCookie = new NewCookie(
                COOKIE_NAME,
                jwt,
                "/",
                null,
                "ADC Session",
                900,
                true,
                true
        );

        Map<String, Object> response = Map.of(
                "status", "success",
                "data", Map.of("message", "Login successful")
        );

        return Response.ok(response)
                .cookie(sessionCookie)
                .build();
    }
}