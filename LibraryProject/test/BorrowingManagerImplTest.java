/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import knihovna.BookManagerImpl;
import knihovna.Book;
import knihovna.BorrowingManagerImpl;
import knihovna.Borrowing;
import knihovna.DBUtils;
import knihovna.ReaderManagerImpl;
import knihovna.ReaderManager;
import knihovna.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
//import static BookManagerImplTest.newBook;

/**
 *
 * @author Helix
 */
public class BorrowingManagerImplTest {
    
    private ReaderManagerImpl readerManager;
    private BookManagerImpl bookManager;
    private BorrowingManagerImpl manager;   
    private DataSource ds;
    
    private static DataSource prepareDataSource() throws SQLException{
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:libraryProject;create=true");        
        return ds;
    }
    
    private Reader r1, r2, r3;
    private Book b1, b2, b3, b4, b5;
        
    private void prepareTestData(){
        r1 = ReaderManagerImplTest.createReader("Reader1","adress1",123456789);
        r2 = ReaderManagerImplTest.createReader("Reader2","adress2",123556789);
        r3 = ReaderManagerImplTest.createReader("Reader3","adress3",127756789);
        
        b1 = BookManagerImplTest.newBook("Book1", "Author1", 5, 123, "Title1");
        b2 = BookManagerImplTest.newBook("Book2", "Author2", 2, 325, "Title1");
        b3 = BookManagerImplTest.newBook("Book3", "Author3", 5, 125, "Title1");
        b4 = BookManagerImplTest.newBook("Book4", "Author4", 1, 135, "Title1");
        b5 = BookManagerImplTest.newBook("Book5", "Author5", 5, 546, "Title1");
               
        
        readerManager.addReader(r1);
        readerManager.addReader(r2);
        readerManager.addReader(r3);
        
        bookManager.addBook(b1);
        bookManager.addBook(b2);
        bookManager.addBook(b3);
        bookManager.addBook(b4);
        bookManager.addBook(b5);
        
       /* bookWithNullId = BookManagerImplTest.newBook("Book with null id","No id", 5, 126, "Genre");
        bookNotInDB = BookManagerImplTest.newBook("Book not in DB","Author", 5, 126, "Genre");
        bookNotInDB.setId(b3.getId() + 100);
        readerWithNullId = ReaderManagerImplTest.createReader("Reader with null id","adress53",3845);
        readerNotInDB = ReaderManagerImplTest.createReader("Reader not in DB","adresa",3877);
        readerNotInDB.setId(r3.getId() + 100);  */          
        
    }
    
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        if(ds== null){
            throw new NullPointerException("DataSource is null.SetUp");
        }
        DBUtils.executeSqlScript(ds, ReaderManager.class.getResource("createTables.sql"));
        
        manager = new BorrowingManagerImpl();
        manager.setDataSource(ds);
        readerManager = new ReaderManagerImpl();
        readerManager.setDataSource(ds);
        bookManager = new BookManagerImpl();
        bookManager.setDataSource(ds);
        
