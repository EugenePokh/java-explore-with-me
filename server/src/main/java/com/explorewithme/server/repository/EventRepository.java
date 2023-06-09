package com.explorewithme.server.repository;

import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Page<Event> findAllByAuthor(User author, Pageable pageable);

    Optional<Event> findByAuthorAndId(User user, Integer id);

    @Query("select e from Event e " +
            "where (e.state in :states or :states is null) " +
            "and (e.author.id in :userIds or :userIds is null) " +
            "and (e.category.id in :categoryIds or :categoryIds is null) " +
            "and ( (e.eventDate between :rangeStart and :rangeEnd) or (cast(:rangeStart as timestamp) is null or cast(:rangeEnd as timestamp) is null) ) " +
            "order by e.eventDate desc")
    Page<Event> search(@Param("userIds") List<Integer> users,
                       @Param("states") List<Event.State> states,
                       @Param("categoryIds") List<Integer> categories,
                       @Param("rangeStart") LocalDateTime rangeStart,
                       @Param("rangeEnd") LocalDateTime rangeEnd,
                       Pageable pageable);


    List<Event> findAllByIdIn(List<Integer> events);

    @Query("select e from Event e " +
            "where (e.category.id in :categoryIds or :categoryIds is null) " +
            "and (:text = '' or :text is null or lower(e.annotation) like lower(concat('%', :text,'%')) or lower(e.description) like lower(concat('%', :text,'%'))) " +
            "and (e.paid = :paid or :paid is null) " +
            "and ((:onlyAvailable = true and e.participantLimit < e.requests.size) or (:onlyAvailable = false) or :paid is null) " +
            "and ( (e.eventDate between :rangeStart and :rangeEnd) or (cast(:rangeStart as timestamp) is null or cast(:rangeEnd as timestamp) is null) ) " +
            "order by e.eventDate desc")
    Page<Event> search(@Param("text") String text,
                       @Param("categoryIds") List<Integer> categories,
                       @Param("paid") Boolean paid,
                       @Param("onlyAvailable") Boolean onlyAvailable,
                       @Param("rangeStart") LocalDateTime rangeStart,
                       @Param("rangeEnd") LocalDateTime rangeEnd,
                       Pageable page);

    Optional<Event> findByIdAndState(Integer eventId, Event.State state);
}
