package bukkitdev.Rogueleader89.fortification;

import org.bukkit.World;

public class RemoveBlock implements Runnable {
	int x, y, z;
	World w;
	int bt;
	
	public RemoveBlock(int ax, int ay, int az, World world, int BlockTypeId){
		x = ax;
		y = ay;
		z = az;
		w = world;
		bt = BlockTypeId;
	}

	@Override
	public void run() {
		if(w.getBlockTypeIdAt(x, y, z) == bt){
			w.getBlockAt(x, y, z).setTypeId(0);
		}
	}
}
