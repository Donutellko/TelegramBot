package com.donutellko.telegrambot;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class WeatherGetter {
	static ResponseObject currentInfo = null;
	static String currentInfoString = "";

	WeatherGetter () {
		CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, 30 * 60 * 1000) { // обновление раз в 30 минут
			@Override
			public void onTick(long l) {
				MainActivity.log.append("\nWeather updated!");
				new WeatherJsonAsync().execute();
			}

			@Override
			public void onFinish() {
				start();
			}
		}.start();
	}

	public String getToday() {
		return getToday(currentInfo);
	}

	public static String getToday (ResponseObject currentInfo) {
		String result = "";
		for (int i = 0; i < 6; i++) {
			if (i >= currentInfo.list.length)
				break;
			ResponseObject.WeatherObject cur = currentInfo.list[i];
			result += "\n " + cur.getTime() + "     " + Math.round(cur.main.temp) + "°C    " + cur.main.humidity + "%    " + cur.weather[0].description;
		}
		if (result.length() > 0)
			result = "Расклад на сегодня такой:\nВремя   Темп.   Влажн." + result;
		return result;
	}
}

class ResponseObject {
	WeatherObject[] list;

	class WeatherObject {
		String dt_txt;
		Main main;
		Weather[] weather;
		Clouds clouds;

		public String getTime() {
			return dt_txt.substring(11, 16);
		}

		public Date getDate() {
			int year = Integer.parseInt(dt_txt.substring(0, 3));
			int month = Integer.parseInt(dt_txt.substring(5, 6));
			int day = Integer.parseInt(dt_txt.substring(8, 9));
			int hour = Integer.parseInt(dt_txt.substring(11, 12));
			return new Date(year, month, day, hour, 0);
		}

		class Main { double temp, pressure, humidity; }
		class Weather { String description; }
		class Clouds { String all; }
	}
}

class WeatherJsonAsync extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... voids) {
		try {
			String response = getDataFromUrl(DonutellkoBot.weatherUrl);
			ResponseObject weathers = new Gson().fromJson(response, ResponseObject.class);
			WeatherGetter.currentInfo = weathers;
			WeatherGetter.currentInfoString = WeatherGetter.getToday(weathers);
			Log.i("Weather", WeatherGetter.currentInfoString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getDataFromUrl(String url_s) throws Exception {
		String result = null;

		BufferedReader reader = null;
		URLConnection uc = null;

		try {
			URL url = new URL(url_s);
			uc = url.openConnection();
			uc.setConnectTimeout(1000);
			uc.connect();
			reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			StringBuilder buffer = new StringBuilder();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			result = buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
		return result;
	}
}