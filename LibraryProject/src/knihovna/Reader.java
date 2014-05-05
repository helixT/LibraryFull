package knihovna;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Objects;

/**
 *
 * @author jduda
 */
public class Reader {
    
    private Long id;
    private String fullName;
    private String adress;
    private Integer phoneNumber;
    
        
    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAdress() {
        return adress;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Reader{" + "id=" + id + ", fullName=" + fullName + ", adress=" + adress + ", phone=" + phoneNumber + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final Reader other = (Reader) obj;
        return Objects.equals(this.id, other.id);
    }
    
}
