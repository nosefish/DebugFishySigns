package net.gmx.nosefish.debugfishysigns;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.gmx.nosefish.fishylib.blocks.BlockInfo;
import net.gmx.nosefish.fishylib.worldmath.FishyDirection;
import net.gmx.nosefish.fishylib.worldmath.FishyLocationInt;
import net.gmx.nosefish.fishylib.worldmath.FishyVectorInt;
import net.gmx.nosefish.fishysigns.Log;
import net.gmx.nosefish.fishysigns.activator.Activator;
import net.gmx.nosefish.fishysigns.activator.ActivatorRedstone;
import net.gmx.nosefish.fishysigns.activator.ImmutableRedstoneChange;
import net.gmx.nosefish.fishysigns.annotation.FishySignIdentifier;
import net.gmx.nosefish.fishysigns.plugin.engine.UnloadedSign;
import net.gmx.nosefish.fishysigns.signs.FishySign;
import net.gmx.nosefish.fishysigns.task.FishyTask;
import net.gmx.nosefish.fishysigns.watcher.RedstoneChangeWatcher;
import net.gmx.nosefish.fishysigns.world.ImmutableBlockState;

public class RedstoneTestSign extends FishySign {
	@FishySignIdentifier
	public static final Pattern[] regEx = {null, Pattern.compile("FishyRSTest"), null, null};
	private static FishyVectorInt[] offsets = {
		new FishyVectorInt(1, 0, 0),
		new FishyVectorInt(-1, 0, 0),
		new FishyVectorInt(0, 0, 1),
		new FishyVectorInt(0, 0, -1)};
	
	public RedstoneTestSign(UnloadedSign sign) {
		super(sign);
	}
	
	@Override
	public void activate(Activator activator) {
		if (activator instanceof ActivatorRedstone) {
			Log.get().logInfo("RedstoneTestSign activated by ActivatorRedstone, "
					+ ((ActivatorRedstone)activator).getChanges().size() 
					+ " changes detected.");
			FishyTask task = new OnActivationTask((ActivatorRedstone)activator);
			task.submit();
		}
	}

	@Override
	public void initialize() {
		for (FishyVectorInt offset : offsets) {
			FishyLocationInt loc = this.location.addIntVector(offset);
			RedstoneChangeWatcher.getInstance().register(this.getID(), loc);
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
		RedstoneChangeWatcher.getInstance().remove(this.getID());
	}
	
	/**
	 * Runs asynchronously in the server thread
	 * 
	 * @author Stefan Steinheimer (nosefish)
	 *
	 */
	private class OnActivationTask extends FishyTask {
		final List<ImmutableRedstoneChange> changes;
		
		public OnActivationTask(ActivatorRedstone activator) {
			changes = activator.getChanges();
		}
		
		@Override
		public void doStuff() {
			Log.get().logInfo("RedstoneTestSign: doStuff");

			for (ImmutableRedstoneChange change:changes) {
				Log.get().logInfo("RedstoneTestSign: check if loaded");
				// is this still loaded?
				World world = change.getLocation().getWorld().getWorldIfLoaded();
				int x = change.getLocation().getIntX();
				int y = change.getLocation().getIntY();
				int z = change.getLocation().getIntZ();
				if (world == null || ! world.isChunkLoaded(x, y, z)) {
					continue;
				}
				Log.get().logInfo("RedstoneTestSign: block is loaded");
				// is this an input?
				// (of course it is, but we have to figure out how to do this)
				FishyVectorInt inputBlockOffsetVector = 
						new FishyVectorInt(change.getLocation().getVectorTo(getLocation()));
				boolean isInputBlock = Arrays.asList(offsets).contains(inputBlockOffsetVector);
				if (isInputBlock) {
					Log.get().logInfo("RedstoneTestSign: input block changed");
				} else {
					Log.get().logInfo("RedstoneTestSign: not an input block");
					return;
				}
				//TODO: test!
				// is the input powering the sign?
				if (isDirectInput(change.getLocation(), change.getBlockState(), getLocation())) {
					Log.get().logInfo("RedstoneTestSign: confirmed as direct input");
				} else {
					Log.get().logInfo("RedstoneTestSign: not a direct input");
				}
			}
		}
		
		public boolean isDirectInput(FishyLocationInt inputLocation,
				ImmutableBlockState inputBlockState,
				FishyLocationInt target) {
			
			if (! inputLocation.getWorld().equals(target.getWorld())) {
				// Are you kidding? They're not even in the same world!
				System.out.println("different world");
				return false;
			}
			short blockTypeID = inputBlockState.getTypeId();
			short blockData = inputBlockState.getData();
			FishyVectorInt in2target = new FishyVectorInt(inputLocation.getVectorTo(target));
			if (in2target.lengthSquared() != 1.0D) {
				// Not adjacent. What a waste of time.
				System.out.println("not adjacent");
				return false;
			}
//			if ((in2target.getIntX() == 0 && in2target.getIntY() == 0) || in2target.getIntY() != 0) {
//				// zero length in XZ plane, or blocks are on different Y levels
//				System.out.println("zero length or wrong level");
//				return false;
//			}
			FishyDirection connectionDirection = in2target.getCardinalDirection();
			if (connectionDirection == FishyDirection.ERROR) {
				// invalid direction
				System.out.println("invalid direction");
				return false;
			}
			if (blockTypeID == BlockType.RedstoneWire.getId()) {
				System.out.println("is redstone wire");
				Set<FishyDirection> directions = BlockInfo.getRedstoneWireConnections(inputLocation);
				return (directions == null) ? false : directions.contains(connectionDirection);
			}
			System.out.println("not redstone wire");
			return BlockInfo.isRedstoneConnector(blockTypeID, blockData, connectionDirection);
		}
	} // end of internal class

}
