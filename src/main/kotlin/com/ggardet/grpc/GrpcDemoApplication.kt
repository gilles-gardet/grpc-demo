package com.ggardet.grpc

import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcDemoApplication

fun main(args: Array<String>) {
    runApplication<GrpcDemoApplication>(*args)
}

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

@GrpcService
class ChatService : ChatServiceGrpc.ChatServiceImplBase() {
    override fun chat(responseObserver: StreamObserver<ChatProto.ChatMessage>): StreamObserver<ChatProto.ChatMessage> {
        return object : StreamObserver<ChatProto.ChatMessage> {
            override fun onNext(request: ChatProto.ChatMessage) {
                val response = ChatProto.ChatMessage.newBuilder()
                    .setUser(request.user)
                    .setMessage("Echo: ${request.message}")
                    .build()
                responseObserver.onNext(response)
            }

            override fun onError(throwable: Throwable) {
                throwable.printStackTrace()
            }

            override fun onCompleted() {
                responseObserver.onCompleted()
            }
        }
    }
}
