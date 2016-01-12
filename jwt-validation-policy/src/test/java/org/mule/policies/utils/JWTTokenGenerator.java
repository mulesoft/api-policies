package org.mule.policies.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

public class JWTTokenGenerator {

	public static String getHMAC(String algorithm, String secret, String issuer, String audience, boolean expired) throws UnsupportedEncodingException, JoseException, InvalidJwtException {
		
		JwtClaims claims = initClaims(issuer, audience, expired);
		
		Key key = new HmacKey(secret.getBytes("UTF-8"));

		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(algorithm);
		jws.setKey(key);
		jws.setDoKeyValidation(false); // relaxes the key length requirement

		String jwt = jws.getCompactSerialization();		
		
		if (!expired){
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
			        .setRequireExpirationTime()
			        .setAllowedClockSkewInSeconds(30)
			        .setRequireSubject()
			        .setExpectedIssuer(issuer)
			        .setExpectedAudience(audience)
			        .setVerificationKey(key)
			        .setRelaxVerificationKeyValidation() // relaxes key length requirement		        
			        .build();
			if (issuer != null && audience != null)
				jwtConsumer.processToClaims(jwt);
		}
		return jwt;
	}
	
	
	
	public static String getRSA(String algorithm, KeyPair keyPair, String issuer, String audience, boolean expired) throws UnsupportedEncodingException, JoseException, InvalidJwtException, NoSuchAlgorithmException, InvalidKeySpecException {
		JwtClaims claims = initClaims(issuer, audience, expired);
						        		      
        JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(algorithm);
		
		jws.setKey(keyPair.getPrivate());
		
		String jwt = jws.getCompactSerialization();
		
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
		        .setRequireExpirationTime()
		        .setAllowedClockSkewInSeconds(30)
		        .setRequireSubject()
		        .setExpectedIssuer(issuer)
		        .setExpectedAudience(audience)	
		        .setVerificationKey(keyPair.getPublic())
		        .build();

		jwtConsumer.processToClaims(jwt);
		
		return jwt;		
	}

	private static JwtClaims initClaims(String issuer, String audience,
			boolean expired) {
		JwtClaims claims = new JwtClaims();
		
		if (expired)		
			claims.setExpirationTime(NumericDate.fromMilliseconds(System.currentTimeMillis() - 24 * 3600 * 1000));
		else
			claims.setExpirationTimeMinutesInTheFuture(5);		
		
		claims.setSubject(JWTConstants.SUBJECT);
		claims.setIssuer(issuer);	
		claims.setAudience(audience);
		return claims;
	}

	public static String getHMAC(String algorithm, String secret,
			String issuer, String[] audiences, boolean expired) throws JoseException, InvalidJwtException, UnsupportedEncodingException {
		
		Key key = new HmacKey(secret.getBytes("UTF-8"));

		JwtClaims claims = new JwtClaims();
		
		if (expired)		
			claims.setExpirationTime(NumericDate.fromMilliseconds(System.currentTimeMillis() - 24 * 3600 * 1000));
		else
			claims.setExpirationTimeMinutesInTheFuture(5);		
		
		claims.setSubject(JWTConstants.SUBJECT);
		claims.setIssuer(issuer);		
		claims.setAudience(Arrays.asList(audiences));
		
		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(algorithm);
		jws.setKey(key);
		jws.setDoKeyValidation(false); // relaxes the key length requirement

		String jwt = jws.getCompactSerialization();		
		
		if (!expired){
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
			        .setRequireExpirationTime()
			        .setAllowedClockSkewInSeconds(30)
			        .setRequireSubject()
			        .setExpectedIssuer(issuer)
			        .setExpectedAudience(audiences)
			        .setVerificationKey(key)
			        .setRelaxVerificationKeyValidation() // relaxes key length requirement		        
			        .build();
	
			jwtConsumer.processToClaims(jwt);
		}
		return jwt;
	}
}
