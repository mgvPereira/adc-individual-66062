package pt.unl.fct.di.adc.adc_individual_66062.resources;

import pt.unl.fct.di.adc.adc_individual_66062.service.UserService;
import pt.unl.fct.di.adc.adc_individual_66062.util.UserData;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/createaccount")
public class CreateAccountResource {

    private final UserService userService = new UserService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(Map<String, Object> request) {
        Map<String, Object> input = (Map<String, Object>) request.get("input");

        String username = (String) input.get("username");
        String password = (String) input.get("password");
        String confirmation = (String) input.get("confirmation");
        String phone = (String) input.get("phone");
        String address = (String) input.get("address");
        String role = (String) input.get("role");

        Map<String, Object> result = userService.createAccount(
                username, password, confirmation, phone, address, role
        );

        if (result.containsKey("status") && !result.get("status").equals("success")) {
            return Response.status(Response.Status.OK).entity(result).build();
        }

        return Response.ok(result).build();
    }
}