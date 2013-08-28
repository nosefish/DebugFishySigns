package net.gmx.nosefish.debugfishysigns;

import java.util.regex.Pattern;

import net.gmx.nosefish.fishylib.worldmath.FishyLocationInt;
import net.gmx.nosefish.fishylib.worldmath.FishyVectorInt;
import net.gmx.nosefish.fishysigns.Log;
import net.gmx.nosefish.fishysigns.activator.Activator;
import net.gmx.nosefish.fishysigns.activator.ActivatorBlocks;
import net.gmx.nosefish.fishysigns.annotation.FishySignIdentifier;
import net.gmx.nosefish.fishysigns.watcher.PollingBlockChangeWatcher;
import net.gmx.nosefish.fishysigns.plugin.engine.UnloadedSign;
import net.gmx.nosefish.fishysigns.signs.FishySign;

public class BlockTestSign extends FishySign {
	@FishySignIdentifier
	public static final Pattern[] regEx = {null, Pattern.compile("FishyBlockTest"), null, null};

	public BlockTestSign(UnloadedSign sign) {
		super(sign);
	}
	
	@Override
	public void initialize() {
		for (int x = -3; x <= 3; x++)
			for (int y = 0; y < 255; y++)
				for (int z = -3; z <= 3; z++) {
					FishyVectorInt offsetVector = new FishyVectorInt(x, y, z);
					FishyLocationInt location = this.location.addIntVector(offsetVector);
					PollingBlockChangeWatcher.getInstance().register(this, location);
				}
		Log.get().logInfo("FishyBlockTest initialized at location " 
				+ location.toString());
	}
	
	@Override
	public void activate(Activator activator) {
		if (activator instanceof ActivatorBlocks) {
			Log.get().logInfo("Activated by block change.");
		}
	}

	@Override
	public boolean validateOnCreate(String arg0) {
		return true;
	}

	@Override
	public boolean validateOnLoad() {
		return true;
	}

	@Override
	public void remove() {
		PollingBlockChangeWatcher.getInstance().remove(this);
	}

}
