package ru.saubulproject.telegramtinkoffbot.bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.saubulproject.telegramtinkoffbot.dto.TinkoffAccount;
import ru.saubulproject.telegramtinkoffbot.service.TinkoffService;

@Component
@RequiredArgsConstructor
public class TelegramTinkoffBot extends TelegramLongPollingBot{

	private String botToken = System.getenv("tinkoffBotToken");
	private String botUsername = "TinkoffTelegramBot";
	
	private final TinkoffService tinkoffService;
	
	// Изменение в боте
	@SneakyThrows
	public void onUpdateReceived(Update update) {
		if(update.hasMessage()) {
			handleMessage(update.getMessage());
		} else if (update.hasCallbackQuery()) {
			handleCallback(update.getCallbackQuery());
		}

	}

	// Обработка Callback
	@SneakyThrows
	private void handleCallback(CallbackQuery callbackQuery) {
		Message message = callbackQuery.getMessage();
		if(tinkoffService.checkUserTinkoffTokenRegistration(message.getChatId())) {
			String tinkoffToken = tinkoffService.getToken(message.getChatId());
			String[] callback = callbackQuery.getData().split(":");
			String action = callback[0];
			String param = callback[1];
			
			switch(action) {
				case "COMMAND":
					switch(param) {
						case "PORTFOLIOS": {
							List<TinkoffAccount> accounts = tinkoffService.getAccounts(tinkoffToken);
							execute(SendMessage.builder()
												   .chatId(message.getChatId())
												   .text("Выберите портфель: ")
												   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getPortfolioButtons(message, accounts)).build())
											   .build());
							break;
						}
					}
				case "PORTFOLIO":
					execute(SendMessage.builder()
										   .chatId(message.getChatId())
										   .text(tinkoffService.getPortfolioStatus(tinkoffToken, param))
										   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
									   .build());		
					break;
			}
		} else {
			registerUserTinkoffToken(message);
		}
	}

	private List<InlineKeyboardButton> getPortfolioButtons(Message message, List<TinkoffAccount> accounts) {
		List<InlineKeyboardButton> portfolioButtons = new ArrayList<>();
		for(TinkoffAccount account: accounts) {
			System.out.println(account.getId());
			portfolioButtons.add(InlineKeyboardButton.builder()
													 	.text(account.getName())
													 	.callbackData("PORTFOLIO:" + account.getId())
													 .build());
		}
		return portfolioButtons;
	}

	// Обработка Message
	@SneakyThrows
	private void handleMessage(Message message) {
		if(tinkoffService.checkUserTinkoffTokenRegistration(message.getChatId())) {
			String tinkoffToken = tinkoffService.getToken(message.getChatId());
			if(message.hasText() && message.hasEntities()) {
				Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
				if(commandEntity.isPresent()) {
					String command = commandEntity.get().getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
					switch(command) {
						case "/menu":
							execute(SendMessage.builder()
												   .chatId(message.getChatId())
												   .text("Меню: ")
												   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
											   .build());
							break;
					}
				}
			}
		} else {
			registerUserTinkoffToken(message);
		}
	}

	@SneakyThrows
	private void registerUserTinkoffToken(Message message) {
		if(!tinkoffService.checkToken(message.getText())) {
			execute(SendMessage.builder()
					   .chatId(message.getChatId())
					   .text("Прежде чем начать пользоваться ботом, пожалуйста, введите токен для работы с Tinkoff Invest API: ")
				   .build());
		} else {
			tinkoffService.addUserTinkoffToken(message.getText(), message.getChatId());
			execute(SendMessage.builder()
					   .chatId(message.getChatId())
					   .text("Токен Tinkoff Invest API сохранен.")
					   .replyMarkup(InlineKeyboardMarkup.builder()
							   								.keyboardRow(getMenuButtons(message))
							   							.build())
				   .build());
		}
	}

	private List<InlineKeyboardButton> getMenuButtons(Message message) {
		List<InlineKeyboardButton> buttons = new ArrayList<>();
		buttons.add(InlineKeyboardButton.builder()
											.text("Список портфелей")
											.callbackData("COMMAND:PORTFOLIOS")
										.build());
		return buttons;
	}

	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

}
