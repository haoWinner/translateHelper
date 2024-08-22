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
 * @Description: 翻译相关参数页面定制：Settings -> TranslateHelper
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

        // 设置布局属性
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // 内边距
        gbc.weightx = 1.0; // 使组件在水平方向上扩展
        gbc.weighty = 0; // 确保不垂直扩展
        gbc.anchor = GridBagConstraints.WEST; // 标签左对齐

        // 第一行：TitledSeparator
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // 占据整行
        mainPanel.add(new TitledSeparator("Baidu Translate"), gbc);

        addLabelAndTextField(gbc, mainPanel, 1, "appId:", baiduAppIdTextField = new JBTextField());
        addLabelAndTextField(gbc, mainPanel, 2, "secretKey:", baiduSecretKeyTextField = new JBTextField());

        return mainPanel;
    }

    private void addLabelAndTextField(GridBagConstraints gbc, DialogPanel mainPanel, int rowIndex, String labelName, JBTextField textField) {
        // 第三行：secretKey文本框
        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.gridwidth = 1; // 标签占用一列
        gbc.weightx = 0; // 标签不扩展
        mainPanel.add(new JLabel(labelName), gbc);

        gbc.gridx = 1; // 文本框占用下一列
        gbc.weightx = 1.0; // 文本框扩展
        gbc.gridwidth = GridBagConstraints.REMAINDER; // 文本框占据剩余空间
        mainPanel.add(textField, gbc);
    }

    @Override
    public boolean isModified() {
        // 获取当前界面中设置的值
        String currentAppId = baiduAppIdTextField.getText();
        String currentSecretKey = baiduSecretKeyTextField.getText();

        // 获取存储中的值
        MySettingsState settings = MySettingsState.getInstance();
        String savedAppId = settings.baiduAppId;
        String savedSecretKey = settings.baiduSecretKey;

        // 比较当前值和保存的值
        boolean appIdModified = !currentAppId.equals(savedAppId);
        boolean secretKeyModified = !currentSecretKey.equals(savedSecretKey);

        // 只要其中一个值被修改了，返回 true
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
