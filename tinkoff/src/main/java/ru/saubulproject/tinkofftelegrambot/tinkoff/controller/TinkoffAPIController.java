package ru.saubulproject.tinkofftelegrambot.tinkoff.controller;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.saubulproject.tinkofftelegrambot.tinkoff.dto.TinkoffAccount;
import ru.saubulproject.tinkofftelegrambot.tinkoff.service.TinkoffAPIService;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.core.InvestApi;

@RestController
@RequestMapping("/tinkoff/api/v1")
@RequiredArgsConstructor
public class TinkoffAPIController {
	
	private final TinkoffAPIService tinkoffAPIService;
	
	@GetMapping("/status")
	@SneakyThrows
	public String getStatus(@RequestParam("tinkoffToken") String tinkoffToken, @RequestParam("portfolio") String portfolio) {
		return tinkoffAPIService.getPortfolioStatus(tinkoffToken, portfolio);
	}
	
	@GetMapping("/accounts")
	public HttpEntity<TinkoffAccount[]> getAccountNames(@RequestParam("tinkoffToken") String tinkoffToken) {
		return new ResponseEntity<>(tinkoffAPIService.getAccounts(tinkoffToken), HttpStatus.OK);
	}
	
	@GetMapping("/findByTicker")
	public String getInstrumentByTicker(@RequestParam("tinkoffToken") String tinkoffToken, @RequestParam("ticker") String ticker) {
		return tinkoffAPIService.getInstrumentByTicker(tinkoffToken, ticker);
	}
}
