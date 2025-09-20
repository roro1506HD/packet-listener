package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class PacketEvent<T extends PacketWrapper> {

    private final @NotNull T packet;
    private final @NotNull Player player;
    private final @NotNull List<PacketWrapper<?>> additionalPackets;

    private @NotNull PacketWrapper<?> packetToProcess;
    private boolean cancelled;
    private boolean dirty;

    PacketEvent(@NotNull T packet, @NotNull Player player) {
        this.packet = packet;
        this.player = player;
        this.additionalPackets = new ArrayList<>();

        this.packetToProcess = packet;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean dirty() {
        return this.dirty;
    }

    public void dirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public @NotNull T packet() {
        return this.packet;
    }

    public void packet(@NotNull PacketWrapper<?> packet) {
        this.packetToProcess = Objects.requireNonNull(packet);
    }

    public void addPacket(@NotNull PacketWrapper<?> packet) {
        this.additionalPackets.add(Objects.requireNonNull(packet));
    }

    public @NotNull Player player() {
        return this.player;
    }

    @NotNull PacketWrapper<?> packetToProcess() {
        return this.packetToProcess;
    }

    @NotNull List<PacketWrapper<?>> additionalPackets() {
        return this.additionalPackets;
    }
}
