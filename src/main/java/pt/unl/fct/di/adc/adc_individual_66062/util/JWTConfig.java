package pt.unl.fct.di.adc.adc_individual_66062.util;

import com.auth0.jwt.algorithms.Algorithm;

public class JWTConfig {

    public enum AlgorithmType {
        HS256, HS384, HS512
    }

    public static final String HMAC_SECRET = "chave-super-secreta-1234";

    // Token expiration: 15 minutes (900 seconds)
    public static final long EXPIRATION_TIME = 1000 * 60 * 15; // 15 minutes in milliseconds

    public static final AlgorithmType ALGORITHM = AlgorithmType.HS256;

    public static Algorithm getAlgorithm() {
        switch (ALGORITHM) {
            case HS256:
                return Algorithm.HMAC256(HMAC_SECRET);
            case HS384:
                return Algorithm.HMAC384(HMAC_SECRET);
            case HS512:
                return Algorithm.HMAC512(HMAC_SECRET);
            default:
                return Algorithm.HMAC256(HMAC_SECRET);
        }
    }
}