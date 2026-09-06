package ru.boycemic.tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.boycemic.tictactoe.domain.service.GameService;
import ru.boycemic.tictactoe.domain.service.GameServiceImpl;
import ru.boycemic.tictactoe.domain.repository.GameRepository;

@Configuration
public class AppConfig {

    @Bean
    public GameService gameService(GameRepository gameRepository) {
        return new GameServiceImpl(gameRepository);
    }
}
