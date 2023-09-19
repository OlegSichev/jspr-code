package ru.netology;

import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.EmptyCompletionHandler;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.multipart.MultipartEntry;
import org.glassfish.grizzly.http.multipart.MultipartEntryHandler;
import org.glassfish.grizzly.http.multipart.MultipartScanner;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
  private static final Logger LOGGER = Grizzly.logger(Main.class);

  public static void main(String[] args) throws IOException, InterruptedException {
    final var server = HttpServer.createSimpleServer("static", 9999);//создаем сервер,в первом параметре указываем
    // папку, где хранятся все ресурсы (которая будет считаться корнвой для этих ресурсов), во втором параметре
    // указываем порт с которым работаем (на котором мы будем наше приложение запускать)
    server.getServerConfiguration().addHttpHandler(new HttpHandler() { //добавляем конфигурацию сервера и добавляем
      // обработчик (HttpHandler), который будет обрабатывать все запросы, которые прихрдят на /api
      @Override
      public void service(Request request, Response response) throws Exception {
//        response.getWriter().write("ok"); //получаем нащ запрос response и пишем туда ок
                response.suspend();
                MultipartScanner.scan(request, multipartEntry -> {
                  LOGGER.info(multipartEntry.getContentDisposition().getDispositionParam("name")); //здесь будет
                  // работать специальный класс MultipartScanner c методом scan, в параметрах передается сам запрос
                  // (request) и в нем ищется второй параметр (multipartEntry) мультипарт запрос. Для каждой части
                  // мультипарт запроса будет срабатывать логгер (LOGGER), который будет логировать именно параметры
                  // TODO: handle entry
                }, new EmptyCompletionHandler<>() {
                  @Override
                  public void completed(Request result) {
                    response.resume();
                    try {
                      response.getWriter().write("ok");
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }
                  @Override
                  public void failed(Throwable throwable) {
                    response.resume();
                  }
                });
      }
    }, "/api"); //на все запросы, которые приходят сюда - будет срабатывать наш обработчик

    server.start(); //запускаем сервер
    Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown)); //вызываем метод shutdown, что б сервер
    // выключился, когда он завершит свою работу
    Thread.currentThread().join(); //блокируем потоки, пока сервер работает
  }
}
