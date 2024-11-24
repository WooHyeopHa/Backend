package com.whh.findmuseapi.review.service;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.art.entity.ArtHistory;
import com.whh.findmuseapi.art.repository.ArtHistoryRepository;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.common.exception.CBadRequestException;
import com.whh.findmuseapi.common.exception.CForbiddenException;
import com.whh.findmuseapi.common.exception.CInternalServerException;
import com.whh.findmuseapi.common.exception.CNotFoundException;
import com.whh.findmuseapi.review.dto.*;
import com.whh.findmuseapi.review.entity.ArtReview;
import com.whh.findmuseapi.review.entity.ArtReviewLike;
import com.whh.findmuseapi.review.repository.ReviewLikeRepository;
import com.whh.findmuseapi.review.repository.ReviewRepository;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService{

    private final ArtRepository artRepository;
    private final ArtHistoryRepository artHistoryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    /**
     * 모든 리뷰 조회
     */
    @Override
    public AllReviewResponse getArtReview(long artId, long userId, String creteria) {
        Art findArt = artRepository.findById(artId).orElseThrow(() -> new CNotFoundException(artId + "은(는) 존재하지 않는 문화예술입니다."));
        userRepository.findById(userId).orElseThrow(() -> new CNotFoundException(userId + "은(는) 존재하지 않는 사용자입니다."));

        List<ArtReview> result;
        if(creteria.equals("like")) {
            result = reviewRepository.findAllByArtOrderByLikeCountDesc(artId, userId).orElse(new ArrayList<>());
        }
        else if(creteria.equals("date")){
            result = reviewRepository.findAllByArtOrderByCreateDateDesc(artId, userId).orElse(new ArrayList<>());
        }
        else {
            throw new CBadRequestException("정렬 조건이 잘못되었습니다. 다시 요청해주세요.");
        }
        return AllReviewResponse.toDto(findArt.getStar(), result);
    }

    /**
     * 리뷰 작성
     */
    @Override
    @Transactional
    public void createReview(ReviewRequest reviewRequest) {
        User findUser = userRepository.findById(reviewRequest.getUserId()).orElseThrow(() -> new CNotFoundException(reviewRequest.getUserId() + "은(는) 존재하지 않는 회원입니다."));
        Art findArt = artRepository.findById(reviewRequest.getArtId()).orElseThrow(() -> new CNotFoundException(reviewRequest.getArtId() + "은(는) 존재하지 않는 문화예술입니다."));
        ArtReview artReview = new ArtReview(reviewRequest.getContent(), findUser, findArt);
        try {
            reviewRepository.save(artReview);
        } catch (IllegalArgumentException ex) {
            throw new CInternalServerException("엔티티 저장에 실패했습니다.");
        }
    }

    /**
     * 리뷰 수정
     */
    @Override
    @Transactional
    public void updateReview(ReviewUpdateRequest reviewUpdateRequest) {
        ArtReview findReview = reviewRepository.findById(reviewUpdateRequest.getReviewId()).orElseThrow(() -> new CNotFoundException(reviewUpdateRequest.getReviewId() + "은(는) 존재하지 않는 리뷰입니다."));

        if (findReview.getUser().getId() == reviewUpdateRequest.getUserId()) {
            findReview.updateReview(reviewUpdateRequest.getContent());
            return;
        }
        throw new CForbiddenException();
    }

    /**
     * 리뷰 삭제
     */
    @Override
    public void deleteReview(long userId, long reviewId) {
        ArtReview findReview = reviewRepository.findById(reviewId).orElseThrow(() -> new CNotFoundException(reviewId + "은(는) 존재하지 않는 리뷰입니다."));

        if (findReview.getUser().getId() == userId) {
            reviewRepository.delete(findReview);
            return;
        }
        throw new CForbiddenException();
    }

    /**
     * 리뷰 공감하기
     */
    @Override
    @Transactional
    public boolean likeReview(ReviewLikeRequest reviewLikeRequest) {
        User findUser = userRepository.findById(reviewLikeRequest.getUserId()).orElseThrow(() -> new CNotFoundException(reviewLikeRequest.getUserId() + "은(는) 존재하지 않는 회원입니다."));
        ArtReview artReview = reviewRepository.findById(reviewLikeRequest.getReviewId()).orElseThrow(() -> new CNotFoundException(reviewLikeRequest.getReviewId() + "은(는) 존재하지 않는 리뷰입니다."));

        try {
            reviewLikeRepository.save(new ArtReviewLike(findUser, artReview));
            return true;
        } catch (IllegalArgumentException ex) {
            throw new CInternalServerException("엔티티 저장에 실패했습니다.");
        }
    }

    /**
     * 공감한 리뷰 취소
     */
    @Override
    @Transactional
    public void likeReviewCancle(long reviewId, long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new CNotFoundException(userId + "은(는) 존재하지 않는 회원입니다."));
        ArtReview artReview = reviewRepository.findById(reviewId).orElseThrow(() -> new CNotFoundException(reviewId + "은(는) 존재하지 않는 리뷰입니다."));
        reviewLikeRepository.deleteArtReviewLikeByUserAndArtReview(findUser, artReview);
    }

    /**
     * 문화예술 별점 매기기
     */
    @Override
    @Transactional
    public void giveReviewStar(ReviewStarRequest reviewStarRequest) {
        User findUser = userRepository.findById(reviewStarRequest.getUserId()).orElseThrow(() -> new CNotFoundException(reviewStarRequest.getUserId() + "은(는) 존재하지 않는 회원입니다."));
        Art findArt = artRepository.findById(reviewStarRequest.getArtId()).orElseThrow(() -> new CNotFoundException(reviewStarRequest.getArtId() + "은(는) 존재하지 않는 문화예술입니다."));
        //이미 관람한 경우
        if (reviewStarRequest.isStared()) {
            ArtHistory findHistory = artHistoryRepository.findByUserAndArt(findUser, findArt);
            findArt.updateStar(findHistory.getStar(), reviewStarRequest.getStar());
        }
        // 처음 관람인 경우
        else {
            findArt.plusViewAndCalStar(reviewStarRequest.getStar());
            artHistoryRepository.save(new ArtHistory(findUser, findArt, reviewStarRequest.getStar()));
            ArtReview findReview = reviewRepository.findByUserAndArt(findUser, findArt);
            findReview.updateStar(String.valueOf(reviewStarRequest.getStar()));
        }
    }
}
