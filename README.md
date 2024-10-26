# gRPC demo

### Advantages of gRPC over a traditional REST API

gRPC offers several advantages over a traditional REST API:

1. **Performance**
   - gRPC uses HTTP/2, which allows for faster and more efficient communication through header compression, request and response multiplexing, and stream management.
   - Messages are serialized using Protocol Buffers, which are more compact and faster to process than textual formats like JSON.

2. **Service Contracts**
   - gRPC uses `.proto` files to define service contracts, enabling automatic code generation for both client and server in multiple languages.
   - This ensures that clients and servers are always synchronized with the same service definitions.

3. **Streaming**
   - gRPC natively supports bidirectional streaming, allowing clients and servers to send and receive data streams continuously.
   - This is particularly useful for applications requiring real-time updates.

4. **Interoperability**
   - gRPC is designed to be used with multiple programming languages, facilitating interoperability between different systems.
   - `.proto` files can be used to generate code in languages like Java, Kotlin, Python, Go, C++, etc.

5. **Security**
   - gRPC integrates robust security mechanisms using TLS to encrypt communications.
   - It also offers advanced authentication and authorization options.

6. **Ease of Development**
   - Automatic generation of client and server stubs reduces boilerplate code and potential errors.
   - gRPC tools and libraries simplify the development, testing, and deployment of services.

In summary, gRPC provides improved performance, better service contract management, streaming capabilities, multi-language interoperability, enhanced security, and ease of development compared to traditional REST APIs.

### Steps to generate gRPC stubs

1. **Configure the Maven plugin for protobuf**

   Ensure that the `protobuf-maven-plugin` is configured in your `pom.xml` file. Here is an example configuration:
   
   ```xml
   <plugin>
       <groupId>org.xolstice.maven.plugins</groupId>
       <artifactId>protobuf-maven-plugin</artifactId>
       <version>${protobuf-maven-plugin.version}</version>
       <configuration>
           <protocArtifact>com.google.protobuf:protoc:${google-protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
           <pluginId>grpc-java</pluginId>
           <pluginArtifact>io.grpc:protoc-gen-grpc-java:${io.grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
       </configuration>
       <executions>
           <execution>
               <id>protobuf-compile</id>
               <goals>
                   <goal>compile</goal>
               </goals>
           </execution>
           <execution>
               <id>protobuf-compile-custom</id>
               <goals>
                   <goal>compile-custom</goal>
               </goals>
           </execution>
       </executions>
   </plugin>
   ```

2. **Define your `.proto` files**

   Place your `.proto` files in the `src/main/proto` directory. For example, `post.proto` might look like this:
   
   ```proto
   syntax = "proto3";
   
   option java_package = "com.ggardet.grpc";
   option java_outer_classname = "PostProto";
   
   message PostRequest {
       int32 id = 1;
   }
   
   message PostResponse {
       int32 id = 1;
       string title = 2;
       string content = 3;
   }
   
   service PostService {
       rpc GetPost (PostRequest) returns (PostResponse);
   }
   ```

3. **Compile the `.proto` files**

   Run the following command to compile the `.proto` files and generate the gRPC stubs:
   
   ```sh
   mvn clean compile
   ```

### Steps to start the project

1. **Configure the `application.properties` file**
   
   Add the necessary configurations in `src/main/resources/application.properties`. For example:
   
   ```properties
   grpc.server.port=9090
   ```

2. **Create the main application class**
   
   Ensure you have a main class to start your Spring Boot application. For example, `GrpcDemoApplication.kt`:

   ```kotlin
   package com.ggardet.grpc
   
   import org.springframework.boot.autoconfigure.SpringBootApplication
   import org.springframework.boot.runApplication
   
   @SpringBootApplication
   class GrpcDemoApplication
   
   fun main(args: Array<String>) {
    runApplication<GrpcDemoApplication>(*args)
   }
   ```

3. **Create the gRPC service**
   
   Implement your gRPC service using the `@GrpcService` annotation. For example, `PostService`:
   
   ```kotlin
   @GrpcService
   class PostService : PostServiceGrpc.PostServiceImplBase() {
       override fun getPost(request: PostRequest, responseObserver: StreamObserver<PostResponse>) {
           val response = PostResponse.newBuilder()
               .setId(request.id)
               .setTitle("First post")
               .setContent("This is the first post")
               .build()
           responseObserver.onNext(response)
           responseObserver.onCompleted()
       }
   }
   ```

4. **Start the application**

   Run the following command to start your Spring Boot application:

   ```sh
   mvn spring-boot:run
   ```
   
   By following these steps, you will be able to generate the gRPC stubs and start your project successfully.

### Steps to use a gRPC client

Many tools can be used to test a gRPC service, such as `postman`, `insomnia`, and [many more](https://github.com/grpc-ecosystem/awesome-grpc?tab=readme-ov-file#gui).  
Here, we will use `grpcurl` to test the `PostService` service.

1. **Install grpcurl**
   
   Follow the installation instructions for grpcurl for your operating system from the [grpcurl GitHub repository](https://github.com/fullstorydev/grpcurl).

2. **Use grpcurl to test the gRPC service**

   Use the following command to call the `GetPost` service:

   ```sh
   grpcurl -plaintext -d '{"id": 999}' localhost:9090 PostService/GetPost
   ```
   
   This command sends a gRPC request to the `PostService` service using the `GetPost` endpoint with an `id` of 999
