package nl.svsticky.crazy88.routes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import nl.svsticky.crazy88.model.User;
import nl.svsticky.crazy88.model.UserRole;
import nl.svsticky.crazy88.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthRoute {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${koala.client-id}")
    private String clientId;
    @Value("${koala.client-secret}")
    private String clientSecret;
    @Value("${koala.redirect-uri}")
    private String redirectUri;
    @Value("${koala.base}")
    private String koalaBase;

    private SecretKey key;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public OAuthRoute(@NotNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/redirect", produces = "application/json")
    public Map<String, String> redirectURL() {
        String redirect = URLEncoder.encode(this.redirectUri, StandardCharsets.UTF_8);
        return Map.of(
            "url", this.koalaBase + "/api/oauth/authorize?response_type=code&client_id=" + this.clientId + "&redirect_uri=" + redirect + "&response_type=code&scope=openid+profile+email+member-read"
        );
    }

    @PostMapping(value = "/token", produces = "application/json")
    public Map<String, Object> getToken(@RequestParam @NotNull String code) throws IOException, InterruptedException {
        if (code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }

        // todo should this be handled in a route handler?
        KoalaAuthResult authResult = this.exchangeCodeForUser(code);
        User user = this.userRepository.findByKoalaUserId(authResult.userId).orElse(null);
        if (user == null) {
            user = new User();
            user.setKoalaUserId(authResult.userId);
            user.setRole(authResult.isAdmin ? UserRole.ADMIN : UserRole.UNPRIVILEGED);
            this.userRepository.save(user);
        }
        user.setName(authResult.name);

        String token = this.tokenFor(user.getId(), user.getRole(), user.getName());
        return Map.of(
            "token", token,
            "role", user.getRole().name()
        );
    }

    private KoalaAuthResult exchangeCodeForUser(@NotNull String code) throws IOException, InterruptedException {
        String accessToken = this.accessTokenForCode(code);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(this.koalaBase + "/oauth/userinfo"))
            .header("Authorization", "Bearer " + accessToken)
            .build();

        HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
            int userId = responseBody.get("sub").getAsInt();
            String name = responseBody.get("full_name").getAsString();
            boolean isAdmin = responseBody.get("is_admin").getAsBoolean();
            return new KoalaAuthResult(userId, isAdmin, name);
        } else {
            throw new RuntimeException("Failed to get user info: " + response.body());
        }
    }

    private String accessTokenForCode(String code) throws IOException, InterruptedException {
        JsonObject json = new JsonObject();
        json.addProperty("grant_type", "authorization_code");
        json.addProperty("code", code);
        json.addProperty("client_id", this.clientId);
        json.addProperty("client_secret", this.clientSecret);
        json.addProperty("redirect_uri", this.redirectUri);

        String requestBody = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(this.koalaBase + "/api/oauth/token"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
            return responseBody.get("access_token").getAsString();
        } else {
            throw new RuntimeException("Failed to exchange code for token: " + response.body());
        }
    }

    private String tokenFor(long userId, UserRole role, String fullName) {
        return Jwts.builder()
            .setSubject(Long.toString(userId))
            .claim("role", role.name())
            .claim("name", fullName)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private record KoalaAuthResult(int userId, boolean isAdmin, String name) {
    }

}
