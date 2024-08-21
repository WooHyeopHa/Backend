package com.whh.findmuseapi.ios.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.whh.findmuseapi.common.constant.Infos.Role;
import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.ios.config.AppleProperties;
import com.whh.findmuseapi.ios.dto.AppleLoginResponse;
import com.whh.findmuseapi.ios.dto.AppleToken;
import com.whh.findmuseapi.ios.feign.AppleAuthClient;
import com.whh.findmuseapi.ios.util.AppleJwtUtils;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.coyote.BadRequestException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@EnableConfigurationProperties({ AppleProperties.class })
@RequiredArgsConstructor
@Service
public class AppleService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    private final AppleProperties appleProperties;
    private final AppleAuthClient appleAuthClient;
    private final AppleJwtUtils appleJwtUtils;
    
    public User login(AppleLoginResponse appleLoginResponse) throws BadRequestException{
        try {
            appleJwtUtils.verifyIdentityToken(appleLoginResponse.getIdToken());
            
            AppleToken.Response tokenResponse = generateAuthToken(appleLoginResponse.getCode());
            
            String accessToken = tokenResponse.getAccessToken();
            
            // ID Token을 통해 회원 고유 식별자 받기
            SignedJWT signedJWT = SignedJWT.parse(String.valueOf(tokenResponse.getIdToken()));
            ReadOnlyJWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject payload = objectMapper.readValue(jwtClaimsSet.toJSONObject().toJSONString(), JSONObject.class);
            JsonNode jsonNode = objectMapper.readTree(payload.toJSONString());
            
            // 유저 정보 추출
            String accountId = String.valueOf(payload.get("sub"));
            String email = String.valueOf(payload.get("email"));
            
            JsonNode nameNode = jsonNode.path("name");
            String firstName = nameNode.path("firstName").asText("");
            String lastName = nameNode.path("lastName").asText("");
            String name = firstName + " " + lastName;
          
            User findUser = userRepository.findByAccountId(accountId).orElse(null);
            
            if (findUser == null) {
                // 신규 회원가입의 경우 DB에 저장
                return userRepository.save(
                    User.builder()
                        .accountId(accountId)
                        .name(name)
                        .email(email)
                        .role(Role.GUEST)
                        .accessToken(accessToken)
                        .refreshToken(jwtService.createRefreshToken())
                        .build()
                );
            } else {
                // 기존 회원 경우 Acess Token 업데이트를 위해 DB에 저장
                findUser.setAccessToken(accessToken);
                userRepository.save(findUser);
            }
            return findUser;
        } catch (JsonProcessingException | ParseException e) {
            log.info(e.toString());
            throw new BadRequestException();
        }
    }
    public AppleToken.Response generateAuthToken(String code) throws BadRequestException{
        if (code == null) throw new IllegalArgumentException();
        
        String clientId = appleProperties.getClientId();
        return appleAuthClient.getToken(AppleToken.Request.builder()
                .code(code)
                .client_id(clientId)
                .client_secret(createClientSecretKey(clientId))
                .grant_type("authorization_code")
                .refresh_token(null)
            .build());
    }
    
    public String createClientSecretKey(String clientId) throws BadRequestException{
        // headersParams 적재
        Map<String, Object> headerParamsMap = new HashMap<>();
        headerParamsMap.put("kid", appleProperties.getLoginKey());
        headerParamsMap.put("alg", "ES256");
        
        // clientSecretKey 생성
        try {
            return Jwts
                .builder()
                .setHeader(headerParamsMap)
                .setIssuer(appleProperties.getTeamId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 30)) // 만료 시간 (30초)
                .setAudience(appleProperties.getAuthUrl())
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
        } catch (IOException e) {
            log.info(e.toString());
            throw new BadRequestException();
        }
    }
    
    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(appleProperties.getKeyPath());
        String privateKey = new String(resource.getInputStream().readAllBytes());
        
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
        
        return converter.getPrivateKey(privateKeyInfo);
    }
    
    public void deleteAppleAccount(String accessToken) throws BadRequestException{
        User user = extractUserFromAccessToken(accessToken);
        deleteUserAcount(user);
        
        String data = "client_id=" + appleProperties.getClientId() +
            "&client_secret=" + createClientSecretKey(appleProperties.getClientId()) +
            "&token=" + user.getAccessToken() +
            "&token_type_hint=access_token";
        sendRevokeRequest(data);
    }
    
    private void sendRevokeRequest(String data) {
        RestTemplate restTemplate = new RestTemplate();
        String appleRevokeUrl = "https://appleid.apple.com/auth/revoke";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<String> entity = new HttpEntity<>(data, headers);
        
        ResponseEntity<String> responseEntity = restTemplate.exchange(appleRevokeUrl, HttpMethod.POST, entity, String.class);
        
        // Get the response status code and body
        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();
        
        log.info("애플 포그인 연결해제 요청 결과");
        log.info("Status Code: " + statusCode);
        log.info("Response: " + responseBody);
    }
    
    public void deleteUserAcount(User user) {
        userRepository.delete(user);
    }

    public User extractUserFromAccessToken(String accessToken) throws BadRequestException{
        Optional<String> email = jwtService.extractClaimFromJWT(JwtService.CLAIM_EMAIL, accessToken);
        if (email.isEmpty()) {
            throw new BadRequestException();
        }
        
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new BadRequestException();
        }
        
        return user.get();
    }
    
    public User loginWithToken(AppleLoginResponse appleLoginResponse) throws BadRequestException{
        return login(appleLoginResponse);
    }
    
    public void loginSuccess(User user, HttpServletResponse response) throws BadRequestException {
        String accessToken = jwtService.createAccessToken(user.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(user.getEmail(), refreshToken);
    }
}
