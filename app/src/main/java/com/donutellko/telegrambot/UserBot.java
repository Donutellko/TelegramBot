package com.donutellko.telegrambot;

import android.util.Log;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

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
		Chat chat = upd.message().chat();
		this.chatId = chat.id();
		this.isPrivate = chat.type().equals(Chat.Type.Private);
		this.name = isPrivate ?
				"@" + chat.username() + " " + chat.firstName() + " " + chat.lastName() :
				chat.title();

		log.append("Started chat " + (isPrivate ? "with " : "in ") + name);
	}

	private String getAnswer(Update upd) {
		Message msg = upd.message();
		String name = upd.message().from().firstName();

		if (msg.entities() != null &&
				msg.entities()[0].type().equals(MessageEntity.Type.bot_command)) {// если является командой
			String text = msg.text();

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
		String forLog = "\n" + upd.message().date().toString() + upd.updateId() + " " + upd.message().from().firstName() + ": " + upd.message().text();
		String answer = getAnswer(upd);
		forLog += ("\n" + "Bot: " + answer);
		MainActivity.updateLog(forLog);
		log.append(forLog);

		if (upd != null) {
			sendMsg(answer);
		}
	}

	public void sendMsg (String text) {
//		SendMessage request = new SendMessage(upd.channelPost().chat().id(), answer)
		SendMessage request = new SendMessage(chatId, text)
				.parseMode(ParseMode.HTML)
				.disableWebPagePreview(true)
				.disableNotification(true)
				.replyToMessageId(1)
//					.replyMarkup(new ForceReply())
				;

		SendResponse sendResponse = MainActivity.bot.execute(request);
		boolean ok = sendResponse.isOk();
		Message message = sendResponse.message();
	}

}
