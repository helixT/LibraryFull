package knihovna;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Helix
 */
class ValidationException extends RuntimeException{

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }
    
    
}
