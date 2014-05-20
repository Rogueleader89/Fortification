package bukkitdev.Rogueleader89.fortification;

import net.minecraft.server.v1_7_R3.Facing;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

//This exists since making instances of the block class and setting them equal to existing blocks apparently behaves like pointers so if those blocks are changed the saved data is changed as well,
//causing swapping things between two spots to be virtually impossible, hence this class to work around that issue 
public class saveBlock 
{

	Material id;
	byte d;
	byte initTpDir;
	BlockFace direction;
	ItemStack[] inv;
	String signLine0;
	String signLine1;
	String signLine2;
	String signLine3;
	
	//normal blocks
	public saveBlock(Material material, byte data, byte startingTpDir)
	{
		id = material;
		d = data;
		initTpDir = startingTpDir;
	}
	
	//containers
	public saveBlock(Material material, byte data, byte startingTpDir, ItemStack[] inventory)
	{
		id = material;
		d = data;
		inv = inventory;
		initTpDir = startingTpDir;
	}
	
	//signs
	public saveBlock(Material material, byte data, byte startingTpDir, String sl0, String sl1, String sl2, String sl3)
	{
		id = material;
		d = data;
		initTpDir = startingTpDir;
		signLine0 = sl0;
		signLine1 = sl1;
		signLine2 = sl2;
		signLine3 = sl3;
	}
	
	public byte getStartingTelepadDirection()
	{
		return initTpDir;
	}
	
	public String getSignLine0()
	{
		return signLine0;
	}
	public String getSignLine1()
	{
		return signLine1;
	}
	public String getSignLine2()
	{
		return signLine2;
	}
	public String getSignLine3()
	{
		return signLine3;
	}
	
	public void setId(Material type)
	{
		id = type;
	}
	
	public void setData(byte data)
	{
		d = data;
	}
	
	public void setInventory(ItemStack[] inventory)
	{
		inv = inventory;
	}
	
	public Material getType()
	{
		return id;
	}
	
	public ItemStack[] getInventory()
	{
		return inv;
	}
	
	public byte getData()
	{
		return d;
	}
	public BlockFace getDirection() 
	{
		return direction;
	}

	public void setDirection(BlockFace blockFace) 
	{
		this.direction = blockFace;
	}
}
