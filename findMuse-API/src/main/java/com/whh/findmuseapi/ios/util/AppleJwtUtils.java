package com.whh.findmuseapi.ios.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.whh.findmuseapi.ios.config.AppleProperties;
import com.whh.findmuseapi.ios.dto.key.ApplePublicKey;
import com.whh.findmuseapi.ios.dto.key.ApplePublicKeys;
import com.whh.findmuseapi.ios.feign.AppleAuthClient;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleJwtUtils {
    
    private final AppleAuthClient appleAuthClient;
    private final AppleProperties appleProperties;
    
    private final String APPLE_AUTH_URL = "https://appleid.apple.com";
    
    /**
     * User가 Sign in with Apple 요청(<a href="https://appleid.apple.com/auth/authorize">...</a>)으로 전달 받은 identity token을 이용한 최초 검증
     *
     * @param idToken
     * @return boolean
     */
    public boolean verifyIdentityToken(String idToken) throws BadRequestException{
        
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            ReadOnlyJWTClaimsSet payload = signedJWT.getJWTClaimsSet();
            
            // EXP(만료기간) 검증
            Date currentTime = new Date(System.currentTimeMillis());
            if (!currentTime.before(payload.getExpirationTime())) {
                throw new BadRequestException();
            }
            
            // ISS, AUD 검증
            String AUD = appleProperties.getClientId();
            if (!APPLE_AUTH_URL.equals(payload.getIssuer()) | !AUD.equals(payload.getAudience().get(0))) {
                throw new BadRequestException();
            }
            
            // RSA 검증
            if (verifyPublicKey(signedJWT)) {
                return true;
            }
            
        } catch (ParseException e) {
            log.info(e.toString());
            throw new BadRequestException();
        }
        
        return false;
    }
    
    /**
     * Apple 서버에서 공개 키를 받아서 서명 확인
     *
     * @param signedJWT
     * @return boolean
     */
    private boolean verifyPublicKey(SignedJWT signedJWT) {
        
        try {
            ApplePublicKeys keys = appleAuthClient.getAppleAuthPublicKey();
            ObjectMapper objectMapper = new ObjectMapper();
            
            for (ApplePublicKey key : keys.getKeys()) {
                RSAKey rsaKey = (RSAKey) JWK.parse(objectMapper.writeValueAsString(key));
                RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
                JWSVerifier verifier = new RSASSAVerifier(publicKey);
                
                if (signedJWT.verify(verifier)) {
                    return true;
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        
        return false;
    }
}
