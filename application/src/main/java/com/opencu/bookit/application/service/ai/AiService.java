package com.opencu.bookit.application.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencu.bookit.application.port.out.ai.LoadSqlPort;
import com.opencu.bookit.application.port.out.ai.SendAiRequestPort;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    private final LoadSqlPort loadSqlPort;
    private final SendAiRequestPort sendAiRequestPort;

    public AiService(LoadSqlPort loadSqlPort, SendAiRequestPort sendAiRequestPort) {
        this.loadSqlPort = loadSqlPort;
        this.sendAiRequestPort = sendAiRequestPort;
    }

    public String getJson(String prompt) throws JsonProcessingException {
        String sql = sendAiRequestPort.sendAiPrompt(prompt);
        return loadSqlPort.sendSqlPrompt(sql);
    }

    public String getNaturalized(String prompt) throws JsonProcessingException {
        return sendAiRequestPort.sendAiPrompt(getJson(prompt));
    }
}
