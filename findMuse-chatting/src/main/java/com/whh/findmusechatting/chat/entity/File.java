package com.whh.findmusechatting.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "files")
public class File {
    @Id
    private UUID id;
    private String name;
    private String contentType;
    private FileDetail fileDetail;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static File create(UUID id, String name, String contentType, FileDetail fileDetail) {
        return File.builder()
                .id(id)
                .name(name)
                .contentType(contentType)
                .fileDetail(fileDetail)
                .build();
    }
}
