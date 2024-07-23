package ovh.roro.libraries.packetlistener;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class PacketManager implements Listener {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger("PacketManager");

    private final @NotNull Map<Class<?>, Set<PacketHandler>> handlers;

    public PacketManager(@NotNull JavaPlugin plugin) {
        this.handlers = new Object2ObjectOpenHashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public <T extends Packet> void addHandler(@NotNull Class<T> packetClass, @NotNull PacketHandler<T> handler) {
        this.handlers.computeIfAbsent(packetClass, unused -> new ObjectOpenHashSet<>()).add(handler);
    }

    public <T extends Packet> void removeHandler(@NotNull Class<T> packetClass, @NotNull PacketHandler<T> handler) {
        Set<PacketHandler> handlers = this.handlers.get(packetClass);

        if (handlers != null) {
            if (handlers.remove(handler) && handlers.isEmpty()) {
                this.handlers.remove(packetClass);
            }
        }
    }

    public <T extends Packet> @Nullable PacketEvent<T> handlePacket(@NotNull T packet, @NotNull CraftPlayer player) {
        Set<PacketHandler> handlers = this.handlers.get(packet.getClass());

        if (handlers == null) {
            return null;
        }

        PacketEvent<T> event = new PacketEvent<>(packet, player);

        for (PacketHandler handler : handlers) {
            try {
                //noinspection unchecked
                handler.handle(event);
            } catch (Throwable ex) {
                PacketManager.LOGGER.error("An error occurred while handling packet", ex);
            }
        }

        return event;
    }

    @EventHandler
    private void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        CraftPlayer craftPlayer = (CraftPlayer) event.getPlayer();

        craftPlayer.getHandle().connection.connection.channel.pipeline().addBefore("packet_handler", "ovh_roro_libraries_packet_handler", new ChannelListener(this, craftPlayer));
    }
}
