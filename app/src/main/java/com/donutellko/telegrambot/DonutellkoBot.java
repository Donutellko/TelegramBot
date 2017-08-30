package com.donutellko.telegrambot;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;

/**
 * Created by donat on 8/30/17.
 */

public class DonutellkoBot {

	public static String myName = "DonutellkoBot";


	public static void sendMsg(Long chatId, String text) {
//		SendMessage request = new SendMessage(upd.channelPost().chat().id(), answer)
		SendMessage request = new SendMessage(chatId, text)
				.parseMode(ParseMode.HTML)
				.disableWebPagePreview(true)
				.disableNotification(true)
//				.replyToMessageId(1)
//				.replyMarkup(new ForceReply())
				;

		MainActivity.bot.execute(request, new Callback<SendMessage, SendResponse>() {
			@Override
			public void onResponse(SendMessage request, SendResponse response) {
				MainActivity.updateLog("      ✓");
			}

			@Override
			public void onFailure(SendMessage request, IOException e) {
				MainActivity.updateLog("      x");
			}
		});

	}
}
