package ru.saubulproject.telegramtinkoffbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "tinkoff")
@Data
public class TinkoffConfig {
	
	private String url;
	
}
