package ovh.roro.libraries.packetlistener;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
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

        if (event == null || !event.cancelled()) {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        PacketEvent<Packet> event = this.packetManager.handlePacket((Packet) msg, this.player);

        if (event == null || !event.cancelled()) {
            super.write(ctx, msg, promise);
        }
    }
}
