package nl.svsticky.crazy88.routes;

import com.google.gson.JsonObject;
import nl.svsticky.crazy88.model.User;
import nl.svsticky.crazy88.model.UserRole;
import nl.svsticky.crazy88.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/oauth")
public class OAuthRoute {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final UserRepository userRepository;

    public OAuthRoute(@NotNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/token")
    public Map<String, Object> getToken(@RequestParam @NotNull String code) {
        if (code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }

        int koalaId = this.exchangeCodeForUserId(code);
        Optional<User> existingUser = this.userRepository.findByKoalaUserId(koalaId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            return Map.of("userId", user.getId(), "koalaUserId", user.getKoalaUserId(), "role", user.getRole());
        } else {
            User newUser = new User();
            newUser.setKoalaUserId(koalaId);
            newUser.setRole(UserRole.UNPRIVILEGED);
            this.userRepository.save(newUser);
            return Map.of("userId", newUser.getId(), "koalaUserId", newUser.getKoalaUserId(), "role", newUser.getRole());
        }
    }

    private int exchangeCodeForUserId(@NotNull String code) {
        JsonObject json = new JsonObject();
        json.addProperty("grant_type", "authorization_code");
        json.addProperty("code", code);
        json.addProperty("client_id", System.getenv().getOrDefault("KOALA_CLIENT_ID", "vSXRuP_WQ4Bfeh8b_AGPHZ7YS-nXQaphye-CHZLlucE"));
        json.addProperty("client_secret", System.getenv().getOrDefault("KOALA_CLIENT_SECRET", "CWq537wqLNZ4TxGr0NNRYnEwk-Zim_c2DUTokZC3PKM"));
        json.addProperty("redirect_uri", System.getenv().getOrDefault("KOALA_REDIRECT_URI", "http://localhost:5173/oauth/return"));

        String requestBody = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://koala.dev.svsticky.nl/api/oauth/token"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonObject responseBody = com.google.gson.JsonParser.parseString(response.body()).getAsJsonObject();
                int credentialsId = responseBody.get("credentials_id").getAsInt();
                boolean isAdmin = responseBody.get("credentials_type").getAsString().equals("Admin");
                return isAdmin ? credentialsId : -credentialsId; // todo this sucks lmao
            } else {
                throw new RuntimeException("Failed to exchange code for token: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during token exchange", e);
        }
    }

}
