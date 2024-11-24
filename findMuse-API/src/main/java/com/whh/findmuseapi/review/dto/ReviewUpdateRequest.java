package com.whh.findmuseapi.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewUpdateRequest {
    private long userId;
    private long reviewId;
    private String content;
}
