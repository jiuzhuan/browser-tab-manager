package com.github.jiuzhuan.browser.tab.manager.navigation;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OneTextForm extends DialogWrapper {
    private JPanel contentPane;
    private JTextField textField;

    public OneTextForm(String title, String text) {
        super(true);
        init();
        setTitle(title);
        if (text != null) textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
