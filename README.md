# Tic-Tac-Toe API

REST API для игры в крестики-нолики с поддержкой игры между двумя игроками и игры против компьютера (AI на Minimax).

## Стек технологий

- **Java 21** + **Spring Boot 4.1.0**
- **PostgreSQL** — база данных
- **Spring Data JPA / Hibernate** — работа с БД (`CrudRepository`)
- **Spring Security** — авторизация (Basic Auth через собственный `AuthFilter`)
- **Gradle (Kotlin DSL)** — сборка

## Быстрый старт

```bash
# 1. Поднять PostgreSQL и создать БД (один раз)
psql -U postgres -c "CREATE DATABASE tictactoe;"

# 2. Собрать проект
./gradlew build

# 3. Запустить
./gradlew bootRun
```

Сервер поднимется на `http://localhost:8080`. Таблицы (`users`, `games`) создаются автоматически при старте (`spring.jpa.hibernate.ddl-auto=update`), миграции руками катить не нужно.

Проверить, что всё живо:

```bash
curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"login":"player1","password":"pass123"}'
```

Ожидаемый ответ — `201 Created`.

### Требования

- JDK 21
- PostgreSQL 14+ (проверено на 16), запущен и слушает `localhost:5432`
- Gradle не нужен — используется `./gradlew` (Gradle Wrapper)

### Конфигурация подключения к БД

