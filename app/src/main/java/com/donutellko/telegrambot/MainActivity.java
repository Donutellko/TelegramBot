package com.donutellko.telegrambot;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.donutellko.telegrambot.MainActivity.ids;
import static com.donutellko.telegrambot.MainActivity.userBots;

public class MainActivity extends AppCompatActivity {

	static TextView tv;
	TextView uv;
	View content;
	static Map<Long, UserBot> userBots = new HashMap<>();
	static List<Long> ids = new LinkedList<>();
	static int updId = 0;

	static StringBuilder log = new StringBuilder("Waiting for orders!\n");
	static String lastUpdated = "Updated: --";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ApiContextInitializer.init();

		// Instantiate Telegram Bots API
		TelegramBotsApi botsApi = new TelegramBotsApi();

		// Register our bot
		try {
			botsApi.registerBot(new DonutellkoBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}


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


		CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, 200) {
			@Override
			public void onTick(long l) {
				//new UpdatesGetter().execute();

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

	/*public static int processUpdates(List<Update> updates) {
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

			cur.process(upd);
			updateLog("\n" + cur.name + " (" +  id + ") ");
		}
		return updId;
	}*/


	public static void updateLog(String text) {
		if (text.length() > 0) {
			log.append("\n" + text);
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

//class UpdatesGetter extends AsyncTask<Void, Void, Void> {
//	@Override
//	protected Void doInBackground(Void... voids) {
//		GetUpdates getUpdates = new GetUpdates().limit(100).offset(MainActivity.updId + 1).timeout(100);
//		GetUpdatesResponse updatesResponse = MainActivity.bot.execute(getUpdates);
//		List<Update> updates = updatesResponse.updates();
//		MainActivity.processUpdates(updates);
//		return null;
//	}
//}

class Broadcaster extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... voids) {
		for (Long id : ids) {
			userBots.get(id).sendMsg("Куку, юзер! Это тест хезе чего сейчас проходит.");
		}
		return null;
	}
}
