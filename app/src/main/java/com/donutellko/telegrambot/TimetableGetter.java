package com.donutellko.telegrambot;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by donat on 8/30/17.
 */

public class TimetableGetter {
	public TimetableGetter (UserBot user) {
		new TimetableAsync().execute(user);
	}
}

class TimetableAsync extends AsyncTask<UserBot, Void, Void> {

	@Override
	protected Void doInBackground(UserBot... bot) {
		try {
			String response = getDataFromUrl("http://ruz.spbstu.ru/faculty/95/groups/" + bot[0].groupId);
			bot[0].timetable = response;
			DonutellkoBot.sendMsg(bot[0].chatId, response);

			Log.i("Timetable", response);
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
			uc.setConnectTimeout(100);
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