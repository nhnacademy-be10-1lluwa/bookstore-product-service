package com.nhnacademy.illuwa;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@OpenAPIDefinition(
        info = @Info(
                title = "Illuwa API 문서",
                description = "Illuwa 프로젝트의 OpenAPI 명세",
                version = "v1.0"
        )
)

@EnableFeignClients
@SpringBootApplication
public class  IlluwaApplication {

    public static void main(String[] args) {
        SpringApplication.run(IlluwaApplication.class, args);
    }

}
