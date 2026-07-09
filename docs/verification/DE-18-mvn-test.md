# DE-18 — `mvn test` verification

This repository includes GitHub Actions enforcement to run the Maven test suite on every `push` and `pull_request`.

## CI enforcement

Workflow: `.github/workflows/ci.yml`

- Runner: `ubuntu-latest`
- Java: Temurin 17 via `actions/setup-java@v4`
- Maven cache: enabled via `setup-java` built-in caching
- Command: `mvn -q test`

## Jansi `/tmp noexec` warning

In some containerized environments, `/tmp` can be mounted with the `noexec` flag. Jansi (used by Maven for console features) tries to extract a native library into `java.io.tmpdir`, which causes a noisy warning like:

> `Failed to load native library ... The native library file at /tmp/... is not executable ... or set the jansi.tmpdir system property`

This warning is non-fatal but confusing in CI logs. To avoid it, CI sets:

- `MAVEN_OPTS=-Djansi.tmpdir=$GITHUB_WORKSPACE/target/tmp`

in `.github/workflows/ci.yml`.

Note: `pom.xml` also configures Surefire with `-Djansi.tmpdir=target/tmp` so the test JVM inherits the same setting.

## Last successful local run

Environment (sandbox):

- OS: Linux (container)
- Java: 17
- Maven: 3.9.x

Command:

```bash
mvn -q test
```

Output (`mvn -q test`):

```text
16:35:54.817 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.example.widgets.WidgetControllerTest]: WidgetControllerTest does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
16:35:54.865 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.example.widgets.WidgetsApplication for test class com.example.widgets.WidgetControllerTest

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\
( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.5)

2026-07-09T16:35:54.981Z  INFO 1543 --- [           main] c.example.widgets.WidgetControllerTest   : Starting WidgetControllerTest using Java 17.0.14 with PID 1543 (started by agent in /work/repo)
2026-07-09T16:35:54.981Z  INFO 1543 --- [           main] c.example.widgets.WidgetControllerTest   : No active profile set, falling back to 1 default profile: "default"
2026-07-09T16:35:55.452Z  INFO 1543 --- [           main] o.s.b.t.m.w.SpringBootMockServletContext : Initializing Spring TestDispatcherServlet ''
2026-07-09T16:35:55.452Z  INFO 1543 --- [           main] o.s.t.web.servlet.TestDispatcherServlet  : Initializing Servlet ''
2026-07-09T16:35:55.452Z  INFO 1543 --- [           main] o.s.t.web.servlet.TestDispatcherServlet  : Completed initialization in 0 ms
2026-07-09T16:35:55.458Z  INFO 1543 --- [           main] c.example.widgets.WidgetControllerTest   : Started WidgetControllerTest in 0.56 seconds (process running for 0.934)
```

Note: this local container still prints a non-fatal Jansi `/tmp noexec` warning to `stderr` when Maven starts. CI sets `MAVEN_OPTS` so the warning should not appear in GitHub Actions logs.
