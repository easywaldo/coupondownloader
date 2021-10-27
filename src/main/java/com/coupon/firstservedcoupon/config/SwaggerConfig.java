package com.coupon.firstservedcoupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
            .title("Coupon Downloader Service")
            .description("This service is dedicated downloading coupon for you")
            .build();
    }

    @Bean
    public Docket commonApi() {

        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("coupon")
            .apiInfo(this.apiInfo())
            .select()
            .apis(RequestHandlerSelectors
                .basePackage("com.coupon.firstservedcoupon.controller"))
            .paths(PathSelectors.ant("/**"))
            .build();
    }

}
