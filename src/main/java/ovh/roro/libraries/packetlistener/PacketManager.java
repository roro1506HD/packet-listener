package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class PacketManager {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger("PacketManager");

    private final @NotNull PacketEventsAPI<Plugin> api;

    private final @NotNull Map<Class<?>, Set<PacketHandlerHolder>> handlers;

    @SuppressWarnings("UnstableApiUsage")
    public PacketManager(@NotNull JavaPlugin plugin) {
        this.api = SpigotPacketEventsBuilder.build(
                plugin,
                new PacketEventsSettings()
                        .reEncodeByDefault(false)
                        .downsampleColors(false)
                        .checkForUpdates(false)
                        .fullStackTrace(true)
        );

        this.api.init();
        this.api.getEventManager().registerListener(new PacketEventsListener(this), PacketListenerPriority.NORMAL);

        this.handlers = new Object2ObjectOpenHashMap<>();
    }

    public void terminate() {
        this.api.terminate();
    }

    public <T extends PacketWrapper<T>> void addHandler(@NotNull Class<T> packetClass, @NotNull PacketWrapperFactory<T> factory, @NotNull PacketHandler<T> handler) {
        Set<PacketHandlerHolder> handlers = this.handlers.computeIfAbsent(packetClass, unused -> new ObjectOpenHashSet<>());

        for (PacketHandlerHolder holder : handlers) {
            if (holder.handler().equals(handler)) {
                // Prevent duplicate handlers
                return;
            }
        }

        handlers.add(new PacketHandlerHolder<>(handler, factory));
    }

    public <T extends PacketWrapper<T>> void removeHandler(@NotNull Class<T> packetClass, @NotNull PacketHandler<T> handler) {
        Set<PacketHandlerHolder> handlers = this.handlers.get(packetClass);

        if (handlers != null) {
            boolean removed = handlers.removeIf(holder -> holder.handler().equals(handler));
            if (removed && handlers.isEmpty()) {
                this.handlers.remove(packetClass);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends PacketWrapper> @Nullable PacketEvent<T> handlePacket(@NotNull ProtocolPacketEvent event, @NotNull Player player) {
        Set<PacketHandlerHolder> handlers = this.handlers.get(event.getPacketType().getWrapperClass());

        if (handlers == null) {
            return null;
        }

        PacketEvent<T> packetEvent = null;
        for (PacketHandlerHolder holder : handlers) {
            if (packetEvent == null) {
                packetEvent = new PacketEvent<>((T) holder.factory().create(event, true), player);
            }

            try {
                holder.handler().handle(packetEvent);
            } catch (Throwable ex) {
                PacketManager.LOGGER.error("An error occurred while handling packet", ex);
            }
        }

        return packetEvent;
    }
}
