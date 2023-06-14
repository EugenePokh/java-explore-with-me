package com.explorewithme.server.repository;

import com.explorewithme.server.model.Compilation;
import com.explorewithme.server.model.EventCompilation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventCompilationRepository extends JpaRepository<EventCompilation, Integer> {

    List<EventCompilation> findAllByCompilationAndEventIdIn(Compilation compilation, List<Integer> eventIds);
}
