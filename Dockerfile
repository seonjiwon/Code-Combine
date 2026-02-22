FROM amazoncorretto:17

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

RUN chmod +x gradlew

COPY src ./src

RUN ./gradlew bootJar -x test

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/code-combine-0.0.1-SNAPSHOT.jar"]