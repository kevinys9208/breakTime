package indiv.park.breaktime.net.handler;

import indiv.park.breaktime.game.etype.ControlType;
import indiv.park.breaktime.game.object.Stage;
import indiv.park.breaktime.net.NetChannelGroup;
import indiv.park.network.server.annotation.ServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@ServerHandler(group = "break", order = 3)
public class NetHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NetChannelGroup.INSTANCE.add(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Stage.INSTANCE.removeCharacter(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		if (obj instanceof FullHttpRequest) {
			DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			response.content().writeCharSequence("Websocket connection allowed.", CharsetUtil.UTF_8);
			
			ctx.channel().writeAndFlush(response).addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					ctx.channel().close();
				}
			});
		}
		
		if (obj instanceof TextWebSocketFrame) {
			TextWebSocketFrame frame = null;
			
			try {
				frame = (TextWebSocketFrame) obj;
				
				String move = frame.text();
				ControlType.valueOf(move).doControl(ctx.channel());
				
			} finally {
				if (frame != null)
					ReferenceCountUtil.safeRelease(frame);
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
}
