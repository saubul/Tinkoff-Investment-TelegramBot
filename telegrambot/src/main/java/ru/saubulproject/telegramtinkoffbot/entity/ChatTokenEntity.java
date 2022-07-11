package ru.saubulproject.telegramtinkoffbot.entity;

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
	
	public ChatTokenEntity(Long chat_id, String tinkoff_token) {
		this.chat_id = chat_id;
		this.tinkoff_token = tinkoff_token;
	}
	
	@Id
	@SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
	private Long id;
	private Long chat_id;
	private String tinkoff_token;
	
}
