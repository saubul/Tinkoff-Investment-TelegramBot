package ru.saubulproject.telegramtinkoffbot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatTokenEntity {
	
	public ChatTokenEntity(Long chatId, String tinkoffToken) {
		this.chatId = chatId;
		this.tinkoffToken = tinkoffToken;
	}
	
	@Id
	@SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
	private Long id;
	
	@Column(name = "chat_id")
	private Long chatId;
	
	@Column(name = "tinkoff_token")
	private String tinkoffToken;
	
}
