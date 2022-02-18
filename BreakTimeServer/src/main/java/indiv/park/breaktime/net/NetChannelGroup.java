package indiv.park.breaktime.net;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NetChannelGroup extends DefaultChannelGroup {

	public static final NetChannelGroup INSTANCE = new NetChannelGroup();
	
	public NetChannelGroup() {
		super(GlobalEventExecutor.INSTANCE);
	}
}
