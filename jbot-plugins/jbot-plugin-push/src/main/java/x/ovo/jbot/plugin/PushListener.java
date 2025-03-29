package x.ovo.jbot.plugin;

import lombok.NonNull;
import x.ovo.jbot.core.event.Event;
import x.ovo.jbot.core.event.EventListener;
import x.ovo.jbot.core.plugin.Plugin;

import java.util.Set;
import java.util.stream.Collectors;

public class PushListener extends EventListener<Event<Object>, Object> {

    private final Set<String> pushEvents;
    private final String template;

    public PushListener(Plugin plugin) {
        super(plugin);
        this.pushEvents = this.plugin.getConfig().getJsonArray("push_event").stream()
                .map(String.class::cast)
                .collect(Collectors.toSet());
        this.template = this.plugin.getConfig().getString("template");
    }

    @Override
    public boolean support(@NonNull Event<Object> event, Object source) {
        return this.pushEvents.contains("*") || this.pushEvents.contains(event.getClass().getSimpleName());
    }

    @Override
    public boolean onEvent(@NonNull Event<Object> event, Object source) {
        PushStrategyFactory.push(EventConverter.convert(event, template));
        return true;
    }
}
