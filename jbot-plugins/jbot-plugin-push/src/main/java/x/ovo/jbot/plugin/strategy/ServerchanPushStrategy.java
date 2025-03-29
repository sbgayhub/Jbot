package x.ovo.jbot.plugin.strategy;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Response;
import x.ovo.jbot.plugin.PushStrategy;

import java.io.IOException;

public class ServerchanPushStrategy implements PushStrategy {

    private static String token;
    private static final String URL = "https://sctapi.ftqq.com/%s.send";


    @Override
    public String channel() {
        return "serverchan";
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
                    "title", "jbot 事件推送",
                    "desp", data
            );
            try (Response response = HttpUtil.createPost(URL.formatted(token)).body(body.encode()).send()) {
                if (!response.isOk()) {
                    promise.fail("请求失败，http状态码：" + response.getStatus());
                    return;
                }
                var msg = Buffer.buffer(response.bodyStr()).toJsonObject().getString("message");
                if (StrUtil.isNotBlank(msg)) {
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
