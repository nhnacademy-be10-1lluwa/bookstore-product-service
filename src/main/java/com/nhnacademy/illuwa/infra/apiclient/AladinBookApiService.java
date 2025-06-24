package com.nhnacademy.illuwa.infra.apiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
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
                .queryParam("Query", title)
                .queryParam("QueryType", "Title")
                .queryParam("SearchTarget", "Book")
                .queryParam("MaxResults", 50)
                .queryParam("Sort", "SalesPoint") // 판매량 순
                .queryParam("Cover", "Big")
                .queryParam("Output", "JS")
                .build()
                .encode()
                .toUri();
        try{
            String response = restTemplate.getForObject(uri,String.class);

            String fixedResponse = response.replace("\\'", "'");

            JsonNode root = objectMapper.readTree(fixedResponse);
            JsonNode itemNode = root.get("item");
            return objectMapper.convertValue(
                    itemNode,
                    new TypeReference<List<BookExternalResponse>>() {}
            );

        }
        catch (RestClientException e) {
            log.error("알라딘 api 호출 실패 : {}" ,title, e);
            throw new BookApiException("알라딘 도서 api 호출 실패", HttpStatus.BAD_GATEWAY);
        }
        catch (JsonProcessingException e) {
            log.error("알라딘 api 응답값의 파싱 실패 : {}" ,title,e);
            throw new BookApiParsingException("알라딘 API 응답값을 바탕으로 파싱 도중 에러 발생");
        }
    }




    public BookExternalResponse findBookByIsbn(String isbn) {

        URI uri = UriComponentsBuilder
                .fromHttpUrl("http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx")
                .queryParam("ttbkey", apiKey)
                .queryParam("itemIdType", "ISBN")
                .queryParam("ItemId", isbn)
                .queryParam("output", "JS")
                .queryParam("Version", "20131101")
                .build()
                .encode()
                .toUri();
        try{
        String response = restTemplate.getForObject(uri, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode itemArray = root.get("item");
            JsonNode firstItem = itemArray.get(0);
            return objectMapper.convertValue(
                    firstItem,
                    new TypeReference<BookExternalResponse>() {}
            );

        }  catch (RestClientException e) {
            log.error("알라딘 api 호출 실패 - 해당 도서의 ISBN : {}" ,isbn, e);
            throw new BookApiException("알라딘 도서 api 호출 실패", HttpStatus.BAD_GATEWAY);
        }
        catch (JsonProcessingException e) {
            log.error("알라딘 api 응답값의 파싱 실패 - 해당 도서의 ISBN : {}" ,isbn,e);
            throw new BookApiParsingException("알라딘 API 응답의 파싱 실패");
        }
    }
}
