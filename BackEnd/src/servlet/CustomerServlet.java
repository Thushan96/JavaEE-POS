package servlet;

import bo.BOFactory;
import bo.custom.CustomerBO;
import dto.CustomerDTO;

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

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    DataSource ds;

    CustomerBO customerBO = (CustomerBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CUSTOMER);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String option = req.getParameter("option");
            resp.setContentType("application/json");
            Connection connection = ds.getConnection();
            PrintWriter writer = resp.getWriter();


            switch (option) {
                case "SEARCH":
                    String cusId = req.getParameter("cusId");
                    CustomerDTO customer = customerBO.searchCustomer(connection, cusId);
                    JsonObjectBuilder searchCustomer = Json.createObjectBuilder();
                    if (customer != null) {
                        searchCustomer.add("status", 200);
                        searchCustomer.add("id", customer.getId());
                        searchCustomer.add("name", customer.getName());
                        searchCustomer.add("address", customer.getAddress());
                        searchCustomer.add("salary", customer.getSalary());
                    } else {
                        searchCustomer.add("status", 400);
                    }
                    writer.print(searchCustomer.build());
//                    String cusId = req.getParameter("cusId");
//                    PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer WHERE id=?");
//                    pstm.setObject(1, cusId);
//                    ResultSet searchSet = pstm.executeQuery();
//
//                    JsonObjectBuilder searchCustomer = Json.createObjectBuilder();
//
//                    while (searchSet.next()) {
//                        String id = searchSet.getString(1);
//                        String name = searchSet.getString(2);
//                        String address = searchSet.getString(3);
//                        String contactNo = searchSet.getString(4);
//
//                        System.out.println(id);
//                        System.out.println(name);
//                        System.out.println(address);
//                        System.out.println(contactNo);
//
//                        searchCustomer.add("status", 200);
//                        searchCustomer.add("id", id);
//                        searchCustomer.add("name", name);
//                        searchCustomer.add("address", address);
//                        searchCustomer.add("contactNo", contactNo);
//
//                    }

                    break;
                case "CustId":
//                    ResultSet custId = connection.prepareStatement("select id from Customer").executeQuery();
//                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
//                    while (custId.next()){
//                        String id = custId.getString(1);
//                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
//                        objectBuilder.add("id", id);
//                        arrayBuilder2.add(objectBuilder.build());
//                    }
                    JsonArray jsonValues = customerBO.generateCustomerId(connection);

                    JsonObjectBuilder responseObject = Json.createObjectBuilder();
                    responseObject.add("status", 200);
                    responseObject.add("message", "Done");
                    responseObject.add("data", jsonValues);
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
//                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
//                    ResultSet rst = connection.prepareStatement("select * from Customer").executeQuery();
//                    while (rst.next()) {
//                        String id = rst.getString(1);
//                        String name = rst.getString(2);
//                        String address = rst.getString(3);
//                        double salary = rst.getDouble(4);
//
//                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
//                        objectBuilder.add("id", id);
//                        objectBuilder.add("name", name);
//                        objectBuilder.add("address", address);
//                        objectBuilder.add("salary", salary);
//                        arrayBuilder.add(objectBuilder.build());
//                    }

                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("status", 200);
                    response.add("message", "Done");
                    response.add("data", customerBO.getAllCustomers(connection));
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

        String customerID = req.getParameter("customerID");
        String customerName = req.getParameter("customerName");
        String customerAddress = req.getParameter("customerAddress");
        double customerContactNo = Double.parseDouble(req.getParameter("customerContactNo"));

        CustomerDTO customer = new CustomerDTO(customerID, customerName, customerAddress, customerContactNo);


        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        try {
            Connection connection = ds.getConnection();
            boolean add = customerBO.addCustomer(connection, customer);

//            PreparedStatement pstm = connection.prepareStatement("Insert into Customer values(?,?,?,?)");
//            pstm.setObject(1, customerID);
//            pstm.setObject(2, customerName);
//            pstm.setObject(3, customerAddress);
//            pstm.setObject(4, customerContactNo);

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
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String customerID = jsonObject.getString("id");
        String customerName = jsonObject.getString("name");
        String customerAddress = jsonObject.getString("address");
        double customerContactNo = Double.parseDouble(jsonObject.getString("contactNo"));
        PrintWriter writer = resp.getWriter();


        resp.setContentType("application/json");
        CustomerDTO customer = new CustomerDTO(customerID, customerName, customerAddress, customerContactNo);


        try {
            Connection connection = ds.getConnection();
            boolean update = customerBO.updateCustomer(connection, customer);
//            PreparedStatement pstm = connection.prepareStatement("Update Customer set name=?,address=?,contact=? where id=?");
//            pstm.setObject(1, customerName);
//            pstm.setObject(2, customerAddress);
//            pstm.setObject(3, customerContactNo);
//            pstm.setObject(4, customerID);
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
            connection.close();
        } catch (SQLException | ClassNotFoundException throwables) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", 500);
            objectBuilder.add("message", "Update Failed");
            objectBuilder.add("data", throwables.getLocalizedMessage());
            writer.print(objectBuilder.build());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String customerID = req.getParameter("cusId");
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");


        try {
            Connection connection = ds.getConnection();
            boolean delete = customerBO.deleteCustomer(connection, customerID);
//            PreparedStatement pstm = connection.prepareStatement("Delete from Customer where id=?");
//            pstm.setObject(1, customerID);

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
