package com.opencu.bookit.application.port.out.ai;

public interface SendAiRequestPort {
    /**
     * @param prompt is a prompt from user in natural language
     * @return String which is a sql request for DB
     */
    String sendAiPrompt(String prompt);

    /**
     * @param jsonString is a response object or array from DB serialized
     * by JSON format.
     * @return response in natural language (in a nutshell, LLM translates JSON to
     * a sentence in natural language)
     */
    String sendPromptForHumanizing(String jsonString);

    String getSystemText();
}
