package com.donutellko.telegrambot;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

	static TextView tv;
	TextView uv;
	View content;
	static TelegramBot bot;
	Map<Long, UserBot> userBots = new HashMap<>();
	List<Long> ids = new LinkedList<>();

	static StringBuilder log = new StringBuilder("Waiting for orders!\n");
	String lastUpdated = "Updated: --";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bot = TelegramBotAdapter.build("424429240:AAHZEgOBd_j4LFOZlQvk3gfdqudjUBHye2U");

		content = findViewById(R.id.content);
		tv = content.findViewById(R.id.tv);
		uv = content.findViewById(R.id.uv);
		Button button = content.findViewById(R.id.button);
		uv.setTextColor(Color.GREEN);
		tv.setText("Ready");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for (Long id : ids) {
					userBots.get(id).sendMsg("Куку, юзер! Это тест хезе чего сейчас проходит.");
				}
			}
		});


		CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, 500) {
			@Override
			public void onTick(long l) {
				tv.setText(log);
				uv.setText(lastUpdated);
			}

			@Override
			public void onFinish() {
				start();
			}
		}.start();

		bot.setUpdatesListener(new UpdatesListener() {
			@Override
			public int process(List<Update> updates) {
				updateTime();

				for (Update upd : updates) {
					Message msg = upd.message();
					Long id = msg.chat().id();
					UserBot cur;

					if (! userBots.containsKey(id)) {
						cur = new UserBot(upd);
						userBots.put(id, cur);
						ids.add(id);
					} else
						cur = userBots.get(id);

					cur.process(upd);
				}
				return UpdatesListener.CONFIRMED_UPDATES_ALL;
			}
		});
	}


	public static void updateLog(String text) {
		if (text.length() > 0) {
			log.append("\n" + text);
			tv.setText(log);
		}
	}

	public void updateTime() {
		lastUpdated = ("Updated: " + Calendar.getInstance().getTime());
	}

}