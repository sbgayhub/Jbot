package x.ovo.jbot.plugin.strategy;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Response;
import x.ovo.jbot.plugin.PushStrategy;

import java.io.IOException;

public class TelegramPushStrategy implements PushStrategy {

    private static String token, chatId;
    private static final String URL = "https://api.telegram.org/bot%s/sendMessage";

    @Override
    public String channel() {
        return "tgbot";
    }

    @Override
    public boolean setConfig(JsonObject json) {
        var config = json.getJsonObject(this.channel());
        if (config != null) {
            token = config.getString("token");
            chatId = config.getString("chat_idd");
            return StrUtil.isNotBlank(token) && StrUtil.isNotBlank(chatId);
        }
        return false;
    }

    @Override
    public Future<Void> push(String data) {
        return Future.future(promise -> {
            var body = JsonObject.of(
                    "chat_id", chatId,
                    "text", data
            );
            try (Response response = HttpUtil.createPost(URL.formatted(token)).body(body.encode()).send()) {
                if (!response.isOk()) {
                    promise.fail("请求失败，http状态码：" + response.getStatus());
                    return;
                }
                var res = Buffer.buffer(response.bodyStr()).toJsonObject();
                if (!res.getBoolean("ok")) {
                    promise.fail("请求失败，返回信息：" + res.getString("description"));
                    return;
                }
                promise.complete();
            } catch (IOException e) {
                promise.fail(e);
            }
        });
    }
}
