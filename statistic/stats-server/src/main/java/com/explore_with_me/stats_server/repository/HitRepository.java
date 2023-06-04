package com.explore_with_me.stats_server.repository;

import com.explore_with_me.stats_server.model.Hit;
import com.explore_with_me.stats_server.model.HitCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HitRepository extends JpaRepository<Hit, UUID> {

    @Query("select new com.explore_with_me.stats_server.model.HitCount(h.app, h.uri, count(h.id)) " +
            "from Hit h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri" +
            "order by count(h.id) desc")
    List<HitCount> findHitCounts(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);

    @Query("select new com.explore_with_me.stats_server.model.HitCount(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri" +
            "order by count(distinct h.ip) desc")
    List<HitCount> findHitCountsUnique(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);


    @Query("select new com.explore_with_me.stats_server.model.HitCount(h.app, h.uri, count(h.id)) " +
            "from Hit h " +
            "where h.uri in :uris and h.timestamp between :start and :end " +
            "group by h.app, h.uri" +
            "order by count(h.id) desc")
    List<HitCount> findHitCountsByUris(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, @Param("uris") List<String> uris);

    @Query("select new com.explore_with_me.stats_server.model.HitCount(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit h " +
            "where h.uri in :uris and h.timestamp between :start and :end " +
            "group by h.app, h.uri" +
            "order by count(distinct h.ip) desc")
    List<HitCount> findHitCountsByUrisUnique(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, @Param("uris") List<String> uris);
}
