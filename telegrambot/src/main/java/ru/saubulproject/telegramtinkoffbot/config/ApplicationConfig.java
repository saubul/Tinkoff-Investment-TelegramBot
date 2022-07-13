package ru.saubulproject.telegramtinkoffbot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import ru.saubulproject.telegramtinkoffbot.encoder.Encoder;
import ru.saubulproject.telegramtinkoffbot.encoder.impl.EncoderSHA256;

@Configuration
@EnableConfigurationProperties(TinkoffConfig.class)
public class ApplicationConfig {
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}
	
	@Bean
	public Encoder encoder() {
		return new EncoderSHA256();
	}
	
}
