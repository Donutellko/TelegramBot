package com.donutellko.telegrambot;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by donat on 8/30/17.
 */

public class DonutellkoBot {
	static String botKey = "424429240:AAHZEgOBd_j4LFOZlQvk3gfdqudjUBHye2U";
	static String weatherUrl = "http://api.openweathermap.org/data/2.5/forecast?id=519690&appid=484811a1b7ad9193b884eb1396f726d1&units=metric&lang=ru";

	public static String myName = "DonutellkoBot";
	static int updId = 0;
	TelegramBot bot;
	static Map<Long, UserBot> userBots = new HashMap<>();
	static List<Long> ids = new LinkedList<>();

	public DonutellkoBot () {
		bot = TelegramBotAdapter.build(botKey);
	}

	public void sendMsg(Long chatId, String text) {
		SendMessage request = new SendMessage(chatId, text)
				.parseMode(ParseMode.HTML)
				.disableWebPagePreview(true)
				.disableNotification(true)
//				.replyToMessageId(1)
//				.replyMarkup(new ForceReply())
				;

		bot.execute(request, new Callback<SendMessage, SendResponse>() {
			@Override
			public void onResponse(SendMessage request, SendResponse response) {MainActivity.updateLog("      ✓"); }
			@Override
			public void onFailure(SendMessage request, IOException e) {
				MainActivity.updateLog("      x");
			}
		});

	}

	public static int processUpdates(List<Update> updates) {
		MainActivity.updateTime();

		for (Update upd : updates) {
			if (upd.updateId() <= updId)
				continue;
			Message msg = upd.message();
			if (msg == null)
				continue;

			Long id = msg.chat().id();

			updId = upd.updateId();
			UserBot cur;

			if (! userBots.containsKey(id)) {
				cur = new UserBot(upd);
				userBots.put(id, cur);
				ids.add(id);
			} else
				cur = userBots.get(id);

			cur.process(upd);
		}
		return updId;
	}

	void broadcast () {
		for (Long id : ids) {
			sendMsg(id, "Куку, юзер! Это короч тест сейчас проходит.");
		}
	}

	void reset() {
		userBots = new HashMap<Long, UserBot>();
		ids = new LinkedList<Long>();
		updId = 0;
	}
}
