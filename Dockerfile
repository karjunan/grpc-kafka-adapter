#FROM openjdk
FROM maven
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline
COPY . .
RUN mvn -B -e -o -T 1C validate

FROM openjdk
COPY --from=0 /usr/src/app/target/*.jar ./

#COPY java-grpc-server.jar .
#ENV CLASSPATH java-grpc-server.jar;
EXPOSE 8000
RUN java -version
RUN ls -ltr
CMD java -jar grpc-kafka-server-1.0-SNAPSHOT.jar