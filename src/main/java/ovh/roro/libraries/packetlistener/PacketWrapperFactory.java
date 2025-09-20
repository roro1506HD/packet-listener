package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PacketWrapperFactory<T extends PacketWrapper<T>> {

    @NotNull T create(@NotNull ProtocolPacketEvent event, boolean read);

}
