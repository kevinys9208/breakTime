package indiv.park.breaktime.net.handler;

import indiv.park.network.server.annotation.ServerHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

@ServerHandler(group = "break", order = 2)
public class NetProtocolHandler extends WebSocketServerProtocolHandler {

	public NetProtocolHandler() {
		super("/break");
	}
}
