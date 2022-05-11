package com.example.bookajax.controller;

import com.example.bookajax.model.Book;
import com.example.bookajax.model.BookForm;
import com.example.bookajax.model.Category;
import com.example.bookajax.service.book.IBookService;
import com.example.bookajax.service.category.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/book")
@CrossOrigin("*")
public class BookController {

    @Autowired
    private IBookService bookService;

    @Autowired
    private ICategoryService categoryService;

    @ModelAttribute("categories")
    private Iterable<Category> categories(){
        return categoryService.findAll();
    }

    @Autowired
    Environment env;


    @GetMapping
    public ResponseEntity<Page<Book>> showList(@RequestParam(name = "q")Optional<String> q, @PageableDefault(value = 3)Pageable pageable) {
        Page<Book> books;
        if (!q.isPresent()) {
            books = bookService.findAll(pageable);
        } else {
            books = bookService.findAllByNameContaining(q.get(), pageable);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


    @GetMapping("/cate")
    public ResponseEntity<Iterable<Category>> showAllCategory(){
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Book> saveBook(@ModelAttribute BookForm bookForm) {
        MultipartFile multipartFile = bookForm.getImage();
        String fileName =multipartFile.getOriginalFilename();
        String fileUpload = env.getProperty("upload.path");
        try {
            FileCopyUtils.copy(multipartFile.getBytes(),new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Book book = new Book(bookForm.getName(), bookForm.getPrice(), bookForm.getAuthor(),fileName, bookForm.getCategory());
        bookService.save(book);
        return new ResponseEntity<>(book,HttpStatus.ACCEPTED);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable Long id) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (!bookOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        bookService.remove(id);
        return new ResponseEntity<>(bookOptional.get(), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Book> editBook(@PathVariable Long id, @ModelAttribute BookForm bookForm){
        Optional<Book> bookOptional=bookService.findById(id);
        bookForm.setId(bookOptional.get().getId());
        MultipartFile multipartFile = bookForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        String fileUpload = env.getProperty("upload.path");
        Book book = new Book(id, bookForm.getName(), bookForm.getPrice(), bookForm.getAuthor(), fileName,bookForm.getCategory());
        try {
            FileCopyUtils.copy(multipartFile.getBytes(),new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bookService.save(book);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findOne(@PathVariable Long id){
        Book book = bookService.findById(id).get();
        return new ResponseEntity<>(book,HttpStatus.OK);
    }


}
