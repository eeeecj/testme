package com.weirddev.testme.intellij.sql;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.weirddev.testme.intellij.TestMePluginRegistration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> executeSqlWithResult(String sql) {
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
        } catch (Exception e) {
            LOG.warn("can't check for keyboard conflicts", e);
        }
        return result;
    }
}
