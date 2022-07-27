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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.saubulproject.telegramtinkoffbot.dto.TinkoffAccount;
import ru.saubulproject.telegramtinkoffbot.entity.ChatTokenEntity;
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
				case "COMMAND": {
					switch(param) {
						case "PORTFOLIOS": {
							List<TinkoffAccount> accounts = tinkoffService.getAccounts(tinkoffToken);
							execute(SendMessage.builder()
												   .chatId(message.getChatId())
												   .text("Выберите портфель: ")
												   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getPortfolioNameButtons(message, accounts)).build())
											   .build());
							break;
						}
						case "FIND": {
							execute(SendMessage.builder()
									   .chatId(message.getChatId())
									   .text("Введите тикер: ")
									   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
								   .build());
							break;
						}
					}
					break;
				}
				case "PORTFOLIO": {
					switch(param) {
						default: {
							execute(SendMessage.builder()
									   .chatId(message.getChatId())
									   .text(tinkoffService.getPortfolioStatus(tinkoffToken, param))
									   .parseMode("HTML")
									   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
								   .build());		
							break;
						}
					}
					break;
				}
				case "INSTRUMENT": {
					switch(param) {
						case "LIMIT": {
							execute(SendMessage.builder()
												   .chatId(message.getChatId())
												   .text("Введите данные в виде: LIMIT: Тикер/Количество/Цена/Действие(BUY/SELL)/Название счета")
											   .build());
							break;
						}
						default: {
							break;
						}
					
					}
					break;
				}
			}
		} else {
			registerUserTinkoffToken(message);
		}
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
//						case "/start":{
//							execute(SendMessage.builder()
//									   .chatId(message.getChatId())
//									   .text("Здравствуйте! Этот телеграм бот предназначен для работы с Тинькофф Инвестициями. ")
//									   .replyMarkup(ReplyKeyboardMarkup.builder()
//											   							   .keyboard(getConstantMenuButtons(message))
//											   						   .build())
//								   .build());
//							break;
//						}
						case "/menu": {
							execute(SendMessage.builder()
												   .chatId(message.getChatId())
												   .text("Меню: ")
												   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
											   .build());
							break;
						}
					}
				}
			} else if (message.hasText()) {
				String text = message.getText();
				switch(text) {
					case "Портфели": {
						List<TinkoffAccount> accounts = tinkoffService.getAccounts(tinkoffToken);
						execute(SendMessage.builder()
											   .chatId(message.getChatId())
											   .text("Выберите портфель: ")
											   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getPortfolioNameButtons(message, accounts)).build())
										   .build());
						break;
					}
					case "Найти инструмент по тикеру": {
						execute(SendMessage.builder()
								   .chatId(message.getChatId())
								   .text("Введите тикер: ")
								   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
							   .build());
						break;
					}
					case "Поменять Tinkoff API Token": {
						tinkoffService.deleteUserTinkoffToken(message.getChatId());
						execute(SendMessage.builder()
											   .chatId(message.getChatId())
											   .text("Введите новый Tinkoff API Token:")
										   .build());
						break;
					}
					case "Отменить заявку" : {
						execute(SendMessage.builder()
								   .chatId(message.getChatId())
								   .text("Введите данные в виде: ORDER: Название счёта/ID заявки")
							   .build());
						break;
					}
					default: {
						if(text.startsWith("LIMIT:")) {
							String[] request = text.substring(6).trim().split("/");
							String ticker = request[0];
							String quantity = request[1];
							String price = request[2];
							String orderDirection = request[3];
							String accountId = tinkoffService.getAccounts(tinkoffToken).stream().filter(account -> account.getName().equalsIgnoreCase(request[4])).findFirst().get().getId();
							String response = tinkoffService.postLimitOrder(tinkoffToken, ticker, quantity, price, orderDirection, accountId);
							execute(SendMessage.builder()
												   .chatId(message.getChatId())
												   .text("ID заявки: " + response)
												   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
											   .build());

						} else if(text.startsWith("ORDER:")) {
							String[] request = text.substring(6).trim().split("/");
							String accountId = tinkoffService.getAccounts(tinkoffToken).stream().filter(account -> account.getName().equalsIgnoreCase(request[0])).findFirst().get().getId();
							String orderId = request[1];
							execute(SendMessage.builder()
									   .chatId(message.getChatId())
									   .text(tinkoffService.cancelOrder(tinkoffToken, accountId, orderId))
									   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getMenuButtons(message)).build())
								   .build());
						}
						else {
							execute(SendMessage.builder()
									   .chatId(message.getChatId())
									   .text(tinkoffService.findInstrumentByTicker(tinkoffToken, text))
									   .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(getInstrumentButtons(message)).build())
								   .build());
						}
						break;
					}
				}

			}
		} else {
			registerUserTinkoffToken(message);
		}
	}

	
	// кнопки возможностей взаимодействия с портфелем
	private List<InlineKeyboardButton> getInstrumentButtons(Message message) {
		List<InlineKeyboardButton> instrumentButtons= getMenuButtons(message);
		instrumentButtons.add(InlineKeyboardButton.builder()
															.text("Выставить лимитную заявку")
															.callbackData("INSTRUMENT:LIMIT")
														.build());
		return instrumentButtons;
	}

	
	// постоянные кнопки меню
	private List<KeyboardRow> getConstantMenuButtons(Message message) {
		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow keyboardRow1 = new KeyboardRow();
		keyboardRow1.add(KeyboardButton.builder().text("Портфели").build());
		
		KeyboardRow keyboardRow2 = new KeyboardRow();
		keyboardRow2.add(KeyboardButton.builder().text("Найти инструмент по тикеру").build());
		
		KeyboardRow keyboardRow3 = new KeyboardRow();
		keyboardRow3.add(KeyboardButton.builder().text("Поменять Tinkoff API Token").build());
		
		KeyboardRow keyboardRow4 = new KeyboardRow();
		keyboardRow4.add(KeyboardButton.builder().text("Отменить заявку").build());
		
		keyboard.add(keyboardRow1);
		keyboard.add(keyboardRow2);
		keyboard.add(keyboardRow3);
		keyboard.add(keyboardRow4);
		return keyboard;
	}


	// Кнопки выбора портфелей
	private List<InlineKeyboardButton> getPortfolioNameButtons(Message message, List<TinkoffAccount> accounts) {
		List<InlineKeyboardButton> portfolioButtons = new ArrayList<>();
		for(TinkoffAccount account: accounts) {
			portfolioButtons.add(InlineKeyboardButton.builder()
													 	.text(account.getName())
													 	.callbackData("PORTFOLIO:" + account.getId())
													 .build());
		}
		return portfolioButtons;
	}
	
	// Кнопки меню
	private List<InlineKeyboardButton> getMenuButtons(Message message) {
		List<InlineKeyboardButton> buttons = new ArrayList<>();
		buttons.add(InlineKeyboardButton.builder()
											.text("Список портфелей")
											.callbackData("COMMAND:PORTFOLIOS")
										.build());
		buttons.add(InlineKeyboardButton.builder()
											.text("Найти инструмент по тикеру")
											.callbackData("COMMAND:FIND")
										.build());
		return buttons;
	}

	// регистрация токена
	@SneakyThrows
	private void registerUserTinkoffToken(Message message) {
		if(!tinkoffService.checkToken(message.getText())) {
			execute(SendMessage.builder()
					   .chatId(message.getChatId())
					   .text("Здравствуйте! Прежде чем начать пользоваться ботом, пожалуйста, введите токен для работы с Tinkoff Invest API: ")
				   .build());
		} else {
			ChatTokenEntity user = tinkoffService.addUserTinkoffToken(message.getText(), message.getChatId());
			tinkoffService.addAccountsToUser(user);
			execute(SendMessage.builder()
					   .chatId(message.getChatId())
					   .text("Токен Tinkoff Invest API сохранен.")
					   .replyMarkup(ReplyKeyboardMarkup.builder()
							   								.keyboard(getConstantMenuButtons(message))
							   							.build())
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
