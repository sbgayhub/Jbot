package x.ovo.jbot.plugin.strategy;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Response;
import x.ovo.jbot.plugin.PushStrategy;

import java.io.IOException;

public class WebhookPushStrategy implements PushStrategy {

    private static String url;

    @Override
    public String channel() {
        return "webhook";
    }

    @Override
    public boolean setConfig(JsonObject json) {
        var config = json.getJsonObject(this.channel());
        if (config != null) {
            url = config.getString("url");
            return StrUtil.isNotBlank(url);
        }
        return false;
    }

    @Override
    public Future<Void> push(String data) {
        return Future.future(promise -> {
            var body = JsonObject.of(
                    "title", "jbot 事件推送",
                    "data", data
            );
            try (Response response = HttpUtil.createPost(url).body(body.encode()).send()) {
                if (!response.isOk()) {
                    promise.fail("请求失败，http状态码：" + response.getStatus());
                    return;
                }
                promise.complete();
            } catch (IOException e) {
                promise.fail(e);
            }
        });
    }
}
