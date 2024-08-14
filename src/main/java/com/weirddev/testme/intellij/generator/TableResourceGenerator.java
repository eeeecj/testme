package com.weirddev.testme.intellij.generator;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.weirddev.testme.intellij.HackedRuntimeInstance;
import com.weirddev.testme.intellij.configuration.TableResourceConfig;
import com.weirddev.testme.intellij.sql.SqlExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TableResourceGenerator {

    private static final Logger LOG = Logger.getInstance(HackedRuntimeInstance.class.getName());

    public void generation(Project project, TableResourceConfig config) throws Exception {
        List<String> tables = config.getTables();
        for (String table : tables) {
            createTableStructure(project, config, table);
            createColumnStructure(project, config, table);
            createDataStructure(project, config, table);
        }

    }

    public void createTableStructure(Project project, TableResourceConfig config, String table) throws Exception {
        String content = SqlExecutor.getCreateTableSql(table);
        if (StringUtil.isEmpty(content)) {
            throw new Exception("The Table " + table + " does not exist");
        }
        writeFile(project, config.getTablePath(table), table + ".txt", content);
    }

    public void createColumnStructure(Project project, TableResourceConfig config, String table) throws Exception {
        List<String> columns = SqlExecutor.getColumns(table);
        if (null == columns) {
            throw new Exception("The Table has no columns");
        }
        String content = String.join(",", columns);
        writeFile(project, config.getTablePath(table), table + ".col", content);
    }

    public void createDataStructure(Project project, TableResourceConfig config, String table) throws Exception {
        List<Map<String, Object>> data = SqlExecutor.getData(table, config.getFetchSize());
        List<String> columns = SqlExecutor.getColumns(table);
        if (null == columns) {
            throw new Exception("The Table has no columns");
        }
        StringBuilder sb = new StringBuilder();
        data.forEach(p -> {
            columns.forEach(c -> {
                sb.append(p.get(c));
                sb.append(config.getSep());
            });
            sb.delete(sb.lastIndexOf(config.getSep()), sb.length());
            sb.append("\n");
        });
        writeFile(project, config.getTablePath(table), table + ".dat", sb.toString());
    }


    public void writeFile(Project project, String packageName, String name, String content) throws IOException {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile virtualFile = null;
            try {
                virtualFile = createPackageDir(packageName).createChildData(project, name);
                virtualFile.setBinaryContent(content.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOG.error(e);
            }
        });

    }

    private static VirtualFile createPackageDir(String packageName) {
        String path = FileUtil.toSystemIndependentName(StringUtil.replace(packageName, ".", "/"));
        new File(path).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }
}
