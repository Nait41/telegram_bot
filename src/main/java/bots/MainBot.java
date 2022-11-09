package bots;

import data.MainData;
import jdk.jfr.internal.tool.Main;
import openFile.OpenTXT;
import org.apache.http.impl.execchain.MainClientExec;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;

public class MainBot extends TelegramLongPollingBot {
    public MainData mainData = new MainData();

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            Message message = update.getMessage();
            if(message.hasText()){
                if(message.getText().equals("/start") && !userPresent(message)){
                    sendHelloMessage(message);
                } else if ((message.getText().toLowerCase(Locale.ROOT).equals("да")
                        || message.getText().toLowerCase(Locale.ROOT).equals("нет")) && !userPresent(message))
                {
                    startQuest(message);
                } else if(message.getText().toCharArray()[0] > '0' && message.getText().toCharArray()[0] < '9' && userPresent(message)){
                    for (int i = 0; i < mainData.userInfo.size(); i++){
                        if(mainData.userInfo.get(i).get(0).equals(message.getChatId().toString())){
                            int maxValue = mainData.questList.get(mainData.userInfo.get(i).size() - 1).size();
                            int currentValue = message.getText().toCharArray()[0] - '0';
                            if(currentValue > 0 && currentValue < maxValue){
                                mainData.userInfo.get(i).add(message.getText());
                                if(mainData.userInfo.get(i).size() - 1 == mainData.questList.size()){
                                    try {
                                        execute(
                                                SendMessage.builder()
                                                        .chatId(message.getChatId().toString())
                                                        .text("Ваши ответы успешно записаны! \nБольшое спасибо за пройденный опрос!")
                                                        .build());
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    String quest = "";
                                    for(int p = 0; p < mainData.questList.get(mainData.userInfo.get(i).size() - 1).size(); p++){
                                        quest += mainData.questList.get(mainData.userInfo.get(i).size() - 1).get(p);
                                        quest += "\n";
                                    }
                                    try {
                                        execute(
                                                SendMessage.builder()
                                                        .chatId(message.getChatId().toString())
                                                        .text(quest)
                                                        .build());
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                try {
                                    sendError(message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    try {
                        sendError(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean userPresent(Message message){
        for(int i = 0; i < mainData.userInfo.size(); i ++){
            if(mainData.userInfo.get(i).get(0).equals(message.getChatId().toString())){
                return true;
            }
        }
        return false;
    }

    public void sendError(Message message) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Не могу понять вас, попробуйте ответить еще раз.")
                        .build());
    }

    public boolean startQuest(Message message){
        if(message.getText().toLowerCase(Locale.ROOT).equals("да")){
            mainData.userInfo.add(new ArrayList<>());
            mainData.userInfo.get(mainData.userInfo.size()-1).add(message.getChatId().toString());
            try {
                execute(
                        SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text("Тогда начнем! На каждый из вопросов вы должны ответить одним числом, другие ответы не принимаются. \n ")
                                .build());
                String quest = "";
                for(int i = 0; i < mainData.questList.get(0).size(); i++){
                    quest += mainData.questList.get(0).get(i);
                    quest += "\n";
                }
                execute(
                        SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text(quest)
                                .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            try {
                execute(
                        SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text("Ничего страшного, ты сможешь пройти опрос в другой раз.\n ")
                                .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void sendHelloMessage(Message message){
        try {
            execute(
                    SendMessage.builder()
                            .chatId(message.getChatId().toString())
                            .text("Привет! Готов пройти опрос по игре LostLight?")
                            .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
