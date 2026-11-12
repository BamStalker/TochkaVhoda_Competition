package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.exception.ServerException;

/**
 * Результат работы метода сервиса: успех (с данными или без) либо ошибка.
 * Сервис ничего не знает про JSON и ServerResponse, это забота слоя Server.
 */
public class ResultService<T> {
    private final boolean isSuccess;
    private final T data;
    private final ServerException serverException;

    private ResultService(boolean isSuccess, T data, ServerException serverException) {
        this.isSuccess = isSuccess;
        this.data = data;
        this.serverException = serverException;
    }

    public ResultService(boolean isSuccess, ServerException serverException) {
        this(isSuccess, null, serverException);
    }

    public ResultService(boolean isSuccess) {
        this(isSuccess, null);
    }

    /** Успех с данными. */
    public static <T> ResultService<T> success(T data) {
        return new ResultService<>(true, data, null);
    }

    /** Ошибка. */
    public static <T> ResultService<T> failure(ServerException serverException) {
        return new ResultService<>(false, null, serverException);
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public T getData() {
        return data;
    }

    public ServerException getServerException() {
        return serverException;
    }
}
