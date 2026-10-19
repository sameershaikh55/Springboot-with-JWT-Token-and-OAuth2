/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UpdateBookFailedException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * @author tdoy
 */
@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	
	public Book saveBook(Book book) {
		
		return bookRepository.save(book);
		
	}
	
	public List<Book> findAll(){
		
		return (List<Book>) bookRepository.findAll();
		
	}
	
	
	public Book findByISBN(String isbn) throws BookNotFoundException {
		
		Book book = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));
		
		return book;
	}
	
	// TODO public Book updateBook(Book book, String isbn)
	public Book updateBook(Book book, String isbn) throws BookNotFoundException {
		// 1. Find the existing book. Throws exception if it doesn't exist.
		Book existingBook = findByISBN(isbn);
		
		// 2. Update the mutable fields
		if (book.getTitle() != null) {
			existingBook.setTitle(book.getTitle());
		}
		if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
			existingBook.setAuthors(book.getAuthors());
		}		
		// 3. Save and return the updated entity
		return bookRepository.save(existingBook);
	}
	
	// TODO public List<Book> findAllPaginate(Pageable page)
	public List<Book> findAllPaginate(Pageable page) {
		// Uses Spring Data JPA's built-in pagination to return a specific "chunk" of books
		Page<Book> pagedResult = bookRepository.findAll(page);
		return pagedResult.getContent();
	}
	
	// TODO public Set<Author> findAuthorsOfBookByISBN(String isbn)
	public Set<Author> findAuthorsOfBookByISBN(String isbn) throws BookNotFoundException {
		// Fetch the book and return its attached HashSet of Authors
		Book book = findByISBN(isbn);
		return book.getAuthors();
	}
	
	// TODO public void deleteById(long id)
	public void deleteById(long id) {
		// Direct deletion using the database ID
		bookRepository.deleteById(id);
	}
	
	// TODO public void deleteByISBN(String isbn)
	public void deleteByISBN(String isbn) throws BookNotFoundException {
		// Find the book by its unique ISBN first, then remove it
		Book book = findByISBN(isbn);
		
		// Clear many-to-many references to avoid join table constraint errors
		book.getAuthors().clear();
		bookRepository.save(book);
		
		bookRepository.delete(book);
	}
}
