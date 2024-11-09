package com.whh.findmusechatting.chat.entity;

import com.whh.findmusechatting.chat.entity.constant.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDetail {
    private FileType type;
    private String url;

    public static FileDetail create(FileType type, String url) {
        return FileDetail.builder()
                .type(type)
                .url(url)
                .build();
    }
}
