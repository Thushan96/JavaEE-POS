package dao.custom;

import dao.CrudDAO;
import entity.OrderDetail;

import javax.json.JsonArray;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


public interface OrderDetailDAO extends CrudDAO<OrderDetail, String> {
    boolean add(Connection connection, OrderDetail orderDetail) throws SQLException, ClassNotFoundException;

}