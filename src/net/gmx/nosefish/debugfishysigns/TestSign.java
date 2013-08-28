package net.gmx.nosefish.debugfishysigns;

import java.util.regex.Pattern;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.gmx.nosefish.fishysigns.annotation.FishySignIdentifier;
import net.gmx.nosefish.fishysigns.activator.*;
import net.gmx.nosefish.fishysigns.plugin.engine.UnloadedSign;
import net.gmx.nosefish.fishysigns.signs.FishySign;
import net.gmx.nosefish.fishysigns.task.FishyTask;
import net.gmx.nosefish.fishysigns.watcher.PlayerRightClickWatcher;


public class TestSign extends FishySign {
	@FishySignIdentifier
	public static final Pattern[] regEx = {null, Pattern.compile("FishyTestSign"), null, null};
	
	public TestSign(UnloadedSign sign) {
		super(sign);
	}
	
	@Override
	public boolean validateOnCreate(String playerName) {
		Player player = Canary.getServer().getPlayer(playerName);
		player.message("FishySigns: You just created a TestSign :)");
		return true;
	}

	@Override
	public boolean validateOnLoad() {
		return true;
	}
	

	@Override
	public void initialize() {
		PlayerRightClickWatcher.getInstance().register(this, this.getLocation());
	}
	
	@Override
	public void activate(Activator activator) {
		if (activator instanceof ActivatorPlayer) {
			String name = ((ActivatorPlayer)activator).getPlayerName();
			String msg = "FishySigns: TestSign activated by player " + name + ".";  
			FishyTask message = 
					new PlayerMessage(name,	msg);
			message.submit();
		}
	}
	
	private class PlayerMessage extends FishyTask{
		private final String name;
		private final String message;
		
		public PlayerMessage(String playerName, String message) {
			this.name = playerName;
			this.message = message;
		}
		
		@Override
		public void doStuff() {
			try {
				Canary.getServer().getPlayer(name).message(message);
			} catch (Exception e) {
				// player was null or something like that. Who cares?
			}
		}
	}

	@Override
	public void remove() {
		PlayerRightClickWatcher.getInstance().remove(this);
	}
}
