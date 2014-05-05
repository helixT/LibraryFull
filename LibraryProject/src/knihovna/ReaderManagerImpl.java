package knihovna;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author jduda
 */
public class ReaderManagerImpl implements ReaderManager {
    public static final Logger logger = Logger.getLogger(ReaderManagerImpl.class.getName());
    
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
    public void addReader(Reader reader) throws ServiceFailureException {
        checkDataSource();
        correctInputReader(reader);
        
        if(reader.getId() != null) {
            throw new IllegalArgumentException("Reader's id isn't null!");            
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); 
            st = conn.prepareStatement("INSERT INTO READER (fullname,adress,"
                    + "phonenumber) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            
            st.setString(1, reader.getFullName());
            st.setString(2, reader.getAdress());
            
            if(reader.getPhoneNumber() == null){
                st.setNull(3, java.sql.Types.INTEGER);
            } else{
                st.setInt(3, reader.getPhoneNumber());
            }
            
            int addedRows = st.executeUpdate();
            DBUtils.checkUpdatesCount(addedRows, reader, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            reader.setId(id);
            
            conn.commit();
            
        } catch (SQLException ex) {
            String message = "Error when inserting reader" + reader;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.doRollback(conn);
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public void updateReader(Reader reader) throws ServiceFailureException {
        checkDataSource();
        correctInputReader(reader);
        if(reader.getId() == null) {
            throw new IllegalArgumentException("Readed's id is null!");            
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); 
            st = conn.prepareStatement("UPDATE READER SET fullname=?,adress=?,phonenumber=? WHERE id=?");
            
            st.setString(1, reader.getFullName());
            st.setString(2, reader.getAdress());
            if(reader.getPhoneNumber() == null){
                st.setNull(3, java.sql.Types.INTEGER);
            } else{
                st.setInt(3, reader.getPhoneNumber());
            }
            st.setLong(4, reader.getId());
            
            int updatedRows = st.executeUpdate();
            DBUtils.checkUpdatesCount(updatedRows, reader, false);
            
            conn.commit();
                        
        }catch(SQLException ex){
            String message = "Error when updating reader" + reader;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.doRollback(conn);
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public void deleteReader(Reader reader) throws ServiceFailureException {
        checkDataSource();
        if(reader == null){
            throw new IllegalArgumentException("Input reader is null!");
        }
        if(reader.getId() == null){
            throw new IllegalArgumentException("Input reader's id is null!");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); 
            st = conn.prepareStatement("DELETE FROM READER WHERE id=? AND fullname=? AND adress=? AND phonenumber=?");
            
            st.setLong(1, reader.getId());
            st.setString(2, reader.getFullName());
            st.setString(3, reader.getAdress());
            st.setInt(4, reader.getPhoneNumber());
            
            int deletedRows = st.executeUpdate();
            DBUtils.checkUpdatesCount(deletedRows, reader, false);
            
            conn.commit();
            
        } catch (SQLException ex) {
            String message = "Error when deleting reader" + reader;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.doRollback(conn);
            DBUtils.closeConnection(conn, st);
        }
    }

    @Override
    public Reader findReaderById(Long id) throws ServiceFailureException {
        checkDataSource();
        if(id.intValue() <= 0){
            throw new IllegalArgumentException("Input id isn't positive number!");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            st = conn.prepareStatement("SELECT id,fullname,adress,phonenumber FROM reader WHERE id=?");
            
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Reader reader = resultToReader(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Error: Was founded more than one reader "
                            + "(Input id: " + id + " and were found " + reader + " and " + resultToReader(rs));
                }            
                
                return reader;
            } else {
                return null;
            }
            
        } catch (SQLException ex) {
            String message = "Error when finding reader by " + id;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        }         
    }

    @Override
    public List<Reader> findReaderByName(String name) throws ServiceFailureException {
        checkDataSource();
        if(name == null){
            throw new IllegalArgumentException("Input name is null!");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            st = conn.prepareStatement("SELECT id,fullname,adress,phonenumber FROM reader WHERE fullname=?");
            
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            List<Reader> listOfReaders = new ArrayList<>();
            
            while(rs.next()){
                listOfReaders.add(resultToReader(rs));
            }
            
            return listOfReaders;
        } catch (SQLException ex) {
            String message = "Error when finding reader name " + name;
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        }  
    }

    @Override
    public List<Reader> findAllReaders() throws ServiceFailureException {
        checkDataSource();
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            st = conn.prepareStatement("SELECT id,fullname,adress,phonenumber FROM reader");
            
            ResultSet rs = st.executeQuery();
            List<Reader> listOfReaders = new ArrayList<>();
            
            while(rs.next()){
                listOfReaders.add(resultToReader(rs));
            }
            
            return listOfReaders;
        } catch (SQLException ex) {
            String message = "Error when finding readers";
            logger.log(Level.SEVERE, message, ex);
            throw new ServiceFailureException(message, ex);
        } finally {
            DBUtils.closeConnection(conn, st);
        } 
    }

    private void correctInputReader(Reader reader) {
        if(reader == null) {
            throw new IllegalArgumentException("Input reader is null");            
        }
        if(reader.getFullName() == null) {
            throw new IllegalArgumentException("Reader's name is null");            
        }
        if(reader.getAdress() == null) {
            throw new IllegalArgumentException("Reader's adress is null");            
        }
        if(reader.getPhoneNumber() != null){
            if(reader.getPhoneNumber().intValue() <= 0){
                throw new IllegalArgumentException("Reader's phone number is neative!");
            }
            if(reader.getPhoneNumber().toString().length() != 9){
                throw new IllegalArgumentException("Reader's phone number hasn't got 9 digits!");
            }
        }
    }
    
    private Reader resultToReader(ResultSet rs) throws SQLException {
        Reader reader = new Reader();
        reader.setId(rs.getLong("id"));
        reader.setFullName(rs.getString("fullname"));
        reader.setAdress(rs.getString("adress"));
        if(rs.getInt("phonenumber") == 0){
            reader.setPhoneNumber(null);
        }else{
            reader.setPhoneNumber(rs.getInt("phonenumber"));
        }
        return reader;
    }
}
