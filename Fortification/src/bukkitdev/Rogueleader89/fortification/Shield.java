package bukkitdev.Rogueleader89.fortification;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class Shield 
{
	private Location pos;
	private int radius;
	private String freq;
	private Chest chest;
	private boolean powered = false;
	private Material mat;
	
	public Shield(Material shieldMaterial, Location l, int shieldRadius, String frequency, Chest c)
	{
		pos = l;
		radius = shieldRadius;
		freq = frequency;
		chest = c;
		mat = shieldMaterial;
	}
	
	public boolean verifyShield()
	{
		if(pos.getBlock().getType() == Material.WALL_SIGN)
		{
			Sign s = (Sign)pos.getBlock().getState();
			if(s.getLine(0).equalsIgnoreCase(mat.toString()) && s.getLine(1).equalsIgnoreCase("[shield]") 
					&& s.getLine(2).equalsIgnoreCase(Integer.toString(radius)) && s.getLine(3).equalsIgnoreCase(freq))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Material getMat() {
		return mat;
	}

	public void setMat(Material mat) {
		this.mat = mat;
	}

	public Location getPos() {
		return pos;
	}
	public void setPos(Location pos) {
		this.pos = pos;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public String getFrequency() {
		return freq;
	}
	public void setFrequency(String frequency) {
		this.freq = frequency;
	}

	public Chest getChest() {
		return chest;
	}

	public void setChest(Chest chest) {
		this.chest = chest;
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
	}
	
	
}
