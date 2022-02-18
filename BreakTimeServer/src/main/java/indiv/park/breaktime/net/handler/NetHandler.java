package indiv.park.breaktime.net.handler;

import indiv.park.breaktime.game.etype.ControlType;
import indiv.park.breaktime.game.object.Stage;
import indiv.park.breaktime.net.NetChannelGroup;
import indiv.park.network.server.annotation.ServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@ServerHandler(group = "break", order = 3)
public class NetHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NetChannelGroup.INSTANCE.add(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Stage.INSTANCE.removeCharacter(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
		String move = frame.text();
		ControlType.valueOf(move).doControl(ctx.channel());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
}
