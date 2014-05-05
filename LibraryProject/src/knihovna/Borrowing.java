package knihovna;


import java.util.Calendar;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Honza
 */
public class Borrowing {
    Long id;
    Calendar bookBorrowedFrom;
    Calendar bookBorrowedTo;
    Reader reader;
    Book book;

    public Long getId() {
        return id;
    }

    public Calendar getBookBorrowedFrom() {
        return bookBorrowedFrom;
    }

    public Calendar getBookBorrowedTo() {
        return bookBorrowedTo;
    }

    public Reader getReader() {
        return reader;
    }

    public Book getBook() {
        return book;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookBorrowedFrom(Calendar bookBorrowedFrom) {
        this.bookBorrowedFrom = bookBorrowedFrom;
    }

    public void setBookBorrowedTo(Calendar bookBorrowedTo) {
        this.bookBorrowedTo = bookBorrowedTo;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Borrowing other = (Borrowing) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Borrowing{" + "id=" + id + ", borrowedFrom=" + bookBorrowedFrom + ", borrowedTo=" + bookBorrowedTo + ", reader=" + reader + ", book=" + book + '}';
    }
}
