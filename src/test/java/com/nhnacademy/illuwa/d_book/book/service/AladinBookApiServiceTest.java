package com.nhnacademy.illuwa.d_book.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AladinBookApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private AladinBookApiService aladinBookApiService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        aladinBookApiService = new AladinBookApiService(restTemplate, objectMapper);
    }



    @Test
    @DisplayName("도서 검색 성공 - 도서 제목을 통해 해당 제목이 들어간 도서 조회")
    void searchBooksByTitleTest_Success() throws JsonProcessingException {
        //given
        String title = "어린 왕자";

        String mockApiResponse = """
                {
                    "item": [
                        {
                            "title": "어린 왕자",
                            "author": "생텍쥐페리",
                            "isbn" : "1341343FO1",
                            "pubDate": "2024-06-16",
                            "priceStandard": 12000,
                            "priceSales": 10000,
                            "cover": "img.jpg",
                            "categoryName": "소설",
                            "publisher": "출판사"
                        }
                    ]
                }
                """;
        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn(mockApiResponse);


        //when
        List<BookExternalResponse> result = aladinBookApiService.searchBooksByTitle(title);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("어린 왕자");
        verify(restTemplate).getForObject(any(URI.class), eq(String.class));

    }



    @Test
    @DisplayName("도서 검색 실패 - 알라딘 도서 API 호출 실패")
    void searchBooksByTitleTest_Failure(){
        //given

        String title = "어린 왕자";

        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenThrow(new RestClientException("API 호출 실패"));

        //when
        assertThatThrownBy(() -> aladinBookApiService.searchBooksByTitle(title))
                .isInstanceOf(BookApiException.class)
                .hasMessage("알라딘 도서 api 호출 실패");


        verify(restTemplate).getForObject(any(URI.class),eq(String.class));



    }

    @Test
    @DisplayName("도서 검색 성공 - 도서의 ISBN을 통해 단권 조회")
    void findBookByIsbn_Success() throws JsonProcessingException {

        String ISBN = "1234567890F";

        String mockApiResponse = """
                {
                    "item": [
                        {
                          "title": "인어 공주",
                          "author": "안데르센",
                          "isbn": "1234567890F",
                          "pubDate": "2024-06-16",
                          "priceStandard": 10000,
                          "priceSales": 7000,
                          "cover": "mermaid/princess.jpg",
                          "categoryName": "서양소설",
                          "publisher": "출판사S"
                        }
                      ]
                }
                """;

        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn(mockApiResponse);




        //when
        BookExternalResponse result = aladinBookApiService.findBookByIsbn(ISBN);

        //then
        assertThat(result.getTitle()).isEqualTo("인어 공주");
        assertThat(result.getIsbn()).isEqualTo("1234567890F");
        verify(restTemplate).getForObject(any(URI.class), eq(String.class));

    }

    @Test
    @DisplayName("도서 검색 실패 - 도서의 ISBN을 통해 단권 조회")
    void findBookByIsbn_Failure(){
        String isbn = "1234567890FA";

        String invalidJsonResponse = "잘못된 API 응답";

        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn(invalidJsonResponse);


        assertThatThrownBy(() -> aladinBookApiService.findBookByIsbn(isbn))
                .isInstanceOf(BookApiParsingException.class)
                .hasMessage("알라딘 API 응답의 파싱 실패");
    }
}
