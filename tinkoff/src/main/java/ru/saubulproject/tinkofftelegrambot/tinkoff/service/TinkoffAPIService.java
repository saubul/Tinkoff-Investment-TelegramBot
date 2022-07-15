package ru.saubulproject.tinkofftelegrambot.tinkoff.service;

import java.util.List;

import ru.saubulproject.tinkofftelegrambot.tinkoff.dto.TinkoffAccount;

public interface TinkoffAPIService {
	
	String getPortfolioStatus(String tinkoffToken, String portfolio);

	TinkoffAccount[] getAccounts(String tinkoffToken);

	String getInstrumentByTicker(String tinkoffToken, String ticker);
	
}
