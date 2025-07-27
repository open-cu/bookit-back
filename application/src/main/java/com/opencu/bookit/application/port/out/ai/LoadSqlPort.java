package com.opencu.bookit.application.port.out.ai;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface LoadSqlPort {
    /**
     * @param sql is a sql request for DB
     * @return json response in string format
     */
    String sendSqlPrompt(String sql) throws JsonProcessingException;
}
