FROM openjdk
WORKDIR /usr/src/app
COPY java-grpc-server.jar .
#ENV CLASSPATH java-grpc-server.jar;
EXPOSE 8000
RUN java -version
RUN ls -ltr
CMD java -jar java-grpc-server.jar