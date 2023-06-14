package com.explorewithme.server.service;

import com.explorewithme.server.dto.CompilationRequestDto;
import com.explorewithme.server.model.Compilation;
import com.explorewithme.server.model.EventCompilation;
import com.explorewithme.server.repository.CompilationRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventService eventService;
    private final EventCompilationService eventCompilationService;

    private final CompilationRepository compilationRepository;

    public Page<Compilation> findAllByPinned(Boolean pinned, PageRequest page) {
        return compilationRepository.findAllByPinned(pinned, page);
    }

    public Optional<Compilation> findById(Integer compilationId) {
        return compilationRepository.findById(compilationId);
    }

    public Compilation create(CompilationRequestDto dto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());
        compilationRepository.save(compilation);

        List<EventCompilation> eventCompilations = eventService.findAllByIdIn(dto.getEvents())
                .stream()
                .map(event -> {
                    EventCompilation eventCompilation = new EventCompilation();
                    eventCompilation.setEvent(event);
                    eventCompilation.setCompilation(compilation);
                    return eventCompilation;
                })
                .collect(Collectors.toList());
        eventCompilationService.saveAll(eventCompilations);

        logger.info("Create compilation - " + compilation);
        return compilationRepository.findById(compilation.getId())
                .get();
    }

    public void delete(Compilation compilation) {
        logger.info("Delete compilation - " + compilation);
        compilationRepository.delete(compilation);
    }

    public Compilation save(Compilation compilation) {
        logger.info("Save compilation - " + compilation);
        return compilationRepository.save(compilation);
    }
}
