package com.explorewithme.server.service;

import com.explorewithme.server.dto.CommentRequestDto;
import com.explorewithme.server.dto.CommentResponseDto;
import com.explorewithme.server.exception.CommentNotFoundException;
import com.explorewithme.server.exception.CommentValidationException;
import com.explorewithme.server.exception.EventNotFoundException;
import com.explorewithme.server.exception.UserNotFoundException;
import com.explorewithme.server.mapper.CommentMapper;
import com.explorewithme.server.model.Comment;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import com.explorewithme.server.model.User;
import com.explorewithme.server.repository.CommentRepository;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentResponseDto post(Integer userId,
                                   CommentRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + dto.getEventId()));

        event.getRequests()
                .stream()
                .filter(request -> request.getState() == Request.State.CONFIRMED)
                .map(Request::getRequester)
                .filter(author -> Objects.equals(author.getId(), userId))
                .findFirst()
                .orElseThrow(() -> new CommentValidationException("Cannot comment event without confirmed request"));

        Comment.State state = Comment.State.PUBLISHED;
        if (event.getCommentModeration()) {
            state = Comment.State.PENDING;
        }

        Comment comment = Comment.builder()
                .user(user)
                .event(event)
                .text(dto.getText())
                .state(state)
                .created(LocalDateTime.now())
                .build();

        Comment created = commentRepository.save(comment);

        logger.info("Created comment - " + created);

        return CommentMapper.toDto(created);
    }

    @Transactional
    public CommentResponseDto patch(Integer commentId,
                                    CommentRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("No such comment with comment id - " + commentId));

        if (comment.getState() != Comment.State.PENDING) {
            throw new CommentValidationException("Can update only pending comment");
        }

        if (Objects.nonNull(dto.getText())) {
            comment.setText(dto.getText());
        }

        if (dto.getStateAction() == CommentRequestDto.StateAction.REJECT_COMMENT) {
            comment.setState(Comment.State.REJECTED);
        } else if (dto.getStateAction() == CommentRequestDto.StateAction.PUBLISH_COMMENT) {
            comment.setState(Comment.State.PUBLISHED);
        }

        Comment updated = commentRepository.save(comment);

        logger.info("Updated comment - " + updated);

        return CommentMapper.toDto(updated);
    }

    @Transactional
    public CommentResponseDto patch(Integer userId,
                                    Integer commentId,
                                    CommentRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));

        Comment comment = commentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new CommentNotFoundException("No such comment with user id - " + userId + " and comment id - " + commentId));

        if (comment.getState() == Comment.State.REJECTED) {
            throw new CommentValidationException("Can not comment rejected comment");
        }

        if (Objects.nonNull(dto.getText())) {
            comment.setText(dto.getText());
        }

        if (comment.getEvent().getCommentModeration()) {
            comment.setState(Comment.State.PENDING);
        }

        Comment updated = commentRepository.save(comment);

        logger.info("Updated comment - " + updated);

        return CommentMapper.toDto(updated);
    }

    @Transactional
    public void delete(Integer userId, Integer commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Comment comment = commentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new CommentNotFoundException("No such comment with user id - " + userId + " and comment id - " + commentId));
        commentRepository.delete(comment);
        logger.info("Deleted comment - " + comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> findByEventId(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId));

        return commentRepository.findByEventAndStateIn(event, Arrays.asList(Comment.State.PUBLISHED))
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

    }
}
