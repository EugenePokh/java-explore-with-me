package com.explorewithme.server.repository;

import com.explorewithme.server.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    @Query("select c from Compilation c where c.pinned = :pinned or :pinned is null")
    Page<Compilation> findByPinned(@Param("pinned") Boolean pinned, Pageable pageable);

}
