package x.ovo.jbot.plugin;


import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 推送策略
 *
 * @author ovo created on 2025/03/29.
 */
public interface PushStrategy {

    /**
     * 获取推送渠道名
     *
     * @return {@link String }
     */
    String channel();

    /**
     * 设置配置
     *
     * @param json config
     * @return boolean 结果
     * @implSpec 如果成功从json中获取到所需配置，则返回true，否则返回false，返回false不会注册该策略
     */
    boolean setConfig(JsonObject json);

    /**
     * 推送消息
     *
     * @param data 数据
     * @return {@link Future }<{@link Void }>
     */
    Future<Void> push(String data);

    /**
     * 注册推送策略
     *
     * @param json config
     */
    default void registry(JsonObject json) {
        // push_service中包含且配置成功，则注册推送策略
        var channels = json.getJsonArray("push_channels");
        if (channels.contains(this.channel()) && this.setConfig(json)) PushStrategyFactory.registry(this);
    }

}
