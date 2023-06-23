package com.explorewithme.server.service;

import com.explorewithme.server.dto.CompilationRequestDto;
import com.explorewithme.server.dto.CompilationResponseDto;
import com.explorewithme.server.exception.CompilationNotFoundException;
import com.explorewithme.server.mapper.CompilationMapper;
import com.explorewithme.server.model.Compilation;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.repository.CompilationRepository;
import com.explorewithme.server.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CompilationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventRepository eventRepository;

    private final CompilationRepository compilationRepository;

    @Transactional(readOnly = true)
    public Page<CompilationResponseDto> findAllByPinned(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return compilationRepository.findByPinned(pinned, page)
                .map(CompilationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CompilationResponseDto findById(Integer compilationId) {
        return CompilationMapper.toDto(
                compilationRepository.findById(compilationId)
                        .orElseThrow(() -> new CompilationNotFoundException("No such category with id - " + compilationId)));
    }

    @Transactional
    public CompilationResponseDto post(CompilationRequestDto dto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());

        if (dto.getEvents() != null) {
            List<Event> eventCompilations = eventRepository.findAllByIdIn(dto.getEvents());
            compilation.setEvents(new HashSet<>(eventCompilations));
        }
        Compilation created = compilationRepository.save(compilation);
        logger.info("Created compilation - " + created);

        return CompilationMapper.toDto(created);
    }

    @Transactional
    public CompilationResponseDto patch(Integer compilationId, CompilationRequestDto dto) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("No such category with id - " + compilationId));

        if (Objects.nonNull(dto.getEvents())) {
            List<Event> eventCompilations;
            if (dto.getEvents().isEmpty()) {
                eventCompilations = new ArrayList<>();
            } else {
                eventCompilations = eventRepository.findAllByIdIn(dto.getEvents());
            }
            compilation.setEvents(new HashSet<>(eventCompilations));
        }

        if (Objects.nonNull(dto.getTitle())) {
            compilation.setTitle(dto.getTitle());
        }

        if (Objects.nonNull(dto.getPinned())) {
            compilation.setPinned(dto.getPinned());
        }

        logger.info("Updated compilation - " + compilation);

        return CompilationMapper.toDto(compilation);
    }

    @Transactional
    public void deleteById(Integer compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("No such category with id - " + compilationId));

        compilationRepository.delete(compilation);
        logger.info("Deleted compilation - " + compilation);
    }
}
