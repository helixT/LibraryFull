package knihovna;


import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Vojtech Bayer
 */
public interface BookManager {
    
    /**
     * Stores book to databse.
     * @param book 
     */
    void addBook(Book book);
    
    /**
     * Updates book.
     * @param book 
     */
    void updateBook(Book book);
    
    void deleteBook(Book book);
    
    List<Book> findAllBooks();
    
    List<Book> findBooksByAuthor(String author);
    
    List<Book> findBooksByGenre(String genre);
    
    List<Book> findBooksByTitle(String title);
    
    Book getBookById(Long id);
    
}
