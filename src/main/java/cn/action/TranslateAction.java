package cn.action;

import cn.api.BaiduTranslateAPI;
import cn.hutool.core.util.StrUtil;
import cn.state.MySettingsState;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBTextArea;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Description: 翻译入口action
 * @Author haodd
 * @Date 2024/8/10 15:31
 * @Version 1.0
 */
public class TranslateAction extends AnAction {

    String[] items = {"zh", "en", "jp"};


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {


        MySettingsState settings = ApplicationManager.getApplication().getService(MySettingsState.class);
        System.out.println(settings.getState().baiduAppId);
        System.out.println(settings.getState().baiduSecretKey);

        String appId = settings.getState().baiduAppId;
        String secretKey = settings.getState().baiduSecretKey;

        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        String selectedText = editor.getSelectionModel().getSelectedText();

        if (StrUtil.isNotEmpty(selectedText)) {

            String translate = BaiduTranslateAPI.translate(appId, secretKey, selectedText, "en", "zh");

            HintManager.getInstance().showInformationHint(editor, translate);

        }else {

            JBTextArea fromTextArea = new JBTextArea();
            JBTextArea toTextArea = new JBTextArea();

            JBSplitter splitPane = new JBSplitter(false,0.5f);
            splitPane.setFirstComponent(fromTextArea);
            splitPane.setSecondComponent(toTextArea);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(splitPane, BorderLayout.CENTER);

            JComboBox<String> comboBoxFrom = new ComboBox<>(items);
            JComboBox<String> comboBoxTo = new ComboBox<>(items);

            DialogWrapper dialog = new DialogWrapper(e.getProject(), false) {
                {
                    init();
                    setTitle("Translate");
                }

                @Override
                protected JComponent createCenterPanel() {
                    return splitPane;
                }

                @Override
                protected JComponent createNorthPanel() {

                    comboBoxFrom.setSelectedItem(items[1]);
                    comboBoxTo.setSelectedItem(items[0]);

                    JPanel northPanel = new JPanel(new GridLayout(1, 2));
                    northPanel.add(comboBoxFrom);
                    northPanel.add(comboBoxTo);

                    return northPanel;

                }

                @Override
                protected JComponent createSouthPanel() {
                    return null;
                }
            };

            fromTextArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        checkInputCompletion();
                    }
                }

                private void checkInputCompletion() {
                    String text = fromTextArea.getText();

                    String from = items[comboBoxFrom.getSelectedIndex()];
                    String to = items[comboBoxTo.getSelectedIndex()];
                    String translate = BaiduTranslateAPI.translate(appId, secretKey, text, from, to);
                    toTextArea.setText(translate);

                }
            });

            dialog.setModal(false);
            dialog.setSize(554,362);
            dialog.show();

        }

    }

}
