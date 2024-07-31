package com.weirddev.testme.intellij.ui.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.weirddev.testme.intellij.configuration.DatasourceConfigComponent;
import com.weirddev.testme.intellij.configuration.TestMeConfigPersistent;
import com.weirddev.testme.intellij.configuration.TestMeWebHelpProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Date: 28/07/2018
 *
 * @author Yaron Yamin
 */
public class TestMeConfigurable1 implements SearchableConfigurable {


    private TestMeDatasourceSettingForm testMeDatasourceSettingForm;
    private DatasourceConfigComponent datasourceConfigComponent;

    public TestMeConfigurable1() {
        datasourceConfigComponent = DatasourceConfigComponent.getInstance();
    }

    @NotNull
    @Override
    public String getId() {
        return TestMeWebHelpProvider.PREFERENCES_TEST_ME_ID;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Datasource";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return TestMeWebHelpProvider.settingsHelpId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        testMeDatasourceSettingForm=new TestMeDatasourceSettingForm();
//        return testMeSettingsForm.getRootPanel();
        return testMeDatasourceSettingForm.getContentPane();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {
        testMeDatasourceSettingForm.dispose();
    }
}
