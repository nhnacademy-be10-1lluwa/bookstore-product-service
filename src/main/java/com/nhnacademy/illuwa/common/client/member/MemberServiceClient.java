package com.nhnacademy.illuwa.common.client.member;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface MemberServiceClient {
    @PostMapping("/api/members/points/event")
    ResponseEntity<Void> earnEventPoint(@RequestHeader("X-USER-ID") Long memberId, @RequestParam String reason);
}
