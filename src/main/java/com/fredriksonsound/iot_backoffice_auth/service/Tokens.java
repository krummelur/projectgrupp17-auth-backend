package com.fredriksonsound.iot_backoffice_auth.service;

import com.fredriksonsound.iot_backoffice_auth.Environment;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureException;
import org.apache.tomcat.util.codec.binary.Base64;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public class Tokens {
    private static class Keys {
        PublicKey pub = null;
        PrivateKey priv = null;
        PublicKey getPublic() {return pub;}
        PrivateKey getPrivate() {return priv; }
        private Keys() {
            String privKey = System.getenv(Environment.JWT_PRIV_KEY_STR);
            String pubKey = System.getenv(Environment.JWT_PUB_KEY_STR);
            try {
                this.priv =  KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(new Base64().decode(privKey.getBytes())));
                this.pub = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(new Base64().decode(pubKey.getBytes())));
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e)
            { e.printStackTrace(); System.exit(-1); }
        }
    }

    private static final long TOKEN_LIFETIME_MILLIS = 1000 * 60 * 3;
    private static final long REFRESH_TOKEN_LIFETIME_MILLIS = 1000L*60L*60L*24L*30L; //30 days
    private static final String ISSUER = "projektgrupp17-auth";
    private static Keys keys = new Keys();

    public static String getCustomToken(String id, String user, long lifetime) {
        return createJWT(id, user, lifetime);
    }


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
    public static String getRefreshToken(String id, String user) {
        return createJWT(id, user, REFRESH_TOKEN_LIFETIME_MILLIS);
    }

    /**
     * Decodes a JWT token
     * @param jwToken the encoded token to decode
     * @return a decoded token body
     * @throws MalformedJwtException    if error occurs in parser
     * @throws SignatureException
     * @throws IllegalArgumentException
     */
    public static Jwt decodeJwToken(String jwToken) {
        JwtParser parser = Jwts.parser().setSigningKey(keys.getPublic());
        return parser.parse(jwToken);
    }

    //https://developer.okta.com/blog/2018/10/31/jwts-with-java
    private static String createJWT(String id, String user, long lifetime) {
        //Using an asymmetric signing algorithm so tokens can be verified without sharing private key with other services.
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(new Date())
                .setSubject(user)
                .setIssuer(ISSUER)
                .signWith(SignatureAlgorithm.RS256, keys.getPrivate());

        Date exp = new Date(System.currentTimeMillis() + lifetime);
        builder.setExpiration(exp);
        return builder.compact();
    }
}
