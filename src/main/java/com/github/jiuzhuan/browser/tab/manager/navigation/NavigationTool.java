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
        // ���û��������ߴ��ڰ�ťʱ�������ù������createToolWindowContent()����
        ContentFactoryImpl factory = new ContentFactoryImpl();

        JBTabbedPane mainTab = this.newMainTabPane();
        toolWindow.getContentManager().addContent(factory.createContent(mainTab, "", false));
    }


    public JBTabbedPane newMainTabPane() {
        JBTabbedPane jbTabbedPane = new JBTabbedPane();
        jbTabbedPane.setTabComponentInsets(JBUI.emptyInsets());
        // ��ӱ�ǩҳ
        jbTabbedPane.insertTab(null, IconLoader.getIcon("/icons/add_dark.png", getClass()), this.getNewMainTabPanel(jbTabbedPane), null, 0);
        // ��ѯ����ǩҳ
        List<String> mainTitleList = NavigationTabMap.getMainTitleList();
        for (int i = 0; i < mainTitleList.size(); i++) {
            // ��ѯ�ӱ�ǩҳ
            jbTabbedPane.addTab(mainTitleList.get(i), this.newSalveTabPane(mainTitleList.get(i)));
        }
        // ��ѡ�����
        jbTabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainTabMouseListener(e, jbTabbedPane);
            }
        });
        return jbTabbedPane;
    }

    private void mainTabMouseListener(MouseEvent mouseEvent, JBTabbedPane jbTabbedPane) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            JMenuItem deleteMenuItem = new JMenuItem("ɾ��");
            deleteMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if (index != -1) {
                    String title = jbTabbedPane.getTitleAt(index);
                    // ҳ��ɾ��
                    jbTabbedPane.removeTabAt(index);
                    // ����ɾ��-����ǩҳ�����ӱ�ǩҳ
                    NavigationTabMap.deleteMainTab(title);
                }
            });
            JBPopupMenu jbPopupMenu = new JBPopupMenu();
            jbPopupMenu.add(deleteMenuItem);
            jbPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private JBTabbedPane newSalveTabPane(String mainTitle) {
        JBTabbedPane jbTabbedPane = new JBTabbedPane();
        jbTabbedPane.setTabComponentInsets(JBUI.emptyInsets());
        jbTabbedPane.insertTab(null, IconLoader.getIcon("/icons/add_dark.png", getClass()), this.getNewSalveTabPanel(mainTitle, jbTabbedPane), null, 0);
        List<Pair<String, String>> salveTabList = NavigationTabMap.getSalveTabList(mainTitle);
        // FIXME: 2020/7/1 �������ӱ�ǩ������ˢ��/���ҵȲ���
        List<JBCefBrowser> jbCefBrowserList = new ArrayList<>();
        for (int i = 0; i < salveTabList.size(); i++) {
            JBCefBrowser jbCefBrowser = new JBCefBrowser(salveTabList.get(i).getRight());
//            CefBrowser cefBrowser = jbCefBrowser.getCefBrowser();
//            JBCefJSQuery jbCefJSQuery = JBCefJSQuery.create(jbCefBrowser);
//            jbCefJSQuery.addHandler((link) -> {
//                // handle link here
//                return null; // can respond back to JS with JBCefJSQuery.Response
//            });
//            cefBrowser.executeJavaScript(
//                    "window.JavaPanelBridge = {" +
//                            "openInExternalBrowser : function(link) {" +
//                            jbCefJSQuery.inject("link") +
//                            "}" +
//                            "};",
//                    cefBrowser.getURL(), 0);
            jbCefBrowserList.add(jbCefBrowser);
            JComponent jbCefBrowserComponent = jbCefBrowser.getComponent();
            jbTabbedPane.addTab(salveTabList.get(i).getLeft(), null, jbCefBrowserComponent);
        }
        jbTabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                salveTabMouseListener(e, jbTabbedPane, mainTitle, jbCefBrowserList);
            }
        });
        // ע��ctrl+f������ݼ�
        jbTabbedPane.registerKeyboardAction(e -> {
            SearchTextForm searchTextForm = new SearchTextForm();
            searchTextForm.show();
            jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).getCefBrowser().find(0, searchTextForm.getSearchText(), true, false, true);
        }, KeyStroke.getKeyStroke("ctrl F"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //ע��f5ˢ�¿�ݼ�
        jbTabbedPane.registerKeyboardAction(e -> {
            jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).getCefBrowser().reload();
        }, KeyStroke.getKeyStroke("F5"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // register f12 devtools��ݼ�(F12��ռ��, ȡ��ռ�ò���Ч, �������ȼ�ActionPromoter? )
//        jbTabbedPane.registerKeyboardAction(e -> {
//            jbCefBrowserList.get(jbTabbedPane.getSelectedIndex() - 1).openDevtools();
//        }, KeyStroke.getKeyStroke("F12"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        return jbTabbedPane;
    }

    private void salveTabMouseListener(MouseEvent mouseEvent, JBTabbedPane jbTabbedPane, String mainTitle, List<JBCefBrowser> jbCefBrowserList) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            JMenuItem deleteMenuItem = new JMenuItem("ɾ��");
            deleteMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if (index != -1) {
                    jbTabbedPane.removeTabAt(index);
                    NavigationTabMap.deleteSalveTab(mainTitle, index - 1);
                }
            });
            JMenuItem refreshMenuItem = new JMenuItem("��ַ��");
            refreshMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if (index != -1) {
                    SearchTextForm searchTextForm = new SearchTextForm();
                    searchTextForm.setSearchText(jbCefBrowserList.get(index - 1).getCefBrowser().getURL());
                    searchTextForm.setSize(700, 100);
                    searchTextForm.show();
                    jbCefBrowserList.get(index - 1).loadURL(searchTextForm.getSearchText());
                }
            });
            JMenuItem reloadMenuItem = new JMenuItem("�ָ�����");
            reloadMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if (index != -1) {
                    // idea 2022.1�汾bug: Ƶ���򿪹رչ��ߴ����ᵼ��ҳ���޷�����, �����½�һ��ҳ�����
                    String url = NavigationTabMap.getSalveTabList(mainTitle).get(index - 1).getRight();
                    jbTabbedPane.setComponentAt(index, new JBCefBrowser(url).getComponent());
                }
            });
            JMenuItem devToolMenuItem = new JMenuItem("�����߹���");
            devToolMenuItem.addActionListener(e -> {
                int index = jbTabbedPane.indexAtLocation(mouseEvent.getX(), mouseEvent.getY());
                if (index != -1) {
                    jbCefBrowserList.get(index - 1).openDevtools();
                }
            });
            JBPopupMenu jbPopupMenu = new JBPopupMenu();
            jbPopupMenu.add(deleteMenuItem);
            jbPopupMenu.add(refreshMenuItem);
            jbPopupMenu.add(reloadMenuItem);
            jbPopupMenu.add(devToolMenuItem);
            jbPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public JBPanel getNewMainTabPanel(JBTabbedPane jbTabbedPane) {
        JBTextField titleText = new JBTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> this.addMainTab(jbTabbedPane, titleText.getText()));

        JButton deleteButton = new JButton("Delete All (Restart)");
        deleteButton.addActionListener(e -> this.deleteAllMainTab());

        JBPanel<JBPanel> jbPanel = new JBPanel<>();
        jbPanel.setLayout(new GridLayout(18, 1));
        jbPanel.add(new JBLabel("New TAB Title"));
        jbPanel.add(titleText);
        jbPanel.add(addButton);
        jbPanel.add(deleteButton);
        jbPanel.setBorder(BorderFactory.createEmptyBorder(200, 100, 200, 100));
