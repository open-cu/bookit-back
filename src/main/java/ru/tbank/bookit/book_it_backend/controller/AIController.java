package ru.tbank.bookit.book_it_backend.controller;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.tbank.bookit.book_it_backend.DTO.AIRequest;
import ru.tbank.bookit.book_it_backend.DTO.AIRequestBuilder;
import ru.tbank.bookit.book_it_backend.DTO.AIResponse;
import ru.tbank.bookit.book_it_backend.DTO.RawAIRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final JdbcTemplate jdbcTemplate;

    public AIController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/get-sql")
    public ResponseEntity<List<Map<String, Object>>> getSql(
            @RequestBody RawAIRequest rawAIRequest
            ) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String url = dotenv.get("API_URL");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", dotenv.get("AUTHORIZATION"));

        AIRequest aiRequest = AIRequestBuilder.createAIRequest(
                dotenv.get("SYSTEM_TEXT"),
                rawAIRequest.prompt(),
                dotenv.get("MODEL_URI")
        );

        HttpEntity<AIRequest> aiRequestHttpEntity = new HttpEntity<>(aiRequest, headers);
        ResponseEntity<AIResponse> response = restTemplate.exchange(url, HttpMethod.POST, aiRequestHttpEntity, AIResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {

            String SQLRequest = response.getBody() != null ? response.getBody().result().alternatives().getFirst().message().text() : null;
            if (SQLRequest != null) {
                SQLRequest = SQLRequest.replace("\\n", "\n");
                SQLRequest = SQLRequest.replace("\\", "");
                SQLRequest = SQLRequest.replace("```", "");
                List<Map<String, Object>> results = jdbcTemplate.queryForList(SQLRequest);
                return ResponseEntity.ok(results);
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
