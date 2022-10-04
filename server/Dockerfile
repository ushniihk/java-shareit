FROM eclipse-temurin:11
COPY target/shareit-server-0.0.1-SNAPSHOT.jar /app.jar
COPY db_scripts/chema.sql /docker-entrypoint-initdb.d/
EXPOSE 9090
ENTRYPOINT java -jar app.jar
