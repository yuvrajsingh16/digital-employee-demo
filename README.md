# Widgets Service

A small Spring Boot service used as the proving ground for the **Dezifi Digital Employee**. It has a
green test suite (`mvn test`) and a [BACKLOG](BACKLOG.md) of tickets the digital engineer implements —
each becomes a Jira ticket assigned to the employee, which plans it, codes it in a sandbox, runs the
tests, and opens a pull request. **The employee never merges — a human does.**

## Run
```bash
mvn spring-boot:run   # http://localhost:8080/widgets
mvn test              # the suite the coder must keep green
```

## API
- `GET /widgets` — list
- `GET /widgets/{id}` — fetch one (404 if unknown)
- `POST /widgets` — create `{ "name": "...", "quantity": N }`
