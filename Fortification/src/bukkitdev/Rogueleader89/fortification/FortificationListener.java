package bukkitdev.Rogueleader89.fortification;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
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
 * 0.91 Changelog
 * 
 * 
 * 0.9 Changelog
 * Fixed Config Generation
 * All sensors can now be set to go lengths shorter than the standard length using [Sensor:length] for instance, [sensor:5] would make the sensor go out 5 spaces, this also works with
 uSensors and dSensors (but not their outdated counterparts upSensor and downSensor)
 ****************************************************************************************
 * UpSensors can now be made with [uSensor] and DownSensors can be made with [dSensor] (not case sensitive for either)
 * Added healthRange filter to sensors, line 3 being the min health players will be detected and line 4 the max, note that counting is done by half hearts.
 * Added armorDetect and armorIgnore filters to sensors (similar to weapondetect/ignore)
 * Fixed telepads not teleporting people (and maybe the vehicles they are in) (still buggy)
 * Fixed exploit involving send signs and trapdoors
 * Added redstone transmitters and receivers [transmit] or [transmitter] and band [receiver] or band [reciever] Note that bands must be EXACT
 * Added /fort radio [band] command and settings for the range of the radio (0 = unlimited) and whether it can cross worlds -- triggers receivers /fort radio <band> [on/off or true/false] blank = on/true 
 * Added telepads [telepad] sendBand ReceiveBand, Note that the bands must be EXACT. Telepads consist of 4 towers of equal height. 
 *  o   o
 * o+   +o
 *   
 * o+ C +o
 *  o   o
 *  + = tower block (default iron), o = support block (default lapis), tower blocks should be up to the height of the area you want teleported, the top tower block differs in composition (defaultly diamond),
 *  support towers should be half the height (rounded down) of the main tower height.
 *  C = sign block, the sign must be placed on a specified block type (default iron), this block must be between two of the towers, ideally somewhere near the menu.
 *  Sign format below:
 *  filter (blank for now)
 *  [telepad]
 *  recBand
 *  sendBand
 *  Also note that to teleport between telepads, the telepads must have the same widths, lengths, and heights (though they can be facing different directions if need be).
 *  
 *  Updated towny support to not use deprecated methods
 *  Changed teleblocks to require fortification.ignoreteleblock permission or op to bypass, fortification.* no longer automatically allows bypassing 
 *  
 * 
 * 0.8.7 Changelog
 * Updated for 1.2.4
 * Teleblock shields now require redstone power in order to function (directed towards sign)
 * 
 * 0.8.6 Changelog
 * Fixed ItemDetect/Ignore sensor filter check to allow for only a single item id to be used.
 * Updated to support new build of factions
 * 
 * 0.8.5 Changelog
 * Fixed error on startup for people using Factions plugin
 * Removed Register support and added Vault support
 * Added new config option for people who are not running a permissions plugin (setting permissions to false lets everyone use everything).
 * Temporarily removed allydetect and enemydetect sensor filters for factions users (towny versions still work fine).
 * 
 * 0.8.4 Changelog
 * Updated to support new Factions builds (Note that old factions builds will now cause errors)
 * Fixed permission issues, support for the old permissions plugin dropped entirely (must have superperms now)
 * Added EnemyDetect and AllyDetect which detect the enemies or allies of the factions listed on lines 3 and 4 of the sign.
 * 
 * 0.8.3 Changelog
 * Fixed an issue that prevented the plugin from working with certain server configurations lacking Towny
 * 
 * 0.8.2 Changelog
 * Added Towny support with townAlert, nationAlert, townDetect, townIgnore, nationDetect, and nationIgnore sensor filters.
 * the msg-only-builder config option now requires towns listed within townAlerts to either be the builder's town, within the same nation,
  or in an allied nation, and nations in nationAlert to either be the builder's nation or an allied nation.
 *  Added upward and downward facing sensors [UpSensor] and [DownSensor], all normal sensor filters apply.
 *  
 * 0.8.1 Changelog
 * Fixed north facing sensors not reseting
 * Fixed south facing trapdoors checking the id of the wrong block
 * Added 3 second delay between chat messages sent from factionalert and areaalert sensors.
 * Added optional ability to toggle teleblocks on/off. Done by placing a redstone torch on the opposite side of block from sign.
 if the torch is on the teleblock is on, if off then teleblock is off. **this works terribly, think of better solution :P
 * Fixed more misguiding instructions pointing to old help commands that no longer exist.
 * Removed check on sensors to see if someone was leaving a sensor area (since sensors are on a time delay to turn off now anyway),
 may help improve performance.
 * FactionAlert sensors no longer trigger redstone if the player detected is in one of the two factions listed on the sign.
 * Added upward and downward facing sensors [UpSensor] and [DownSensor], all normal sensor filters apply. **these apparently are not working..
 * 
 * 
 * 0.8 Changelog
 * Increased radius of sensors to detect one above and one below the level of the sign.
 * Added arrow turrets under permission fortification.turret.arrow
 * Fixed a permissions for info commands related to flame and web turrets (fortification.turret.flame and fortification.turret.web)
 * Replaced BOSEconomy Support with Register support (requires Register on server), providing compatibility with multiple economy plugins.
 * Message signs now send their location along with messages.
 * Trapdoors now go through water/lava.
 * Added itemdetect and itemignore sensor filters that check if the player has (or does not have) an item of the id listed on line 3 or 4.
 * Added weapondetect and weaponignore sensor filters that check if a player is or is not carrying a weapon (sword or bow).
 * Added areaalert and factionalert sensor filters. Area alert broadcasts the detected player's name to everyone within sensor-broadcast-dist blocks distance.
 factionalert broadcasts the detected player's name to everyone in the factions listed on lines 3 and 4 unless that player is in one of the factions listed. Both trigger redstone on detecting a player
 * Added new config option, msg-only-builder. When set to true message signs always contain the name of their builder on the first line, and factionalert sensors require that
 the factions listed either be the faction the builder of the sensor is in, or a faction allied to their faction.
 * Added tooldetect and toolignore sensor filters that check if a player is or is not carrying a tool (pick, shovel, axe, shears).
 * 
 * 0.7 Changelog
 * added new shield types: chest, playerchest, and factionchest, all must have a lever on the opposite side of the sign redstone is triggered 
 * by chest use within 5 (eventually chestshieldrange..) blocks of the shield, playerchests ignore the people listed on lines 3 & 4, factionchest
 * ignores people in the factions listed on lines 3 & 4. Permission fortification.shield.chest
 * Web turrets added, permission fortification.turret.web
 * Added BOSEconomy Support (optional cost per mechanism)
 * 
 * 0.6 Changelog
 * Updated for Bukkit
 * Frost Turrets removed
 * factiondetect and factionignore sensors added - requires Factions plugin.
 * doubledetect and singledetect sensor filters combined and changed to playerdetect
 * doubleignore and singleignore sensor filters combined and changed to playerignore
 * added multiworld support
 * teleblock shields now detect 1 block above and 1 below the level they are on along their radius of effect.
 * 
 * BUG LIST
 * Class cast exception in playerteleport/teleblock search radius, specifically
 furnaces are getting past the check to ensure the block is a sign. **looks to be a craftbukkit issue**
 * Arrows fired from arrow turrets appear to fall to the ground immediately on the client
 but actually do move and do damage as intended (would need to send proper packet to client to fix).
 * 
 * 
 * ToDo:
 * New Sensor filter to detect armor (armordetect)
 * New Sensor filter to detect time since player was last in combat (combatdetect [sensor] 1d 5h 3m 2s)
 * New Sensor filter to detect players who have a certain custom defined permission (fort.group.whateverpermission) or actually let them define any permission at all so it can link to other plugins...
 * [Telepad]
 * 
 * Next Major Release (0.9):
 * -Update configuration to new style (current one is deprecated..)
 * Add Lift Signs that take standard filters
 * -Add transmitters for sending redstone signal via commands/spout gui or in-world transmitters to recievers filter [transmitter] dest1 dest2.
 * -Fix Teleblock toggle, detect redstone power instead of using a torch.
 * Faction/Town Message Signs
 * Check and make sure trapdoor length can't be increased beyond limit with send signs
 * Allow the use of "fire" as a synonym for flame on flame turrets
 * Consider adding telepads or similar warp gate functionality with standard filter compatibility
 * Consider adding craftbook style gates
 * 
 * 1.0 Release:
 * Re-examine and possibly finish implementation of send signs.
 * Bug fix everything, optimize code where needed.
 * Add lifts in some form (preferably elevators as a vehicle of sorts).
 * Add mob sensors + filters (hostile mob vs non-hostile, mobdetect/ignore).
 * Individual settings per mechanism - Range, direction, radius, filters (if applicable)
 * Send packet to client to show arrows when they are shot from arrow turrets
 * Add forcefield type shields and other such things (with necessary weaknesses, possibly only deflect certain amounts of stuff?)
 * (delayed for 1.3 to see which api catches on..)Add spout support, condensing sensors, turrets, etc. into single custom blocks. Retain old creation methods as well. This may wait till 1.0
 * 
 * Other:
 * 
 * Prevent a single message sign from spamming a player constantly with text.
 * This also applies to factionalert signs, possibly delay between messages?
 * combine factionignore and factionalert signs?
 * make teleblocks toggleable
 * 
 * Tractorbeam - pulls blocks towards it on redstone change
 * Repulser - pushes blocks away from it on redstone change
 * 
 * Add command that allows player to mute all messages from message signs.
 * 
 * Allow user to define plugin priority in config file -- not really necessary...
 * 
 * Create new type of send sign that exchanges/flips text between two signs (so s1 becomes s2 and s2 becomes s1)
 * 
 * Add ability to move strings from one line of a sign to a different line on another sign (s1l1 to s2l4 for instance [Send N3] would send
 * the string on the line the send sign is on to line 3 on a sign to the north.
 * 
 * Add device that can detect the destruction of nearby blocks and activate redstone when they are destroyed.
 * 
 * Add optional fuel costs to turrets
 * 
 * add area health regen signs w/filters like sensors, possibly a shield type?
 * 
 * add new sensor types (multi-directional, radius detect, more filter options (custom filters))
 * 
 * add sensor that can detect mobs
 * 
 * shield signs to increase hardness/durability of blocks around it (not possible atm).
 * different types of shields are possible here. Bubbleshield could hold back water similar to the bubble spell.
 * Endurance shields could replace destroyed blocks around them with blocks from a chest located on the block the shield sign is on.
 * 
 * Large Area Teleporter - teleport a number of blocks/people from one point to another (for use with airships).
 * Essentially a warp gate.
 * 
 * advanced sensors that can send name of detected player to another sign (could have it be l1 = listname; l2 = [Sensor]; l3 = name of player)
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
 * item grabber - Take item player is holding and stores in chest above turret (or an item of id defined by user?)
 * item dispenser - shoot out an item from a chest near the turret. Notch did this, but implement anyway, works well with other chest-based
 * plugins. Chest should be ontop of block with turret sign, specify direction that chest dispenses items.
 * Water Release - redstone toggleable water
 * Lava Release - redstone toggleable lava
 * 4 way turrets, place block ontop of turret block, fires out in all 4 directions (or even 8 if arrows are used).
 * Floor traps - turrets that come up from the floor and fire once when redstone triggered.
 */
public class FortificationListener implements Listener {
		private Fortification fort;
	//	private PropertiesFile properties = new PropertiesFile("fortification.properties");
		private int arrowturretblockId;
		private int flamelength;
		private int flameturretblockId;
		private int weblength;
		private int webturretblockId;
		private int webTime;
		private int maxtraplength;
		private int sendlength;
		private int[] trapblocks;
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
		
		@EventHandler
		public void onBlockBreak(BlockBreakEvent e)
		{
			//TODO: Fix error here, telepads aren't being deleted properly, transmitters likely have a similar, less noticable, issue. Also console errors out trying to cast to sign..
			//if its a sign this is really easy
			if(e.getBlock().getTypeId() == 68 && e.getBlock() instanceof Sign)
			{
				removeReceiver((Sign)e.getBlock());
				removeTelepad((Sign)e.getBlock());
			}
			//see if a sign was attached to whatever was broken
			if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()-1) == 68)
			{
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()-1);
				if(((Block)s).getData() == 0x2)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()+1) == 68)
			{
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()+1);
				if(((Block)s).getData() == 0x3)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getLocation().getBlockX()-1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()) == 68)
			{
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX()-1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ());
				if(((Block)s).getData() == 0x4)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getLocation().getBlockX()+1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()) == 68)
			{
				Sign s = (Sign)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().getBlockX()+1, e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ());
				if(((Block)s).getData() == 0x5)
				{
					removeReceiver(s);
					removeTelepad(s);
				}
			}
			e.getBlock().getLocation();
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
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx, my, mz + 1) == 68){
					handleinput(mx, my, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx, my + 1, mz + 1) == 68){
					handleinput(mx, my+1, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx, my - 1, mz + 1) == 68){
					handleinput(mx, my-1, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx, my, mz - 1) == 68){
					handleinput(mx, my, mz-1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx, my + 1, mz - 1) == 68){
					handleinput(mx, my+1, mz-1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx, my - 1, mz - 1) == 68){
					handleinput(mx, my-1, mz-1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx - 1, my, mz) == 68){
					handleinput(mx-1, my, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx - 1, my + 1, mz) == 68){
					handleinput(mx-1, my+1, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx - 1, my - 1, mz) == 68){
					handleinput(mx-1, my-1, mz+1, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx + 1, my, mz) == 68){
					handleinput(mx+1, my, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx + 1, my + 1, mz) == 68){
					handleinput(mx+1, my+1, mz, rnew, e.getBlock().getWorld());
				}
				if(e.getBlock().getWorld().getBlockTypeIdAt(mx + 1, my - 1, mz) == 68){
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
		
		public void handleinput(int x, int y, int z, boolean powered, World w){
			BlockState b = w.getBlockAt(x, y, z).getState();
			if(!(b instanceof Sign)){
				return;
			}
			Sign sign = (Sign)b;
			String l1 = sign.getLine(0);
			String l2 = sign.getLine(1);
			String l3 = sign.getLine(2);
			String l4 = sign.getLine(3);
	
			////////////
			//Telepads//
			////////////
			if(l2.equalsIgnoreCase("[telepad]") && powered)
			{
				for(int i = 0; i < fort.getPadList().size(); i++)
				{
					if(fort.getPadList().get(i).getLocation().equals(b.getLocation()))
					{
						if(fort.getPadList().get(i).checkIntegrity())
						{
							for(int k = 0; k < fort.getPadList().size(); k++)
							{
								if(fort.getPadList().get(k).getRecBand().equals(fort.getPadList().get(i).getSendBand()) && k != i)
								{
									if(fort.getPadList().get(k).checkIntegrity())
									{
										if(fort.getPadList().get(k).getWidth() == fort.getPadList().get(i).getWidth() 
												&& fort.getPadList().get(k).getLength() == fort.getPadList().get(i).getLength()
												&& fort.getPadList().get(k).getHeight() == fort.getPadList().get(i).getHeight())
										{
											//Telepads are compatible, transport matter.
											//TODO: West facing telepads aren't working
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
											for(int w1 = 0; w1 < p.size(); w1++)
											{
												switch(fort.getPadList().get(i).getDirection())
												{
												case 0x2://+z = back, left = +x
													if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getFRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
															&& p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getBLTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
														tel = true;
														log.info("Player teleported.");//DEBUG
														p.get(w1).teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
														if(p.get(w1).getVehicle() != null)
														{
															p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
														}
													}
													break;
												case 0x3://-z = back, left = -x
													if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getFRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
															&& p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getBLTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area
														tel = true;
														log.info("Player teleported.");//DEBUG
														p.get(w1).teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
														if(p.get(w1).getVehicle() != null)
														{
															p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
														}
													}
													break;
												case 0x4://+x = back, left = -z
													if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getFRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
															&& p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getBLTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area
														tel = true;
														log.info("Player teleported.");//DEBUG
														p.get(w1).teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
														if(p.get(w1).getVehicle() != null)
														{
															p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
														}
													}
													break;
												case 0x5://-x = back, left = +z
													if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(k).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() > fort.getPadList().get(k).getFRTower().getLocation().getBlockX()
															&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(k).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(k).getHeight()
															&& p.get(w1).getLocation().getZ() < fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() > fort.getPadList().get(k).getBLTower().getLocation().getBlockZ())
													{
														//Player is in teleportation area
														tel = true;
														log.info("Player teleported.");//DEBUG
														p.get(w1).teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY()),
																fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
														if(p.get(w1).getVehicle() != null)
														{
															p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
														}
													}
													break;
												}
												if(!tel)
												{
													switch(fort.getPadList().get(k).getDirection())
													{
													case 0x2://+z = back, left = +x
														if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getFRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
																&& p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getBLTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
															log.info("Player teleported.");//DEBUG
															p.get(w1).teleport(new Location(fort.getPadList().get(i).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
															if(p.get(w1).getVehicle() != null)
															{
																p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(k).getLocation().getWorld(),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
															}
														}
														break;
													case 0x3://-z = back, left = -x
														if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getFRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
																&& p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getBLTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
															log.info("Player teleported.");//DEBUG
															p.get(w1).teleport(new Location(fort.getPadList().get(i).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
															if(p.get(w1).getVehicle() != null)
															{
																p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(i).getLocation().getWorld(),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
															}
														}
														break;
													case 0x4://+x = back, left = -z
														if(p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getFRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
																&& p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getBLTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
															log.info("Player teleported.");//DEBUG
															p.get(w1).teleport(new Location(fort.getPadList().get(i).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
															if(p.get(w1).getVehicle() != null)
															{
																p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(i).getLocation().getWorld(),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
															}
														}
														break;
													case 0x5://-x = back, left = +z
														if(p.get(w1).getLocation().getBlockX() < fort.getPadList().get(i).getFLTower().getLocation().getBlockX() && p.get(w1).getLocation().getBlockX() > fort.getPadList().get(i).getFRTower().getLocation().getBlockX()
																&& p.get(w1).getLocation().getBlockY() >= fort.getPadList().get(i).getFLTower().getLocation().getBlockY() && p.get(w1).getLocation().getBlockY() <= fort.getPadList().get(k).getLocation().getBlockY() + fort.getPadList().get(i).getHeight()
																&& p.get(w1).getLocation().getZ() < fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() && p.get(w1).getLocation().getZ() > fort.getPadList().get(i).getBLTower().getLocation().getBlockZ())
														{
															//Player is in teleportation area, teleport them to equivalent spot on connected telepad.
															log.info("Player teleported.");//DEBUG
															p.get(w1).teleport(new Location(fort.getPadList().get(i).getLocation().getWorld(),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getLocation().getX()),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getLocation().getY() ),
																	fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getLocation().getZ())));
															if(p.get(w1).getVehicle() != null)
															{
																p.get(w1).getVehicle().teleport(new Location(fort.getPadList().get(i).getLocation().getWorld(),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockX() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockX() - p.get(w1).getVehicle().getLocation().getX()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockY() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockY() - p.get(w1).getVehicle().getLocation().getY()),
																		fort.getPadList().get(i).getFLTower().getLocation().getBlockZ() + (fort.getPadList().get(k).getFLTower().getLocation().getBlockZ() - p.get(w1).getVehicle().getLocation().getZ())));
															}
														}
														break;
													}
												}
												else
												{
													tel = false;
												}
											}
										}
									}
									else
									{
										//not a valid telepad, remove k from list and destroy sign.
										fort.getPadList().remove(k);
										fort.getPadList().get(k).getLocation().getBlock().setTypeId(0);
										fort.getPadList().get(k).getLocation().getWorld().dropItem(new Location(fort.getPadList().get(k).getLocation().getWorld(),
												fort.getPadList().get(k).getLocation().getBlock().getX(), fort.getPadList().get(k).getLocation().getBlock().getY(),
												fort.getPadList().get(k).getLocation().getBlock().getZ()), new ItemStack(323, 1));
									}
								}
							}
						}
						else
						{
							fort.getPadList().remove(i);
							b.setTypeId(0);
							b.getWorld().dropItem(new Location(b.getWorld(), b.getBlock().getX(), b.getBlock().getY(), b.getBlock().getZ()), new ItemStack(323, 1));
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
							if(l.getBlock().getTypeId() == 68)
							{
								switch(l.getBlock().getData())
								{
								case 0x2:
									if(l.getWorld().getBlockTypeIdAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2) == 69)
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
									if(l.getWorld().getBlockTypeIdAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2) == 69)
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
									if(l.getWorld().getBlockTypeIdAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()) == 69)
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
									if(l.getWorld().getBlockTypeIdAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()) == 69)
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
			
			if(l2.equalsIgnoreCase("[Message]")){
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
			if(powered){
			if(l1.equalsIgnoreCase("[Send N]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext){
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send S]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						if(!sendoverwrite || !sendoverwritescommands){
							BlockState c = w.getBlockAt(p, y, z).getState();
							if(!(c instanceof Sign)){
								return;
							}
							s2 = (Sign)c;
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext){
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send E]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
						  if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext){
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send W]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
						  if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext){
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send U]")){
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
						n--;
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
						  if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(0, c1);
						s2.update();
						if(sendremovetext){
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send D]")){
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
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
						  if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(0).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(0).startsWith("[") && s2.getLine(0).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s.setLine(0, s2.getLine(0));
						s2.update();
						if(sendremovetext){
							s.setLine(0, "");
							s.update();
						}
						
					}
				}
			}
			if(l1.equalsIgnoreCase("[Send R]")){
				//east, north is to the right.
				if(w.getBlockAt(x, y, z).getData() == 0x2){
					
				}
				//west, south is to the right
				if(w.getBlockAt(x, y, z).getData() == 0x3){
					
				}
				//north, east is to the right
				if(w.getBlockAt(x, y, z).getData() == 0x4){
					
				}
				//south, west is to the right
				if(w.getBlockAt(x, y, z).getData() == 0x5){
					
				}
			}
			if(l1.equalsIgnoreCase("[Send L]")){
				//east, south is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x2){
					
				}
				//west, north is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x3){
					
				}
				//north, west is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x4){
					
				}
				//south, east is to the left
				if(w.getBlockAt(x, y, z).getData() == 0x5){
					
				}
			}
			
			if(l2.equalsIgnoreCase("[Send N]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c2.startsWith("[") && c2.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(1).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext){
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send S]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n--;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c2.startsWith("[") && c2.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(1).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext){
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send E]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n--;
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
						  if(c2.startsWith("[") && c2.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(1).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext){
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send W]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
						  if(c2.startsWith("[") && c2.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(1).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext){
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			//Send text from sign below to sign above
			if(l2.equalsIgnoreCase("[Send U]")){
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
						n--;
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
						/*	Check these for line 1 text sending.
						 * if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}*/
							if(c2.startsWith("[") && c2.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(1).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext){
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			if(l2.equalsIgnoreCase("[Send D]")){
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
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
						  if(c2.startsWith("[") && c2.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(1).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(1).startsWith("[") && s2.getLine(1).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(1, c2);
						s2.update();
						if(sendremovetext){
							s.setLine(1, "");
							s.update();
						}
						
					}
				}
			}
			
			if(l3.equalsIgnoreCase("[Send N]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
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
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(2).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext){
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send S]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n--;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
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
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(2).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext){
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send E]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n--;
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(2).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext){
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send W]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(2).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext){
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			//Send text from sign below to sign above
			if(l3.equalsIgnoreCase("[Send U]")){
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
						n--;
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(2).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext){
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			if(l3.equalsIgnoreCase("[Send D]")){
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
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
						  if(c3.startsWith("[") && c3.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(2).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(2).startsWith("[") && s2.getLine(2).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(2, c3);
						s2.update();
						if(sendremovetext){
							s.setLine(2, "");
							s.update();
						}
						
					}
				}
			}
			
			
			if(l4.equalsIgnoreCase("[Send N]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c4.startsWith("[") && c4.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(3).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext){
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send S]")){
				int n = x;
				int p = x;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n--;
					if(w.getBlockTypeIdAt(n, y, z) == 68){
						BlockState c = w.getBlockAt(n, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
							s = (Sign)c;
							c1 = s.getLine(0);
							c2 = s.getLine(1);
							c3 = s.getLine(2);
							c4 = s.getLine(3);
						
						if(!commandsend){
						  if(c4.startsWith("[") && c4.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(p, y, z) == 68){
						BlockState c = w.getBlockAt(p, y, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(3).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(3, c2);
						s2.update();
						if(sendremovetext){
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send W]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n++;
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
						  if(c4.startsWith("[") && c4.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(3).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext){
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send E]")){
				int n = z;
				int p = z;
				@SuppressWarnings("unused")
				String c1="", c2 = "", c3 ="", c4="";
				Sign s=null, s2 = null;
				boolean origin = false;
				boolean destination = false;
				for(int i = 0; i < sendlength; i++){
					//Sign found below
					if(!origin){
						n--;
					if(w.getBlockTypeIdAt(x, y, n) == 68){
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
						  if(c4.startsWith("[") && c4.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, y, p) == 68){
						BlockState c = w.getBlockAt(x, y, p).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(3).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext){
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			//Send text from sign below to sign above
			if(l4.equalsIgnoreCase("[Send U]")){
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
						n--;
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
						/*	Check these for line 1 text sending.
						 * if(c1.startsWith("[") && c1.endsWith("]")){
								return;
							}
							else if(c1.equalsIgnoreCase("AllDo:") || c1.equalsIgnoreCase("PlayerDo:") || c1.equalsIgnoreCase("ServerDo:")){
								return;
							}*/
							if(c4.startsWith("[") && c4.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p++;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(3).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext){
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			if(l4.equalsIgnoreCase("[Send D]")){
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
					if(w.getBlockTypeIdAt(x, n, z) == 68){
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
						  if(c4.startsWith("[") && c4.endsWith("]")){
								return;
							}
						}
							origin = true;
						
						
					}
					}
					//Sign found above
					if(!destination){
						p--;
					if(w.getBlockTypeIdAt(x, p, z) == 68){
						BlockState c = w.getBlockAt(x, p, z).getState();
						if(!(c instanceof Sign)){
							return;
						}
						s2 = (Sign)c;
						if(!sendoverwrite || !sendoverwritescommands){
							if(s2.getLine(3).trim().length() > 0 && !sendoverwrite){
								return;
							}
							if(!sendoverwritescommands && s2.getLine(3).startsWith("[") && s2.getLine(3).endsWith("]")){
								return;
							}
						}
						destination = true;
					}
					}
					if(destination && origin){
						s2.setLine(3, c4);
						s2.update();
						if(sendremovetext){
							s.setLine(3, "");
							s.update();
						}
						
					}
				}
			}
			
			//////////////
			//Trap Doors//
			//////////////
			if(l2.equalsIgnoreCase("[TrapDoor]")){
				//get integer for range on first line, error checking to avoid non-ints is done onsignchange.
				int r = Integer.parseInt(l1);
				if(r > fort.getMaxtraplength())
				{
					r = fort.getMaxtraplength();
				}
				int id = 0;
				Block d = w.getBlockAt(x, y, z);
				if(!(d.getType() == Material.WALL_SIGN)){
					return;
				}
				//east
				if(d.getData() == 0x2){
					id = w.getBlockTypeIdAt(x, y, z+1);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x, y, z+2+i) == id){
								w.getBlockAt(x, y, z+2+i).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x, y, z+2+k) == 0 || w.getBlockTypeIdAt(x, y, z+2+k) == 8 || w.getBlockTypeIdAt(x, y, z+2+k) == 9 || w.getBlockTypeIdAt(x, y, z+2+k) == 10 || w.getBlockTypeIdAt(x, y, z+2+k) == 11){
									w.getBlockAt(x, y, z+2+k).setTypeId(id);
								}
							}
						}
				}
				//west
				if(d.getData() == 0x3){
					id = w.getBlockTypeIdAt(x, y, z-1);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x, y, z-2-i) == id){
								w.getBlockAt(x, y, z-2-i).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x, y, z-2-k) == 0 || w.getBlockTypeIdAt(x, y, z-2-k) == 10 || w.getBlockTypeIdAt(x, y, z-2-k) == 11 || w.getBlockTypeIdAt(x, y, z-2-k) == 8 || w.getBlockTypeIdAt(x, y, z-2-k) == 9){
									w.getBlockAt(x, y, z-2-k).setTypeId(id);
								}
							}
						}
				}
				//north
				if(d.getData() == 0x4){
					id = w.getBlockTypeIdAt(x+1, y, z);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x+2+i, y, z) == id){
								w.getBlockAt(x+2+i, y, z).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x+2+k, y, z) == 0 || w.getBlockTypeIdAt(x+2+k, y, z) == 8 || w.getBlockTypeIdAt(x+2+k, y, z) == 9 || w.getBlockTypeIdAt(x+2+k, y, z) == 10 || w.getBlockTypeIdAt(x+2+k, y, z) == 11){
									w.getBlockAt(x+2+k, y, z).setTypeId(id);
								}
							}
						}
				}
				//south
				if(d.getData() == 0x5){
					id = w.getBlockTypeIdAt(x-1, y, z);
					if(powered){
					for(int i = 0; i < r; i++){
						if(w.getBlockTypeIdAt(x-2-i, y, z) == id){
							w.getBlockAt(x-2-i, y, z).setTypeId(0);
						}
					}
					}
					if(replacetrap && !powered){
						for(int k = 0; k < r; k++){
							if(w.getBlockTypeIdAt(x-2-k, y, z) == 0 || w.getBlockTypeIdAt(x-2-k, y, z) == 8 || w.getBlockTypeIdAt(x-2-k, y, z) == 9 || w.getBlockTypeIdAt(x-2-k, y, z) == 10 || w.getBlockTypeIdAt(x-2-k, y, z) == 11){
								w.getBlockAt(x-2-k, y, z).setTypeId(id);
							}
						}
					}
				}
			}
			/////////////////////////////////////
			//Upward trapdoor
			if(l2.equalsIgnoreCase("[UpTrapDoor]")){
				//get integer for range on first line, error checking to avoid non-ints is done onsignchange.
				int r = Integer.parseInt(l1);
				if(r > fort.getMaxtraplength())
				{
					r = fort.getMaxtraplength();
				}
				int id = 0;
				Block d = w.getBlockAt(x, y, z);
				if(!(d.getType() == Material.WALL_SIGN)){
					return;
				}
				//east
				if(d.getData() == 0x2){
					id = w.getBlockTypeIdAt(x, y, z+1);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x, y+1+i, z+1) == id){
								w.getBlockAt(x, y+1+i, z+1).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x, y+1+k, z+1) == 0 || w.getBlockTypeIdAt(x, y+1+k, z+1) == 8 || w.getBlockTypeIdAt(x, y+1+k, z+1) == 9 || w.getBlockTypeIdAt(x, y+1+k, z+1) == 10 || w.getBlockTypeIdAt(x, y+1+k, z+1) == 11){
									w.getBlockAt(x, y+1+k, z+1).setTypeId(id);
								}
							}
						}
				}
				//west
				if(d.getData() == 0x3){
					id = w.getBlockTypeIdAt(x, y, z-1);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x, y+1+i, z-1) == id){
								w.getBlockAt(x, y+1+i, z-1).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x, y+1+k, z-1) == 0 || w.getBlockTypeIdAt(x, y+1+k, z-1) == 10 || w.getBlockTypeIdAt(x, y+1+k, z-1) == 11 || w.getBlockTypeIdAt(x, y+1+k, z-1) == 8 || w.getBlockTypeIdAt(x, y+1+k, z-1) == 9){
									w.getBlockAt(x, y+1+k, z-1).setTypeId(id);
								}
							}
						}
				}
				//north
				if(d.getData() == 0x4){
					id = w.getBlockTypeIdAt(x+1, y, z);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x+1, y+1+i, z) == id){
								w.getBlockAt(x+1, y+1+i, z).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x+1, y+1+k, z) == 0 || w.getBlockTypeIdAt(x+1, y+1+k, z) == 8 || w.getBlockTypeIdAt(x+1, y+1+k, z) == 9 || w.getBlockTypeIdAt(x+1, y+1+k, z) == 10 || w.getBlockTypeIdAt(x+1, y+1+k, z) == 11){
									w.getBlockAt(x+1, y+1+k, z).setTypeId(id);
								}
							}
						}
				}
				//south
				if(d.getData() == 0x5){
					id = w.getBlockTypeIdAt(x-1, y, z);
					if(powered){
					for(int i = 0; i < r; i++){
						if(w.getBlockTypeIdAt(x-1, y+1+i, z) == id){
							w.getBlockAt(x-1, y+1+i, z).setTypeId(0);
						}
					}
					}
					if(replacetrap && !powered){
						for(int k = 0; k < r; k++){
							if(w.getBlockTypeIdAt(x-1, y+1+k, z) == 0 || w.getBlockTypeIdAt(x-1, y+1+k, z) == 8 || w.getBlockTypeIdAt(x-1, y+1+k, z) == 9 || w.getBlockTypeIdAt(x-1, y+1+k, z) == 10 || w.getBlockTypeIdAt(x-1, y+1+k, z) == 11){
								w.getBlockAt(x-1, y+1+k, z).setTypeId(id);
							}
						}
					}
				}
			}
			/////////////////////////////////////
			//Downward trapdoor
			if(l2.equalsIgnoreCase("[DownTrapDoor]")){
				//get integer for range on first line, error checking to avoid non-ints is done onsignchange.
				int r = Integer.parseInt(l1);
				if(r > fort.getMaxtraplength())
				{
					r = fort.getMaxtraplength();
				}
				int id = 0;
				Block d = w.getBlockAt(x, y, z);
				if(!(d.getType() == Material.WALL_SIGN)){
					return;
				}
				//east
				if(d.getData() == 0x2){
					id = w.getBlockTypeIdAt(x, y, z+1);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x, y-1-i, z+1) == id){
								w.getBlockAt(x, y-1-i, z+1).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x, y-1-k, z+1) == 0 || w.getBlockTypeIdAt(x, y-1-k, z+1) == 8 || w.getBlockTypeIdAt(x, y-1-k, z+1) == 9 || w.getBlockTypeIdAt(x, y-1-k, z+1) == 10 || w.getBlockTypeIdAt(x, y-1-k, z+1) == 11){
									w.getBlockAt(x, y-1-k, z+1).setTypeId(id);
								}
							}
						}
				}
				//west
				if(d.getData() == 0x3){
					id = w.getBlockTypeIdAt(x, y, z-1);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x, y-1-i, z-1) == id){
								w.getBlockAt(x, y-1-i, z-1).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x, y-1-k, z-1) == 0 || w.getBlockTypeIdAt(x, y-1-k, z-1) == 10 || w.getBlockTypeIdAt(x, y-1-k, z-1) == 11 || w.getBlockTypeIdAt(x, y-1-k, z-1) == 8 || w.getBlockTypeIdAt(x, y-1-k, z-1) == 9){
									w.getBlockAt(x, y-1-k, z-1).setTypeId(id);
								}
							}
						}
				}
				//north
				if(d.getData() == 0x4){
					id = w.getBlockTypeIdAt(x+1, y, z);
					if(powered){
						for(int i = 0; i < r; i++){
							if(w.getBlockTypeIdAt(x+1, y-1-i, z) == id){
								w.getBlockAt(x+1, y-1-i, z).setTypeId(0);
							}
						}
						}
						if(replacetrap && !powered){
							for(int k = 0; k < r; k++){
								if(w.getBlockTypeIdAt(x+1, y-1-k, z) == 0 || w.getBlockTypeIdAt(x+1, y-1-k, z) == 8 || w.getBlockTypeIdAt(x+1, y-1-k, z) == 9 || w.getBlockTypeIdAt(x+1, y-1-k, z) == 10 || w.getBlockTypeIdAt(x+1, y-1-k, z) == 11){
									w.getBlockAt(x+1, y-1-k, z).setTypeId(id);
								}
							}
						}
				}
				//south
				if(d.getData() == 0x5){
					id = w.getBlockTypeIdAt(x-1, y, z);
					if(powered){
					for(int i = 0; i < r; i++){
						if(w.getBlockTypeIdAt(x-1, y-1-i, z) == id){
							w.getBlockAt(x-1, y-1-i, z).setTypeId(0);
						}
					}
					}
					if(replacetrap && !powered){
						for(int k = 0; k < r; k++){
							if(w.getBlockTypeIdAt(x-1, y-1-k, z) == 0 || w.getBlockTypeIdAt(x-1, y-1-k, z) == 8 || w.getBlockTypeIdAt(x-1, y-1-k, z) == 9 || w.getBlockTypeIdAt(x-1, y-1-k, z) == 10 || w.getBlockTypeIdAt(x-1, y-1-k, z) == 11){
								w.getBlockAt(x-1, y-1-k, z).setTypeId(id);
							}
						}
					}
				}
			}
			///////////
			//Turrets//
			///////////
			if(l2.equalsIgnoreCase("[Turret]")){
			//	List<Player> p = fort.getServer().getOnlinePlayers();
				//Web Turret - Shoots web out to web length.
				if(l1.equalsIgnoreCase("web")){
					if(powered){
					//Sign facing east, so fire west, z increases
					if(w.getBlockAt(x, y, z).getData() == 0x2){
						//If sign is on ice...
					if(w.getBlockTypeIdAt(x, y, z+1)==webturretblockId || webturretblockId == 0){
								if(w.getBlockTypeIdAt(x, y, z+2+weblength) == 0 || w.getBlockTypeIdAt(x, y, z+2+weblength) == 8 || w.getBlockTypeIdAt(x, y, z+2+weblength) == 9){
									w.getBlockAt(x, y, z+2+weblength).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z+2+weblength, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x, y, z+2+weblength+1) == 0 || w.getBlockTypeIdAt(x, y, z+2+weblength+1) == 8 || w.getBlockTypeIdAt(x, y, z+2+weblength+1) == 9){
									w.getBlockAt(x, y, z+2+weblength+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z+2+weblength+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x, y, z+2+weblength-1) == 0 || w.getBlockTypeIdAt(x, y, z+2+weblength-1) == 8 || w.getBlockTypeIdAt(x, y, z+2+weblength-1) == 9){
									w.getBlockAt(x, y, z+2+weblength-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z+2+weblength-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-1, y, z+2+weblength) == 0 || w.getBlockTypeIdAt(x-1, y, z+2+weblength) == 8 || w.getBlockTypeIdAt(x-1, y, z+2+weblength) == 9){
									w.getBlockAt(x-1, y, z+2+weblength).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z+2+weblength, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+1, y, z+2+weblength) == 0 || w.getBlockTypeIdAt(x+1, y, z+2+weblength) == 8 || w.getBlockTypeIdAt(x+1, y, z+2+weblength) == 9){
									w.getBlockAt(x+1, y, z+2+weblength).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z+2+weblength, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+1, y, z+2+weblength+1) == 0 || w.getBlockTypeIdAt(x+1, y, z+2+weblength+1) == 8 || w.getBlockTypeIdAt(x+1, y, z+2+weblength+1) == 9){
									w.getBlockAt(x+1, y, z+2+weblength+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z+2+weblength+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-1, y, z+2+weblength+1) == 0 || w.getBlockTypeIdAt(x-1, y, z+2+weblength+1) == 8 || w.getBlockTypeIdAt(x-1, y, z+2+weblength+1) == 9){
									w.getBlockAt(x-1, y, z+2+weblength+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z+2+weblength+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+1, y, z+2+weblength-1) == 0 || w.getBlockTypeIdAt(x+1, y, z+2+weblength-1) == 8 || w.getBlockTypeIdAt(x+1, y, z+2+weblength-1) == 9){
									w.getBlockAt(x+1, y, z+2+weblength-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z+2+weblength-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-1, y, z+2+weblength-1) == 0 || w.getBlockTypeIdAt(x-1, y, z+2+weblength-1) == 8 || w.getBlockTypeIdAt(x-1, y, z+2+weblength-1) == 9){
									w.getBlockAt(x-1, y, z+2+weblength-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z+2+weblength-1, w, 30), webTime*20);
								}
					}
					}
					//Sign facing west, so fire east, z decreases
					if(w.getBlockAt(x, y, z).getData() == 0x3){
						//If sign is on ice...
						if(w.getBlockTypeIdAt(x, y, z-1)==webturretblockId || webturretblockId == 0){
							if(w.getBlockTypeIdAt(x, y, z-1)==webturretblockId || webturretblockId == 0){
								if(w.getBlockTypeIdAt(x, y, z-2-weblength) == 0 || w.getBlockTypeIdAt(x, y, z-2-weblength) == 8 || w.getBlockTypeIdAt(x, y, z-2-weblength) == 9){
									w.getBlockAt(x, y, z-2-weblength).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z-2-weblength, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x, y, z-2-weblength+1) == 0 || w.getBlockTypeIdAt(x, y, z-2-weblength+1) == 8 || w.getBlockTypeIdAt(x, y, z-2-weblength+1) == 9){
									w.getBlockAt(x, y, z-2-weblength+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z-2-weblength+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x, y, z-2-weblength-1) == 0 || w.getBlockTypeIdAt(x, y, z-2-weblength-1) == 8 || w.getBlockTypeIdAt(x, y, z-2-weblength-1) == 9){
									w.getBlockAt(x, y, z-2-weblength-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x, y, z-2-weblength-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-1, y, z-2-weblength) == 0 || w.getBlockTypeIdAt(x-1, y, z-2-weblength) == 8 || w.getBlockTypeIdAt(x-1, y, z-2-weblength) == 9){
									w.getBlockAt(x-1, y, z-2-weblength).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z-2-weblength, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+1, y, z-2-weblength) == 0 || w.getBlockTypeIdAt(x+1, y, z-2-weblength) == 8 || w.getBlockTypeIdAt(x+1, y, z-2-weblength) == 9){
									w.getBlockAt(x+1, y, z-2-weblength).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z-2-weblength, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+1, y, z-2-weblength+1) == 0 || w.getBlockTypeIdAt(x+1, y, z-2-weblength+1) == 8 || w.getBlockTypeIdAt(x+1, y, z-2-weblength+1) == 9){
									w.getBlockAt(x+1, y, z-2-weblength+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z-2-weblength+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-1, y, z-2-weblength+1) == 0 || w.getBlockTypeIdAt(x-1, y, z-2-weblength+1) == 8 || w.getBlockTypeIdAt(x-1, y, z-2-weblength+1) == 9){
									w.getBlockAt(x-1, y, z-2-weblength+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z-2-weblength+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+1, y, z-2-weblength-1) == 0 || w.getBlockTypeIdAt(x+1, y, z-2-weblength-1) == 8 || w.getBlockTypeIdAt(x+1, y, z-2-weblength-1) == 9){
									w.getBlockAt(x+1, y, z-2-weblength-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+1, y, z-2-weblength-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-1, y, z-2-weblength-1) == 0 || w.getBlockTypeIdAt(x-1, y, z-2-weblength-1) == 8 || w.getBlockTypeIdAt(x-1, y, z-2-weblength-1) == 9){
									w.getBlockAt(x-1, y, z-2-weblength-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-1, y, z-2-weblength-1, w, 30), webTime*20);
								}
					}
					}
					}
						
					//Sign facing north, so fire north, x increases
					if(w.getBlockAt(x, y, z).getData() == 0x4){
						//If sign is on ice...
						if(w.getBlockTypeIdAt(x+1, y, z)==webturretblockId || webturretblockId == 0){
							if(w.getBlockTypeIdAt(x+1, y, z)==webturretblockId || webturretblockId == 0){
								if(w.getBlockTypeIdAt(x+2+weblength, y, z) == 0 || w.getBlockTypeIdAt(x+2+weblength, y, z) == 8 || w.getBlockTypeIdAt(x+2+weblength, y, z) == 9){
									w.getBlockAt(x+2+weblength, y, z).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength, y, z, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength, y, z+1) == 0 || w.getBlockTypeIdAt(x+2+weblength, y, z+1) == 8 || w.getBlockTypeIdAt(x+2+weblength, y, z+1) == 9){
									w.getBlockAt(x+2+weblength, y, z+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength, y, z+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength, y, z-1) == 0 || w.getBlockTypeIdAt(x+2+weblength, y, z-1) == 8 || w.getBlockTypeIdAt(x+2+weblength, y, z-1) == 9){
									w.getBlockAt(x+2+weblength, y, z-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength, y, z-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength-1, y, z) == 0 || w.getBlockTypeIdAt(x+2+weblength-1, y, z) == 8 || w.getBlockTypeIdAt(x+2+weblength-1, y, z) == 9){
									w.getBlockAt(x+2+weblength-1, y, z).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength-1, y, z, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength+1, y, z) == 0 || w.getBlockTypeIdAt(x+2+weblength+1, y, z) == 8 || w.getBlockTypeIdAt(x+2+weblength+1, y, z) == 9){
									w.getBlockAt(x+2+weblength+1, y, z).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength+1, y, z, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength+1, y, z+1) == 0 || w.getBlockTypeIdAt(x+2+weblength+1, y, z+1) == 8 || w.getBlockTypeIdAt(x+2+weblength+1, y, z+1) == 9){
									w.getBlockAt(x+2+weblength+1, y, z+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength+1, y, z+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength-1, y, z+1) == 0 || w.getBlockTypeIdAt(x+2+weblength-1, y, z+1) == 8 || w.getBlockTypeIdAt(x+2+weblength-1, y, z+1) == 9){
									w.getBlockAt(x+2+weblength-1, y, z+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength-1, y, z+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength+1, y, z-1) == 0 || w.getBlockTypeIdAt(x+2+weblength+1, y, z-1) == 8 || w.getBlockTypeIdAt(x+2+weblength+1, y, z-1) == 9){
									w.getBlockAt(x+2+weblength+1, y, z-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength+1, y, z-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x+2+weblength-1, y, z-1) == 0 || w.getBlockTypeIdAt(x+2+weblength-1, y, z-1) == 8 || w.getBlockTypeIdAt(x+2+weblength-1, y, z-1) == 9){
									w.getBlockAt(x+2+weblength-1, y, z-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x+2+weblength-1, y, z-1, w, 30), webTime*20);
								}
					}
					}
					}
					//Sign facing south, so fire south, x decreases
					if(w.getBlockAt(x, y, z).getData() == 0x5){
						//If sign is on ice...
						if(w.getBlockTypeIdAt(x-1, y, z)==webturretblockId || webturretblockId == 0){
							if(w.getBlockTypeIdAt(x-1, y, z)==webturretblockId || webturretblockId == 0){
								if(w.getBlockTypeIdAt(x-2-weblength, y, z) == 0 || w.getBlockTypeIdAt(x-2-weblength, y, z) == 8 || w.getBlockTypeIdAt(x-2-weblength, y, z) == 9){
									w.getBlockAt(x-2-weblength, y, z).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength, y, z, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength, y, z+1) == 0 || w.getBlockTypeIdAt(x-2-weblength, y, z+1) == 8 || w.getBlockTypeIdAt(x-2-weblength, y, z+1) == 9){
									w.getBlockAt(x-2-weblength, y, z+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength, y, z+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength, y, z-1) == 0 || w.getBlockTypeIdAt(x-2-weblength, y, z-1) == 8 || w.getBlockTypeIdAt(x-2-weblength, y, z-1) == 9){
									w.getBlockAt(x-2-weblength, y, z-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength, y, z-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength-1, y, z) == 0 || w.getBlockTypeIdAt(x-2-weblength-1, y, z) == 8 || w.getBlockTypeIdAt(x-2-weblength-1, y, z) == 9){
									w.getBlockAt(x-2-weblength-1, y, z).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength-1, y, z, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength+1, y, z) == 0 || w.getBlockTypeIdAt(x-2-weblength+1, y, z) == 8 || w.getBlockTypeIdAt(x-2-weblength+1, y, z) == 9){
									w.getBlockAt(x-2-weblength+1, y, z).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength+1, y, z, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength+1, y, z+1) == 0 || w.getBlockTypeIdAt(x-2-weblength+1, y, z+1) == 8 || w.getBlockTypeIdAt(x-2-weblength+1, y, z+1) == 9){
									w.getBlockAt(x-2-weblength+1, y, z+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength+1, y, z+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength-1, y, z+1) == 0 || w.getBlockTypeIdAt(x-2-weblength-1, y, z+1) == 8 || w.getBlockTypeIdAt(x-2-weblength-1, y, z+1) == 9){
									w.getBlockAt(x-2-weblength-1, y, z+1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength-1, y, z+1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength+1, y, z-1) == 0 || w.getBlockTypeIdAt(x-2-weblength+1, y, z-1) == 8 || w.getBlockTypeIdAt(x-2-weblength+1, y, z-1) == 9){
									w.getBlockAt(x-2-weblength+1, y, z-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength+1, y, z-1, w, 30), webTime*20);
								}
								if(w.getBlockTypeIdAt(x-2-weblength-1, y, z-1) == 0 || w.getBlockTypeIdAt(x-2-weblength-1, y, z-1) == 8 || w.getBlockTypeIdAt(x-2-weblength-1, y, z-1) == 9){
									w.getBlockAt(x-2-weblength-1, y, z-1).setTypeId(30);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new RemoveBlock(x-2-weblength-1, y, z-1, w, 30), webTime*20);
								}
					}
					}
					}
				}
				}
				
				//Flame Turret - Shoots fire out to flamelength
				if(l1.equalsIgnoreCase("flame")){
					//Sign facing east, so fire west, z increases
					if(w.getBlockAt(x, y, z).getData() == 0x2){
						//If sign is on bloodstone...
					if(w.getBlockTypeIdAt(x, y, z+1)==flameturretblockId || flameturretblockId == 0){
						for(int i = 0; i < flamelength; i++){
							if(w.getBlockTypeIdAt(x, y, z+2+i) == 0){
								w.getBlockAt(x, y, z+2+i).setTypeId(51);
							}
							else{
								break;
							}
							}
					}
					}
					//Sign facing west, so fire east, z decreases
					if(w.getBlockAt(x, y, z).getData() == 0x3){
						//If sign is on bloodstone...
						if(w.getBlockTypeIdAt(x, y, z-1)==flameturretblockId || flameturretblockId == 0){
						for(int i = 0; i < flamelength; i++){
							if(w.getBlockTypeIdAt(x, y, z-2-i) == 0){
								w.getBlockAt(x, y, z-2-i).setTypeId(51);
							}
							else{
								break;
							}
						}
					}
					}
						
					//Sign facing north, so fire north, x increases
					if(w.getBlockAt(x, y, z).getData() == 0x4){
						//If sign is on bloodstone...
						if(w.getBlockTypeIdAt(x+1, y, z)==flameturretblockId || flameturretblockId == 0){
						for(int i = 0; i < flamelength; i++){
							if(w.getBlockTypeIdAt(x+2+i, y, z) == 0){
								w.getBlockAt(x+2+i, y, z).setTypeId(51);
							}
							else{
								break;
							}
							}
	
					}
					}
					//Sign facing south, so fire south, x decreases
					if(w.getBlockAt(x, y, z).getData() == 0x5){
						//If sign is on bloodstone...
						if(w.getBlockTypeIdAt(x-1, y, z)==flameturretblockId || flameturretblockId == 0){
						for(int i = 0; i < flamelength; i++){
							if(w.getBlockTypeIdAt(x-2-i, y, z) == 0){
								w.getBlockAt(x-2-i, y, z).setTypeId(51);
							}
							else{
								break;
							}
							}
	
					}
					}
				}
				//Arrow Turret - fires an arrow in direction it is facing.
				if(l1.equalsIgnoreCase("arrow") || l1.equalsIgnoreCase("default") || l1.equalsIgnoreCase("") || l1 == null){
					//Sign facing east, so fire west, z increases
					if(w.getBlockAt(x, y, z).getData() == 0x2){
						//If sign is on specified block type...
						if(w.getBlockTypeIdAt(x, y, z+1)==arrowturretblockId || arrowturretblockId == 0){
							//fire arrow
							Location target = new Location(w,x,y+1.5,z+10);
							Location origin = new Location(w,x,y+1.5,z+1.5);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), 1.0F, 7);
						}
					}
					//Sign facing west, so fire east, z decreases
					if(w.getBlockAt(x, y, z).getData() == 0x3){
						//If sign is on specified block type...
						if(w.getBlockTypeIdAt(x, y, z-1)==arrowturretblockId || arrowturretblockId == 0){
							//fire arrow
							Location target = new Location(w,x,y+1.5,z-10);
							Location origin = new Location(w,x,y+1.5,z-1.5);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), 1.0F, 7);
						}
					}
						
					//Sign facing north, so fire north, x increases
					if(w.getBlockAt(x, y, z).getData() == 0x4){
						//If sign is on specified block type...
						if(w.getBlockTypeIdAt(x+1, y, z)==arrowturretblockId || arrowturretblockId == 0){
							//fire arrow
							Location target = new Location(w,x+10,y+1.5,z);
							Location origin = new Location(w,x+1.5,y+1.5,z);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), 1.0F, 7);
						}
					}
					//Sign facing south, so fire south, x decreases
					if(w.getBlockAt(x, y, z).getData() == 0x5){
						//If sign is on specified block type...
						if(w.getBlockTypeIdAt(x-1, y, z)==arrowturretblockId || arrowturretblockId == 0){
							//fire arrow
							Location target = new Location(w,x-10,y+1.5,z);
							Location origin = new Location(w,x-1.5,y+1.5,z);
							w.spawnArrow(origin, new Vector(target.getX()-origin.getX(),target.getY()-origin.getY(),target.getZ()-origin.getZ()), 1.0F, 7);
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
			ItemStack si = new ItemStack(323, 1);
			Player player = e.getPlayer();
			//message sign
				if(e.getLine(1).equalsIgnoreCase("[Message]"))
				{
					if(fort.isPermissionsEnabled())
					{
						if(!player.hasPermission("fortification.msgsign") && !player.hasPermission("fortification.*"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build message signs.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
						if(w.getBlockAt(new Location(w,x,y,z-1)).getTypeId() == fort.getTelepadBlockId() || fort.getTeleblockId() == 0)
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockTypeIdAt(new Location(w,x-i,y,z-1)) == fort.getTelepadTowerId())
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockTypeIdAt(x-i-1,y,z-1) == fort.getTelepadSupportId() && w.getBlockTypeIdAt(x-i, y, z) == fort.getTelepadSupportId())
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x-i,y,z-1), new Location(w,x-i-1,y,z-1), new Location(w,x-i, y, z), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockTypeIdAt(x-i,y,z-1-k) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x-i,y,z-2-k) == fort.getTelepadSupportId() 
														&& w.getBlockTypeIdAt(x-i-1,y,z-1-k) == fort.getTelepadSupportId())
												{
													bLeft = new TelepadTower(new Location(w,x-i,y,z-1-k), new Location(w,x-i,y,z-2-k), new Location(w,x-i-1,y,z-1-k), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockTypeIdAt(x-i+j,y,z-1-k) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x-i+j,y,z-2-k) == fort.getTelepadSupportId() 
																	&& w.getBlockTypeIdAt(x-i+1+j,y,z-1-k) == fort.getTelepadSupportId())
															{
																bRight = new TelepadTower(new Location(w,x-i+j,y,z-1-k), new Location(w, x-i+1+j,y,z-1-k), new Location(w,x-i+j,y,z-2-k), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right tower integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					case 0x2://+z = back, left = +x
						if(w.getBlockAt(new Location(w,x,y,z+1)).getTypeId() == fort.getTelepadBlockId() || fort.getTeleblockId() == 0)
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockTypeIdAt(new Location(w,x+i,y,z+1)) == fort.getTelepadTowerId())
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockTypeIdAt(x+i+1,y,z+1) == fort.getTelepadSupportId() && w.getBlockTypeIdAt(x+i, y, z) == fort.getTelepadSupportId())
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x+i,y,z+1), new Location(w,x+i+1,y,z+1), new Location(w,x+i, y, z), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockTypeIdAt(x+i,y,z+1+k) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x+i,y,z+2+k) == fort.getTelepadSupportId() 
														&& w.getBlockTypeIdAt(x+i+1,y,z+1+k) == fort.getTelepadSupportId())
												{
													bLeft = new TelepadTower(new Location(w,x+i,y,z+1+k), new Location(w,x+i,y,z+2+k), new Location(w,x+i+1,y,z+1+k), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockTypeIdAt(x+i-j,y,z+1+k) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x+i-j,y,z+2+k) == fort.getTelepadSupportId() 
																	&& w.getBlockTypeIdAt(x+i-1-j,y,z+1+k) == fort.getTelepadSupportId())
															{
																bRight = new TelepadTower(new Location(w,x+i-j,y,z+1+k), new Location(w, x+i-j,y,z+2+k), new Location(w,x+i-1-j,y,z+1+k), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right tower integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					case 0x5://-x = back, left = +z
						if(w.getBlockAt(new Location(w,x-1,y,z)).getTypeId() == fort.getTelepadBlockId() || fort.getTeleblockId() == 0)
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockTypeIdAt(new Location(w,x-1,y,z+i)) == fort.getTelepadTowerId())
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockTypeIdAt(x-1,y,z+i+1) == fort.getTelepadSupportId() && w.getBlockTypeIdAt(x, y, z+i) == fort.getTelepadSupportId())
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x-1,y,z+i), new Location(w,x-1,y,z+i+1), new Location(w,x, y, z+i), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockTypeIdAt(x-k-1,y,z+i) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x-k-1,y,z+i+1) == fort.getTelepadSupportId() 
														&& w.getBlockTypeIdAt(x-k-2,y,z+i) == fort.getTelepadSupportId())
												{
													bLeft = new TelepadTower(new Location(w,x-k-1,y,z+i), new Location(w,x-k-1,y,z+i+1), new Location(w,x-k-2,y,z+i), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockTypeIdAt(x-k-1,y,z+i-j) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x-k-1,y,z+i-j-1) == fort.getTelepadSupportId() 
																	&& w.getBlockTypeIdAt(x-k-2,y,z+i-j) == fort.getTelepadSupportId())
															{
																bRight = new TelepadTower(new Location(w,x-k-1,y,z+i-j), new Location(w, x-k-1,y,z+i-j-1), new Location(w,x-k-2,y,z+i-j), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right tower integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					case 0x4://+x = back, left = -z
						if(w.getBlockAt(new Location(w,x+1,y,z)).getTypeId() == fort.getTelepadBlockId() || fort.getTeleblockId() == 0)
						{
							//find front left tower
							for(int i = 0; i < teleLength; i++)
							{
								if(w.getBlockTypeIdAt(new Location(w,x+1,y,z-i)) == fort.getTelepadTowerId())
								{
									//still a telepad, keep going, but check support height while we are here
									if(w.getBlockTypeIdAt(x+1,y,z-i-1) == fort.getTelepadSupportId() && w.getBlockTypeIdAt(x, y, z-i) == fort.getTelepadSupportId())
									{
										//Found front left tower
										fLeft = new TelepadTower(new Location(w,x+1,y,z-i), new Location(w,x+1,y,z-i-1), new Location(w,x, y, z-i), fort);
										if(!fLeft.checkIntegrity())
										{
											player.sendMessage(ChatColor.RED + "Telepad front left tower integrity compromised.");
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
											player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
											return;
										}
										else
										{
											//find back left tower
											for(int k = 0; k < teleLength; k++)
											{
												if(w.getBlockTypeIdAt(x+1+k,y,z-i) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x+k+1,y,z-i-1) == fort.getTelepadSupportId() 
														&& w.getBlockTypeIdAt(x+2+k,y,z-i) == fort.getTelepadSupportId())
												{
													bLeft = new TelepadTower(new Location(w,x+k+1,y,z-i), new Location(w,x+k+1,y,z-i-1), new Location(w,x+k+2,y,z-i), fort);
													if(!bLeft.checkIntegrity())
													{
														player.sendMessage(ChatColor.RED + "Telepad back left tower integrity compromised.");
														player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
														player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
														return;
													}
													else
													{
														//find back right tower
														for(int j = 0; j < teleLength; j++)
														{
															if(w.getBlockTypeIdAt(x+k+1,y,z-i+j) == fort.getTelepadTowerId() && w.getBlockTypeIdAt(x+k+1,y,z-i+j+1) == fort.getTelepadSupportId() 
																	&& w.getBlockTypeIdAt(x+k+2,y,z-i+j) == fort.getTelepadSupportId())
															{
																bRight = new TelepadTower(new Location(w,x+k+1,y,z-i+j), new Location(w, x+k+1,y,z-i+j+1), new Location(w,x+k+2,y,z-i+j), fort);
																if(!bRight.checkIntegrity())
																{
																	player.sendMessage(ChatColor.RED + "Telepad back right integrity compromised.");
																	player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																		return;
																	}
																	Telepad t;
																	t = new Telepad(fort, e.getBlock().getLocation(), fLeft, bLeft, bRight, fRight, e.getBlock().getData(), e.getLine(2), e.getLine(3));
																	
																	if(!t.checkIntegrity())
																	{
																		player.sendMessage(ChatColor.RED + "Telepad integrity compromised.");
																		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You must use a block of id: " + fort.getTeleblockId() + " behind the [Telepad] sign");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getTransmitterCost()){
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getTransmitterCost());
									return;
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getTransmitterCost() + ")");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.shield.teleblock") && !player.hasPermission("fortification.shield.chest"))
							{
								player.sendMessage(ChatColor.RED + "You do not have permission to build shields.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						if(e.getLine(0).equalsIgnoreCase("teleblock"))
						{
							if(fort.isPermissionsEnabled())
							{
								if(!player.hasPermission("fortification.shield.*") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.shield.teleblock"))
								{
									player.sendMessage(ChatColor.RED + "You do not have permission to build teleblock shields.");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							if(!player.hasPermission("fortification.sendsign") && !player.hasPermission("fortification.*")){
								player.sendMessage(ChatColor.RED + "You do not have permission to build send signs.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						if(fort.isEcon()){
							if(e.getPlayer() != null){
							if(fort.getSendsignCost() > 0){
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getSendsignCost()){
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getSendsignCost());
									return;
								}
							else{
								player.sendMessage(ChatColor.RED + "You do not enough money for this purchase (" + fort.getSendsignCost() + ")");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
							}
							}
						}
					}
			//Turrets
				if(e.getLine(1).equalsIgnoreCase("[Turret]")){
					if(fort.isPermissionsEnabled())
					{
						if(!player.hasPermission("fortification.turret.flame") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*") && !player.hasPermission("fortification.turret.web") && !player.hasPermission("fortification.turret.arrow"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission to build turrets.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
					else{
					//if flame turret check permissions
					if(e.getLine(0).equalsIgnoreCase("flame")){
						if(!player.hasPermission("fortification.turret.flame") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*")){
							player.sendMessage(ChatColor.RED + "You do not have permission to build flame turrets.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						if(fort.isEcon()){
							if(e.getPlayer() != null){
							if(fort.getFlameturretCost() > 0){
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getFlameturretCost()){
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getFlameturretCost());
									return;
								}
							else{
								player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getFlameturretCost() + ")");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
							}
							}
						}
					}
					//web turret
					else if(e.getLine(0).equalsIgnoreCase("web")){
						if(!player.hasPermission("fortification.turret.web") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*")){
							player.sendMessage(ChatColor.RED + "You do not have permission to build web turrets.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						if(fort.isEcon()){
							if(e.getPlayer() != null){
							if(fort.getWebturretCost() > 0){
								if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getWebturretCost()){
									fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getWebturretCost());
									return;
								}
							else{
								player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getWebturretCost() + ")");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
							}
						}
						}
					}
					//arrow turret
					else if(e.getLine(0).equalsIgnoreCase("arrow") || e.getLine(0).equalsIgnoreCase("default") || e.getLine(0).equalsIgnoreCase("") || e.getLine(0) == null){
						if(!player.hasPermission("fortification.turret.arrow") && !player.hasPermission("fortification.*") && !player.hasPermission("fortification.turret.*")){
							player.sendMessage(ChatColor.RED + "You do not have permission to build arrow turrets.");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						if(fort.isEcon()){
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
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
							}
						}
						}
					}
					else {
						//turret type is invalid, tell user
						player.sendMessage(ChatColor.RED + "Invalid turret type, type /fort turret for a list of turret types.");
						player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
						player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else
						{
							String filter = e.getLine(0);
							if(!filter.equalsIgnoreCase("playerdetect") && !filter.equalsIgnoreCase("playerignore") && !filter.equalsIgnoreCase("factiondetect")
									&& !filter.equalsIgnoreCase("groupdetect") && !filter.equalsIgnoreCase("factionignore") && !filter.equalsIgnoreCase("groupignore")
									&& !filter.equalsIgnoreCase("default") && !filter.equalsIgnoreCase("") && !filter.equalsIgnoreCase(" ") && !(filter == null)
									&& !filter.equalsIgnoreCase("weapondetect") && !filter.equalsIgnoreCase("weaponignore") && !filter.equalsIgnoreCase("itemdetect")
									&& !filter.equalsIgnoreCase("itemignore") && !filter.equalsIgnoreCase("areaalert") && !filter.equalsIgnoreCase("factionalert") 
									&& !filter.equalsIgnoreCase("tooldetect") && !filter.equalsIgnoreCase("toolignore") && !filter.equalsIgnoreCase("towndetect")
									&& !filter.equalsIgnoreCase("townignore") && !filter.equalsIgnoreCase("nationignore") && !filter.equalsIgnoreCase("nationdetect")
									&& !filter.equalsIgnoreCase("townalert") && !filter.equalsIgnoreCase("nationalert")
									&& !filter.equalsIgnoreCase("allydetect") && !filter.equalsIgnoreCase("enemydetect") && !filter.equalsIgnoreCase("healthrange")
									&& !filter.equalsIgnoreCase("armorDetect") && !filter.equalsIgnoreCase("armorIgnore"))
							{
								//invalid sensor type
								player.sendMessage(ChatColor.RED + "Invalid sensor type, type /fort sensor for a list of sensor types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
							else
							{
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
												player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
												player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
												return;
											}
										}
									}
								}
								if(e.getLine(0).equalsIgnoreCase("factionalert"))
								{
									if(fort.isMsgOnlyBuilder()){
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
									if(fort.isMsgOnlyBuilder()){
									if(fort.isTownyEnabled()){
										Nation t = null;
										try {
											t = TownyUniverse.getDataSource().getResident(e.getPlayer().getName()).getTown().getNation();
										} catch (Exception e2) {
											e2.printStackTrace();
										}
										if(e.getLine(2).equalsIgnoreCase("") || e.getLine(2) == null){
											
										}
										else{
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
																player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																return;
															}
													}
												}
											} catch (Exception e1) {
												e1.printStackTrace();
											}
										}
										if(e.getLine(3).equalsIgnoreCase("") || e.getLine(3) == null){
										
										}
										else{
											try {
												if(TownyUniverse.getDataSource().getTown(e.getLine(3)) != null)
												{
													if(t.equals(TownyUniverse.getDataSource().getTown(e.getLine(3)))){
														//same nation, this is fine
													}
													else{
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
															if(!temp2){
																player.sendMessage(ChatColor.RED + "One of the nations you listed is either neutral or an enemy to your own.");
																player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																return;
															}
													}
												}
											} catch (Exception e1) {
												e1.printStackTrace();
											}
										}
									}
									}
								}
								if(e.getLine(0).equalsIgnoreCase("townalert"))
								{
									if(fort.isMsgOnlyBuilder()){
									if(fort.isTownyEnabled()){
										Towny town = (Towny)towny;
										if(town == null)
										{
											return;
										}
										Town t = null;
										try {
											t = TownyUniverse.getDataSource().getResident(e.getPlayer().getName()).getTown();
										} catch (Exception e2) {
											e2.printStackTrace();
										}
										if(e.getLine(2).equalsIgnoreCase("") || e.getLine(2) == null){
											
										}
										else{
											try {
												if(TownyUniverse.getDataSource().getTown(e.getLine(2)) != null)
												{
													if(t.equals(TownyUniverse.getDataSource().getTown(e.getLine(2)))){
														//then this is fine
													}
													else{
														if(TownyUniverse.getDataSource().getTown(e.getLine(2)).getNation().equals(t.getNation())){
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
															if(!temp){
																player.sendMessage(ChatColor.RED + "One of the towns you listed is not within the same nation as you or a nation allied to yours.");
																player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																return;
															}
														}
													}
												}
											} catch (Exception e1) {
												e1.printStackTrace();
											}
										}
										if(e.getLine(3).equalsIgnoreCase("") || e.getLine(3) == null){
										
										}
										else{
											try {
												if(TownyUniverse.getDataSource().getTown(e.getLine(3)) != null)
												{
													if(t.equals(TownyUniverse.getDataSource().getTown(e.getLine(3)))){
														//then this is fine
													}
													else{
														if(TownyUniverse.getDataSource().getTown(e.getLine(3)).getNation().equals(t.getNation())){
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
															if(!temp2){
																player.sendMessage(ChatColor.RED + "One of the towns you listed is not within the same nation as you or a nation allied to yours.");
																player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
																player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
																return;
															}
														}
													}
												}
											} catch (Exception e1) {
												e1.printStackTrace();
											}
										}
									}
									}
								}
								if(e.getLine(0).equalsIgnoreCase("itemdetect") || e.getLine(0).equalsIgnoreCase("itemignore"))
								{
									try{
										if(e.getLine(2) != null && e.getLine(2) != "")
										{
										Integer.parseInt(e.getLine(2));
										}
										if(e.getLine(3) != null && e.getLine(3) != "")
										{
										Integer.parseInt(e.getLine(3));
										}
									}
									catch(Exception ex){
										player.sendMessage(ChatColor.RED + "The 3rd and 4th lines must contain the integer id of an item.");
										player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
										player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
										return;
									}
								}
							}
						}
					}
			//Trap door
				int temp;
					if(e.getLine(1).equalsIgnoreCase("[TrapDoor]") || e.getLine(1).equalsIgnoreCase("[UpTrapdoor]") || e.getLine(1).equalsIgnoreCase("[DownTrapdoor]")){
						if(!player.hasPermission("fortification.trapdoor") && !player.hasPermission("fortification.*") && fort.isPermissionsEnabled()){
							player.sendMessage(ChatColor.RED + "You do not have permission to build a trap door");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
							player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
							return;
						}
						else{
							try{
								temp = Integer.parseInt(e.getLine(0));
							}
							catch(Exception ex){
								player.sendMessage(ChatColor.RED + "The first line must contain an integer value.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
								if(temp > maxtraplength){
									player.sendMessage(ChatColor.RED + "The length of the trap door can not exceed " + Integer.toString(maxtraplength));
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
									player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
								if(temp <= 0){
									player.sendMessage(ChatColor.RED + "Trap doors must have a length greater than 0.");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
									player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
						}
						//east
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x2){
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++){
								if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+1) == trapblocks[i]){
									validblock = true;
									break;
								}
							}
							if(!validblock){
								player.sendMessage(ChatColor.RED + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						//west
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x3){
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++){
								if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-1) == trapblocks[i]){
									validblock = true;
									break;
								}
							}
							if(!validblock){
								player.sendMessage(ChatColor.RED + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						//north
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x4){
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++){
								if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX()+1, e.getBlock().getY(), e.getBlock().getZ()) == trapblocks[i]){
									validblock = true;
									break;
								}
							}
							if(!validblock){
								player.sendMessage("&c" + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
						}
						//south
						if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x5){
							boolean validblock = false;
							for(int i = 0; i < trapblocks.length; i++){
								if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX()-1, e.getBlock().getY(), e.getBlock().getZ()) == trapblocks[i]){
									validblock = true;
									break;
								}
							}
							if(!validblock){
								player.sendMessage(ChatColor.RED + "The block type you are trying to use is not supported, type /fort trapdoor for a list of valid block types.");
								player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
								player.getWorld().dropItem(new Location(player.getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
								return;
							}
							if(fort.isEcon()){
								if(e.getPlayer() != null){
								if(fort.getTrapdoorCost() > 0){
									if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getTrapdoorCost()){
										fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getTrapdoorCost());
										return;
									}
								else{
									player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getTrapdoorCost() + ")");
									player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
									player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
									return;
								}
								}
								}
							}
						}
					}
			if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()) == 68){
				if(e.getLine(1).equalsIgnoreCase("[Equals]")){
					if(!player.hasPermission("fortification.equalsign") && !player.hasPermission("fortification.*") && fort.isPermissionsEnabled()){
						player.sendMessage(ChatColor.RED + "You do not have permission to build equals signs.");
						player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
						player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
						return;
					}
					if(fort.isEcon()){
						if(e.getPlayer() != null){
						if(fort.getEqualsignCost() > 0){
							if(fort.getEconomy().getBalance(e.getPlayer().getName()) >= fort.getEqualsignCost()){
								fort.getEconomy().withdrawPlayer(e.getPlayer().getName(),fort.getEqualsignCost());
								return;
							}
						else{
							player.sendMessage(ChatColor.RED + "You do not have enough money for this purchase (" + fort.getEqualsignCost() + ")");
							player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
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
					if(e.getLine(1).equalsIgnoreCase("[Equals]")){
							//if equal turn on redstone
							if(e.getLine(0).equalsIgnoreCase(e.getLine(2))){
								if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).getData() == 0x2){
									if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+2) == 69){
										int d = e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+2).getData();
										int nd = d | 0x8;
										if(nd != d){
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()+2).setData((byte) nd);
											//May have to update block physics here somehow?
										}
									}
								}
								else if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x3){
									if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-2) == 69){
										int d = player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-2).getData();
										int nd = d | 0x8;
										if(nd != d){
											e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()-2).setData((byte)nd);
										//	etc.getServer().updateBlockPhysics(sign.getX(), sign.getY(), sign.getZ()-2, nd);
										}
									}
								}
								else if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x4){
									if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getX()+2, e.getBlock().getY(), e.getBlock().getZ()) == 69){
										int d = e.getBlock().getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(), e.getBlock().getZ()).getData();
										int nd = d | 0x8;
										if(nd != d){
											e.getBlock().getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(), e.getBlock().getZ()).setData((byte)nd);
										//	etc.getServer().updateBlockPhysics(sign.getX()+2, sign.getY(), sign.getZ(), nd);
										}
									}
								}
								else if(e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x5){
									if(e.getBlock().getWorld().getBlockTypeIdAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()) == 69){
										int d = e.getBlock().getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).getData();
										int nd = d | 0x8;
										if(nd != d){
											e.getBlock().getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).setData((byte)nd);
										//	etc.getServer().updateBlockPhysics(sign.getX()-2, sign.getY(), sign.getZ(), nd);
										}
									}
								}
							}
							//if not equal turn off redstone
							else{
								if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x2){
									if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()+2) == 69){
										int d = player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()+2).getData();
										int nd = d & 0x7;
										if(nd != d){
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()+2).setData((byte)nd);
									//		etc.getServer().updateBlockPhysics(sign.getX(), sign.getY(), sign.getZ()+2, nd);
										}
									}
								}
								else if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x3){
									if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()-2) == 69){
										int d = player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()-2).getData();
										int nd = d & 0x7;
										if(nd != d){
											player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()-2).setData((byte)nd);
									//		etc.getServer().updateBlockPhysics(sign.getX(), sign.getY(), sign.getZ()-2, nd);
										}
									}
								}
								else if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x4){
									if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX()+2, e.getBlock().getY(),e.getBlock().getZ()) == 69){
										int d = player.getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(),e.getBlock().getZ()).getData();
										int nd = d & 0x7;
										if(nd != d){
											player.getWorld().getBlockAt(e.getBlock().getX()+2, e.getBlock().getY(),e.getBlock().getZ()).setData((byte)nd);
									//		etc.getServer().updateBlockPhysics(sign.getX()+2, sign.getY(), sign.getZ(), nd);
										}
									}
								}
								else if(player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(),e.getBlock().getZ()).getData() == 0x5){
									if(player.getWorld().getBlockTypeIdAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()) == 69){
										int d = player.getWorld().getBlockAt(e.getBlock().getX()-2, e.getBlock().getY(),e.getBlock().getZ()).getData();
										int nd = d & 0x7;
										if(nd != d){
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