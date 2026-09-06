package ru.boycemic.tictactoe.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.boycemic.tictactoe.domain.model.Game;
import ru.boycemic.tictactoe.domain.model.GameStatus;
import ru.boycemic.tictactoe.domain.model.User;
import ru.boycemic.tictactoe.domain.repository.GameRepository;
import ru.boycemic.tictactoe.domain.service.GameService;
import ru.boycemic.tictactoe.domain.service.UserService;
import ru.boycemic.tictactoe.web.exception.GameNotAvailableException;
import ru.boycemic.tictactoe.web.exception.GameNotFoundException;
import ru.boycemic.tictactoe.web.exception.UserNotFoundException;
import ru.boycemic.tictactoe.web.mapper.GameWebMapper;
import ru.boycemic.tictactoe.web.model.CreateGameRequest;
import ru.boycemic.tictactoe.web.model.GameResponse;
import ru.boycemic.tictactoe.web.model.MoveRequest;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import java.util.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final GameRepository gameRepository;
    private final UserService userService;
    private final GameWebMapper mapper;

    public GameController(
            GameService gameService,
            GameRepository gameRepository,
            UserService userService,
            GameWebMapper mapper
    ) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping
    public GameResponse createGame(
            @RequestBody(required = false) CreateGameRequest request,
            HttpServletRequest httpRequest
    ) {

        UUID userId = (UUID) httpRequest.getAttribute("userId");
        User player1 = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        boolean vsComputer = request != null && request.getOpponent() == CreateGameRequest.OpponentType.COMPUTER;

        Game game = new Game(
                null,
                new int[3][3],
                vsComputer ? GameStatus.IN_PROGRESS : GameStatus.WAITING_FOR_PLAYERS,
                player1,
                null,
                vsComputer
        );

        if (vsComputer) {
            game.setCurrentTurnPlayerId(player1.getId());
        }

        gameRepository.save(game);

        return mapper.toResponse(game);
    }

    @GetMapping
    public List<GameResponse> getActiveGames() {

        List<Game> allGames = StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        return allGames.stream()
                .filter(g -> g.getStatus() == GameStatus.WAITING_FOR_PLAYERS
                        && !g.isVsComputer()
                        && g.getPlayer2() == null)
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/{uuid}/join")
    public GameResponse joinGame(
            @PathVariable String uuid,
            HttpServletRequest request
    ) {

        UUID userId = (UUID) request.getAttribute("userId");
        User player2 = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        Game game = gameRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + uuid));

        if (game.isVsComputer() || game.getPlayer2() != null || game.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new GameNotAvailableException("Game is not available to join");
        }

        if (game.getPlayer1().getId().equals(player2.getId())) {
            throw new GameNotAvailableException("Cannot join your own game");
        }

        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCurrentTurnPlayerId(game.getPlayer1().getId());
        gameRepository.save(game);

        return mapper.toResponse(game);
    }

    @GetMapping("/{uuid}")
    public GameResponse getGame(@PathVariable String uuid) {

        Game game = gameRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + uuid));

        return mapper.toResponse(game);
    }

    @PostMapping("/{uuid}/move")
    public GameResponse makeMove(
            @PathVariable String uuid,
            @RequestBody MoveRequest request,
            HttpServletRequest requestObj
    ) {

        UUID userId = (UUID) requestObj.getAttribute("userId");

        Game game = gameRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + uuid));

        game = gameService.makeMove(
                game,
                request.getRow(),
                request.getColumn(),
                userId
        );

        return mapper.toResponse(game);
    }

    @GetMapping("/user/{uuid}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String uuid) {

        User user = userService.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + uuid));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("login", user.getLogin());
        return ResponseEntity.ok(result);
    }
}
