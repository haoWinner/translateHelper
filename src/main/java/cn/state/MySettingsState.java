package cn.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Description: 负责存储和管理插件设置的持久化状态，通过 IntelliJ IDEA 的持久化状态机制来实现
 * @Author haodd
 * @Date 2024/8/10 10:22
 * @Version 1.0
 */
@State(
        name = "MyPluginSettings",
        storages = @Storage("MyPluginSettings.xml")
)
@Service(Service.Level.APP) // For application-level services
public final class MySettingsState implements PersistentStateComponent<MySettingsState> {

    public String baiduAppId = "";
    public String baiduSecretKey = "";

    public static MySettingsState getInstance() {
        return ApplicationManager.getApplication().getService(MySettingsState.class);
    }

    @Nullable
    @Override
    public MySettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MySettingsState state) {
        this.baiduAppId = state.baiduAppId;
        this.baiduSecretKey = state.baiduSecretKey;
    }
}
