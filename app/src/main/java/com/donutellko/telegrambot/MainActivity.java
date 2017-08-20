package com.donutellko.telegrambot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		TelegramBot bot = TelegramBotAdapter.build("424429240:AAH_o-ElyO8Mzi1CCSvEinNZs_dDhqmRBn4");

		bot.setUpdatesListener(new UpdatesListener() {
			@Override
			public int process(List<Update> updates) {
				Log.i("UpdatesListener", "Processing updates.");

				for (Update upd : updates) {
					Log.i("Processing update", upd.toString());
				}

				return UpdatesListener.CONFIRMED_UPDATES_ALL;
			}
		});

    }


    /*
    public void Execute (TelegramBot bot, T request) {
		bot.execute(request, new Callback() {
			@Override
			public void onResponse(BaseRequest request, BaseResponse response) {

			}

			@Override
			public void onFailure(BaseRequest request, IOException e) {

			}
		});
	}
	*/
}
