# University Management System

Object-Oriented Programming final project: a console-based university management system with authentication, role-based menus, academic workflows, research features, news, support requests, logs, and persistent storage.

## Team

| Name | Role |
| --- | --- |
| Aruzhan | Team Lead |
| Altynai | Member |
| Daulet | Member |

## Tech Stack

- Java
- Console UI
- Object serialization for data persistence
- OOP principles and design patterns

## How to Run

Compile the project:

```bash
javac -cp src -d out $(find src -name "*.java")
```

Run the application:

```bash
java -cp out system.Main
```

Default admin account:

```text
username: admin
password: admin
```

## Data Storage

The application saves data in:

```text
university.ser
```

Admin action logs can be exported from the admin menu.

## Main Roles

- Admin
- Manager
- Teacher
- Student
- Graduate Student
- Tech Support Specialist

## Main Features

- Authentication and role-based console routing
- Admin user management
- Course creation and teacher assignment
- Course registration requests with manager approval
- Student transcript, marks, schedule, attendance, and teacher rating
- Graduate student research menu, supervisor view, and diploma projects
- Teacher grade and attendance management
- News and comments
- Research papers, journals, citations, and h-index
- Tech support requests with statuses
- Official university messages
- Search by users, courses, and news
- Interface language switching
- Persistent save/load through `UniversityDataStore`

## Project Structure

```text
src/
  comparators/        custom comparators
  data/               persistent data store
  demo/               demo data seeding
  enums/              system enums
  exceptions/         custom exceptions
  i18n/               localization
  models/             domain models
  pattern/            design pattern implementations
  services/           business logic
  system/             application entry point
  ui/                 console user interface
```

## Design Patterns

- Factory: user creation
- Observer: journal/news style notifications
- Strategy: sorting research papers
- Decorator: researcher-related extension

## Notes

This project separates console UI from business logic. UI classes collect user input and display results, while services handle validation, persistence, and system behavior.
