package knihovna;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.sql.DataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Vojtech Bayer
 * @version 1.0
 */
public class BookManagerImpl implements BookManager {

    public static final Logger logger = Logger.getLogger(BookManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void addBook(Book book) {
        checkDataSource();
        correctInputBook(book);

        if (book.getId() != null) {
            throw new IllegalArgumentException("Book's id isn't null!");
        }

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO BOOK (author,genre,title,isbn,quantity) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, book.getAuthor());
            st.setString(2, book.getGenre());
            st.setString(3, book.getTitle());
            st.setInt(4, book.getIsbn());
            st.setInt(5, book.getQuantity());

            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("More rows inserted when tryig insert book" + book);
            }

            book.setId(DBUtils.getId(st.getGeneratedKeys()));
            conn.commit();

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when inserting book " + book, ex);
        } finally {
            DBUtils.doRollback(conn);
            DBUtils.closeConnection(conn, st);
        }

    }

    @Override
    public void updateBook(Book book) {
        checkDataSource();
        correctInputBook(book);
        if (book.getId() == null) {
            throw new IllegalArgumentException("Book's id is null!");
        }

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE BOOK SET author= ?, genre= ?, title= ?, isbn= ?, quantity= ? WHERE id = ?");
            st.setString(1, book.getAuthor());
            st.setString(2, book.getGenre());
            st.setString(3, book.getTitle());
            st.setInt(4, book.getIsbn());
            st.setInt(5, book.getQuantity());
            st.setLong(6, book.getId());

            int updatedRows = st.executeUpdate();
            DBUtils.checkUpdatesCount(updatedRows, book, false);

            conn.commit();

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when updating book." + book, ex);

        } finally {
            DBUtils.doRollback(conn);
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public void deleteBook(Book book) {
        checkDataSource();
        correctInputBook(book);
        if (book.getId() == null) {
            throw new IllegalArgumentException("Book's id is null!");
        }

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement("DELETE FROM BOOK WHERE id = ? AND title = ? AND author = ?");
            st.setLong(1, book.getId());
            st.setString(2, book.getTitle());
            st.setString(3, book.getAuthor());

            int deletedRows = st.executeUpdate();
            DBUtils.checkUpdatesCount(deletedRows, book, false);

            conn.commit();

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when updating book." + book, ex);
        } finally {
            DBUtils.doRollback(conn);
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public List<Book> findAllBooks() {
        checkDataSource();

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, genre, title, isbn, quantity FROM book");
            ResultSet rs = st.executeQuery();
            List<Book> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultSetToBook(rs));
            }
            return result;

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when retrieving book with id.", ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        checkDataSource();

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, genre, title, isbn, quantity FROM book WHERE author = ?");
            st.setString(1, author);
            ResultSet rs = st.executeQuery();
            List<Book> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultSetToBook(rs));
            }
            return result;

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when retrieving book with by author.", ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public List<Book> findBooksByGenre(String genre) {
        checkDataSource();

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, genre, title, isbn, quantity FROM book WHERE genre = ?");
            st.setString(1, genre);
            ResultSet rs = st.executeQuery();
            List<Book> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultSetToBook(rs));
            }
            return result;

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when retrieving book with by genre.", ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        checkDataSource();

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, genre, title, isbn, quantity FROM book WHERE title = ?");
            st.setString(1, title);
            ResultSet rs = st.executeQuery();
            List<Book> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultSetToBook(rs));
            }
            return result;

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when retrieving book with by title.", ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public Book getBookById(Long id) {

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, genre, title, isbn, quantity FROM book WHERE id = ? ");
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Book book = resultSetToBook(rs);

                if (rs.next()) {
                    throw new ServiceFailureException("More entities with the same id found"
                            + id + book + resultSetToBook(rs));
                }
                return book;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error when retrieving book with id.", ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        }
    }

    private void correctInputBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book is null.");
        }
        if (book.getQuantity() < 0) {
            throw new IllegalArgumentException("quantity is negative value");
        }
        if (book.getTitle() == null) {
            throw new IllegalArgumentException("Title is null.");
        }
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("Author is null.");
        }
        if (book.getIsbn() < 1) {
            throw new IllegalArgumentException("Isbn is negative.");
        }
    }

    private Book resultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();

        book.setId(rs.getLong("id"));
        book.setAuthor(rs.getString("author"));
        book.setGenre(rs.getString("genre"));
        book.setTitle(rs.getString("title"));
        book.setIsbn(rs.getInt("isbn"));
        book.setQuantity(rs.getInt("quantity"));

        return book;
    }

}
