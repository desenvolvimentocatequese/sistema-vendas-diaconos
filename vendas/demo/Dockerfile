# Build: use Root Directory = vendas/demo no Render (ou contexto apontando para esta pasta).
# O Render define PORT; o Spring usa server.port=${PORT:9090} em application.properties.

FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /app/target/demo-*.jar /app/app.jar
RUN chown -R app:app /app

USER app
EXPOSE 9090

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
