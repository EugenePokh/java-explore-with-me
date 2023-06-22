package com.explorewithme.server.repository;

import com.explorewithme.server.model.Comment;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByIdAndUser(Integer id, User user);

    List<Comment> findByEventAndStateIn(Event event, List<Comment.State> asList);
}
