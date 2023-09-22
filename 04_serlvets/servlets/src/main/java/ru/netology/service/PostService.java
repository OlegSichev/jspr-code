package ru.netology.service;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.repository.PostRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

  private final PostRepositoryInterface repository;

  public PostService(PostRepositoryInterface repository) {
    this.repository = repository;
  }

  public List<Post> all() {
    return repository.all();
  }

  public Post getById(long id) {
    return repository.getById(id).orElseThrow(() ->
            new NotFoundException("Post with id=" + id + " is not found"));
  }

  public Post save(Post post) {
    if (post.getId() <= repository.all().size()) {
      return repository.save(post);
    }
    throw new NotFoundException("Post with id=" + post.getId() + " is not found");
  }

  public void removeById(long id) {
    repository.removeById(id);
  }
}
