package com.explorewithme.server.service;

import com.explorewithme.server.dto.ChangeStateRequestDto;
import com.explorewithme.server.dto.ChangeStateResponseDto;
import com.explorewithme.server.dto.RequestResponseDto;
import com.explorewithme.server.exception.*;
import com.explorewithme.server.mapper.RequestMapper;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import com.explorewithme.server.model.User;
import com.explorewithme.server.repository.EventRepository;
import com.explorewithme.server.repository.RequestRepository;
import com.explorewithme.server.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public RequestResponseDto cancel(Integer userId, Integer requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));

        Request request = requestRepository.findByRequesterAndId(user, requestId)
                .orElseThrow(() -> new RequestNotFoundException("No such user with id - " + userId));

        request.setState(Request.State.CANCELED);
        Request cancelled = requestRepository.save(request);
        logger.info("Cancelled request - " + request);

        return RequestMapper.toDto(cancelled);
    }

    @Transactional(readOnly = true)
    public List<RequestResponseDto> findAllByRequester(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        return requestRepository.findByRequester(user)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChangeStateResponseDto changeState(Integer userId,
                                              Integer eventId,
                                              ChangeStateRequestDto changeStateDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventRepository.findByAuthorAndId(author, eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId));

        if (requestRepository.countAllByIdInAndStateIn(changeStateDto.getRequestIds(), Arrays.asList(Request.State.REJECTED, Request.State.CONFIRMED)) > 0) {
            throw new EventValidationException("Request ids has illegal state");
        }

        ChangeStateRequestDto.Status status = changeStateDto.getStatus();
        Request.State state;
        if (status == ChangeStateRequestDto.Status.CONFIRMED) {
            state = Request.State.CONFIRMED;
        } else if (status == ChangeStateRequestDto.Status.REJECTED) {
            state = Request.State.REJECTED;
        } else {
            throw new IllegalArgumentException("There is no behavior for status " + status);
        }

        List<Request> requests = requestRepository.findByEventAndIdIn(event, changeStateDto.getRequestIds());
        requests.forEach(request -> request.setState(state));
        requestRepository.saveAll(requests);

        List<RequestResponseDto> dto = requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());

        ChangeStateResponseDto changeStateResponseDto = new ChangeStateResponseDto();
        if (status == ChangeStateRequestDto.Status.CONFIRMED) {
            changeStateResponseDto.setConfirmedRequests(dto);
        } else if (status == ChangeStateRequestDto.Status.REJECTED) {
            changeStateResponseDto.setRejectedRequests(dto);
        } else {
            throw new IllegalArgumentException("There is no behavior for status " + status);
        }

        return changeStateResponseDto;


    }

    @Transactional
    public RequestResponseDto post(Integer userId, Integer eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId));

        if (event.getState() != Event.State.PUBLISHED) {
            throw new RequestValidationException("Event with id " + event.getId() + " is not published");
        }

        if (event.getAuthor().equals(user)) {
            throw new RequestValidationException("Event`s authors can not add request");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= requestRepository.countAllByEventAndStateIn(
                event,
                Arrays.asList(Request.State.CONFIRMED, Request.State.PENDING))) {
            throw new RequestValidationException("Event`s participant size is full");
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        Request.State state = Request.State.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            state = Request.State.CONFIRMED;
        }
        request.setState(state);


        Request created = requestRepository.save(request);
        logger.info("Created request - " + created);

        return RequestMapper.toDto(created);
    }

    @Transactional(readOnly = true)
    public List<RequestResponseDto> findAllByRequesterAndEvent(Integer userId, Integer eventId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventRepository.findByAuthorAndId(author, eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId));

        List<Request> requests = requestRepository.findByEvent(event);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
