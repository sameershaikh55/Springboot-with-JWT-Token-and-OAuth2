/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
		
	@Autowired
	private BookRepository bookRepository;
	
	public Author findById(int id) throws AuthorNotFoundException {
		
		Author author = authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));
		
		return author;
	}
	
	// TODO public saveAuthor(Author author)
	public Author saveAuthor(Author author) {
		return authorRepository.save(author);
	}
		
	
	// TODO public Author updateAuthor(Author author, int id)
	public Author updateAuthor(Author author, int id) throws AuthorNotFoundException {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(
                        "Cannot update. Author with id " + id + " not found"));

        existing.setFirstname(author.getFirstname());
        existing.setLastname(author.getLastname());

        return authorRepository.save(existing);
    }
	
	// TODO public List<Author> findAll()
	public List<Author> findAll() {
        return (List<Author>) authorRepository.findAll();
    }

	
	// TODO public void deleteById(int id) throws AuthorNotFoundException 
	public void deleteAuthor(int id) throws AuthorNotFoundException {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(
                        "Cannot delete. Author with id " + id + " not found"));
        
        authorRepository.delete(existing);
    }
	
	// TODO public Set<Book> findBooksByAuthorId(int id)
	public List<Book> findBooksByAuthorId(int id) throws AuthorNotFoundException {
		authorRepository.findById(id)
				.orElseThrow(() -> new AuthorNotFoundException
						("Cannot find books. Author with id " + id + " not found"));
		
		List<Book> books = bookRepository.findBooksByAuthorId(id);
		return books;
	}
	
}
