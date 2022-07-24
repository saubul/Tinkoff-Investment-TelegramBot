package ru.saubulproject.telegramtinkoffbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.saubulproject.telegramtinkoffbot.entity.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long>{
	
	List<AccountEntity> findAllByUserId(Long userId);

	void deleteAllByUserId(Long id);
	
}