//        Box verticalBox = Box.createVerticalBox();
//        jbPanel.add(verticalBox);
//        verticalBox.add(Box.createVerticalStrut(300));
//        verticalBox.add(new JBLabel("��ѡ�����"));
//        verticalBox.add(titleText);
//        verticalBox.add(addButton);
//        verticalBox.add(Box.createVerticalStrut(300));

        return jbPanel;
    }

    private void deleteAllMainTab() {
        for (String mainTitle : NavigationTabMap.getMainTitleList()) {
            NavigationTabMap.deleteMainTab(mainTitle);
        }
    }

    public JBPanel getNewSalveTabPanel(String mainTitle, JBTabbedPane jbTabbedPane) {

        JBTextField titleText = new JBTextField();
        JBTextField urlText = new JBTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> this.addSalveTab(mainTitle, jbTabbedPane, titleText.getText(), urlText.getText()));

        JBPanel<JBPanel> jbPanel = new JBPanel<>();
        jbPanel.setLayout(new GridLayout(16, 1));
        jbPanel.add(new JBLabel("New TAB Title"));
        jbPanel.add(titleText);
        jbPanel.add(new JBLabel("URL"));
        jbPanel.add(urlText);
        jbPanel.add(addButton);
        jbPanel.setBorder(BorderFactory.createEmptyBorder(200, 100, 200, 100));
        return jbPanel;
    }

    private void addMainTab(JBTabbedPane jbTabbedPane, String title) {
        jbTabbedPane.addTab(title, this.newSalveTabPane(title));
        NavigationTabMap.addMainTab(title);
    }

    private void addSalveTab(String mainTitle, JBTabbedPane jbTabbedPane, String title, String url) {
        jbTabbedPane.addTab(title, null, new JBCefBrowser(url).getComponent());
        NavigationTabMap.addSalveTab(mainTitle, title, url);
    }

}
