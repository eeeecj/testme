package com.weirddev.testme.intellij.sql;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.weirddev.testme.intellij.TestMePluginRegistration;
import org.apache.commons.collections.CollectionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SqlExecutor {
    private static final Logger LOG = Logger.getInstance(TestMePluginRegistration.class.getName());
    public static final String DATASOURCE_CONNECTED = "Server Connected.";

    public static String testConnected() {
        DatasourceComponent datasourceComponent = ApplicationManager.getApplication().getService(DatasourceComponent.class);

        try (Connection ignored = datasourceComponent.getConnection()) {
            return DATASOURCE_CONNECTED;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return String.format("Server can't Connect!\n%s", sw);
        }
    }

    public static List<Map<String, Object>> executeSqlWithResult(String sql) throws Exception {
        DatasourceComponent datasourceComponent = ApplicationManager.getApplication().getService(DatasourceComponent.class);
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection connection = datasourceComponent.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(rsmd.getColumnName(i), rs.getObject(i));
                }
                result.add(map);
            }
        }
        return result;
    }

    public static String getCreateTableSql(String tableName) throws Exception {
        String sql = "SHOW CREATE TABLE " + tableName;
        List<Map<String, Object>> result = SqlExecutor.executeSqlWithResult(sql);
        if (CollectionUtils.isEmpty(result)) {
            return "";
        }
        Map<String, Object> map = result.get(0);
        return (String) map.get("Create Table");
    }

    public static List<String> getColumns(String tableName) throws Exception {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = \"" + tableName
                + "\" ORDER BY ORDINAL_POSITION";
        List<Map<String, Object>> result = SqlExecutor.executeSqlWithResult(sql);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.stream().map(p -> (String) p.get("COLUMN_NAME")).collect(Collectors.toList());
    }

    public static List<Map<String, Object>> getData(String tableName, String size) throws Exception {
        String sql = "SELECT * FROM " + tableName + " limit " + size;
        return SqlExecutor.executeSqlWithResult(sql);
    }
}
