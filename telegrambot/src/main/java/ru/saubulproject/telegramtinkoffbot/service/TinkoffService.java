package ru.saubulproject.telegramtinkoffbot.service;

public interface TinkoffService {

	boolean checkUserTinkoffTokenRegistration(Long chatId);

	void addUserTinkoffToken(String tinkoff_token, Long chat_id);
	
	
}
