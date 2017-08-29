package com.donutellko.telegrambot;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static com.donutellko.telegrambot.MainActivity.ids;
import static com.donutellko.telegrambot.MainActivity.userBots;

public class MainActivity extends AppCompatActivity {

	static TextView tv;
	TextView uv;
	View content;
	static TelegramBot bot;
	static Map<Long, UserBot> userBots = new HashMap<>();
	static List<Long> ids = new LinkedList<>();
	static int updId = 0;

	static StringBuilder log = new StringBuilder("Waiting for orders!\n");
	static String lastUpdated = "Updated: --";

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
				(new Broadcaster()).execute();
			}
		});


		CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, 150) {
			@Override
			public void onTick(long l) {
				new UpdatesGetter().execute();

				tv.setText(log);
				uv.setText(lastUpdated);
			}

			@Override
			public void onFinish() {
				start();
			}
		}.start();

//		bot.setUpdatesListener(new UpdatesListener() {
//			@Override
//			public int process(List<Update> updates) {
//				return processUpdates(updates);
//			}
//		});
	}

	public static int processUpdates(List<Update> updates) {
		updateTime();

		for (Update upd : updates) {
			if (upd.updateId() <= updId)
				continue;

			Message msg = upd.message();
			Long id = msg.chat().id();

			updId = upd.updateId();
			UserBot cur;

			if (! userBots.containsKey(id)) {
				cur = new UserBot(upd);
				userBots.put(id, cur);
				ids.add(id);
			} else
				cur = userBots.get(id);

			//updateLog("\n" + cur.name);
			cur.process(upd);
		}
		return updId;
	}


	public static void updateLog(String text) {
		if (text.length() > 0) {
			log.append(text);
		}
	}

	public static void updateTime() {
		lastUpdated = ("Updated: " + Calendar.getInstance().getTime());
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences sp = getApplicationContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();

		spe.putInt("Last processed update", updId);
	}

	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences sp = getApplicationContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
//		SharedPreferences.Editor spe = sp.edit();

		updId = sp.getInt("Last processed update", 0);
	}
}

class UpdatesGetter extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... voids) {
		GetUpdates getUpdates = new GetUpdates().limit(100).offset(MainActivity.updId + 1).timeout(100);
		GetUpdatesResponse updatesResponse = MainActivity.bot.execute(getUpdates);
		List<Update> updates = updatesResponse.updates();
		MainActivity.processUpdates(updates);
		return null;
	}
}

class Broadcaster extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... voids) {
		for (Long id : ids) {
			userBots.get(id).sendMsg("Куку, юзер! Это тест хезе чего сейчас проходит.");
		}
		return null;
	}
}
