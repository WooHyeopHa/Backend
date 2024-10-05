package com.whh.findmusechatting.chat.handler;


import io.rsocket.Payload;
import io.rsocket.RSocket;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ChatMessageHandler implements RSocket {


    /**
     * RequestResponse
     * @return Request 1 : Response 0~1
     */
    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return RSocket.super.requestResponse(payload);
    }

    /**
     * FireAndForget
     * @return Request 1 : Response 0
     */
    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        return RSocket.super.fireAndForget(payload);
    }

    /**
     * RequestStream
     * @return Request 1 : Response 0~N
     */
    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return RSocket.super.requestStream(payload);
    }

    /**
     * RequestChannel
     * @return Request 0~N : Response 0~N
     */
    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return RSocket.super.requestChannel(payloads);
    }
}
