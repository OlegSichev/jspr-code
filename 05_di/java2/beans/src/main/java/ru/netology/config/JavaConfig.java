package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.repository.PostRepositoryStubImpl;
import ru.netology.service.PostService;

@Configuration
public class JavaConfig {
  @Bean
  // название метода - название бина
  public PostController postController() {
    // вызов метода и есть DI
    return new PostController(postService()); //Вызываем метод postController без параметров, внутри метода создаем
    // новый postContoller, а в параметр сразу передаем вызов метода postService(), соответственно, в итоге, в параметр
    // передастся то, что вернет метод postService()
  }

  @Bean
  public PostService postService() {
    return new PostService(postRepository()); //вызываем метод postService без параметров, внутри метода создаем
    // новый postService, а в параметр сразу передаем вызов метода postRepository(), соответственно, в итоге, в параметр
    // передастся то, что вернет метод postRepository()
  }

  @Bean
  public PostRepository postRepository() {
    return new PostRepositoryStubImpl(); //вызываем метод postRepository, в нем создается новый PostRepositoryStubImpl
    // и возвращается тому, кто вызвал этот метод. Для создания репозитория - никакие параметры не требуются, он ни от
    // кого не зависит
  }
}
