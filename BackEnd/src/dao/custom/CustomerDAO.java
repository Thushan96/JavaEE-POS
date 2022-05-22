package dao.custom;

import dao.CrudDAO;
import entity.Customer;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.sql.Connection;
import java.sql.SQLException;


public interface CustomerDAO extends CrudDAO<Customer, String> {
    JsonArray generateId(Connection connection) throws SQLException, ClassNotFoundException;
}