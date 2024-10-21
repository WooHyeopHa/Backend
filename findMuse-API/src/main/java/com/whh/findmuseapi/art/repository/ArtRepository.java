package com.whh.findmuseapi.art.repository;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.common.constant.Infos.ArtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtRepository extends JpaRepository<Art, Long> {

    @Query("select a from Art a left outer join a.artLikes al on al.user.id = :userId where a.startDate <= :date and a.artType in (:types) order by a.startDate desc")
    List<Art> findArtByCondition(Long userId, String date, List<ArtType> types);

    @Query("select a from Art a left outer join a.artLikes al on al.user.id = :userId where a.startDate <= :date and a.artType in (:types) order by a.viewCnt, a.star desc")
    List<Art> findArtByConditionRank(Long userId, String date, List<ArtType> types);


    @Query(value = "select a from Art a left outer join a.artLikes al on al.user.id = :userId where a.artType = :type order by a.viewCnt, a.star desc limit 50")
    List<Art> findArtByRankAndGenre(Long userId, ArtType type);

    @Query(value = "select a from Art a left outer join a.artLikes al on al.user.id = :userId where a.artType = :type order by a.viewCnt, a.star desc limit 5")
    List<Art> findArtByRankAndGenreSimple(Long userId, ArtType type);

    @Query("select a from Art a left outer join a.artLikes al on al.user.id = :userId order by a.viewCnt, a.star desc limit 50")
    List<Art> findArtByRankAll(Long userId);

    @Query("select a from Art a left outer join a.artLikes al on al.user.id = :userId order by a.startDate desc limit 15")
    List<Art> findArtByDate(Long userId);

    // 취향 별 랜덤 추출 5개
    @Query(value = "select a from Art a where a.artType = :type and a.randomId >= floor(rand() * 100000000) limit 5", nativeQuery = true)
    List<Art> findArtByGenre(ArtType type);

    // 취향 정보가 없을 때 랜덤 추출 5개
    @Query(value = "select a from Art a where a.randomId >= floor(rand() * 100000000) limit 5", nativeQuery = true)
    List<Art> findArtByNoGenre();

    @Query(value = "select a from Art a where a.startDate >= :date and a.randomId >= floor(rand() * 100000000) limit 1", nativeQuery = true)
    Art findArtByTodayAndRandom(String date);
}
