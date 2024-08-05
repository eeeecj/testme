package com.weirddev.testme.intellij.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@State(name = "TableResourceConfig", storages = {@Storage("TableResourceConfig.xml")})
public class TableResourceConfigComponent implements PersistentStateComponent<TableResourceConfig> {

    private TableResourceConfig config;

    // 获取实例的便捷方法
    public static TableResourceConfigComponent getInstance(Project project) {
        return project.getService(TableResourceConfigComponent.class);
    }

    @Override
    public @Nullable TableResourceConfig getState() {
        if (config == null) {
            config = new TableResourceConfig();
            config.setTables(new ArrayList<>());
        }
        return config;
    }

    @Override
    public void loadState(@NotNull TableResourceConfig tableResourceConfig) {
        this.config = tableResourceConfig;
    }

    public TableResourceConfig getTableResourceConfig() {
        return getState();
    }


    public List<String> getTables(){
        if (getState()==null){
            return  new ArrayList<>();
        }
        return getState().getTables();
    }

    public String getClassPath(){
        if (getState()==null){
            return Strings.EMPTY;
        }
        return getState().getClassPath();
    }

    public String getFetchSize(){
        if (getState()==null){
            return Strings.EMPTY;
        }
        return getState().getFetchSize();
    }

    public String getSep(){
        if (getState()==null){return Strings.EMPTY;}
        return getState().getSep();
    }

}
