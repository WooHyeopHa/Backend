package com.whh.findmuseapi.common.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

@Configuration
@EnableFeignClients("com.whh.findmuseapi")
public class OpenFeignConfig {
    @Bean
    Retryer.Default retryer() {
        // 1초의 간격으로 시작해 최대 5초의 간격으로 점점 증가하며, 최대5번 재시도한다.
        return new Retryer.Default(1000, TimeUnit.SECONDS.toMillis(5000), 5);
    }
    
    @Bean
    Logger.Level feignLoggerLevel() {
        /*
         * NONE: 로깅하지 않음(기본값)
         * BASIC: 요청 메소드와 URI와 응답 상태와 실행시간만 로깅함
         * HEADERS: 요청과 응답 헤더와 함께 기ㄹ본 정보들을 남김
         * FULL: 요청과 응답에 대한 헤더와 바디, 메타 데이터를 남김
         *
         * Feign의 로그는 DEBUG 레벨로만 남길 수 있음
         */
        return Logger.Level.FULL;
    }
    @Bean
    public FeignFormatterRegistrar dateTimeFormatterRegistrar() {
        /*
         * 주고 받는 데이터 타입으로 LocalDate, LocalDateTime, LocalTime 등이 사용된다면 제대로 처리가 되지 않으므로
         * DateTimer 관련된 포매터 추가 설정이 필요함
         */
        return registry -> {
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setUseIsoFormat(true);
            registrar.registerFormatters(registry);
        };
    }
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (ArrayUtils.isEmpty(requestTemplate.body()) && !isGetOrDelete(requestTemplate)) {
                // body가 비어있는 경우에 요청을 보내면 411 에러가 생김 https://github.com/OpenFeign/feign/issues/1251
                // content-length로 처리가 안되어서 빈 값을 항상 보내주도록 함
                requestTemplate.body("{}");
            }
        };
    }
    
    private boolean isGetOrDelete(RequestTemplate requestTemplate) {
        return Request.HttpMethod.GET.name().equals(requestTemplate.method())
            || Request.HttpMethod.DELETE.name().equals(requestTemplate.method());
    }
}
