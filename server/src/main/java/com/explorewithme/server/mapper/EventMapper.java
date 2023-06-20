package com.explorewithme.server.mapper;

import com.explorewithme.server.dto.EventResponseFullDto;
import com.explorewithme.server.dto.EventResponseShortDto;
import com.explorewithme.server.dto.LocationDto;
import com.explorewithme.server.dto.UserEventResponseDto;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventMapper {

    public EventResponseShortDto toShortDto(Event event) {
        return EventResponseShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .confirmedRequests(event.getRequests() == null ?
                        0 : event.getRequests().stream().filter(request -> request.getState() == Request.State.CONFIRMED).count())
                .initiator(UserEventResponseDto.builder()
                        .id(event.getAuthor().getId())
                        .name(event.getAuthor().getName())
                        .build())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(0L)
                .build();
    }

    public EventResponseFullDto toFullDto(Event event, Long viewCount) {
        return EventResponseFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .createdOn(event.getCreated())
                .confirmedRequests(event.getRequests() == null ?
                        0 : event.getRequests().stream().filter(request -> request.getState() == Request.State.CONFIRMED).count())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserEventResponseDto.builder()
                        .id(event.getAuthor().getId())
                        .name(event.getAuthor().getName())
                        .build())
                .location(LocationDto.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .paid(event.getPaid())
                .publishedOn(event.getPublished())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(viewCount)
                .build();
    }
}
