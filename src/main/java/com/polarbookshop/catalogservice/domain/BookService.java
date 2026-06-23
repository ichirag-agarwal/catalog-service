package com.polarbookshop.catalogservice.domain;

import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookDetails(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public Book addBookToCatalog(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new BookAlreadyExistsException(request.isbn());
        }

        var book = Book.of(request.isbn(), request.title(), request.author(), request.price(), request.publisher());
        return bookRepository.save(book);
    }

    public void removeBookFromCatalog(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public Book editBookDetails(String isbn, BookRequest request) {
        return bookRepository.findByIsbn(isbn)
                .map(existingBook -> {
                    var bookToUpdate = new Book(
                            existingBook.id(),
                            existingBook.isbn(),
                            request.title(),
                            request.author(),
                            request.price(),
                            request.publisher(),
                            existingBook.createdDate(),
                            existingBook.lastModifiedDate(),
                            existingBook.version()
                    );
                    return bookRepository.save(bookToUpdate);
                })
                .orElseGet(() -> addBookToCatalog(request));
    }
}
