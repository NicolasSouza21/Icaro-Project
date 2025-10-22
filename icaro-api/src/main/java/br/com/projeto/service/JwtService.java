package br.com.projeto.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
// ✨ ALTERAÇÃO AQUI: Import 'SignatureAlgorithm' REMOVIDO
// ✨ ALTERAÇÃO AQUI: Imports de 'Decoders', 'Keys' e 'SecretKey' ADICIONADOS
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey; // ✨ ALTERAÇÃO AQUI
import java.nio.charset.StandardCharsets; // ✨ ALTERAÇÃO AQUI
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY; // String simples (do application.properties)

    @Value("${jwt.expiration-ms}")
    private long EXPIRATION_MS;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        
        return buildToken(claims, userDetails, EXPIRATION_MS);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // ✨ ALTERAÇÃO AQUI: Sintaxe moderna (usa a chave HMAC)
                // O .signWith agora recebe um objeto 'SecretKey'
                .signWith(getSignInKey()) 
                .compact();
    }

    private Claims extractAllClaims(String token) {
        // ✨ ALTERAÇÃO AQUI: Sintaxe moderna (parser agora usa 'verifyWith')
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✨ ALTERAÇÃO AQUI: Novo método helper
    /**
     * Gera uma chave de assinatura 'SecretKey' segura (padrão HMAC-SHA)
     * a partir da nossa SECRET_KEY (String) do application.properties.
     */
    private SecretKey getSignInKey() {
        // A chave precisa ter um tamanho mínimo. Usamos UTF-8 para garantir consistência.
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}