package servlet;

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

@WebServlet(urlPatterns = "/order")
public class OrderServlet extends HttpServlet {
    @Resource(name = "java:comp/env/jdbc/pool")
    DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String option = req.getParameter("option");
            resp.setContentType("application/json");
            Connection connection = ds.getConnection();
            PrintWriter writer = resp.getWriter();


            switch (option) {
                case "GENERATEORDERID":
                    ResultSet orderIdSet = connection.prepareStatement("SELECT orderId FROM `Order` ORDER BY orderId DESC LIMIT 1").executeQuery();
                    JsonObjectBuilder obj = Json.createObjectBuilder();
                    if (orderIdSet.next()) {
                        int tempId = Integer.parseInt(orderIdSet.getString(1).split("-")[1]);
                        tempId = tempId + 1;
                        if (tempId <= 9) {
                            String id = "O-000" + tempId;
                            obj.add("orderId", id);
                        } else if (tempId <= 99) {
                            String id = "O-00" + tempId;
                            obj.add("orderId", id);
                        } else if (tempId <= 999) {
                            String id = "O-0" + tempId;
                            obj.add("orderId", id);
                        } else if (tempId <= 9999) {
                            String id = "O-" + tempId;
                            obj.add("orderId", id);
                        }
                    } else {
                        String id = "O-0001";
                        obj.add("orderId", id);
                    }

                    writer.print(obj.build());

                    break;
                case "CustId":
                    ResultSet custId = connection.prepareStatement("select id from Customer").executeQuery();
                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
                    while (custId.next()){
                        String id = custId.getString(1);
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("id", id);
                        arrayBuilder2.add(objectBuilder.build());
                    }

                    JsonObjectBuilder responseObject = Json.createObjectBuilder();
                    responseObject.add("status", 200);
                    responseObject.add("message", "Done");
                    responseObject.add("data", arrayBuilder2.build());
                    writer.print(responseObject.build());
                    break;
                case "CustName":
                    String Id = req.getParameter("cusId");
                    PreparedStatement preparedStatement = connection.prepareStatement("select name from customer WHERE id=?");
                    preparedStatement.setObject(1, Id);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    JsonObjectBuilder searchCustomerName = Json.createObjectBuilder();
                    while (resultSet.next()){
                        String name = resultSet.getString(1);
                        searchCustomerName.add("status", 200);
                        searchCustomerName.add("name", name);
                    }

                    writer.print(searchCustomerName.build());
                    break;

                case "GETALL":
                    ResultSet rst = connection.prepareStatement("select * from Customer").executeQuery();
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(); // json array
                    while (rst.next()) {
                        String id = rst.getString(1);
                        String name = rst.getString(2);
                        String address = rst.getString(3);
                        double salary = rst.getDouble(4);

                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("id", id);
                        objectBuilder.add("name", name);
                        objectBuilder.add("address", address);
                        objectBuilder.add("salary", salary);
                        arrayBuilder.add(objectBuilder.build());
                    }

                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("status", 200);
                    response.add("message", "Done");
                    response.add("data", arrayBuilder.build());
                    writer.print(response.build());
                    break;
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String orderId = jsonObject.getString("orderId");
        String orderDate = jsonObject.getString("orderDate");
        String customerId = jsonObject.getString("customerId");
        double orderTotal = Double.parseDouble(jsonObject.getString("orderTotal"));
        JsonArray orderDetails = jsonObject.getJsonArray("orderDetails");

        JsonObjectBuilder builder = Json.createObjectBuilder();

        resp.setContentType("application/json");

        PrintWriter writer = resp.getWriter();

//        try {
//            Connection connection = ds.getConnection();
//            connection.setAutoCommit(false);
//
//            Order order = new Order(orderId, orderDate, customerId, orderTotal);
//            boolean add = orderDAO.add(order);
//            if (!add) {
//                connection.rollback();
//                connection.setAutoCommit(true);
//                builder.add("boolean", false);
//                writer.print(builder.build());
//            }
//
//            for (JsonValue object : orderDetails) {
//                OrderDetail orderDetail = new OrderDetail(orderId, object.asJsonObject().getString("itemCode"), object.asJsonObject().getString("itemName"), Double.parseDouble(object.asJsonObject().getString("unitPrice")), object.asJsonObject().getInt("buyQty"), object.asJsonObject().getInt("total"));
//                boolean orderDetailsAdd = orderDetailDAO.add(orderDetail);
//                if (!orderDetailsAdd) {
//                    connection.rollback();
//                    connection.setAutoCommit(true);
//                    builder.add("boolean", false);
//                    writer.print(builder.build());
//                }
//            }
//
//            connection.commit();
//            connection.setAutoCommit(true);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        builder.add("boolean", true);
        writer.print(builder.build());
    }
}
