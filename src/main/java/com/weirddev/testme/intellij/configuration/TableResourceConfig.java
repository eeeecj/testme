package com.weirddev.testme.intellij.configuration;

import java.io.File;
import java.util.List;

public class TableResourceConfig {
    private List<String> tables;
    private String classPath;
    private String fetchSize;
    private String sep;

    public String getSep() {
        return sep;
    }

    public void setSep(String sep) {
        this.sep = sep;
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getResourcePath() {
        return String.join(File.separator, classPath, "test", "resources");
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getTablePath(String table) {
        return String.join(File.separator, getResourcePath(), table);
    }
}
