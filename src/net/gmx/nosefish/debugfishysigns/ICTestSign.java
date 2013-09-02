package net.gmx.nosefish.debugfishysigns;

import java.util.regex.Pattern;

import net.gmx.nosefish.fishysigns.Log;
import net.gmx.nosefish.fishysigns.annotation.FishySignIdentifier;
import net.gmx.nosefish.fishysigns.plugin.engine.UnloadedSign;
import net.gmx.nosefish.fishysigns.signs.FishyICSign;
import net.gmx.nosefish.fishysigns.signs.plumbing.FishySignSignal;

public class ICTestSign extends FishyICSign {
	@FishySignIdentifier
	public static final Pattern[] regEx = {null, Pattern.compile("FishyICTest"), null, null};
	
	public ICTestSign(UnloadedSign sign) {
		super(sign);
	}

	@Override
	protected void onRedstoneInputChange(FishySignSignal oldS,
			FishySignSignal newS) {
		Log.get().logInfo("FishyICSign: " + newS);
		this.outputBox.updateOutput(newS);
	}

}
