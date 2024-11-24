package com.whh.findmuseapi.art.service;

import com.whh.findmuseapi.art.dto.*;
import com.whh.findmuseapi.art.dto.response.*;
import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.art.entity.ArtLike;
import com.whh.findmuseapi.art.repository.ArtHistoryRepository;
import com.whh.findmuseapi.art.repository.ArtLikeRepository;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.common.constant.Infos.ArtType;
import com.whh.findmuseapi.common.exception.CBadRequestException;
import com.whh.findmuseapi.common.exception.CInternalServerException;
import com.whh.findmuseapi.common.exception.CNotFoundException;
import com.whh.findmuseapi.review.repository.ReviewRepository;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
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
    private final ArtHistoryRepository artHistoryRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 문화예술 개별 조회
     */
    @Override
    public ArtOneResponse getArtInfoOne(Long artId, Long userId) {
        Art findArt = artRepository.findById(artId).orElseThrow(() -> new CNotFoundException(artId + "은(는) 존재하지 않는 문화예술입니다."));
        User findUser = userRepository.findById(userId).orElseThrow(() -> new CNotFoundException(userId + "은(는) 존재하지 않는 회원입니다."));
        return ArtOneResponse.toDto(findArt,
                artLikeRepository.existsByUserAndArt(findUser, findArt),
                artHistoryRepository.existsByUserAndArt(findUser, findArt),
                reviewRepository.existsByUserAndArt(findUser, findArt));
    }

    /**
     * 조건에 따른 문화예술 리스트 조회
     */
    @Override
    public ArtListResponse getArtByCondition(Long userId, String date, List<String> genre, String sort) {
        if (genre == null) {
            throw new CBadRequestException("잘못된 요청입니다. 장르를 입력해주세요");
        }
        List<ArtType> artTypes = genre.stream().map(ArtType::convert).toList();
        userRepository.findById(userId).orElseThrow(() -> new CNotFoundException(userId + "은(는) 존재하지 않는 회원입니다."));

        if (sort.equals("최신순")) {
            return ArtListResponse.toDto(artRepository.findArtByCondition(userId, date, artTypes));
        }
        return ArtListResponse.toDto(artRepository.findArtByConditionRank(userId, date, artTypes));
    }

    /**
     * 홈화면
     */
    @Override
    public ArtHomeResponse getArtByHome(Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new CNotFoundException(userId + "은(는) 존재하지 않는 회원입니다."));
        return ArtHomeResponse.toDto(getArtByRandAndGenre(findUser), getArtByTodayRandom());

    }

    /**
     * 오늘의 문화예술 추천
     */
    private Art getArtByTodayRandom() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            return artRepository.findArtByTodayAndRandom(today);
        } catch(DateTimeException ex) {
            throw new CInternalServerException("날짜 변환에 실패하였습니다.");
        }
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
        User findUser = userRepository.findById(artLikeRequest.getUserId()).orElseThrow(() -> new CNotFoundException(artLikeRequest.getUserId() + "은(는) 존재하지 않는 회원입니다."));
        Art findArt = artRepository.findById(artLikeRequest.getArtId()).orElseThrow(() -> new CNotFoundException(artLikeRequest.getArtId() + "은(는) 존재하지 않는 문화예술입니다."));

        ArtLike liked = artLikeRepository.findArtLikeByArtAndUser(findArt, findUser)
                .orElse(new ArtLike(findUser, findArt));

        liked.changeStatus();
        artLikeRepository.save(liked);
    }

    /**
     * 지도 정보 반환
     */
    @Override
    public MapResponse getMapInfo() {
        List<Art> allArtList = artRepository.findAll();
        return MapResponse.toDto(allArtList);
    }
}
