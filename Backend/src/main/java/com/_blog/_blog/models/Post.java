// package com._blog._blog.models;

// import java.time.LocalDateTime;

// import org.hibernate.annotations.CreationTimestamp;

// import jakarta.persistence.Entity;
// import jakarta.persistence.ForeignKey;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "posts")
// public class Post {

//     @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private Long id;

//     // Assuming there's a User entity you're referencing
//     @ManyToOne
//     @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_post_author"))
//     private User author;

//     private String title;
//     private String content;
//     private String videoOrImageUrl;

//     @CreationTimestamp
//     private LocalDateTime createdAt;

//     // Constructors
//     public Post() {}

//     public Post(User author, String title, String content, String videoOrImageUrl) {
//         this.author = author;
//         this.title = title;
//         this.content = content;
//         this.videoOrImageUrl = videoOrImageUrl;
//     }

//     public Long getId() {
//         return id;
//     }

//     public String getTitle() {
//         return title;
//     }
//     public void setTitle(String title) {
//         this.title = title;
//     }
//     public String getContent() {
//         return content;
//     }
//     public void setContent(String content) {
//         this.content = content;
//     }
//     public User getAuthor() {
//         return author;
//     }
//     public void setAuthor(User author) {
//         this.author = author;
//     }
//     public String getVideoOrImageUrl() {
//         return videoOrImageUrl;
//     }
//     public void setVideoOrImageUrl(String videoOrImageUrl) {
//         this.videoOrImageUrl = videoOrImageUrl;
//     }
//     public LocalDateTime getCreatedAt() {
//         return createdAt;
//     }

// }
