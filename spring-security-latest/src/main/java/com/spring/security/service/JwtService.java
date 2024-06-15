package com.spring.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component  // automatically create bean of JwtService class at the load of spring boot application/project
public class JwtService {

    private static final String SECRET = "c573c0eab6339eddf8161d7abf117ba2b982cc9e4f216e404e7a4ca2b7a24d58";

//    ==================================================================
//    decode jwt token and get all value like userName, expiredAt etc.
//    ==================================================================
    // extract userName from token
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject); // subject is the userName in jwt token
    }

    // extract expiration from token
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration); // subject is the userName in jwt token
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    // get all jwt tokens in extracted form
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // check if token is valid or expired
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    //
    public Boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


//    =========================================================================
    // create JWT TOKEN
//    =========================================================================
    // note: in JWT all the components(i.e header, payload, signature) are called as claims
    // we need to generate claims for each component in jwt
    // for that only we have use Map<String, Object> inorder to store each component
    public String generateToken(String userName){
        //
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        // this method will create jwt token
        // to use jwt add dependency of jwt in pom.xml

        return Jwts.builder()
                .setClaims(claims)  // set claims
                .setSubject(userName)   // set the username in payload
                .setIssuedAt(new Date(System.currentTimeMillis()))  // current time of system to issue jwt token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))   // set expiration time to 30 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();  // provide signKey, signatureAlgorithm (.compact() will give you the jwt string back)

    }

    // in getSignKey() method we will provide the encoded base 64 header and encoded base 64 signature for jwt
    private Key getSignKey() {
        // create a byte array as we are converting our key into base 64 byte encoder
        // used this link to generate encryption key https://asecuritysite.com/encryption/plain

        byte[] keyBytes = Decoders.BASE64.decode(SECRET); // secret is my own encrypted secret
        return Keys.hmacShaKeyFor(keyBytes);   // this will give you the sign key based on the secret


    }

}
