package com.nhnacademy.illuwa.d_book.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class AladinBookApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AladinBookApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<BookExternalResponseDto> searchBooksByTitle(String title) throws JsonProcessingException {


        String apiKey = "ttbchlgur13m0908001";

        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
                .queryParam("ttbkey", apiKey)
                .queryParam("Query",title)
                .queryParam("QueryType","Title")
                .queryParam("MaxResult", 100)
                .queryParam("Output", "JS")
                .build()
                .encode()
                .toUri();

        String response = restTemplate.getForObject(uri,String.class);
        JsonNode root = objectMapper.readTree(response);

        JsonNode itemNode = root.get("item");

        List<BookExternalResponseDto> books = objectMapper.readValue(
                itemNode.toString(),
                new TypeReference<List<>>() {}
        );


        return books;
    }

}
