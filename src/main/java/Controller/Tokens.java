package Controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class Tokens {
    private static final String API_SECRET_KEY = "0";
    private static final long TOKEN_LIFETIME_MILLIS = 30000;
    private static final long REFRESH_TOKEN_LIFETIME_MILLIS = 1000L*60L*60L*24L*60L; //60 days
    private static final String ISSUER = "projektgrupp17";

    public static String getAccessToken(String id, String user) {
        return createJWT(id, user, TOKEN_LIFETIME_MILLIS);
    }

    public static String retRefreshToken(String id, String user) {
        return createJWT(id, user, REFRESH_TOKEN_LIFETIME_MILLIS);
    }

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

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(API_SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }


}
