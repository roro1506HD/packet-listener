package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

class PacketHandlerHolder<T extends PacketWrapper<T>> {

    private final @NotNull PacketHandler<T> handler;
    private final @Nullable Function<PacketSendEvent, T> sendFactory;
    private final @Nullable Function<PacketReceiveEvent, T> receiveFactory;

    PacketHandlerHolder(
            @NotNull PacketHandler<T> handler,
            @Nullable Function<PacketSendEvent, T> sendFactory,
            @Nullable Function<PacketReceiveEvent, T> receiveFactory
    ) {
        this.handler = handler;
        this.sendFactory = sendFactory;
        this.receiveFactory = receiveFactory;
    }

    public @NotNull PacketHandler<T> handler() {
        return this.handler;
    }

    public @Nullable Function<PacketSendEvent, T> sendFactory() {
        return this.sendFactory;
    }

    public @Nullable Function<PacketReceiveEvent, T> receiveFactory() {
        return this.receiveFactory;
    }
}
