package com.weirddev.testme.intellij.sql;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;

public class SqlExecutor {
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
}
