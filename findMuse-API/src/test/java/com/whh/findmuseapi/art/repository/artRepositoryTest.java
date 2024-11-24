package com.whh.findmuseapi.art.repository;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.common.constant.Infos;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class artRepositoryTest {

    @Autowired
    private ArtRepository artRepository;

    @Test
    @Transactional
    public void test1() {
        List<Art> artList = artRepository.findArtByCondition(1L, "2024.09.08", Infos.ArtType.MUSICAL_DRAMA);
    }

    @Test
    @Transactional
    public void test2() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Art> art = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            art.addAll(artRepository.findArtByGenre(Infos.ArtType.CONCERT));
        }
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        Assertions.assertEquals(5, art.size());
    }
}
