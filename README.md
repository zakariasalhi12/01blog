 # 01blog
 is a fullstack social blogging platform designed for students to document and share their learning experiences, discoveries, and progress. The platform fosters a collaborative environment where users can interact with content, follow peers, and engage in meaningful educational discussions.
 Built with modern technologies, 01Blog combines a robust Java Spring Boot backend with a dynamic Angular frontend to deliver a seamless user experience.

### Technologies
- Backend :
    - Java Spring Boot
- Frontend :
    - Angular
- Database :
    - PostgreSQL
- Authentication :
    - JWT

### features
- Authentication :
    - User registration, login, and secure password handling.
    - Role-based access control (user vs admin).
- User Block Page :
    - Each user has a public profile (their "block") listing all their posts.
    - Users can subscribe to other profiles.
    - Subscribed users receive notifications when new posts are published.
- Posts :
    - Users can create/edit/delete posts with media (image or video) and text.
    - Each post includes a timestamp, description, and media preview.
    - Other users can like and comment on posts.
- Reports :
    - Users can report profiles for inappropriate or offensive content.
    - Reports must include a reason and timestamp.
    - Reports are stored and visible only to admins.
- Admin Panel :
    - Admin can view and manage all users.
    - Admin can manage posts and remove inappropriate content.
    - Admin can handle user reports (ban/delete user or post).
    - All admin routes must be protected by access control.

# Installation
### 1 - Clone the repository
```bash
    git clone https://github.com/zakariasalhi12/01blog.git
    cd 01blog
```
### 2 - Backend Setup
```bash
    cd Backend
    ./mvnw spring-boot:run
```
### 3 - Frontend Setup
```bash
    cd Frontend
    npm install
    ng serve
```

### Docker compose Setup
```bash
    docker compose up -d
```
