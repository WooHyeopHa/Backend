package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.art.dto.response.ArtHistoryResponse;
import com.whh.findmuseapi.art.dto.response.ArtListResponse;
import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.art.entity.ArtHistory;
import com.whh.findmuseapi.art.entity.ArtLike;
import com.whh.findmuseapi.art.repository.ArtHistoryRepository;
import com.whh.findmuseapi.art.repository.ArtLikeRepository;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.review.dto.ReviewResponse;
import com.whh.findmuseapi.review.entity.ArtReview;
import com.whh.findmuseapi.review.entity.ArtReviewLike;
import com.whh.findmuseapi.review.repository.ReviewRepository;
import com.whh.findmuseapi.user.dto.response.MyInfo;
import com.whh.findmuseapi.user.dto.response.TasteGroupResponse;
import com.whh.findmuseapi.user.entity.Taste;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.entity.UserTaste;
import com.whh.findmuseapi.user.repository.TasteRepository;
import com.whh.findmuseapi.user.repository.UserRepository;
import com.whh.findmuseapi.user.repository.UserTasteRepository;
import com.whh.findmuseapi.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final ArtRepository artRepository;
    private final ArtLikeRepository artLikeRepository;
    private final ArtHistoryRepository artHistoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserTasteRepository userTasteRepository;
    private final TasteRepository tasteRepository;

    @Description("메인 화면")
    @Transactional(readOnly = true)
    public MyInfo getMyInfo(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        List<ArtHistory> artHistories = artHistoryRepository.findByUserId(user.getId());
        List<ArtLike> artLikes = artLikeRepository.findByUserId(user.getId());

        return UserMapper.toMyInfo(user, artRepository.count(), artLikes, artHistories);
    }

    @Description("보고싶어요 리스트업")
    @Transactional(readOnly = true)
    public ArtListResponse getMyArtLikeList(User user, String artTypeInfo) {
        List<ArtLike> artLikes = artLikeRepository.findByUserId(user.getId());

        List<Art> arts = artLikes.stream()
                .map(ArtLike::getArt)
                .distinct()
                .toList();

        if (artTypeInfo != null && !artTypeInfo.isEmpty()) {
            try {
                Infos.ArtType artType = Infos.ArtType.convert(artTypeInfo);
                arts = arts.stream()
                        .filter(art -> art.getArtType() == artType)
                        .toList();
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("유효하지 않은 ArtType 정보입니다.");
            }
        }

        return ArtListResponse.toDto(arts);
    }

    @Description("상대방 보고싶어요 조회")
    @Transactional(readOnly = true)
    public ArtListResponse getUserLikeList(long userId, String artTypeInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!user.isShowStatus()) {
            throw new RuntimeException("감상내역 비공개 뮤즈에요!");
        }
        return getMyArtLikeList(user, artTypeInfo);
    }

    @Description("보고싶어요 취소")
    @Transactional
    public void cancelMyArtLike(User user, Long artId) {
        Art artWithAssociations = artRepository.findById(artId)
                .orElseThrow(() -> new RuntimeException("공연이 존재하지 않습니다."));

        ArtLike artLike = artLikeRepository.findArtLikeByArtAndUser(artWithAssociations, user)
                .orElseThrow(() -> new RuntimeException("해당 '보고싶어요' 항목이 존재하지 않습니다."));

        artLikeRepository.delete(artLike);
    }

    @Description("관람했어요 리스트업")
    @Transactional(readOnly = true)
    public List<ArtHistoryResponse> getMyArtHistory(User user, String artTypeInfo) {
        List<ArtHistory> artHistories = artHistoryRepository.findByUserId(user.getId());

        if (artTypeInfo != null && !artTypeInfo.isEmpty()) {
            try {
                Infos.ArtType artType = Infos.ArtType.convert(artTypeInfo);
                artHistories = artHistories.stream()
                        .filter(artHistory -> artHistory.getArt().getArtType() == artType)
                        .toList();
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("유효하지 않은 ArtType 정보입니다.");
            }
        }

        return artHistories.stream()
                .map(ArtHistoryResponse::fromEntity)
                .toList();
    }

    @Description("상대방 관람했어요 조회")
    @Transactional(readOnly = true)
    public List<ArtHistoryResponse> getUserArtHistory(long userId, String artTypeInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!user.isShowStatus()) {
            throw new RuntimeException("감상내역 비공개 뮤즈에요!");
        }
        return getMyArtHistory(user, artTypeInfo);
    }

    @Description("행사 리뷰 조회")
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReview(User user, String creteria) {
        Infos.ReviewSortType sortType = Infos.ReviewSortType.fromString(creteria);

        List<Object[]> reviews;
        if (sortType == Infos.ReviewSortType.LATEST) {
            reviews = reviewRepository.findAllByUserOrderByCreateDateDesc(user.getId());
        } else if (sortType == Infos.ReviewSortType.POPULAR) {
            reviews = reviewRepository.findAllByUserOrderByLikeCountDesc(user.getId());
        } else {
            throw new IllegalArgumentException("지원되지 않는 정렬 기준입니다.");
        }

        return reviews.stream()
                .map(r -> {
                    ArtReview review = (ArtReview) r[0];
                    ArtReviewLike reviewLike = (ArtReviewLike) r[1];
                    return ReviewResponse.toDto(review, reviewLike);
                })
                .toList();
    }

    @Description("상대방 행사 리뷰 조회")
    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReview(long userId, String creteria) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!user.isShowStatus()) {
            throw new RuntimeException("감상내역 비공개 뮤즈에요!");
        }
        return getMyReview(user, creteria);
    }

    @Description("레벨 자세히보기")
    @Transactional(readOnly = true)
    public MyInfo.UserLevelResponse getMyUserLevel(User user) {
        return UserMapper.toMyLevelResponse(user);
    }

    @Description("상대방 취향 조회")
    @Transactional(readOnly = true)
    public List<TasteGroupResponse> getUserTastes(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        List<UserTaste> userTastes = userTasteRepository.findAllByUser(user);

        Map<Long, List<TasteGroupResponse.TasteResponse>> groupedByParent = userTastes.stream()
                .map(UserTaste::getTaste)
                .collect(Collectors.groupingBy(taste ->
                                taste.getParent() != null ? taste.getParent().getId() : null,
                        Collectors.mapping(taste -> new TasteGroupResponse.TasteResponse(taste.getId(), taste.getName()), Collectors.toList())
                ));
        return groupedByParent.entrySet().stream()
                .map(entry -> {
                    // Parent Taste 정보도 포함
                    Taste parentTaste = tasteRepository.findById(entry.getKey())
                            .orElseThrow(() -> new RuntimeException("상위 취향이 발견되지 않습니다."));
                    return new TasteGroupResponse(entry.getKey(), parentTaste.getName(), entry.getValue());
                })
                .toList();
    }
}
