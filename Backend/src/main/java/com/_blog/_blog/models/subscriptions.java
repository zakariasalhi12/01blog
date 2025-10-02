package com._blog._blog.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscriptions")

public class subscriptions {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long subscriberId;
    private Long subscribedToId;

    public subscriptions() {}

    public subscriptions(Long subscriberId, Long subscribedToId) {
        this.subscriberId = subscriberId;
        this.subscribedToId = subscribedToId;
    }

    public Long getId() {
        return id;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public Long getSubscribedToId() {
        return subscribedToId;
    }

    public void setSubscribedToId(Long subscribedToId) {
        this.subscribedToId = subscribedToId;
    }
}
