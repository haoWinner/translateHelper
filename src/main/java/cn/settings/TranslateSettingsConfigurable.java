package cn.settings;

import cn.state.MySettingsState;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.newEditor.SettingsDialog;
import com.intellij.openapi.ui.DialogPanel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @Description: Settings -> TranslateHelper
 * @Author haodd
 * @Date 2024/8/10 11:02
 * @Version 1.0
 */
public class TranslateSettingsConfigurable implements Configurable {

    private JBTextField baiduAppIdTextField;
    private JBTextField baiduSecretKeyTextField;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "TranslateHelper";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        DialogPanel mainPanel = new DialogPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(new TitledSeparator("Baidu Translate"), gbc);

        addLabelAndTextField(gbc, mainPanel, 1, "appId:", baiduAppIdTextField = new JBTextField());
        addLabelAndTextField(gbc, mainPanel, 2, "secretKey:", baiduSecretKeyTextField = new JBTextField());

        return mainPanel;
    }

    private void addLabelAndTextField(GridBagConstraints gbc, DialogPanel mainPanel, int rowIndex, String labelName, JBTextField textField) {
        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel(labelName), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(textField, gbc);
    }

    @Override
    public boolean isModified() {
        String currentAppId = baiduAppIdTextField.getText();
        String currentSecretKey = baiduSecretKeyTextField.getText();

        MySettingsState settings = MySettingsState.getInstance();
        String savedAppId = settings.baiduAppId;
        String savedSecretKey = settings.baiduSecretKey;

        boolean appIdModified = !currentAppId.equals(savedAppId);
        boolean secretKeyModified = !currentSecretKey.equals(savedSecretKey);

        return appIdModified || secretKeyModified;
    }

    @Override
    public void apply() {
        MySettingsState.getInstance().baiduAppId = baiduAppIdTextField.getText();
        MySettingsState.getInstance().baiduSecretKey = baiduSecretKeyTextField.getText();
    }

    @Override
    public void reset() {
        baiduAppIdTextField.setText(MySettingsState.getInstance().baiduAppId);
        baiduSecretKeyTextField.setText(MySettingsState.getInstance().baiduSecretKey);
    }
}
