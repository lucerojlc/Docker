FROM eclipse-temurin:21-jdk

WORKDIR /app
<<<<<<< HEAD

# Descargar MySQL Connector
RUN apt-get update && apt-get install -y wget && \
    wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar -O mysql-connector-j.jar

# Copiar código
COPY server.java .

# Compilar SIN paquete
RUN javac -cp ".:mysql-connector-j.jar" server.java

# Ejecutar SIN paquete
CMD ["java", "-cp", ".:mysql-connector-j.jar", "server"]
=======
COPY . .
RUN javac -cp /usr/local/lib/mysql-connector-j.jar server.java
CMD ["java", "-cp", ".:/usr/local/lib/mysql-connector-j.jar", "server"]
>>>>>>> c86ab12b4a6e4fad13e7abbc383e990c1cf26f22
