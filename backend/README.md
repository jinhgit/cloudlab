# backend/ — CloudLab Platform API

Spring Boot 3.5 · Java 21 · package `com.cloudlab`

상세: [docs/backend.md](../docs/backend.md)

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
./gradlew test
./gradlew bootRun
curl -s http://localhost:8080/actuator/health
curl -s http://localhost:8080/api/health
```
