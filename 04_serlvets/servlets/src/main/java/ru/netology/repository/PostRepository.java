package ru.netology.repository;

import org.springframework.stereotype.Repository;
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
    if(post.getId() == 0) {
      post.setId(postID.incrementAndGet());
      list.put(post.getId(), post);
    } else {
      Post existingPost = list.get(post.getId());
      existingPost.setContent(post.getContent());
    }
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