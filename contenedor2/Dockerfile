FROM eclipse-temurin:21-jdk
<<<<<<< HEAD
=======

WORKDIR /app

COPY Cliente.java .

RUN javac Cliente.java

CMD ["java", "Cliente"]

>>>>>>> c86ab12b4a6e4fad13e7abbc383e990c1cf26f22

WORKDIR /app

# Copiar código
COPY Cliente.java .

# Compilar SIN paquete
RUN javac Cliente.java

# Ejecutar SIN paquete
CMD ["java", "Cliente"]