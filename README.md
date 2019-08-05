# grpc-kafka-adapter

This documentation provides a basic Java programmer’s introduction to working with gRPC server and how to build services 
to connect to Kafka.

By walking through this example you’ll learn how to:

Define a service in a .proto file.
Generate server code using the protocol buffer compiler.
Gennerate Avro files using Avro plugins.
Use the Java gRPC API to write two simple services which would produce and consume messages from Kafka

Why use gRPC?

   The microservices are build using Node js. Nodejs libraries which connect to kafka dosent support some features provided by Kafak client 
   libraries. Eg ( Transcational , Idempotent producer, Excatly once semantics and some features related to streaming api's). To support these 
   features , we would create a grpc server using Java programming language. The core features which are mentioned above will be implemented in
   java inside grpc server and will be exposed as api to node microservices. The node microservices would use the 
