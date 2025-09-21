package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

class PacketEventsListener implements PacketListener {

    private final @NotNull PacketManager packetManager;

    public PacketEventsListener(@NotNull PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        this.handle(event, PacketHandlerHolder::receiveFactory);
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        this.handle(event, PacketHandlerHolder::sendFactory);
    }

    private <T extends PacketWrapper<T>, U extends ProtocolPacketEvent> void handle(
            @NotNull U event,
            @NotNull Function<PacketHandlerHolder<T>, Function<U, T>> factoryMapper
    ) {
        PacketEvent<T> packetEvent = this.packetManager.handlePacket(event, factoryMapper, event.getPlayer());

        if (packetEvent != null) {
            if (packetEvent.cancelled()) {
                packetEvent.packet().setBuffer(event.getByteBuf());
                event.markForReEncode(packetEvent.dirty());
            } else {
                event.setCancelled(true);
            }

            for (PacketWrapper<?> packet : packetEvent.additionalPackets()) {
                event.getUser().writePacketSilently(packet);
            }

            ChannelHelper.flushInContext(event.getChannel(), PacketEvents.ENCODER_NAME);
        }
    }
}
