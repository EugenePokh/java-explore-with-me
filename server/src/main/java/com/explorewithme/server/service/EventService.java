package com.explorewithme.server.service;

import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.User;
import com.explorewithme.server.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventRepository eventRepository;

    public Page<Event> findAllByAuthor(User author, PageRequest page) {
        return eventRepository.findAllByAuthor(author, page);
    }

    public Event save(Event event) {
        logger.info("Save event - " + event);
        return eventRepository.save(event);
    }

    public Optional<Event> findByAuthorAndId(User author, Integer eventId) {
        return eventRepository.findByAuthorAndId(author, eventId);
    }

    public Page<Event> findAllBySearch(List<Integer> users,
                                       List<Event.State> states,
                                       List<Integer> categories,
                                       LocalDateTime rangeStat,
                                       LocalDateTime rangeEnd,
                                       PageRequest page) {
        return eventRepository.findAllBySearch(users, states, categories, //rangeStat, rangeEnd,
                page);
    }

    public Optional<Event> findById(Integer eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> findAllByIdIn(List<Integer> events) {
        return eventRepository.findAllByIdIn(events);
    }

    public Page<Event> findAllBySearch(String text, List<Integer> categories, Boolean paid, Boolean onlyAvailable, PageRequest page) {
        return eventRepository.findAllBySearch(text, categories, paid, onlyAvailable, page);
    }
}
