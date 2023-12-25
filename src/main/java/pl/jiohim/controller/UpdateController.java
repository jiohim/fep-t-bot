package pl.jiohim.controller;


import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import pl.jiohim.service.UpdateProducer;
import pl.jiohim.util.MessageUtil;

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;
    private final MessageUtil messageUtil;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtil messageUtil, UpdateProducer updateProducer) {
        this.messageUtil = messageUtil;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }
        if (update.getMessage() != null) {
            distributeMesageByType(update);
        } else {
            log.error("Received unsupported type " + update);
        }
    }

    private void distributeMesageByType(Update update) {
        var message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else {
            setUnsupportedMessageBYTypeView(update);
        }

    }

    private void setUnsupportedMessageBYTypeView(Update update) {
        var sendMessage = messageUtil.generateSendMessageWithText(update, "Unsupported message");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce("text",update);
        setFileIsReceivedView(update);

    }

    private void processDocMessage(Update update) {
        updateProducer.produce("doc",update);
        setFileIsReceivedView(update);

    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce("photo",update);
        setFileIsReceivedView(update);

    }


    private void setFileIsReceivedView(Update update) {

    }
}
