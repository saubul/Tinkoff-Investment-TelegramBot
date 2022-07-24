package ru.saubulproject.tinkofftelegrambot.tinkoff.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import ru.saubulproject.tinkofftelegrambot.tinkoff.dto.TinkoffAccount;
import ru.saubulproject.tinkofftelegrambot.tinkoff.service.TinkoffAPIService;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.OrdersService;
import ru.tinkoff.piapi.core.UsersService;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Position;

@Service
public class TinkoffAPIServiceImpl implements TinkoffAPIService{

	private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

	@Override
	@SneakyThrows
	public String getPortfolioStatus(String tinkoffToken, String accountId) {
		StringBuilder status = new StringBuilder();
		StringBuilder positionsString = new StringBuilder();
		InvestApi investApi = getInvestApi(tinkoffToken);
		
		UsersService usersService = investApi.getUserService();
		List<Account> accounts = usersService.getAccounts().get();
		
		OrdersService ordersService = investApi.getOrdersService();
		
		for(Account account: accounts) {
			if(account.getId().equals(accountId)) {
				status.append("<b>" + account.getName() + "</b>:");
			}
		}
		Portfolio portfolio = investApi.getOperationsService().getPortfolio(accountId).get();
		List<Position> positions = portfolio.getPositions();
		InstrumentsService instrumentsService = investApi.getInstrumentsService();
		
		//BigDecimal price = BigDecimal.ZERO;
		
		for(Position position: positions) {
			Instrument instrument = instrumentsService.getInstrumentByFigiSync(position.getFigi());
			positionsString.append("\n    Название:  " + instrument.getName());
			positionsString.append("\n    Тикер:  " + instrument.getTicker());
			positionsString.append("\n    Количество:  " + decimalFormat.format(position.getQuantity()));
			positionsString.append("\n    Цена: " + decimalFormat.format(position.getCurrentPrice().getValue()) + " " + position.getCurrentPrice().getCurrency().getSymbol());
			positionsString.append("\n	  ------------------------");
			
//			if(position.getCurrentPrice().getCurrency().getCurrencyCode().equals("USD")) {
//				price = price.add(position.getCurrentPrice().getValue().multiply(position.getQuantity().abs()).multiply(getInstrumentPriceByFigi(tinkoffToken, "BBG0013HGFT4")));
//			} else {
//				price = price.add(position.getCurrentPrice().getValue().multiply(position.getQuantity().abs()));
//			}
		}
		//status.append("\nСуммарная стоимость портфеля: " + decimalFormat.format(price) + " \u20BD");
		//status.append("\n" + ordersService.);
		status.append(positionsString.toString());
		status.append("\n");
		return status.toString();
	}
	
	

	@Override
	@SneakyThrows
	public TinkoffAccount[] getAccounts(String tinkoffToken) {
		InvestApi investApi = getInvestApi(tinkoffToken);
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
		InvestApi investApi = getInvestApi(tinkoffToken);
		InstrumentsService instrumentsService = investApi.getInstrumentsService();

		StringBuilder instrument = new StringBuilder();
		
		Instrument instr;
		try {
			instr = instrumentsService.getInstrumentByTicker(ticker, "SPBXM").get();
			Quotation priceOfInstr = investApi.getMarketDataService().getLastPrices(List.of(instr.getFigi())).get().get(0).getPrice();
			instrument.append("Название: " + instr.getName());
			instrument.append("\nТикер: " + instr.getTicker());
			instrument.append("\nТип: " + instr.getInstrumentType().toUpperCase());
			instrument.append("\nБиржа: " + instr.getExchange());
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
			instrument.append("\nЦена: " + priceOfInstr.getUnits() + "." + nanoPrice + " " + instr.getCurrency().toUpperCase());
		} catch (Exception e) {
			instrument.append("Инструмент по заданному тикеру не найден.");
		} 
		return instrument.toString();
	}
	
	private BigDecimal getInstrumentPriceByFigi(String tinkoffToken, String figi) {
		InvestApi investApi = InvestApi.create(tinkoffToken);
		InstrumentsService instrumentsService = investApi.getInstrumentsService();
		Instrument instr;
		StringBuilder price = new StringBuilder();
		try {
			instr = instrumentsService.getInstrumentByFigi(figi).get();
			Quotation priceOfInstr = investApi.getMarketDataService().getLastPrices(List.of(instr.getFigi())).get().get(0).getPrice();

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
			price.append(priceOfInstr.getUnits() + "." + nanoPrice);
		} catch (Exception e) {
		} 
		return BigDecimal.valueOf(Double.valueOf(price.toString()));
	}
	
	
	@Cacheable
	private InvestApi getInvestApi(String tinkoffToken) {
		return InvestApi.create(tinkoffToken);
	}



	@Override
	@SneakyThrows
	public String makeLimitOrder(String tinkoffToken, 
								 String ticker, 
								 String quantity, 
								 String price,
								 String orderDirection,
								 String accountId) {
		InvestApi investApi = getInvestApi(tinkoffToken);
		OrdersService ordersService = investApi.getOrdersService();
		Quotation qPrice = Quotation.newBuilder()
										.setUnits(Long.valueOf(price.split("\\.")[0]))
										.setNano(Integer.valueOf(price.split("\\.")[1]))
									.build();
		OrderDirection oDir = OrderDirection.ORDER_DIRECTION_UNSPECIFIED;
		if(orderDirection.equalsIgnoreCase("BUY")) {
			oDir = OrderDirection.ORDER_DIRECTION_BUY;
		} else if(orderDirection.equalsIgnoreCase("SELL")) {
			oDir = OrderDirection.ORDER_DIRECTION_SELL;
		}
		PostOrderResponse response = ordersService.postOrder(investApi.getInstrumentsService().getInstrumentByTickerSync(ticker, "SPBXM").getFigi(), 
															 Long.valueOf(quantity), 
															 qPrice, 
															 oDir, 
															 accountId, 
															 OrderType.ORDER_TYPE_LIMIT, 
															 UUID.randomUUID().toString()).get();
		return response.getOrderId();
	}
	
}
