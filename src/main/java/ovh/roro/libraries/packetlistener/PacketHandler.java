package ovh.roro.libraries.packetlistener;

import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
@FunctionalInterface
public interface PacketHandler<T extends Packet> {

    void handle(@NotNull PacketEvent<T> event);

}
