package com.opencu.bookit.adapter.out.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(value = "tg-bot.enabled")
public class MyTelegramBot extends TelegramLongPollingBot {
    private final String photoUrl;
    private final String webSite;
    private final String username;

    public MyTelegramBot(@Value("${tg-bot.token}") String token,
                         @Value("${tg-bot.photo-url}") String photoUrl,
                         @Value("${tg-bot.website}") String webSite,
                         @Value("${tg-bot.username}") String username) {
        super(token);
        this.photoUrl = photoUrl;
        this.webSite = webSite;
        this.username = username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();

            if (messageText.equals("/start")) {
                String welcomeText = String.format(
                        "Привет, %s! 👋\n\n" +
                                "Этот бот поможет тебе легко и быстро забронировать переговорку или место в коворкинге %s\n\n" +
                                "Просто нажми кнопку ниже, чтобы начать! 🚀",
                        firstName,
                        "🏢💺📅"
                );

                SendPhoto photo = new SendPhoto();
                photo.setChatId(String.valueOf(chatId));
                photo.setPhoto(new InputFile(photoUrl));
                photo.setCaption(welcomeText);

                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow row = new KeyboardRow();

                KeyboardButton webAppButton = new KeyboardButton();
                webAppButton.setText("Открыть Mini App 🚪");
                webAppButton.setWebApp(new WebAppInfo(webSite));

                row.add(webAppButton);
                keyboard.add(row);
                keyboardMarkup.setKeyboard(keyboard);
                keyboardMarkup.setResizeKeyboard(true);

                photo.setReplyMarkup(keyboardMarkup);

                try {
                    execute(photo);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            } else {
                // TO DO: remove this part
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText("Your chatId is " + chatId  + " and your id " + update.getMessage().getFrom().getId());
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * @param chatId stands for tgId of a user in string format. You should check if the user present in DB
     *               before sending a message.
     * @param messageText stands for a message you want to send to a user.
     */
    public void sendMessage(String chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    public void clearWebhook() {

    }
}
