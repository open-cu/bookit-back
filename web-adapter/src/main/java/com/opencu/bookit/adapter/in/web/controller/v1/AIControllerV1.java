package com.opencu.bookit.adapter.in.web.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencu.bookit.adapter.in.web.dto.request.RawAIRequest;
import com.opencu.bookit.application.service.ai.AiService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AIControllerV1 {
    private final AiService aiService;

    public AIControllerV1(AiService aiService) {
        this.aiService = aiService;
    }


    @PostMapping
    public ResponseEntity<String> getAiResponse(
            @RequestParam(defaultValue = "false") boolean humanize,
            @RequestBody RawAIRequest rawAIRequest
    ) {
        String result;
        try {
            if (humanize) {
                result = aiService.getNaturalized(
                        rawAIRequest.prompt()
                );
            } else {
                result = aiService.getJson(rawAIRequest.prompt());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<String> getAiResponse() {
        return ResponseEntity.ok(aiService.getSystemText());
    }
}