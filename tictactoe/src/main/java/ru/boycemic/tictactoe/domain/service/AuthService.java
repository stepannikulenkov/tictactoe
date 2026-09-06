package ru.boycemic.tictactoe.domain.service;

import ru.boycemic.tictactoe.web.model.SignUpRequest;

import java.util.UUID;

/**
 * Сервис авторизации. Не хранит пользователей сам — использует {@link UserService}.
 */
public interface AuthService {

    /** Регистрирует нового пользователя, возвращает факт успешной регистрации. */
    boolean register(SignUpRequest request);

    /**
     * Проверяет заголовок {@code Authorization: Basic base64(login:password)}
     * и возвращает UUID пользователя.
     */
    UUID authenticate(String authorizationHeader);
}
