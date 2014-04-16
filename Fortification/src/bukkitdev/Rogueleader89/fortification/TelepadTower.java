package bukkitdev.Rogueleader89.fortification;

import org.bukkit.Location;

public class TelepadTower 
{
	private Fortification fort;
	private Location twr, supportA, supportB;
	private int height;
	private int supportHeight;
	
	public TelepadTower(Location tower, Location support1, Location support2, Fortification p)
	{
		fort = p;
		twr = tower;
		supportA = support1;
		supportB = support2;
		height = 0;
		supportHeight = 0;
	}
	
	public boolean checkIntegrity()
	{
		supportHeight = 0;
		height = 0;
		for(int i = 0; i < fort.getTelepadMaxHeight(); i++)
		{
			if(supportA.getWorld().getBlockAt(supportA.getBlockX(), supportA.getBlockY()+i, supportA.getBlockZ()).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId())
					&& supportB.getWorld().getBlockAt(supportB.getBlockX(), supportB.getBlockY()+i, supportB.getBlockZ()).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
			{
				supportHeight++;
			}
			if(twr.getWorld().getBlockAt(twr.getBlockX(), twr.getBlockY()+i, twr.getBlockZ()).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()))
			{
				height++;
			}
			else if(twr.getWorld().getBlockAt(twr.getBlockX(), twr.getBlockY()+i, twr.getBlockZ()).getType().toString().equalsIgnoreCase(fort.getTelepadTowerTopId()))
			{
				height++;
				if(supportHeight >= Math.floor((double)height/2.0))
				{
					return true;//this tower is good
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		return false;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Location getLocation()
	{
		return twr;
	}
	public Location getS1Location()
	{
		return supportA;
	}
	public Location getS2Location()
	{
		return supportB;
	}
}
