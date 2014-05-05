package knihovna;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;

/**
 *
 * @author Honza
 */
public interface BorrowingManager {
    void addBorrowing(Borrowing borrowing);
    void updateBorrowing(Borrowing borrowing);
    void deleteBorrowing(Borrowing borrowing);
    List<Borrowing> findAllBorrowing();
    Borrowing findBorrowingById(Long id);
    List<Borrowing> findBorrowingByReader(Reader reader);
}
