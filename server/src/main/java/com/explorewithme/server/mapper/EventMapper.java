package com.explorewithme.server.mapper;

import com.explorewithme.server.dto.EventResponseDto;
import com.explorewithme.server.dto.LocationDto;
import com.explorewithme.server.dto.UserResponseDto;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventMapper {

    public EventResponseDto toDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .createdOn(event.getCreated())
                .confirmedRequests(event.getRequests() == null ?
                        0 : event.getRequests().stream().filter(request -> request.getState() == Request.State.CONFIRMED).count())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserResponseDto.builder()
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
                .views(null) //todo
                .build();
    }
}
