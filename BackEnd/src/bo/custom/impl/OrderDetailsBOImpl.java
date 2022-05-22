package bo.custom.impl;

import bo.custom.OrderDetailsBO;
import dao.DAOFactory;
import dao.custom.OrderDAO;
import dao.custom.OrderDetailDAO;
import dto.OrderDTO;
import dto.OrderDetailDTO;
import entity.OrderDetail;

import java.sql.Connection;
import java.sql.SQLException;

public class OrderDetailsBOImpl implements OrderDetailsBO {

    OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDERDETAIL);

    @Override
    public boolean placeOrder(Connection connection, OrderDetailDTO orderDetailDTO) throws SQLException, ClassNotFoundException {
        OrderDetail orderDetail=new OrderDetail(orderDetailDTO.getOrderId(),orderDetailDTO.getItemCode(),orderDetailDTO.getItemName(),orderDetailDTO.getUnitPrice(),
                orderDetailDTO.getBuyQty(),orderDetailDTO.getTotal()
        );
        return orderDetailDAO.add(connection,orderDetail);
    }
}
