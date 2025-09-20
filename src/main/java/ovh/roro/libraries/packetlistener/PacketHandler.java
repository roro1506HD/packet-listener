package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
@FunctionalInterface
public interface PacketHandler<T extends PacketWrapper> {

    void handle(@NotNull PacketEvent<T> event);

}
