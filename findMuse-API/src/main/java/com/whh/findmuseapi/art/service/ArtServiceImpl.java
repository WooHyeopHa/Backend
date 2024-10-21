package com.whh.findmuseapi.art.service;

import com.whh.findmuseapi.art.dto.*;
import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.art.entity.ArtLike;
import com.whh.findmuseapi.art.repository.ArtLikeRepository;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.common.constant.Infos.ArtType;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ArtServiceImpl implements ArtService{

    private final ArtRepository artRepository;
    private final UserRepository userRepository;
    private final ArtLikeRepository artLikeRepository;

    /**
     * 문화예술 개별 조회
     */
    @Override
    public ArtOneResponse getArtInfoOne(Long artId) {
        Art findArt = artRepository.findById(artId)
                .orElseThrow();
        return ArtOneResponse.toDto(findArt);
    }

    /**
     * 조건에 따른 문화예술 리스트 조회
     */
    @Override
    public ArtListResponse getArtByCondition(Long userId, String date, List<String> genre, String sort) {
        if (genre != null) {
            List<ArtType> artTypes = genre.stream().map(ArtType::convert).toList();
            if (sort.equals("최신순")) {
                return ArtListResponse.toDto(artRepository.findArtByCondition(userId, date, artTypes));
            }
            return ArtListResponse.toDto(artRepository.findArtByConditionRank(userId, date, artTypes));
        }
        //TODO : 예외처리
        throw new RuntimeException();
    }

    /**
     * 홈화면
     */
    @Override
    public ArtHomeResponse getArtByHome(Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow();
        return ArtHomeResponse.toDto(getArtByRandAndGenre(findUser), getArtByTodayRandom());

    }

    /**
     * 오늘의 문화예술 추천
     */
    private Art getArtByTodayRandom() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        return artRepository.findArtByTodayAndRandom(today);
    }

    /**
     * 장르별 랜덤 5개 추출
     */
    private List<ArtRandomResponse> getArtByRandAndGenre(User user) {
        List<ArtRandomResponse> randArtList = new ArrayList<>();
        //취향 정보가 없는 경우
        if (user.getTasteList().isEmpty()) {
            randArtList.addAll(ArtRandomResponse.toDto(artRepository.findArtByNoGenre()));
        }
        else {
            // 취향 정보가 있는 경우
            user.getTasteList().forEach(t -> {
                ArtType artType = ArtType.convert(t.getTaste().getName());
                randArtList.addAll(ArtRandomResponse.toDto(artRepository.findArtByGenre(artType)));
            });
        }
        return randArtList;
    }

    /**
     * 좋아요 표시
     */
    @Override
    @Transactional
    public void markLike(ArtLikeRequest artLikeRequest) {
        //TODO : 이게 최선일까?
        User findUser = userRepository.findById(artLikeRequest.getUserId()).orElseThrow();
        Art findArt = artRepository.findById(artLikeRequest.getArtId()).orElseThrow();

        ArtLike liked = artLikeRepository.findArtLikeByArtIdAndUserId(artLikeRequest.getArtId(), artLikeRequest.getUserId())
                .orElse(new ArtLike(findUser, findArt));

        liked.changeStatus();
        artLikeRepository.save(liked);
    }

    /**
     * 지도 정보 반환
     */
    @Override
    public MapResponse getMapInfo() {
        // 필요한 정보
        // 좌표, 장르, 포스터, 장소, 시작날짜, 끝나는 날짜
        List<Art> allArtList = artRepository.findAll();
        return MapResponse.toDto(allArtList);
    }
}
