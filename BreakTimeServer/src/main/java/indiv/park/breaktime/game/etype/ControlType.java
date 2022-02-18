package indiv.park.breaktime.game.etype;

import indiv.park.breaktime.game.object.Character;
import indiv.park.breaktime.game.object.Stage;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public enum ControlType {

	LON() {
		
		@Override
		public void doControl(Channel channel) {
			Character c = Stage.INSTANCE.characterMap.get(channel.id());
			if (c != null) {
				c.left.set(true);
			}
						
		}
		
	}, LOFF() {
		
		@Override
		public void doControl(Channel channel) {
			Character c = Stage.INSTANCE.characterMap.get(channel.id());
			if (c != null) {
				c.left.set(false);
			}
		}
		
	}, RON() {
		
		@Override
		public void doControl(Channel channel) {
			Character c = Stage.INSTANCE.characterMap.get(channel.id());
			if (c != null) {
				c.right.set(true);
			}
		}
		
	}, ROFF() {
		
		@Override
		public void doControl(Channel channel) {
			Character c = Stage.INSTANCE.characterMap.get(channel.id());
			if (c != null) {
				c.right.set(false);
			}
		}
		
	}, START() {

		@Override
		public void doControl(Channel channel) {
			if (!Stage.INSTANCE.isStart.get()) {
				Stage.INSTANCE.start();
			}
		}
		
	}, STOP() {

		@Override
		public void doControl(Channel channel) {
			if (Stage.INSTANCE.isStart.get()) {
				Stage.INSTANCE.stop();
			}
		}
		
	}, REQUEST_ID() {

		@Override
		public void doControl(Channel channel) {
			if (Stage.INSTANCE.isStart.get()) {
				channel.writeAndFlush(new TextWebSocketFrame("O"));
				return;
			}
			Stage.INSTANCE.createCharacter(channel);
		}
		
	};
	
	public abstract void doControl(Channel channel);
}
