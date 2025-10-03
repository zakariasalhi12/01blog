// package com._blog._blog.models;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "subscriptions")

// public class subscriptions {

//     @Id
//     @GeneratedValue(strategy= GenerationType.AUTO)
//     private Long id;

//     @ManyToOne
//     private User subscriberId;

//     @ManyToOne
//     private User subscribedToId;

//     public subscriptions() {}

//     public subscriptions(User subscriberId, User subscribedToId) {
//         this.subscriberId = subscriberId;
//         this.subscribedToId = subscribedToId;
//     }

//     public Long getId() {
//         return id;
//     }

//     public User getSubscriberId() {
//         return subscriberId;
//     }

//     public void setSubscriberId(User subscriberId) {
//         this.subscriberId = subscriberId;
//     }

//     public User getSubscribedToId() {
//         return subscribedToId;
//     }

//     public void setSubscribedToId(User subscribedToId) {
//         this.subscribedToId = subscribedToId;
//     }
// }
