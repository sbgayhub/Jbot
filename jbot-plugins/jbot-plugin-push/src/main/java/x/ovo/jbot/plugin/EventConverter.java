package x.ovo.jbot.plugin;

import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.text.StrUtil;
import x.ovo.jbot.core.event.Event;
import x.ovo.jbot.core.event.ExceptionEvent;
import x.ovo.jbot.core.event.MessageEvent;

import java.util.Map;

public class EventConverter {

    /**
     * 将事件内容转为字符串
     *
     * @param event    事件
     * @param template 格式化模板
     * @return {@link String }
     */
    public static String convert(Event<?> event, String template) {
        if (event instanceof MessageEvent<?> e) return format(template, e.getClass().getSimpleName(), e.getContent());
        if (event instanceof ExceptionEvent e) return format(template, e.getClass().getSimpleName(), e.getData().getMessage());
        return format(template, event.getClass().getSimpleName(), (String) event.getData());
    }


    /**
     * 格式化
     *
     * @param template 模板
     * @param type     事件类型
     * @param content  事件内容
     * @return {@link String }
     */
    private static String format(String template, String type, String content) {
        return StrUtil.formatByMap(template, Map.of("type", type, "content", content, "timestamp", System.currentTimeMillis(), "datetime", DateUtil.formatNow()));
    }

}
