package bukkitdev.Rogueleader89.fortification;

import org.bukkit.Material;

//This exists since making instances of the block class and setting them equal to existing blocks apparently behaves like pointers so if those blocks are changed the saved data is changed as well,
//causing swapping things between two spots to be virtually impossible, hence this class to work around that issue 
public class saveBlock {

	Material id;
	byte d;
	
	public saveBlock(Material material, byte data)
	{
		id = material;
		d = data;
	}
	
	public void setId(Material type)
	{
		id = type;
	}
	
	public void setData(byte data)
	{
		d = data;
	}
	
	public Material getType()
	{
		return id;
	}
	
	public byte getData()
	{
		return d;
	}
}
