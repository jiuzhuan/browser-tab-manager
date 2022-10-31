package com.github.jiuzhuan.browser.tab.manager.navigation;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author pengfwang@trip.com
 * @date 2022/10/31 16:14
 */
public class SlaveTabEdit extends DialogWrapper {
    public JTextField tabText;
    public JTextField urlText;
    private JPanel contentPane;

    protected SlaveTabEdit(String tabTitle, String url) {
        super(true);
        init();
        setTitle("edit");
        if (tabTitle != null) tabText.setText(tabTitle);
        if (url != null) urlText.setText(url);
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
