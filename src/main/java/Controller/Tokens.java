package Controller;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

public class Tokens {
    private static final String API_SECRET_KEY = "0";
    private static final long TOKEN_LIFETIME_MILLIS = 30000;
    private static final long REFRESH_TOKEN_LIFETIME_MILLIS = 1000L*60L*60L*24L*60L; //60 days
    private static final String ISSUER = "projektgrupp17";

    /**
     * Generates a short lived access token
     * @param id the id of the token
     * @param user the user this token is valid for
     * @return a short lived JWT token
     */
    public static String getAccessToken(String id, String user) {
        return createJWT(id, user, TOKEN_LIFETIME_MILLIS);
    }

    /**
     * Generates a long lived refresh token for a specified user, with a specified token id
     * @param id the token id
     * @param user the user the token is valid for
     * @return a long lived refresh token
     */
    public static String retRefreshToken(String id, String user) {
        return createJWT(id, user, REFRESH_TOKEN_LIFETIME_MILLIS);
    }

    /**
     * Decodes a JWT token
     * @param jwToken the encoded token to decode
     * @return a decoded token body
     * @throws ValidationError
     */
    public static Jwt decodeJwToken(String jwToken) throws ValidationError {
        JwtParser parser = Jwts.parser().setSigningKey(API_SECRET_KEY.getBytes());
        return parser.parse(jwToken);
    }

    //https://developer.okta.com/blog/2018/10/31/jwts-with-java
    private static String createJWT(String id, String user, long lifetime) {
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = API_SECRET_KEY.getBytes();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(new Date())
                .setSubject(user)
                .setIssuer(ISSUER)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
            Date exp = new Date(System.currentTimeMillis() + lifetime);
            builder.setExpiration(exp);

        JwtParser parser = Jwts.parser().setSigningKey(API_SECRET_KEY.getBytes());
        parser.parse(builder.compact());
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        //jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiOGMwYmYwOS1jMTllLTQwYmUtOTBiMC1mNWMyZGMyM2EwMjkiLCJpYXQiOjE1ODcxMzExMzQsInN1YiI6ImFhYUBhYWEuY29tIiwiaXNzIjoicHJvamVrdGdydXBwMTciLCJleHAiOjE1ODcxMzExNjR9.nljcjUVsNWQ4XcNVP8FMhwtxI71JzHd7WU7LkGZTpNY";
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(API_SECRET_KEY.getBytes())
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
}
