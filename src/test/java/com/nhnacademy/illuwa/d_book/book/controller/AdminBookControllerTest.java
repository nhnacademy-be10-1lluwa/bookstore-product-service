package com.nhnacademy.illuwa.d_book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookController.class)
class AdminBookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookMapper bookMapper;

    @MockBean
    private AladinBookApiService aladinBookApiService;





    @MockBean
    private BookService bookService;


    @Test
    void searchBooksByExternalApi() throws Exception {
        // given
        String title = "도서제목";

        // when & then
        mockMvc.perform(get("/admin/books/external")
                        .param("title",title))
                .andExpect(status().isOk());

        verify(bookService).searchBookFromExternalApi(title);
    }


    @Test
    void searchBookById() throws Exception {
        // given
        Long id = 11L;

        // when & then
        mockMvc.perform(get("/admin/books/{id}",id))
                .andExpect(status().isOk());

        verify(bookService).searchBookById(id);
    }

    @Test
    void getRegisteredBooks() throws Exception {

        // when & then
        mockMvc.perform(get("/admin/books"))
                .andExpect(status().isOk());

        verify(bookService).getAllBooks();
    }

    @Test
    void testSearchBookByIsbn() throws Exception {
        // given
        String isbn = "00AA123";

        // when & then
        mockMvc.perform(get("/admin/books/isbn/{isbn}",isbn))
                .andExpect(status().isOk());

        verify(aladinBookApiService).findBookByIsbn(isbn);
    }

    @Test
    void deleteBook() throws Exception {
        // given
        Long id = 11L;

        // when & then
        mockMvc.perform(delete("/admin/books/{id}",id))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(id);
    }

    @Test
    void updateBook() throws Exception {
        // given
        Long id = 11L;

        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest("contents","des",100,true);


        // when & then

        mockMvc.perform(patch("/admin/books/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookUpdateRequest)))
                .andExpect(status().isNoContent());


        verify(bookService).updateBook(any(),any());

    }
}