package com.opencu.bookit.application.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencu.bookit.application.port.out.ai.LoadSqlPort;
import com.opencu.bookit.application.port.out.ai.SendAiRequestPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private LoadSqlPort loadSqlPort;

    @Mock
    private SendAiRequestPort sendAiRequestPort;

    @InjectMocks
    private AiService aiService;

    @Test
    @DisplayName("getJson returns DB json and calls ports in order: AI -> SQL")
    void getJson_happyPath_callsInOrder_andReturnsJson() throws Exception {
        String prompt = "Find all users";
        String sql = "SELECT * FROM users";
        String expectedJson = "{\"data\":[1,2,3]}";

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenReturn(expectedJson);

        String actual = aiService.getJson(prompt);

        assertEquals(expectedJson, actual);

        InOrder inOrder = inOrder(sendAiRequestPort, loadSqlPort);
        inOrder.verify(sendAiRequestPort).sendAiPrompt(prompt);
        inOrder.verify(loadSqlPort).sendSqlPrompt(sql);
        verifyNoMoreInteractions(sendAiRequestPort, loadSqlPort);
    }

    @Test
    @DisplayName("getJson propagates JsonProcessingException from LoadSqlPort")
    void getJson_exceptionFromLoadSql_isPropagated() throws Exception {
        String prompt = "Find all users";
        String sql = "SELECT * FROM users";
        JsonProcessingException jpe = new JsonProcessingException("broken json") {};

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenThrow(jpe);

        JsonProcessingException thrown = assertThrows(JsonProcessingException.class, () -> aiService.getJson(prompt));
        assertSame(jpe, thrown);

        InOrder inOrder = inOrder(sendAiRequestPort, loadSqlPort);
        inOrder.verify(sendAiRequestPort).sendAiPrompt(prompt);
        inOrder.verify(loadSqlPort).sendSqlPrompt(sql);
        verifyNoMoreInteractions(sendAiRequestPort, loadSqlPort);
    }

    @Test
    @DisplayName("getNaturalized returns humanized string and uses JSON from DB as input")
    void getNaturalized_happyPath_returnsHumanized() throws Exception {
        String prompt = "Count orders";
        String sql = "SELECT COUNT(*) FROM orders";
        String json = "{\"count\":42}";
        String expectedNatural = "Всего заказов: 42";

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenReturn(json);
        when(sendAiRequestPort.sendPromptForHumanizing(json)).thenReturn(expectedNatural);

        String naturalized = aiService.getNaturalized(prompt);

        assertEquals(expectedNatural, naturalized);

        InOrder inOrder = inOrder(sendAiRequestPort, loadSqlPort, sendAiRequestPort);
        inOrder.verify(sendAiRequestPort).sendAiPrompt(prompt);
        inOrder.verify(loadSqlPort).sendSqlPrompt(sql);
        inOrder.verify(sendAiRequestPort).sendPromptForHumanizing(json);
        verifyNoMoreInteractions(sendAiRequestPort, loadSqlPort);
    }

    @Test
    @DisplayName("getNaturalized propagates JsonProcessingException and does not call humanizer")
    void getNaturalized_exceptionFromLoadSql_propagated_andSkipsHumanize() throws Exception {
        String prompt = "Count orders";
        String sql = "SELECT COUNT(*) FROM orders";
        JsonProcessingException jpe = new JsonProcessingException("db json error") {};

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenThrow(jpe);

        JsonProcessingException thrown = assertThrows(JsonProcessingException.class, () -> aiService.getNaturalized(prompt));
        assertSame(jpe, thrown);

        verify(sendAiRequestPort).sendAiPrompt(prompt);
        verify(loadSqlPort).sendSqlPrompt(sql);
        verify(sendAiRequestPort, never()).sendPromptForHumanizing(anyString());
        verifyNoMoreInteractions(sendAiRequestPort, loadSqlPort);
    }

    @Test
    @DisplayName("getJson passes through SQL from AI port to LoadSqlPort (including empty string)")
    void getJson_passesThroughSql_evenIfEmpty() throws Exception {
        String prompt = "Whatever";
        String sql = "";
        String expectedJson = "[]";

        when(sendAiRequestPort.sendAiPrompt(prompt)).thenReturn(sql);
        when(loadSqlPort.sendSqlPrompt(sql)).thenReturn(expectedJson);

        String actual = aiService.getJson(prompt);

        assertEquals(expectedJson, actual);
        verify(sendAiRequestPort).sendAiPrompt(prompt);
        verify(loadSqlPort).sendSqlPrompt(sql);
        verifyNoMoreInteractions(sendAiRequestPort, loadSqlPort);
    }
}

