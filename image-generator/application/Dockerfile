FROM eclipse-temurin:21.0.4_7-jdk
WORKDIR /app
COPY target/*.jar app.jar
RUN apt-get update && \
    apt-get install --assume-yes fontconfig && \
    rm -rf /var/lib/apt/lists/* /var/cache/debconf/*
CMD ["java", "-jar", "app.jar"]