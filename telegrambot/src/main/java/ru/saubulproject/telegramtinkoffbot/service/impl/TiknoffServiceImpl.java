package ru.saubulproject.telegramtinkoffbot.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import ru.saubulproject.telegramtinkoffbot.config.TinkoffConfig;
import ru.saubulproject.telegramtinkoffbot.dto.TinkoffAccount;
import ru.saubulproject.telegramtinkoffbot.encoder.Encoder;
import ru.saubulproject.telegramtinkoffbot.entity.AccountEntity;
import ru.saubulproject.telegramtinkoffbot.entity.ChatTokenEntity;
import ru.saubulproject.telegramtinkoffbot.repository.AccountRepository;
import ru.saubulproject.telegramtinkoffbot.repository.ChatTokenRepository;
import ru.saubulproject.telegramtinkoffbot.service.TinkoffService;

@Service
@RequiredArgsConstructor
public class TiknoffServiceImpl implements TinkoffService{
	
	private final RestTemplate restTemplate;
	private final ChatTokenRepository chatTokenRepo;
	private final Encoder encoder;
	private final TinkoffConfig tinkoffConfig;
	private final AccountRepository accountRepo;
	
	@Override
	public boolean checkUserTinkoffTokenRegistration(Long chatId) {
		return chatTokenRepo.findByChatId(chatId) == null? false : true;
	}

	@Override
	public ChatTokenEntity addUserTinkoffToken(String tinkoffToken, Long chatId) {
		return chatTokenRepo.save(new ChatTokenEntity(chatId, encoder.encode(tinkoffToken)));
	}

	@Override
	public String getToken(Long chatId) {
		return encoder.decode(chatTokenRepo.findByChatId(chatId).getTinkoffToken());
	}

	@Override
	public String getPortfolioStatus(String tinkoffToken, String portfolio) {
		return restTemplate.getForObject(tinkoffConfig.getUrl() + "/status?tinkoffToken={tinkoffToken}&portfolio={portfolio}", String.class, tinkoffToken, portfolio);
	}

	@Override
	public boolean checkToken(String tinkoffToken) {
		if(tinkoffToken.length() != 88 || !tinkoffToken.substring(0,2).equals("t.")) return false;
		return true;
	}

	@Override
	public List<TinkoffAccount> getAccounts(String tinkoffToken) {
		TinkoffAccount[] accounts = restTemplate.getForEntity(tinkoffConfig.getUrl() + "/accounts?tinkoffToken={tinkoffToken}", 
															  TinkoffAccount[].class, 
															  tinkoffToken)
												.getBody();
		return Arrays.asList(accounts);
	}

	@Override
	public String findInstrumentByTicker(String tinkoffToken, String ticker) {
		return restTemplate.getForObject(tinkoffConfig.getUrl() + "/findByTicker?tinkoffToken={tinkoffToken}&ticker={ticker}", String.class, tinkoffToken, ticker);
	}

	@Override
	@Transactional
	public void deleteUserTinkoffToken(Long chatId) {
		accountRepo.deleteAllByUserId(chatTokenRepo.findByChatId(chatId).getId());
		chatTokenRepo.deleteByChatId(chatId);
	}

	@Override
	public void addAccountsToUser(ChatTokenEntity user) {
		List<TinkoffAccount> accounts = getAccounts(encoder.decode(user.getTinkoffToken()));

		for(TinkoffAccount account: accounts) {
			accountRepo.save(new AccountEntity(account.getId(), user.getId()));
		}
	}

	@Override
	public String postOrder(String tinkoffToken, String ticker, String quantity, String price, String orderDirection,
			String accountId) {
		return restTemplate.getForObject(tinkoffConfig.getUrl() + "/limit?tinkoffToken={tinkoffToken}&"
																	   + "ticker={ticker}&"
																	   + "quantity={quantity}&"
																	   + "price={price}&"
																	   + "orderDirection={orderDirection}&"
																	   + "accountId={accountId}", String.class, tinkoffToken,
																	   													  ticker,
																	   													  quantity,
																	   													  price,
																	   													  orderDirection,
																	   													  accountId);
	}
	
	
}
