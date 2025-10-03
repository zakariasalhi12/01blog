// package com._blog._blog.models;

// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "likes")
// public class likes {
    
//     public enum Type {
//         LIKE,
//         DISLIKE
//     }

//     public enum TargetType {
//         POST,
//         COMMENT
//     }

//     @Id
//     @GeneratedValue(strategy= GenerationType.AUTO)
//     private Long id;

//     // foreign keys

//     @ManyToOne
//     private User userId;

//     @ManyToOne
//     private User targetId;

//     @Enumerated(EnumType.STRING)
//     private TargetType targetType;

//     @Enumerated(EnumType.STRING)
//     private Type type;


//     public likes() {}

//     public likes(User userId, User targetId, TargetType targetType, Type type) {
//         this.userId = userId;
//         this.targetId = targetId;
//         this.targetType = targetType;
//         this.type = type;
//     }

//     public Long getId() {
//         return id;
//     }

//     public User getUserId() {
//         return userId;
//     }

//     public void setUserId(User userId) {
//         this.userId = userId;
//     }

//     public User getTargetId() {
//         return targetId;
//     }

//     public void setTargetId(User targetId) {
//         this.targetId = targetId;
//     }

//     public TargetType getTargetType() {
//         return targetType;
//     }

//     public void setTargetType(TargetType targetType) {
//         this.targetType = targetType;
//     }

//     public Type getType() {
//         return type;
//     }

//     public void setType(Type type) {
//         this.type = type;
//     }
// }
