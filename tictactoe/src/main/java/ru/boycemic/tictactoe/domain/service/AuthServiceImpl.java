package ru.boycemic.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import ru.boycemic.tictactoe.domain.model.User;
import ru.boycemic.tictactoe.web.exception.InvalidCredentialsException;
import ru.boycemic.tictactoe.web.model.SignUpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String BASIC_PREFIX = "Basic ";

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean register(SignUpRequest request) {
        return userService.register(request);
    }

    @Override
    public UUID authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_PREFIX)) {
            throw new InvalidCredentialsException("Authorization header must be 'Basic base64(login:password)'");
        }

        String decoded;
        try {
            decoded = new String(
                    Base64.getDecoder().decode(authorizationHeader.substring(BASIC_PREFIX.length())),
                    StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsException("Malformed Authorization header");
        }

        String[] parts = decoded.split(":", 2);
        if (parts.length != 2) {
            throw new InvalidCredentialsException("Malformed Authorization header");
        }

        User user = userService.authenticate(parts[0], parts[1]);
        return user.getId();
    }
}
