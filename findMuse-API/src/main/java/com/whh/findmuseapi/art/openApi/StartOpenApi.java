//package com.whh.findmuseapi.art.openApi;
//
//import com.whh.findmuseapi.art.openApi.service.ArtOpenApiService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class StartOpenApi implements ApplicationRunner {
//
//    private final ArtOpenApiService artOpenApiService;
//
//    /**
//     * 실행 후 API 호출 후 DB 저장
//     */
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("start api");
//        artOpenApiService.init();
//        System.out.println("end api");
//    }
//}
