package bukkitdev.Rogueleader89.fortification;

import org.bukkit.Location;
import org.bukkit.Material;

public class Telepad {

	Fortification fort;
	Location pos;//sign location
	TelepadTower fLeft, bLeft, fRight, bRight;
	byte dir;//Sign direction data value
	private String sendBand;
	private String recBand;
	private int width, length, height;
	
	public Telepad(Fortification p, Location l, TelepadTower fl, TelepadTower bl, TelepadTower br, TelepadTower fr, byte data, String rBand, String sBand)
	{
		fort = p;
		pos = l;
		fLeft = fl;
		bLeft = bl;
		fRight = fr;
		bRight = br;
		dir = data;
		setRecBand(rBand);
		setSendBand(sBand);
		width = (int)fLeft.getLocation().distance(fRight.getLocation());
		length = (int)fLeft.getLocation().distance(bLeft.getLocation());
		height = (int)fLeft.getHeight();
	}
	
	public boolean checkIntegrity()
	{
		if(fLeft.checkIntegrity() && fRight.checkIntegrity() && bLeft.checkIntegrity() && bRight.checkIntegrity())
		{
			//Check to make sure the sign and the block it should be placed on actually exist.
			switch(dir)
			{
			case 0x2://+z = back
				if(!(pos.getBlock().getType().equals(Material.WALL_SIGN) && pos.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()+1).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId())))
				{
					return false;
				}
				break;
			case 0x3://-z = back
				if(!(pos.getBlock().getType().equals(Material.WALL_SIGN) && pos.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()-1).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId())))
				{
					return false;
				}
				break;
			case 0x4://+x = back
				if(!(pos.getBlock().getType().equals(Material.WALL_SIGN) && pos.getWorld().getBlockAt(pos.getBlockX()+1, pos.getBlockY(), pos.getBlockZ()).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId())))
				{
					return false;
				}
				break;
			case 0x5://-x = back
				if(!(pos.getBlock().getType().equals(Material.WALL_SIGN) && pos.getWorld().getBlockAt(pos.getBlockX()-1, pos.getBlockY(), pos.getBlockZ()).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId())))
				{
					return false;
				}
				break;
			}
			//Make sure width, length, and height are within defined bounds
			if(width <= fort.getTelepadMaxLength() && length <= fort.getTelepadMaxLength() && height <= fort.getTelepadMaxHeight())
			{
				return true;
			}
		}
		return false;
	}
	
	/*public Player[] getPlayers()
	{
		List<Player> p = fLeft.getLocation().getWorld().getPlayers();
		for(int i = 0; i < p.size(); i++)
		{
			
		}
		return null;
	}*/
	public saveBlock[][][] getBlocks()
	{
		saveBlock[][][] b = new saveBlock[length-1][width-1][height];
		Location l;
		//Block[] b = new Block[(length-2)*(width-2)*height];
		for(int i = 0; i < length-1; i++)
		{
			for(int j = 0; j < width-1; j++)
			{
				for(int k = 0; k < height; k++)
				{
					//b[i][j][k];
					//length = front to back; width = side to side;
					switch(dir)
					{
					case 0x2://+z = back, left = +x
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - j, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() + 1 + i).getLocation();
						b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData());// = l.getBlock();
						break;
					case 0x3://-z = back, left = -x
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + j, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() - 1 - i).getLocation();
						b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData());
						break;
					case 0x4://+x = back, left = -z
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + i, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() + 1 + j).getLocation();
						b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData());
						break;
					case 0x5://-x = back, left = +z
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - i, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() - 1 - j).getLocation();
						b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData());
						break;
					}
				}
			}
		}
		return b;
	}
	
	public void setBlocks(saveBlock[][][] b)
	{
		Location l;
		//Block[] b = new Block[(length-2)*(width-2)*height];
		for(int i = 0; i < length-1; i++)
		{
			for(int j = 0; j < width-1; j++)
			{
				for(int k = 0; k < height; k++)
				{
					//b[i][j][k];
					//length = front to back; width = side to side;
					switch(dir)
					{
					case 0x2://+z = back, left = +x
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - j, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() + 1 + i).getLocation();
						l.getBlock().setType(b[i][j][k].getType());
						l.getBlock().setData(b[i][j][k].getData());
						break;
					case 0x3://-z = back, left = -x
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + j, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() - 1 - i).getLocation();
						l.getBlock().setType(b[i][j][k].getType());
						l.getBlock().setData(b[i][j][k].getData());
						break;
					case 0x4://+x = back, left = -z
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + i, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() + 1 + j).getLocation();
						l.getBlock().setType(b[i][j][k].getType());
						l.getBlock().setData(b[i][j][k].getData());
						break;
					case 0x5://-x = back, left = +z
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - i, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() - 1 - j).getLocation();
						l.getBlock().setType(b[i][j][k].getType());
						l.getBlock().setData(b[i][j][k].getData());
						break;
					}
				}
			}
		}
	}
	public byte getDirection()
	{
		return dir;
	}
	public Location getLocation()
	{
		return pos;
	}
	public TelepadTower getBRTower()
	{
		return bRight;
	}
	public TelepadTower getBLTower()
	{
		return bLeft;
	}
	public TelepadTower getFRTower()
	{
		return fRight;
	}
	public TelepadTower getFLTower()
	{
		return fLeft;
	}
	public void setRecBand(String recBand) {
		this.recBand = recBand;
	}

	public String getRecBand() {
		return recBand;
	}

	public void setSendBand(String sendBand) {
		this.sendBand = sendBand;
	}

	public String getSendBand() {
		return sendBand;
	}

	public int getWidth() {
		return width;
	}
	
	public int getLength() {
		return length;
	}


	public int getHeight() {
		return height;
	}
}
