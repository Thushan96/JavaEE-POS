package dao;

import javax.json.JsonArray;
import java.sql.SQLException;

public interface CrudDAO<T, ID> extends SuperDAO {
    JsonArray getAll() throws SQLException, ClassNotFoundException;

    boolean add(T t) throws SQLException, ClassNotFoundException;

    boolean update(T t) throws SQLException, ClassNotFoundException;

    boolean delete(ID id) throws SQLException, ClassNotFoundException;

    T search(ID id) throws SQLException, ClassNotFoundException;
}
