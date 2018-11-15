package com.zy.character2.service;

import com.zy.character2.helper.DatabaseHelper;
import com.zy.character2.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    public boolean createCustomer(Map<String, Object> fieldMap){

        return DatabaseHelper.insertEntity(Customer.class, fieldMap);
    }

    public boolean deleteCustomer(long id){

        return DatabaseHelper.deleteEntity(Customer.class, id);
    }

    public boolean updateCustomer(long id, Map<String, Object> fieldMap){

        return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
    }

    public Customer getCustomer(long id){
        String sql = "select * from customer where id = ?";
        return DatabaseHelper.queryEntity(Customer.class, sql ,id);
    }

    public List<Customer> getCustomerList(){
        Connection connection = null;
        try {
            connection = DatabaseHelper.getConnection();
            String sql = "select * from customer";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Customer> customerList = new ArrayList<Customer>();
            while (resultSet.next()){
                Customer customer = new Customer();
                customer.setId(resultSet.getLong("id"));
                customer.setContact(resultSet.getString("contact"));
                customer.setTelephone(resultSet.getString("telephone"));
                customer.setEmail(resultSet.getString("email"));
                customer.setRemark(resultSet.getString("remark"));
                customerList.add(customer);
            }
            return customerList;
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("Execute SQL failure, more info : {}", e);
        }finally {
            DatabaseHelper.closeConnection(connection);
        }

        return null;
    }

    public List<Customer> getCustomerList1(){
        Connection connection = DatabaseHelper.getConnection();
        String sql = "select * from customer";
        List<Customer> customerList;
        try {
            customerList = DatabaseHelper.queryEntityList(connection, Customer.class, sql);
            return customerList;
        } finally {
            DatabaseHelper.closeConnection(connection);
        }

    }

    public List<Customer> getCustomerList2(){
        String sql = "select * from customer";
        return DatabaseHelper.queryEntityList(Customer.class, sql);
    }
}
