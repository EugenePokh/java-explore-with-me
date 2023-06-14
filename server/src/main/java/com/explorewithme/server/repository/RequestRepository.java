package com.explorewithme.server.repository;


import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import com.explorewithme.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByRequester(User user);

    Optional<Request> findByRequesterAndId(User user, Integer id);

    List<Request> findAllByRequesterAndEvent(User user, Event event);

    List<Request> findAllByEventAndIdIn(Event event, List<Integer> requestIds);

    long countAllByEventAndState(Event event, Request.State confirmed);

    long countAllByIdInAndStateIn(List<Integer> ids, List<Request.State> states);
}
