package ru.tbank.bookit.book_it_backend.payload.response;

/**
 * Класс для ответа с сообщением
 */
public class MessageResponse {
    private String message;

    /**
     * Конструктор для создания ответа с сообщением
     */
    public MessageResponse(String message) {
        this.message = message;
    }

    // Геттеры и сеттеры

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}