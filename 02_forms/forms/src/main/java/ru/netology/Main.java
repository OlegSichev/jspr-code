package ru.netology;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {
  public static final String GET = "GET";
  public static final String POST = "POST";

  public static void main(String[] args) {
    final var allowedMethods = List.of(GET, POST); //Только два поддерживаемых метода т.к. формы с другими методами
    // не работают

    try (final var serverSocket = new ServerSocket(9999)) {
      while (true) {
        try (
                final var socket = serverSocket.accept(); //ожидание клиента
                final var in = new BufferedInputStream(socket.getInputStream()); //буферизованный входящий поток
                final var out = new BufferedOutputStream(socket.getOutputStream()); //буферизованный исходящий поток
        ) {
          // лимит на request line + заголовки
          final var limit = 4096; //лимит загловков ставится, что б защитить наш сервер от некорректных
          // запросов, которые весят слишком много. Например, из-за запроса, который весит 10гб - сервер
          // может лечь. Такой запрос может произойти, либо из-за сбоя работы клиента, либо из-за специальной
          // хакерской атаки. 4096байт (4кб), либо 8кб - стандарт ограничения лимита на серверах

          in.mark(limit); //с помощью метода mark - Мы ставим лимит на входные данные (входному
          // буферизованному потоку). В параметр передаем поле limit со значением в 4096 байт.
          final var buffer = new byte[limit]; //создаем буффер нужного размера
          final var read = in.read(buffer); //читаем созданный буффер и в переменную read сохраняем число
          // - сколько байт Мы в итоге прочитали

          // ищем request line
          final var requestLineDelimiter = new byte[]{'\r', '\n'};
          final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
          if (requestLineEnd == -1) {
            badRequest(out);
            continue;
          }

          // читаем request line
          final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
          //с помошью метода Arrays.copyOf Мы копируем из buffer (первый параметр) нужное нам количество байт
          // (второй параметр - requestLineEnd). Этот массив байт превращается в String (строку) и с помощью
          // метода .split(" ") разделяется на три части по пробелу
          if (requestLine.length != 3) { //елси оказалось не три части, значит нам что-то не то прислали,
            // Мы возвращаем ошибку badRequest (плохой запрос) и переходим к следующей иттерации цикла
            badRequest(out);
            continue;
          }

          final var method = requestLine[0]; //присваиваем к полю method - наш запрос с методом
          if (!allowedMethods.contains(method)) { //если метод есть в списке allowedMethods (находится в
            // самом начале класса), то все хорошо, если нет, то возвращаем ошибку badRequest (плохой
            // запрос) и переходим к следующей иттерации цикла (выходим)
            badRequest(out);
            continue;
          }
          System.out.println(method); //логируем, что мы распарсили (выводим на экран)

          final var path = requestLine[1]; //проверяем, какой в запросе путь
          if (!path.startsWith("/")) {//если путь начинается не с "/", то возвращаем ошибку badRequest
            badRequest(out);
            continue;
          }
          System.out.println(path); //логируем (выводим на экран) путь, если все хорошо

          // ищем заголовки
          final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'}; // Создание массива байт
          // headersDelimiter, который представляет символы разделителя заголовков в HTTP-сообщении.
          // В данном случае, это последовательность символов \r\n\r\n (Carriage Return, Line Feed,
          // Carriage Return, Line Feed), которая обозначает конец списка заголовков.
          final var headersStart = requestLineEnd + requestLineDelimiter.length; // Добавляем индекс конца
          // строки (на чем остановились) к количеству символов впереди и получаем таким образом индекс начала
          // заголовков
          final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read); //Определение индекса
          // конца заголовков (headersEnd). Для этого вызывается функция indexOf(buffer, headersDelimiter,
          // headersStart, read), которая ищет в буфере buffer разделитель headersDelimiter, начиная
          // с позиции headersStart и заканчивая позицией read. Если разделитель не найден, функция
          // возвращает -1.
          if (headersEnd == -1) { //Проверка, найден ли конец заголовков. Если значение headersEnd равно -1,
            // это означает, что разделитель не был найден, и соответственно запрос является некорректным.
            // В этом случае вызывается функция badRequest(out) для отправки ответа с кодом ошибки "Bad
            // Request". Затем оператор continue используется для продолжения выполнения следующей итерации
            // цикла, пропуская оставшиеся инструкции данной итерации.
            badRequest(out);
            continue;
          }

          // отматываем на начало буфера
          in.reset(); //метод in.reset сбрасывает указатель на начало буффера
          // пропускаем requestLine
          in.skip(headersStart); //с помощью метода skip и параметьра headersStart - мы перематываем параметры
          // на то место с которого начинаются заголовки (обязательно сначала надо сделать reset (предыдущий
          // метод))

          final var headersBytes = in.readNBytes(headersEnd - headersStart); //вычитаем из поля
          // содержащего индекс конца - поле с индексом начала и получаем то количество байт, в котором
          // находятся заголовки. Считываем их из входного потока с помощью метода readNBytes и
          // соответственно, в поле headersByter - находятся все байты в которых находятся заголовки
          final var headers = Arrays.asList(new String(headersBytes).split("\r\n")); //затем из этих
          // байт Мы создаем строку и разделяем с помощью метода .split по r\n, создаем массив строк и
          // преобразовываем ее в List с помощью метода asList
          System.out.println(headers); //получили список заголовков и вывели его на экран

          // для GET тела нет
          if (!method.equals(GET)) { //если приходит запрос GET, то мы просто передаем, что 200 ок, запрос
            // обработан
            in.skip(headersDelimiter.length); //если пришел метод Post, то пропускаем еще 4 байта
            // вычитываем Content-Length, чтобы прочитать body
            final var contentLength = extractHeader(headers, "Content-Length"); //принимает два
            // параметра - список заголовков и имя заголовка для поиска. Получаем строку и значение
            // заголовков.
            if (contentLength.isPresent()) { //проверяем, что заголовок есть, что мы его получили т.к. есть
              // вероятность, что такого заголовка может не быть, нам никто не гарантирует, что он будет
              final var length = Integer.parseInt(contentLength.get()); //после того, как Мы получили
              // строку, Мы должны ее преобразовать в int, понять, что количество байт у нас целое
              final var bodyBytes = in.readNBytes(length); //после того, что мы знаем, какое количество
              // байт нам надо прочитать, Мы читаем эти байты из входного потока с помощью метода
              // readNBytes

              final var body = new String(bodyBytes); //полученные байты преобразовываем в строку
              // (для программы это не нужно. В первую очередь это нужно для отладки, что б Мы
              // преобразованное количество байт могли вывести на экран методом System.out.println
              // (строчка ниже)
              System.out.println(body); //залогировали тело запроса на экран, затем вывели 200ок и запрос
              // таким образом был обработан
            }
          }

          out.write(( // если пришел метод GET (чуть выше это описал)
                  "HTTP/1.1 200 OK\r\n" +
                          "Content-Length: 0\r\n" +
                          "Connection: close\r\n" +
                          "\r\n"
          ).getBytes());
          out.flush();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Optional<String> extractHeader(List<String> headers, String header) {
    return headers.stream() //с помощью стрима (потока) мы ищем
            .filter(o -> o.startsWith(header)) //фильтруем все заголовки, которые начинаются с указанной строки
            .map(o -> o.substring(o.indexOf(" "))) //ищем с помощью indexOf вхождение пробела. Берем строку от
            // вхождения пробела до конца
            .map(String::trim) //если вдруг остались какие-то пробелы, то мы их триммем (стираем)
            .findFirst(); //возвращаем первый найденный заголовок
  } //получаем строку и значение заголовков

  private static void badRequest(BufferedOutputStream out) throws IOException {
    out.write((
            "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
    ).getBytes());
    out.flush();
  }

  // from google guava with modifications
  private static int indexOf(byte[] array, byte[] target, int start, int max) {
    outer:
    for (int i = start; i < max - target.length + 1; i++) {
      for (int j = 0; j < target.length; j++) {
        if (array[i + j] != target[j]) {
          continue outer;
        }
      }
      return i;
    }
    return -1;
  }
}
