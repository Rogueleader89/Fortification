package bukkitdev.Rogueleader89.fortification;

//This exists since making instances of the block class and setting them equal to existing blocks apparently behaves like pointers so if those blocks are changed the saved data is changed as well,
//causing swapping things between two spots to be virtually impossible, hence this class to work around that issue 
public class saveBlock {

	int id;
	byte d;
	
	public saveBlock(int type, byte data)
	{
		id = type;
		d = data;
	}
	
	public void setId(int type)
	{
		id = type;
	}
	
	public void setData(byte data)
	{
		d = data;
	}
	
	public int getTypeId()
	{
		return id;
	}
	
	public byte getData()
	{
		return d;
	}
}
