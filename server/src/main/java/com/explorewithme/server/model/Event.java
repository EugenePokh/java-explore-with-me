package com.explorewithme.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column
    private Double lat;

    @Column
    private Double lon;

    @Column
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "req_moderation")
    private Boolean requestModeration;

    @Column
    private String title;

    @Enumerated(EnumType.STRING)
    @Column
    private State state;

    @Column
    private LocalDateTime created;

    @Column
    private LocalDateTime published;

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private List<Request> requests;

    public enum State {
        PENDING,
        PUBLISHED,
        CANCELED
    }
}
