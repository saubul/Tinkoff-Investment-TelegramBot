package ru.saubulproject.telegramtinkoffbot.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
	
	@Id
	@SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
	private Long id;
	
	@Column(name = "account_id")
	private String accountId;
	
	@Column(name = "user_id")
	private Long userId;
	
	public AccountEntity(String accountId, Long userId) {
		this.accountId = accountId;
		this.userId = userId;
	}
	
}
