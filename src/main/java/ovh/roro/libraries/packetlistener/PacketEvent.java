package ovh.roro.libraries.packetlistener;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public class PacketEvent<T extends Packet> {

    private final @NotNull T packet;
    private final @NotNull CraftPlayer player;

    private boolean cancelled;

    public PacketEvent(@NotNull T packet, @NotNull CraftPlayer player) {
        this.packet = packet;
        this.player = player;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public @NotNull T packet() {
        return this.packet;
    }

    public @NotNull CraftPlayer player() {
        return this.player;
    }
}
