package ovh.roro.libraries.packetlistener;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
class ChannelListener extends ChannelDuplexHandler {

    private final @NotNull PacketManager packetManager;
    private final @NotNull CraftPlayer player;

    public ChannelListener(@NotNull PacketManager packetManager, @NotNull CraftPlayer player) {
        this.packetManager = packetManager;
        this.player = player;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketEvent<Packet> event = this.packetManager.handlePacket((Packet) msg, this.player);

        if (event == null) {
            super.channelRead(ctx, msg);
        } else {
            if (!event.cancelled()) {
                super.channelRead(ctx, event.packetToProcess());
            }

            for (Packet<?> packet : event.additionalPackets()) {
                super.channelRead(ctx, packet);
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        PacketEvent<Packet> event = this.packetManager.handlePacket((Packet) msg, this.player);

        if (event == null) {
            super.write(ctx, msg, promise);
        } else {
            if (!event.cancelled()) {
                super.write(ctx, event.packetToProcess(), promise);
            }

            for (Packet<?> packet : event.additionalPackets()) {
                super.write(ctx, packet, ctx.newPromise());
            }
        }
    }
}
