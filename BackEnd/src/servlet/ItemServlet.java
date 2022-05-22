package servlet;

import bo.BOFactory;
import bo.custom.ItemBO;
import dto.ItemDTO;

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

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    DataSource ds;

    ItemBO itemBO = (ItemBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ITEM);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String option = req.getParameter("option");
            resp.setContentType("application/json");
            Connection connection = ds.getConnection();
            PrintWriter writer = resp.getWriter();


            switch (option) {
                case "SEARCH":
                    String itemCode = req.getParameter("code");
                    ItemDTO item = itemBO.searchItem(connection, itemCode);
//                    PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
//                    pstm.setObject(1, itemCode);
//                    ResultSet searchSet = pstm.executeQuery();

                    JsonObjectBuilder searchItem = Json.createObjectBuilder();

                    if (item !=null) {
//                        String code = searchSet.getString(1);
//                        String name = searchSet.getString(2);
//                        double unitPrice = searchSet.getDouble(3);
//                        int qtyOnHand = searchSet.getInt(4);
//
//                        System.out.println(code);
//                        System.out.println(name);
//                        System.out.println(unitPrice);
//                        System.out.println(qtyOnHand);

                        searchItem.add("status", 200);
                        searchItem.add("code", item.getCode());
                        searchItem.add("name", item.getName());
                        searchItem.add("unitPrice", item.getUnitPrice());
                        searchItem.add("qtyOnHand", item.getQty());

                    }else{
                        searchItem.add("status", 400);
                    }

                    writer.print(searchItem.build());

                    break;
                case "ItemId":
//                    ResultSet custId = connection.prepareStatement("select code from item").executeQuery();
//                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
//                    while (custId.next()){
//                        String code = custId.getString(1);
//                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
//                        objectBuilder.add("code", code);
//                        arrayBuilder2.add(objectBuilder.build());
//                    }
                    JsonObjectBuilder responseObject = Json.createObjectBuilder();
                    responseObject.add("status", 200);
                    responseObject.add("message", "Done");
                    responseObject.add("data", itemBO.generateItemCode(connection));
                    writer.print(responseObject.build());
                    break;
                case "GETALL":
//                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(); // json array
//                    ResultSet rst = connection.prepareStatement("select * from item").executeQuery();
//                    while (rst.next()) {
//                        String code = rst.getString(1);
//                        String name = rst.getString(2);
//                        double unitPrice = rst.getDouble(3);
//                        int qtyOnHand = rst.getInt(4);
//
//                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
//                        objectBuilder.add("code", code);
//                        objectBuilder.add("name", name);
//                        objectBuilder.add("unitPrice", unitPrice);
//                        objectBuilder.add("qtyOnHand", qtyOnHand);
//                        arrayBuilder.add(objectBuilder.build());
//                    }

                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("status", 200);
                    response.add("message", "Done");
                    response.add("data", itemBO.getAllItems(connection));
                    writer.print(response.build());
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
        String itemCode = req.getParameter("itemCode");
        String itemName = req.getParameter("itemName");
        double  unitPrice = Double.parseDouble(req.getParameter("unitPrice"));
        int QtyOnHand =Integer.parseInt(req.getParameter("QtyOnHand"));

        ItemDTO item = new ItemDTO(itemCode, itemName, unitPrice, QtyOnHand);


        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        try {
            Connection connection = ds.getConnection();
//            PreparedStatement pstm = connection.prepareStatement("Insert into item values(?,?,?,?)");
//            pstm.setObject(1, itemCode);
//            pstm.setObject(2, itemName);
//            pstm.setObject(3, unitPrice);
//            pstm.setObject(4, QtyOnHand);

            boolean add = itemBO.addItem(connection, item);


            if (add) {
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String option = req.getParameter("option");
            resp.setContentType("application/json");
            Connection connection = ds.getConnection();
            switch (option) {
                case "UPDATEALL":
                    JsonReader reader = Json.createReader(req.getReader());
                    JsonObject jsonObject = reader.readObject();
                    String code = jsonObject.getString("code");
                    String name = jsonObject.getString("name");
                    double UnitPrice = Double.parseDouble(jsonObject.getString("unitPrice"));
                    int qtyOnHand = Integer.parseInt(jsonObject.getString("qtyOnHand"));
                    ItemDTO item = new ItemDTO(code, name, UnitPrice, qtyOnHand);

                    boolean update = itemBO.updateItem(connection, item);

//                    PreparedStatement pstm = connection.prepareStatement("Update item set name=?,unitPrice=?,qtyOnHand=? where code=?");
//                    pstm.setObject(1, name);
//                    pstm.setObject(2, UnitPrice);
//                    pstm.setObject(3, qtyOnHand);
//                    pstm.setObject(4, code);
                    if (update) {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("status", 200);
                        objectBuilder.add("message", "Successfully Updated");
                        objectBuilder.add("data", "");
                        writer.print(objectBuilder.build());
                    } else {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("status", 400);
                        objectBuilder.add("message", "Update Failed");
                        objectBuilder.add("data", "");
                        writer.print(objectBuilder.build());
                    }

                    break;
                case "UPDATEQTY":
                    JsonReader itemQtyReader = Json.createReader(req.getReader());
                    JsonObject jsonItemObject = itemQtyReader.readObject();
                    String updateItemId = jsonItemObject.getString("itemCode");
                    int updateQty = jsonItemObject.getInt("QTYLeft");

                    boolean updateQuantity = itemBO.updateQty(connection,updateQty,updateItemId);

//                    PreparedStatement pst = connection.prepareStatement("update item set qtyOnHand=? where code=?");
//                    pst.setObject(1,updateQty);
//                    pst.setObject(2,updateItemId);
                    if (updateQuantity) {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("status", 200);
                        objectBuilder.add("message", "Successfully Updated Qty");
                        objectBuilder.add("data", "");
                        writer.print(objectBuilder.build());
                    }else {
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        resp.setStatus(HttpServletResponse.SC_OK);
                        objectBuilder.add("status", 400);
                        objectBuilder.add("message", "Qty Update Failed");
                        objectBuilder.add("data", "");
                        writer.print(objectBuilder.build());
                    }
                    break;
            }
            connection.close();
        } catch (SQLException throwables) {
            resp.setStatus(200);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", 500);
            objectBuilder.add("message", "Update Failed");
            objectBuilder.add("data", throwables.getLocalizedMessage());
            writer.print(objectBuilder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");


        try {
            Connection connection = ds.getConnection();
            boolean delete = itemBO.deleteItem(connection, code);
//            PreparedStatement pstm = connection.prepareStatement("Delete from item where code=?");
//            pstm.setObject(1, code);

            if (delete) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", 200);
                objectBuilder.add("data", "");
                objectBuilder.add("message", "Successfully Deleted");
                writer.print(objectBuilder.build());
            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", 400);
                objectBuilder.add("data", "Wrong Id Inserted");
                objectBuilder.add("message", "");
                writer.print(objectBuilder.build());
            }
            connection.close();

        } catch (SQLException throwables) {
            resp.setStatus(200);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", 500);
            objectBuilder.add("message", "Error");
            objectBuilder.add("data", throwables.getLocalizedMessage());
            writer.print(objectBuilder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
