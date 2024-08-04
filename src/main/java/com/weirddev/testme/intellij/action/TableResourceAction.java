package com.weirddev.testme.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.weirddev.testme.intellij.generator.TableResourceGenerator;
import com.weirddev.testme.intellij.ui.settings.TestMeTableStructure;
import org.jetbrains.annotations.NotNull;

public class TableResourceAction extends AnAction {

    private TableResourceGenerator generator = new TableResourceGenerator();

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        ShowSettingsUtil.getInstance().editConfigurable(project, new TestMeTableStructure(project, generator));
    }

}
