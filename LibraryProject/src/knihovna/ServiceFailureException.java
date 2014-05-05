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
class ServiceFailureException extends RuntimeException{

    public ServiceFailureException() {
    }
    
    public ServiceFailureException(String msg){
        super(msg);
    }
    
    public ServiceFailureException(String msg, Throwable cause){
        super(msg,cause);
    }
    
}
