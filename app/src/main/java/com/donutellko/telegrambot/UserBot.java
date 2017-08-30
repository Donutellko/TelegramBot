package com.donutellko.telegrambot;

import android.util.Log;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;

import static com.donutellko.telegrambot.MainActivity.bot;

/**
 * Created by donat on 8/22/17.
 */

public class UserBot {

	Long chatId;
	String name;
	boolean isPrivate;
	StringBuilder log = new StringBuilder();
	int groupId = 24103;
	public String timetable = "";
	Question question = null;
	TimetableGetter timetableGetter = null;

	public UserBot(Update upd) {
		Chat chat = upd.message().chat();
		this.chatId = chat.id();
		this.isPrivate = chat.type().equals(Chat.Type.Private);
		this.name = isPrivate ?
				chat.firstName() + " " + chat.lastName() : chat.title();

		log.append("Started chat " + (isPrivate ? "with " : "in ") + name);
	}

	public void process(Update upd) {
		String forLog = upd.message().from().firstName() + ": " + upd.message().text();
		String answer = getAnswer(upd);
		forLog += ("\n" + "Bot: " + answer);
		MainActivity.updateLog("\n" + forLog);
		log.append(forLog);

		if (upd != null) {
			DonutellkoBot.sendMsg(chatId, answer);
		}
	}

	private String getAnswer(Update upd) {
		Message msg = upd.message();
		String name = upd.message().from().firstName();

		/* if (msg.entities() != null &&
				msg.entities()[0].type().equals(MessageEntity.Type.bot_command)) {// если является командой */
			String text = msg.text();

			String commandName = text.replace("@" + DonutellkoBot.myName, "");


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
				case "/stop":
					question = null;
					return "Ок.";
				default:
					return questionAnswer(commandName);
			}
		/*} else
			return "Даже не знаю, что сказать...";*/
	}

	public String getToday() {
		if (timetable.length() > 0) {
			return timetable;
		} else if (groupId > 0) {
			if (timetableGetter == null)
				timetableGetter = new TimetableGetter(this);
			return "Инфы для группы с id=" + groupId + " ещё нет...";// "Попробуй спросить /today ещё раз...";
		} else {
			question = Question.GROUP_NUMBER;
			return "Назови id своей группы на сайте расписаний: ruz.spbstu.ru";
		}
	}

	public String getWeek() {
		return "Нет инфы. Зайди потом.";
	}

	public static String getWeather() {
		if (MainActivity.weatherGetter.currentInfoString.length() > 0)
			return MainActivity.weatherGetter.currentInfoString;
		else
			return "С вероятностью в 99.(9)% сегодня пойдёт дождь.";
	}

	public String questionAnswer (String answer) {

		if (question.equals(Question.GROUP_NUMBER)) {
			try {
				groupId = Integer.parseInt(answer);
			} catch (Exception e) {
				return "Неверный формат. Пришли мне одно число или \'/stop\' чтоб отменить.";
			}
			return getToday();
		} else
			return "Не понял тебя...";
	}

	enum Question { GROUP_NUMBER }
}
