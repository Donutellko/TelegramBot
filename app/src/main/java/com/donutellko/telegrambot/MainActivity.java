package com.donutellko.telegrambot;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	TextView tv, uv;
	View content;
	TelegramBot bot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bot = TelegramBotAdapter.build("424429240:AAH_o-ElyO8Mzi1CCSvEinNZs_dDhqmRBn4");

		content = findViewById(R.id.content);
		tv = content.findViewById(R.id.tv);
		uv = content.findViewById(R.id.uv);
		uv.setTextColor(Color.GREEN);
		tv.setText("Ready");

		bot.setUpdatesListener(new UpdatesListener() {
			@Override
			public int process(List<Update> updates) {
				updateTime();
				String result = "";

				for (Update upd : updates) {
					Log.i("Processing update", upd.toString());
					result += "\n" + upd.updateId() + " from " + upd.message().from().firstName() + ": " + upd.message().text();

					String answer = getAnswer(upd);

					if (upd != null) {
						SendMessage request = new SendMessage(upd.message().chat().id(), answer)
								.parseMode(ParseMode.HTML)
								.disableWebPagePreview(true)
								.disableNotification(true)
								.replyToMessageId(1)
//								.replyMarkup(new ForceReply())
								;

						SendResponse sendResponse = bot.execute(request);
						boolean ok = sendResponse.isOk();
						Message message = sendResponse.message();
					}
				}

				updateText(result);

				return UpdatesListener.CONFIRMED_UPDATES_ALL;
			}
		});

		tv.setText("Waiting for orders!");
	}

	private String getAnswer(Update upd) {
		Message msg = upd.message();
		String name = upd.message().from().firstName();

		switch (msg.text()) {
			case "/start": return name.equals("Donat") ? "Привет, Повелитель!" : "Привет, " + name + "!";
			case "/echo": return "Я тут. Привет, " + name + ".";
			case "/today": return getToday();
			case "/week": return getWeek();
			case "/weather": return getWeather();
			case "/help": return "Сам разберись, если не тупой.";
			default: return "Не понял тебя...";
		}
	}

	public void updateText(String text) {
		if (text.length() > 0)
			tv.append("\n" + text);
	}

	public void updateTime() {
		uv.setText("Updated: " + Calendar.getInstance().getTime());
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
}
