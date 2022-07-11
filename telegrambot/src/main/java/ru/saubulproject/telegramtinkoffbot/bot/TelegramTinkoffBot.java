package ru.saubulproject.telegramtinkoffbot.bot;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.saubulproject.telegramtinkoffbot.service.TinkoffService;

@Component
@RequiredArgsConstructor
public class TelegramTinkoffBot extends TelegramLongPollingBot{

	private String botToken = System.getenv("tinkoffBotToken");
	private String botUsername = "TinkoffTelegramBot";
	
	private final TinkoffService tinkoffService;
	
	@SneakyThrows
	public void onUpdateReceived(Update update) {
		if(update.hasMessage()) {
			handleMessage(update.getMessage());
		} else if (update.hasCallbackQuery()) {
			handleCallback(update.getCallbackQuery());
		}

	}

	@SneakyThrows
	private void handleCallback(CallbackQuery callbackQuery) {
		Message message = callbackQuery.getMessage();
		if(tinkoffService.checkUserTinkoffTokenRegistration(message.getChatId())) {
			
		} else {
			registerUserTinkoffToken(message);
		}
	}

	@SneakyThrows
	private void handleMessage(Message message) {
		if(message.hasText()) {
			if(tinkoffService.checkUserTinkoffTokenRegistration(message.getChatId())) {
				if(message.hasEntities()) {
					Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equalsIgnoreCase(e.getText())).findFirst();
					if(commandEntity.isPresent()) {
						String command = commandEntity.get().getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
						switch(command) {
							case "/start":
								registerUserTinkoffToken(message);
								break;
							case "/status":
								break;
						}
					}
				}
			} else {
					registerUserTinkoffToken(message);
			}
		}
	}

	@SneakyThrows
	private void registerUserTinkoffToken(Message message) {
		if(message.getText().length() == 88) {
			tinkoffService.addUserTinkoffToken(message.getText(), message.getChatId());
		} else {
			execute(SendMessage.builder()
								   .chatId(message.getChatId())
								   .text("Please enter your Tinkoff Invest API token:")
							   .build());
		}
	}

	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

}
