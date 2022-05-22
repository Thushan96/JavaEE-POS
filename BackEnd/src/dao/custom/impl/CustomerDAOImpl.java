package dao.custom.impl;

import dao.CrudUtil;
import dao.custom.CustomerDAO;
import entity.Customer;

import javax.json.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public JsonArray getAll(Connection connection) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM Customer");
        JsonArrayBuilder customerArray = Json.createArrayBuilder();
        while (rst.next()) {
            Customer customer = new Customer(rst.getString(1), rst.getString(2), rst.getString(3), rst.getDouble(4));
            JsonObjectBuilder customerObj = Json.createObjectBuilder();
            customerObj.add("id", customer.getId());
            customerObj.add("name", customer.getName());
            customerObj.add("address", customer.getAddress());
            customerObj.add("salary", customer.getSalary());
            customerArray.add(customerObj.build());
        }
        return customerArray.build();
    }

    @Override
    public boolean add(Connection connection, Customer customer) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "INSERT INTO Customer Values (?,?,?,?)", customer.getId(), customer.getName(), customer.getAddress(), customer.getSalary());
    }

    @Override
    public boolean update(Connection connection, Customer customer) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "UPDATE Customer SET name=?,address=?,salary=? WHERE id=?", customer.getName(), customer.getAddress(), customer.getSalary(), customer.getId());
    }

    @Override
    public boolean delete(Connection connection, String id) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "DELETE FROM Customer WHERE id=?", id);
    }

    @Override
    public Customer search(Connection connection, String id) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM Customer WHERE id=?", id);
        Customer customer = null;
        while (rst.next()) {
            customer = new Customer(rst.getString(1), rst.getString(2), rst.getString(3), rst.getDouble(4));
        }
        return customer;
    }

    @Override
    public JsonArray generateId(Connection connection) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery(connection, "SELECT id FROM Customer");
        JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
        while (resultSet.next()){
            String id = resultSet.getString(1);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("id", id);
            arrayBuilder2.add(objectBuilder.build());
        }
        return arrayBuilder2.build();
    }

}