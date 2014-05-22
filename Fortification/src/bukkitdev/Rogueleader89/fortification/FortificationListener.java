package bukkitdev.Rogueleader89.fortification;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;





//import com.massivecraft.factions.FPlayer;
//import com.massivecraft.factions.FPlayers;
//import com.massivecraft.factions.Faction;
//import com.massivecraft.factions.Factions;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;


/*
 * 1.0 Release:
 * Emitters for various effects, blindness, slow digging, limited building/destruction, healing
 * Re-examine and possibly finish implementation of send signs.
 * Bug fix everything, optimize code where needed.
 * Add mob sensors + filters (hostile mob vs non-hostile, mobdetect/ignore).
 * Individual settings per mechanism - Range, direction, radius, filters (if applicable)
 * Send packet to client to show arrows when they are shot from arrow turrets
 * Add forcefield type shields and other such things (with necessary weaknesses, possibly only deflect certain amounts of stuff?)
 * Support display names of items as well as material types???
 * 
 * Other:
 * Add lifts in some form (preferably elevators as a vehicle of sorts).
 * 
 * Prevent a single message sign from spamming a player constantly with text.
 * This also applies to factionalert signs, possibly delay between messages?
 * combine factionignore and factionalert signs?
 * 
 * Tractorbeam - pulls blocks/players towards it on redstone change
 * Repulser - pushes blocks/players away from it on redstone change
 * 
 * Add command that allows player to mute all messages from message signs.
 * 
 * Create new type of send sign that exchanges/flips text between two signs (so s1 becomes s2 and s2 becomes s1)
 * 
 * Add ability to move strings from one line of a sign to a different line on another sign (s1l1 to s2l4 for instance [Send N3] would send
 * the string on the line the send sign is on to line 3 on a sign to the north.
 * 
 * Block Destruction Sensor, Block Placement Sensor
 * 
 * Add optional fuel costs to turrets
 * 
 * [rSensor] detects in radius around it instead of in a straight line
 * 
 * shield signs to increase hardness/durability of blocks around it (not possible atm).
 * different types of shields are possible here. Bubbleshield could hold back water similar to the bubble spell.
 * Endurance shields could replace destroyed blocks around them with blocks from a chest located on the block the shield sign is on.
 * 
 * advanced sensors that can send name of detected player to another sign (could have it be l1 = name; l2 = [Sensor]; l3 = name of player detected)
 * 
 * Math signs, allow user to add/subtract/divide/multiply numbers on lines 1 and 3, return result on line 4.
 * 
 * Boolean signs, allow user to check a true/false value, if true turn on redstone, if false turn off (similar to string comparison
 * but can use number symbols like '>', '<', '<=', '>=' in conjunction with math signs)
 * 
 * Ammunition/item use system for turrets/shields.
 * 
 * String transmitter/reciever, l2 = [sTransmitter], l2 = [sReciever]; l3 & l4 are transmitted/recieved, l1 = bandwidth (name of rec or rec to send to)
 * 
 * Rotator block - rotates blocks attached to the top of it 90 degrees. clockwise and counterclockwise settings.
 * 
 * Turret types to add...
 * Fire Arrows - Light any blocks they hit on fire.
 * Artillery - calculate ballistic trajectory based on a set angle and launch speed, fire tnt along trajectory. May be possible in bukkit using
 * new ability to tell where arrows/snowballs land and just making an explosion there.
 * warp - teleport the player to a new position (defined by sign placement? similar to transmitter/reciever?)
 * push - Pushes player back away from the turret
 * pull - pulls player towards the turret
 * freeze - freezes a player in place temporarily, freezes water below firing area? Creates snow as well? (make last 2 optional.)
 * item grabber - Take item player is holding and stores in chest attached to turret sign (or an item of id defined by user? Various filters)
 * 4 way turrets, place block ontop of turret block, fires out in all 4 directions (or even 8 if arrows are used).
 * Floor traps - turrets that come up from the floor and fire once when redstone triggered.
 */
public class FortificationListener implements Listener 
{
		private Fortification fort;
	//	private PropertiesFile properties = new PropertiesFile("fortification.properties");
		private String arrowturretblockId;
		private int flamelength;
		private String flameturretblockId;
		private int weblength;
		private String webturretblockId;
		private int webTime;
		private int maxtraplength;
		private int sendlength;
		private String[] trapblocks;
		private boolean replacetrap;
		private boolean sendremovetext;
		private boolean commandsend;
		private boolean sendoverwrite;
		private boolean sendoverwritescommands;
		private Plugin towny;
		protected static final Logger log = Logger.getLogger("Minecraft");
		
//		private List<Player> frozenPlayers;
//		private List<Integer> frozenTime;

		// This controls the accessibility of functions / variables from the main class.
		public FortificationListener(Fortification plugin) {
			fort = plugin;
			flamelength = fort.getFlamelength();
			flameturretblockId = fort.getFlameturretblockId();
			maxtraplength = fort.getMaxtraplength();
			sendlength = fort.getSendlength();
			trapblocks = fort.getTrapblocks();
			replacetrap = fort.isReplacetrap();
			sendremovetext = fort.isSendremovetext();
			commandsend = fort.isCommandsend();
			sendoverwrite = fort.isSendoverwrite();
			sendoverwritescommands = fort.isSendoverwritescommands();
			weblength = fort.getWeblength();
			webturretblockId = fort.getWebturretblockId();
			webTime = fort.getWebtime();
			arrowturretblockId = fort.getArrowturretId();
			if(fort.isTownyEnabled())
			{
				towny = fort.getServer().getPluginManager().getPlugin("Towny");
			}
		}
		
		public void removeReceiver(Sign s)
		{
			if(s.getLine(1).equalsIgnoreCase("[receiver]") || s.getLine(1).equalsIgnoreCase("[reciever]"))
			{
				for(int i = 0; i < fort.getReceiverList().size(); i++)
				{
					if(fort.getReceiverList().get(i).getBand().equals(s.getLine(0)))
					{
						if(fort.getReceiverList().get(i).getLocation().equals(s.getLocation()))
						{
							fort.getReceiverList().remove(i);
							return;
						}
					}
				}
			}
		}
		
		public void removeTelepad(Sign s)
		{
			if(s.getLine(1).equalsIgnoreCase("[telepad]"))
			{
				for(int i = 0; i < fort.getPadList().size(); i++)
				{
					//if(fort.getPadList().get(i).getRecBand().equals(s.getLine(3)))
					//{
						if(fort.getPadList().get(i).getLocation().equals(s.getLocation()))
						{
							fort.getPadList().remove(i);
							return;
						}
					//}
				}
			}
		}
		
	//	@EventHandler
	//	public void BlockPhysicsEvent(BlockPhysicsEvent e)
	//	{
	//		//Check if a sign was attached to a block that got destroyed here...
	//	}
		
		@EventHandler
		public void onEntityExplode(EntityExplodeEvent e)
		{
			List<Block> destroyedBlocks = e.blockList();
			for(int j = 0; j < e.blockList().size(); j++)
			{
				Location l = destroyedBlocks.get(j).getLocation();
				for(int i = 0; i < fort.getShieldMaterials().length; i++)
				{
					if(destroyedBlocks.get(j).getType().equals(fort.getShieldMaterials()[i]))
					{
						//check if the block is a part of a shield...
						for(int k = 0; k < fort.getShieldList().size(); k++)
						{
							int radius = fort.getShieldList().get(k).getRadius();
							//if((x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) <= rSquared 
							//&& (x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) > (radius-1) * (radius-1)) 
							double dist = l.distance(fort.getShieldList().get(k).getPos());
							if(dist <= radius && dist > radius-1)
							{
								if(fort.getShieldList().get(k).isPowered() && fort.getShieldList().get(k).getMat().equals(destroyedBlocks.get(j).getType()))
								{
									if(fort.getShieldList().get(k).getChest().getInventory().contains(destroyedBlocks.get(j).getType()))
	                    			{
										fort.getShieldList().get(k).getChest().getInventory().removeItem(new ItemStack(destroyedBlocks.get(j).getType(), 1));
										fort.getShieldList().get(k).getChest().update(true);
										e.setCancelled(true);
										return;
	                    			}
								}
							}
						}
					//	if(fort.getShieldList().get(i))
					}
				}
				
				for(int k = 0; k < fort.getShieldList().size(); k++)
				{
					int radius = fort.getShieldList().get(k).getRadius();
					//if((x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) <= rSquared 
					//&& (x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) > (radius-1) * (radius-1)) 
					double dist = l.distance(fort.getShieldList().get(k).getPos());
					if(dist <= radius && dist > radius-1)
					{
						if(fort.getShieldList().get(k).isPowered())
						{
							if(fort.getShieldList().get(k).getChest().getInventory().contains(fort.getShieldList().get(k).getMat()))
	            			{
								fort.getShieldList().get(k).getChest().getInventory().removeItem(new ItemStack(fort.getShieldList().get(k).getMat(), 1));
								fort.getShieldList().get(k).getChest().update(true);
								e.setCancelled(true);
								l.getBlock().setType(fort.getShieldList().get(k).getMat());
								return;
	            			}
						}
					}
				}
			}
		}
		
