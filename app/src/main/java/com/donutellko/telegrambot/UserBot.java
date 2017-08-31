package com.donutellko.telegrambot;

import android.util.Log;
import android.util.StringBuilderPrinter;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import static com.donutellko.telegrambot.MainActivity.donutellkoBot;

/**
 * Created by donat on 8/22/17.
 */

public class UserBot {
	Long chatId;
	String name;
	boolean isPrivate;
	int groupId = 0;
	public String timetable = "";
	Question question = null;
	StringBuilder dialog = new StringBuilder();

	public UserBot(Update upd) {
		Chat chat = upd.message().chat();
		this.chatId = chat.id();
		this.isPrivate = chat.type().equals(Chat.Type.Private);
		this.name = isPrivate ?
				chat.firstName() + " " + chat.lastName() : chat.title();

		dialog.append("Started chat " + (isPrivate ? "with " : "in ") + name);
	}

	public void process(Update upd) {
		String forLog = upd.message().from().firstName() + ": " + upd.message().text();
		String answer = getAnswer(upd);
		forLog += ("\n" + "Bot: " + answer);
		MainActivity.updateLog("\n" + forLog);
		dialog.append(forLog);

		if (upd != null) {
			donutellkoBot.sendMsg(chatId, answer);
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
		return "Нет инфы. Зайди потом.";
	}

	public static String getWeather() {
		if (MainActivity.weatherGetter.currentInfoString.length() > 0)
			return MainActivity.weatherGetter.currentInfoString;
		else
			return "С вероятностью в 99.(9)% сегодня пойдёт дождь. А к сервису погоды чего-то не подключиться... Смыло штоле?";
	}

	public String getWeek() {
		if (timetable.length() > 0) {
			return timetable;
		} else if (groupId > 0) {
			TimetableGetter.getTimetable(this);
			return timetable.length() > 0 ? "" : "Инфы для группы с id=" + groupId + " нет или сайт недоступен...";// "Попробуй спросить /today ещё раз...";
		} else {
			question = Question.GROUP_NUMBER;
			return "Назови id своей группы на сайте расписаний: ruz.spbstu.ru";
		}
	}

	public String questionAnswer (String answer) {
		if (question != null && question.equals(Question.GROUP_NUMBER)) {
			try {
				groupId = Integer.parseInt(answer);
			} catch (Exception e) {
				return "Неверный формат. Пришли мне одно число или \'/stop\' чтоб отменить.";
			}
			return getWeek();
		} else
			return "Не понял тебя...";
	}

	enum Question { GROUP_NUMBER }
}
