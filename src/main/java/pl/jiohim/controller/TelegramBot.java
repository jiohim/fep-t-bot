package pl.jiohim.controller;


import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@PropertySource("application.properties")
@Data
@RequiredArgsConstructor
@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {


    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
    private UpdateController updateController;
    private static final String BOT_TOKEN = "fep_bot_token";

    public TelegramBot (UpdateController updateController){
        this.updateController =updateController;
    }

    @PostConstruct
    public void init(){
        updateController.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        var originalMessagge =update.getMessage();
        log.debug(originalMessagge.getText());

        var response = new SendMessage();
        response.setChatId(originalMessagge.getChatId().toString());
        response.setText("Hello from bot");
        sendAnswerMessage(response);
    }

    @Override
    public String getBotUsername() {
        return "jiohimalerts_bot";
    }

    @Override
    public String getBotToken() {
        String token = System.getenv(BOT_TOKEN);
        return token;
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
