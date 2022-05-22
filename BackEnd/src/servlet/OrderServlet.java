package servlet;



import bo.BOFactory;
import bo.custom.OrderBO;
import bo.custom.OrderDetailsBO;
import dto.OrderDTO;
import dto.OrderDetailDTO;
import entity.Order;
import entity.OrderDetail;


import javax.annotation.Resource;
import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(urlPatterns = "/order")
public class OrderServlet extends HttpServlet {
    @Resource(name = "java:comp/env/jdbc/pool")
    DataSource ds;

    OrderBO orderBO = (OrderBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ORDER);
    OrderDetailsBO orderDetailsBO = (OrderDetailsBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ORDERDETAIL);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String option = req.getParameter("option");
            resp.setContentType("application/json");
            Connection connection = ds.getConnection();
            PrintWriter writer = resp.getWriter();

            OrderBO orderBO = (OrderBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ORDER);

            switch (option) {
                case "GENERATEORDERID":
//                    ResultSet orderIdSet = connection.prepareStatement("SELECT orderId FROM `Order` ORDER BY orderId DESC LIMIT 1").executeQuery();
//                    JsonObjectBuilder obj = Json.createObjectBuilder();
//                    if (orderIdSet.next()) {
//                        int tempId = Integer.parseInt(orderIdSet.getString(1).split("-")[1]);
//                        tempId = tempId + 1;
//                        if (tempId <= 9) {
//                            String id = "O-000" + tempId;
//                            obj.add("orderId", id);
//                        } else if (tempId <= 99) {
//                            String id = "O-00" + tempId;
//                            obj.add("orderId", id);
//                        } else if (tempId <= 999) {
//                            String id = "O-0" + tempId;
//                            obj.add("orderId", id);
//                        } else if (tempId <= 9999) {
//                            String id = "O-" + tempId;
//                            obj.add("orderId", id);
//                        }
//                    } else {
//                        String id = "O-0001";
//                        obj.add("orderId", id);
//                    }
//
//                    writer.print(obj.build());
                    writer.print(orderBO.generateOrderId(connection));
                    break;
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        JsonObjectBuilder builder = Json.createObjectBuilder();

        try {
            Connection connection = ds.getConnection();
            JsonReader reader = Json.createReader(req.getReader());
            JsonObject jsonObject = reader.readObject();
            String orderId = jsonObject.getString("orderId");
            String orderDate = jsonObject.getString("orderDate");
            String customerId = jsonObject.getString("customerId");
            double orderTotal = Double.parseDouble(jsonObject.getString("orderTotal"));
            JsonArray orderDetails = jsonObject.getJsonArray("orderDetails");
            ArrayList<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();


            OrderDTO orderDTO = new OrderDTO(orderId, orderDate, customerId, orderTotal, orderDetailDTOS);

            for (JsonValue object : orderDetails) {
                OrderDetailDTO orderDetail = new OrderDetailDTO(orderId, object.asJsonObject().getString("itemCode"), object.asJsonObject().getString("itemName"), Double.parseDouble(object.asJsonObject().getString("unitPrice")), object.asJsonObject().getInt("buyQty"), object.asJsonObject().getInt("total"));
                orderDetailDTOS.add(orderDetail);
            }


            connection.setAutoCommit(false);

            Order order = new Order(orderId, orderDate, customerId, orderTotal);
//            boolean add = OrderDAO.add(order);

            boolean added = orderBO.placeOrder(connection, orderDTO);


//
//            PreparedStatement pstm = connection.prepareStatement("INSERT INTO `Order` VALUES (?,?,?,?)");
//            pstm.setObject(1,orderId);
//            pstm.setObject(2,orderDate);
//            pstm.setObject(3,customerId);
//            pstm.setObject(4,orderTotal);



            int i=0;
            boolean updated=true;
            if (added) {
                System.out.println("order table updated ");

                for (JsonValue object : orderDetails) {
                    OrderDetailDTO orderDetail = new OrderDetailDTO(orderId, object.asJsonObject().getString("itemCode"), object.asJsonObject().getString("itemName"), Double.parseDouble(object.asJsonObject().getString("unitPrice")), object.asJsonObject().getInt("buyQty"), object.asJsonObject().getInt("total"));
//                    PreparedStatement pstm2 = connection.prepareStatement("INSERT INTO `orderdetails` VALUES (?,?,?,?,?,?)");
//                    pstm2.setObject(1, orderDetail.getOrderId());
//                    pstm2.setObject(2, orderDetail.getItemCode());
//                    pstm2.setObject(3, orderDetail.getItemName());
//                    pstm2.setObject(4, orderDetail.getUnitPrice());
//                    pstm2.setObject(5, orderDetail.getBuyQty());
//                    pstm2.setObject(6, orderDetail.getTotal());
////                boolean orderDetailsAdd = OrderDetailDAO.add(orderDetail);
////                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `Order Detail` VALUES (?,?,?,?,?,?)", ,  (), (),, orderDetail.getTotal()");
//                    i = pstm2.executeUpdate();
                    if (updated){
                        updated = orderDetailsBO.placeOrder(connection, orderDetail);
                    }

                }

                if (updated) {
                        System.out.println("order detail table updated");
                        connection.commit();
                        builder.add("status", 200);
                        builder.add("message", "Order Placed.Thank You");
                        writer.print(builder.build());
                    }else{
                        connection.rollback();
                        builder.add("status", 400);
                        builder.add("message", "operation failed please try again");
                        writer.print(builder.build());
                    }
            }else{
                connection.rollback();
                builder.add("status", 500);
                builder.add("message", "operation failed please try again");
                writer.print(builder.build());
            }

            connection.setAutoCommit(true);
            connection.close();

//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            builder.add("status", 500);
            builder.add("message", "operation failed");
            writer.print(builder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
