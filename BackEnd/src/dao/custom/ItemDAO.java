package dao.custom;

import dao.CrudDAO;
import entity.Item;

import javax.json.JsonObject;
import java.sql.SQLException;


public interface ItemDAO extends CrudDAO<Item, String> {
    JsonObject generateCode() throws SQLException, ClassNotFoundException;

    boolean updateQty(int qty, String code) throws SQLException, ClassNotFoundException;
}