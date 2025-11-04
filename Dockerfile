
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN javac contenedor2/Cliente.java
CMD ["java", "Cliente"]
