package dao.custom;

import dao.CrudDAO;
import entity.OrderDetail;

import javax.json.JsonArray;
import java.sql.SQLException;


public interface OrderDetailDAO extends CrudDAO<OrderDetail, String> {
    JsonArray searchOrderDetails(String id) throws SQLException, ClassNotFoundException;
}