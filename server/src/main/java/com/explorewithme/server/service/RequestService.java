package com.explorewithme.server.service;

import com.explorewithme.server.exception.RequestValidationException;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import com.explorewithme.server.model.User;
import com.explorewithme.server.repository.RequestRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RequestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RequestRepository requestRepository;

    public Request cancel(Request request) {
        request.setState(Request.State.CANCELED);
        logger.info("Cancel request - " + request);
        return requestRepository.save(request);
    }

    public List<Request> findAllByRequestor(User user) {
        return requestRepository.findAllByRequester(user);
    }

    public Optional<Request> findAllRequestorAndId(User user, Integer requestId) {
        return requestRepository.findByRequesterAndId(user, requestId);
    }

    public Request create(User user, Event event) {
        if (event.getState() != Event.State.PUBLISHED) {
            throw new RequestValidationException("Event with id " + event.getId() + " is not published");
        }

        if (event.getAuthor().equals(user)) {
            throw new RequestValidationException("Event`s authors can not add request");
        }

        if (Objects.nonNull(event.getParticipantLimit())
                && event.getParticipantLimit() <= requestRepository.countAllByEventAndState(event, Request.State.CONFIRMED)) {
            throw new RequestValidationException("Event`s participant size is full");
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        Request.State state = Request.State.PENDING;
        if (!event.getRequestModeration()) {
            state = Request.State.CONFIRMED;
        }
        request.setState(state);

        logger.info("Create request - " + request);
        return requestRepository.save(request);
    }

    public List<Request> findAllByRequesorAndEvent(User user, Event event) {
        return requestRepository.findAllByRequesterAndEvent(user, event);
    }

    public List<Request> changeState(Event event, List<Integer> requestIds, Request.State state) {
        List<Request> requests = requestRepository.findAllByEventAndIdIn(event, requestIds);
        requests.forEach(request -> request.setState(state));
        requestRepository.saveAll(requests);

        return requests;
    }

    public long countAllByIdInAndStateIn(List<Integer> requestIds, List<Request.State> states) {
        return requestRepository.countAllByIdInAndStateIn(requestIds, states);
    }
}
