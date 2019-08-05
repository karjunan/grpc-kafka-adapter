<!-- <p align="center">
  <a href="https://www.pickles.com.au/" target="blank"><img src="https://upload.wikimedia.org/wikipedia/commons/4/48/Pickles-Logo.jpg" width="320" alt="Pickles Logo" /></a>
</p> -->

![Pickles Logo](/images/logo.jpg)

# Reference Grpc-Kafka-Adapter powered by Java

## Table of Contents

- [Introduction](#Introduction)
- [Why use gRPC](#Why use GRPC)
- [Functional dependencies](#Functional dependencies)


## Introduction
This documentation provides a basic Java programmer’s introduction to working with gRPC server and how to build services 
to connect to Kafka.

By walking through this example you’ll learn how to:

1. Define a service in a .proto file.
1. Generate server code using the protocol buffer compiler.
1. Generate Avro files using Avro plugins.
1. Use the Java gRPC API to write two simple services which would produce and consume messages from Kafka

## Why use GRPC

   The microservices are build using Node js. Nodejs libraries which connect to kafka dosent support some features provided by node client 
   libraries. Eg ( Transcational , Idempotent producer, Excatly once semantics and some features related to streaming api's). The core features which are mentioned above will be implemented in java using grpc server and will be exposed as api to node microservices. The node microservices would use the apis to invoke the produce and consume functionalities. Also some of the complex streaming functionalities which node js dosent support can also be introduced in Grpc service and can be exposed to node microserivce using grpc streaming apis. 
   
   
## Functional dependencies
 
 1. Java programming language will be used to build the functionalities.
 1. Spring boot and Spring Kafka(spring-kafka) will be used as the framework to abstract the business logic.
 1. Junit, Mockito and spring kafa test will be using for testing the application. 
 1. Slf4j will be used for log abstraction and Microsoft azure logging will provide the implemention.
 1. Maven will be used a build toold
 1. protobuf-maven-plugin will be used for generating the java code based .proto files
 1. avro-maven-plugin will be used for generating the java code for avro files. This will be used by the schema registry and will be handling
 the kafka message compatibility.
 1. Grpc server (grpc-netty-shaded) will be the server for handling the request and response.
 


 
