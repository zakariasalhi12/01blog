package com._blog._blog.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "subscriptions")
public class Subscriptions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User subscriber;

    @ManyToOne
    @JoinColumn(name = "subscribed_to_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User subscribedTo;

    public Subscriptions(User subscriber, User subscribedTo) {
        this.subscriber = subscriber;
        this.subscribedTo = subscribedTo;
    }
}
