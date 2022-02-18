package indiv.park.breaktime.net.handler;

import indiv.park.network.server.annotation.ServerHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;

@ServerHandler(group = "break", order = 1)
public class NetProtocolAggregator extends HttpObjectAggregator {

	public NetProtocolAggregator() {
		super(1024);
	}
}
