package ru.tbank.bookit.book_it_backend.controller;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.tbank.bookit.book_it_backend.DTO.AIRequest;
import ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents.MessageDTO;
import ru.tbank.bookit.book_it_backend.DTO.AIResponse;
import ru.tbank.bookit.book_it_backend.DTO.RawAIRequest;
import ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents.CompletionOptionsDTO;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/get-sql")
    public ResponseEntity<AIResponse> getSql(
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
        CompletionOptionsDTO completionOptions = new CompletionOptionsDTO(
                null,
                0,
                null,
                null
        );
        List<MessageDTO> messages = new ArrayList<>();
        messages.add(new MessageDTO("system", dotenv.get("SYSTEM_TEXT")));
        messages.add(new MessageDTO("user", rawAIRequest.prompt()));
        AIRequest aiRequest = new AIRequest(dotenv.get("MODEL_URI"), completionOptions, messages);

        HttpEntity<AIRequest> aiRequestHttpEntity = new HttpEntity<>(aiRequest, headers);
        ResponseEntity<AIResponse> response = restTemplate.exchange(url, HttpMethod.POST, aiRequestHttpEntity, AIResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }
}
