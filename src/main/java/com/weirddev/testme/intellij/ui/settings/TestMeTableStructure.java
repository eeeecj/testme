package com.weirddev.testme.intellij.ui.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.weirddev.testme.intellij.HackedRuntimeInstance;
import com.weirddev.testme.intellij.configuration.DatasourceConfigComponent;
import com.weirddev.testme.intellij.configuration.DatasourceConfiguration;
import com.weirddev.testme.intellij.configuration.TableResourceConfig;
import com.weirddev.testme.intellij.configuration.TableResourceConfigComponent;
import com.weirddev.testme.intellij.generator.TableResourceGenerator;
import com.weirddev.testme.intellij.intention.FileChooserIntention;
import com.weirddev.testme.intellij.sql.DatasourceComponent;
import com.weirddev.testme.intellij.sql.SqlExecutor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 小傅哥，微信：fustack
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class TestMeTableStructure implements Configurable, Disposable {
    private static final Logger LOG = Logger.getInstance(HackedRuntimeInstance.class.getName());

    private JPanel main;
    private JTextField classpath;
    private JTextField database;
    private JTable table1;
    private JTextField host;
    private JTextField port;
    private JTextField user;
    private JPasswordField password;
    private JTextField url;
    private JTextArea testResult;
    private JPanel namePanel;
    private JButton saveConfiguration;
    private JButton testConnection;
    private JButton deleteButton;
    private JButton showTables;
    private JTextField fetchSize;
    private JTextField sep;
    private JButton classpathButton;
    private JButton newButton;

    private Project project;

    private List<String> tableNames;

    private final JTextField nameText = new JTextField();
    private final JComboBox<String> nameComboBox = new ComboBox<>();

    private static final String APPLICATION_NAME = "TestMeTaskQueue";

    private TableResourceGenerator tableResourceGenerator;

    public TestMeTableStructure(Project project, TableResourceGenerator tableResourceGenerator) {
        this.project = project;

        this.tableResourceGenerator = tableResourceGenerator;

        namePanel.setLayout(new BorderLayout());

        addDatasourceTextAction();

        main.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.tableNames = new ArrayList<>();

        initData();
        addActions();

        nameComboBox.addActionListener(e -> {
            DatasourceConfigComponent component = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);

            String current = (String) nameComboBox.getSelectedItem();

            component.setCurrent(current);

            host.setText(component.getHost());
            port.setText(component.getPort());
            user.setText(component.getUser());
            password.setText(component.getPassword());
            database.setText(component.getDatabase());

            DatasourceComponent datasourceComponent = ApplicationManager.getApplication().getService(DatasourceComponent.class);
            datasourceComponent.updateDatasource();
        });

        // 查询数据库表列表
        this.showTables.addActionListener(e -> {
            ProgressManager.getInstance().run(new Task.Backgroundable(null, APPLICATION_NAME) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    try {
                        DatasourceComponent datasourceComponent = ApplicationManager.getApplication().getService(DatasourceComponent.class);

                        List<String> tableList = datasourceComponent.getAllTableName();

                        String[] title = {"", "表名"};
                        Object[][] data = new Object[tableList.size()][2];
                        for (int i = 0; i < tableList.size(); i++) {
                            data[i][1] = tableList.get(i);
                        }

                        ApplicationManager.getApplication().invokeLater(() -> {
                            table1.setModel(new DefaultTableModel(data, title));
                            if (data.length > 0) {
                                table1.setPreferredSize(new Dimension(-1, 30 * data.length));
                            }
                            TableColumn tc = table1.getColumnModel().getColumn(0);
                            tc.setCellEditor(new DefaultCellEditor(new JCheckBox()));
                            tc.setCellEditor(table1.getDefaultEditor(Boolean.class));
                            tc.setCellRenderer(table1.getDefaultRenderer(Boolean.class));
                            tc.setMaxWidth(100);
                        });
                    } catch (Exception exception) {
                        ApplicationManager.getApplication().invokeLater(() -> testResult.setText("数据库连接错误,请检查配置"));
                    }
                }
            });
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

    private void addDatasourceTextAction() {
        host.getDocument().addDocumentListener(new DatasourceChangeListener());
        port.getDocument().addDocumentListener(new DatasourceChangeListener());
        database.getDocument().addDocumentListener(new DatasourceChangeListener());
    }

    private void addActions() {
        this.saveConfiguration.addMouseListener(new MouseCursorAdapter(this.saveConfiguration));
        this.testConnection.addMouseListener(new MouseCursorAdapter(this.testConnection));
        this.deleteButton.addMouseListener(new MouseCursorAdapter(this.deleteButton));
        this.newButton.addMouseListener(new MouseCursorAdapter(this.newButton));
        this.deleteButton.setEnabled(true);

        classpathButton.addActionListener((e) -> {
            FileChooserIntention component = FileChooserIntention.getInstance(project);
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile virtualFile = component.showFolderSelectionDialog("选择PO生成目录", baseDir, baseDir);
            if (null != virtualFile) {
                TestMeTableStructure.this.classpath.setText(virtualFile.getPath());
            }
        });

        newButton.addActionListener((e) -> addDatasource());

        saveConfiguration.addActionListener((e) -> ProgressManager.getInstance().run(new Task.Backgroundable(null, APPLICATION_NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                updateClassPathForPersistent();
                updateDatasourceForPersistent();
                ApplicationManager.getApplication().invokeLater(() -> testResult.setText("Save success."));
            }
        }));

        testConnection.addActionListener((e) -> ProgressManager.getInstance().run(new Task.Backgroundable(null, APPLICATION_NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                updateDatasourceForPersistent();
                String connectionInfo = SqlExecutor.testConnected();

                ApplicationManager.getApplication().invokeLater(() -> testResult.setText(connectionInfo));
            }
        }));

        deleteButton.addActionListener(e -> deleteDatasource());
    }

    private void initData() {
        TableResourceConfigComponent tableResourceConfigComponent = TableResourceConfigComponent.getInstance(project);
        classpath.setText(tableResourceConfigComponent.getClassPath());
        fetchSize.setText(tableResourceConfigComponent.getFetchSize());
        sep.setText(tableResourceConfigComponent.getSep());

        DatasourceConfigComponent datasourceConfigComponent = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);
        host.setText(datasourceConfigComponent.getHost());
        port.setText(datasourceConfigComponent.getPort());
        user.setText(datasourceConfigComponent.getUser());
        password.setText(datasourceConfigComponent.getPassword());
        database.setText(datasourceConfigComponent.getDatabase());

        List<String> components = datasourceConfigComponent.getAllDatasourceNames();
        if (StringUtils.isNotBlank(datasourceConfigComponent.getName())) {
            displayNameComboBox();
            String urlText = String.format(DatasourceComponent.DATABASE_URL_TEMPLATE,
                    datasourceConfigComponent.getHost(), datasourceConfigComponent.getPort(),
                    datasourceConfigComponent.getDatabase());
            url.setText(urlText);
        } else {
            addDatasource();
        }
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
        updateClassPathForPersistent();
        updateDatasourceForPersistent();

        TableResourceConfigComponent tableResourceConfig = TableResourceConfigComponent.getInstance(project);
        TableResourceConfig config = tableResourceConfig.getTableResourceConfig();

        try {
            tableResourceGenerator.generation(project, config);
        } catch (Exception e) {
            LOG.error(e);
            Messages.showWarningDialog(project, "生成数据配置文件错误:\n" + e.getMessage(), "Warning");
        }
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Table Generator";
    }


    private class DatasourceChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateUrlTextField();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateUrlTextField();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateUrlTextField();
        }

        private void updateUrlTextField() {
            String hostText = host.getText();
            String portText = port.getText();
            String databaseText = database.getText();
            String urlText = String.format(DatasourceComponent.DATABASE_URL_TEMPLATE, hostText, portText, databaseText);

            url.setText(urlText);
        }
    }


    private void updateClassPathForPersistent() {
        TableResourceConfigComponent tableResourceConfig = TableResourceConfigComponent.getInstance(project);
        TableResourceConfig config = tableResourceConfig.getTableResourceConfig();

        config.setClassPath(this.classpath.getText());
        config.setTables(this.tableNames);
        config.setSep(this.sep.getText());
        config.setFetchSize(this.fetchSize.getText() != null ? this.fetchSize.getText() : "10");
    }

    private void updateDatasourceForPersistent() {

        DatasourceConfigComponent component = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);

        DatasourceComponent datasourceComponent = ApplicationManager.getApplication().getService(DatasourceComponent.class);

        String name = "";
        if (nameText.isVisible()) {
            name = nameText.getText();
        } else if (nameComboBox.isVisible()) {
            name = (String) nameComboBox.getSelectedItem();
        }

        component.setCurrent(name);

        DatasourceConfiguration configuration = component.getConfig();
        if (configuration == null) {
            configuration = new DatasourceConfiguration();
            component.addDatasourceConfiguration(configuration);
        }

        configuration.name(name).host(host.getText()).port(port.getText())
                .user(user.getText())
                .password(String.valueOf(password.getPassword()))
                .database(database.getText());

        datasourceComponent.updateDatasource();

    }

    private void addDatasource() {
        // 隐藏combobox, 显示text field用于创建新的数据源
        displayNameFieldText();
        this.deleteButton.setEnabled(false);
    }

    private void displayNameFieldText() {
        // 隐藏combobox, 显示text field用于创建新的数据源
        host.setText(StringUtils.EMPTY);
        port.setText(StringUtils.EMPTY);
        user.setText(StringUtils.EMPTY);
        password.setText(StringUtils.EMPTY);
        database.setText(StringUtils.EMPTY);

        nameText.setVisible(true);
        nameComboBox.setVisible(false);
        namePanel.remove(nameComboBox);
        namePanel.add(nameText);
        nameText.setText("");
    }

    private void displayNameComboBox() {

        DatasourceConfigComponent component = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);

        List<String> datasourceNames = component.getAllDatasourceNames();
        nameComboBox.removeAllItems();
        for (String name : datasourceNames) {
            nameComboBox.addItem(name);
        }
        nameComboBox.setSelectedItem(component.getName());

        namePanel.remove(nameText);
        namePanel.add(nameComboBox);

        nameText.setVisible(false);
        nameComboBox.setVisible(true);

    }

    private void deleteDatasource() {
        DatasourceConfigComponent component = ApplicationManager.getApplication().getService(DatasourceConfigComponent.class);
        String removedName = component.getName();
        component.remove();
        nameComboBox.removeItem(removedName);
        if (StringUtils.isBlank(component.getName())) {
            addDatasource();
        }
        nameComboBox.setSelectedItem(component.getName());
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

}
