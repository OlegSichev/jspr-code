package ru.netology.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException() {
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }

  public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace); //Подробнее о параметрах в методе - String message
    // (сообщение об ошибке выводящееся в консоль, Throwable cause (причина, можно передать другую ошибку, как объект
    // для более понятной связи ошибок (исключений)), boolean enableSuppression (флаг обозначающий - разрешено ли
    // скрытие (подавление) этого исключения другими исключениями), boolean writableStackTrace (флаг, если true, то это
    // исключений будет содержать стек вызовов (stack trace) для более подробной информации о том, где произошло
    // исключение). Параметры всех предыдущих конструкторов - по аналогии с этим комментарием
  }
}
