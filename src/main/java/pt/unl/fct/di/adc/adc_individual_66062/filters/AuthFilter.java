package pt.unl.fct.di.adc.adc_individual_66062.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import pt.unl.fct.di.adc.adc_individual_66062.authentication.JWTToken;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String COOKIE_NAME = "session::adc";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.equals("login") || path.equals("createaccount")) {
            return;
        }

        Map<String, Cookie> cookies = requestContext.getCookies();
        Cookie sessionCookie = cookies.get(COOKIE_NAME);

        if (sessionCookie == null) {
            requestContext.abortWith(Response.status(Response.Status.OK)
                    .entity("{\"status\":9903,\"data\":\"No authentication token found\"}")
                    .build());
            return;
        }

        try {
            DecodedJWT jwt = JWTToken.verify(sessionCookie.getValue());
            String username = JWTToken.getUsername(jwt);
            String role = JWTToken.getRole(jwt);

            requestContext.setProperty("username", username);
            requestContext.setProperty("role", role);

        } catch (JWTVerificationException e) {
            requestContext.abortWith(Response.status(Response.Status.OK)
                    .entity("{\"status\":9904,\"data\":\"Invalid or expired token\"}")
                    .build());
        }
    }
}