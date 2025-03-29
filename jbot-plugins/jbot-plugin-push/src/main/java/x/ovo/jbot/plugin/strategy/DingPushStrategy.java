package x.ovo.jbot.plugin.strategy;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Response;
import x.ovo.jbot.plugin.PushStrategy;

import java.io.IOException;

/**
 * 钉钉推送策略
 *
 * @author ovo created on 2025/03/29.
 */
public class DingPushStrategy implements PushStrategy {

    private static final String URL = "https://oapi.dingtalk.com/robot/send?access_token=";
    private static String token;

    @Override
    public String channel() {
        return "dingding";
    }

    @Override
    public boolean setConfig(JsonObject json) {
        var config = json.getJsonObject(this.channel());
        if (config != null) {
            token = config.getString("token");
            return StrUtil.isNotBlank(token);
        }
        return false;
    }

    @Override
    public Future<Void> push(String data) {
        return Future.future(promise -> {
            var body = JsonObject.of(
                    "msgtype", "text",
                    "text", JsonObject.of("content", data)
            );
            try (Response response = HttpUtil.createPost(URL + token).body(body.encode()).send()) {
                if (!response.isOk()) {
                    promise.fail("请求失败，http状态码：" + response.getStatus());
                    return;
                }
                var msg = Buffer.buffer(response.bodyStr()).toJsonObject().getString("msg");
                if (!"ok".equals(msg)) {
                    promise.fail("请求失败，返回信息：" + msg);
                    return;
                }
                promise.complete();
            } catch (IOException e) {
                promise.fail(e);
            }
        });
    }
}
