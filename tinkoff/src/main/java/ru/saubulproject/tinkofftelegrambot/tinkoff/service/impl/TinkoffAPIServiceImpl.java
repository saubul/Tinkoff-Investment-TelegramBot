package ru.saubulproject.tinkofftelegrambot.tinkoff.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import ru.saubulproject.tinkofftelegrambot.tinkoff.dto.TinkoffAccount;
import ru.saubulproject.tinkofftelegrambot.tinkoff.service.TinkoffAPIService;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;
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
		
		List<Account> accounts = investApi.getUserService().getAccounts().get();
		for(Account account: accounts) {
			if(account.getId().equals(accountId)) {
				status.append(account.getName() + ":");
			}
		}
		Portfolio portfolio = investApi.getOperationsService().getPortfolio(accountId).get();
		List<Position> positions = portfolio.getPositions();
		InstrumentsService instrumentsService = investApi.getInstrumentsService();
		for(Position position: positions) {
			Instrument instrument = instrumentsService.getInstrumentByFigiSync(position.getFigi());
			status.append("\n    ��������:  " + instrument.getName());
			status.append("\n    �����:  " + instrument.getTicker());
			status.append("\n    ����������:  " + decimalFormat.format(position.getQuantity()));
			status.append("\n    ����: " + decimalFormat.format(position.getCurrentPrice().getValue()) + " " + position.getCurrentPrice().getCurrency().getSymbol());
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

	@Override
	public String getInstrumentByTicker(String tinkoffToken, String ticker) {
		InvestApi investApi = InvestApi.create(tinkoffToken);
		InstrumentsService instrumentsService = investApi.getInstrumentsService();

		StringBuilder instrument = new StringBuilder();
		
		Instrument instr;
		try {
			instr = instrumentsService.getInstrumentByTicker(ticker, "SPBXM").get();
			Quotation priceOfInstr = investApi.getMarketDataService().getLastPrices(List.of(instr.getFigi())).get().get(0).getPrice();
			instrument.append("��������: " + instr.getName());
			instrument.append("\n�����: " + instr.getTicker());
			instrument.append("\n���: " + instr.getInstrumentType().toUpperCase());
			instrument.append("\n�����: " + instr.getExchange());
			String nanoPrice =  String.valueOf(priceOfInstr.getNano());
			if(nanoPrice.length() == 8) {
				nanoPrice = "0" + nanoPrice;
			}
			nanoPrice = nanoPrice.replaceFirst("00+", "");
			if(nanoPrice.length() == 0) {
				nanoPrice = "00";
			} else if (nanoPrice.length() == 1) {
				nanoPrice = nanoPrice + "0";
			}
			instrument.append("\n����: " + priceOfInstr.getUnits() + "." + nanoPrice + " " + instr.getCurrency().toUpperCase());
		} catch (Exception e) {
			instrument.append("���������� �� ��������� ������ �� ������.");
		} 
		return instrument.toString();
	}
	
	
}
