package com.github.jiuzhuan.browser.tab.manager.navigation;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SearchTextForm extends DialogWrapper {
    private JPanel contentPane;
    private JTextField searchTextField;

    public SearchTextForm(String title) {
        super(true);
        init();
        setTitle(title);
    }

    public String getSearchText() {
        return searchTextField.getText();
    }

    public void setSearchText(String text) {
        searchTextField.setText(text);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
