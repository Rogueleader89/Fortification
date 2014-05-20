package bukkitdev.Rogueleader89.fortification;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;

public class Telepad 
{

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
		fLeft.checkIntegrity();
		fRight.checkIntegrity();
		bLeft.checkIntegrity();
		bRight.checkIntegrity();
		
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
			Sign s;
			//Make sure width, length, and height are within defined bounds
			if(width <= fort.getTelepadMaxLength() && length <= fort.getTelepadMaxLength() && height <= fort.getTelepadMaxHeight())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean validateSign()
	{
		Sign s;
		if(pos.getBlock().getState() instanceof Sign)
		{
			s = (Sign)pos.getBlock().getState();
			if(sendBand.equals(s.getLine(3)) && recBand.equals(s.getLine(2)))
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
	
	public boolean isSolidBlock(Material blockMat)
	{
		if(blockMat == Material.SIGN_POST || blockMat == Material.LEVER || blockMat == Material.WALL_SIGN || blockMat == Material.RAILS
				|| blockMat == Material.REDSTONE || blockMat == Material.ACTIVATOR_RAIL || blockMat == Material.DETECTOR_RAIL || blockMat == Material.POWERED_RAIL
				|| blockMat == Material.VINE || blockMat == Material.SAPLING || blockMat == Material.BROWN_MUSHROOM || blockMat == Material.RED_MUSHROOM 
				|| blockMat == Material.RED_ROSE || blockMat == Material.YELLOW_FLOWER || blockMat == Material.WHEAT || blockMat == Material.CACTUS
				|| blockMat == Material.CARROT || blockMat == Material.PAINTING || blockMat == Material.ITEM_FRAME || blockMat == Material.WALL_SIGN
				|| blockMat == Material.BED_BLOCK || blockMat == Material.CAKE_BLOCK || blockMat == Material.WATER_LILY || blockMat == Material.WOODEN_DOOR
				|| blockMat == Material.IRON_DOOR_BLOCK || blockMat == Material.TRAP_DOOR || blockMat == Material.TRIPWIRE_HOOK || blockMat == Material.TRIPWIRE
				|| blockMat == Material.SNOW || blockMat == Material.TORCH || blockMat == Material.REDSTONE_TORCH_ON || blockMat == Material.REDSTONE_TORCH_OFF
				|| blockMat == Material.DIODE_BLOCK_ON || blockMat == Material.DIODE_BLOCK_OFF || blockMat == Material.REDSTONE_COMPARATOR_OFF
				|| blockMat == Material.REDSTONE_COMPARATOR_ON || blockMat == Material.PUMPKIN_STEM || blockMat == Material.MELON_STEM
				|| blockMat == Material.LONG_GRASS || blockMat == Material.SUGAR_CANE_BLOCK || blockMat == Material.LADDER || blockMat == Material.STONE_BUTTON
				|| blockMat == Material.WOOD_BUTTON)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
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
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - j, fLeft.getLocation().getBlockY() + k,
								fLeft.getLocation().getBlockZ() + 1 + i).getLocation();
						saveComplexBlocks(l,i,j,k,b);
						if(!isSolidBlock(l.getBlock().getType()))
						{
							l.getBlock().setType(Material.AIR);
						}
						break;
					case 0x3://-z = back, left = -x
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + j, fLeft.getLocation().getBlockY() + k,
								fLeft.getLocation().getBlockZ() - 1 - i).getLocation();
						saveComplexBlocks(l,i,j,k,b);
						if(!isSolidBlock(l.getBlock().getType()))
						{
							l.getBlock().setType(Material.AIR);
						}
						break;
					case 0x4://+x = back, left = -z
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + i, fLeft.getLocation().getBlockY() + k,
								fLeft.getLocation().getBlockZ() + 1 + j).getLocation();
						saveComplexBlocks(l,i,j,k,b);
						if(!isSolidBlock(l.getBlock().getType()))
						{
							l.getBlock().setType(Material.AIR);
						}
						break;
					case 0x5://-x = back, left = +z
						l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - i, fLeft.getLocation().getBlockY() + k,
								fLeft.getLocation().getBlockZ() - 1 - j).getLocation();
						saveComplexBlocks(l,i,j,k,b);
						if(!isSolidBlock(l.getBlock().getType()))
						{
							l.getBlock().setType(Material.AIR);
						}
						break;
					}
				}
			}
		}
		return b;
	}
	
	public void saveComplexBlocks(Location l, int i, int j, int k, saveBlock[][][] b)
	{
		ItemStack[] inv;
		if(l.getBlock().getType().equals(Material.CHEST))
		{
			Chest c = (Chest)l.getBlock().getState();
			inv = c.getInventory().getContents();
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir, inv);
		}
		else if(l.getBlock().getType().equals(Material.FURNACE) || l.getBlock().getType().equals(Material.BURNING_FURNACE))
		{
			Furnace f = (Furnace)l.getBlock().getState();
			inv = f.getInventory().getContents();
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir, inv);
		}
		else if(l.getBlock().getType().equals(Material.DISPENSER))
		{
			Dispenser d = (Dispenser)l.getBlock().getState();
			inv = d.getInventory().getContents();
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir, inv);
		}
		else if(l.getBlock().getType().equals(Material.DROPPER))
		{
			Dropper d = (Dropper)l.getBlock().getState();
			inv = d.getInventory().getContents();
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir, inv);
		}
		else if(l.getBlock().getType().equals(Material.BREWING_STAND))
		{
			BrewingStand bs = (BrewingStand)l.getBlock().getState();
			inv = bs.getInventory().getContents();
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir, inv);
		}
		else if(l.getBlock().getType().equals(Material.HOPPER))
		{
			Hopper h = (Hopper)l.getBlock().getState();
			inv = h.getInventory().getContents();
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir, inv);
		}
		else if(l.getBlock().getType().equals(Material.SIGN_POST) || l.getBlock().getType().equals(Material.WALL_SIGN))
		{
			Sign s = (Sign)l.getBlock().getState();
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3));
		}
		else
		{
			b[i][j][k] = new saveBlock(l.getBlock().getType(), l.getBlock().getData(), dir);
		}
		//Save the direction the block is facing
		if(l.getBlock().getState() instanceof Directional)
		{
			Directional dir = (Directional)l.getBlock().getState();
			b[i][j][k].setDirection(dir.getFacing());
		}
	}
	
	public void setComplexBlocks(Location l, int i, int j, int k, saveBlock[][][] b)
	{
		//Make sure we aren't duplicating items...
		if(l.getBlock().getType().equals(Material.CHEST))
		{
			Chest c = (Chest)l.getBlock().getState();
			//c.getInventory().clear();//.setContents(b[i][j][k].getInventory());
			c.getBlockInventory().clear();
		}
		else if(l.getBlock().getType().equals(Material.FURNACE) || l.getBlock().getType().equals(Material.BURNING_FURNACE))
		{
			Furnace f = (Furnace)l.getBlock().getState();
			f.getInventory().clear();
		}
		else if(l.getBlock().getType().equals(Material.DISPENSER))
		{
			Dispenser d = (Dispenser)l.getBlock().getState();
			d.getInventory().clear();
		}
		else if(l.getBlock().getType().equals(Material.DROPPER))
		{
			Dropper d = (Dropper)l.getBlock().getState();
			d.getInventory().clear();
		}
		else if(l.getBlock().getType().equals(Material.BREWING_STAND))
		{
			BrewingStand bs = (BrewingStand)l.getBlock().getState();
			bs.getInventory().clear();
		}
		else if(l.getBlock().getType().equals(Material.HOPPER))
		{
			Hopper h = (Hopper)l.getBlock().getState();
			h.getInventory().clear();
		}
		
		l.getBlock().setType(b[i][j][k].getType());
		l.getBlock().setData(b[i][j][k].getData());
		//set the direction the block is facing
	//	if(l.getBlock().getState() instanceof Directional)
	//	{
	//		Directional dir = (Directional)l.getBlock().getState();
	//		dir.setFacingDirection(b[i][j][k].getDirection());
	//	}
		
		if(l.getBlock().getType().equals(Material.CHEST))
		{
			Chest c = (Chest)l.getBlock().getState();
			c.setRawData(b[i][j][k].getData());
			c.getBlockInventory().setContents(b[i][j][k].getInventory());
		}
		else if(l.getBlock().getType().equals(Material.FURNACE) || l.getBlock().getType().equals(Material.BURNING_FURNACE))
		{
			Furnace f = (Furnace)l.getBlock().getState();
			f.setRawData(b[i][j][k].getData());
			f.getInventory().setContents(b[i][j][k].getInventory());
		}
		else if(l.getBlock().getType().equals(Material.DISPENSER))
		{
			Dispenser d = (Dispenser)l.getBlock().getState();
			d.setRawData(b[i][j][k].getData());
			d.getInventory().setContents(b[i][j][k].getInventory());
		}
		else if(l.getBlock().getType().equals(Material.DROPPER))
		{
			Dropper d = (Dropper)l.getBlock().getState();
			d.setRawData(b[i][j][k].getData());
			d.getInventory().setContents(b[i][j][k].getInventory());
		}
		else if(l.getBlock().getType().equals(Material.BREWING_STAND))
		{
			BrewingStand bs = (BrewingStand)l.getBlock().getState();
			bs.setRawData(b[i][j][k].getData());
			bs.getInventory().setContents(b[i][j][k].getInventory());
		}
		else if(l.getBlock().getType().equals(Material.HOPPER))
		{
			Hopper h = (Hopper)l.getBlock().getState();
			h.setRawData(b[i][j][k].getData());
			h.getInventory().setContents(b[i][j][k].getInventory());
		}
		else if(l.getBlock().getType().equals(Material.SIGN_POST) || l.getBlock().getType().equals(Material.WALL_SIGN))
		{
			Sign s = (Sign)l.getBlock().getState();
			s.setLine(0, b[i][j][k].getSignLine0());
			s.setLine(1, b[i][j][k].getSignLine1());
			s.setLine(2, b[i][j][k].getSignLine2());
			s.setLine(3, b[i][j][k].getSignLine3());
			s.update();
		}
	}
	
	public BlockFace Rotate90Clockwise(BlockFace initBlockDir)
	{
		if(initBlockDir == BlockFace.NORTH_NORTH_EAST){return BlockFace.EAST_SOUTH_EAST;}
		if(initBlockDir == BlockFace.NORTH){           return BlockFace.EAST;}
		if(initBlockDir == BlockFace.NORTH_NORTH_WEST){return BlockFace.EAST_NORTH_EAST;}
		if(initBlockDir == BlockFace.NORTH_WEST){      return BlockFace.NORTH_EAST;}
		if(initBlockDir == BlockFace.WEST_NORTH_WEST){ return BlockFace.NORTH_NORTH_EAST;}
		if(initBlockDir == BlockFace.WEST){            return BlockFace.NORTH;}
		if(initBlockDir == BlockFace.WEST_SOUTH_WEST){ return BlockFace.NORTH_NORTH_WEST;}
		if(initBlockDir == BlockFace.SOUTH_WEST){      return BlockFace.NORTH_WEST;}
		if(initBlockDir == BlockFace.SOUTH_SOUTH_WEST){return BlockFace.WEST_NORTH_WEST;}
		if(initBlockDir == BlockFace.SOUTH){           return BlockFace.WEST;}
		if(initBlockDir == BlockFace.SOUTH_SOUTH_EAST){return BlockFace.WEST_SOUTH_WEST;}
		if(initBlockDir == BlockFace.SOUTH_EAST){      return BlockFace.SOUTH_WEST;}
		if(initBlockDir == BlockFace.EAST_SOUTH_EAST){ return BlockFace.SOUTH_SOUTH_WEST;}
		if(initBlockDir == BlockFace.EAST){            return BlockFace.SOUTH;}
		if(initBlockDir == BlockFace.EAST_NORTH_EAST){ return BlockFace.SOUTH_SOUTH_EAST;}
		if(initBlockDir == BlockFace.NORTH_EAST){      return BlockFace.SOUTH_EAST;}
		return BlockFace.NORTH;
	}
	public BlockFace Rotate90CounterClockwise(BlockFace initBlockDir)
	{
		if(initBlockDir == BlockFace.NORTH_NORTH_EAST){return BlockFace.WEST_NORTH_WEST;}
		if(initBlockDir == BlockFace.NORTH){           return BlockFace.WEST;}
		if(initBlockDir == BlockFace.NORTH_NORTH_WEST){return BlockFace.WEST_SOUTH_WEST;}
		if(initBlockDir == BlockFace.NORTH_WEST){      return BlockFace.SOUTH_EAST;}
		if(initBlockDir == BlockFace.WEST_NORTH_WEST){ return BlockFace.SOUTH_SOUTH_WEST;}
		if(initBlockDir == BlockFace.WEST){            return BlockFace.SOUTH;}
		if(initBlockDir == BlockFace.WEST_SOUTH_WEST){ return BlockFace.SOUTH_SOUTH_EAST;}
		if(initBlockDir == BlockFace.SOUTH_WEST){      return BlockFace.SOUTH_EAST;}
		if(initBlockDir == BlockFace.SOUTH_SOUTH_WEST){return BlockFace.EAST_SOUTH_EAST;}
		if(initBlockDir == BlockFace.SOUTH){           return BlockFace.EAST;}
		if(initBlockDir == BlockFace.SOUTH_SOUTH_EAST){return BlockFace.EAST_NORTH_EAST;}
		if(initBlockDir == BlockFace.SOUTH_EAST){      return BlockFace.NORTH_EAST;}
		if(initBlockDir == BlockFace.EAST_SOUTH_EAST){ return BlockFace.NORTH_NORTH_EAST;}
		if(initBlockDir == BlockFace.EAST){            return BlockFace.NORTH;}
		if(initBlockDir == BlockFace.EAST_NORTH_EAST){ return BlockFace.NORTH_NORTH_WEST;}
		if(initBlockDir == BlockFace.NORTH_EAST){      return BlockFace.NORTH_WEST;}
		return BlockFace.NORTH;
	}
	
	public byte Rotate90Clockwise(byte initBlockDir, Material bType)
	{
		if(bType ==  Material.WALL_SIGN || bType == Material.FURNACE || 
				bType == Material.CHEST || bType == Material.LADDER || 
				bType == Material.DISPENSER || bType == Material.DROPPER ||
				bType == Material.HOPPER || bType == Material.BURNING_FURNACE)
		{
			if(initBlockDir == 0x0){return 0x0;}
			if(initBlockDir == 0x1){return 0x1;}
			
			if(initBlockDir == 0x2){return 0x5;}
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x3){return 0x4;}
			if(initBlockDir == 0x5){return 0x3;}
			if(initBlockDir == 0x8){return 0x8;}
		}
		if(bType ==  Material.SIGN_POST)
		{
		if(initBlockDir == 0x9){return 0xD;}
		if(initBlockDir == 0x8){return 0xC;}
		if(initBlockDir == 0x7){return 0xB;}
		if(initBlockDir == 0x6){return 0xA;}
		if(initBlockDir == 0x5){return 0x9;}
		if(initBlockDir == 0x4){return 0x8;}
		if(initBlockDir == 0x3){return 0x7;}
		if(initBlockDir == 0x2){return 0x6;}
		if(initBlockDir == 0x1){return 0x5;}
		if(initBlockDir == 0x0){return 0x4;}
		if(initBlockDir == 0xF){return 0x3;}
		if(initBlockDir == 0xE){return 0x2;}
		if(initBlockDir == 0xD){return 0x1;}
		if(initBlockDir == 0xC){return 0x0;}
		if(initBlockDir == 0xB){return 0xF;}
		if(initBlockDir == 0xA){return 0xE;}
		}
		
		if(bType == Material.TORCH || bType == Material.REDSTONE_TORCH_ON || bType == Material.REDSTONE_TORCH_OFF)
		{
			if(initBlockDir == 0x4){return 0x1;}
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x1){return 0x3;}
		}
		
		if(bType == Material.BED_BLOCK)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x0;}
			if(initBlockDir == 0x1){return 0x2;}
		}
		
		if(bType == Material.PISTON_BASE || bType == Material.PISTON_STICKY_BASE
				|| bType == Material.PISTON_EXTENSION)
		{
			if(initBlockDir == 0x2){return 0x5;}
			if(initBlockDir == 0x3){return 0x4;}
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x5){return 0x3;}
		}
		
		if(bType == Material.COBBLESTONE_STAIRS || bType == Material.NETHER_BRICK_STAIRS ||
				bType == Material.ACACIA_STAIRS || bType == Material.QUARTZ_STAIRS ||
				bType == Material.BRICK_STAIRS || bType == Material.BIRCH_WOOD_STAIRS ||
				bType == Material.WOOD_STAIRS || bType == Material.DARK_OAK_STAIRS ||
				bType == Material.SANDSTONE_STAIRS || bType == Material.SMOOTH_STAIRS)
		{
			if(initBlockDir == 0x0){return 0x2;}
			if(initBlockDir == 0x1){return 0x3;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x3){return 0x0;}
		}
		
		if(bType == Material.WOODEN_DOOR || bType == Material.IRON_DOOR_BLOCK)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x0;}
		}
		
		if(bType == Material.RAILS || bType == Material.ACTIVATOR_RAIL 
				|| bType == Material.POWERED_RAIL || bType == Material.DETECTOR_RAIL)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x0;}
			
			if(initBlockDir == 0x2){return 0x5;}
			if(initBlockDir == 0x3){return 0x4;}
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x5){return 0x3;}
			
			if(initBlockDir == 0x6){return 0x7;}
			if(initBlockDir == 0x7){return 0x8;}
			if(initBlockDir == 0x8){return 0x9;}
			if(initBlockDir == 0x9){return 0x6;}
		}
		
		if(bType == Material.LEVER)
		{
			if(initBlockDir == 0x1){return 0x3;}
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x4){return 0x1;}
			
			if(initBlockDir == 0x5){return 0x6;}
			if(initBlockDir == 0x6){return 0x5;}
			
			if(initBlockDir == 0x7){return 0x0;}
			if(initBlockDir == 0x0){return 0x7;}
		}
		
		if(bType == Material.STONE_BUTTON || bType == Material.WOOD_BUTTON)
		{
			if(initBlockDir == 0x1){return 0x3;}
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x4){return 0x1;}
		}
		
		if(bType == Material.PUMPKIN || bType == Material.JACK_O_LANTERN ||
				bType == Material.REDSTONE_COMPARATOR_OFF || bType == Material.REDSTONE_COMPARATOR_ON 
				|| bType == Material.DIODE_BLOCK_OFF || bType == Material.DIODE_BLOCK_ON)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x0;}
		}
		
		if(bType == Material.TRAP_DOOR)
		{
			if(initBlockDir == 0x0){return 0x3;}
			if(initBlockDir == 0x1){return 0x2;}
			if(initBlockDir == 0x2){return 0x0;}
			if(initBlockDir == 0x3){return 0x1;}
		}
		
		if(bType == Material.VINE)
		{
			if(initBlockDir == 0x1){return 0x2;}
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x4){return 0x8;}
			if(initBlockDir == 0x8){return 0x2;}
		}
		
		if(bType == Material.FENCE_GATE || bType == Material.ENDER_PORTAL_FRAME ||
				bType == Material.COCOA || bType == Material.TRIPWIRE_HOOK)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x0;}
		}
		
		if(bType == Material.SKULL)
		{
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x3){return 0x5;}
			if(initBlockDir == 0x4){return 0x3;}
			if(initBlockDir == 0x5){return 0x2;}
		}
		
		if(bType == Material.ANVIL)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x0;}
		}
		return initBlockDir;
	}
	public byte Rotate90CounterClockwise(byte initBlockDir, Material bType)
	{
		if(bType ==  Material.WALL_SIGN || bType == Material.FURNACE || 
				bType == Material.CHEST || bType == Material.LADDER || 
				bType == Material.DISPENSER || bType == Material.DROPPER ||
				bType == Material.HOPPER)
		{
			if(initBlockDir == 0x0){return 0x0;}
			if(initBlockDir == 0x1){return 0x1;}
			
			if(initBlockDir == 0x5){return 0x2;}
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x4){return 0x3;}
			if(initBlockDir == 0x3){return 0x5;}
			if(initBlockDir == 0x8){return 0x8;}
		}
		if(bType ==  Material.SIGN_POST)
		{
		if(initBlockDir == 0xD){return 0x9;}
		if(initBlockDir == 0xC){return 0x8;}
		if(initBlockDir == 0xB){return 0x7;}
		if(initBlockDir == 0xA){return 0x6;}
		if(initBlockDir == 0x9){return 0x5;}
		if(initBlockDir == 0x8){return 0x4;}
		if(initBlockDir == 0x7){return 0x3;}
		if(initBlockDir == 0x6){return 0x2;}
		if(initBlockDir == 0x5){return 0x1;}
		if(initBlockDir == 0x4){return 0x0;}
		if(initBlockDir == 0x3){return 0xF;}
		if(initBlockDir == 0x2){return 0xE;}
		if(initBlockDir == 0x1){return 0xD;}
		if(initBlockDir == 0x0){return 0xC;}
		if(initBlockDir == 0xF){return 0xB;}
		if(initBlockDir == 0xE){return 0xA;}
		}
		
		if(bType == Material.TORCH || bType == Material.REDSTONE_TORCH_ON || bType == Material.REDSTONE_TORCH_OFF)
		{
			if(initBlockDir == 0x1){return 0x4;}
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x1;}
		}
		
		if(bType == Material.BED_BLOCK)
		{
			if(initBlockDir == 0x1){return 0x0;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x0){return 0x3;}
			if(initBlockDir == 0x2){return 0x1;}
		}
		
		if(bType == Material.PISTON_BASE || bType == Material.PISTON_STICKY_BASE
				|| bType == Material.PISTON_EXTENSION)
		{
			if(initBlockDir == 0x5){return 0x2;}
			if(initBlockDir == 0x4){return 0x3;}
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x3){return 0x5;}
		}
		
		if(bType == Material.COBBLESTONE_STAIRS || bType == Material.NETHER_BRICK_STAIRS ||
				bType == Material.ACACIA_STAIRS || bType == Material.QUARTZ_STAIRS ||
				bType == Material.BRICK_STAIRS || bType == Material.BIRCH_WOOD_STAIRS ||
				bType == Material.WOOD_STAIRS || bType == Material.DARK_OAK_STAIRS ||
				bType == Material.SANDSTONE_STAIRS || bType == Material.SMOOTH_STAIRS)
		{
			if(initBlockDir == 0x2){return 0x0;}
			if(initBlockDir == 0x3){return 0x1;}
			if(initBlockDir == 0x1){return 0x2;}
			if(initBlockDir == 0x0){return 0x3;}
		}
		
		if(bType == Material.WOODEN_DOOR || bType == Material.IRON_DOOR_BLOCK)
		{
			if(initBlockDir == 0x1){return 0x0;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x0){return 0x3;}
		}
		
		if(bType == Material.RAILS || bType == Material.ACTIVATOR_RAIL 
				|| bType == Material.POWERED_RAIL || bType == Material.DETECTOR_RAIL)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x0;}
			
			if(initBlockDir == 0x5){return 0x2;}
			if(initBlockDir == 0x4){return 0x3;}
			if(initBlockDir == 0x2){return 0x4;}
			if(initBlockDir == 0x3){return 0x5;}
			
			if(initBlockDir == 0x7){return 0x6;}
			if(initBlockDir == 0x8){return 0x7;}
			if(initBlockDir == 0x9){return 0x8;}
			if(initBlockDir == 0x6){return 0x9;}
		}
		
		if(bType == Material.LEVER)
		{
			if(initBlockDir == 0x3){return 0x1;}
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x1){return 0x4;}
			
			if(initBlockDir == 0x5){return 0x6;}
			if(initBlockDir == 0x6){return 0x5;}
			
			if(initBlockDir == 0x7){return 0x0;}
			if(initBlockDir == 0x0){return 0x7;}
		}
		
		if(bType == Material.STONE_BUTTON || bType == Material.WOOD_BUTTON)
		{
			if(initBlockDir == 0x3){return 0x1;}
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x1){return 0x4;}
		}
		
		if(bType == Material.PUMPKIN || bType == Material.JACK_O_LANTERN ||
				bType == Material.REDSTONE_COMPARATOR_OFF || bType == Material.REDSTONE_COMPARATOR_ON 
				|| bType == Material.DIODE_BLOCK_OFF || bType == Material.DIODE_BLOCK_ON)
		{
			if(initBlockDir == 0x1){return 0x0;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x0){return 0x3;}
		}
		
		if(bType == Material.TRAP_DOOR)
		{
			if(initBlockDir == 0x3){return 0x0;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x0){return 0x2;}
			if(initBlockDir == 0x1){return 0x3;}
		}
		
		if(bType == Material.VINE)
		{
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x8){return 0x4;}
			if(initBlockDir == 0x1){return 0x2;}
		}
		
		if(bType == Material.FENCE_GATE || bType == Material.ENDER_PORTAL_FRAME ||
				bType == Material.COCOA || bType == Material.TRIPWIRE_HOOK)
		{
			if(initBlockDir == 0x1){return 0x0;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x0){return 0x3;}
		}
		
		if(bType == Material.SKULL)
		{
			if(initBlockDir == 0x4){return 0x2;}
			if(initBlockDir == 0x5){return 0x3;}
			if(initBlockDir == 0x3){return 0x4;}
			if(initBlockDir == 0x2){return 0x5;}
		}
		
		if(bType == Material.ANVIL)
		{
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x0;}
		}
		return initBlockDir;
	}
	
	public byte Rotate180(byte initBlockDir, Material bType)
	{
		if(bType ==  Material.WALL_SIGN || bType == Material.FURNACE || 
				bType == Material.CHEST || bType == Material.LADDER || 
				bType == Material.DISPENSER || bType == Material.DROPPER ||
				bType == Material.HOPPER || bType == Material.BURNING_FURNACE)
		{
			if(initBlockDir == 0x0){return 0x0;}
			if(initBlockDir == 0x1){return 0x1;}
			
			if(initBlockDir == 0x5){return 0x4;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x4){return 0x5;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x8){return 0x8;}
		}
		if(bType ==  Material.SIGN_POST)
		{
		if(initBlockDir == 0xD){return 0x5;}
		if(initBlockDir == 0xC){return 0x4;}
		if(initBlockDir == 0xB){return 0x3;}
		if(initBlockDir == 0xA){return 0x2;}
		if(initBlockDir == 0x9){return 0x1;}
		if(initBlockDir == 0x8){return 0x0;}
		if(initBlockDir == 0x7){return 0xF;}
		if(initBlockDir == 0x6){return 0xE;}
		if(initBlockDir == 0x5){return 0xD;}
		if(initBlockDir == 0x4){return 0xC;}
		if(initBlockDir == 0x3){return 0xB;}
		if(initBlockDir == 0x2){return 0xA;}
		if(initBlockDir == 0x1){return 0x9;}
		if(initBlockDir == 0x0){return 0x8;}
		if(initBlockDir == 0xF){return 0x7;}
		if(initBlockDir == 0xE){return 0x6;}
		}
		
		if(bType == Material.TORCH || bType == Material.REDSTONE_TORCH_ON || bType == Material.REDSTONE_TORCH_OFF)
		{
			if(initBlockDir == 0x1){return 0x2;}
			if(initBlockDir == 0x4){return 0x3;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x3){return 0x4;}
		}
		
		if(bType == Material.BED_BLOCK)
		{
			if(initBlockDir == 0x1){return 0x3;}
			if(initBlockDir == 0x3){return 0x1;}
			if(initBlockDir == 0x0){return 0x2;}
			if(initBlockDir == 0x2){return 0x0;}
		}
		
		if(bType == Material.PISTON_BASE || bType == Material.PISTON_STICKY_BASE
				|| bType == Material.PISTON_EXTENSION)
		{
			if(initBlockDir == 0x5){return 0x4;}
			if(initBlockDir == 0x4){return 0x5;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x2;}
		}
		
		if(bType == Material.COBBLESTONE_STAIRS || bType == Material.NETHER_BRICK_STAIRS ||
				bType == Material.ACACIA_STAIRS || bType == Material.QUARTZ_STAIRS ||
				bType == Material.BRICK_STAIRS || bType == Material.BIRCH_WOOD_STAIRS ||
				bType == Material.WOOD_STAIRS || bType == Material.DARK_OAK_STAIRS ||
				bType == Material.SANDSTONE_STAIRS || bType == Material.SMOOTH_STAIRS)
		{
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x1){return 0x0;}
			if(initBlockDir == 0x0){return 0x1;}
		}
		
		if(bType == Material.WOODEN_DOOR || bType == Material.IRON_DOOR_BLOCK)
		{
			if(initBlockDir == 0x1){return 0x3;}
			if(initBlockDir == 0x2){return 0x0;}
			if(initBlockDir == 0x3){return 0x1;}
			if(initBlockDir == 0x0){return 0x2;}
		}
		
		if(bType == Material.RAILS || bType == Material.ACTIVATOR_RAIL 
				|| bType == Material.POWERED_RAIL || bType == Material.DETECTOR_RAIL)
		{
			if(initBlockDir == 0x0){return 0x0;}
			if(initBlockDir == 0x1){return 0x1;}
			
			if(initBlockDir == 0x5){return 0x4;}
			if(initBlockDir == 0x4){return 0x5;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x3){return 0x2;}
			
			if(bType == Material.RAILS)
			{
				if(initBlockDir == 0x7){return 0x9;}
				if(initBlockDir == 0x8){return 0x6;}
				if(initBlockDir == 0x9){return 0x7;}
				if(initBlockDir == 0x6){return 0x8;}
			}
		}
		
		if(bType == Material.LEVER)
		{
			if(initBlockDir == 0x3){return 0x4;}
			if(initBlockDir == 0x4){return 0x3;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x1){return 0x2;}
			
			if(initBlockDir == 0x5){return 0x5;}
			if(initBlockDir == 0x6){return 0x6;}
			
			if(initBlockDir == 0x7){return 0x7;}
			if(initBlockDir == 0x0){return 0x0;}
		}
		
		if(bType == Material.STONE_BUTTON || bType == Material.WOOD_BUTTON)
		{
			if(initBlockDir == 0x3){return 0x4;}
			if(initBlockDir == 0x4){return 0x3;}
			if(initBlockDir == 0x2){return 0x1;}
			if(initBlockDir == 0x1){return 0x2;}
		}
		
		if(bType == Material.PUMPKIN || bType == Material.JACK_O_LANTERN ||
				bType == Material.REDSTONE_COMPARATOR_OFF || bType == Material.REDSTONE_COMPARATOR_ON 
				|| bType == Material.DIODE_BLOCK_OFF || bType == Material.DIODE_BLOCK_ON)
		{
			if(initBlockDir == 0x1){return 0x3;}
			if(initBlockDir == 0x2){return 0x0;}
			if(initBlockDir == 0x3){return 0x1;}
			if(initBlockDir == 0x0){return 0x2;}
		}
		
		if(bType == Material.TRAP_DOOR)
		{
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
			if(initBlockDir == 0x0){return 0x1;}
			if(initBlockDir == 0x1){return 0x0;}
		}
		
		if(bType == Material.VINE)
		{
			if(initBlockDir == 0x2){return 0x8;}
			if(initBlockDir == 0x4){return 0x1;}
			if(initBlockDir == 0x8){return 0x2;}
			if(initBlockDir == 0x1){return 0x4;}
		}

		if(bType == Material.FENCE_GATE || bType == Material.ENDER_PORTAL_FRAME ||
				bType == Material.COCOA || bType == Material.TRIPWIRE_HOOK)
		{
			if(initBlockDir == 0x1){return 0x3;}
			if(initBlockDir == 0x2){return 0x0;}
			if(initBlockDir == 0x3){return 0x1;}
			if(initBlockDir == 0x0){return 0x2;}
		}
		
		if(bType == Material.SKULL)
		{
			if(initBlockDir == 0x4){return 0x5;}
			if(initBlockDir == 0x5){return 0x4;}
			if(initBlockDir == 0x3){return 0x2;}
			if(initBlockDir == 0x2){return 0x3;}
		}
		
		if(bType == Material.ANVIL)
		{
			if(initBlockDir == 0x0){return 0x0;}
			if(initBlockDir == 0x1){return 0x1;}
		}
		return initBlockDir;
	}
	
	public void setBlocks(saveBlock[][][] b)
	{
		Location l;
		//Block[] b = new Block[(length-2)*(width-2)*height];
		boolean solidBlocks = true;
		for(int w = 0; w < 2; w++)
		{
			for(int i = 0; i < length-1; i++)
			{
				for(int j = 0; j < width-1; j++)
				{
					for(int k = 0; k < height; k++)
					{
						//b[i][j][k];
						//length = front to back; width = side to side;
						byte initDir;
						Material blockMat;
						switch(dir)
						{
							case 0x2://SOUTH +z = back, left = +x
								l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - j, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() + 1 + i).getLocation();
								blockMat = b[i][j][k].getType();
								if(solidBlocks && isSolidBlock(blockMat))
								{//*/
									setComplexBlocks(l,i,j,k,b);
								}
								if(!solidBlocks && !isSolidBlock(blockMat))
								{
									setComplexBlocks(l,i,j,k,b);
								}//*/
						//		if(l.getBlock().getState().getData() instanceof Directional)
							//	{
									//This is never triggered
								//	Directional direction = (Directional)l.getBlock().getState().getData();
									initDir = b[i][j][k].getStartingTelepadDirection();
									
									switch(initDir)
									{
										case 0x2://South -- no rotation
										//	direction.setFacingDirection(b[i][j][k].getDirection());
											break;
										case 0x3://North -- 180 degrees
											l.getBlock().setData(Rotate180(b[i][j][k].getData(), blockMat));
										//	direction.setFacingDirection(b[i][j][k].getDirection().getOppositeFace());
											break;
										case 0x4://East -- 90 degrees counterclockwise
											l.getBlock().setData(Rotate90Clockwise(b[i][j][k].getData(), blockMat));
										//	direction.setFacingDirection(Rotate90CounterClockwise(b[i][i][k].getDirection()));
											break;
										case 0x5://West -- 90 degrees clockwise
											l.getBlock().setData(Rotate90CounterClockwise(b[i][j][k].getData(), blockMat));
										//	direction.setFacingDirection(Rotate90Clockwise(b[i][i][k].getDirection()));
											break;
									}
						//		}
								break;
							case 0x3://NORTH -z = back, left = -x
								l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + j, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() - 1 - i).getLocation();
								blockMat = b[i][j][k].getType();
								if(solidBlocks && isSolidBlock(blockMat))
								{//*/
									setComplexBlocks(l,i,j,k,b);
								}
								if(!solidBlocks && !isSolidBlock(blockMat))
								{
									setComplexBlocks(l,i,j,k,b);
								}//*/
					//			if(l.getBlock().getState().getData() instanceof Directional)
						//		{
					//				Directional direction = (Directional)l.getBlock().getState().getData();
									initDir = b[i][j][k].getStartingTelepadDirection();
									switch(initDir)
									{
										case 0x2://South -- 180
											l.getBlock().setData(Rotate180(b[i][j][k].getData(), blockMat));
						//					direction.setFacingDirection(b[i][j][k].getDirection().getOppositeFace());
											break;
										case 0x3://North -- 0
							//				direction.setFacingDirection(b[i][j][k].getDirection());
											break;
										case 0x4://East -- 90 degrees clockwise
											l.getBlock().setData(Rotate90CounterClockwise(b[i][j][k].getData(), blockMat));
						//					direction.setFacingDirection(Rotate90Clockwise(b[i][i][k].getDirection()));
											break;
										case 0x5://West -- 90 degrees counterclockwise
											l.getBlock().setData(Rotate90Clockwise(b[i][j][k].getData(), blockMat));
							//				direction.setFacingDirection(Rotate90CounterClockwise(b[i][i][k].getDirection()));
											break;
									}
							//	}
								break;
							case 0x4://EAST +x = back, left = -z
								l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() + 1 + i, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() + 1 + j).getLocation();
								blockMat = b[i][j][k].getType();
								if(solidBlocks && isSolidBlock(blockMat))
								{//*/
									setComplexBlocks(l,i,j,k,b);
								}
								if(!solidBlocks && !isSolidBlock(blockMat))
								{
									setComplexBlocks(l,i,j,k,b);
								}//*/
					//			if(l.getBlock().getState().getData() instanceof Directional)
						//		{
						//			Directional direction = (Directional)l.getBlock().getState().getData();
									initDir = b[i][j][k].getStartingTelepadDirection();
									switch(initDir)
									{
										case 0x2://South -- 90c
											l.getBlock().setData(Rotate90CounterClockwise(b[i][j][k].getData(), blockMat));
							//				direction.setFacingDirection(Rotate90Clockwise(b[i][i][k].getDirection()));
											break;
										case 0x3://North -- 90cc
											l.getBlock().setData(Rotate90Clockwise(b[i][j][k].getData(), blockMat));
								//			direction.setFacingDirection(Rotate90CounterClockwise(b[i][i][k].getDirection()));
											break;
										case 0x4://East -- 0
							//				direction.setFacingDirection(b[i][j][k].getDirection());
											break;
										case 0x5://West -- 180
											l.getBlock().setData(Rotate180(b[i][j][k].getData(), blockMat));
							//				direction.setFacingDirection(b[i][j][k].getDirection().getOppositeFace());
											break;
									}
						//		}
								break;
							case 0x5://WEST -x = back, left = +z
								l = fLeft.getLocation().getWorld().getBlockAt(fLeft.getLocation().getBlockX() - 1 - i, fLeft.getLocation().getBlockY() + k, fLeft.getLocation().getBlockZ() - 1 - j).getLocation();
								blockMat = b[i][j][k].getType();
								if(solidBlocks && isSolidBlock(blockMat))
								{//*/
									setComplexBlocks(l,i,j,k,b);
								}
								if(!solidBlocks && !isSolidBlock(blockMat))
								{
									setComplexBlocks(l,i,j,k,b);
								}//*/
						//		if(l.getBlock().getState().getData() instanceof Directional)
							//	{
					//				Directional direction = (Directional)l.getBlock().getState().getData();
									initDir = b[i][j][k].getStartingTelepadDirection();
									switch(initDir)
									{
										case 0x2://South -- 90cc
											l.getBlock().setData(Rotate90Clockwise(b[i][j][k].getData(), blockMat));
						//					direction.setFacingDirection(Rotate90CounterClockwise(b[i][i][k].getDirection()));
											break;
										case 0x3://North -- 90c
											l.getBlock().setData(Rotate90CounterClockwise(b[i][j][k].getData(), blockMat));
						//					direction.setFacingDirection(Rotate90Clockwise(b[i][i][k].getDirection()));
											break;
										case 0x4://East -- 180
											l.getBlock().setData(Rotate180(b[i][j][k].getData(), blockMat));
							//				direction.setFacingDirection(b[i][j][k].getDirection().getOppositeFace());
											break;
										case 0x5://West -- 0
							//				direction.setFacingDirection(b[i][j][k].getDirection());
											break;
									}
						//		}
								break;
						}
					}
				}
			}//end inner for loops
			solidBlocks = false;
		}//end outer for loop
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
	public void setRecBand(String recBand) 
	{
		this.recBand = recBand;
	}

	public String getRecBand() 
	{
		return recBand;
	}

	public void setSendBand(String sendBand) 
	{
		this.sendBand = sendBand;
	}

	public String getSendBand() 
	{
		return sendBand;
	}

	public int getWidth() 
	{
		return width;
	}
	
	public int getLength() 
	{
		return length;
	}


	public int getHeight() 
	{
		return height;
	}
}
