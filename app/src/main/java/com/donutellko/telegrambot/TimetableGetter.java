package com.donutellko.telegrambot;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import static com.donutellko.telegrambot.MainActivity.donutellkoBot;

public class TimetableGetter { // При успешном получении расписания передаёт userBot'у и посылает пользователю
	public static void getTimetable (UserBot user) {
//		new TimetableAsync().execute(user);
		new TimetableAsync().doInBackground(user); // WTF?????
	}

	public static String icalToText(String ical) {
		int tmp;
		char newline = ical.charAt(15);

		String[] vevents = ical.split("BEGIN:VEVENT"); // разделение на блоки (нулевой -- заголовок)
		Subject[] subjects = new Subject[vevents.length - 1]; // кроме нулевого

		tmp = vevents[0].indexOf("Расписание");

		String groupN = vevents[0].substring(tmp, vevents[0].indexOf(newline, tmp)); // Получение номера группы

		for (int i = 0; i < vevents.length - 1; i++) {
			subjects[i] = new Subject(vevents[i + 1]);
		}

		String[] months = {"???", "января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
		String dtMIN = ""; // Исп. чтобы определять что дни разные
		String result = groupN;
		for (Subject subj : subjects) {
			String d = subj.DTSTART.substring(0, 7);
			if (d.compareTo(dtMIN) > 0) {
				dtMIN = d;
				result += "\n   " + subj.start.day + " " + months[subj.start.month] + ", " + subj.start.weekday;
			}
			result += "\n" + subj.getTimeRange() + " " + subj.SUMMARY + " (" + subj.LOCATION + ") ";
		}

		return result;
	}

	static class Subject {
		String DTSTART, DTEND, SUMMARY, LOCATION;
		Time start, end;

		public Subject (String vevent) {
			int tmp;
			tmp = vevent.indexOf("SUMMARY:") + ("SUMMARY:").length();
			SUMMARY  = vevent.substring(tmp, vevent.indexOf('\n', tmp));
			tmp = vevent.indexOf("LOCATION:") + ("LOCATION:").length();
			LOCATION = vevent.substring(tmp, vevent.indexOf('\n', tmp));
			tmp = vevent.indexOf("DTSTART:") + ("DTSTART:").length();
			DTSTART  = vevent.substring(tmp, vevent.indexOf('\n', tmp));
			tmp = vevent.indexOf("DTEND:") + ("DTEND:").length();
			DTEND    = vevent.substring(tmp, vevent.indexOf('\n', tmp));

			start = new Time(DTSTART);
			end = new Time(DTEND);
		}

		public String getTimeRange() {
			return start.getHourMinute() + "-" + end.getHourMinute();
		}

		class Time {
			int year, month, day, hour, min;
			String weekday;

			public Time (String s) {
				String[] weekdays = {"Понедельник", "Вторник", "Среда", "Четверг", " Пятница", "Суббота", "Воскресенье", };

				year   = Integer.parseInt(s.substring(0, 4));
				month  = Integer.parseInt(s.substring(4, 6));
				day    = Integer.parseInt(s.substring(6, 8));
				hour   = Integer.parseInt(s.substring(9, 11)) + 2;
				min    = Integer.parseInt(s.substring(11, 13));

				Calendar c = Calendar.getInstance();
				c.set(year, month, day + 3); // Я НЕ ЗНАЮ, ПОЧЕМУ +3. НО БЕЗ ЭТОГО ДАЁТ НЕВЕРНЫЕ ДНИ НЕДЕЛИ....
				int d = c.get(Calendar.DAY_OF_WEEK);
				Log.i("dayofweek", d + " " + weekdays[d]);
				weekday = weekdays[d];

//				Log.i("Год день месяц", year + " " + day + " " + month + " " + s);
			}

			public String getHourMinute() {
				return (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min;
			}
		}
	}
}

class TimetableAsync extends AsyncTask<UserBot, Void, Void> {

	@Override
	protected Void doInBackground(UserBot... userBots) {
		try {
			String response = getDataFromUrl("http://ruz.spbstu.ru/faculty/95/groups/" + userBots[0].groupId + "/ical");
			String result = response;
			try {
				result = TimetableGetter.icalToText(response);
				userBots[0].timetable = result;
			} catch (Exception e) {
				Log.i("ОШИБКА РАСПИСАНИЯ!", response);
				e.printStackTrace();
				result = "Ошибка при формировании результата. Перешли это сообщение мне (@Donutellko)." + "\n\n" + e.toString() + e.getStackTrace()[0].toString() + "\n\n" + response;
			}

			donutellkoBot.sendMsg(userBots[0].chatId, result);
		} catch (Exception e) {
			Log.i("Timetable", "FAIL!!!!");
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