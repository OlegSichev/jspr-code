package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository implements PostRepositoryInterface {
  private final ConcurrentHashMap<Long, Post> repo = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong(1);

  public List<Post> all() {
    return new ArrayList<>(repo.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(repo.get(id));
  }

  public Post save(Post post) {
    long postId = post.getId();
    if (postId == 0) {
      long newPostId = idCounter.getAndIncrement();
      post.setId(newPostId);
      repo.put(newPostId, post);
    } else {
      repo.put(postId, post);
    }
    return post;
  }

  public void removeById(long id) {
    if (repo.containsKey(id)) {
      repo.remove(id);
    } else {
      throw new NotFoundException("Not found post with id=" + id);
    }
  }
}