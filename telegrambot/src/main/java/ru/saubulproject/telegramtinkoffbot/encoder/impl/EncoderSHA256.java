package ru.saubulproject.telegramtinkoffbot.encoder.impl;

import java.util.Base64;

import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import ru.saubulproject.telegramtinkoffbot.encoder.Encoder;

@Component
public class EncoderSHA256 implements Encoder{

	@Override
	@SneakyThrows
	public String encode(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes());
	}

	@Override
	public String decode(String string) {
		return new String(Base64.getDecoder().decode(string.getBytes()));
	}

}
