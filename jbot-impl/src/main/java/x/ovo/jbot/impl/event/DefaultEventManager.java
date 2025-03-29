package x.ovo.jbot.impl.event;

import io.vertx.core.Future;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import x.ovo.jbot.core.Context;
import x.ovo.jbot.core.event.*;
import x.ovo.jbot.core.event.EventListener;
import x.ovo.jbot.core.plugin.Plugin;

import java.util.*;

@Slf4j
public class DefaultEventManager implements EventManager {

    private final List<Plugin> plugins = new ArrayList<>();
    @SuppressWarnings("rawtypes")
    private final Map<Plugin, EventListener> container = new HashMap<>(16);

    @Override
    public Future<Void> onInit() throws Exception {
        log.info("事件管理器初始化完成");
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> register(Plugin plugin) {
        if (Objects.isNull(plugin.getEventListener())) return Future.succeededFuture();
        return Future.future(promise -> {
            this.plugins.add(plugin);
            Collections.sort(this.plugins);
            this.container.put(plugin, plugin.getEventListener());
            log.debug("插件 [{}] 注册事件监听器成功", plugin.getDescription().getName());
            promise.complete();
        });
    }

    @Override
    public Future<Void> unregister(Plugin plugin) {
        return Future.future(promise -> {
            this.plugins.removeIf(plugin::equals);
            this.container.remove(plugin);
        });
    }

    @Override
    @SuppressWarnings({"t", "unchecked"})
    public Future<Void> publish(@NonNull Event<?> event) {
        log.debug("[{}] 事件触发: {}", event.getClass().getSimpleName(), event.getData());
        return Future.future(promise -> {
            // 遍历插件列表
            for (Plugin plugin : this.plugins) {
                // 如果插件未启用，则跳过
                if (!plugin.isEnabled()) continue;
                // 从容器中获取事件处理器
                var listener = this.container.get(plugin);
                // 如果处理器为空或者监听器判断条件不支持，则跳过
                if (listener == null) continue;

                // 如果是群消息事件，检查插件是否在限制名单中
                if (event instanceof MessageEvent<?> e && e.getData().isGroup()) {
                    var target = e.getData().getSender();
                    if (Context.get().getPluginManager().isLimited(plugin, target)) {
                        log.debug("[{}] 插件在群 [{}] 限制名单中，跳过执行", plugin.getDescription().getName(), target.getNickname());
                        continue;
                    }
                }

                // 检查事件类型是否匹配监听器期望的事件类型
                if (!listener.getEventClass().isAssignableFrom(event.getClass())) continue;
                // 检查事件源类型是否匹配监听器期望的事件源类型
                if (!listener.getSourceClass().isAssignableFrom(event.getData().getClass())) continue;
                // 根据监听器的判断条件，确认是否支持处理当前事件和事件来源
                if (!listener.support(event, event.getData())) continue;

                var result = false;
                try {
                    result = listener.onEvent(event, event.getData());
                } catch (Exception e) {
                    log.error("[{}] 插件事件处理时出现异常：{}", plugin.getDescription().getName(), e.getMessage());
                    PluginExceptionEvent.of(plugin, e).publish();
                }

                // 检查是否需要执行下一个监听器
                if (result && !listener.executeNext()) {
                    log.debug("[{}] 插件事件处理完成：{}，跳过 后续插件监听器", plugin.getDescription().getName(), true);
                    break;
                } else {
                    log.debug("[{}] 插件事件处理完成：{}， 执行 后续插件监听器", plugin.getDescription().getName(), result);
                }
            }
            promise.complete();
        });
    }

}
