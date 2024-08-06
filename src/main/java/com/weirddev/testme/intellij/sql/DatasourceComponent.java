package com.weirddev.testme.intellij.sql;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.weirddev.testme.intellij.configuration.DatasourceConfigComponent;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatasourceComponent {
    public static final String DATABASE_URL_TEMPLATE = "jdbc:mysql://%s:%s/%s";

    private DruidDataSource dataSource;

    public Connection getConnection() throws Exception {
        if (dataSource == null || dataSource.isClosed()) {
            dataSource = createDatasource();
        }

        return dataSource.getConnection(3000);
    }

    public List<String> getAllTableName() throws Exception {
        Connection conn = this.getConnection();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            List<String> ls = new ArrayList<>();
            while (rs.next()) {
                String s = rs.getString("TABLE_NAME");
                ls.add(s);
            }
            return ls;
        } finally {
            closeConnection(conn);
        }
    }

    public void updateDatasource() {
//        this.close();
        try {
            dataSource = createDatasource();
        } catch (Exception ignored) {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void close() {
        try {
            if (dataSource != null) {
                dataSource.close();
            }
        } catch (Exception ignored) {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }

    private DruidDataSource createDatasource() throws Exception {

        DatasourceConfigComponent component = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);

        Properties properties = new Properties();

        String url = String.format(DATABASE_URL_TEMPLATE, component.getHost(), component.getPort(), component.getDatabase());
        properties.put(DruidDataSourceFactory.PROP_URL, url);
        properties.put(DruidDataSourceFactory.PROP_USERNAME, component.getUser());
        properties.put(DruidDataSourceFactory.PROP_PASSWORD, component.getPassword());
        properties.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, "com.mysql.cj.jdbc.Driver");
        properties.put(DruidDataSourceFactory.PROP_MINIDLE, "5");
        properties.put(DruidDataSourceFactory.PROP_MAXACTIVE, "10");
        properties.put(DruidDataSourceFactory.PROP_MAXWAIT, "5000");

        return (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

    }
}
