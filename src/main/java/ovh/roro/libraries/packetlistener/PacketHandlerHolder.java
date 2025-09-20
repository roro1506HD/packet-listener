package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.NotNull;

class PacketHandlerHolder<T extends PacketWrapper<T>> {

    private final @NotNull PacketHandler<T> handler;
    private final @NotNull PacketWrapperFactory<T> factory;

    PacketHandlerHolder(@NotNull PacketHandler<T> handler, @NotNull PacketWrapperFactory<T> factory) {
        this.handler = handler;
        this.factory = factory;
    }

    public @NotNull PacketHandler<T> handler() {
        return this.handler;
    }

    public @NotNull PacketWrapperFactory<T> factory() {
        return this.factory;
    }
}
