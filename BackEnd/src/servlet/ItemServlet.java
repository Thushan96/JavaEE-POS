package servlet;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
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

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {

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
                case "SEARCH":
                    System.out.println("Search");
                    String cusId = req.getParameter("cusId");
                    PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
                    pstm.setObject(1, cusId);
                    ResultSet searchSet = pstm.executeQuery();

                    JsonObjectBuilder searchCustomer = Json.createObjectBuilder();

                    while (searchSet.next()) {
                        String code = searchSet.getString(1);
                        String name = searchSet.getString(2);
                        String unitPrice = searchSet.getString(3);
                        String qtyOnHand = searchSet.getString(4);

                        System.out.println(code);
                        System.out.println(name);
                        System.out.println(unitPrice);
                        System.out.println(qtyOnHand);

                        searchCustomer.add("status", 200);
                        searchCustomer.add("code", code);
                        searchCustomer.add("name", name);
                        searchCustomer.add("unitPrice", unitPrice);
                        searchCustomer.add("qtyOnHand", qtyOnHand);

                    }

                    writer.print(searchCustomer.build());

                    break;
                case "CustId":
                    ResultSet custId = connection.prepareStatement("select code from item").executeQuery();
                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
                    while (custId.next()){
                        String code = custId.getString(1);
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("code", code);
                        arrayBuilder2.add(objectBuilder.build());
                    }

                    JsonObjectBuilder responseObject = Json.createObjectBuilder();
                    responseObject.add("status", 200);
                    responseObject.add("message", "Done");
                    responseObject.add("data", arrayBuilder2.build());
                    writer.print(responseObject.build());
                    break;
                case "GETALL":
                    ResultSet rst = connection.prepareStatement("select * from item").executeQuery();
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(); // json array
                    while (rst.next()) {
                        String code = rst.getString(1);
                        String name = rst.getString(2);
                        String unitPrice = rst.getString(3);
                        double qtyOnHand = rst.getDouble(4);

                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("code", code);
                        objectBuilder.add("name", name);
                        objectBuilder.add("unitPrice", unitPrice);
                        objectBuilder.add("qtyOnHand", qtyOnHand);
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
        String itemCode = req.getParameter("itemCode");
        String itemName = req.getParameter("itemName");
        String unitPrice = req.getParameter("unitPrice");
        String QtyOnHand = req.getParameter("QtyOnHand");



        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        try {
            Connection connection = ds.getConnection();
            PreparedStatement pstm = connection.prepareStatement("Insert into item values(?,?,?,?)");
            pstm.setObject(1, itemCode);
            pstm.setObject(2, itemName);
            pstm.setObject(3, unitPrice);
            pstm.setObject(4, QtyOnHand);

            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder response = Json.createObjectBuilder();
                resp.setStatus(HttpServletResponse.SC_CREATED);//201
                response.add("status", 200);
                response.add("message", "Successfully Added");
                response.add("data", "");
                writer.print(response.build());
            }
            connection.close();
        } catch (SQLException throwables) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("status", 400);
            response.add("message", "Error");
            response.add("data", throwables.getLocalizedMessage());
            writer.print(response.build());
            resp.setStatus(HttpServletResponse.SC_OK); //200
            throwables.printStackTrace();
        }
    }
}
