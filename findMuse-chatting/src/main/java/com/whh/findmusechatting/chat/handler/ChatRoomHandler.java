package com.whh.findmusechatting.chat.handler;


import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ChatRoomHandler implements RSocket {



    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        System.out.println("Received request-response request with payload: " + payload.getDataUtf8());
        return Mono.just(DefaultPayload.create("Hello, " + payload.getDataUtf8() + "!"));
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("Received fire-and-forget request with payload: " + payload.getDataUtf8());
        return Mono.empty();
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
//        return RSocket.super.requestStream(payload);
        System.out.println("Received request-stream request with payload: " + payload.getDataUtf8());
        return Flux.just(DefaultPayload.create("Message 1"), DefaultPayload.create("Message 2"), DefaultPayload.create("Message 3"));
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        System.out.println("Received request-channel request");
        return Flux.from(payloads)
                .doOnNext(payload -> System.out.println("Received payload: " + payload.getDataUtf8()))
                .map(payload -> DefaultPayload.create("Hello, " + payload.getDataUtf8() + "!"));
    }

}
