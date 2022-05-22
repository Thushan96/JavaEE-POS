package dao.custom.impl;

import dao.CrudUtil;
import dao.custom.ItemDAO;
import entity.Item;

import javax.json.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ItemDAOImpl implements ItemDAO {
    @Override
    public JsonArray getAll(Connection connection) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM Item");
        JsonArrayBuilder itemArray = Json.createArrayBuilder();

        while (rst.next()) {
            Item item = new Item(rst.getString(1), rst.getString(2), rst.getDouble(3), rst.getInt(4));
            JsonObjectBuilder itemObj = Json.createObjectBuilder();
            itemObj.add("code", item.getCode());
            itemObj.add("name", item.getName());
            itemObj.add("unitPrice", item.getUnitPrice());
            itemObj.add("qty", item.getQty());
            itemArray.add(itemObj.build());
        }
        return itemArray.build();
    }

    @Override
    public boolean add(Connection connection, Item item) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "INSERT INTO Item VALUES (?,?,?,?)", item.getCode(), item.getName(), item.getUnitPrice(), item.getQty());
    }

    @Override
    public boolean update(Connection connection, Item item) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "UPDATE Item SET name=?,unitPrice=?,qtyOnHand=? WHERE code=?", item.getName(), item.getUnitPrice(), item.getQty(), item.getCode());
    }

    @Override
    public boolean delete(Connection connection, String code) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection,"DELETE FROM Item WHERE code=?", code);
    }

    @Override
    public Item search(Connection connection, String code) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery(connection, "SELECT * FROM Item WHERE code=?", code);
        Item item = null;
        while (rst.next()) {
            item = new Item(rst.getString(1), rst.getString(2), rst.getDouble(3), rst.getInt(4));
        }
        return item;
    }

    @Override
    public JsonArray generateCode(Connection connection) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery(connection, "SELECT code FROM Item");
        JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
        while (resultSet.next()){
            String id = resultSet.getString(1);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("code", id);
            arrayBuilder2.add(objectBuilder.build());
        }
        return arrayBuilder2.build();
    }

    @Override
    public boolean updateQty(Connection connection, int qty, String code) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate(connection, "UPDATE Item SET qtyOnHand='" + qty + "' WHERE code='" + code + "'");
    }
}