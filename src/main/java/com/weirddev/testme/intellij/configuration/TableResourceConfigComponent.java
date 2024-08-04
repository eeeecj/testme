package com.weirddev.testme.intellij.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import kotlinx.collections.immutable.PersistentCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;


@State(name = "TableResourceConfig", storages = {@Storage("TableResourceConfig.xml")})
public class TableResourceConfigComponent implements PersistentStateComponent<TableResourceConfig> {

    private TableResourceConfig config;

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
        XmlSerializerUtil.copyBean(tableResourceConfig, Objects.requireNonNull(getState()));
    }

    public TableResourceConfig getTableResourceConfig() {
        return getState();
    }
}
