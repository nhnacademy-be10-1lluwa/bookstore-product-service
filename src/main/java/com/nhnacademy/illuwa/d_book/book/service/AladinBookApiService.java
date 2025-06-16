package com.nhnacademy.illuwa.d_book.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class AladinBookApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AladinBookApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${external.aladin.api-key}")
    private String apiKey;

    public List<BookExternalResponse> searchBooksByTitle(String title) {




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

        try{
            JsonNode root = objectMapper.readTree(response);
            JsonNode itemNode = root.get("item");
            return objectMapper.convertValue(
                    itemNode,
                    new TypeReference<List<BookExternalResponse>>() {}
            );

        } catch (JsonProcessingException e) {
            throw new BookApiParsingException("도서 API 응답 파싱 중 오류");
        }
    }

    public BookExternalResponse findBookByIsbn(String isbn) {
        String apiKey = "ttbchlgur13m0908001";


        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
                .queryParam("ttbkey", apiKey)
                .queryParam("Query", isbn)
                .queryParam("QueryType", "ISBN")
                .queryParam("output", "JS")  // 대소문자 주의
                .build()
                .encode()
                .toUri();
        try{
        String response = restTemplate.getForObject(uri, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode itemNode = root.get("item");
            return objectMapper.convertValue(
                    itemNode,
                    new TypeReference<BookExternalResponse>() {}
            );

        } catch (JsonProcessingException e) {
            throw new BookApiParsingException("도서 API 요청 또는 파싱 중 오류");
        }

    }
}
