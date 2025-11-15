FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN javac -cp /usr/local/lib/mysql-connector-j.jar server.java
CMD ["java", "-cp", ".:/usr/local/lib/mysql-connector-j.jar", "server"]
