package com.whh.findmusechatting.chat.repository;

import com.whh.findmusechatting.chat.entity.File;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface FileRepository extends ReactiveMongoRepository<File, UUID> {
    Mono<File> findByFileDetailUrl(String url);
    Mono<Void> deleteByFileDetailUrl(String url);
}
