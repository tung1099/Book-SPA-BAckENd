package com.example.bookajax.service.book;

import com.example.bookajax.model.Book;
import com.example.bookajax.model.Category;
import com.example.bookajax.service.GeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookService extends GeneralService<Book> {
    Iterable<Book> findAllByCategory(Category category);
    Page<Book> findAll(Pageable pageable);
    Page<Book> findAllByNameContaining(String name, Pageable pageable);
}