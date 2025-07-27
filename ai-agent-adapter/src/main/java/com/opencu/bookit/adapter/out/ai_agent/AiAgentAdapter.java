package com.opencu.bookit.adapter.out.ai_agent;

import com.opencu.bookit.adapter.out.ai_agent.dto.request.AIRequest;
import com.opencu.bookit.adapter.out.ai_agent.dto.request.AIRequestBuilder;
import com.opencu.bookit.adapter.out.ai_agent.dto.response.AIResponse;
import com.opencu.bookit.application.port.out.ai.SendAiRequestPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AiAgentAdapter implements SendAiRequestPort {
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${yandex-gpt.api-url}")
    private String url;

    @Value("${yandex-gpt.authorization}")
    private String authorization;

    @Value("${yandex-gpt.model-uri}")
    private String modelUri;

    @Value("${yandex-gpt.system-text}")
    private String systemText;

    /**
     * this system prompt is necessary for analyzing results of 
     * a response from DB (second request to the LLM if required)
     */
    @Value("${yandex-gpt.system-text-analyze}")
    private String systemTextAnalyze;

    /**
     * @param prompt is a prompt from user in natural language 
     * @return String which is a sql request for DB. If HttpStatus is not OK
     * or response is null, return null
     */
    @Override
    public String sendAiPrompt(String prompt) {
        ResponseEntity<AIResponse> response = getResponseEntity(prompt, systemText);

        if (response.getStatusCode() == HttpStatus.OK) {
            String SQLRequest = response.getBody() != null ? response.getBody().result().alternatives().getFirst().message().text() : null;
            if (SQLRequest != null) {
                SQLRequest = SQLRequest.replace("\\n", "\n");
                SQLRequest = SQLRequest.replace("\\", "");
                SQLRequest = SQLRequest.replace("```", "");
                
                return SQLRequest;
            }
        }
        return null;
    }

    /**
     * @param jsonString is a response object or array from DB serialized
     * by JSON format.
     * @return response in natural language (in a nutshell, LLM translates JSON to
     * a sentence in natural language). If jsonString is null or HttpStatus is not OK, null is returned.
     */
    @Override
    public String sendPromptForHumanizing(String jsonString) {
        ResponseEntity<AIResponse> response = getResponseEntity(jsonString, systemText);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody() != null ? response.getBody().result().alternatives().getFirst().message().text() : null;
        }
        return "";
    }

    private ResponseEntity<AIResponse> getResponseEntity(
            String prompt,
            String systemPrompt
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);

        AIRequest aiRequest = AIRequestBuilder.createAIRequest(
                systemPrompt,
                prompt,
                modelUri
        );

        HttpEntity<AIRequest> aiRequestHttpEntity = new HttpEntity<>(aiRequest, headers);
        return restTemplate.exchange(url, HttpMethod.POST, aiRequestHttpEntity, AIResponse.class);
    }
}
