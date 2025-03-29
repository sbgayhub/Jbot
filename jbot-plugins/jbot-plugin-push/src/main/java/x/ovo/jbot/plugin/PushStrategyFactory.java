package x.ovo.jbot.plugin;

import io.vertx.core.json.JsonArray;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.convert.ConvertUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@UtilityClass
public class PushStrategyFactory {

    private static Set<String> CHANNELS;
    private static final Map<String, PushStrategy> MAP = new HashMap<>(16);

    public static void registry(PushStrategy strategy) {
        MAP.put(strategy.channel(), strategy);
    }

    public static Set<String> getChannels() {
        return MAP.keySet();
    }

    public static void setChannels(JsonArray array) {
        if (Objects.isNull(array) || array.isEmpty()) CHANNELS = Set.of();
        else if (array.contains("*")) CHANNELS = getChannels();
        else CHANNELS = ConvertUtil.toSet(String.class, array);
        log.info("已设置推送渠道：{}", CHANNELS);
    }

    public static void push(String data) {
        CHANNELS.forEach(
                channel -> MAP.get(channel).push(data)
                        .onSuccess(v -> log.info("[{}] 推送成功", channel))
                        .onFailure(e -> log.error("[{}] 推送失败：{}", channel, e.getMessage(), e))
        );
    }

}
