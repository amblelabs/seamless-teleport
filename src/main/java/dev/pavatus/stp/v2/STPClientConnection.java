package dev.pavatus.stp.v2;

import dev.pavatus.stp.mixin.v2.ServerPlayNetworkHandlerAccessor;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.fabric.impl.event.interaction.FakePlayerNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import java.net.SocketAddress;

public class STPClientConnection extends ClientConnection {
    
    private final ClientConnection parent;

    public STPClientConnection(ServerPlayNetworkHandler networkHandler) {
        this(((ServerPlayNetworkHandlerAccessor) networkHandler).getConnection());
    }

    public STPClientConnection(ClientConnection connection) {
        super(connection.getSide());
        
        this.parent = connection;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        parent.channelActive(context);
    }

    @Override
    public void setState(NetworkState state) {
        parent.setState(state);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        parent.channelInactive(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable ex) {
        parent.exceptionCaught(context, ex);
    }

    @Override
    public void setPacketListener(PacketListener listener) {
        parent.setPacketListener(listener);
    }

    @Override
    public void send(Packet<?> packet) {
        parent.send(packet);
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketCallbacks callbacks) {
        parent.send(packet, callbacks);
    }

    @Override
    public void tick() {
        parent.tick();
    }

    @Override
    public SocketAddress getAddress() {
        return parent.getAddress();
    }

    @Override
    public void disconnect(Text disconnectReason) {
        parent.disconnect(disconnectReason);
    }

    @Override
    public boolean isLocal() {
        return parent.isLocal();
    }

    @Override
    public NetworkSide getSide() {
        return parent.getSide();
    }

    @Override
    public NetworkSide getOppositeSide() {
        return parent.getOppositeSide();
    }

    @Override
    public void setupEncryption(Cipher decryptionCipher, Cipher encryptionCipher) {
        parent.setupEncryption(decryptionCipher, encryptionCipher);
    }

    @Override
    public boolean isEncrypted() {
        return parent.isEncrypted();
    }

    @Override
    public boolean isOpen() {
        return parent.isOpen();
    }

    @Override
    public boolean isChannelAbsent() {
        return parent.isChannelAbsent();
    }

    @Override
    public PacketListener getPacketListener() {
        return parent.getPacketListener();
    }

    @Override
    @Nullable
    public Text getDisconnectReason() {
        return parent.getDisconnectReason();
    }

    @Override
    public void disableAutoRead() {
        parent.disableAutoRead();
    }

    @Override
    public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets) {
        parent.setCompressionThreshold(compressionThreshold, rejectsBadPackets);
    }

    @Override
    public void handleDisconnection() {
        parent.handleDisconnection();
    }

    @Override
    public float getAveragePacketsReceived() {
        return parent.getAveragePacketsReceived();
    }

    @Override
    public float getAveragePacketsSent() {
        return parent.getAveragePacketsSent();
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return parent.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        parent.channelRead(ctx, msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        parent.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        parent.channelUnregistered(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        parent.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        parent.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        parent.channelWritabilityChanged(ctx);
    }

    @Override
    public boolean isSharable() {
        return parent.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        parent.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        parent.handlerRemoved(ctx);
    }
}
