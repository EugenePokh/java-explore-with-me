package com.explorewithme.server.service;

import com.explorewithme.dto.HitRequestDto;
import com.explorewithme.server.controller.EventController;
import com.explorewithme.server.dto.EventRequestDto;
import com.explorewithme.server.dto.EventResponseFullDto;
import com.explorewithme.server.dto.EventResponseShortDto;
import com.explorewithme.server.exception.CategoryNotFoundException;
import com.explorewithme.server.exception.EventNotFoundException;
import com.explorewithme.server.exception.UserNotFoundException;
import com.explorewithme.server.mapper.EventMapper;
import com.explorewithme.server.model.Category;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.User;
import com.explorewithme.server.repository.CategoryRepository;
import com.explorewithme.server.repository.EventRepository;
import com.explorewithme.server.repository.UserRepository;
import com.explorewithme.statsclient.StatsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.explorewithme.server.controller.EventController.EventSort.EVENT_DATE;

@Service
public class EventService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.application.name}")
    private String appName;

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatsClient client;

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository, StatsClient client) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.client = client;
    }

    @Transactional(readOnly = true)
    public Page<EventResponseShortDto> findAllByAuthor(Integer userId, Integer from, Integer size) {
        User author = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        PageRequest page = PageRequest.of(from / size, size);

        return eventRepository.findAllByAuthor(author, page)
                .map(EventMapper::toShortDto);
    }

    @Transactional(readOnly = true)
    public EventResponseFullDto findByAuthorAndId(Integer userId, Integer eventId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        return EventMapper.toFullDto(
                eventRepository.findByAuthorAndId(author, eventId)
                        .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId)),
                null);
    }

    @Transactional(readOnly = true)
    public Page<EventResponseFullDto> findAllBySearch(List<Integer> users,
                                                      List<Event.State> states,
                                                      List<Integer> categories,
                                                      LocalDateTime rangeStart,
                                                      LocalDateTime rangeEnd,
                                                      Integer from,
                                                      Integer size) {
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("rangeEnd can not be before rangeStart");
            }
        }
        PageRequest page = PageRequest.of(from / size, size);

        return eventRepository.search(users, states, categories, rangeStart, rangeEnd, page)
                .map(event -> EventMapper.toFullDto(event, null));
    }

    @Transactional(readOnly = true)
    public EventResponseFullDto findById(Integer eventId,
                                         HttpServletRequest httpServletRequest) {

        client.saveStatistic(HitRequestDto.builder()
                .app(appName)
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .uri(httpServletRequest.getRequestURI())
                .build());

        long hits = client.findHitCountsByUrisUnique(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1),
                        Arrays.asList("/events/" + eventId))
                .get(0)
                .getHits();

        EventResponseFullDto dto = EventMapper.toFullDto(
                eventRepository.findById(eventId)
                        .filter(event -> event.getState() == Event.State.PUBLISHED)
                        .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId)),
                hits);


        return dto;
    }

    @Transactional(readOnly = true)
    public List<Event> findAllByIdIn(List<Integer> events) {
        return eventRepository.findAllByIdIn(events);
    }

    @Transactional(readOnly = true)
    public Page<EventResponseFullDto> findAllBySearch(String text,
                                                      List<Integer> categories,
                                                      Boolean paid,
                                                      Boolean onlyAvailable,
                                                      LocalDateTime rangeStart,
                                                      LocalDateTime rangeEnd,
                                                      EventController.EventSort sort,
                                                      Integer from,
                                                      Integer size,
                                                      HttpServletRequest httpServletRequest) {

        client.saveStatistic(
                HitRequestDto.builder()
                        .app(appName)
                        .ip(httpServletRequest.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .uri(httpServletRequest.getRequestURI())
                        .build());

        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("rangeEnd can not be before rangeStart");
            }
        }

        Sort sortValue = Sort.by("eventDate").descending();
        if (sort == EVENT_DATE) {
            sortValue = Sort.by("eventDate").descending();
        }

        PageRequest page = PageRequest.of(from / size, size, sortValue);

        return eventRepository.search(text, categories, paid, onlyAvailable, rangeStart, rangeEnd, page)
                .map(event -> EventMapper.toFullDto(event, null));
    }

    @Transactional
    public EventResponseFullDto patch(Integer eventId, EventRequestDto eventRequestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId));

        if (Objects.nonNull(eventRequestDto.getEventDate())) {
            event.setEventDate(eventRequestDto.getEventDate());
        }

        if (Objects.nonNull(eventRequestDto.getAnnotation())) {
            event.setAnnotation(eventRequestDto.getAnnotation());
        }

        if (Objects.nonNull(eventRequestDto.getRequestModeration())) {
            event.setRequestModeration(eventRequestDto.getRequestModeration());
        }

        if (Objects.nonNull(eventRequestDto.getCategory())) {
            Category cat = categoryRepository.findById(eventRequestDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + eventRequestDto.getCategory()));
            event.setCategory(cat);
        }

        if (Objects.nonNull(eventRequestDto.getPaid())) {
            event.setPaid(eventRequestDto.getPaid());
        }

        if (Objects.nonNull(eventRequestDto.getDescription())) {
            event.setDescription(eventRequestDto.getDescription());
        }

        if (Objects.nonNull(eventRequestDto.getTitle())) {
            event.setTitle(eventRequestDto.getTitle());
        }

        if (Objects.nonNull(eventRequestDto.getParticipantLimit())) {
            event.setParticipantLimit(eventRequestDto.getParticipantLimit());
        }

        if (Objects.nonNull(eventRequestDto.getLocation())) {
            if (eventRequestDto.getLocation().getLat() != null) {
                event.setLat(eventRequestDto.getLocation().getLat());
            }

            if (eventRequestDto.getLocation().getLon() != null) {
                event.setLon(eventRequestDto.getLocation().getLon());
            }
        }

        if (Objects.nonNull(eventRequestDto.getStateAction())) {
            if (event.getState() != Event.State.PENDING) {
                throw new IllegalArgumentException("For change event status - it must be in PENDING status");
            }

            Event.State state;
            if (eventRequestDto.getStateAction() == EventRequestDto.StateAction.PUBLISH_EVENT) {
                state = Event.State.PUBLISHED;
            } else if (eventRequestDto.getStateAction() == EventRequestDto.StateAction.REJECT_EVENT) {
                state = Event.State.CANCELED;
            } else {
                throw new IllegalArgumentException("Define behavior for status " + eventRequestDto.getStateAction());
            }
            event.setState(state);
        }

        Event updated = eventRepository.save(event);
        logger.info("Updated event - " + updated);

        return EventMapper.toFullDto(updated, null);
    }

    @Transactional
    public EventResponseFullDto patch(Integer userId, Integer eventId, EventRequestDto eventRequestDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventRepository.findByAuthorAndId(author, eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId));

        if (event.getState() == Event.State.PUBLISHED) {
            throw new IllegalArgumentException("Can not update published event");
        }

        if (Objects.nonNull(eventRequestDto.getEventDate())) {
            event.setEventDate(eventRequestDto.getEventDate());
        }

        if (Objects.nonNull(eventRequestDto.getAnnotation())) {
            event.setAnnotation(eventRequestDto.getAnnotation());
        }

        if (Objects.nonNull(eventRequestDto.getRequestModeration())) {
            event.setRequestModeration(eventRequestDto.getRequestModeration());
        }

        if (Objects.nonNull(eventRequestDto.getCategory())) {
            Category cat = categoryRepository.findById(eventRequestDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + eventRequestDto.getCategory()));
            event.setCategory(cat);
        }

        if (Objects.nonNull(eventRequestDto.getPaid())) {
            event.setPaid(eventRequestDto.getPaid());
        }

        if (Objects.nonNull(eventRequestDto.getDescription())) {
            event.setDescription(eventRequestDto.getDescription());
        }

        if (Objects.nonNull(eventRequestDto.getTitle())) {
            event.setTitle(eventRequestDto.getTitle());
        }

        if (Objects.nonNull(eventRequestDto.getParticipantLimit())) {
            event.setParticipantLimit(eventRequestDto.getParticipantLimit());
        }

        if (Objects.nonNull(eventRequestDto.getLocation())) {
            if (eventRequestDto.getLocation().getLat() != null) {
                event.setLat(eventRequestDto.getLocation().getLat());
            }

            if (eventRequestDto.getLocation().getLon() != null) {
                event.setLon(eventRequestDto.getLocation().getLon());
            }
        }

        if (Objects.nonNull(eventRequestDto.getStateAction())) {
            Event.State state;
            if (eventRequestDto.getStateAction() == EventRequestDto.StateAction.SEND_TO_REVIEW) {
                state = Event.State.PENDING;
            } else if (eventRequestDto.getStateAction() == EventRequestDto.StateAction.CANCEL_REVIEW) {
                state = Event.State.CANCELED;
            } else {
                throw new IllegalArgumentException("Define behavior for status " + eventRequestDto.getStateAction());
            }
            event.setState(state);
        }

        Event updated = eventRepository.save(event);
        logger.info("Updated event - " + event);

        return EventMapper.toFullDto(updated, null);
    }

    @Transactional
    public EventResponseFullDto post(Integer userId, EventRequestDto eventRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Category category = categoryRepository.findById(eventRequestDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + eventRequestDto.getCategory()));

        Event event = Event.builder()
                .author(user)
                .category(category)
                .annotation(eventRequestDto.getAnnotation())
                .eventDate(eventRequestDto.getEventDate())
                .description(eventRequestDto.getDescription())
                .lat(eventRequestDto.getLocation().getLat())
                .lon(eventRequestDto.getLocation().getLon())
                .paid(Objects.isNull(eventRequestDto.getPaid()) ? false : eventRequestDto.getPaid())
                .participantLimit(Objects.isNull(eventRequestDto.getParticipantLimit()) ? 0 : eventRequestDto.getParticipantLimit())
                .requestModeration(Objects.isNull(eventRequestDto.getRequestModeration()) ? true : eventRequestDto.getRequestModeration())
                .title(eventRequestDto.getTitle())
                .state(Event.State.PENDING)
                .created(LocalDateTime.now())
                .build();


        Event created = eventRepository.save(event);
        logger.info("Created event - " + created);

        return EventMapper.toFullDto(created, null);
    }
}
