package ru.saubulproject.telegramtinkoffbot.service;

import java.util.List;

import ru.saubulproject.telegramtinkoffbot.dto.TinkoffAccount;

public interface TinkoffService {

	boolean checkUserTinkoffTokenRegistration(Long chatId);

	void addUserTinkoffToken(String tinkoffToken, Long chatId);

	String getToken(Long chatId);
	
	String getPortfolioStatus(String tinkoffToken, String portfolio);

	boolean checkToken(String text);

	List<TinkoffAccount> getAccounts(String tinkoffToken);

	String findInstrumentByTicker(String tinkoffToken, String ticker);
	
	
}
