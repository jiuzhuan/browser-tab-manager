package com.github.jiuzhuan.browser.tab.manager.navigation;

import com.github.jiuzhuan.browser.tab.manager.common.repository.NavigationTabMap;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.ContentFactoryImpl;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2022/6/26 12:11
 */
public class NavigationTool implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 当用户单击工具窗口按钮时，将调用工厂类的createToolWindowContent()方法
        ContentFactoryImpl factory = new ContentFactoryImpl();

        JBTabbedPane mainTab = this.newMainTabPane();
        toolWindow.getContentManager().addContent(factory.createContent(mainTab, "", false));
    }


    public JBTabbedPane newMainTabPane() {
        JBTabbedPane jbTabbedPane = new JBTabbedPane();
        jbTabbedPane.setTabComponentInsets(JBUI.emptyInsets());
        // 添加标签页
        jbTabbedPane.insertTab(null, IconLoader.getIcon("/icons/add_dark.png", getClass()), this.getNewMainTabPanel(jbTabbedPane), null, 0);
        // 查询主标签页
        List<String> mainTitleList = NavigationTabMap.getMainTitleList();
        for (int i = 0; i < mainTitleList.size(); i++) {
            // 查询子标签页
            JBTabbedPane salveTabPane = this.newSalveTabPane(mainTitleList.get(i));
            jbTabbedPane.addTab(mainTitleList.get(i), salveTabPane);
        }
        // 主选项卡监听
        jbTabbedPane.addMouseListener(new MouseAdapter() {
            private Integer sourceIndex;
            @Override
            public void mousePressed(MouseEvent e) {
                int index = jbTabbedPane.indexAtLocation(e.getX(), e.getY());
                if(index < 1) return;
                sourceIndex = index;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int index = jbTabbedPane.indexAtLocation(e.getX(), e.getY());
                if(index < 1) return;
                if (index == sourceIndex) return;
                NavigationTabMap.moveMainTitleList(sourceIndex - 1, index - 1);
                String sourceTitle = jbTabbedPane.getTitleAt(sourceIndex);
                jbTabbedPane.insertTab(sourceTitle, null, newSalveTabPane(sourceTitle), null, index);
                if (index < sourceIndex) sourceIndex++;
                jbTabbedPane.removeTabAt(sourceIndex);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mainTabMouseListener(e, jbTabbedPane);
            }
        });
        return jbTabbedPane;
    }

    private void mainTabMouseListener(MouseEvent mouseEvent, JBTabbedPane jbTabbedPane) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            JMenuItem editMenuItem = new JMenuItem("Edit");
            editMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if(index != -1) {
                    String title = jbTabbedPane.getTitleAt(index);
                    OneTextForm searchTextForm = new OneTextForm("Edit", title);
                    searchTextForm.show();
                    jbTabbedPane.setTitleAt(index, searchTextForm.getText());
                    NavigationTabMap.updateMainTab(title, searchTextForm.getText());
                }
            });
            JMenuItem deleteMenuItem = new JMenuItem("Delete");
            deleteMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if(index != -1) {
                    String title = jbTabbedPane.getTitleAt(index);
                    // 页面删除
                    jbTabbedPane.removeTabAt(index);
                    // 配置删除-主标签页及其子标签页
                    NavigationTabMap.deleteMainTab(title);
                }
            });
            JBPopupMenu jbPopupMenu = new JBPopupMenu();
            jbPopupMenu.add(editMenuItem);
            jbPopupMenu.add(deleteMenuItem);
            jbPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private JBTabbedPane newSalveTabPane(String mainTitle) {
        List<JBCefBrowser> jbCefBrowserList = new ArrayList<>();

        JBTabbedPane jbTabbedPane = new JBTabbedPane();
        jbTabbedPane.setTabComponentInsets(JBUI.emptyInsets());
        jbTabbedPane.insertTab(null, IconLoader.getIcon("/icons/add_dark.png", getClass()), this.getNewSalveTabPanel(mainTitle, jbTabbedPane, jbCefBrowserList), null, 0);
        List<Pair<String, String>> salveTabList = NavigationTabMap.getSalveTabList(mainTitle);
        for (int i = 0; i < salveTabList.size(); i++) {
            JBCefBrowser jbCefBrowser = new JBCefBrowser(salveTabList.get(i).getRight());
            jbCefBrowserList.add(jbCefBrowser);
            JComponent jbCefBrowserComponent = jbCefBrowser.getComponent();
            jbTabbedPane.addTab(salveTabList.get(i).getLeft(), null, jbCefBrowserComponent);
        }
        jbTabbedPane.addMouseListener(new MouseAdapter() {
            private Integer sourceIndex;
            @Override
            public void mousePressed(MouseEvent e) {
                int index = jbTabbedPane.indexAtLocation(e.getX(), e.getY());
                if(index < 1) return;
                sourceIndex = index;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int index = jbTabbedPane.indexAtLocation(e.getX(), e.getY());
                if(index < 1) return;
                if (index == sourceIndex) return;
                List<Pair<String, String>> salveTabList = NavigationTabMap.moveSalveTabList(mainTitle, sourceIndex - 1, index - 1);
                String sourceTitle = jbTabbedPane.getTitleAt(sourceIndex);
                JBCefBrowser jbCefBrowser = new JBCefBrowser(salveTabList.get(index - 1).getRight());
                jbCefBrowserList.add(index - 1, jbCefBrowser);
                jbTabbedPane.insertTab(sourceTitle, null, jbCefBrowser.getComponent(), null, index);
                if (index < sourceIndex) sourceIndex++;
                jbCefBrowserList.remove(sourceIndex - 1);
                jbTabbedPane.removeTabAt(sourceIndex);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                salveTabMouseListener(e, jbTabbedPane, mainTitle, jbCefBrowserList);
            }
        });
        // 注册ctrl+f搜索快捷键
        jbTabbedPane.registerKeyboardAction(e -> {
            OneTextForm searchTextForm = new OneTextForm("Find", null);
            searchTextForm.show();
            jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).getCefBrowser().find(0, searchTextForm.getText(), true, false, true);
        }, KeyStroke.getKeyStroke("ctrl F"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //注册f5刷新快捷键
        jbTabbedPane.registerKeyboardAction(e -> {
            jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).getCefBrowser().reload();
        }, KeyStroke.getKeyStroke("F5"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // register f12 devtools快捷键(F12被占用, 取消占用才生效, 设置优先级ActionPromoter? )
//        jbTabbedPane.registerKeyboardAction(e -> {
//            jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).openDevtools();
//        }, KeyStroke.getKeyStroke("F12"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        return jbTabbedPane;
    }

    private void salveTabMouseListener(MouseEvent mouseEvent, JBTabbedPane jbTabbedPane, String mainTitle, List<JBCefBrowser> jbCefBrowserList) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            JMenuItem editMenuItem = new JMenuItem("Edit");
            editMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if(index != -1) {
                    String title = jbTabbedPane.getTitleAt(index);
                    String url = jbCefBrowserList.get(index - 1).getCefBrowser().getURL();
                    SlaveTabEdit slaveTabEdit = new SlaveTabEdit(title, url);
                    slaveTabEdit.show();
                    jbTabbedPane.setTitleAt(index, slaveTabEdit.tabText.getText());
                    jbCefBrowserList.get(index - 1).loadURL(slaveTabEdit.urlText.getText());
                    NavigationTabMap.updateSlaveTab(mainTitle, title, slaveTabEdit.tabText.getText(), slaveTabEdit.urlText.getText());
                }
            });
            JMenuItem deleteMenuItem = new JMenuItem("Delete");
            deleteMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if(index != -1) {
                    jbTabbedPane.removeTabAt(index);
                    jbCefBrowserList.remove(index - 1);
                    NavigationTabMap.deleteSalveTab(mainTitle, index - 1);
                }
            });
            JMenuItem urlMenuItem = new JMenuItem("URL");
            urlMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if(index != -1) {
                    OneTextForm searchTextForm = new OneTextForm("URL", jbCefBrowserList.get(index - 1).getCefBrowser().getURL());
                    searchTextForm.setSize(700, 100);
                    searchTextForm.show();
                    jbCefBrowserList.get(index - 1).loadURL(searchTextForm.getText());
                }
            });
            JMenuItem restoreMenuItem = new JMenuItem("Restore");
            restoreMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if(index != -1) {
                    // idea 2022.1版本bug: 频繁打开关闭工具创建会导致页面无法加载, 这里增加一个回复配置按钮
                    String url = NavigationTabMap.getSalveTabList(mainTitle).get(index - 1).getRight();
                    JBCefBrowser jbCefBrowser = new JBCefBrowser(url);
                    jbTabbedPane.setComponentAt(index, jbCefBrowser.getComponent());
                }
            });
            JMenuItem refreshMenuItem = new JMenuItem("Refresh(F5)");
            refreshMenuItem.addActionListener(e -> {
                jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).getCefBrowser().reload();
            });
            JMenuItem findMenuItem = new JMenuItem("Find(ctrl+F)");
            findMenuItem.addActionListener(e -> {
                OneTextForm searchTextForm = new OneTextForm("Find", null);
                searchTextForm.show();
                jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).getCefBrowser().find(0, searchTextForm.getText(), true, false, true);
            });
            JMenuItem devToolMenuItem = new JMenuItem("DevTools");
            devToolMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if(index != -1) {
                    jbCefBrowserList.get(index - 1).openDevtools();
                }
            });
            JBPopupMenu jbPopupMenu = new JBPopupMenu();
            jbPopupMenu.add(editMenuItem);
            jbPopupMenu.add(deleteMenuItem);
            jbPopupMenu.add(restoreMenuItem);
            jbPopupMenu.add(urlMenuItem);
            jbPopupMenu.add(refreshMenuItem);
            jbPopupMenu.add(findMenuItem);
            jbPopupMenu.add(devToolMenuItem);
            jbPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public JBPanel getNewMainTabPanel(JBTabbedPane jbTabbedPane){
        JBTextField titleText = new JBTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> this.addMainTab(jbTabbedPane, titleText.getText()));

        JButton deleteButton = new JButton("Delete All (Restart)");
        deleteButton.addActionListener(e -> this.deleteAllMainTab());

        JBPanel<JBPanel> contentPanel = new JBPanel<>();
        contentPanel.setLayout(new GridLayout(4, 1));
        contentPanel.add(new JBLabel("New Tab Title"));
        contentPanel.add(titleText);
        contentPanel.add(addButton);
        contentPanel.add(deleteButton);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 0, 100));

        // BorderLayout布局分为东南西北中五个区域, 其中南北可横向伸缩不可纵向伸缩, 东西相反, 中横向纵向都可伸缩
        JBPanel<JBPanel> panel = new JBPanel<>();
        panel.setLayout(new BorderLayout());
        panel.add(contentPanel, BorderLayout.NORTH);
        return panel;
    }

    private void deleteAllMainTab() {
        for (String mainTitle : NavigationTabMap.getMainTitleList()) {
            NavigationTabMap.deleteMainTab(mainTitle);
        }
    }

    public JBPanel getNewSalveTabPanel(String mainTitle, JBTabbedPane jbTabbedPane, List<JBCefBrowser> jbCefBrowserList){

        JBTextField titleText = new JBTextField();
        JBTextField urlText = new JBTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> this.addSalveTab(mainTitle, jbTabbedPane, titleText.getText(), urlText.getText(), jbCefBrowserList));

        JBPanel<JBPanel> contentPanel = new JBPanel<>();
        contentPanel.setLayout(new GridLayout(5, 1));
        contentPanel.add(new JBLabel("New Tab Title"));
        contentPanel.add(titleText);
        contentPanel.add(new JBLabel("URL"));
        contentPanel.add(urlText);
        contentPanel.add(addButton);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 0, 100));

        JBPanel<JBPanel> panel = new JBPanel<>();
        panel.setLayout(new BorderLayout());
        panel.add(contentPanel, BorderLayout.NORTH);
        return panel;
    }

    private void addMainTab(JBTabbedPane jbTabbedPane, String title) {
        if (StringUtils.isEmpty(title)) return;
        jbTabbedPane.addTab(title, this.newSalveTabPane(title));
        NavigationTabMap.addMainTab(title);
    }

    private void addSalveTab(String mainTitle, JBTabbedPane jbTabbedPane, String title, String url, List<JBCefBrowser> jbCefBrowserList) {
        if (StringUtils.isEmpty(title)) return;
        JBCefBrowser jbCefBrowser = new JBCefBrowser(url);
        jbTabbedPane.addTab(title, null, jbCefBrowser.getComponent());
        jbCefBrowserList.add(jbCefBrowser);
        NavigationTabMap.addSalveTab(mainTitle, title, url);
    }

}
