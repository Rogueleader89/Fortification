package bukkitdev.Rogueleader89.fortification;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.SubTexture;
import org.getspout.spoutapi.block.design.Texture;
//import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.massivecraft.factions.zcore.persist.Entity;

public class Sensor extends GenericCustomBlock {

	public Sensor(Plugin plugin, String name, GenericBlockDesign design, Texture tex, SubTexture subtex) {
		//plugin, block name, texture url, texture size
		super(plugin, "Sensor");
		// TODO Auto-generated constructor stub
	}
	
	public void onNeighborBlockChange(World world, int x, int y, int z, int changedId) { }
	 
    public void onBlockPlace(World world, int x, int y, int z) { }
 
    public void onBlockPlace(World world, int x, int y, int z, LivingEntity living) { }
 
    public void onBlockDestroyed(World world, int x, int y, int z) { }
 
    public boolean onBlockInteract(World world, int x, int y, int z, SpoutPlayer player) {
        return true;
    }
 
    public void onEntityMoveAt(World world, int x, int y, int z, Entity entity) { }
 
    public void onBlockClicked(World world, int x, int y, int z, SpoutPlayer player) { }
 
    public boolean isProvidingPowerTo(World world, int x, int y, int z, BlockFace face) {
        return false;
    }
 
    public boolean isIndirectlyProvidingPowerTo(World world, int x, int y, int z, BlockFace face) {
        return false;
    }

}