		@EventHandler
		public void onBlockBreak(BlockBreakEvent e)
		{
			Location l = e.getBlock().getLocation();
			for(int i = 0; i < fort.getShieldMaterials().length; i++)
			{
				if(e.getBlock().getType().equals(fort.getShieldMaterials()[i]))
				{
					//check if the block is a part of a shield...
					for(int k = 0; k < fort.getShieldList().size(); k++)
					{
						int radius = fort.getShieldList().get(k).getRadius();
						//if((x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) <= rSquared 
						//&& (x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) > (radius-1) * (radius-1)) 
						double dist = l.distance(fort.getShieldList().get(k).getPos());
						if(dist <= radius && dist > radius-1)
						{
							if(fort.getShieldList().get(k).isPowered() && fort.getShieldList().get(k).getMat().equals(e.getBlock().getType()))
							{
								if(fort.getShieldList().get(k).getChest().getInventory().contains(e.getBlock().getType()))
                    			{
									fort.getShieldList().get(k).getChest().getInventory().removeItem(new ItemStack(e.getBlock().getType(), 1));
									fort.getShieldList().get(k).getChest().update(true);
									e.setCancelled(true);
									return;
                    			}
							}
						}
					}
				//	if(fort.getShieldList().get(i))
				}
			}
			
			for(int k = 0; k < fort.getShieldList().size(); k++)
			{
				int radius = fort.getShieldList().get(k).getRadius();
				//if((x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) <= rSquared 
				//&& (x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) > (radius-1) * (radius-1)) 
				double dist = l.distance(fort.getShieldList().get(k).getPos());
				if(dist <= radius && dist > radius-1)
				{
					if(fort.getShieldList().get(k).isPowered())
					{
						if(fort.getShieldList().get(k).getChest().getInventory().contains(fort.getShieldList().get(k).getMat()))
            			{
							fort.getShieldList().get(k).getChest().getInventory().removeItem(new ItemStack(fort.getShieldList().get(k).getMat(), 1));
							fort.getShieldList().get(k).getChest().update(true);
							e.setCancelled(true);
							l.getBlock().setType(fort.getShieldList().get(k).getMat());
							return;
            			}
					}
				}
			}
			
			//TODO: Fix error here, telepads aren't being deleted properly, transmitters likely have a similar, less noticable, issue. Also console errors out trying to cast to sign..
			//if its a sign this is really easy
			if(e.getBlock().getType().equals(Material.WALL_SIGN) && e.getBlock() instanceof Sign)
			{
				removeReceiver((Sign)e.getBlock());
				removeTelepad((Sign)e.getBlock());
			}
			//see if a sign was attached to whatever was broken
	/*		if(e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()-1).getType().equals(Material.WALL_SIGN))
			{
				//Need to check if this block's material is an instance of wall sign before converting the block first.
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()-1);
				if(((Block)s).getData() == 0x2)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			if(e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()+1).getType().equals(Material.WALL_SIGN))
			{
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()+1);
				if(((Block)s).getData() == 0x3)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			if(e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX()-1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()).getType().equals(Material.WALL_SIGN))
			{
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX()-1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ());
				if(((Block)s).getData() == 0x4)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			if(e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX()+1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()).getType().equals(Material.WALL_SIGN))
			{
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX()+1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ());
				if(((Block)s).getData() == 0x5)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			e.getBlock().getLocation();*/
		}
		
		
		@EventHandler
		public void onBlockRedstoneChange(BlockRedstoneEvent e){
			Block block = e.getBlock();
			int oldlevel = e.getOldCurrent(); 
			int newlevel = e.getNewCurrent();
			boolean rold = oldlevel >= 1;
			boolean rnew = newlevel >= 1;
				int mx = block.getX();
				int my = block.getY();
				int mz = block.getZ();

				try{
				//63 = sign post, 68 = wall sign
			if(rnew != rold){
				if(e.getBlock().getWorld().getBlockAt(mx, my, mz + 1).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx, my, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx, my + 1, mz + 1).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx, my+1, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx, my - 1, mz + 1).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx, my-1, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx, my, mz - 1).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx, my, mz-1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx, my + 1, mz - 1).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx, my+1, mz-1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx, my - 1, mz - 1).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx, my-1, mz-1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx - 1, my, mz).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx-1, my, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx - 1, my + 1, mz).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx-1, my+1, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx - 1, my - 1, mz).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx-1, my-1, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx + 1, my, mz).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx+1, my, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx + 1, my + 1, mz).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx+1, my+1, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockAt(mx + 1, my - 1, mz).getType().equals(Material.WALL_SIGN))
				{
					handleinput(mx+1, my-1, mz, rnew, e.getBlock().getWorld());
				}
			}
				}
			catch(Exception ex){
				log.info(ex.getMessage());
			}
		}
		
		/* Old found player from when sensors ran out of OnRedstoneChange
		//Sensor found a player, check this player's data against sensor requirements
		public boolean foundplayer(List<Player> p, int x, int y, int z, String l1, String l2, String l3, String l4, int i){
			//Only detect person on line 3
			if(l1.equalsIgnoreCase("singledetect"))
			{
				if(p.get(i).getName().equalsIgnoreCase(l3)){
					return true;
				}
			}
			//Only detect people on lines 3 and 4
			else if(l1.equalsIgnoreCase("doubledetect")){
				if(p.get(i).getName().equalsIgnoreCase(l3) || p.get(i).getName().equalsIgnoreCase(l4)){
					return true;
				}
			}
			//only detect group on line 3 + groups that inherit that group
			else if(l1.equalsIgnoreCase("groupdetect")){
				String[] pgroups = p.get(i).getGroups();
				for(int j = 0; j < pgroups.length; j++){
					if(pgroups[j].equalsIgnoreCase(l3)){
						return true;
					}
				}
			}
			//only detect groups on lines 3 and 4 + groups that inherit those groups
			else if(l1.equalsIgnoreCase("dgroupdetect")){
				String[] pgroups = p.get(i).getGroups();
				for(int j = 0; j < pgroups.length; j++){
					if(pgroups[j].equalsIgnoreCase(l3) || pgroups[j].equalsIgnoreCase(l4)){
						return true;
					}
				}
			}
			//detect everyone except the person listed on line 3
			else if(l1.equalsIgnoreCase("singleignore")){
				if(!p.get(i).getName().equalsIgnoreCase(l3)){
					return true;
				}
			}
			//detect everyone except the two people listed on lines 3 and 4
			else if(l1.equalsIgnoreCase("doubleignore")){
				if(!p.get(i).getName().equalsIgnoreCase(l3) && !p.get(i).getName().equalsIgnoreCase(l4)){
					return true;
				}
			}
			//detect everyone except people in the group on line 3 and those in groups that inherit said group
			else if(l1.equalsIgnoreCase("groupignore")){
				boolean ingroup = false;
				String[] pgroups = p.get(i).getGroups();
				for(int j = 0; j < pgroups.length; j++){
					if(pgroups[j].equalsIgnoreCase(l3)){
						ingroup = true;
						break;
					}
				}
				if(ingroup == false){
					return true;
				}
			}
			//detect everyone except the people in the groups on lines 3 and 4, and those who inherit said groups
			else if(l1.equalsIgnoreCase("dgroupignore")){
				boolean ingroup = false;
				String[] pgroups = p.get(i).getGroups();
				for(int j = 0; j < pgroups.length; j++){
					if(pgroups[j].equalsIgnoreCase(l3) || pgroups[j].equalsIgnoreCase(l4)){
						ingroup = true;
						break;
					}
				}
				if(ingroup == false){
					return true;
				}
			}
			//detect everyone
			else if(l1.equalsIgnoreCase("") || l1 == null || l1.equalsIgnoreCase(" ") || l1.equalsIgnoreCase("default")){
				return true;
			}
			//Nobody matching sensor requirements found
			else{
				return false;
			}
			//return false if something odd happens
			return false;
		}
		*/
		
		public float getRelativeRotation(byte initDir, byte newDir, float yaw)
		{	
			switch(initDir)
			{
				case 0x2://North
					switch(newDir)
					{
						case 0x2:
							return yaw;
						case 0x3:
							return yaw + 180f;
						case 0x4:
							return yaw - 90f;
						case 0x5:
							return yaw + 90f;
					}
					break;
				case 0x3://South
					switch(newDir)
					{
						case 0x2:
							return yaw + 180f;
						case 0x3:
							return yaw;
						case 0x4:
							return yaw + 90f;
						case 0x5:
							return yaw - 90f;
					}
					break;
				case 0x4://West
					switch(newDir)
					{
						case 0x2:
							return yaw + 90;
						case 0x3:
							return yaw - 90;
						case 0x4:
							return yaw;
						case 0x5:
							return yaw + 180f;
					}
					break;
				case 0x5://East
					switch(newDir)
					{
						case 0x2:
							return yaw - 90f;
						case 0x3:
							return yaw + 90f;
						case 0x4:
							return yaw + 180f;
						case 0x5:
							return yaw;
					}
					break;
			}
			return yaw;
		}
		
		public Location getRelativeLocation(double back, double right, double up, byte dir, Location l)
		{
			switch(dir)
			{
				case 0x2://+z = back, left = +x -- North
					return new Location(l.getWorld(), l.getX() - right, l.getY() + up, l.getZ() + back);
				case 0x3://-z = back, left = -x -- South
					return new Location(l.getWorld(), l.getX() + right, l.getY() + up, l.getZ() - back);
				case 0x4://+x = back, left = -z -- West
					return new Location(l.getWorld(), l.getX() + back, l.getY() + up, l.getZ() + right);
				case 0x5://-x = back, left = +z -- East
					return new Location(l.getWorld(), l.getX() - back, l.getY() + up, l.getZ() - right);
			}
			return l;//<-- should never be reached
		}
		
		public void handleinput(int x, int y, int z, boolean powered, World w)
		{
			BlockState b = w.getBlockAt(x, y, z).getState();
			if(!(b instanceof Sign)){
				return;
			}
			Sign sign = (Sign)b;
			String l1 = sign.getLine(0);
			String l2 = sign.getLine(1);
			String l3 = sign.getLine(2);
			String l4 = sign.getLine(3);
			
				///////////
				//Shields//
				///////////
				if(l2.equalsIgnoreCase("[shield]") && !l1.equalsIgnoreCase("teleblock") && !l1.equalsIgnoreCase("chest"))
				{
					for(int i = 0; i < fort.getShieldMaterials().length; i++)
					{
						if(l1.equalsIgnoreCase(fort.getShieldMaterials()[i].toString()))
						{
							Material shieldMat = fort.getShieldMaterials()[i];
							Chest chest = null;
							switch(b.getBlock().getData())
							{
								case 0x2://+z = back, left = +x
									if(w.getBlockAt(x,y,z+1).getType().equals(Material.CHEST))
									{
										chest = (Chest)w.getBlockAt(x,y,z+1).getState();
									}
									break;
								case 0x3://-z = back, left = -x
									if(w.getBlockAt(x,y,z-1).getType().equals(Material.CHEST))
									{
										chest = (Chest)w.getBlockAt(x,y,z-1).getState();
									}
									break;
								case 0x4://+x = back, left = -z
									if(w.getBlockAt(x+1,y,z).getType().equals(Material.CHEST))
									{
										chest = (Chest)w.getBlockAt(x+1,y,z).getState();
									}
									break;
								case 0x5://-x = back, left = +z
									if(w.getBlockAt(x-1,y,z).getType().equals(Material.CHEST))
									{
										chest = (Chest)w.getBlockAt(x-1,y,z).getState();
									}
									break;
							}
							
							
							
							int radius = fort.getShieldRadius();
							try
							{
								radius = Integer.parseInt(l3);
							}
							catch(Exception e)
							{
								radius = fort.getShieldRadius();
							}
							
							Shield shield = null;
							for(int k = 0; k < fort.getShieldList().size(); k++)
							{
								if(fort.getShieldList().get(k).getPos().getBlockX() == b.getBlock().getLocation().getBlockX()
										&& fort.getShieldList().get(k).getPos().getBlockY() == b.getBlock().getLocation().getBlockY()
										&& fort.getShieldList().get(k).getPos().getBlockZ() == b.getBlock().getLocation().getBlockZ())
								{
									shield = fort.getShieldList().get(k);
								}
							}
							if(shield != null)
							{
								shield.setPowered(powered);
							}
							else
							{
								Shield sh = new Shield(shieldMat, b.getBlock().getLocation(), radius, l4, chest);
								sh.setPowered(true);
								fort.getShieldList().add(sh);
							}
							
				            int rSquared = radius * radius;
				            
				            for (int i1 = x - radius; i1 <= x + radius; i1++) 
				            {
				                for (int k = z - radius; k <= z + radius; k++) 
				                {
				                	for(int cy = y - radius; cy <= y + radius; cy++)
				                	{
					                    if((x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) <= rSquared && (x - i1) * (x - i1) + (y - cy) * (y - cy) + (z - k) * (z - k) > (radius-1) * (radius-1)) 
					                    {
					                    	if(powered)
					                    	{
						                        final Location l = new Location(w, i1, cy, k);
						                        if(l.getBlock().getType().equals(Material.AIR) || l.getBlock().getType().equals(Material.FIRE) 
						                        		|| l.getBlock().getType().equals(Material.WATER) || l.getBlock().getType().equals(Material.LAVA))
						                        {
						                        	if(chest != null)
						                        	{
							                        	if(chest.getInventory().contains(shieldMat))
					                        			{
							                        		chest.getInventory().removeItem(new ItemStack(shieldMat, 1));
							                        		chest.update(true);
							                        		l.getBlock().setType(shieldMat);
					                        			}
						                        	}
						                        }
					                    	}
					                    	else
					                    	{
					                    		//not powered, remove and store blocks.
					                    		final Location l = new Location(w, i1, cy, k);
						                        if(l.getBlock().getType().equals(shieldMat))
						                        {
						                        	if(chest != null)
						                        	{
							                        		chest.getInventory().addItem(new ItemStack(shieldMat, 1));
							                        		chest.update(true);
							                        		l.getBlock().setType(Material.AIR);
						                        	}
						                        }
					                    	}
					                    }
				                	}
				                }
				            }//End Sphere generation loops...
						}
					}
				}
			////////////
			//Telepads//
			////////////
			if(l2.equalsIgnoreCase("[telepad]") && powered)
			{
				for(int i = 0; i < fort.getPadList().size(); i++)
				{
		//			log.info("1");
					if(fort.getPadList().get(i).getLocation().equals(b.getLocation()) && fort.getPadList().get(i).getSendBand().equals(l4) && fort.getPadList().get(i).getRecBand().equals(l3))
					{
			//			log.info("2");
						if(fort.getPadList().get(i).checkIntegrity() && fort.getPadList().get(i).validateSign())
						{
				//			log.info("3");
							for(int k = 0; k < fort.getPadList().size(); k++)
							{
								if(fort.getPadList().get(k).getRecBand().equals(fort.getPadList().get(i).getSendBand()) && k != i)
								{
					//				log.info("4");
									if(fort.getPadList().get(k).checkIntegrity() && fort.getPadList().get(k).validateSign())
									{
						//				log.info("5");
										if(fort.getPadList().get(k).getWidth() == fort.getPadList().get(i).getWidth() 
												&& fort.getPadList().get(k).getLength() == fort.getPadList().get(i).getLength()
												&& fort.getPadList().get(k).getHeight() == fort.getPadList().get(i).getHeight())
										{
						//					log.info("6");
											//Telepads are compatible, transport matter.
											/*Block[] b1;
											Block[] b2;
											b1 = new Block[fort.getPadList().get(i).getLength() * fort.getPadList().get(i).getWidth() * fort.getPadList().get(i).getHeight()];
											b2 = b1 = new Block[fort.getPadList().get(i).getLength() * fort.getPadList().get(i).getWidth() * fort.getPadList().get(i).getHeight()];*/
											saveBlock[][][] b1 = fort.getPadList().get(i).getBlocks();
											saveBlock[][][] b2 = fort.getPadList().get(k).getBlocks();
											
											//Move Blocks
											fort.getPadList().get(k).setBlocks(b1);
											fort.getPadList().get(i).setBlocks(b2);
											
											//Move Players & vehicles they are in
											//TODO: Players need to be teleported relative to rotation as well as the current relative position, have not tested vehicles.
											List<Player> p = fort.getPadList().get(k).getFLTower().getLocation().getWorld().getPlayers();
											boolean tel = false;
											double back = 0.0;
											double right = 0.0;
											double up = 0.0;
											//telepad i = origin; telepad k = destination
											for(int w1 = 0; w1 < p.size(); w1++)
											{
												switch(fort.getPadList().get(i).getDirection())
												{
												case 0x2://+z = back, left = +x
													if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getFLTower().getLocation().getBlockX() 
															&& p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getBRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() 
															&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(i).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
															&& p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() 
															&& p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getBRTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
														tel = true;
														//log.info("Player teleported.");//DEBUG
														back = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
														right = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
														up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
														
														p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
														p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getLocation().getYaw()));
														
														if(p.get(w1).getVehicle() != null)
														{
															back = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
															right = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
															
															p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
															p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
														}
													}
													break;
												case 0x3://-z = back, left = -x
													if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getFLTower().getLocation().getBlockX() 
															&& p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getBRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() 
															&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(i).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
															&& p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() 
															&& p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getBRTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area
														tel = true;
													//	log.info("Player teleported.");//DEBUG
														back = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
														right = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
														up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
														
														p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
														p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getLocation().getYaw()));
														
														if(p.get(w1).getVehicle() != null)
														{
															back = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
															right = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
															
															p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
															p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
														}
													}
													break;
												case 0x4://+x = back, left = -z
													if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getFLTower().getLocation().getBlockX() 
															&& p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getBRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() 
															&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(i).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
															&& p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() 
															&& p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getBRTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area
														tel = true;
												//		log.info("Player teleported.");//DEBUG
														right = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
														back = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
														up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
														
														p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
														p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getLocation().getYaw()));
														
														if(p.get(w1).getVehicle() != null)
														{
															right = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
															back = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
															
															p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
															p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
														}
													}
													break;
												case 0x5://-x = back, left = +z
													if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getFLTower().getLocation().getBlockX() 
															&& p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getBRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() 
															&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(i).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
															&& p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() 
															&& p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getBRTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area
														tel = true;
													//	log.info("Player teleported.");//DEBUG
														right = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
														back = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
														up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
														
														p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
														p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getLocation().getYaw()));
														
														if(p.get(w1).getVehicle() != null)
														{
															right = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(i).getFLTower().getLocation().getBlockZ());
															back = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(i).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(i).getFLTower().getLocation().getBlockY());
															
															p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(k).getDirection(), fort.getPadList().get(k).getFLTower().getLocation()));
															p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(i).getDirection(), fort.getPadList().get(k).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
														}
													}
													break;
												}
												if(!tel)
												{
													switch(fort.getPadList().get(k).getDirection())
													{
													case 0x2://+z = back, left = +x
														if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getFLTower().getLocation().getBlockX() 
																&& p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getBRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() 
																&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
																&& p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() 
																&& p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getBRTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
													//		log.info("Player teleported.");//DEBUG
															back = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
															right = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
															
															p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
															p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getLocation().getYaw()));
															
															if(p.get(w1).getVehicle() != null)
															{
																back = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
																right = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
																up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
																
																p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
																p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
															}
														}
														break;
													case 0x3://-z = back, left = -x
														if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getFLTower().getLocation().getBlockX() 
																&& p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getBRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() 
																&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
																&& p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() 
																&& p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getBRTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
														//	log.info("Player teleported.");//DEBUG
															back = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
															right = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
															
															p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
															p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getLocation().getYaw()));
															
															if(p.get(w1).getVehicle() != null)
															{
																back = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
																right = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
																up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
																
																p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
																p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
															}
														}
														break;
													case 0x4://+x = back, left = -z
														if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getFLTower().getLocation().getBlockX() 
																&& p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getBRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() 
																&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
																&& p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() 
																&& p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getBRTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
														//	log.info("Player teleported.");//DEBUG
															back = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
															right = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
															
															p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
															p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getLocation().getYaw()));
															
															if(p.get(w1).getVehicle() != null)
															{
																back = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
																right = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
																up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
																
																p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
																p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
															}
														}
														break;
													case 0x5://-x = back, left = +z
														if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getFLTower().getLocation().getBlockX() 
																&& p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getBRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() 
																&& p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
																&& p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() 
																&& p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getBRTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
														//	log.info("Player teleported.");//DEBUG
															back = Math.abs(p.get(w1).getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
															right = Math.abs(p.get(w1).getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
															up = Math.abs(p.get(w1).getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
															
															p.get(w1).teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
															p.get(w1).getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getLocation().getYaw()));
															
															if(p.get(w1).getVehicle() != null)
															{
																back = Math.abs(p.get(w1).getVehicle().getLocation().getZ() - fort.getPadList().get(k).getFLTower().getLocation().getBlockZ());
																right = Math.abs(p.get(w1).getVehicle().getLocation().getX() - fort.getPadList().get(k).getFLTower().getLocation().getBlockX());
																up = Math.abs(p.get(w1).getVehicle().getLocation().getY() - fort.getPadList().get(k).getFLTower().getLocation().getBlockY());
																
																p.get(w1).getVehicle().teleport(getRelativeLocation(back, right, up, fort.getPadList().get(i).getDirection(), fort.getPadList().get(i).getFLTower().getLocation()));
																p.get(w1).getVehicle().getLocation().setYaw(getRelativeRotation(fort.getPadList().get(k).getDirection(), fort.getPadList().get(i).getDirection(), p.get(w1).getVehicle().getLocation().getYaw()));
															}
														}
														break;
													}
												}
												else
												{
													tel = false;
												}
											}//for//*/
											return;
										}
									}
									else
									{
										//not a valid telepad, remove k from list and destroy sign.
										fort.getPadList().remove(k);
										fort.getPadList().get(k).getLocation().getBlock().setType(Material.AIR);
										fort.getPadList().get(k).getLocation().getWorld().dropItem(new Location(fort.getPadList().get(k).getLocation().getWorld(),
										fort.getPadList().get(k).getLocation().getBlock().getX(), fort.getPadList().get(k).getLocation().getBlock().getY(),
										fort.getPadList().get(k).getLocation().getBlock().getZ()), new ItemStack(Material.SIGN, 1));
									}
								}
							}
						}
						else
						{
							fort.getPadList().remove(i);
							b.setType(Material.AIR);;
							b.getWorld().dropItem(new Location(b.getWorld(), b.getBlock().getX(), b.getBlock().getY(), b.getBlock().getZ()), new ItemStack(Material.SIGN, 1));
						}
					}
				}
			}
			
			//////////////////////////
			//Transmitters/Receivers//
			//////////////////////////
			if(l2.equalsIgnoreCase("[Transmitter]") || l2.equalsIgnoreCase("[Transmit]"))
			{
				//this is coded very inefficiently, a search algorithm/data sorting could cut down time dramatically for large numbers
				for(int i = 0; i < fort.getReceiverList().size(); i++)
				{
					if(fort.getReceiverList().get(i).getBand().equals(l1))
					{
						if(fort.getReceiverList().get(i).exists())
						{
							Location l = fort.getReceiverList().get(i).getLocation();
							if(l.getBlock().getType().equals(Material.WALL_SIGN))
							{
								switch(l.getBlock().getData())
								{
								case 0x2:
									if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getType().equals(Material.LEVER))
									{
										if(powered)
										{
											//set receiver as powered
											int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).setData((byte) nd);
											}
										}
										else
										{
											//set receiver as unpowered
											int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getData();
											int nd = d & 0x7;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).setData((byte) nd);
											}
										}
									}
									break;
								case 0x3:
									if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getType().equals(Material.LEVER))
									{
										if(powered)
										{
											//set receiver as powered
											int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).setData((byte) nd);
											}
										}
										else
										{
											//set receiver as unpowered
											int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getData();
											int nd = d & 0x7;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).setData((byte) nd);
											}
										}
									}
									break;
								case 0x4:
									if(l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getType().equals(Material.LEVER))
									{
										if(powered)
										{
											//set receiver as powered
											int d = l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
											}
										}
										else
										{
											//set receiver as unpowered
											int d = l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getData();
											int nd = d & 0x7;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
											}
										}
									}
									break;
								case 0x5:
									if(l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getType().equals(Material.LEVER))
									{
										if(powered)
										{
											//set receiver as powered
											int d = l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
											}
										}
										else
										{
											//set receiver as unpowered
											int d = l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getData();
											int nd = d & 0x7;
											if(nd != d)
											{
												l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
											}
										}
									}
									break;
								}
							}
						}
						else
						{
							fort.getReceiverList().remove(i);
						}
					}
				}
			}
			
			/////////////////
			//Message Signs//
			/////////////////
			
			if(l2.equalsIgnoreCase("[Message]"))
			{
				try{
					if(powered){
						fort.getServer().getPlayer(l1).sendMessage(ChatColor.GOLD + l3 + " " + l4 + "[" + Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z) + "]");
					}
				}
				catch(Exception e){
					
				}
			}
			//////////////
			//Send Signs//
			//////////////
			if(powered)
			{
			if(l1.equalsIgnoreCase("[Send N]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend)
						{
						    if(c1.startsWith("[") && c1.endsWith("]"))
						    {
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:"))
							{
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands)
						{
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite)
							{
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]"))
							{
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin)
					{
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send S]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c1.startsWith("[") && c1.endsWith("]"))
						  {
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:"))
							{
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
					{
						if(!sendoverwrite || !sendoverwritescommands)
						{
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign)){
								return;
							}
							s2 = (Sign)c;
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite)
							{
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]"))
							{
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin)
					{
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send E]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, y, n).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend)
						{
						  if(c1.startsWith("[") && c1.endsWith("]"))
						  {
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:"))
							{
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination)
					{
						p--;
					if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands)
						{
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite)
							{
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]"))
							{
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin)
					{
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send W]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, y, n).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend)
						{
						    if(c1.startsWith("[") && c1.endsWith("]"))
						    {
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:"))
							{
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination)
					{
						p++;
					if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands)
						{
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite)
							{
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]"))
							{
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin)
					{
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send U]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin){
						n--;
					if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, n, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c1.startsWith("[") && c1.endsWith("]"))
						  {
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:"))
							{
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination)
					{
						p++;
					if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands)
						{
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite)
							{
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]"))
							{
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin)
					{
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send D]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, n, z).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						    if(c1.startsWith("[") && c1.endsWith("]"))
						    {
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:"))
							{
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands)
						{
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite)
							{
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]"))
							{
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin)
					{
						s.setLine(0, s2.getLine(0));
						s2.update();
						if(sendremovetext)
						{
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send R]"))
			{
				//east, north is to the right.
				if(w.getBlockAt(x, y, z).getData() == 0x2)
				{
					
				}
				//west, south is to the right
				if(w.getBlockAt(x, y, z).getData() == 0x3)
				{
					
				}
				//north, east is to the right
				if(w.getBlockAt(x, y, z).getData() == 0x4)
				{
					
				}
				//south, west is to the right
				if(w.getBlockAt(x, y, z).getData() == 0x5)
				{
					
				}
			}
			if(l1.equalsIgnoreCase("[Send L]")){
				//east, south is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x2)
				{
					
				}
				//west, north is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x3)
				{
					
				}
				//north, west is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x4)
				{
					
				}
				//south, east is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x5)
				{
					
				}
			}
			
			if(l2.equalsIgnoreCase("[Send N]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(n, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c2.startsWith("[") && c2.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
							
							
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(1).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send S]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
					if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend)
						{
						    if(c2.startsWith("[") && c2.endsWith("]"))
						    {
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(1).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send E]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, n).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend){
							    if(c2.startsWith("[") && c2.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
							
							
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
					if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands)
						{
							if(s2.getLine(1).trim().length() > 0 && !sendoverwrite)
							{
								return;
							}
							if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]"))
							{
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin)
					{
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send W]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, n).getState();
							if(!(c instanceof Sign)){
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c2.startsWith("[") && c2.endsWith("]"))
							    {
						  			return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, p).getState();
							if(!(c instanceof Sign)){
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(1).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			//Send text from sign below to sign above
			if(l2.equalsIgnoreCase("[Send U]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, n, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							/*	Check these for line 1 text sending.
							 * if(c1.startsWith("[") && c1.endsWith("]")){
									return;
								}
								else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
									return;
								}*/
								if(c2.startsWith("[") && c2.endsWith("]"))
								{
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, p, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(1).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send D]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, n, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c2.startsWith("[") && c2.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, p, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(1).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			
			if(l3.equalsIgnoreCase("[Send N]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(n, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c3.startsWith("[") && c3.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(2).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send S]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(n, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
						    	if(c3.startsWith("[") && c3.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(2).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send E]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, n).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c3.startsWith("[") && c3.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, p).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(2).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send W]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, n).getState();
							if(!(c instanceof Sign)){
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend){
							  if(c3.startsWith("[") && c3.endsWith("]")){
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, p).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(2).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			//Send text from sign below to sign above
			if(l3.equalsIgnoreCase("[Send U]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, n, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							/*	Check these for line 1 text sending.
							 * if(c1.startsWith("[") && c1.endsWith("]")){
									return;
								}
								else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
									return;
								}*/
								if(c3.startsWith("[") && c3.endsWith("]")){
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, p, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(2).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send D]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, n, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c3.startsWith("[") && c3.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, p, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(2).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(2, "");
							s.update();
						}
					}
				}
			}
			
			
			if(l4.equalsIgnoreCase("[Send N]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
					if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
					{
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign))
						{
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend)
						{
						    if(c4.startsWith("[") && c4.endsWith("]"))
						    {
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(3).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(3, "");
							s.update();
						}
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send S]"))
			{
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(n, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(n, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c4.startsWith("[") && c4.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(p, y, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(3).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(3, c2);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send W]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, n).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c4.startsWith("[") && c4.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, p).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(3).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(3, "");
							s.update();
						}
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send E]"))
			{
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(x, y, n).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, n).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c4.startsWith("[") && c4.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(x, y, p).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, y, p).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(3).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			//Send text from sign below to sign above
			if(l4.equalsIgnoreCase("[Send U]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n--;
						if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, n, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							/*	Check these for line 1 text sending.
							 * if(c1.startsWith("[") && c1.endsWith("]")){
									return;
								}
								else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
									return;
								}*/
								if(c4.startsWith("[") && c4.endsWith("]"))
								{
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p++;
						if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, p, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(3).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send D]"))
			{
				int n = y;
				int p = y;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++)
				{
					//Sign found below
					if(!origin)
					{
						n++;
						if(w.getBlockAt(x, n, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, n, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
								s = (Sign)c;
								c1 = s.getLine(0);
								c2 = s.getLine(1);
								c3 = s.getLine(2);
								c4 = s.getLine(3);
							
							if(!commandsend)
							{
							    if(c4.startsWith("[") && c4.endsWith("]"))
							    {
									return;
								}
							}
								origin = true;
						}
					}
					//Sign found above
					if(!destination)
					{
						p--;
						if(w.getBlockAt(x, p, z).getType().equals(Material.WALL_SIGN))
						{
							BlockState c = w.getBlockAt(x, p, z).getState();
							if(!(c instanceof Sign))
							{
								return;
							}
							s2 = (Sign)c;
							if(!sendoverwrite || !sendoverwritescommands)
							{
								if(s2.getLine(3).trim().length() > 0 && !sendoverwrite)
								{
									return;
								}
								if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]"))
								{
									return;
								}
							}
							destination = true;
						}
					}
					if(destination && origin)
					{
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext)
						{
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			
			//////////////
			//Trap Doors//
			//////////////
			if(l2.equalsIgnoreCase("[TrapDoor]"))
			{
				//get integer for range on first line, error checking to avoid non-ints is done onsignchange.
				int r = Integer.parseInt(l1);
				if(r > fort.getMaxtraplength())
				{
					r = fort.getMaxtraplength();
				}
				Material id = Material.AIR;
				Block d = w.getBlockAt(x, y, z);
				if(!(d.getType() == Material.WALL_SIGN))
				{
					return;
				}
				//east
				if(d.getData() == 0x2)
				{
					id = w.getBlockAt(x, y, z+1).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x, y, z+2+i).getType().equals(id))
							{
								w.getBlockAt(x, y, z+2+i).setType(Material.AIR);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x, y, z+2+k).getType().equals(Material.AIR) || w.getBlockAt(x, y, z+2+k).getType().equals(Material.WATER) || 
									w.getBlockAt(x, y, z+2+k).getType().equals(Material.STATIONARY_WATER)|| 
									w.getBlockAt(x, y, z+2+k).getType().equals(Material.LAVA) || 
									w.getBlockAt(x, y, z+2+k).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x, y, z+2+k).setType(id);
							}
						}
					}
				}
				//west
				if(d.getData() == 0x3)
				{
					id = w.getBlockAt(x, y, z-1).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x, y, z-2-i).getType().equals(id))
							{
								w.getBlockAt(x, y, z-2-i).setType(Material.AIR);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x, y, z-2-k).getType().equals(Material.AIR) || w.getBlockAt(x, y, z-2-k).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y, z-2-k).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x, y, z-2-k).getType().equals(Material.LAVA) ||
									w.getBlockAt(x, y, z-2-k).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x, y, z-2-k).setType(id);
							}
						}
					}
				}
				//north
				if(d.getData() == 0x4)
				{
					id = w.getBlockAt(x+1, y, z).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x+2+i, y, z).getType().equals(id))
							{
								w.getBlockAt(x+2+i, y, z).setType(Material.AIR);
							}
						}
						
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x+2+k, y, z).getType().equals(Material.AIR) || w.getBlockAt(x+2+k, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+k, y, z).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x+2+k, y, z).getType().equals(Material.LAVA) ||
									w.getBlockAt(x+2+k, y, z).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x+2+k, y, z).setType(id);
							}
						}
					}
				}
				//south
				if(d.getData() == 0x5)
				{
					id = w.getBlockAt(x-1, y, z).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x-2-i, y, z).getType().equals(id))
							{
								w.getBlockAt(x-2-i, y, z).setType(Material.AIR);
							}
						}
					}
					if(replacetrap && !powered){
						for(int k = 0; k < r; k++){
							if(w.getBlockAt(x-2-k, y, z).getType().equals(Material.AIR)|| w.getBlockAt(x-2-k, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-k, y, z).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x-2-k, y, z).getType().equals(Material.LAVA) ||
									w.getBlockAt(x-2-k, y, z).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x-2-k, y, z).setType(id);
							}
						}
					}
				}
			}
			/////////////////////////////////////
			//Upward trapdoor
			if(l2.equalsIgnoreCase("[UpTrapDoor]"))
			{
				//get integer for range on first line, error checking to avoid non-ints is done onsignchange.
				int r = Integer.parseInt(l1);
				if(r > fort.getMaxtraplength())
				{
					r = fort.getMaxtraplength();
				}
				Material id = Material.AIR;
				Block d = w.getBlockAt(x, y, z);
				if(!(d.getType() == Material.WALL_SIGN))
				{
					return;
				}
				//east
				if(d.getData() == 0x2)
				{
					id = w.getBlockAt(x, y, z+1).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x, y+1+i, z+1).equals(id))
							{
								w.getBlockAt(x, y+1+i, z+1).setTypeId(0);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x, y+1+k, z+1).getType().equals(Material.AIR) || w.getBlockAt(x, y+1+k, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y+1+k, z+1).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x, y+1+k, z+1).getType().equals(Material.LAVA) ||
									w.getBlockAt(x, y+1+k, z+1).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x, y+1+k, z+1).setType(id);
							}
						}
					}
				}
				//west
				if(d.getData() == 0x3)
				{
					id = w.getBlockAt(x, y, z-1).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x, y+1+i, z-1).getType().equals(id))
							{
								w.getBlockAt(x, y+1+i, z-1).setType(Material.AIR);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x, y+1+k, z-1).getType().equals(Material.AIR) || w.getBlockAt(x, y+1+k, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y+1+k, z-1).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x, y+1+k, z-1).getType().equals(Material.LAVA) ||
									w.getBlockAt(x, y+1+k, z-1).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x, y+1+k, z-1).setType(id);
							}
						}
					}
				}
				//north
				if(d.getData() == 0x4)
				{
					id = w.getBlockAt(x+1, y, z).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x+1, y+1+i, z).getType().equals(id))
							{
								w.getBlockAt(x+1, y+1+i, z).setTypeId(0);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x+1, y+1+k, z).getType().equals(Material.AIR) || w.getBlockAt(x+1, y+1+k, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x+1, y+1+k, z).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x+1, y+1+k, z).getType().equals(Material.LAVA) ||
									w.getBlockAt(x+1, y+1+k, z).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x+1, y+1+k, z).setType(id);
							}
						}
					}
				}
				//south
				if(d.getData() == 0x5)
				{
					id = w.getBlockAt(x-1, y, z).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x-1, y+1+i, z).getType().equals(id))
							{
								w.getBlockAt(x-1, y+1+i, z).setType(Material.AIR);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x-1, y+1+k, z).getType().equals(Material.AIR) || w.getBlockAt(x-1, y+1+k, z).getType().equals(Material.WATER)||
									w.getBlockAt(x-1, y+1+k, z).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x-1, y+1+k, z).getType().equals(Material.LAVA) ||
									w.getBlockAt(x-1, y+1+k, z).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x-1, y+1+k, z).setType(id);
							}
						}
					}
				}
			}
			/////////////////////////////////////
			//Downward trapdoor
			if(l2.equalsIgnoreCase("[DownTrapDoor]"))
			{
				//get integer for range on first line, error checking to avoid non-ints is done onsignchange.
				int r = Integer.parseInt(l1);
				if(r > fort.getMaxtraplength())
				{
					r = fort.getMaxtraplength();
				}
				Material id = Material.AIR;
				Block d = w.getBlockAt(x, y, z);
				if(!(d.getType() == Material.WALL_SIGN))
				{
					return;
				}
				//east
				if(d.getData() == 0x2)
				{
					id = w.getBlockAt(x, y, z+1).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x, y-1-i, z+1).getType().equals(id))
							{
								w.getBlockAt(x, y-1-i, z+1).setTypeId(0);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x, y-1-k, z+1).getType().equals(Material.AIR) || w.getBlockAt(x, y-1-k, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y-1-k, z+1).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x, y-1-k, z+1).getType().equals(Material.LAVA) ||
									w.getBlockAt(x, y-1-k, z+1).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x, y-1-k, z+1).setType(id);
							}
						}
					}
				}
				//west
				if(d.getData() == 0x3)
				{
					id = w.getBlockAt(x, y, z-1).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x, y-1-i, z-1).getType().equals(id))
							{
								w.getBlockAt(x, y-1-i, z-1).setType(Material.AIR);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x, y-1-k, z-1).getType().equals(Material.AIR) || w.getBlockAt(x, y-1-k, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y-1-k, z-1).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x, y-1-k, z-1).getType().equals(Material.LAVA) ||
									w.getBlockAt(x, y-1-k, z-1).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x, y-1-k, z-1).setType(id);
							}
						}
					}
				}
				//north
				if(d.getData() == 0x4)
				{
					id = w.getBlockAt(x+1, y, z).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x+1, y-1-i, z).getType().equals(id))
							{
								w.getBlockAt(x+1, y-1-i, z).setTypeId(0);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x+1, y-1-k, z).getType().equals(Material.AIR) || w.getBlockAt(x+1, y-1-k, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x+1, y-1-k, z).getType().equals(Material.STATIONARY_WATER)||
									w.getBlockAt(x+1, y-1-k, z).getType().equals(Material.LAVA) ||
									w.getBlockAt(x+1, y-1-k, z).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x+1, y-1-k, z).setType(id);
							}
						}
					}
				}
				//south
				if(d.getData() == 0x5)
				{
					id = w.getBlockAt(x-1, y, z).getType();
					if(powered)
					{
						for(int i = 0; i < r; i++)
						{
							if(w.getBlockAt(x-1, y-1-i, z).getType().equals(id))
							{
								w.getBlockAt(x-1, y-1-i, z).setType(Material.AIR);
							}
						}
					}
					if(replacetrap && !powered)
					{
						for(int k = 0; k < r; k++)
						{
							if(w.getBlockAt(x-1, y-1-k, z).getType().equals(Material.AIR) || w.getBlockAt(x-1, y-1-k, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x-1, y-1-k, z).getType().equals(Material.STATIONARY_WATER) ||
									w.getBlockAt(x-1, y-1-k, z).getType().equals(Material.LAVA) ||
									w.getBlockAt(x-1, y-1-k, z).getType().equals(Material.STATIONARY_LAVA))
							{
								w.getBlockAt(x-1, y-1-k, z).setType(id);
							}
						}
					}
				}
			}
			///////////
			//Turrets//
			///////////
			if(l2.equalsIgnoreCase("[Turret]"))
			{
			//	List<Player> p = fort.getServer().getOnlinePlayers();
				//Web Turret - Shoots web out to web length.
				if(l1.equalsIgnoreCase("web"))
				{
					if(powered)
					{
					//Sign facing east, so fire west, z increases
					if(w.getBlockAt(x, y, z).getData() == 0x2)
					{
						//If sign is on ice...
						if(w.getBlockAt(x, y, z+1).getType().toString().equalsIgnoreCase(webturretblockId) || webturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							int webDist = weblength;
							try
							{
								webDist = Integer.parseInt(l3);
								if(webDist > weblength)
								{
									webDist = weblength;
								}
							}
							catch(Exception e)
							{
								webDist = weblength;
							}
								if(w.getBlockAt(x, y, z+2+webDist).getType().equals(Material.AIR) ||
										w.getBlockAt(x, y, z+2+webDist).getType().equals(Material.WATER) ||
										w.getBlockAt(x, y, z+2+webDist).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x, y, z+2+webDist).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z+2+webDist, w, 30), webTime*20);
								}
								if(w.getBlockAt(x, y, z+2+webDist+1).getType().equals(Material.AIR) ||
										w.getBlockAt(x, y, z+2+webDist+1).getType().equals(Material.WATER) ||
										w.getBlockAt(x, y, z+2+webDist+1).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x, y, z+2+webDist+1).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z+2+webDist+1, w, 30), webTime*20);
								}
								if(w.getBlockAt(x, y, z+2+webDist-1).getType().equals(Material.AIR) ||
										w.getBlockAt(x, y, z+2+webDist-1).getType().equals(Material.WATER) ||
										w.getBlockAt(x, y, z+2+webDist-1).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x, y, z+2+webDist-1).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z+2+webDist-1, w, 30), webTime*20);
								}
								if(w.getBlockAt(x-1, y, z+2+webDist).getType().equals(Material.AIR) ||
										w.getBlockAt(x-1, y, z+2+webDist).getType().equals(Material.WATER) ||
										w.getBlockAt(x-1, y, z+2+webDist).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x-1, y, z+2+webDist).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z+2+webDist, w, 30), webTime*20);
								}
								if(w.getBlockAt(x+1, y, z+2+webDist).getType().equals(Material.AIR) ||
										w.getBlockAt(x+1, y, z+2+webDist).getType().equals(Material.WATER) ||
										w.getBlockAt(x+1, y, z+2+webDist).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x+1, y, z+2+webDist).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z+2+webDist, w, 30), webTime*20);
								}
								if(w.getBlockAt(x+1, y, z+2+webDist+1).getType().equals(Material.AIR) ||
										w.getBlockAt(x+1, y, z+2+webDist+1).getType().equals(Material.WATER) ||
										w.getBlockAt(x+1, y, z+2+webDist+1).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x+1, y, z+2+webDist+1).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z+2+webDist+1, w, 30), webTime*20);
								}
								if(w.getBlockAt(x-1, y, z+2+webDist+1).getType().equals(Material.AIR) ||
										w.getBlockAt(x-1, y, z+2+webDist+1).getType().equals(Material.WATER) ||
										w.getBlockAt(x-1, y, z+2+webDist+1).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x-1, y, z+2+webDist+1).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z+2+webDist+1, w, 30), webTime*20);
								}
								if(w.getBlockAt(x+1, y, z+2+webDist-1).getType().equals(Material.AIR) ||
										w.getBlockAt(x+1, y, z+2+webDist-1).getType().equals(Material.WATER) ||
										w.getBlockAt(x+1, y, z+2+webDist-1).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x+1, y, z+2+webDist-1).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z+2+webDist-1, w, 30), webTime*20);
								}
								if(w.getBlockAt(x-1, y, z+2+webDist-1).getType().equals(Material.AIR) ||
										w.getBlockAt(x-1, y, z+2+webDist-1).getType().equals(Material.WATER) ||
										w.getBlockAt(x-1, y, z+2+webDist-1).getType().equals(Material.STATIONARY_WATER))
								{
									w.getBlockAt(x-1, y, z+2+webDist-1).setType(Material.WEB);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z+2+webDist-1, w, 30), webTime*20);
								}
						}
					}
					//Sign facing west, so fire east, z decreases
					if(w.getBlockAt(x, y, z).getData() == 0x3)
					{
						//If sign is on ice...
						if(w.getBlockAt(x, y, z-1).getType().toString().equalsIgnoreCase(webturretblockId) ||
								webturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							int webDist = weblength;
							try
							{
								webDist = Integer.parseInt(l3);
								if(webDist > weblength)
								{
									webDist = weblength;
								}
							}
							catch(Exception e)
							{
								webDist = weblength;
							}
							if(w.getBlockAt(x, y, z-2-webDist).getType().equals(Material.AIR) ||
									w.getBlockAt(x, y, z-2-webDist).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y, z-2-webDist).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x, y, z-2-webDist).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z-2-webDist, w, 30), webTime*20);
							}
							if(w.getBlockAt(x, y, z-2-webDist+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x, y, z-2-webDist+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y, z-2-webDist+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x, y, z-2-webDist+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z-2-webDist+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x, y, z-2-webDist-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x, y, z-2-webDist-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x, y, z-2-webDist-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x, y, z-2-webDist-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z-2-webDist-1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-1, y, z-2-webDist).getType().equals(Material.AIR)||
									w.getBlockAt(x-1, y, z-2-webDist).getType().equals(Material.WATER) ||
									w.getBlockAt(x-1, y, z-2-webDist).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-1, y, z-2-webDist).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z-2-webDist, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+1, y, z-2-webDist).getType().equals(Material.AIR) ||
									w.getBlockAt(x+1, y, z-2-webDist).getType().equals(Material.WATER) ||
									w.getBlockAt(x+1, y, z-2-webDist).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+1, y, z-2-webDist).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z-2-webDist, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+1, y, z-2-webDist+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+1, y, z-2-webDist+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+1, y, z-2-webDist+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+1, y, z-2-webDist+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z-2-webDist+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-1, y, z-2-webDist+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-1, y, z-2-webDist+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-1, y, z-2-webDist+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-1, y, z-2-webDist+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z-2-webDist+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+1, y, z-2-webDist-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+1, y, z-2-webDist-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+1, y, z-2-webDist-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+1, y, z-2-webDist-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z-2-webDist-1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-1, y, z-2-webDist-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-1, y, z-2-webDist-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-1, y, z-2-webDist-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-1, y, z-2-webDist-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z-2-webDist-1, w, 30), webTime*20);
							}
						}
					}
						
					//Sign facing north, so fire north, x increases
					if(w.getBlockAt(x, y, z).getData() == 0x4)
					{
						//If sign is on ice...
						if(w.getBlockAt(x+1, y, z).getType().toString().equalsIgnoreCase(webturretblockId) ||
								webturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							int webDist = weblength;
							try
							{
								webDist = Integer.parseInt(l3);
								if(webDist > weblength)
								{
									webDist = weblength;
								}
							}
							catch(Exception e)
							{
								webDist = weblength;
							}
							if(w.getBlockAt(x+2+webDist, y, z).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist, y, z).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+weblength, y, z).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist, y, z, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist, y, z+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist, y, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist, y, z+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist, y, z+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist, y, z+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist, y, z-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist, y, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist, y, z-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist, y, z-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist, y, z-1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist-1, y, z).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist-1, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist-1, y, z).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist-1, y, z).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist-1, y, z, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist+1, y, z).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist+1, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist+1, y, z).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist+1, y, z).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist+1, y, z, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist+1, y, z+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist+1, y, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist+1, y, z+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist+1, y, z+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist+1, y, z+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist-1, y, z+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist-1, y, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist-1, y, z+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist-1, y, z+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist-1, y, z+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist+1, y, z-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist+1, y, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist+1, y, z-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist+1, y, z-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist+1, y, z-1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x+2+webDist-1, y, z-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x+2+webDist-1, y, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x+2+webDist-1, y, z-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x+2+webDist-1, y, z-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+webDist-1, y, z-1, w, 30), webTime*20);
							}
						}
					}
					//Sign facing south, so fire south, x decreases
					if(w.getBlockAt(x, y, z).getData() == 0x5)
					{
						//If sign is on ice...
						if(w.getBlockAt(x-1, y, z).getType().toString().equalsIgnoreCase(webturretblockId) ||
								webturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							int webDist = weblength;
							try
							{
								webDist = Integer.parseInt(l3);
								if(webDist > weblength)
								{
									webDist = weblength;
								}
							}
							catch(Exception e)
							{
								webDist = weblength;
							}

							if(w.getBlockAt(x-2-webDist, y, z).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist, y, z).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist, y, z).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist, y, z, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist, y, z+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist, y, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist, y, z+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist, y, z+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist, y, z+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist, y, z-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist, y, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist, y, z-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist, y, z-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist, y, z-1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist-1, y, z).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist-1, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist-1, y, z).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist-1, y, z).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist-1, y, z, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist+1, y, z).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist+1, y, z).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist+1, y, z).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist+1, y, z).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist+1, y, z, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist+1, y, z+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist+1, y, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist+1, y, z+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist+1, y, z+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist+1, y, z+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist-1, y, z+1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist-1, y, z+1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist-1, y, z+1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist-1, y, z+1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist-1, y, z+1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist+1, y, z-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist+1, y, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist+1, y, z-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist+1, y, z-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist+1, y, z-1, w, 30), webTime*20);
							}
							if(w.getBlockAt(x-2-webDist-1, y, z-1).getType().equals(Material.AIR) ||
									w.getBlockAt(x-2-webDist-1, y, z-1).getType().equals(Material.WATER) ||
									w.getBlockAt(x-2-webDist-1, y, z-1).getType().equals(Material.STATIONARY_WATER))
							{
								w.getBlockAt(x-2-webDist-1, y, z-1).setType(Material.WEB);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-webDist-1, y, z-1, w, 30), webTime*20);
							}
						}
					}
				}
				}
				
				//Flame Turret - Shoots fire out to flamelength
				if(l1.equalsIgnoreCase("flame") || l1.equals("fire"))
				{
					//Sign facing east, so fire west, z increases
					if(w.getBlockAt(x, y, z).getData() == 0x2)
					{
						//If sign is on bloodstone...
						if(w.getBlockAt(x, y, z+1).getType().toString().equalsIgnoreCase(flameturretblockId) ||
								flameturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							for(int i = 0; i < flamelength; i++)
							{
								if(w.getBlockAt(x, y, z+2+i).getType().equals(Material.AIR))
								{
									w.getBlockAt(x, y, z+2+i).setType(Material.FIRE);
								}
								else
								{
									break;
								}
							}
						}
					}
					//Sign facing west, so fire east, z decreases
					if(w.getBlockAt(x, y, z).getData() == 0x3)
					{
						//If sign is on bloodstone...
						if(w.getBlockAt(x, y, z-1).getType().toString().equalsIgnoreCase(flameturretblockId) ||
								flameturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							for(int i = 0; i < flamelength; i++)
							{
								if(w.getBlockAt(x, y, z-2-i).getType().equals(Material.AIR))
								{
									w.getBlockAt(x, y, z-2-i).setType(Material.FIRE);
								}
								else
								{
									break;
								}
							}
						}
					}
						
					//Sign facing north, so fire north, x increases
					if(w.getBlockAt(x, y, z).getData() == 0x4)
					{
						//If sign is on bloodstone...
						if(w.getBlockAt(x+1, y, z).getType().toString().equalsIgnoreCase(flameturretblockId) ||
								flameturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							for(int i = 0; i < flamelength; i++)
							{
								if(w.getBlockAt(x+2+i, y, z).getType().equals(Material.AIR))
								{
									w.getBlockAt(x+2+i, y, z).setType(Material.FIRE);
								}
								else
								{
									break;
								}
							}
						}
					}
					//Sign facing south, so fire south, x decreases
					if(w.getBlockAt(x, y, z).getData() == 0x5)
					{
						//If sign is on bloodstone...
						if(w.getBlockAt(x-1, y, z).getType().toString().equalsIgnoreCase(flameturretblockId) ||
								flameturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							for(int i = 0; i < flamelength; i++)
							{
								if(w.getBlockAt(x-2-i, y, z).getType().equals(Material.AIR))
								{
									w.getBlockAt(x-2-i, y, z).setType(Material.FIRE);
								}
								else
								{
									break;
								}
							}
						}
					}
				}
				//Arrow Turret - fires an arrow in direction it is facing.
				if(l1.equalsIgnoreCase("arrow") || l1.equalsIgnoreCase("default") || l1.equalsIgnoreCase("") || l1 == null)
				{
					float arrowSpeed = 1f;
					float arrowSpread = 7f;
					try
					{
						arrowSpeed = Float.parseFloat(l3);
					}
					catch(Exception e)
					{
						arrowSpeed = 1f;
					}
					
					try
					{
						arrowSpread = Float.parseFloat(l4);
					}
					catch(Exception e)
					{
						arrowSpread = 7f;
					}
					//Sign facing east, so fire west, z increases
					if(w.getBlockAt(x, y, z).getData() == 0x2)
					{
						//If sign is on specified block type...
						if(w.getBlockAt(x, y, z+1).getType().toString().equalsIgnoreCase(arrowturretblockId) ||
								arrowturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							//fire arrow
							Location target = new Location(w,x,y+0.5,z+10);
							Location origin = new Location(w,x,y+0.5,z+1.5);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), arrowSpeed, arrowSpread);
						}
					}
					//Sign facing west, so fire east, z decreases
					if(w.getBlockAt(x, y, z).getData() == 0x3){
						//If sign is on specified block type...
						if(w.getBlockAt(x, y, z-1).getType().toString().equalsIgnoreCase(arrowturretblockId) ||
								arrowturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							//fire arrow
							Location target = new Location(w,x,y+1.5,z-10);
							Location origin = new Location(w,x,y+1.5,z-1.5);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), arrowSpeed, arrowSpread);
						}
					}
						
					//Sign facing north, so fire north, x increases
					if(w.getBlockAt(x, y, z).getData() == 0x4){
						//If sign is on specified block type...
						if(w.getBlockAt(x+1, y, z).getType().toString().equalsIgnoreCase(arrowturretblockId) ||
								arrowturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							//fire arrow
							Location target = new Location(w,x+10,y+1.5,z);
							Location origin = new Location(w,x+1.5,y+1.5,z);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), arrowSpeed, arrowSpread);
						}
					}
					//Sign facing south, so fire south, x decreases
					if(w.getBlockAt(x, y, z).getData() == 0x5){
						//If sign is on specified block type...
						if(w.getBlockAt(x-1, y, z).getType().toString().equalsIgnoreCase(arrowturretblockId) ||
								arrowturretblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							//fire arrow
							Location target = new Location(w,x-10,y+1.5,z);
							Location origin = new Location(w,x-1.5,y+1.5,z);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), arrowSpeed, arrowSpread);
						}
					}
				}
			}//End Turrets
			
		
		}

		//Check user permissions
		@EventHandler
		public void onSignChange(SignChangeEvent e) 
		{
			if(e.getPlayer() != null)
			{
			ItemStack si = new ItemStack(Material.SIGN, 1);
			Player player = e.getPlayer();
			//message sign
				if(e.getLine(1).equalsIgnoreCase("[Message]"))
				{
					if(fort.isPermissionsEnabled())
					{
						if(!player.hasPermission("fortification.msgsign") && !player.hasPermission("fortification.*"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build message signs.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					}
					if(fort.isMsgOnlyBuilder() && !e.getPlayer().getName().equalsIgnoreCase(e.getLine(0)))
					{
						e.setLine(0, e.getPlayer().getName());
					}
					if(fort.isEcon())
					{
						if(e.getPlayer() != null)
						{
							if(fort.getMsgsignCost() > 0)
							{
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getMsgsignCost())
								{
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getMsgsignCost());
									return;
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getMsgsignCost() + ")");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
						}
					}
					player.sendMessage(ChatColor.GOLD + "Message Sign created.");
				}
				//Receiver -- included most common mis-spelling of the word too to make life easier :P
				if(e.getLine(1).equalsIgnoreCase("[Receiver]") || e.getLine(1).equalsIgnoreCase("[Reciever]"))
				{
					if(fort.isPermissionsEnabled())
					{
						if(!player.hasPermission("fortification.*") && !player.hasPermission("fortification.receiver"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build receivers.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					}
					if(fort.isEcon())
					{
						if(e.getPlayer() != null)
						{
							if(fort.getTransmitterCost() > 0)
							{
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getReceiverCost())
								{
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getReceiverCost());
									return;
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getReceiverCost() + ")");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
						}
					}
					fort.getReceiverList().add(new Receiver(e.getBlock().getLocation(), e.getLine(0)));
					player.sendMessage(ChatColor.GOLD + "Receiver created on band: " + e.getLine(0));
				}
				///////////
				//Telepad//
				///////////
				if(e.getLine(1).equalsIgnoreCase("[Telepad]"))
				{
					//check player permissions
					if(fort.isPermissionsEnabled())
					{
						if(!player.hasPermission("fortification.*") && !player.hasPermission("fortification.telepad"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build telepads.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					}
					//if(e.getLine(0).equalsIgnoreCase("ignore: id")) //TODO: Add filters here later
					//Make sure there is a proper receiving band
					for(int i = 0; i < fort.getPadList().size(); i++)
					{
						if(e.getLine(2).equals(fort.getPadList().get(i).getRecBand()))
						{
							player.sendMessage(ChatColor.RED + "The Receiving band is already in use.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					}
					/*if(e.getLine(0) == null || e.getLine(0) == "")
					{
						for(int i = 0; i < fort.getPadList().size(); i++)
						{
							if(e.getLine(2).equals(fort.getPadList().get(i).getRecBand()))
							{
								player.sendMessage(ChatColor.RED + "The Receiving band is already in use.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
					}
					else if(e.getLine(0).equalsIgnoreCase("advanced"))
					{
						for(int i = 0; i < fort.getPadList().size(); i++)
						{
							if(e.getLine(2).equals(fort.getPadList().get(i).getRecBand()))
							{
								player.sendMessage(ChatColor.RED + "The Receiving band is already in use.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
					}
					else
					{
						for(int i = 0; i < fort.getPadList().size(); i++)
						{
							if(e.getLine(0).equals(fort.getPadList().get(i).getRecBand()))
							{
								player.sendMessage(ChatColor.RED + "The Receiving band is already in use.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
					}*/
					//find towers, make sure they are intact... - first sign direction
					int teleLength = fort.getTelepadMaxLength();
					World w = e.getBlock().getWorld();
					int x = e.getBlock().getX();
					int y = e.getBlock().getY();
					int z = e.getBlock().getZ();
					TelepadTower fLeft,bLeft,fRight,bRight;
					switch(w.getBlockAt(new Location(w,x,y,z)).getData())
					{
					case 0x3: //-z = back, left = -x
						if(w.getBlockAt(new Location(w,x,y,z-1)).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId()) || fort.getTeleblockId() == "0")
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockAt(new Location(w,x-i,y,z-1)).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()))
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockAt(x-i-1,y,z-1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) && w.getBlockAt(x-i, y, z).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x-i,y,z-1), new Location(w,x-i-1,y,z-1), new Location(w,x-i, y, z), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockAt(x-i,y,z-1-k).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
														w.getBlockAt(x-i,y,z-2-k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
														&& w.getBlockAt(x-i-1,y,z-1-k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
												{
													bLeft = new TelepadTower(new Location(w,x-i,y,z-1-k), new Location(w,x-i,y,z-2-k), new Location(w,x-i-1,y,z-1-k), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockAt(x-i+j,y,z-1-k).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
																	w.getBlockAt(x-i+j,y,z-2-k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
																	&& w.getBlockAt(x-i+1+j,y,z-1-k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
															{
																bRight = new TelepadTower(new Location(w,x-i+j,y,z-1-k), new Location(w, x-i+1+j,y,z-1-k), new Location(w,x-i+j,y,z-2-k), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right tower integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
																else
																{
																	//find front right tower
																	fRight = new TelepadTower(new Location(w,x-i+j,y,z-1), new Location(w,x-i+j,y,z), new Location(w,x-i+1+j,y,z-1), fort);
																	if(!fRight.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad front right tower integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	else
																	{
																		fort.getPadList().add(t);
																		player.sendMessage(ChatColor.GOLD + "Telepad Detected.");
																		return;
																	}
																}
															}
														}//for j
													}
												}
											}//for k
										}
									}
								}
							}//for i
							player.sendMessage(ChatColor.RED + "Telepad - tower missing, integrity compromised.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					case 0x2://+z = back, left = +x
						if(w.getBlockAt(new Location(w,x,y,z+1)).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId()) ||
								fort.getTeleblockId().equalsIgnoreCase(Material.AIR.toString()))
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockAt(new Location(w,x+i,y,z+1)).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()))
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockAt(x+i+1,y,z+1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) &&
											w.getBlockAt(x+i, y, z).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x+i,y,z+1), new Location(w,x+i+1,y,z+1), new Location(w,x+i, y, z), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockAt(x+i,y,z+1+k).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
														w.getBlockAt(x+i,y,z+2+k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
														&& w.getBlockAt(x+i+1,y,z+1+k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
												{
													bLeft = new TelepadTower(new Location(w,x+i,y,z+1+k), new Location(w,x+i,y,z+2+k), new Location(w,x+i+1,y,z+1+k), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockAt(x+i-j,y,z+1+k).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
																	w.getBlockAt(x+i-j,y,z+2+k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
																	&& w.getBlockAt(x+i-1-j,y,z+1+k).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
															{
																bRight = new TelepadTower(new Location(w,x+i-j,y,z+1+k), new Location(w, x+i-j,y,z+2+k), new Location(w,x+i-1-j,y,z+1+k), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right tower integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
																else
																{
																	//find front right tower
																	fRight = new TelepadTower(new Location(w,x+i-j,y,z+1), new Location(w,x+i-j,y,z), new Location(w,x+i-1-j,y,z+1), fort);
																	if(!fRight.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad front right tower integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	else
																	{
																		fort.getPadList().add(t);
																		player.sendMessage(ChatColor.GOLD + "Telepad Detected.");
																		return;
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
							player.sendMessage(ChatColor.RED + "Telepad - tower missing, integrity compromised.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					case 0x5://-x = back, left = +z
						if(w.getBlockAt(new Location(w,x-1,y,z)).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId()) ||
								fort.getTeleblockId().equalsIgnoreCase(Material.AIR.toString()))
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockAt(new Location(w,x-1,y,z+i)).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()))
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockAt(x-1,y,z+i+1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) &&
											w.getBlockAt(x, y, z+i).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x-1,y,z+i), new Location(w,x-1,y,z+i+1), new Location(w,x, y, z+i), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockAt(x-k-1,y,z+i).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
														w.getBlockAt(x-k-1,y,z+i+1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
														&& w.getBlockAt(x-k-2,y,z+i).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
												{
													bLeft = new TelepadTower(new Location(w,x-k-1,y,z+i), new Location(w,x-k-1,y,z+i+1), new Location(w,x-k-2,y,z+i), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockAt(x-k-1,y,z+i-j).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
																	w.getBlockAt(x-k-1,y,z+i-j-1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
																	&& w.getBlockAt(x-k-2,y,z+i-j).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
															{
																bRight = new TelepadTower(new Location(w,x-k-1,y,z+i-j), new Location(w, x-k-1,y,z+i-j-1), new Location(w,x-k-2,y,z+i-j), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right tower integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
																else
																{
																	//find front right tower
																	fRight = new TelepadTower(new Location(w,x-1,y,z+i-j), new Location(w,x-1,y,z+i-j-1), new Location(w,x,y,z+i-j), fort);
																	if(!fRight.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad front right tower integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	else
																	{
																		fort.getPadList().add(t);
																		player.sendMessage(ChatColor.GOLD + "Telepad Detected.");
																		return;
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
							player.sendMessage(ChatColor.RED + "Telepad - tower missing, integrity compromised.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					case 0x4://+x = back, left = -z
						if(w.getBlockAt(new Location(w,x+1,y,z)).getType().toString().equalsIgnoreCase(fort.getTelepadBlockId()) ||
								fort.getTeleblockId().equalsIgnoreCase(Material.AIR.toString()))
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockAt(new Location(w,x+1,y,z-i)).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()))
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockAt(x+1,y,z-i-1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) &&
											w.getBlockAt(x, y, z-i).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x+1,y,z-i), new Location(w,x+1,y,z-i-1), new Location(w,x, y, z-i), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockAt(x+1+k,y,z-i).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
														w.getBlockAt(x+k+1,y,z-i-1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
														&& w.getBlockAt(x+2+k,y,z-i).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
												{
													bLeft = new TelepadTower(new Location(w,x+k+1,y,z-i), new Location(w,x+k+1,y,z-i-1), new Location(w,x+k+2,y,z-i), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockAt(x+k+1,y,z-i+j).getType().toString().equalsIgnoreCase(fort.getTelepadTowerId()) &&
																	w.getBlockAt(x+k+1,y,z-i+j+1).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()) 
																	&& w.getBlockAt(x+k+2,y,z-i+j).getType().toString().equalsIgnoreCase(fort.getTelepadSupportId()))
															{
																bRight = new TelepadTower(new Location(w,x+k+1,y,z-i+j), new Location(w, x+k+1,y,z-i+j+1), new Location(w,x+k+2,y,z-i+j), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
																else
																{
																	//find front right tower
																	fRight = new TelepadTower(new Location(w,x+1,y,z-i+j), new Location(w,x+1,y,z-i+j+1), new Location(w,x,y,z-i+j), fort);
																	if(!fRight.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad front right tower integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	else
																	{
																		fort.getPadList().add(t);
																		player.sendMessage(ChatColor.GOLD + "Telepad Detected.");
																		return;
																	}
																}
															}
														}
														break;
													}
												}
											}
											break;
										}
									}
								}
							}
							player.sendMessage(ChatColor.RED + "Telepad - tower missing, integrity compromised.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					}
				}
			//Transmitter
				if(e.getLine(1).equalsIgnoreCase("[Transmitter]") || e.getLine(1).equalsIgnoreCase("[Transmit]"))
				{
					if(fort.isPermissionsEnabled())
					{
						if(!player.hasPermission("fortification.*") && !player.hasPermission("fortification.transmitter"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build transmitters.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					}
					if(fort.isEcon())
					{
						if(e.getPlayer() != null)
						{
							if(fort.getTransmitterCost() > 0)
							{
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getTransmitterCost())
								{
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getTransmitterCost());
									return;
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getTransmitterCost() + ")");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
						}
					}
					player.sendMessage(ChatColor.GOLD + "Transmitter created on band: " + e.getLine(0));
				}
			//Shields
					if(e.getLine(1).equalsIgnoreCase("[Shield]"))
					{
						if(fort.isPermissionsEnabled())
						{
							if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") &&
									!player.hasPermission("fortification.shield.teleblock") && !player.hasPermission("fortification.shield.chest"))
							{
								player.sendMessage(ChatColor.RED + "You do not have permission to build shields.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						if(Material.getMaterial(e.getLine(0)) != null)
						{
							Material mat = Material.getMaterial(e.getLine(0));
							for(int i = 0; i < fort.getShieldMaterials().length; i++)
							{
								if(mat.equals(fort.getShieldMaterials()[i]))
								{
									if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.shield.default"))
									{
										player.sendMessage(ChatColor.RED + "You do not have permission to build the default shield type.");
										player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
										player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
										return;
									}
									else
									{
										//Check if an economic cost is associated with this?
										
										//At this point we should check to make sure the radius set doesn't exceed the max, for conveniance of the player...
										
										//Save shield into shield list.
										int shieldRadius = fort.getShieldRadius();
										try
										{
											shieldRadius = Integer.parseInt(e.getLine(2));
										}
										catch(Exception ex)
										{
											shieldRadius = fort.getShieldRadius();
										}
										
										World w = e.getBlock().getWorld();
										int x = e.getBlock().getX();
										int y = e.getBlock().getY();
										int z = e.getBlock().getZ();
										Chest chest = null;
										switch(e.getBlock().getData())
										{
											case 0x2://+z = back, left = +x
												if(w.getBlockAt(x,y,z+1).getType().equals(Material.CHEST))
												{
													chest = (Chest)w.getBlockAt(x,y,z+1).getState();
												}
												break;
											case 0x3://-z = back, left = -x
												if(w.getBlockAt(x,y,z-1).getType().equals(Material.CHEST))
												{
													chest = (Chest)w.getBlockAt(x,y,z-1).getState();
												}
												break;
											case 0x4://+x = back, left = -z
												if(w.getBlockAt(x+1,y,z).getType().equals(Material.CHEST))
												{
													chest = (Chest)w.getBlockAt(x+1,y,z).getState();
												}
												break;
											case 0x5://-x = back, left = +z
												if(w.getBlockAt(x-1,y,z).getType().equals(Material.CHEST))
												{
													chest = (Chest)w.getBlockAt(x-1,y,z).getState();
												}
												break;
										}
										
										if(chest == null)
										{
											player.sendMessage(ChatColor.RED + "This shield must be built out of a sign placed on a chest.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
										}
										else
										{
											fort.getShieldList().add(new Shield(mat, e.getBlock().getLocation(), shieldRadius, e.getLine(3), chest));
										}
									}
								}
							}
						}
						if(e.getLine(0).equalsIgnoreCase("teleblock"))
						{
							if(fort.isPermissionsEnabled())
							{
								if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.shield.teleblock"))
								{
									player.sendMessage(ChatColor.RED + "You do not have permission to build teleblock shields.");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
							if(fort.isEcon())
							{
								if(e.getPlayer() != null)
								{
									if(fort.getTeleblockshieldCost() > 0)
									{
										if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getTeleblockshieldCost())
										{
											fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getTeleblockshieldCost());
											return;
										}
										else
										{
											player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getTeleblockshieldCost() + ")");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
									}
								}
							}
						}
						else if(e.getLine(0).equalsIgnoreCase("chest"))
						{
							if(fort.isPermissionsEnabled())
							{
								if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.shield.chest"))
								{
									player.sendMessage(ChatColor.RED + "You do not have permission to build chest shields.");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
							if(fort.isEcon())
							{
								if(e.getPlayer() != null)
								{
									if(fort.getChestshieldCost() > 0)
									{
										if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getChestshieldCost()){
											fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getChestshieldCost());
											return;
										}
										else
										{
											player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getChestshieldCost() + ")");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
									}
								}
							}
						}
						else if(e.getLine(0).equalsIgnoreCase("playerchest"))
						{
							if(fort.isPermissionsEnabled())
							{
								if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.shield.chest")){
									player.sendMessage(ChatColor.RED + "You do not have permission to build playerchest shields.");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
							if(fort.isEcon())
							{
								if(e.getPlayer() != null)
								{
									if(fort.getChestshieldCost() > 0)
									{
										if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getChestshieldCost())
										{
											fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getChestshieldCost());
											return;
										}
										else
										{
											player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getChestshieldCost() + ")");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
									}
								}
							}
						}
						else if(e.getLine(0).equalsIgnoreCase("factionchest"))
						{
							if(fort.isPermissionsEnabled())
							{
								if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.shield.chest")){
									player.sendMessage(ChatColor.RED + "You do not have permission to build factionchest shields");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
							if(fort.isEcon())
							{
								if(e.getPlayer() != null)
								{
								if(fort.getChestshieldCost() > 0)
								{
									if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getChestshieldCost())
									{
										fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getChestshieldCost());
										return;
									}
								else
								{
									player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getChestshieldCost() + ")");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
								}
								}
							}
						}
					}
			
			//Send Signs
					if(e.getLine(0).equalsIgnoreCase("[Send]") || e.getLine(1).equalsIgnoreCase("[Send]") || e.getLine(2).equalsIgnoreCase("[Send]") || e.getLine(3).equalsIgnoreCase("[Send]")){
						if(fort.isPermissionsEnabled())
						{
							if(!player.hasPermission("fortification.sendsign") && !player.hasPermission("fortification.*"))
							{
								player.sendMessage(ChatColor.RED + "You do not have permission to build send signs.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						if(fort.isEcon())
						{
							if(e.getPlayer() != null)
							{
								if(fort.getSendsignCost() > 0)
								{
									if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getSendsignCost())
									{
										fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getSendsignCost());
										return;
									}
									else
									{
										player.sendMessage(ChatColor.RED + "You do not enough money for this purchase (" + fort.getSendsignCost() + ")");
										player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
										player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
										return;
									}
								}
							}
						}
					}
			//Turrets
				if(e.getLine(1).equalsIgnoreCase("[Turret]"))
				{
					if(fort.isPermissionsEnabled())
					{
						if(!player.hasPermission("fortification.turret.flame") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*") && !player.hasPermission("fortification.turret.web") && !player.hasPermission("fortification.turret.arrow"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build turrets.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					else
					{
						//if flame turret check permissions
						if(e.getLine(0).equalsIgnoreCase("flame"))
						{
							if(!player.hasPermission("fortification.turret.flame") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*")){
								player.sendMessage(ChatColor.RED + "You do not have permission to build flame turrets.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
							if(fort.isEcon())
							{
								if(e.getPlayer() != null)
								{
									if(fort.getFlameturretCost() > 0)
									{
										if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getFlameturretCost())
										{
											fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getFlameturretCost());
											return;
										}
										else
										{
											player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getFlameturretCost() + ")");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
									}
								}
							}
						}
					//web turret
					else if(e.getLine(0).equalsIgnoreCase("web"))
					{
						if(!player.hasPermission("fortification.turret.web") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build web turrets.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						float test = -2f;
						try
						{
							test = Float.parseFloat(e.getLine(2));
						}
						catch(Exception ex)
						{
							test = -2f;
						}
						if(test > weblength || test < 0)
						{
							e.setLine(2, Integer.toString(weblength));
						}
						if(fort.isEcon())
						{
							if(e.getPlayer() != null)
							{
								if(fort.getWebturretCost() > 0)
								{
									if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getWebturretCost())
									{
										fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getWebturretCost());
										return;
									}
									else
									{
										player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getWebturretCost() + ")");
										player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
										player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
										return;
									}
								}
							}
						}
					}
					//arrow turret
					else if(e.getLine(0).equalsIgnoreCase("arrow") || e.getLine(0).equalsIgnoreCase("default") || e.getLine(0).equalsIgnoreCase("") || e.getLine(0) == null)
					{
						if(!player.hasPermission("fortification.turret.arrow") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build arrow turrets.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						if(fort.isEcon())
						{
							if(e.getPlayer() != null)
							{
								if(fort.getArrowturretCost() > 0)
								{
									if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getArrowturretCost())
									{
										fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getArrowturretCost());
										return;
									}
									else
									{
										player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getArrowturretCost() + ")");
										player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
										player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
										return;
									}
								}
							}
						}
					}
					else 
					{
						//turret type is invalid, tell user
						player.sendMessage(ChatColor.RED + "Invalid turret type, type /fort turret for a list of turret types.");
						player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
						player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
						return;
					}
					}
				}
				}
				
				//Sensor
				if(e.getLine(1).contains("[Sensor") || e.getLine(1).contains("[uSensor") || e.getLine(1).contains("[dSensor"))
				{
					int detectRange;
					String[] s = new String[2];
					try
					{
						s = e.getLine(1).split(":");
						if(s[1].isEmpty())
						{
							//continue on to static range sensors
						}
						else
						{
							if((s[0].equalsIgnoreCase("[Sensor") || s[0].equalsIgnoreCase("[uSensor") || s[0].equalsIgnoreCase("[dSensor")) && s[1].endsWith("]"))
							{
								String i = s[1].replace(']', ' ');
								i.trim();
								detectRange = Integer.parseInt(i);
								//Make sure sensor does not exceed range limit
								if(detectRange > fort.getSensorlength())
								{
									detectRange = fort.getSensorlength();
									e.setLine(1, s[0] + ":" + Integer.toString(detectRange) + "]");
								}
							}
						}
					}
					catch(Exception e1)
					{
						e1.printStackTrace();
						player.sendMessage(ChatColor.RED + "Sensor range must be an integer");
						player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
						player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
						return;
					}
			}
					if(e.getLine(1).equalsIgnoreCase("[Sensor]") || e.getLine(1).equalsIgnoreCase("[UpSensor]") || e.getLine(1).equalsIgnoreCase("[DownSensor]") || e.getLine(1).equalsIgnoreCase("[uSensor]") || e.getLine(1).equalsIgnoreCase("[dSensor]"))
					{
						player.sendMessage("sensor detected");
						if(!player.hasPermission("fortification.sensor") && !player.hasPermission("fortification.*") && fort.isPermissionsEnabled())
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build a sensor");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
						//	String filter = e.getLine(0);
							//This is the code that use to check the validiy of sensor filters, since we have an api to add filters now 
							//though this has to be removed or custom filters won't be usable.
						/*	if(!filter.equalsIgnoreCase("playerdetect") && !filter.equalsIgnoreCase("playerignore") && !filter.equalsIgnoreCase("factiondetect")
									&& !filter.equalsIgnoreCase("groupdetect") && !filter.equalsIgnoreCase("factionignore") && !filter.equalsIgnoreCase("groupignore")
									&& !filter.equalsIgnoreCase("default") && !filter.equalsIgnoreCase("") && !filter.equalsIgnoreCase(" ") && !(filter == null)
									&& !filter.equalsIgnoreCase("weapondetect") && !filter.equalsIgnoreCase("weaponignore") && !filter.equalsIgnoreCase("itemdetect")
									&& !filter.equalsIgnoreCase("itemignore") && !filter.equalsIgnoreCase("areaalert") && !filter.equalsIgnoreCase("factionalert") 
									&& !filter.equalsIgnoreCase("tooldetect") && !filter.equalsIgnoreCase("toolignore") && !filter.equalsIgnoreCase("towndetect")
									&& !filter.equalsIgnoreCase("townignore") && !filter.equalsIgnoreCase("nationignore") && !filter.equalsIgnoreCase("nationdetect")
									&& !filter.equalsIgnoreCase("townalert") && !filter.equalsIgnoreCase("nationalert")
									&& !filter.equalsIgnoreCase("allydetect") && !filter.equalsIgnoreCase("enemydetect") && !filter.equalsIgnoreCase("healthrange")
									&& !filter.equalsIgnoreCase("armorDetect") && !filter.equalsIgnoreCase("armorIgnore") && !filter.equalsIgnoreCase("armor")
									&& !filter.equalsIgnoreCase("tool") && !filter.equalsIgnoreCase("weapon") && !filter.equalsIgnoreCase("item")
									&& !filter.equalsIgnoreCase("player") && !filter.equalsIgnoreCase("town") && !filter.equalsIgnoreCase("nation")
									&& !filter.equalsIgnoreCase("faction") && !filter.equalsIgnoreCase("enemy") && !filter.equalsIgnoreCase("ally")
									&& !filter.equalsIgnoreCase("fire") && !filter.equalsIgnoreCase("fireIgnore") && !filter.equalsIgnoreCase("fireDetect")
									&& !filter.equalsIgnoreCase("sprint") && !filter.equalsIgnoreCase("sprintDetect") && !filter.equalsIgnoreCase("sprintIgnore") 
									&& !filter.equalsIgnoreCase("sneak") && !filter.equalsIgnoreCase("sneakDetect") && !filter.equalsIgnoreCase("SneakIgnore")
									&& !filter.equalsIgnoreCase("permission") && !filter.equalsIgnoreCase("permDetect") && !filter.equalsIgnoreCase("permIgnore")
									&& !filter.equalsIgnoreCase("foodRange"))
							{
								//invalid sensor type
								player.sendMessage(ChatColor.RED + "Invalid sensor type, type /fort sensor for a list of sensor types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}*/
								if(fort.isEcon())
								{
									if(e.getPlayer() != null)
									{
										if(fort.getSensorCost() > 0)
										{
											if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getSensorCost())
											{
												fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getSensorCost());
												return;
											}
											else
											{
												player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getSensorCost() + ")");
												player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
												player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
												return;
											}
										}
									}
								}
								if(e.getLine(0).equalsIgnoreCase("factionalert"))
								{
									if(fort.isMsgOnlyBuilder())
									{
										/*if(fort.isFactionsEnabled()){
											FPlayer p = FPlayers.i.get(player);
											Faction f = p.getFaction();
											//P fa = (P)fort.getFac();
											Factions fac = Factions.i;
											if(e.getLine(2).equalsIgnoreCase("") || e.getLine(2) == null)
											{
												
											}
											else{
												if(fac.getBestTagMatch(e.getLine(2)).getId() == f.getId())
												{
													//then this is fine
												}
												else
												{
													if(fac.getBestTagMatch(e.getLine(2)).getRelationTo(f).isAlly())
													{
														//then this is fine
													}
													else{
														player.sendMessage(ChatColor.RED + "One of the factions you listed is either neutral or an enemy to your own.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
												}
											}
											if(e.getLine(3).equalsIgnoreCase("") || e.getLine(3) == null){
											
											}
											else{
												if(fac.getBestTagMatch(e.getLine(3)).getId() == f.getId()){
													//then this is fine
												}
												else{
													if(fac.getBestTagMatch(e.getLine(3)).getRelationTo(f).isAlly()){
														//then this is fine
													}
													else{
														player.sendMessage(ChatColor.RED + "One of the factions you listed is either neutral or an enemy to your own.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
												}
											}
										}*/
									}
								}
								if(e.getLine(0).equalsIgnoreCase("nationalert"))
								{
									if(fort.isMsgOnlyBuilder())
									{
										if(fort.isTownyEnabled())
										{
											Nation t = null;
											try {
												t = TownyUniverse.getDataSource().getResident(e.getPlayer().getName()).getTown().getNation();
											} catch (Exception e2) {
												e2.printStackTrace();
											}
											if(e.getLine(2).equalsIgnoreCase("") || e.getLine(2) == null)
											{
												
											}
											else
											{
												try {
													if(TownyUniverse.getDataSource().getNation(e.getLine(2)) != null)
													{
														if(t.equals(TownyUniverse.getDataSource().getNation(e.getLine(2)))){
															//same nation, this is fine
														}
														else{
																boolean temp = false;
																for(int i = 0; i < TownyUniverse.getDataSource().getTown(e.getLine(2)).getNation().getAllies().size(); i++)
																{
																	if(TownyUniverse.getDataSource().getTown(e.getLine(2)).getNation().getAllies().get(i).getName().equalsIgnoreCase(t.getName()))
																	{
																		//allied nation, this is fine
																		temp = true;
																		break;
																	}
																}
																if(!temp){
																	player.sendMessage(ChatColor.RED + "One of the nations you listed is either neutral or an enemy to your own.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
														}
													}
												} 
												catch (Exception e1) 
												{
													e1.printStackTrace();
												}
											}
											if(e.getLine(3).equalsIgnoreCase("") || e.getLine(3) == null)
											{
											
											}
											else
											{
												try 
												{
													if(TownyUniverse.getDataSource().getTown(e.getLine(3)) != null)
													{
														if(t.equals(TownyUniverse.getDataSource().getTown(e.getLine(3))))
														{
															//same nation, this is fine
														}
														else
														{
																boolean temp2 = false;
																for(int i = 0; i < TownyUniverse.getDataSource().getTown(e.getLine(3)).getNation().getAllies().size(); i++)
																{
																	if(TownyUniverse.getDataSource().getTown(e.getLine(3)).getNation().getAllies().get(i).getName().equalsIgnoreCase(t.getName()))
																	{
																		//nation is allied, this is fine
																		temp2 = true;
																		break;
																	}
																}
																if(!temp2)
																{
																	player.sendMessage(ChatColor.RED + "One of the nations you listed is either neutral or an enemy to your own.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
														}
													}
												} 
												catch (Exception e1) 
												{
													e1.printStackTrace();
												}
											}
										}
									}
								}
								if(e.getLine(0).equalsIgnoreCase("townalert"))
								{
									if(fort.isMsgOnlyBuilder())
									{
										if(fort.isTownyEnabled())
										{
											Towny town = (Towny)towny;
											if(town == null)
											{
												return;
											}
											Town t = null;
											try 
											{
												t = TownyUniverse.getDataSource().getResident(e.getPlayer().getName()).getTown();
											} catch (Exception e2) {
												e2.printStackTrace();
											}
											if(e.getLine(2).equalsIgnoreCase("") || e.getLine(2) == null)
											{
												
											}
											else
											{
												try 
												{
													if(TownyUniverse.getDataSource().getTown(e.getLine(2)) != null)
													{
														if(t.equals(TownyUniverse.getDataSource().getTown(e.getLine(2))))
														{
															//then this is fine
														}
														else{
															if(TownyUniverse.getDataSource().getTown(e.getLine(2)).getNation().equals(t.getNation()))
															{
																//town is in the same nation, this is fine
															}
															else
															{
																boolean temp = false;
																for(int i = 0; i < TownyUniverse.getDataSource().getTown(e.getLine(2)).getNation().getAllies().size(); i++)
																{
																	if(TownyUniverse.getDataSource().getTown(e.getLine(2)).getNation().getAllies().get(i).getName().equalsIgnoreCase(t.getNation().getName()))
																	{
																		//town is in an allied nation, this is fine
																		temp = true;
																		break;
																	}
																}
																if(!temp)
																{
																	player.sendMessage(ChatColor.RED + "One of the towns you listed is not within the same nation as you or a nation allied to yours.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
															}
														}
													}
												} 
												catch (Exception e1) 
												{
													e1.printStackTrace();
												}
											}
											if(e.getLine(3).equalsIgnoreCase("") || e.getLine(3) == null)
											{
											
											}
											else
											{
												try 
												{
													if(TownyUniverse.getDataSource().getTown(e.getLine(3)) != null)
													{
														if(t.equals(TownyUniverse.getDataSource().getTown(e.getLine(3))))
														{
															//then this is fine
														}
														else
														{
															if(TownyUniverse.getDataSource().getTown(e.getLine(3)).getNation().equals(t.getNation()))
															{
																//town is in the same nation, this is fine
															}
															else
															{
																boolean temp2 = false;
																for(int i = 0; i < TownyUniverse.getDataSource().getTown(e.getLine(3)).getNation().getAllies().size(); i++)
																{
																	if(TownyUniverse.getDataSource().getTown(e.getLine(3)).getNation().getAllies().get(i).getName().equalsIgnoreCase(t.getNation().getName()))
																	{
																		//town is in an allied nation, this is fine
																		temp2 = true;
																		break;
																	}
																}
																if(!temp2)
																{
																	player.sendMessage(ChatColor.RED + "One of the towns you listed is not within the same nation as you or a nation allied to yours.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
																	player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																	return;
																}
															}
														}
													}
												} 
												catch (Exception e1) 
												{
													e1.printStackTrace();
												}
											}
										}
									}
								}
					//			if(e.getLine(0).equalsIgnoreCase("itemdetect") || e.getLine(0).equalsIgnoreCase("itemignore"))
					//			{
					//				
					//				try
					//				{
					//					if(e.getLine(2) != null && e.getLine(2) != "")
					//					{
					//						Integer.parseInt(e.getLine(2));
					//					}
					//					if(e.getLine(3) != null && e.getLine(3) != "")
					//					{
					//						Integer.parseInt(e.getLine(3));
					//					}
					//				}
					//				catch(Exception ex){
					//					player.sendMessage(ChatColor.RED + "The 3rd and 4th lines must contain the material name of an item.");
					//					player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
					//					player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
					//					return;
					//				}
					//			}
						}
					}
			//Trap door
				int temp;
					if(e.getLine(1).equalsIgnoreCase("[TrapDoor]") || e.getLine(1).equalsIgnoreCase("[UpTrapdoor]") || e.getLine(1).equalsIgnoreCase("[DownTrapdoor]"))
					{
						if(!player.hasPermission("fortification.trapdoor") && !player.hasPermission("fortification.*") && fort.isPermissionsEnabled())
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build a trap door");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
							player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							try
							{
								temp = Integer.parseInt(e.getLine(0));
							}
							catch(Exception ex)
							{
								player.sendMessage(ChatColor.RED + "The first line must contain an integer value.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
								if(temp > maxtraplength)
								{
									player.sendMessage(ChatColor.RED + "The length of the trap door can not exceed " + Integer.toString(maxtraplength));
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
								if(temp <= 0)
								{
									player.sendMessage(ChatColor.RED + "Trap doors must have a length greater than 0.");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
						}
						//east
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x2)
						{
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++)
							{
								if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+1).getType().toString().equalsIgnoreCase(trapblocks[i]))
								{
									validblock = true;
									break;
								}
							}
							if(!validblock)
							{
								player.sendMessage(ChatColor.RED + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						//west
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x3)
						{
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++)
							{
								if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-1).getType().toString().equalsIgnoreCase(trapblocks[i]))
								{
									validblock = true;
									break;
								}
							}
							if(!validblock)
							{
								player.sendMessage(ChatColor.RED + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						//north
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x4)
						{
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++)
							{
								if(player.getWorld().getBlockAt(e.getBlock().getX()+1, e.getBlock().getY(), e.getBlock().getZ()).getType().toString().equalsIgnoreCase(trapblocks[i]))
								{
									validblock = true;
									break;
								}
							}
							if(!validblock)
							{
								player.sendMessage("&c" + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						//south
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x5)
						{
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++)
							{
								if(player.getWorld().getBlockAt(e.getBlock().getX()-1, e.getBlock().getY(), e.getBlock().getZ()).getType().toString().equalsIgnoreCase(trapblocks[i]))
								{
									validblock = true;
									break;
								}
							}
							if(!validblock)
							{
								player.sendMessage(ChatColor.RED + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
							if(fort.isEcon())
							{
								if(e.getPlayer() != null)
								{
									if(fort.getTrapdoorCost() > 0)
									{
										if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getTrapdoorCost())
										{
											fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getTrapdoorCost());
											return;
										}
										else
										{
											player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getTrapdoorCost() + ")");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
									}
								}
							}
						}
					}
			if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getType().equals(Material.WALL_SIGN))
			{
				if(e.getLine(1).equalsIgnoreCase("[Equals]"))
				{
					if(!player.hasPermission("fortification.equalsign") && !player.hasPermission("fortification.*") && fort.isPermissionsEnabled())
					{
						player.sendMessage(ChatColor.RED + "You do not have permission to build equals signs.");
						player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
						player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
						return;
					}
					if(fort.isEcon())
					{
						if(e.getPlayer() != null)
						{
							if(fort.getEqualsignCost() > 0)
							{
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getEqualsignCost())
								{
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getEqualsignCost());
									return;
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getEqualsignCost() + ")");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setType(Material.AIR);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
						}
					}
			return;
		}
				//player is potentially null here..
				//equals sign
					if(e.getLine(1).equalsIgnoreCase("[Equals]"))
					{
							//if equal turn on redstone
							if(e.getLine(0).equalsIgnoreCase(e.getLine(2))){
								if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x2)
								{
									if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+2).getType().equals(Material.LEVER))
									{
										int d = e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+2).setData((byte) nd);
											//May have to update block physics here somehow?
										}
									}
								}
								else if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x3)
								{
									if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-2).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-2).setData((byte)nd);
										//	etc.getServer().updateBlockPhysics(sign.getX(), sign.getY(), sign.getZ()-2, nd);
										}
									}
								}
								else if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x4)
								{
									if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(), e.getBlock().getZ()).getType().equals(Material.LEVER))
									{
										int d = e.getBlock().getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(), e.getBlock().getZ()).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											e.getBlock().getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(), e.getBlock().getZ()).setData((byte)nd);
										//	etc.getServer().updateBlockPhysics(sign.getX()+2, sign.getY(), sign.getZ(), nd);
										}
									}
								}
								else if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x5)
								{
									if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).getType().equals(Material.LEVER))
									{
										int d = e.getBlock().getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											e.getBlock().getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).setData((byte)nd);
										//	etc.getServer().updateBlockPhysics(sign.getX()-2, sign.getY(), sign.getZ(), nd);
										}
									}
								}
							}
							//if not equal turn off redstone
							else{
								if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x2)
								{
									if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()+2).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()+2).getData();
										int nd = d & 0x7;
										if(nd != d)
										{
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()+2).setData((byte)nd);
									//		etc.getServer().updateBlockPhysics(sign.getX(), sign.getY(), sign.getZ()+2, nd);
										}
									}
								}
								else if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x3)
								{
									if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()-2).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()-2).getData();
										int nd = d & 0x7;
										if(nd != d)
										{
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()-2).setData((byte)nd);
									//		etc.getServer().updateBlockPhysics(sign.getX(), sign.getY(), sign.getZ()-2, nd);
										}
									}
								}
								else if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x4)
								{
									if(player.getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(),e.getBlock().getZ()).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(),e.getBlock().getZ()).getData();
										int nd = d & 0x7;
										if(nd != d)
										{
											player.getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(),e.getBlock().getZ()).setData((byte)nd);
									//		etc.getServer().updateBlockPhysics(sign.getX()+2, sign.getY(), sign.getZ(), nd);
										}
									}
								}
								else if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x5)
								{
									if(player.getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).getData();
										int nd = d & 0x7;
										if(nd != d)
										{
											player.getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).setData((byte)nd);
									//		etc.getServer().updateBlockPhysics(sign.getX()-2, sign.getY(), sign.getZ(), nd);
										}
									}
								}
							}
						}
					
				}
		}
		
	}
}