package com.weirddev.testme.intellij.ui.settings;

import cn.bugstack.guide.idea.plugin.domain.model.vo.CodeGenContextVO;
import cn.bugstack.guide.idea.plugin.domain.model.vo.ORMConfigVO;
import cn.bugstack.guide.idea.plugin.domain.service.IProjectGenerator;
import cn.bugstack.guide.idea.plugin.infrastructure.data.DataSetting;
import cn.bugstack.guide.idea.plugin.infrastructure.po.Table;
import cn.bugstack.guide.idea.plugin.infrastructure.utils.DBHelper;
import cn.bugstack.guide.idea.plugin.module.FileChooserComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.weirddev.testme.intellij.configuration.DatasourceConfigComponent;
import com.weirddev.testme.intellij.sql.DatasourceComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: 小傅哥，微信：fustack
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class TestMeTableStructure implements Configurable {

    private JPanel main;
    private JTextField classpath;
    private JTextField projectName;
    private JTextField database;
    private JTextField urlName;
    private JButton selectButton;
    private JTable table1;
    private JComboBox<String> urlCheckBox;

    private Project project;

    private List<String> tableNames;

    public TestMeTableStructure(Project project) {
        this.project = project;

        this.projectName.setText(project.getName());
        this.classpath.setText(project.getBasePath());

        this.tableNames = new ArrayList<>();
        initData();

        this.urlCheckBox.addActionListener(e -> {
            DatasourceConfigComponent component = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);

            String current = (String) urlCheckBox.getSelectedItem();

            component.setCurrent(current);
            database.setText(component.getDatabase());

            DatasourceComponent datasourceComponent = ApplicationManager.getApplication().getService(DatasourceComponent.class);
            datasourceComponent.updateDatasource();
        });

        // 查询数据库表列表
        this.selectButton.addActionListener(e -> {
            try {
                DatasourceComponent datasourceComponent = ApplicationManager.getApplication().getService(DatasourceComponent.class);

                List<String> tableList = datasourceComponent.getAllTableName(this.database.getText());

                String[] title = {"", "表名"};
                Object[][] data = new Object[tableList.size()][2];
                for (int i = 0; i < tableList.size(); i++) {
                    data[i][1] = tableList.get(i);
                }

                table1.setModel(new DefaultTableModel(data, title));
                TableColumn tc = table1.getColumnModel().getColumn(0);
                tc.setCellEditor(new DefaultCellEditor(new JCheckBox()));
                tc.setCellEditor(table1.getDefaultEditor(Boolean.class));
                tc.setCellRenderer(table1.getDefaultRenderer(Boolean.class));
                tc.setMaxWidth(100);
            } catch (Exception exception) {
                Messages.showWarningDialog(project, "数据库连接错误,请检查配置.", "Warning");
            }
        });

        // 给表添加事件
        this.table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (1 == e.getClickCount()) {
                    int rowIdx = table1.rowAtPoint(e.getPoint());
                    Boolean flag = (Boolean) table1.getValueAt(rowIdx, 0);
                    if (null != flag && flag) {
                        tableNames.add(table1.getValueAt(rowIdx, 1).toString());
                    } else {
                        tableNames.remove(table1.getValueAt(rowIdx, 1).toString());
                    }
                }
            }
        });
    }

    private void initData() {
        DatasourceConfigComponent datasourceConfigComponent = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);

        List<String> components = datasourceConfigComponent.getAllDatasourceNames();

        for (String component : components) {
            this.urlCheckBox.addItem(component);
        }

        this.urlCheckBox.setSelectedItem(datasourceConfigComponent.getName());

        this.database.setText(datasourceConfigComponent.getDatabase());
    }

    public @Nullable JComponent createComponent() {
        return main;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        // 链接DB
        DBHelper dbHelper = new DBHelper(config.getHost(), Integer.parseInt(config.getPort()), config.getUser(), config.getPassword(), config.getDatabase());

        // 组装代码生产上下文
        CodeGenContextVO codeGenContext = new CodeGenContextVO();
        codeGenContext.setModelPackage(config.getPoPath() + "/po/");
        codeGenContext.setDaoPackage(config.getDaoPath() + "/dao/");
        codeGenContext.setMapperDir(config.getXmlPath() + "/mapper/");
        List<Table> tables = new ArrayList<>();
        Set<String> tableNames = config.getTableNames();
        for (String tableName : tableNames) {
            tables.add(dbHelper.getTable(tableName));
        }
        codeGenContext.setTables(tables);

        // 生成代码
        projectGenerator.generation(project, codeGenContext);
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Config";
    }

}
