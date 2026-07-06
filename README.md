# PlacementPrep

A placement aptitude practice platform (like IndiaBix / PrepInsta) — Spring Boot + MySQL backend, Bootstrap + vanilla JS frontend.

## What's included (Phase 1 — working core)

- **Auth**: Register/Login with JWT + BCrypt password hashing
- **Dashboard**: total tests, highest score, average score, accuracy, streak, recent activity
- **16 aptitude categories**, difficulty levels (Easy/Medium/Hard), company tags
- **Quiz engine**: random questions, timer with auto-submit, question palette, bookmark, skip, previous/next
- **Result page**: score, percentage, correct/wrong/skipped, accuracy, time taken, Chart.js breakdown, answer review with explanations
- **Profile**: view/update details, change password
- **Admin panel**: full CRUD on questions
- **Leaderboard API**: `/api/leaderboard`
- **Dark/light theme toggle**

## Not yet included (Phase 2 — you can extend)

- CSV bulk import for questions
- Certificate generation / PDF result download
- Email verification & OTP-based forgot password
- Full analytics dashboard with charts for admin
- Negative marking

These are straightforward to add on top of this structure — the `QuestionController` and `ResultController` are natural places to extend.

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+
- A code editor (IntelliJ IDEA recommended for backend, VS Code for frontend)

## 1. Database Setup

1. Start MySQL and create the database (or let Hibernate auto-create it — see below):
   ```sql
   CREATE DATABASE placementprep_db;
   ```
2. Optionally run `database/schema.sql` to create tables and seed sample questions manually.
   Otherwise, Spring Boot's `spring.jpa.hibernate.ddl-auto=update` will auto-create tables on first run —
   in that case just insert a few rows into `questions` yourself, or use the Admin Panel once you've made
   your own user an ADMIN (see step 4 below).

## 2. Backend Setup

```bash
cd backend
```

Edit `src/main/resources/application.properties`:
- `spring.datasource.username` / `spring.datasource.password` — your MySQL credentials
- `app.jwt.secret` — change to a long random string before deploying anywhere real

Run it:
```bash
mvn spring-boot:run
```
The API will start on **http://localhost:8080**.

## 3. Frontend Setup

The frontend is plain HTML/CSS/JS — no build step needed.

- Open the `frontend` folder in VS Code and use the **Live Server** extension (serves on `http://localhost:5500` by default), or
- Just open `frontend/index.html` directly in a browser.

If you serve it on a different port, update `app.cors.allowed-origins` in `application.properties` to match, and restart the backend.

The frontend talks to the backend via `API_BASE_URL` in `frontend/js/api.js` — currently set to `http://localhost:8080/api`.

## 4. Making yourself an Admin

Register normally through the app, then in MySQL run:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';
```
Log out and log back in (so a fresh JWT with the ADMIN role is issued), then visit `frontend/admin.html`.

## 5. Deployment (as planned)

- **Frontend** → Netlify (drag-and-drop the `frontend` folder, or connect a Git repo)
- **Backend** → Render or Railway (deploy the Spring Boot app; set the same env vars as `application.properties`)
- **Database** → any managed MySQL instance (Railway MySQL, PlanetScale, AWS RDS free tier, etc.)

Remember to update `API_BASE_URL` in `js/api.js` to your deployed backend URL, and `app.cors.allowed-origins`
in `application.properties` to your deployed frontend URL, before going live.

## Project Structure

```
PlacementPrep/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/placementprep/
│       ├── controller/    → REST endpoints
│       ├── service/       → business logic
│       ├── repository/    → Spring Data JPA repositories
│       ├── entity/        → JPA entities (User, Question, Result, Bookmark)
│       ├── dto/           → request/response DTOs
│       ├── config/        → Spring Security config
│       ├── security/      → JWT filter, JWT util, UserDetailsService
│       └── exception/     → global exception handling
├── frontend/
│   ├── index.html, login.html, register.html
│   ├── dashboard.html, quiz.html, result.html
│   ├── profile.html, admin.html
│   ├── css/style.css
│   └── js/ (api.js, auth.js, dashboard.js, quiz.js, result.js)
└── database/schema.sql
```

## Key REST APIs

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register new user |
| POST | `/api/auth/login` | No | Login, returns JWT |
| GET | `/api/dashboard` | Yes | Dashboard stats |
| GET | `/api/quiz/questions?category=&difficulty=&limit=` | Yes | Get quiz questions (answers hidden) |
| POST | `/api/quiz/submit` | Yes | Submit answers, get scored result |
| GET | `/api/results/my` | Yes | Your past results |
| GET | `/api/leaderboard` | Yes | Top scores across all users |
| GET/PUT | `/api/profile` | Yes | View/update profile |
| PUT | `/api/profile/password` | Yes | Change password |
| GET/POST/PUT/DELETE | `/api/admin/questions` | Admin only | Question CRUD |
| GET | `/api/questions/public/categories` | No | Browse category list |

## Notes on portfolio value

This is a good SDE-portfolio project because it demonstrates: JWT auth + Spring Security, layered
architecture (entity/dto/repo/service/controller), a real MySQL schema with relationships, and a
polished frontend consuming a REST API — exactly the kind of full-stack proof-of-work startups
(Zoho, Freshworks, Chargebee) look for. Consider adding 2-3 GitHub commits showing incremental
feature additions (Phase 2 items above) rather than one giant commit — it reads better in interviews.
