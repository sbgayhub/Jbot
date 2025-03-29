package x.ovo.jbot.plugin;

import x.ovo.jbot.core.command.CommandExecutor;
import x.ovo.jbot.core.event.Event;
import x.ovo.jbot.core.event.EventListener;
import x.ovo.jbot.core.plugin.Plugin;
import x.ovo.jbot.plugin.strategy.*;

public class PushPlugin extends Plugin {

    @Override
    public CommandExecutor getCommandExecutor() {
        return null;
    }

    @Override
    public void onLoad() throws Exception {
        // 根据配置文件初始化推送策略
        new DingPushStrategy().registry(this.config);
        new FeishuPushStrategy().registry(this.config);
        new PushplusPushStrategy().registry(this.config);
        new ServerchanPushStrategy().registry(this.config);
        new TelegramPushStrategy().registry(this.config);
        new WebhookPushStrategy().registry(this.config);
        new WechatworkPushStrategy().registry(this.config);
    }

    @Override
    public EventListener<Event<Object>, Object> getEventListener() {
        return new PushListener(this);
    }
}
