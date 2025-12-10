# Auth

### Login

```js
POST /api/auth/login 
Content-Type : application/json
{login , password}
```

### Register 

```js
POST /api/auth/register
Content-Type : application/json
{username , email , password , age}
```

### Logout 

```js
GET /api/auth/logout
```

### Logged ?

```js
GET /api/auth/logged
```

# Posts

### Create post

```js
POST /api/posts
Content-Type : multipart/form-data
{title , content, file (not required)}
```

### Get posts

```js
GET /api/posts?page=0&size=10
```

### User Posts
```js
GET /api/posts/me?page=0&size=10
```

### Update Post 

```js
PUT /api/posts/{id}
Content-Type : multipart/form-data
{title , content, file}
```

### Delete Post 

```js
DELETE /api/posts/{id}
```

### Like Post 

```js
GET /api/posts/{postId}/like
```

# Comments

### Create Comment

```js
POST /api/comments
Content-Type : application/json
{postId , content}
```

### Get Comments

```js
GET /api/comments?page=0&size=10
```


### Delete Comment

```js
DELETE /api/comments/{id}
```

### Like Comment

```js
GET /api/comments/{commentId}/like
```