`src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tictactoe
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Поменяйте `username`/`password`/`url` под своё окружение или переопределите переменными окружения (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).

### Тесты

```bash
./gradlew test
```

## Структура проекта

```
ru.boycemic.tictactoe
├── TictactoeApplication.java       — точка входа Spring Boot
├── domain/
│   ├── model/
│   │   ├── User.java               — пользователь (JPA-сущность): UUID, login, password
│   │   ├── Game.java               — игра (JPA-сущность), см. таблицу полей ниже
│   │   ├── GameStatus.java         — статусы игры
│   │   ├── Board.java              — игровое поле 3x3 + проверка победы/заполненности
│   │   └── BoardConverter.java     — сериализация int[][] поля в текстовую колонку БД
│   ├── repository/
│   │   └── GameRepository.java     — CrudRepository<Game, UUID>
│   └── service/
│       ├── AuthService.java        — сервис авторизации (регистрация/логин), использует UserService
│       ├── AuthServiceImpl.java
│       ├── UserService.java        — сервис пользователей (CRUD + проверка логина/пароля)
│       ├── UserServiceImpl.java
│       ├── GameService.java        — игровая логика (ход, проверка окончания игры)
│       ├── GameServiceImpl.java
│       └── Minimax.java            — AI-противник (полный перебор, играет безупречно)
├── datasource/
│   └── repository/
│       └── UserRepository.java     — CrudRepository<User, UUID> + findByLogin
├── di/
│   ├── AppConfig.java              — бин GameService
│   └── SecurityConfig.java         — SecurityFilterChain + подключение AuthFilter
├── web/
│   ├── controller/
│   │   ├── AuthController.java     — регистрация / логин
│   │   ├── GameController.java     — создание/список/join/ход/просмотр игры, инфо о пользователе
│   │   └── ExceptionHandlerController.java — маппинг исключений в HTTP-коды
│   ├── filter/
│   │   └── AuthFilter.java         — проверка Basic Auth на каждый запрос, кроме /auth/**
│   ├── mapper/
│   │   └── GameWebMapper.java      — Game -> GameResponse
│   ├── model/
│   │   ├── GameResponse.java       — ответ API с состоянием игры
│   │   ├── CreateGameRequest.java  — тело запроса на создание игры (соперник: HUMAN/COMPUTER)
│   │   ├── MoveRequest.java        — тело запроса на ход
│   │   └── SignUpRequest.java      — тело запроса на регистрацию
│   └── exception/                  — типизированные исключения (см. таблицу ошибок)
```

## Модель игры (`Game`)

| Поле                  | Тип       | Описание                                                                 |
|------------------------|-----------|---------------------------------------------------------------------------|
| `id`                   | UUID      | Идентификатор игры (генерируется БД)                                     |
| `board`                | int[3][3] | `0` — пусто, `1` — знак игрока 1, `2` — знак игрока 2                    |
| `status`               | GameStatus| См. таблицу статусов ниже                                                 |
| `player1` / `player2`  | UUID      | Игроки; `player2 = null`, если игра против компьютера или ещё не начата  |
| `player1Mark` / `player2Mark` | int | Значки, которыми ходят игроки (по умолчанию `1` и `2`)                   |
| `vsComputer`           | boolean   | `true` — игра против AI, `player2` в этом случае всегда `null`            |
| `currentTurnPlayerId`  | UUID      | Чей сейчас ход; `null`, если игра ещё не началась или уже завершена       |
| `winnerId`             | UUID      | Победитель; `null` для ничьи, незавершённой игры и при победе компьютера |

## Статусы игры

| Статус                 | Описание                                                             |
|-------------------------|-----------------------------------------------------------------------|
| `WAITING_FOR_PLAYERS`   | Игра создана как «против человека», ждёт второго игрока (`join`)     |
| `IN_PROGRESS`           | Игра идёт — чей ход, смотрите `currentTurnPlayerId`                  |
| `DRAW`                  | Ничья                                                                 |
| `FINISHED`              | Игра завершена победой — победитель в `winnerId` (`null`, если выиграл компьютер) |

## API Endpoints

### 🔐 Авторизация

#### Регистрация

```
POST /auth/register
Content-Type: application/json

{
  "login": "player1",
  "password": "secret123"
}
```

| Код | Описание                                     |
|-----|-----------------------------------------------|
| 201 | Пользователь создан                            |
| 409 | Пользователь с таким логином уже существует    |

#### Логин

```
POST /auth/login
Authorization: Basic base64(login:password)
```

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Authorization: Basic $(echo -n 'player1:secret123' | base64)"
```

Ответ:

```json
{ "userId": "550e8400-e29b-41d4-a716-446655440000" }
```

| Код | Описание                 |
|-----|---------------------------|
| 200 | Авторизация успешна        |
| 401 | Неверный логин/пароль или отсутствует заголовок |

---

### 🎮 Игры

> Все эндпоинты, кроме `/auth/**`, требуют заголовок `Authorization: Basic base64(login:password)`.

#### Создать игру

```
POST /game
Authorization: Basic base64(login:password)
Content-Type: application/json

{ "opponent": "HUMAN" }
```

`opponent` — `HUMAN` (по умолчанию, ждём второго игрока) или `COMPUTER` (игра стартует сразу же, ходит текущий пользователь). Тело запроса необязательно — без него создаётся игра против человека.

Создатель всегда становится **Player 1** (mark = `1`, ходит первым).

#### Получить список доступных игр

```
GET /game
Authorization: Basic base64(login:password)
```

Возвращает игры в статусе `WAITING_FOR_PLAYERS` без второго игрока (игры против компьютера в список не попадают — присоединиться к ним нельзя).

#### Присоединиться к игре

```
POST /game/{uuid}/join
Authorization: Basic base64(login:password)
```

Присоединяет текущего пользователя как **Player 2** (mark = `2`). Игра переходит в `IN_PROGRESS`, первый ход — у Player 1.

#### Получить состояние игры

```
GET /game/{uuid}
Authorization: Basic base64(login:password)
```

#### Сделать ход

```
POST /game/{uuid}/move
Authorization: Basic base64(login:password)
Content-Type: application/json

{ "row": 1, "column": 0 }
```

- Ход принимается только от игрока, чей сейчас `currentTurnPlayerId` — иначе `400 InvalidMoveException`.
- В игре **против компьютера**: сразу после хода игрока автоматически ходит AI.
- Координаты `row`/`column` — в диапазоне `0..2`, клетка должна быть свободна.

#### Получить информацию о пользователе

```
GET /game/user/{uuid}
Authorization: Basic base64(login:password)
```

```json
{ "id": "uuid", "login": "player1" }
```

---

## Пример сценария: игра против компьютера

```bash
curl -s -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" \
  -d '{"login":"player1","password":"pass123"}'

AUTH=$(echo -n 'player1:pass123' | base64)

GAME_ID=$(curl -s -X POST http://localhost:8080/game \
  -H "Authorization: Basic $AUTH" -H "Content-Type: application/json" \
  -d '{"opponent":"COMPUTER"}' | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

curl -s -X POST "http://localhost:8080/game/$GAME_ID/move" \
  -H "Authorization: Basic $AUTH" -H "Content-Type: application/json" \
  -d '{"row":1,"column":1}'
```

## Пример сценария: игра двух игроков

```bash
curl -s -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" \
  -d '{"login":"player1","password":"pass123"}'
curl -s -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" \
  -d '{"login":"player2","password":"pass456"}'

AUTH_P1=$(echo -n 'player1:pass123' | base64)
AUTH_P2=$(echo -n 'player2:pass456' | base64)

GAME_ID=$(curl -s -X POST http://localhost:8080/game \
  -H "Authorization: Basic $AUTH_P1" -H "Content-Type: application/json" \
  -d '{"opponent":"HUMAN"}' | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

curl -s -X POST "http://localhost:8080/game/$GAME_ID/join" -H "Authorization: Basic $AUTH_P2"

curl -s -X POST "http://localhost:8080/game/$GAME_ID/move" \
  -H "Authorization: Basic $AUTH_P1" -H "Content-Type: application/json" -d '{"row":0,"column":0}'

curl -s -X POST "http://localhost:8080/game/$GAME_ID/move" \
  -H "Authorization: Basic $AUTH_P2" -H "Content-Type: application/json" -d '{"row":1,"column":1}'
```

## Обработка ошибок

| HTTP код | Исключение                     | Когда возникает                                        |
|----------|----------------------------------|----------------------------------------------------------|
| 400      | `InvalidMoveException`           | Неверные координаты, клетка занята, чужая очередь хода, игра ещё ждёт второго игрока |
| 400      | `GameFinishedException`          | Попытка хода в уже завершённой игре                      |
| 401      | `InvalidCredentialsException`    | Неверный логин/пароль, отсутствующий или битый заголовок `Authorization` |
| 404      | `GameNotFoundException`          | Игра с таким UUID не найдена                              |
| 404      | `UserNotFoundException`          | Пользователь с таким UUID не найден                       |
| 409      | `GameNotAvailableException`      | Игра уже занята/не в статусе ожидания/попытка зайти в свою же игру |

## Алгоритм AI

**Minimax** с полным перебором игрового дерева — играет безупречно, выиграть у него нельзя, максимум — свести к ничьей.

- Игрок-человек = mark `1`, компьютер = mark `2`.
- Оценка терминальных состояний: победа AI = `+10`, победа человека = `-10`, ничья = `0`.
