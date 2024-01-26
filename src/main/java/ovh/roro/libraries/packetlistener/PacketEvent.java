package ovh.roro.libraries.packetlistener;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class PacketEvent<T extends Packet> {

    private final @NotNull T packet;
    private final @NotNull CraftPlayer player;
    private final @NotNull List<Packet<?>> additionalPackets;
    private final @NotNull ConnectionProtocol protocol;
    private final @NotNull PacketFlow flow;

    private @NotNull Packet<?> packetToProcess;
    private boolean cancelled;

    PacketEvent(@NotNull T packet, @NotNull CraftPlayer player, @NotNull ConnectionProtocol protocol, @NotNull PacketFlow flow) {
        this.packet = packet;
        this.player = player;
        this.additionalPackets = new ArrayList<>();
        this.protocol = protocol;
        this.flow = flow;

        this.packetToProcess = packet;
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

    public void packet(@NotNull Packet<?> packet) {
        if (this.protocol.getPacketId(this.flow, packet) == -1) {
            throw new IllegalArgumentException("Cannot set packet: provided packet is not on the same flow and the same state");
        }

        this.packetToProcess = Objects.requireNonNull(packet);
    }

    public void addPacket(@NotNull Packet<?> packet) {
        this.additionalPackets.add(Objects.requireNonNull(packet));
    }

    public @NotNull CraftPlayer player() {
        return this.player;
    }

    @NotNull Packet<?> packetToProcess() {
        return this.packetToProcess;
    }

    @NotNull List<Packet<?>> additionalPackets() {
        return this.additionalPackets;
    }
}
