package com.nhnacademy.illuwa.common.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/api/order/members/confirmed")
    ResponseEntity<Boolean> isConfirmedOrder(@RequestHeader("X-USER-ID") Long memberId, @RequestParam Long bookId);
}