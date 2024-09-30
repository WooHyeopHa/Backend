package com.whh.findmuseapi.art.service;

import com.whh.findmuseapi.art.dto.ArtHomeResponse;
import com.whh.findmuseapi.art.dto.ArtLikeRequest;
import com.whh.findmuseapi.art.dto.ArtListResponse;
import com.whh.findmuseapi.art.dto.ArtOneResponse;

import java.util.List;


public interface ArtService {

    ArtOneResponse getArtInfoOne(Long artId);

    ArtListResponse getArtByCondition(Long userId, String date, List<String> genre, String sort);

    void markLike(ArtLikeRequest artLikeRequest);

    ArtListResponse getArtByRank(Long userId, String genre);

    ArtHomeResponse getArtByHome(Long userId);
}
