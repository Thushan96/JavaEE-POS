package dao.custom;

import dao.CrudDAO;
import entity.Customer;

import javax.json.JsonObject;
import java.sql.SQLException;


public interface CustomerDAO extends CrudDAO<Customer, String> {
    JsonObject generateId() throws SQLException, ClassNotFoundException;
}