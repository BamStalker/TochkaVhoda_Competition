package academy.tochkavhoda.competition.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.server.ServerResponse;

/** Общие вспомогательные методы сервисов (JSON, формирование ответов, проверка логина и пароля). */
final class ServiceUtils {
    static final int MIN_LOGIN_LENGTH = 3;
    static final int MIN_PASSWORD_LENGTH = 8;
    private static final Gson GSON = new Gson();

    private ServiceUtils() {
    }

    static <T> T fromJson(String json, Class<T> clazz) throws ServerException {
        if (json == null || json.trim().isEmpty()) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
        T result;
        try {
            result = GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
        if (result == null) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
        return result;
    }

    static ServerResponse ok(Object responseDto) {
        return new ServerResponse(200, GSON.toJson(responseDto));
    }

    static ServerResponse error(ServerException e) {
        return new ServerResponse(400, GSON.toJson(new ErrorDtoResponse(e.getMessage())));
    }

    static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    static void validateNotBlank(String value, ErrorCode errorCode) throws ServerException {
        if (isBlank(value)) {
            throw new ServerException(errorCode);
        }
    }

    static void validateLogin(String login) throws ServerException {
        validateNotBlank(login, ErrorCode.EMPTY_LOGIN);
        if (login.length() < MIN_LOGIN_LENGTH) {
            throw new ServerException(ErrorCode.SHORT_LOGIN);
        }
    }

    static void validatePassword(String password) throws ServerException {
        validateNotBlank(password, ErrorCode.EMPTY_PASSWORD);
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ServerException(ErrorCode.SHORT_PASSWORD);
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new ServerException(ErrorCode.WEAK_PASSWORD);
        }
    }
}
