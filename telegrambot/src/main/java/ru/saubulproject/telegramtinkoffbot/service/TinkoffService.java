package ru.saubulproject.telegramtinkoffbot.service;

import java.util.List;

import ru.saubulproject.telegramtinkoffbot.dto.TinkoffAccount;
import ru.saubulproject.telegramtinkoffbot.entity.ChatTokenEntity;

public interface TinkoffService {

	boolean checkUserTinkoffTokenRegistration(Long chatId);

	ChatTokenEntity addUserTinkoffToken(String tinkoffToken, Long chatId);

	String getToken(Long chatId);
	
	String getPortfolioStatus(String tinkoffToken, String portfolio);

	boolean checkToken(String text);

	List<TinkoffAccount> getAccounts(String tinkoffToken);

	String findInstrumentByTicker(String tinkoffToken, String ticker);

	void deleteUserTinkoffToken(Long chatId);

	void addAccountsToUser(ChatTokenEntity user);

	String postOrder(String tinkoffToken, String ticker, String quantity, String price, String orderDirection, String accountId);
	
	
}
