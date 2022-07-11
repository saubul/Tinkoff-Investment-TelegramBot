package ru.saubulproject.telegramtinkoffbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import lombok.SneakyThrows;
import ru.saubulproject.telegramtinkoffbot.bot.TelegramTinkoffBot;

@SpringBootApplication
public class TelegramBotApplication {
	
	@SneakyThrows
	public TelegramBotApplication(TelegramTinkoffBot bot) {
		TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
		api.registerBot(bot);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}
	
}
