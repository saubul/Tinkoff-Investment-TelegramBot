package ru.saubulproject.telegramtinkoffbot.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import ru.saubulproject.telegramtinkoffbot.entity.ChatTokenEntity;
import ru.saubulproject.telegramtinkoffbot.repository.ChatTokenRepository;
import ru.saubulproject.telegramtinkoffbot.service.TinkoffService;

@Service
@RequiredArgsConstructor
public class TiknoffServiceImpl implements TinkoffService{
	
	private RestTemplate restTemplate;
	private ChatTokenRepository chatTokenRepo;
	
	public boolean checkUserTinkoffTokenRegistration(Long chatId) {
		return false;
	}

	public void addUserTinkoffToken(String tinkoff_token, Long chat_id) {
		chatTokenRepo.save(new ChatTokenEntity(chat_id, tinkoff_token));
	}
	
}
