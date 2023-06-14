package com.explorewithme.server.service;

import com.explorewithme.server.model.Compilation;
import com.explorewithme.server.model.EventCompilation;
import com.explorewithme.server.repository.EventCompilationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventCompilationService {
    private final EventCompilationRepository eventCompilationRepository;


    public void saveAll(List<EventCompilation> eventCompilations) {
        eventCompilationRepository.saveAll(eventCompilations);
    }

    public List<EventCompilation> findAllByCompilationAndEventIdIn(Compilation compilation, List<Integer> events) {
        return eventCompilationRepository.findAllByCompilationAndEventIdIn(compilation, events);
    }
}
