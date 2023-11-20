package org.tatiSmol;

import org.tatiSmol.question.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {
    private HashMap<Long, UserData> users;
    private ArrayList<AbstractQuestion> questions;

    public Bot() {
        users = new HashMap<>();
        questions = new ArrayList<>();
        questions.add(new GitQuestion());
        questions.add(new JavaQuestion());
        questions.add(new SQLQuestion());
        questions.add(new HTTPQuestion());
    }

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
            sendText(userId, "Привет! Это тест на знание языка Java. Итак, начнём :)");
            users.put(userId, new UserData());
            String question = questions.get(0).getQuestion();
            sendText(userId, question);
        } else if (users.get(userId).getQuestionNumber() > questions.size() - 1) {
            sendText(userId,
                    "Спасибо за участие. Тест завершён." + "\n" +
                          "Правильные ответы: " + users.get(userId).getScore() + " из " + questions.size() +
                          ".\n" + "Чтобы пройти тест заново используйте команду /start.");
        } else {
            UserData userData = users.get(userId);
            int qNumb = userData.getQuestionNumber();
            boolean trueResult = questions.get(qNumb).checkAnswer(text);

            int score = userData.getScore();
            if (trueResult) {
                sendText(userId, "Верно! :)");
                userData.setScore(score + 1);
            } else {
                sendText(userId, "Неправильный ответ :(");
            }

            int nextQ = qNumb + 1;
            userData.setQuestionNumber(nextQ);

            if (nextQ == questions.size()) {
                sendText(userId,
                        "Правильные ответы: " + users.get(userId).getScore() + " из " + questions.size() + ".");
            } else {
                String question = questions.get(userData.getQuestionNumber()).getQuestion();
                sendText(userId, question);
            }
        }
    }
}
