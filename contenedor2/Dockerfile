FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar código
COPY Cliente.java .

# Compilar SIN paquete
RUN javac Cliente.java

# Ejecutar SIN paquete
CMD ["java", "Cliente"]