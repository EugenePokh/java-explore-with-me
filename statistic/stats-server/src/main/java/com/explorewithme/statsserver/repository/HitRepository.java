package com.explorewithme.statsserver.repository;

import com.explorewithme.dto.HitCountResponse;
import com.explorewithme.statsserver.model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HitRepository extends JpaRepository<Hit, UUID> {

    @Query("select new com.explorewithme.dto.HitCountResponse(h.app, h.uri, count(h.id)) " +
            "from Hit h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(h.id) desc")
    List<HitCountResponse> findHitCounts(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);

    @Query("select new com.explorewithme.dto.HitCountResponse(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<HitCountResponse> findHitCountsUnique(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);


    @Query("select new com.explorewithme.dto.HitCountResponse(h.app, h.uri, count(h.id)) " +
            "from Hit h " +
            "where h.uri in :uris and h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(h.id) desc")
    List<HitCountResponse> findHitCountsByUris(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, @Param("uris") List<String> uris);

    @Query("select new com.explorewithme.dto.HitCountResponse(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit h " +
            "where h.uri in :uris and h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<HitCountResponse> findHitCountsByUrisUnique(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, @Param("uris") List<String> uris);
}
