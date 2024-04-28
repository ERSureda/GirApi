package gom.girlocal.GirApi.security;

import gom.girlocal.GirApi.entity.User;
import gom.girlocal.GirApi.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private UserRepository userRepository;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${security.jwt.expiration-ms}")
    private long EXPIRATION_MS;

    public String generateToken(User user, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (EXPIRATION_MS * 60 * 1000)))
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("email", user.getEmail());
        extraClaims.put("Role", user.getRole().name());
        return extraClaims;
    }

    public String extractEmailFromJwt(String authToken) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(authToken).getBody().getSubject();
    }

    public Claims extractAllClaims(String authToken) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(authToken).getBody();
    }

    public boolean validateToken(String authToken) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(authToken).getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }

    public Authentication getAuthentication(String authToken) {
        UserDetails userDetails = UserPrincipal.create(userRepository.findByEmail(extractEmailFromJwt(authToken))
                .orElseThrow(() -> new JwtAuthenticationException("User not found", HttpStatus.NOT_FOUND)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}


