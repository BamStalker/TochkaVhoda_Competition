package academy.tochkavhoda.competition.exception;

public enum ErrorCode {
    USER_NOT_FOUND("Пользователь не найден"),
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
    INVALID_TOKEN("Недействительный токен"),
    NOT_PARTICIPANT("Эта операция доступна только участнику"),
    EMPTY_APPLICATIONS("Список заявок не должен быть пустым"),
    EMPTY_TITLE("Название заявки не должно быть пустым"),
    EMPTY_DESCRIPTION("Описание заявки не должно быть пустым"),
    INVALID_AMOUNT("Запрашиваемая сумма должна быть больше нуля"),
    APPLICATION_NOT_FOUND("Заявка не найдена"),
    NOT_APPLICATION_OWNER("Нельзя отменить чужую заявку");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
