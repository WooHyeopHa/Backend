package com.whh.findmuseapi.ios;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.whh.findmuseapi.ios.config.AppleProperties;
import com.whh.findmuseapi.ios.dto.key.ApplePublicKey;
import com.whh.findmuseapi.ios.dto.key.ApplePublicKeys;
import com.whh.findmuseapi.ios.feign.AppleAuthClient;
import com.whh.findmuseapi.ios.util.AppleJwtUtils;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppleJwtUtilsTest {
    
    @InjectMocks
    private AppleJwtUtils appleJwtUtils;
    
    @Mock
    private AppleAuthClient appleAuthClient;
    
    @Mock
    private AppleProperties appleProperties;
    
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        java.security.KeyPair keyPair = gen.generateKeyPair();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        
    }
    
    @Test
    void 유효한_아이덴티티_토큰_검증_성공() throws Exception {
        // given
        String clientId = "com.example.app";
        when(appleProperties.getClientId()).thenReturn(clientId);
        
        JWTClaimsSet claimsSet = new JWTClaimsSet();
        claimsSet.setIssuer("https://appleid.apple.com");
        claimsSet.setAudience(clientId);
        claimsSet.setExpirationTime(new Date(System.currentTimeMillis() + 1000 * 60));
        
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build(),
            claimsSet);
        
        JWSSigner signer = new RSASSASigner(privateKey);
        signedJWT.sign(signer);
        
        String idToken = signedJWT.serialize();
        
        ApplePublicKey applePublicKey = new ApplePublicKey();
        applePublicKey.setKty("RSA");
        applePublicKey.setKid("123");
        applePublicKey.setUse("sig");
        applePublicKey.setAlg("RS256");
        applePublicKey.setN(Base64URL.encode(publicKey.getModulus()).toString());
        applePublicKey.setE(Base64URL.encode(publicKey.getPublicExponent()).toString());
        
        ApplePublicKeys applePublicKeys = new ApplePublicKeys();
        applePublicKeys.setKeys(Collections.singletonList(applePublicKey));
        
        when(appleAuthClient.getAppleAuthPublicKey()).thenReturn(applePublicKeys);
        
        // when
        boolean result = appleJwtUtils.verifyIdentityToken(idToken);
        
        // then
        assertTrue(result);
        verify(appleAuthClient).getAppleAuthPublicKey();
        verify(appleProperties).getClientId();
    }
    
    @Test
    void 만료된_아이덴티티_토큰_검증_실패() throws Exception {
        // given
        String clientId = "com.example.app";
        when(appleProperties.getClientId()).thenReturn(clientId);
        
        JWTClaimsSet claimsSet = new JWTClaimsSet();
        claimsSet.setIssuer("https://appleid.apple.com");
        claimsSet.setAudience(clientId);
        claimsSet.setExpirationTime(new Date(System.currentTimeMillis() - 1000));
        
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build(),
            claimsSet);
        
        JWSSigner signer = new RSASSASigner(privateKey);
        signedJWT.sign(signer);
        
        String idToken = signedJWT.serialize();
        
        // when & then
        assertThrows(BadRequestException.class, () -> appleJwtUtils.verifyIdentityToken(idToken));
    }
    
    @Test
    void 잘못된_발급자_아이덴티티_토큰_검증_실패() throws Exception {
        // given
        String clientId = "com.example.app";
        when(appleProperties.getClientId()).thenReturn(clientId);
        
        JWTClaimsSet claimsSet = new JWTClaimsSet();
        claimsSet.setIssuer("https://wrong-issuer.com");
        claimsSet.setAudience(clientId);
        claimsSet.setExpirationTime(new Date(System.currentTimeMillis() + 1000 * 60));
        
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build(),
            claimsSet);
        
        JWSSigner signer = new RSASSASigner(privateKey);
        signedJWT.sign(signer);
        
        String idToken = signedJWT.serialize();
        
        // when & then
        assertThrows(BadRequestException.class, () -> appleJwtUtils.verifyIdentityToken(idToken));
    }
}