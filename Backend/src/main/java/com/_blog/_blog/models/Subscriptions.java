    package com._blog._blog.models;

    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.Table;

    @Entity
    @Table(name = "subscriptions")
    public class Subscriptions {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "subscriber_id", nullable = false)
        private User subscriber;

        @ManyToOne
        @JoinColumn(name = "subscribed_to_id", nullable = false)
        private User subscribedTo;

        public Subscriptions() {}

        public Subscriptions(User subscriber, User subscribedTo) {
            this.subscriber = subscriber;
            this.subscribedTo = subscribedTo;
        }

        public Long getId() {
            return id;
        }

        public User getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(User subscriber) {
            this.subscriber = subscriber;
        }

        public User getSubscribedTo() {
            return subscribedTo;
        }

        public void setSubscribedTo(User subscribedTo) {
            this.subscribedTo = subscribedTo;
        }
    }
