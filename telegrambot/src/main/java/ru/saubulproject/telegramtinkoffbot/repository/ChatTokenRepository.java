package ru.saubulproject.telegramtinkoffbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.saubulproject.telegramtinkoffbot.entity.ChatTokenEntity;

@Repository
public interface ChatTokenRepository extends JpaRepository<ChatTokenEntity, Long>{

}
