package com.donutellko.telegrambot;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.util.Calendar;
import java.util.List;

import static com.donutellko.telegrambot.MainActivity.donutellkoBot;

public class MainActivity extends AppCompatActivity {
	static DonutellkoBot donutellkoBot = new DonutellkoBot();
	static WeatherGetter weatherGetter = new WeatherGetter();

	static TextView tv;
	TextView uv;
	View content;

	static StringBuilder log = new StringBuilder("Waiting for orders!\n");
	static String lastUpdated = "Updated: --";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sp = getApplicationContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
		donutellkoBot.updId = sp.getInt("Last processed update", 0);

		// WakeLock:
		Context context = getApplicationContext();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "lolo");
		wl.acquire();

		// Logs on screen:
		content = findViewById(R.id.content);
		tv = content.findViewById(R.id.tv);
		uv = content.findViewById(R.id.uv);
		Button button = content.findViewById(R.id.button);
		uv.setTextColor(Color.GREEN);
		tv.setText("Ready");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		// Update timer:
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

		spe.putInt("Last processed update", donutellkoBot.updId);
	}
}

class UpdatesGetter extends AsyncTask<Void, Void, Void> {
	@Override
	protected Void doInBackground(Void... voids) {
		GetUpdates getUpdates = new GetUpdates().limit(100).offset(donutellkoBot.updId + 1).timeout(100);
		try {
			GetUpdatesResponse updatesResponse = donutellkoBot.bot.execute(getUpdates);
			List<Update> updates = updatesResponse.updates();
			donutellkoBot.processUpdates(updates);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}


