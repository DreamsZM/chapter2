package com.zy.character2.helper;

import com.zy.character2.util.CollectionUtil;
import com.zy.character2.util.PropsUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库工具类
 * 为了确保一个线程中只有一个Connection，使用ThreadLocal存放本地线程变量，ThreadLocal可以理解为一个隔离线程的容器
 */
public class DatabaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DRIVER;

    private static final String URL;

    private static final String USERNAME;

    private static final String PASSWORD;

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    static {
        Properties properties = PropsUtil.loadProps("config.properties");
        DRIVER = properties.getProperty("jdbc.driver");
        URL = properties.getProperty("jdbc.url");
        USERNAME = properties.getProperty("jdbc.username");
        PASSWORD = properties.getProperty("jdbc.password");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("Driver load failure");
        }
    }

    public static <T>List<T> queryEntityList(Connection connection, Class<T> entityClass, String sql, Object... params){
        List<T> entityList = null;
        try {
            entityList = QUERY_RUNNER.query(connection, sql, new BeanListHandler<>(entityClass), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entityList;
    }

    public static <T>List<T> queryEntityList(Connection connection, Class<T> entityClass, String sql){
        List<T> entityList = null;
        try {
            entityList = QUERY_RUNNER.query(connection, sql, new BeanListHandler<>(entityClass));
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("query failure");
        }

        return entityList;
    }

    /**
     *
     * @param entityClass
     * @param sql
     * @param <T>
     * @return
     */
    public static <T>List<T> queryEntityList(Class<T> entityClass, String sql){
        List<T> entityList = null;
        try {
            entityList = QUERY_RUNNER.query(getConnection(), sql, new BeanListHandler<>(entityClass));
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("query failure");
        } finally {
            closeConnection();
        }
        return entityList;
    }

    public static <T>T queryEntity(Class<T> clazz, String sql, Object... params){
        T entity = null;
        try {
            entity = QUERY_RUNNER.query(getConnection(), sql, new BeanHandler<>(clazz), params);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return entity;
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params){
        List<Map<String, Object>> result = null;
        try {
            result = QUERY_RUNNER.query(getConnection(), sql, new MapListHandler(), params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return result;
    }

    public static int executeUpdate(String sql, Object... params){
        int rows = 0;
        try {
            rows = QUERY_RUNNER.update(getConnection(), sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static <T> boolean insertEntity(Class<T> clazz, Map<String, Object> fieldMap){
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("Can not insert entity: fieldMap is empty");
            return false;
        }

        String sql = "insert into " + getTableName(clazz);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append(", ");
            values.append("?").append(", ");
        }

        columns.replace(columns.lastIndexOf(","), columns.length(), ")");
        values.replace(values.lastIndexOf(","), columns.length(), ")");

        sql += columns + " values " + values;
        Object[] params = fieldMap.values().toArray();

        return executeUpdate(sql, params) == 1;
    }

    public static <T> boolean updateEntity(Class<T> clazz, long id, Map<String, Object> fieldMap){
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("Can not update entity: fieldMap is empty");
            return false;
        }

        String sql = "update " + getTableName(clazz) + " set ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append("=?,");
        }
        sql+=columns.substring(0, columns.lastIndexOf(",")) + " where id=?";

        List<Object> paramList = new ArrayList<>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return executeUpdate(sql, params) == 1;
    }

    public static <T> boolean deleteEntity(Class<T> clazz, long id){
        String sql = "delete from " + getTableName(clazz) + " where id = ?";
        return executeUpdate(sql, id) == 1;
    }

    /**
     * 数据库表名区分大小写，数据库表明定义为小写字母表示
     * @param clazz
     * @return
     */
    private static String getTableName(Class<?> clazz){
        return clazz.getSimpleName().toLowerCase();
    }

    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection(){
        Connection connection = CONNECTION_HOLDER.get();
        if (connection == null){
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("getConnection failure");
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }
        return connection;
    }

    public static void closeConnection(){
        Connection connection = CONNECTION_HOLDER.get();
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    public static void closeConnection(Connection connection){
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("connction close failure");
            }
        }
    }
}
