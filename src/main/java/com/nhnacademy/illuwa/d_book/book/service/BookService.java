package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.mapper.BookExternalMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {
    private final AladinBookApiService aladinBookApiService;
    private final BookRepository bookRepository;
    private final BookExternalMapper bookExternalMapper;
    private final BookResponseMapper bookResponseMapper;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final TagRepository tagRepository;


    public BookService(AladinBookApiService aladinBookApiService, BookRepository bookRepository, BookExternalMapper bookExternalMapper, BookResponseMapper bookResponseMapper, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository, TagRepository tagRepository) {
        this.aladinBookApiService = aladinBookApiService;
        this.bookRepository = bookRepository;
        this.bookExternalMapper = bookExternalMapper;
        this.bookResponseMapper = bookResponseMapper;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.tagRepository = tagRepository;
    }

    //도서 등록 전 도서 검색
    public List<BookExternalResponse> searchBookFromExternalApi(String title) {
        List<BookExternalResponse> bookExternalResponseDtos = aladinBookApiService.searchBooksByTitle(title);

        if (bookExternalResponseDtos == null || bookExternalResponseDtos.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }
        return bookExternalResponseDtos;
    }


    //도서 수정 전 도서 검색
    @Transactional(readOnly = true)
    public BookDetailResponse searchBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }

        Book bookEntity = book.get();
        log.info("조회된 도서의 id : {}", bookEntity.getId());

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }




    @Transactional
    public BookDetailResponse registerBook(String isbn) {
        //이미 등록된 도서인 경우
        log.info("도서 등록 시작: ISBN={}", isbn);
        if (bookRepository.existsByIsbn(isbn)) {
            log.warn("이미 등록된 도서: ISBN={}", isbn);
            throw new BookAlreadyExistsException("이미 등록된 도서입니다.");
        }

        BookExternalResponse bookByIsbn = aladinBookApiService.findBookByIsbn(isbn);

        if (bookByIsbn == null) {
            throw new NotFoundBookException("ISBN과 일치하는 도서가 없습니다.");
        }

        Category parentCategory = getCategory(bookByIsbn);


        Book bookEntity = bookExternalMapper.toBookEntity(bookByIsbn);
        bookRepository.save(bookEntity);
        log.info("도서 등록 완료 : ID={}, ISBN={}", bookEntity.getId(),isbn);


        BookCategory bookCategory = new BookCategory(bookEntity,parentCategory);
        bookCategoryRepository.save(bookCategory);
        log.info("도서-카테고리 등록 완료 - 도서 이름 : {}, 카테고리(하위) 이름 : {}", bookCategory.getBook().getTitle(),bookCategory.getCategory().getCategoryName());


        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }


    //카테고리 파싱
    private Category getCategory(BookExternalResponse bookByIsbn) {

        String[] categoryNames = bookByIsbn.getCategoryName().split("<");
        Category parentCategory = null;
        Category currentCategory;

        for(String categoryName : categoryNames){
            Category tempParentCategory = parentCategory;
            String trimmedCategoryName = categoryName.trim();
            currentCategory = categoryRepository.findByCategoryNameAndParentCategory(trimmedCategoryName,parentCategory)
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setCategoryName(trimmedCategoryName);
                        newCategory.setParentCategory(tempParentCategory);
                        return categoryRepository.save(newCategory);
                    });
            parentCategory = currentCategory;
        }
        return parentCategory;
    }


    @Transactional
    public void updateBook(Long id, BookUpdateRequest requestDto) {
        Optional<Book> bookById = bookRepository.findById(id);

        if(bookById.isEmpty()){
            log.info("해당 도서를 찾을 수 없습니다. id : {}", id);
            throw new NotFoundBookException("해당 도서는 존재하지 않아서 수정이 불가능합니다.");
        }

        Book book = bookById.get();
        String description = requestDto.getDescription();
        String contents = requestDto.getContents();
        Integer price = requestDto.getPrice();

        if(description != null){
            book.setDescription(description);
        }
        if(contents != null) {
            book.setContents(contents);
        }
        if(price != null){
            book.setRegularPrice(price);
        }

    }

    @Transactional
    public void deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if(book.isEmpty()){
            log.warn("도서를 찾을 수 없습니다. id : {}",id);
            throw new NotFoundBookException("id : " + id + "에 해당하는 도서를 찾을 수 없습니다.");
        }

        Book targetBook = book.get();
        bookRepository.delete(targetBook);

        log.info("삭제된 도서 제목 : {}" , targetBook.getTitle());

    }

}
