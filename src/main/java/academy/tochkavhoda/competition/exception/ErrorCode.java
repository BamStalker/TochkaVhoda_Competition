package academy.tochkavhoda.competition.exception;

public enum ErrorCode {
    INVALID_JSON("Некорректный формат запроса"),
    EMPTY_FIRST_NAME("Имя не должно быть пустым"),
    EMPTY_LAST_NAME("Фамилия не должна быть пустой"),
    EMPTY_FIRM_NAME("Название фирмы не должно быть пустым"),
    EMPTY_DIRECTIONS("Список направлений не должен быть пустым"),
    EMPTY_LOGIN("Логин не должен быть пустым"),
    SHORT_LOGIN("Логин слишком короткий (минимум 3 символа)"),
    EMPTY_PASSWORD("Пароль не должен быть пустым"),
    SHORT_PASSWORD("Пароль слишком короткий (минимум 8 символов)"),
    WEAK_PASSWORD("Пароль должен содержать хотя бы одну букву и одну цифру"),
    LOGIN_ALREADY_USED("Этот логин уже используется"),
    INVALID_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    INVALID_TOKEN("Недействительный токен");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
