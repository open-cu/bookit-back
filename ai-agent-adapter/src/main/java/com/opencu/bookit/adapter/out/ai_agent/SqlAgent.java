package com.opencu.bookit.adapter.out.ai_agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencu.bookit.adapter.out.ai_agent.dto.response.SqlResponse;
import com.opencu.bookit.application.port.out.ai.LoadSqlPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SqlAgent implements LoadSqlPort {
    private final JdbcTemplate jdbcTemplate;

    /**
     * @param sql is a sql request for DB 
     * @return
     */
    @Override
    public String sendSqlPrompt(String sql) throws JsonProcessingException {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        SqlResponse sqlResponseDTO = new SqlResponse(results);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(sqlResponseDTO);
    }
}
