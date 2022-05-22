package bo.custom;

import bo.SuperBO;
import dto.OrderDTO;
import dto.OrderDetailDTO;
import entity.OrderDetail;

import java.sql.Connection;
import java.sql.SQLException;

public interface OrderDetailsBO extends SuperBO {
    boolean placeOrder(Connection connection, OrderDetailDTO orderDetailDTO) throws SQLException, ClassNotFoundException;
}
