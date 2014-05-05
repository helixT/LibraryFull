/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import knihovna.BookManagerImpl;
import knihovna.IllegalEntityException;
import knihovna.Book;
import knihovna.DBUtils;
import knihovna.BookManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Vojtch Bayer
 */
public class BookManagerImplTest {
    
    private BookManagerImpl manager;
    private DataSource dataSource;
    
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:derby:memory:libraryProject;create=true");
        return dataSource;
    }
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, BookManager.class.getResource("createTables.sql"));
        manager = new BookManagerImpl();
        manager.setDataSource(dataSource);
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, BookManager.class.getResource("dropTables.sql"));
    }

    @Test
    public void testAddBook() {
        
        Book book = newBook("Book","Author",2,1245,"Genre");
        manager.addBook(book);
        
        Long bookId = book.getId();        
        assertNotNull("Book is null.",bookId);        
        Book result = manager.getBookById(bookId);
                
        assertEquals("Asserting book with result.",book, result);
        assertNotSame("Book and result, assertNotSame",book, result);
        assertBookDeepEquals(book, result);
    }
    
    @Test
    public void testAddBookWithWrongArguments(){
        
        try{
            manager.addBook(null);
            fail("Trying to add null.");
        } catch(IllegalArgumentException ex){
            //OK
        }
        
        Book book = newBook("Book","Author",2,1245,"Genre");
        book.setId(1l);
        try{
            manager.addBook(book);
            fail("Book with wrong ID has been added.");
        } catch(IllegalArgumentException ex){
            //OK
        }
        
        book = newBook(null,"Author",2,1245,"Genre");
        try{
            manager.addBook(book);
            fail("Book without title has been added.");
        } catch(IllegalArgumentException ex){
            //OK
        }
        
        book = newBook("Title",null,2,1245,"Genre");
        try{
            manager.addBook(book);
            fail("Book without author has been added.");
        } catch(IllegalArgumentException ex){
            //OK
        }
        
        book = newBook("Title","Author",-2,1245,"Genre");
        try{
            manager.addBook(book);
            fail("Book with wrong quantity.");
        } catch(IllegalArgumentException ex){
            //OK
        }
        
        book = newBook("Title","Author",0,1245,"Genre");
        manager.addBook(book);
        Book result = manager.getBookById(book.getId()); 
        assertNotNull("result is not null.",result);
        assertBookDeepEquals(book, result);
        
        
    }

    @Test
    public void testUpdateBook() {
        
        Book b1 = newBook("Book","Author",2,1245,"Genre");
        Book b2 = newBook("Book2","Author2",2,1745,"Genre2");
        
        manager.addBook(b1);
        manager.addBook(b2);
        
        Long bookID = b1.getId();
        Book result;
        
        b1 = manager.getBookById(bookID);
        b1.setAuthor("Rowling");
        manager.updateBook(b1);
        result = manager.getBookById(bookID);
        assertBookDeepEquals(b1, result);
        
        b1 = manager.getBookById(bookID);
        b1.setGenre("Fantasy");
        manager.updateBook(b1);
        result = manager.getBookById(bookID);
        assertBookDeepEquals(b1, result);
        
        b1 = manager.getBookById(bookID);
        b1.setIsbn(451123);
        manager.updateBook(b1);
        result = manager.getBookById(bookID);
        assertBookDeepEquals(b1, result);
        
        b1 = manager.getBookById(bookID);
        b1.setQuantity(45);
        manager.updateBook(b1);
        result = manager.getBookById(bookID);
        assertBookDeepEquals(b1, result);
        
        b1 = manager.getBookById(bookID);
        b1.setTitle("Hobit");
        manager.updateBook(b1);
        result = manager.getBookById(bookID);
        assertBookDeepEquals(b1, result);
        
        assertBookDeepEquals(b2, manager.getBookById(b2.getId()));
       

    }
    
    @Test
    public void testUpdateBookWithWrongArguments(){
        
        Book book = newBook("Book","Author",2,1245,"Genre");
        manager.addBook(book);        
        Long bookId = book.getId(); 
        
        try {
            manager.updateBook(null);
            fail("Trying to update null.");
        } catch (IllegalEntityException | IllegalArgumentException ex) {
            //OK
        }
        
        try {
            book = manager.getBookById(bookId);
            book.setId(null);
            manager.updateBook(book);        
            fail("Trying to update book without id.");
        } catch (IllegalEntityException | IllegalArgumentException ex) {
            //OK
        }
        
        try {
            book = manager.getBookById(bookId);
            book.setId(bookId - 1);
            manager.updateBook(book);        
            fail("Trying to update book with wrong id.");
        } catch (IllegalEntityException | IllegalArgumentException ex) {
            //OK
        }       
                
    }

    @Test
    public void testDeleteBook() {
        
        Book b1 = newBook("Kniha","Capek",12,12456,"Roman");
        Book b2 = newBook("Kniha2","Nemcova",3,56454,"Horor");
        
        manager.addBook(b1);
        manager.addBook(b2);
        
        assertNotNull("b1 is null.", manager.getBookById(b1.getId()));
        assertNotNull("b2 is null.", manager.getBookById(b2.getId()));
        
        manager.deleteBook(b1);
        
        assertNull("b1 is not null.", manager.getBookById(b1.getId()));
        assertNotNull("b2 is null.", manager.getBookById(b2.getId()));
        
    }
    
    @Test
    public void testDeleteBookWithWrongArguments(){
        
        Book book = newBook("Book","Author",2,1245,"Genre");
        manager.addBook(book);        
        Long bookId = book.getId(); 
        
        try {
            manager.deleteBook(null);
            fail("Trying to delete null.");
        } catch (IllegalEntityException | IllegalArgumentException ex) {
            //OK
        }
        
        try {
            book = manager.getBookById(bookId);
            book.setId(null);
            manager.deleteBook(book);        
            fail("Trying to delete book without id.");
        } catch (IllegalEntityException | IllegalArgumentException ex) {
            //OK
        }
        
        try {
            book = manager.getBookById(bookId);
            book.setId(bookId - 1);
            manager.deleteBook(book);        
            fail("Trying to delete book with wrong id.");
        } catch (IllegalEntityException | IllegalArgumentException ex) {
            //OK
        }        
        
    }

    @Test
    public void testFindAllBooks() {
        
        assertTrue(manager.findAllBooks().isEmpty());
        
        Book b1 = newBook("Title1","Wake",14,15556,"Roman");
        Book b2 = newBook("Title02","Williams",3,7454,"Horor");
        
        manager.addBook(b1);
        manager.addBook(b2);
        
        List<Book> expected = Arrays.asList(b1,b2);
        List<Book> actual = manager.findAllBooks();
        
        assertEquals(expected, actual);
        assertBookCollectionEquals(expected, actual);
        
    }

    @Test
    public void testFindBooksByAuthor() {
        Book b1 = newBook("Title1","Wake",14,15556,"Roman");
        Book b2 = newBook("Title02","Williams",3,7454,"Horor");
        Book b3 = newBook("Title03","Williams",1,74784,"Horor");
        
        manager.addBook(b1);
        manager.addBook(b2);
        manager.addBook(b3);
        
        String author = b2.getAuthor();
        List<Book> expected = Arrays.asList(b2,b3);
        List<Book> actual = manager.findBooksByAuthor(author);
        
        assertEquals(expected, actual);
        assertBookCollectionEquals(expected, actual);
    }

    @Test
    public void testFindBooksByGenre() {
        Book b1 = newBook("Title1","Wake",14,15556,"Roman");
        Book b2 = newBook("Title02","Williams",3,7454,"Horor");
        Book b3 = newBook("Title03","Williams",1,74784,"Horor");
        
        manager.addBook(b1);
        manager.addBook(b2);
        manager.addBook(b3);
        
        String genre = b2.getGenre();
        List<Book> expected = Arrays.asList(b2,b3);
        List<Book> actual = manager.findBooksByGenre(genre);
        
        assertEquals(expected, actual);
        assertBookCollectionEquals(expected, actual);
    }

    @Test
    public void testFindBooksByTitle() {
        Book b1 = newBook("Title1","Wake",14,15556,"Roman");
        Book b2 = newBook("Title02","Williams",3,7454,"Horor");
        Book b3 = newBook("Title1","Williams",1,74464,"Horor");
        
        manager.addBook(b1);
        manager.addBook(b2);
        manager.addBook(b3);
        
        String title = b1.getTitle();
        List<Book> expected = Arrays.asList(b1,b3);
        List<Book> actual = manager.findBooksByTitle(title);
        
        assertEquals(expected, actual);
        assertBookCollectionEquals(expected, actual);
    }

    @Test
    public void testGetBookById() {
        
        assertNull("Book is not null and should be.",manager.getBookById(1l));
        Book book = newBook("Book","Author",2,1245,"Genre");
        manager.addBook(book);
        
        Long bookId = book.getId();                
        Book result = manager.getBookById(bookId);
                
        assertEquals("Testing ID, result is not the same.", book, result);
        assertBookDeepEquals(book, result);
        
        
    }
    
   static void assertBookDeepEquals(Book expected, Book actual) {
        assertEquals("ID of books.", expected.getId(), actual.getId());
        assertEquals("Author of books.", expected.getAuthor(), actual.getAuthor());
        assertEquals("Genre of books.", expected.getGenre(), actual.getGenre());
        assertEquals("Isbn of books.", expected.getIsbn(),actual.getIsbn());
        assertEquals("Quantity of books.", expected.getQuantity(), actual.getQuantity());
        assertEquals("Title of books.", expected.getTitle(), actual.getTitle()); 
    } 
    
    static void assertBookCollectionEquals(List<Book> expected, List<Book> actual){
        
        assertEquals("Size of the list with books",expected.size(), actual.size());
        List<Book> expectedSorted = new ArrayList<>(expected);
        List<Book> actualSorted = new ArrayList<>(actual);
        Collections.sort(expectedSorted, bookComparator);
        Collections.sort(actualSorted, bookComparator);
        
        for (int i = 0; i < expectedSorted.size(); i++){
            assertBookDeepEquals(expectedSorted.get(i), actualSorted.get(i));
        }
        
    }
    
    private static Comparator<Book> bookComparator = new Comparator<Book>(){
        
        @Override
        public int compare(Book b1, Book b2){
            return b1.getId().compareTo(b2.getId());
        }
    };
    
   static Book newBook(String title, String author, int qunatity, int isbn, String genre){
        Book book = new Book();
        book.setAuthor(author);
        book.setGenre(genre);
        book.setIsbn(isbn);
        book.setQuantity(qunatity);
        book.setTitle(title);
        return book;
    }
    
}
