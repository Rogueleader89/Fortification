package bukkitdev.Rogueleader89.fortification;

import org.bukkit.World;


public class LeverOff implements Runnable {
	int x, y, z;
	World w;
	
	public LeverOff(int ax, int ay, int az, World world){
		x = ax;
		y = ay;
		z = az;
		w = world;
	}

	@Override
	public void run() {
		int d = w.getBlockAt(x, y, z).getData();
		int nd = d & 0x7;
		if(nd != d){
			w.getBlockAt(x, y, z).setData((byte) nd);
		}
	}
}
