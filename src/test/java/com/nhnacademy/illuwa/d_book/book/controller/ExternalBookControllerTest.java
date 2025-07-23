package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExternalBookController.class)
class ExternalBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AladinBookApiService aladinBookApiService;

    @Test
    @DisplayName("ISBN으로 외부 도서 조회 - 성공")
    void getBookByIsbn_success() throws Exception {
        BookExternalResponse mockResponse = new BookExternalResponse();
        mockResponse.setTitle("Test External Book");

        when(aladinBookApiService.findBookByIsbn(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/external-books/isbn/{isbn}", "1234567890"))
                .andExpect(status().isOk());

        verify(aladinBookApiService).findBookByIsbn("1234567890");
    }

    @Test
    @DisplayName("ISBN으로 외부 도서 조회 - 실패 (책 없음)")
    void getBookByIsbn_notFound() throws Exception {
        when(aladinBookApiService.findBookByIsbn(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/external-books/isbn/{isbn}", "1234567890"))
                .andExpect(status().isNotFound());

        verify(aladinBookApiService).findBookByIsbn("1234567890");
    }
}
