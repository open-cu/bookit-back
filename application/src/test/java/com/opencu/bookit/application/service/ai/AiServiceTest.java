package com.opencu.bookit.application.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencu.bookit.application.port.out.ai.LoadSqlPort;
import com.opencu.bookit.application.port.out.ai.SendAiRequestPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AiService}.
 */
@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private LoadSqlPort loadSqlPort;

    @Mock
    private SendAiRequestPort sendAiRequestPort;

    @InjectMocks
    private AiService aiService;

    @Test
    void getJson_ShouldReturnJson_WhenPromptIsValid() throws JsonProcessingException {
        // given
        String prompt = "get all users";
        String sql = "SELECT * FROM users;";
        String expectedJson = "{\"users\": []}";

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenReturn(expectedJson);

        // when
        String result = aiService.getJson(prompt);

        // then
        assertThat(result).isEqualTo(expectedJson);
        verify(sendAiRequestPort).sendAiPrompt(prompt);
        verify(loadSqlPort).sendSqlPrompt(sql);
        verifyNoMoreInteractions(sendAiRequestPort, loadSqlPort);
    }

    @Test
    void getNaturalized_ShouldReturnHumanReadableString_WhenPromptIsValid() throws JsonProcessingException {
        // given
        String prompt = "get all users";
        String sql = "SELECT * FROM users;";
        String json = "{\"users\": []}";
        String expectedNaturalized = "There are no users in the database.";

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenReturn(json);
        when(sendAiRequestPort.sendPromptForHumanizing(json)).thenReturn(expectedNaturalized);

        // when
        String result = aiService.getNaturalized(prompt);

        // then
        assertThat(result).isEqualTo(expectedNaturalized);
        verify(sendAiRequestPort).sendAiPrompt(prompt);
        verify(loadSqlPort).sendSqlPrompt(sql);
        verify(sendAiRequestPort).sendPromptForHumanizing(json);
        verifyNoMoreInteractions(sendAiRequestPort, loadSqlPort);
    }

    @Test
    void getJson_ShouldPropagateJsonProcessingException_FromLoadSqlPort() throws JsonProcessingException {
        // given
        String prompt = "invalid query";
        String sql = "INVALID SQL";

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenThrow(new JsonProcessingException("parse error") {});

        // then
        org.junit.jupiter.api.Assertions.assertThrows(
                JsonProcessingException.class,
                () -> aiService.getJson(prompt)
        );

        verify(sendAiRequestPort).sendAiPrompt(prompt);
        verify(loadSqlPort).sendSqlPrompt(sql);
    }
}