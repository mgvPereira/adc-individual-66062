package pt.unl.fct.di.adc.adc_individual_66062.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import pt.unl.fct.di.adc.adc_individual_66062.util.JWTConfig;

import java.util.Date;

public class JWTToken {

    public static String create(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWTConfig.EXPIRATION_TIME))
                .sign(JWTConfig.getAlgorithm());
    }

    public static DecodedJWT verify(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(JWTConfig.getAlgorithm()).build();
        return verifier.verify(token);
    }

    public static String getUsername(DecodedJWT jwt) {
        return jwt.getSubject();
    }

    public static String getRole(DecodedJWT jwt) {
        return jwt.getClaim("role").asString();
    }
}