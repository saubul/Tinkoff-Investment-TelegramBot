package ru.saubulproject.tinkofftelegrambot.tinkoff.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import ru.saubulproject.tinkofftelegrambot.tinkoff.dto.TinkoffAccount;
import ru.saubulproject.tinkofftelegrambot.tinkoff.service.TinkoffAPIService;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Position;

@Service
public class TinkoffAPIServiceImpl implements TinkoffAPIService{

	private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");
	
	@Override
	@SneakyThrows
	public String getPortfolioStatus(String tinkoffToken, String accountId) {
		StringBuilder status = new StringBuilder();
		
		InvestApi investApi = InvestApi.create(tinkoffToken);
		InstrumentsService instrumentsService = investApi.getInstrumentsService();
		Portfolio portfolio = investApi.getOperationsService().getPortfolio(accountId).get();
		List<Position> positions = portfolio.getPositions();
		for(Position position: positions) {
			Instrument instrument = instrumentsService.getInstrumentByFigiSync(position.getFigi());
			status.append("\n    Название:  " + instrument.getName());
			status.append("\n    Тикер:  " + instrument.getTicker());
			status.append("\n    Количество:  " + decimalFormat.format(position.getQuantity()));
			status.append("\n    Цена: " + decimalFormat.format(position.getCurrentPrice().getValue()) + " " + position.getCurrentPrice().getCurrency().getSymbol());
			status.append("\n------------------------");
		}
		status.append("\n");
		
		return status.toString();
	}

	@Override
	@SneakyThrows
	public TinkoffAccount[] getAccounts(String tinkoffToken) {
		InvestApi investApi = InvestApi.create(tinkoffToken);
		List<Account> accounts = investApi.getUserService().getAccounts().get();
		TinkoffAccount[] tinkoffAccounts = new TinkoffAccount[accounts.size()];
		int i = 0;
		for(Account account : accounts) {
			tinkoffAccounts[i] = new TinkoffAccount(account.getName(), account.getId());
			i++;
		}
		return tinkoffAccounts;
	}
	
	
}
