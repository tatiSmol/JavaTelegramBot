package org.tatiSmol;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    public int qNumb;

    @Override
    public String getBotUsername() {
        return "some user name";
    }

    @Override
    public String getBotToken() {
        return "some token";
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder().chatId(who.toString()).text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        long userId = message.getFrom().getId();

        if (text.equals("/start")) {
            sendText(userId, "Hello! This is a Java skills test. So, let's begin :)");
            qNumb = 1;
            String question = getQuestion(qNumb);
            sendText(userId, question);
        } else if (qNumb > 4) {
            sendText(userId, "You have answered all the questions.");
        } else {
            boolean trueResult = checkAnswer(qNumb, text);
            sendText(userId, trueResult ? "Всё верно!" : "Ответ неверный :(");
            qNumb++;
            String question = getQuestion(qNumb);
            sendText(userId, question);
        }



    }

    public String getQuestion(int number) {
        return switch (number) {
            case 1 -> "Вопрос 1. Сколько в языке программирования Java есть примитивов?";
            case 2 -> "Вопрос 2. Сколько в реляционных (SQL) базах данных существует типов связей между таблицами?";
            case 3 -> "Вопрос 3. С помощью какой команды в системе контроля версий Git " +
                                "можно просмотреть авторов различных срок в одном файле?";
            case 4 -> "Вопрос 4. Какие методы HTTP-запросов вы знаете?";
            default -> "";
        };
    }

    public boolean checkAnswer(int number, String answer) {
        answer = answer.toLowerCase();
        return switch (number) {
            case 1 -> answer.equals("8");
            case 2 -> answer.equals("3");
            case 3 -> answer.contains("blame");
            default -> answer.contains("get") && answer.contains("post") &&
                       answer.contains("put") && answer.contains("patch") && answer.contains("delete");
        };
    }
}
