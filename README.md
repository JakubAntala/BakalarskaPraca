# Bachelor's Thesis — Spring Boot Web Application

> Full-stack web application built as the practical part of my Bachelor's thesis at the **Department of Informatics, Constantine the Philosopher University in Nitra (UKF)**.

## About the project

This application demonstrates a complete server-rendered web stack: a **Java Spring Boot** back-end with **JSP** views, persisted through a relational database. The codebase covers the typical layers a production-style application needs — controllers, services, persistence, and templated views — and was developed as part of my Bachelor's thesis defense.

## Tech stack

- **Language:** Java
- **Framework:** Spring Boot
- **View layer:** JSP (JavaServer Pages)
- **Database:** Relational (SQL schema included in `Import a návrh databázy/`)
- **Build:** Maven (via Spring Boot project structure)

## Repository contents

```
├── Bakalárska práca/           # Thesis text and documentation
├── Import a návrh databázy/    # Database schema and import scripts
├── JavaServer Pages/           # JSP view templates
├── Prihlasovacie údaje/        # Login credentials info (local/dev)
└── Spring Boot.zip             # Spring Boot application source
```

## Getting started

1. Unzip `Spring Boot.zip` to your working directory.
2. Import the database schema from `Import a návrh databázy/` into your local MySQL/MariaDB instance.
3. Configure `application.properties` with your local DB credentials.
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Open `http://localhost:8080` in your browser.

## What I learned

- Designing and implementing a relational data model from scratch
- Structuring a Spring Boot application across controllers, services, and repositories
- Server-side rendering with JSP and request handling end-to-end
- Writing and defending a technical document (the thesis itself)

## Author

**Jakub Antala** — Applied Informatics, UKF Nitra
📧 jakub.antala10@gmail.com

---

<sub>🇸🇰 <em>Bakalárska práca — full-stack webová aplikácia v jazyku Java na frameworku Spring Boot s JSP pohľadmi a relačnou databázou. Repozitár obsahuje text práce, návrh databázy aj zdrojové kódy aplikácie.</em></sub>
