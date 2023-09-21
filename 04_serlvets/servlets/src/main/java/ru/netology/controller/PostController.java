package ru.netology.controller;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class PostController {
  public static final String APPLICATION_JSON = "application/json";
  private final PostService service;

  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON); //устанавливаем в заголовке ответа тип JSON
    final var data = service.all(); //получаем все посты с помощью сервиса и кладем их в поле data
    final var gson = new Gson(); //создаем объект gson дья сериализации данных в json
    response.getWriter().print(gson.toJson(data)); //сериализуем данные из поля data в json и отправляем их клиенту
  }

  public void getById(long id, HttpServletResponse response) {
    response.setContentType(APPLICATION_JSON);
    final var gson = new Gson();
    final var data = service.getById(id);
    try {
      response.getWriter().print(gson.toJson(data));
    } catch (IOException e) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  public void save(Reader body, HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var gson = new Gson();
    final var post = gson.fromJson(body, Post.class); //первый параметр Reader body - из него метод десериализует в
    // формат класса Post и сохраняет результат десериализации в формате класса Post в поле var post.
    final var data = service.save(post); //вызывает метод класса PostService service.save(post) и передает в параметр
    // поле post. То, что метод возвращает, сохраняется в поле data
    response.getWriter().print(gson.toJson(data)); //обновленный данные из поля data сериализуются в формат json и
    // отправляются клиенту
  }

  public void removeById(long id, HttpServletResponse response) {
    service.removeById(id);
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
