package ru.saubulproject.tinkofftelegrambot.tinkoff.service;

import java.util.List;

import ru.saubulproject.tinkofftelegrambot.tinkoff.dto.TinkoffAccount;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.Quotation;

public interface TinkoffAPIService {
	
	String getPortfolioStatus(String tinkoffToken, String portfolio);

	TinkoffAccount[] getAccounts(String tinkoffToken);

	String getInstrumentByTicker(String tinkoffToken, String ticker);
	
	String makeLimitOrder(String tinkoffToken, String ticker, String quantity, String price, String direction, String accountId);
	
	String cancelOrder(String tinkoffToken, String accountId, String orderId);
}
 