        prepareTestData();
    }
    
    @After
    public void tearDown() throws SQLException {
        if(ds== null){
            throw new NullPointerException("DataSource is null.");
        }
        DBUtils.executeSqlScript(ds, ReaderManager.class.getResource("dropTables.sql"));
    }

    @Test
    public void testAddBorrowing() {
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(2013, 3, 21);
        Calendar calTo = Calendar.getInstance();
        calTo.set(2013, 4, 21);
        
        Borrowing br1 = newBorrowing(r1, b1, calFrom, calTo);
        manager.addBorrowing(br1);
        
        Long Id = br1.getId();        
        assertNotNull("Borrowing id is null.",Id);        
        Borrowing result = manager.findBorrowingById(Id);
                
        assertEquals("Asserting borrowing with result.",br1, result);
        assertNotSame("Borrowing and result, assertNotSame",br1, result);
        assertBorrowingDeepEquals(br1, result);
        
        
    }

    @Test
    public void testUpdateBorrowing() {
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(2013, 3, 21);
        Calendar calTo = Calendar.getInstance();
        calTo.set(2013, 4, 21);
        
        Borrowing br1 = newBorrowing(r1, b1, calFrom, calTo);
        manager.addBorrowing(br1);
        Borrowing br2 = newBorrowing(r2, b2, calFrom, calTo);
        manager.addBorrowing(br2);
        
        Long Id = br1.getId();
        Borrowing result;
        
        br1 = manager.findBorrowingById(Id);
        br1.setBook(b3);
        manager.updateBorrowing(br1);
        result = manager.findBorrowingById(Id);
        assertBorrowingDeepEquals(br1, result);
        
       
        assertBorrowingDeepEquals(br2, manager.findBorrowingById(br2.getId()));
    }

    @Test
    public void testDeleteBorrowing() {
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(2013, 3, 21);
        Calendar calTo = Calendar.getInstance();
        calTo.set(2013, 4, 21);
        
        Borrowing br1 = newBorrowing(r1, b1, calFrom, calTo);
        manager.addBorrowing(br1);
        Borrowing br2 = newBorrowing(r2, b2, calFrom, calTo);
        manager.addBorrowing(br2);
        
        assertNotNull("br1 is null.", manager.findBorrowingById(br1.getId()));
        assertNotNull("br2 is null.", manager.findBorrowingById(br2.getId()));
        
        manager.deleteBorrowing(br1);
        
        assertNull("br1 is not null.", manager.findBorrowingById(br1.getId()));
        assertNotNull("br2 is null.", manager.findBorrowingById(br2.getId()));
    }

    @Test
    public void testFindAllBorrowing() {
        assertTrue(manager.findAllBorrowing().isEmpty());
        
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(2013, 3, 21);
        Calendar calTo = Calendar.getInstance();
        calTo.set(2013, 4, 21);
        
        Borrowing br1 = newBorrowing(r1, b1, calFrom, calTo);
        manager.addBorrowing(br1);
        Borrowing br2 = newBorrowing(r2, b2, calFrom, calTo);
        manager.addBorrowing(br2);
        
        List<Borrowing> expected = Arrays.asList(br1,br2);
        List<Borrowing> actual = manager.findAllBorrowing();
        
        assertEquals(expected, actual);
        assertBorrowingCollectionEquals(expected, actual);
    }

    @Test
    public void testFindBorrowingById() {
        assertNull("Borrowing is not null and should be.",manager.findBorrowingById(1l));
        
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(2013, 3, 21);
        Calendar calTo = Calendar.getInstance();
        calTo.set(2013, 4, 21);
        
        Borrowing br1 = newBorrowing(r1, b1, calFrom, calTo);
        manager.addBorrowing(br1);
        
        Long Id = br1.getId();                
        Borrowing result = manager.findBorrowingById(Id);
                
        assertEquals("Testing ID, result is not the same.", br1, result);
        assertBorrowingDeepEquals(br1, result);
    }

    @Test
    public void testFindBorrowingByReader() {
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(2013, 3, 21);
        Calendar calTo = Calendar.getInstance();
        calTo.set(2013, 4, 21);
        
        Borrowing br1 = newBorrowing(r1, b1, calFrom, calTo);
        manager.addBorrowing(br1);
        Borrowing br2 = newBorrowing(r2, b2, calFrom, calTo);
        manager.addBorrowing(br2);
        Borrowing br3 = newBorrowing(r2, b2, calFrom, calTo);
        manager.addBorrowing(br3);
        
        Reader reader = br2.getReader();
        List<Borrowing> expected = Arrays.asList(br2,br3);
        List<Borrowing> actual = manager.findBorrowingByReader(reader);
        
        assertEquals(expected, actual);
        assertBorrowingCollectionEquals(expected, actual);
    }
    
    private static Comparator<Borrowing> borrowingComparator = new Comparator<Borrowing>(){
        
        @Override
        public int compare(Borrowing b1, Borrowing b2){
            return b1.getId().compareTo(b2.getId());
        }
    };
    
    private static Borrowing newBorrowing(Reader r, Book b,Calendar From,Calendar To){
        Borrowing borrowing = new Borrowing();
        borrowing.setBook(b);
        borrowing.setReader(r);
        borrowing.setBookBorrowedFrom(From);
        borrowing.setBookBorrowedTo(To);
        
        return borrowing;
    }

    private static void assertBorrowingDeepEquals(Borrowing expected, Borrowing actual) {
        assertEquals("Book is not the same ",expected.getBook(), actual.getBook());
        assertEquals("Reader is not the same ",expected.getReader(), actual.getReader());
        assertEquals("From is not the same ",expected.getBookBorrowedFrom(), actual.getBookBorrowedFrom());
        assertEquals("To is not the same ",expected.getBookBorrowedTo(), actual.getBookBorrowedTo());
    }
    
    static void assertBorrowingCollectionEquals(List<Borrowing> expected, List<Borrowing> actual){
        
        assertEquals("Size of the lists of borrwoing is not same.",expected.size(), actual.size());
        List<Borrowing> expectedSorted = new ArrayList<>(expected);
        List<Borrowing> actualSorted = new ArrayList<>(actual);
        Collections.sort(expectedSorted, borrowingComparator);
        Collections.sort(actualSorted, borrowingComparator);
        
        for (int i = 0; i < expectedSorted.size(); i++){
            assertBorrowingDeepEquals(expectedSorted.get(i), actualSorted.get(i));
        }
        
    }
}
