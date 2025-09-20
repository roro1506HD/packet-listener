package ovh.roro.libraries.packetlistener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
class PacketEventsListener implements PacketListener {

    private final @NotNull PacketManager packetManager;

    public PacketEventsListener(@NotNull PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        this.handle(event);
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        this.handle(event);
    }

    private void handle(@NotNull ProtocolPacketEvent event) {
        PacketEvent<PacketWrapper> packetEvent = this.packetManager.handlePacket(event, event.getPlayer());

        if (packetEvent != null) {
            if (packetEvent.cancelled()) {
                event.setCancelled(true);
                return;
            }

            packetEvent.packet().setBuffer(event.getByteBuf());
            for (PacketWrapper<?> packet : packetEvent.additionalPackets()) {
                event.getUser().sendPacketSilently(packet);
            }
        }
    }
}
