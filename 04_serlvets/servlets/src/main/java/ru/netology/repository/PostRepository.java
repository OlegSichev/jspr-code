package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
  private final Map<Long, Post> list = new HashMap<>();
  private final AtomicLong postID = new AtomicLong();

  public List<Post> all() {
    return new ArrayList<>(list.values());

  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(list.get(id));
  }

  public Post save(Post post) {
    postID.incrementAndGet();
    list.put(postID.get(), post);
    return post;
  }

  public void removeById(long id) {
    try {
      list.remove(id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}