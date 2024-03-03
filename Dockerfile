FROM openjdk:17-jdk

WORKDIR /avni

# build
COPY . ./
RUN ./gradlew clean build -x test

# expose port
EXPOSE 8081

# run
CMD ["java", "-jar", "./build/libs/etl-1.0.0-SNAPSHOT.jar"]


