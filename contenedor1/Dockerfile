FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN javac -cp /usr/local/lib/mysql-connector-j.jar contenedor1/server.java
CMD ["java", "-cp", ".:/usr/local/lib/mysql-connector-j.jar", "contenedor1.server"]