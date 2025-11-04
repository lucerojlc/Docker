FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
COPY mysql-connector-j-9.0.0.jar /usr/local/lib/mysql-connector-j.jar
RUN javac -cp .:/usr/local/lib/mysql-connector-j.jar contenedor1/server.java
CMD ["java", "-cp", ".:/usr/local/lib/mysql-connector-j.jar", "contenedor1.server"]