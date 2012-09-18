package bukkitdev.Rogueleader89.fortification;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public class Receiver {
	
	private Location loc;
	private String band;
	
	public Receiver(Location l, String recBand)
	{
		setLocation(l);
		setBand(recBand);
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getBand() {
		return band;
	}

	public void setLocation(Location loc) {
		this.loc = loc;
	}

	public Location getLocation() {
		return loc;
	}
	
	public boolean exists()
	{
		int id = loc.getBlock().getTypeId();
		if(id == 68)
		{
			try
			{
				Sign s = (Sign)loc.getBlock();
				if((s.getLine(1).equalsIgnoreCase("[Receiver]") || s.getLine(1).equalsIgnoreCase("[Reciever]")) && s.getLine(0).equalsIgnoreCase(band))
				{
					return true;
				}
					
			}
			catch(Exception e)
			{
				//there was some issue converting a sign into a sign object, be optimistic and say it works
				return true;
			}
		}
		return false;
	}
}
