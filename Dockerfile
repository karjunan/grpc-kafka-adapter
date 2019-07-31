#FROM openjdk
FROM maven
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline
COPY . .
COPY application.properties .
RUN mvn -B -e -o -T 1C validate
RUN mvn -B package -DskipTests
RUN ls -ltr /usr/src/app


FROM openjdk
COPY --from=0 /usr/src/app/target/*.jar ./
COPY --from=0 /usr/src/app/target/application.properties .

#COPY java-grpc-server.jar .
#ENV CLASSPATH java-grpc-server.jar;
EXPOSE 8000
RUN java -version
RUN ls -ltr
CMD java -jar grpc-kafka-server-1.0-SNAPSHOT.jar --spring.config.location=file:///application.properties