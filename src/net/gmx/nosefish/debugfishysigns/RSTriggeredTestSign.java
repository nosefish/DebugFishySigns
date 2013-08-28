package net.gmx.nosefish.debugfishysigns;

import java.util.regex.Pattern;

import net.gmx.nosefish.fishysigns.Log;
import net.gmx.nosefish.fishysigns.annotation.FishySignIdentifier;
import net.gmx.nosefish.fishysigns.plugin.engine.UnloadedSign;
import net.gmx.nosefish.fishysigns.signs.FishySignInput;
import net.gmx.nosefish.fishysigns.signs.RedstoneTriggeredFishySign;

public class RSTriggeredTestSign extends RedstoneTriggeredFishySign {
	@FishySignIdentifier
	public static final Pattern[] regEx = {null, Pattern.compile("RSTriggered"), null, null};
	
	public RSTriggeredTestSign(UnloadedSign sign) {
		super(sign);
	}
	
	@Override
	protected void onRedstoneInputChange(FishySignInput oldInput,
			FishySignInput newInput) {
		Log.get().logInfo("RSTriggeredTestSign.onRedstoneInputChange called");
	}

	@Override
	public boolean validateOnLoad() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean validateOnCreate(String playerName) {
		// TODO Auto-generated method stub
		Log.get().logInfo("RSTriggeredTestSign.validateOnCreate called");
		return true;
	}

}
