package cn.action;

import cn.api.BaiduTranslateAPI;
import cn.hutool.core.util.StrUtil;
import cn.state.MySettingsState;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.AsyncProcessIcon;
import org.jetbrains.annotations.NotNull;
import org.jsoup.internal.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Description: action
 * @Author haodd
 * @Date 2024/8/10 15:31
 * @Version 1.0
 */
public class TranslateAction extends AnAction {

    String[] items = {"zh", "en"};


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {


        MySettingsState settings = ApplicationManager.getApplication().getService(MySettingsState.class);
        System.out.println(settings.getState().baiduAppId);
        System.out.println(settings.getState().baiduSecretKey);

        String appId = settings.getState().baiduAppId;
        String secretKey = settings.getState().baiduSecretKey;

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        String selectedText;
        if (editor != null) {
            selectedText = editor.getSelectionModel().getSelectedText();
        } else {
            selectedText = null;
        }

        if (StrUtil.isNotEmpty(selectedText)) {

//            String translate = BaiduTranslateAPI.translate(appId, secretKey, selectedText, "en", "zh");
//            HintManager.getInstance().showInformationHint(editor, translate);

            JLabel loadingLabel = new JLabel(AnimatedIcon.Default.INSTANCE, SwingConstants.LEFT);

            Balloon balloon = JBPopupFactory.getInstance()
                    .createBalloonBuilder(loadingLabel)
                    .setFillColor(JBColor.background())
                    .setHideOnClickOutside(true)
                    .setCloseButtonEnabled(false)
                    .createBalloon();

            balloon.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below);
            ApplicationManager.getApplication().executeOnPooledThread(() -> {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                String translate = BaiduTranslateAPI.translate(appId, secretKey, selectedText, "en", "zh");
                ApplicationManager.getApplication().invokeLater(() -> {
                    balloon.hide();

                    JLabel resultLabel = new JLabel(translate);
                    Balloon resultBalloon = JBPopupFactory.getInstance()
                            .createBalloonBuilder(resultLabel)
                            .setFillColor(JBColor.background())
                            .setHideOnClickOutside(true)
                            .createBalloon();

                    resultBalloon.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below);
                });
            });

        }else {

            JBTextArea fromTextArea = new JBTextArea();
            JBTextArea toTextArea = new JBTextArea();
            fromTextArea.setLineWrap(true);
            toTextArea.setLineWrap(true);
            toTextArea.setEditable(false);

            JScrollPane fromScroll = new JBScrollPane(fromTextArea);
            JScrollPane toScroll = new JBScrollPane(toTextArea);

            JBSplitter splitPane = new JBSplitter(false, 0.5f);
            splitPane.setFirstComponent(fromScroll);
            splitPane.setSecondComponent(toScroll);

            JComboBox<String> comboBoxFrom = new ComboBox<>(items);
            JComboBox<String> comboBoxTo = new ComboBox<>(items);
            comboBoxTo.setSelectedIndex(1); // default to "en"

            JButton swapButton = new JButton(AllIcons.Actions.Refresh);
            swapButton.addActionListener(e1 -> {
                int fromIndex = comboBoxFrom.getSelectedIndex();
                comboBoxFrom.setSelectedIndex(comboBoxTo.getSelectedIndex());
                comboBoxTo.setSelectedIndex(fromIndex);
            });

            JButton translateButton = new JButton(AllIcons.Actions.Execute);
            translateButton.addActionListener(e1 -> {
                String text = fromTextArea.getText();
                if (text.trim().isEmpty()) return;

                String from = (String) comboBoxFrom.getSelectedItem();
                String to = (String) comboBoxTo.getSelectedItem();

                AsyncProcessIcon loadingIcon = new AsyncProcessIcon("loading...");
                JPanel loadingPanel = new JPanel(new BorderLayout());
                loadingPanel.add(loadingIcon, BorderLayout.CENTER);
                splitPane.setSecondComponent(loadingPanel);

                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    String result = BaiduTranslateAPI.translate(appId, secretKey, text, from, to);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        splitPane.setSecondComponent(toScroll);
                        toTextArea.setText(result);
                    });
                });
            });

            DialogWrapper dialog = new DialogWrapper(editor.getProject(), false) {
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
                    JPanel panel = new JPanel(new BorderLayout());

                    JPanel comboPanel = new JPanel(new GridLayout(1, 3));
                    comboPanel.add(comboBoxFrom);
                    comboPanel.add(swapButton);
                    comboPanel.add(comboBoxTo);

                    panel.add(comboPanel, BorderLayout.CENTER);
                    return panel;
                }

                @Override
                protected JComponent createSouthPanel() {
                    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    panel.add(translateButton);
                    return panel;
                }
            };

            dialog.setModal(false);
            dialog.setSize(600, 400);
            dialog.show();


        }

    }

}
