package dao.custom;

import dao.CrudDAO;
import entity.Item;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.sql.Connection;
import java.sql.SQLException;


public interface ItemDAO extends CrudDAO<Item, String> {
    JsonArray generateCode(Connection connection) throws SQLException, ClassNotFoundException;

    boolean updateQty(Connection connection,int qty, String code) throws SQLException, ClassNotFoundException;
}