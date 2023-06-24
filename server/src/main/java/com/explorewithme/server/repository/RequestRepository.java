package com.explorewithme.server.repository;


import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import com.explorewithme.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequester(User user);

    Optional<Request> findByRequesterAndId(User user, Integer id);

    List<Request> findByEventAndIdIn(Event event, List<Integer> requestIds);

    long countAllByEventAndStateIn(Event event, List<Request.State> states);

    long countAllByIdInAndStateIn(List<Integer> ids, List<Request.State> states);

    List<Request> findByEvent(Event event);

    boolean existsByEventAndRequesterAndState(Event event, User user, Request.State confirmed);
}
