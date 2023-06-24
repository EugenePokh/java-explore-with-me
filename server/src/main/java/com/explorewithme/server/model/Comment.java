package com.explorewithme.server.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column
    private String text;

    @Enumerated(EnumType.STRING)
    @Column
    private Comment.State state;

    @Column
    private LocalDateTime created;

    public enum State {
        PENDING,
        PUBLISHED,
        REJECTED
    }
}

