package com.donutellko.telegrambot;

import android.util.Log;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by donat on 8/22/17.
 */

public class UserBot {

	Long chatId;
	String name;
	boolean isPrivate;
	StringBuilder log = new StringBuilder();
	private String myName = "DonutellkoBot";


	public UserBot(Update upd) {
		Chat chat = upd.getMessage().getChat();
		this.chatId = chat.getId();
//		this.isPrivate = chat.type().equals(Chat.Type.Private);
		this.isPrivate = chat.isUserChat();
		this.name = isPrivate ?
				"@" + chat.getUserName() + " " + chat.getFirstName() + " " + chat.getLastName() :
				chat.getTitle();

		log.append("Started chat " + (isPrivate ? "with " : "in ") + name);
	}

	private String getAnswer(Update upd) {
		Message msg = upd.getMessage();
		String name = upd.getMessage().getFrom().getFirstName();

		if (msg.getEntities() != null &&
				msg.getEntities().get(0).getType().contains("bot_command")) { // если является командой
			String text = msg.getText();

			String commandName = text.replace(myName, "");

//			int ind = text.indexOf("@" + myName);
//			if (ind >= 0)
//				commandName = text.substring(0, ind);
//				// + text.substring(ind + myName.length() + 1);

			Log.i("command:", commandName);

			switch (commandName) {
				case "/start":
					return name.equals("Donat") ? "Привет, Повелитель!" : "Привет, " + name + "!";
				case "/echo":
					return "Я тут. Привет, " + name + ".";
				case "/today":
					return getToday();
				case "/week":
					return getWeek();
				case "/weather":
					return getWeather();
				case "/help":
					return "Сам разберись, если не тупой.";
				default:
					return "Не понял тебя...";
			}
		} else
			return "Даже не знаю, что сказать...";
	}

	public static String getToday() {
		return "Нет инфы. Зайди потом.";
	}

	public static String getWeek() {
		return "Нет инфы. Зайди потом.";
	}

	public static String getWeather() {
		return "С вероятностью в 70% сегодня пойдёт дождь.";
	}

	public void process(Update upd) {
		String forLog = "\n" + upd.getMessage().getDate().toString() + upd.getUpdateId() + " " + upd.getMessage().getFrom().getFirstName() + ": " + upd.getMessage().getText();
		String answer = getAnswer(upd);
		forLog += ("\n" + "Bot: " + answer);
		MainActivity.updateLog(forLog);
		log.append(forLog);

		if (upd != null) {
			sendMsg(answer);
		}
	}

	public void sendMsg (String message_text) {
		SendMessage message = new SendMessage() // Create a message object object
				.setChatId(chatId)
				.setText(message_text);
		try {
			DonutellkoBot.donutellkoBot.sendMessage(message); // Sending our message object to user
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
