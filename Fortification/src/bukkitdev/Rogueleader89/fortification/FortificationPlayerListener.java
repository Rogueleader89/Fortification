package bukkitdev.Rogueleader89.fortification;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UPlayer;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class FortificationPlayerListener implements Listener 
{
	private Fortification fort;
//	private boolean end = false;
	private int sensorlength = 8;
	private int teleblockrange;
	private String teleblockstring;
	private String teleblockId;
	private int chestrange = 5;
	private String chestshieldId = "0";
	private int sensorBroadcastDist = 0;
	private ArrayList<FortPlayer> fpList = new ArrayList<FortPlayer>();
	Plugin towny;
	
	public FortificationPlayerListener(Fortification plugin) 
	{
		fort = plugin;
		sensorlength = fort.getSensorlength();
		teleblockrange = fort.getTeleblockrange();
		teleblockstring = fort.getTeleblockstring();
		teleblockId = fort.getTeleblockId();
		chestshieldId = fort.getChestshieldId();
		sensorBroadcastDist = fort.getSensorBroadcastDist();
		if(fort.isTownyEnabled())
		{
			towny = fort.getServer().getPluginManager().getPlugin("Towny");
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(!fpList.isEmpty())
		{
			int size = fpList.size();
			boolean pExists = false;
			for(int i = 0; i < size; i++)
			{
				if(fpList.get(i).getName().equalsIgnoreCase(e.getPlayer().getName()))
				{
					pExists = true;
						break;
				}
			}
			if(!pExists)
			{
				fpList.add(new FortPlayer(e.getPlayer()));
			}
		}
		else
		{
			fpList.add(new FortPlayer(e.getPlayer()));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(!fpList.isEmpty())
		{
			int size = fpList.size();
			for(int i = 0; i < size; i++)
			{
				if(fpList.get(i).getName().equalsIgnoreCase(e.getPlayer().getName()))
				{
					fpList.remove(i);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		//chest shield - triggers redstone on attempted chest use
		//Note, these don't work in any direction they are facing at all....
		if(((e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getAction() == Action.LEFT_CLICK_BLOCK))
		{
			if(e.getClickedBlock().getType().equals(Material.CHEST))
			{
				ArrayList<Sign> signlist = searchradius(e.getClickedBlock().getLocation().getBlockX(), e.getClickedBlock().getLocation().getBlockY(), e.getClickedBlock().getLocation().getBlockZ(), chestrange, e.getClickedBlock().getWorld());
				for(int i = 0; i < signlist.size(); i++)
				{
					if(signlist.get(i).getLine(1).equalsIgnoreCase("[Shield]"))
					{
						if(signlist.get(i).getLine(0).equalsIgnoreCase("chest"))
						{
							if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x2)
							{
								if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+1).getType().toString().equalsIgnoreCase(chestshieldId) ||
										chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).getType().equals(Material.LEVER))
									{
										int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2, e.getPlayer().getWorld()),50);
										}
									}
								}
							}
							else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x3)
							{
								if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-1).getType().toString().equalsIgnoreCase(chestshieldId) ||
										chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).getType().equals(Material.LEVER))
									{
										int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2, e.getPlayer().getWorld()),50);
										}
									}
								}
							}
							else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x4)
							{
								if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+1, signlist.get(i).getY(), signlist.get(i).getZ()).getType().toString().equalsIgnoreCase(chestshieldId) ||
										chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).getType().equals(Material.LEVER))
									{
										int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ(), e.getPlayer().getWorld()),50);
										}
									}
								}
							}
							else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x5)
							{
								if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-1, signlist.get(i).getY(), signlist.get(i).getZ()).getType().toString().equalsIgnoreCase(chestshieldId) ||
										chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).getType().equals(Material.LEVER))
									{
										int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ(), e.getPlayer().getWorld()),50);
										}
									}
								}
							}
						}
						//playerchest shield - trigger redstone on attempted chest use if player in question is not listed on l3 or l4 of sign.
						else if(signlist.get(i).getLine(0).equalsIgnoreCase("playerchest"))
						{
							if(!e.getPlayer().getName().equalsIgnoreCase(signlist.get(i).getLine(2)) && !e.getPlayer().getName().equalsIgnoreCase(signlist.get(i).getLine(3)))
							{
								if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x2)
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+1).getType().toString().equalsIgnoreCase(chestshieldId) ||
											chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).getType().equals(Material.LEVER))
										{
											int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).setData((byte) nd);
												fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2, e.getPlayer().getWorld()),50);
											}
										}
									}
								}
								else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x3)
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-1).getType().toString().equalsIgnoreCase(chestshieldId) ||
											chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).getType().equals(Material.LEVER))
										{
											int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).setData((byte) nd);
												fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2, e.getPlayer().getWorld()),50);
											}
										}
									}
								}
								else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x4)
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+1, signlist.get(i).getY(), signlist.get(i).getZ()).getType().toString().equalsIgnoreCase(chestshieldId) ||
											chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).getType().equals(Material.LEVER))
										{
											int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).setData((byte) nd);
												fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ(), e.getPlayer().getWorld()),50);
											}
										}
									}
								}
								else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x5)
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-1, signlist.get(i).getY(), signlist.get(i).getZ()).getType().toString().equalsIgnoreCase(chestshieldId) ||
											chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).getType().equals(Material.LEVER))
										{
											int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).getData();
											int nd = d | 0x8;
											if(nd != d)
											{
												e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).setData((byte) nd);
												fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ(), e.getPlayer().getWorld()),50);
											}
										}
									}
								}
							}
						}
						//factionchest shield - trigger redstone on attempted chest use if player is not a member of a faction listed on l3 or l4
						else if(signlist.get(i).getLine(0).equalsIgnoreCase("factionchest"))
						{
							if(fort.isFactionsEnabled())
							{
								UPlayer me = (UPlayer)e.getPlayer();
								if(!me.getFaction().getName().equalsIgnoreCase(signlist.get(i).getLine(2)) && !me.getFaction().getName().equalsIgnoreCase(signlist.get(i).getLine(3)))
								{
									if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x2)
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+1).getType().toString().equalsIgnoreCase(chestshieldId) ||
												chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
										{
											if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).getType().equals(Material.LEVER))
											{
												int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).getData();
												int nd = d | 0x8;
												if(nd != d)
												{
													e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2).setData((byte) nd);
													fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()+2, e.getPlayer().getWorld()),50);
												}
											}
										}
									}
									else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x3)
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-1).getType().toString().equalsIgnoreCase(chestshieldId) ||
												chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
										{
											if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).getType().equals(Material.LEVER))
											{
												int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).getData();
												int nd = d | 0x8;
												if(nd != d)
												{
													e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2).setData((byte) nd);
													fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()-2, e.getPlayer().getWorld()),50);
												}
											}
										}
									}
									else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x4)
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+1, signlist.get(i).getY(), signlist.get(i).getZ()).getType().toString().equalsIgnoreCase(chestshieldId) ||
												chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
										{
											if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).getType().equals(Material.LEVER))
											{
												int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).getData();
												int nd = d | 0x8;
												if(nd != d)
												{
													e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ()).setData((byte) nd);
													fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX()+2, signlist.get(i).getY(), signlist.get(i).getZ(), e.getPlayer().getWorld()),50);
												}
											}
										}
									}
									else if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX(), signlist.get(i).getY(), signlist.get(i).getZ()).getData() == 0x5)
									{
										if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-1, signlist.get(i).getY(), signlist.get(i).getZ()).getType().toString().equalsIgnoreCase(chestshieldId) ||
												chestshieldId.equalsIgnoreCase(Material.AIR.toString()))
										{
											if(e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).getType().equals(Material.LEVER))
											{
												int d = e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).getData();
												int nd = d | 0x8;
												if(nd != d)
												{
													e.getPlayer().getWorld().getBlockAt(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ()).setData((byte) nd);
													fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(signlist.get(i).getX()-2, signlist.get(i).getY(), signlist.get(i).getZ(), e.getPlayer().getWorld()),50);
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
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) 
	{
		findsensor(e.getPlayer(), e.getFrom(), e.getTo());
	}
	
	//Player is within sensor range, check to see if said player matches sensor requirements
	//takes triggering player, xyz coords of sign, and all 4 lines of the sign.
	public boolean foundplayer(Player p, int x, int y, int z, String l1, String l2, String l3, String l4)
	{
		/*
		 * Sensor Filters
		 * 
		 */
		if(l1.equalsIgnoreCase("armorDetect"))
		{
			if(p.getInventory().contains(Material.LEATHER_HELMET) || p.getInventory().contains(Material.LEATHER_CHESTPLATE) || p.getInventory().contains(Material.LEATHER_LEGGINGS) || p.getInventory().contains(Material.LEATHER_BOOTS)
					|| p.getInventory().contains(Material.CHAINMAIL_HELMET) || p.getInventory().contains(Material.CHAINMAIL_CHESTPLATE) || p.getInventory().contains(Material.CHAINMAIL_LEGGINGS) || p.getInventory().contains(Material.CHAINMAIL_BOOTS)
					|| p.getInventory().contains(Material.IRON_HELMET) || p.getInventory().contains(Material.IRON_CHESTPLATE) || p.getInventory().contains(Material.IRON_LEGGINGS) || p.getInventory().contains(Material.IRON_BOOTS)
					|| p.getInventory().contains(Material.DIAMOND_HELMET) || p.getInventory().contains(Material.DIAMOND_CHESTPLATE) || p.getInventory().contains(Material.DIAMOND_LEGGINGS) || p.getInventory().contains(Material.DIAMOND_LEGGINGS)
					|| p.getInventory().contains(Material.GOLD_HELMET) || p.getInventory().contains(Material.GOLD_CHESTPLATE) || p.getInventory().contains(Material.GOLD_LEGGINGS) || p.getInventory().contains(Material.GOLD_BOOTS))
			{
				return true;
			}
		}
		else if(l1.equalsIgnoreCase("armorIgnore"))
		{
			if(!p.getInventory().contains(Material.LEATHER_HELMET) || p.getInventory().contains(Material.LEATHER_CHESTPLATE) || p.getInventory().contains(Material.LEATHER_LEGGINGS) || p.getInventory().contains(Material.LEATHER_BOOTS)
					|| p.getInventory().contains(Material.CHAINMAIL_HELMET) || p.getInventory().contains(Material.CHAINMAIL_CHESTPLATE) || p.getInventory().contains(Material.CHAINMAIL_LEGGINGS) || p.getInventory().contains(Material.CHAINMAIL_BOOTS)
					|| p.getInventory().contains(Material.IRON_HELMET) || p.getInventory().contains(Material.IRON_CHESTPLATE) || p.getInventory().contains(Material.IRON_LEGGINGS) || p.getInventory().contains(Material.IRON_BOOTS)
					|| p.getInventory().contains(Material.DIAMOND_HELMET) || p.getInventory().contains(Material.DIAMOND_CHESTPLATE) || p.getInventory().contains(Material.DIAMOND_LEGGINGS) || p.getInventory().contains(Material.DIAMOND_LEGGINGS)
					|| p.getInventory().contains(Material.GOLD_HELMET) || p.getInventory().contains(Material.GOLD_CHESTPLATE) || p.getInventory().contains(Material.GOLD_LEGGINGS) || p.getInventory().contains(Material.GOLD_BOOTS))
			{
				return true;
			}
		}
		else if(l1.equalsIgnoreCase("healthRange"))
		{
			int min, max;
			try
			{
				min = Integer.parseInt(l3);
				max = Integer.parseInt(l4);
			}
			catch(Exception e)
			{
				return false;
			}
			double hp = p.getHealth();
			if(hp >= min && hp <= max)
			{
				return true;
			}
		}
		//Broadcasts a local area message containing the detected player's name and location. Redstone triggers same as default sensor.
		else if(l1.equalsIgnoreCase("areaalert"))
		{
			for(int i = 0; i < p.getWorld().getPlayers().size(); i++)
			{
				if(p.getWorld().getPlayers().get(i).getLocation().toVector().distance(p.getLocation().toVector()) <= sensorBroadcastDist)
				{
					//send message to player containing name and location of sensor triggering player.
					int size = fpList.size();
					for(int i1 = 0; i1 < size; i1++)
					{
						if(fpList.get(i1).getName().equalsIgnoreCase(p.getName()))
						{
							if(!fpList.get(i1).isIgnoreAreaAlert())
							{
								p.getWorld().getPlayers().get(i1).sendMessage(ChatColor.RED + p.getDisplayName() + " was detected at [" + Double.toString(Math.floor(p.getLocation().getX())) + "," + Double.toString(Math.floor(p.getLocation().getY())) + "," + Double.toString(Math.floor(p.getLocation().getZ())) + "]");
								fpList.get(i1).setIgnoreAreaAlert(true);
								fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new DelayedVarToggle(fpList.get(i1), 0, false),60);
							}
						}
					}//end for loop
				}//end if in range
			}
			return true;
		}
		//Broadcasts a message to people in the towns listed on lines 3 and 4 containing the detected player's name and location, ignoring people in their own town entirely. Redstone triggers same as townignore sensor.
		else if(l1.equalsIgnoreCase("nationalert"))
		{
			if(fort.isTownyEnabled())
			{
				Nation t = null;
				Nation t2 = null;
				try 
				{
					if(TownyUniverse.getDataSource().getNation(l3) != null)
					{
						t = TownyUniverse.getDataSource().getNation(l3);
					}
					if(TownyUniverse.getDataSource().getNation(l4) != null)
					{
						t2 = TownyUniverse.getDataSource().getNation(l4);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					return false;
				}
				if(t != null)
				{
					try 
					{
						if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().equals(t))
						{
							//ignore, don't send an alert to a town about their own members...
						}
						else
						{
							int size = fpList.size();
							for(int i1 = 0; i1 < size; i1++)
							{
								for(int k = 0; k < t.getResidents().size();k++)//this can probably be done more effectively...
								{
									if(fpList.get(i1).getName().equalsIgnoreCase(t.getResidents().get(k).getName()))
									{
										if(!fpList.get(i1).isIgnoreFactionAlert())
										{
											fort.getServer().getOnlinePlayers();
											fpList.get(i1).sendMessage(ChatColor.RED + p.getDisplayName() + " was detected at [" + Double.toString(Math.floor(p.getLocation().getX())) + "," + Double.toString(Math.floor(p.getLocation().getY())) + "," + Double.toString(Math.floor(p.getLocation().getZ())) + "]");
											fpList.get(i1).setIgnoreTownAlert(true);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new DelayedVarToggle(fpList.get(i1), 3, false),60);
										}
									}
								}
							}//end for loop
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}					
				}
					if(t2 != null)
					{
						try 
						{
							if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().equals(t2))
							{
								//ignore, don't send an alert to a town about their own members...
							}
							else
							{
								int size = fpList.size();
								for(int i1 = 0; i1 < size; i1++)
								{
									for(int k = 0; k < t.getResidents().size();k++)//this can probably be done more effectively...
									{
										if(fpList.get(i1).getName().equalsIgnoreCase(t2.getResidents().get(k).getName()))
										{
											if(!fpList.get(i1).isIgnoreFactionAlert())
											{
												fort.getServer().getOnlinePlayers();
												fpList.get(i1).sendMessage(ChatColor.RED + p.getDisplayName() + " was detected at [" + Double.toString(Math.floor(p.getLocation().getX())) + "," + Double.toString(Math.floor(p.getLocation().getY())) + "," + Double.toString(Math.floor(p.getLocation().getZ())) + "]");
												fpList.get(i1).setIgnoreTownAlert(true);
												fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new DelayedVarToggle(fpList.get(i1), 3, false),60);
											}
										}
									}
								}//end for loop
							}
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
					try 
					{
						if(!TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().equals(t) && !TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().equals(t2))
						{
							return true;
						}
						else
						{
							return false;
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			else
			{//if towny isn't enabled return false
				return false;
			}
		}
		//Broadcasts a message to people in the towns listed on lines 3 and 4 containing the detected player's name and location, ignoring people in their own town entirely. Redstone triggers same as townignore sensor.
		else if(l1.equalsIgnoreCase("townalert"))
		{
			if(fort.isTownyEnabled())
			{
				Town t = null;
				Town t2 = null;
				try 
				{
					if(TownyUniverse.getDataSource().getTown(l3) != null)
					{
						t = TownyUniverse.getDataSource().getTown(l3);
					}
					if(TownyUniverse.getDataSource().getTown(l4) != null)
					{
						t2 = TownyUniverse.getDataSource().getTown(l4);
					}
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
					return false;
				}
				if(t != null)
				{
					try 
					{
						if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().equals(t))
						{
							//ignore, don't send an alert to a town about their own members...
						}
						else
						{
							int size = fpList.size();
							for(int i1 = 0; i1 < size; i1++)
							{
								for(int k = 0; k < t.getResidents().size();k++)//this can probably be done more effectively...
								{
									if(fpList.get(i1).getName().equalsIgnoreCase(t.getResidents().get(k).getName()))
									{
										if(!fpList.get(i1).isIgnoreFactionAlert())
										{
											fort.getServer().getOnlinePlayers();
											fpList.get(i1).sendMessage(ChatColor.RED + p.getDisplayName() + " was detected at [" + Double.toString(Math.floor(p.getLocation().getX())) + "," + Double.toString(Math.floor(p.getLocation().getY())) + "," + Double.toString(Math.floor(p.getLocation().getZ())) + "]");
											fpList.get(i1).setIgnoreTownAlert(true);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new DelayedVarToggle(fpList.get(i1), 2, false),60);
										}
									}
								}
							}//end for loop
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}					
				}
					if(t2 != null)
					{
						try 
						{
							if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().equals(t2))
							{
								//ignore, don't send an alert to a town about their own members...
							}
							else
							{
								int size = fpList.size();
								for(int i1 = 0; i1 < size; i1++)
								{
									for(int k = 0; k < t.getResidents().size();k++)//this can probably be done more effectively...
									{
										if(fpList.get(i1).getName().equalsIgnoreCase(t2.getResidents().get(k).getName()))
										{
											if(!fpList.get(i1).isIgnoreFactionAlert())
											{
												fort.getServer().getOnlinePlayers();
												fpList.get(i1).sendMessage(ChatColor.RED + p.getDisplayName() + " was detected at [" + Double.toString(Math.floor(p.getLocation().getX())) + "," + Double.toString(Math.floor(p.getLocation().getY())) + "," + Double.toString(Math.floor(p.getLocation().getZ())) + "]");
												fpList.get(i1).setIgnoreTownAlert(true);
												fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new DelayedVarToggle(fpList.get(i1), 2, false),60);
											}
										}
									}
								}//end for loop
							}
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
					try 
					{
						if(!TownyUniverse.getDataSource().getResident(p.getName()).getTown().equals(t) && !TownyUniverse.getDataSource().getResident(p.getName()).getTown().equals(t2))
						{
							return true;
						}
						else
						{
							return false;
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			else
			{//if towny isn't enabled return false
				return false;
			}
		}
		//Broadcasts a local area message to people in the factions listed on lines 3 and 4 containing the detected player's name and location, ignoring people in their own faction entirely. Redstone triggers same as factionignore sensor.
		else if(l1.equalsIgnoreCase("factionalert"))
		{
				if(fort.isFactionsEnabled())
				{
					UPlayer me = UPlayer.get(p);
					
					//Factions fac = Factions.i;
					Faction f = FactionColls.get().getForUniverse(me.getUniverse()).getByName(l3);//fac.getByTag(l3);
					Faction f2 =  FactionColls.get().getForUniverse(me.getUniverse()).getByName(l4);
					
					
					if(f != null)
					{
						if(me.getFaction().getId() == f.getId())
						{
							//ignore, don't send an alert to a faction about their own members...
						}
						else
						{
							List<UPlayer> fp = f.getUPlayers();//.getFPlayers();
							UPlayer fp1;
							Iterator<UPlayer> iter = fp.iterator();
							int i1 = 0;
							while(iter.hasNext())
							{
								fp1 = (UPlayer)iter.next();
									if(fpList.get(i1).getName().equalsIgnoreCase(fp1.getName()))
									{
										if(!fpList.get(i1).isIgnoreFactionAlert())
										{
											
											fp1.sendMessage(ChatColor.RED + p.getDisplayName() + " was detected at [" + Double.toString(Math.floor(p.getLocation().getX())) + "," + Double.toString(Math.floor(p.getLocation().getY())) + "," + Double.toString(Math.floor(p.getLocation().getZ())) + "]");
											fpList.get(i1).setIgnoreFactionAlert(true);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new DelayedVarToggle(fpList.get(i1), 1, false),60);
										}
									}
								i1++;
							}
						}
					}
					if(f2 != null)
					{
						if(me.getFaction().getId() == f2.getId())
						{
							//ignore, don't send an alert to a faction about their own members...
						}
						else
						{
							List<UPlayer> fp = f2.getUPlayers();
							UPlayer fp1;
							Iterator<UPlayer> iter = fp.iterator();
							int i1 = 0;
							while(iter.hasNext())
							{
								fp1 = (UPlayer)iter.next();
									if(fpList.get(i1).getName().equalsIgnoreCase(fp1.getName()))
									{
										if(!fpList.get(i1).isIgnoreFactionAlert())
										{
											fp1.sendMessage(ChatColor.RED + p.getDisplayName() + " was detected at [" + Double.toString(Math.floor(p.getLocation().getX())) + "," + Double.toString(Math.floor(p.getLocation().getY())) + "," + Double.toString(Math.floor(p.getLocation().getZ())) + "]");
											fpList.get(i1).setIgnoreFactionAlert(true);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new DelayedVarToggle(fpList.get(i1), 1, false),60);
										}
									}
									i1++;
							}
						}
					}
					if(!me.getFaction().getName().equalsIgnoreCase(l3) && !me.getFaction().getName().equalsIgnoreCase(l4))
					{
						return true;
					}
					else
					{
						return false;
					}
				}//
				else
				{//if factions isn't enabled return false
					return false;
				}
		}
		//detects if the player found is carrying tools, picks, shovels, axes, or shears.
		else if(l1.equalsIgnoreCase("tooldetect"))
		{
			if(p.getInventory().contains(Material.WOOD_SPADE) || p.getInventory().contains(Material.WOOD_PICKAXE) || p.getInventory().contains(Material.WOOD_AXE)
					|| p.getInventory().contains(Material.IRON_SPADE) || p.getInventory().contains(Material.IRON_PICKAXE) || p.getInventory().contains(Material.IRON_AXE)
					 || p.getInventory().contains(Material.STONE_SPADE) || p.getInventory().contains(Material.STONE_PICKAXE) || p.getInventory().contains(Material.STONE_AXE)
					 || p.getInventory().contains(Material.DIAMOND_SPADE) || p.getInventory().contains(Material.DIAMOND_PICKAXE) || p.getInventory().contains(Material.DIAMOND_AXE)
					 || p.getInventory().contains(Material.SHEARS))
			{
				return true;
			}
		}
		//detects if the player found is not carrying tools, picks, shovels, axes, or shears.
		else if(l1.equalsIgnoreCase("toolignore"))
		{
			if(!p.getInventory().contains(Material.WOOD_SPADE) || p.getInventory().contains(Material.WOOD_PICKAXE) || p.getInventory().contains(Material.WOOD_AXE)
					|| p.getInventory().contains(Material.IRON_SPADE) || p.getInventory().contains(Material.IRON_PICKAXE) || p.getInventory().contains(Material.IRON_AXE)
					 || p.getInventory().contains(Material.STONE_SPADE) || p.getInventory().contains(Material.STONE_PICKAXE) || p.getInventory().contains(Material.STONE_AXE)
					 || p.getInventory().contains(Material.DIAMOND_SPADE) || p.getInventory().contains(Material.DIAMOND_PICKAXE) || p.getInventory().contains(Material.DIAMOND_AXE)
					 || p.getInventory().contains(Material.SHEARS))
			{
				return true;
			}
		}
		//detects if the player found is carrying weapons, swords or bow.
		else if(l1.equalsIgnoreCase("weapondetect"))
		{
			if(p.getInventory().contains(Material.BOW) || p.getInventory().contains(Material.WOOD_SWORD) || p.getInventory().contains(Material.IRON_SWORD)
					|| p.getInventory().contains(Material.STONE_SWORD) || p.getInventory().contains(Material.DIAMOND_SWORD) || p.getInventory().contains(Material.GOLD_SWORD))
			{
				return true;
			}
		}
		//detects if the player found is not carrying weapons, swords or bow.
		else if(l1.equalsIgnoreCase("weaponignore"))
		{
			if(!p.getInventory().contains(Material.BOW) || p.getInventory().contains(Material.WOOD_SWORD) || p.getInventory().contains(Material.IRON_SWORD)
					|| p.getInventory().contains(Material.STONE_SWORD) || p.getInventory().contains(Material.DIAMOND_SWORD) || p.getInventory().contains(Material.GOLD_SWORD))
			{
				return true;
			}
		}
		//detects if the player found is carrying items of the specified ids.
		else if(l1.equalsIgnoreCase("itemdetect"))
		{
			if(l3 != null && l3 != "")
			{
				if(p.getInventory().contains(Material.getMaterial(l3)))
				{
				return true;
				}
			}
			if(l4 != null && l4 != "")
			{
				if(p.getInventory().contains(Material.getMaterial(l4)))
				{
				return true;
				}
			}
			/*if(p.getInventory().contains(Integer.parseInt(l3)) || p.getInventory().contains(Integer.parseInt(l4)))
			{
				return true;
			}*/
		}
		//detects if the player found is not carrying items of the specified ids.
		else if(l1.equalsIgnoreCase("itemignore"))
		{
			if(l3 != null && l3 != "")
			{
				if(p.getInventory().contains(Material.getMaterial(l3)))
				{
				return false;
				}
			}
			if(l4 != null && l4 != "")
			{
				if(p.getInventory().contains(Material.getMaterial(l4)))
				{
				return false;
				}
			}
			return true;
		}
		
		//Only detect people on lines 3 and 4
		else if(l1.equalsIgnoreCase("playerdetect"))
		{
			if(p.getName().equalsIgnoreCase(l3) || p.getName().equalsIgnoreCase(l4))
			{
				return true;
			}
		}
		//only detect group on line 3
	/*	else if(l1.equalsIgnoreCase("groupdetect")){
			String[] pgroups = p.getGroups();
			for(int j = 0; j < pgroups.length; j++){
				if(pgroups[j].equalsIgnoreCase(l3)){
					return true;
				}
			}
		} */
		//only detect groups on lines 3 and 4
	/*	else if(l1.equalsIgnoreCase("dgroupdetect")){
			String[] pgroups = p.getGroups();
			for(int j = 0; j < pgroups.length; j++){
				if(pgroups[j].equalsIgnoreCase(l3) || pgroups[j].equalsIgnoreCase(l4)){
					return true;
				}
			}
		}*/
		//detect everyone except the person listed on line 3
	/*	else if(l1.equalsIgnoreCase("singleignore")){
			if(!p.getName().equalsIgnoreCase(l3)){
				return true;
			}
		}
		//detect everyone except the two people listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("doubleignore")){
			if(!p.getName().equalsIgnoreCase(l3) && !p.getName().equalsIgnoreCase(l4)){
				return true;
			}
		}*/
		//detect everyone except the two people listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("playerignore"))
		{
			if(!p.getName().equalsIgnoreCase(l3) && !p.getName().equalsIgnoreCase(l4))
			{
				return true;
			}
		}
		//detect people in towns listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("towndetect"))
		{
			if(fort.isTownyEnabled())
			{
				try 
				{
					if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName().equalsIgnoreCase(l3) || TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName().equalsIgnoreCase(l4))
					{
						return true;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		//ignore people in towns listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("townignore"))
		{
			if(fort.isTownyEnabled())
			{
				try 
				{
					if(!TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName().equalsIgnoreCase(l3) && !TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName().equalsIgnoreCase(l4))
					{
						return true;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		//detect people in nations listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("nationdetect"))
		{
			if(fort.isTownyEnabled())
			{
				try 
				{
					if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().getName().equalsIgnoreCase(l3) || TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().getName().equalsIgnoreCase(l4))
					{
						return true;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		//ignore people in nations listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("nationignore"))
		{
			if(fort.isTownyEnabled())
			{
				try 
				{
					if(!TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().getName().equalsIgnoreCase(l3) && !TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().getName().equalsIgnoreCase(l4))
					{
						return true;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		//detect people in the factions listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("factiondetect"))
		{
			if(fort.isFactionsEnabled())
			{
				if(UPlayer.get(p).getFaction().getName().equalsIgnoreCase(l3) || UPlayer.get(p).getFaction().getName().equalsIgnoreCase(l4))
				{
					return true;
				}
			}
			else
			{
				return false;
			}
		}
		//detect everyone except people in the factions listed on lines 3 and 4
		else if(l1.equalsIgnoreCase("factionignore"))
		{
			if(fort.isFactionsEnabled())
			{
				if(!UPlayer.get(p).getFaction().getName().equalsIgnoreCase(l3) && !UPlayer.get(p).getFaction().getName().equalsIgnoreCase(l4))
				{
					return true;
				}
			}
			else
			{
				return false;
			}
		}
		else if(l1.equalsIgnoreCase("EnemyDetect"))
		{
			if(fort.isFactionsEnabled())
			{
				UPlayer me = UPlayer.get(p);
				
				Faction f = FactionColls.get().getForUniverse(me.getUniverse()).getByName(l3);//fac.getByTag(l3);
				Faction f2 =  FactionColls.get().getForUniverse(me.getUniverse()).getByName(l4);

				if(f != null)
				{
					if(me.getFaction().getRelationTo(f) == Rel.ENEMY)
					{
						return true;
					}
				}
				if(f2 != null)
				{
					if(me.getFaction().getRelationTo(f2) == Rel.ENEMY)
					{
						return true;
					}
				}
			}
			else if(fort.isTownyEnabled())
			{
					try 
					{
						if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().hasEnemy(TownyUniverse.getDataSource().getNation(l3)) || TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().hasEnemy(TownyUniverse.getDataSource().getNation(l4)))
						{
							return true;
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
			}
			else
			{
				return false;
			}
		}
		//detect the factions listed on l3 and l4 and their allies
		else if(l1.equalsIgnoreCase("AllyDetect"))
		{
			if(fort.isFactionsEnabled())
			{
				UPlayer me = UPlayer.get(p);
				
				//Factions fac = Factions.i;
				Faction f = FactionColls.get().getForUniverse(me.getUniverse()).getByName(l3);//fac.getByTag(l3);
				Faction f2 =  FactionColls.get().getForUniverse(me.getUniverse()).getByName(l4);
				
				if(me.getFaction().getName().equalsIgnoreCase(l3) || me.getFaction().getName().equalsIgnoreCase(l4))
				{
					return true;
				}
				else
				{
					if(f != null)
					{
						if(me.getFaction().getRelationTo(f) == Rel.ALLY)
						{
							return true;
						}
					}
					if(f2 != null)
					{
						if(me.getFaction().getRelationTo(f2) == Rel.ALLY)
						{
							return true;
						}
					}
				}
			}
			else if(fort.isTownyEnabled())
			{
					try 
					{
						if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().hasAlly(TownyUniverse.getDataSource().getNation(l3)) || TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().hasAlly(TownyUniverse.getDataSource().getNation(l4)))
						{
							return true;
						}
						if(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().getName().equalsIgnoreCase(l3) || TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation().getName().equalsIgnoreCase(l4))
						{
							return true;
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
			}
			else
			{
				return false;
			}
		}
		//detect everyone except people in the group on line 3 and those in groups
	/*	else if(l1.equalsIgnoreCase("groupignore")){
			boolean ingroup = false;
			String[] pgroups = p.getGroups();
			for(int j = 0; j < pgroups.length; j++){
				if(pgroups[j].equalsIgnoreCase(l3)){
					ingroup = true;
					break;
				}
			}
			if(ingroup == false){
				return true;
			}
		}*/
		//detect everyone except the people in the groups on lines 3 and 4
	/*	else if(l1.equalsIgnoreCase("dgroupignore")){
			boolean ingroup = false;
			String[] pgroups = p.getGroups();
			for(int j = 0; j < pgroups.length; j++){
				if(pgroups[j].equalsIgnoreCase(l3) || pgroups[j].equalsIgnoreCase(l4)){
					ingroup = true;
					break;
				}
			}
			if(ingroup == false){
				return true;
			}
		}*/
		//detect everyone
		else if(l1.equalsIgnoreCase("") || l1 == null || l1.equalsIgnoreCase(" ") || l1.equalsIgnoreCase("default"))
		{
			return true;
		}
		//Nobody matching sensor requirements found
		else
		{
			return false;
		}
		//return false if something odd happens
		return false;
	}
	
	//Finds all signs in square radius around set point, returns array of signs.
	public ArrayList<Sign> searchradius(int x, int y, int z, int radius, World w)
	{
		ArrayList<Sign> signlist = new ArrayList<Sign>();
		for(int i = 0; i < radius; i++)
		{
			for(int k = 0; k < radius; k++)
			{
				if(w.getBlockAt(x-i, y, z-k).getType().equals(Material.WALL_SIGN))
				{
					if(w.getBlockAt(x-i, y, z-k).getState() instanceof Sign)
					{
						signlist.add((Sign)w.getBlockAt(x-i, y, z-k).getState());
					}
				}
				if(w.getBlockAt(x-i, y, z+k).getType().equals(Material.WALL_SIGN))
				{
					if(w.getBlockAt(x-i, y, z+k).getState() instanceof Sign)
					{
						signlist.add((Sign)w.getBlockAt(x-i, y, z+k).getState());
					}
				}
				if(w.getBlockAt(x+i, y, z-k).getType().equals(Material.WALL_SIGN))
				{
					if(w.getBlockAt(x+i, y, z-k).getState() instanceof Sign)
					{
						signlist.add((Sign)w.getBlockAt(x+i, y, z-k).getState());
					}
				}
				if(w.getBlockAt(x+i, y, z+k).getType().equals(Material.WALL_SIGN))
				{
					if(w.getBlockAt(x+i, y, z+k).getState() instanceof Sign)
					{
						signlist.add((Sign)w.getBlockAt(x+i, y, z+k).getState());
					}
				}
			}
		}
			for(int i = 0; i < radius; i++)
			{
				for(int k = 0; k < radius; k++)
				{
					if(w.getBlockAt(x-i, y+1, z-k).getType().equals(Material.WALL_SIGN))
					{
						if(w.getBlockAt(x-i, y+1, z-k).getState() instanceof Sign)
						{
							signlist.add((Sign)w.getBlockAt(x-i, y+1, z-k).getState());
						}
					}
					if(w.getBlockAt(x-i, y+1, z+k).getType().equals(Material.WALL_SIGN))
					{
						if(w.getBlockAt(x-i, y+1, z+k).getState() instanceof Sign)
						{
							signlist.add((Sign)w.getBlockAt(x-i, y+1, z+k).getState());
						}
					}
					if(w.getBlockAt(x+i, y+1, z-k).getType().equals(Material.WALL_SIGN))
					{
						if(w.getBlockAt(x+i, y+1, z-k).getState() instanceof Sign)
						{
							signlist.add((Sign)w.getBlockAt(x+i, y+1, z-k).getState());
						}
					}
					if(w.getBlockAt(x+i, y+1, z+k).getType().equals(Material.WALL_SIGN))
					{
						if(w.getBlockAt(x+i, y+1, z+k).getState() instanceof Sign)
						{
							signlist.add((Sign)w.getBlockAt(x+i, y+1, z+k).getState());
						}
					}
				}
			}
				for(int i1 = 0; i1 < radius; i1++)
				{
					for(int k = 0; k < radius; k++)
					{
						if(w.getBlockAt(x-i1, y-1, z-k).getType().equals(Material.WALL_SIGN))
						{
							if(w.getBlockAt(x-i1, y-1, z-k).getState() instanceof Sign)
							{
								signlist.add((Sign)w.getBlockAt(x-i1, y-1, z-k).getState());
							}
						}
						if(w.getBlockAt(x-i1, y-1, z+k).getType().equals(Material.WALL_SIGN))
						{
							if(w.getBlockAt(x-i1, y-1, z+k).getState() instanceof Sign)
							{
								signlist.add((Sign)w.getBlockAt(x-i1, y-1, z+k).getState());
							}
						}
						if(w.getBlockAt(x+i1, y-1, z-k).getType().equals(Material.WALL_SIGN))
						{
							if(w.getBlockAt(x+i1, y-1, z-k).getState() instanceof Sign)
							{
								signlist.add((Sign)w.getBlockAt(x+i1, y-1, z-k).getState());
							}
						}
						if(w.getBlockAt(x+i1, y-1, z+k).getType().equals(Material.WALL_SIGN))
						{
							if(w.getBlockAt(x+i1, y-1, z+k).getState() instanceof Sign)
							{
								signlist.add((Sign)w.getBlockAt(x+i1, y-1, z+k).getState());
							}
						}
					}
		}
		return signlist;
	}
	
	//Finds teleblock shields around player, if shield is found within range and activated (redstone torch on or no torch), returns true.
	public boolean findteleblock(Location from, Location to)
	{
		int fx = (int)Math.floor(from.getX());
		int fy = (int)Math.floor(from.getY());
		int fz = (int)Math.floor(from.getZ());
		
		int tx = (int)Math.floor(to.getX());
		int ty = (int)Math.floor(to.getY());
		int tz = (int)Math.floor(to.getZ());
		
		ArrayList<Sign> fsignlist = searchradius(fx, fy, fz, teleblockrange, from.getWorld());
		ArrayList<Sign> tsignlist = searchradius(tx, ty, tz, teleblockrange, to.getWorld());
		
		for(int i = 0; i < fsignlist.size(); i++)
		{
			if(fsignlist.get(i).getLine(1).equalsIgnoreCase("[Shield]"))
			{
				if(fsignlist.get(i).getLine(0).equalsIgnoreCase("teleblock"))
				{
					if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).getData() == 0x2)
					{
						if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()+1).getType().toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							return true;
						}
					}
					else if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).getData() == 0x3)
					{
						if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()-1).toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							return true;
						}
					}
					else if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).getData() == 0x4)
					{
						if(from.getWorld().getBlockAt(fsignlist.get(i).getX()+1, fsignlist.get(i).getY(), fsignlist.get(i).getZ()).toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							{
								return true;
							}
						}
					}
					else if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).getData() == 0x5)
					{
						if(from.getWorld().getBlockAt(fsignlist.get(i).getX()-1, fsignlist.get(i).getY(), fsignlist.get(i).getZ()).toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(from.getWorld().getBlockAt(fsignlist.get(i).getX(), fsignlist.get(i).getY(), fsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							{
								return true;
							}
						}
					}
				}
			}
		}
		for(int i = 0; i < tsignlist.size(); i++)
		{
			if(tsignlist.get(i).getLine(1).equalsIgnoreCase("[Shield]"))
			{
				if(tsignlist.get(i).getLine(0).equalsIgnoreCase("teleblock"))
				{
					if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).getData() == 0x2)
					{
						if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()+1).toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							{
								return true;
							}
						}
					}
					if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).getData() == 0x3)
					{
						if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()-1).toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							{
								return true;
							}
						}
					}
					else if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).getData() == 0x4)
					{
						if(to.getWorld().getBlockAt(tsignlist.get(i).getX()+1, tsignlist.get(i).getY(), tsignlist.get(i).getZ()).toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							{
								return true;
							}
						}
					}
					else if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).getData() == 0x5)
					{
						if(to.getWorld().getBlockAt(tsignlist.get(i).getX()-1, tsignlist.get(i).getY(), tsignlist.get(i).getZ()).toString().equalsIgnoreCase(teleblockId) ||
								teleblockId.equalsIgnoreCase(Material.AIR.toString()))
						{
							if(to.getWorld().getBlockAt(tsignlist.get(i).getX(), tsignlist.get(i).getY(), tsignlist.get(i).getZ()).isBlockIndirectlyPowered())
							{	
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	//Sensor dynamic range code
	/*int detectRange;
	String[] s = new String[2];
	try
	{
		s = e.getLine(0).split(":");
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
					e.setLine(0, s[0] + ":" + i.toString() + "]");
				}
			}
		}
	}
	catch(Exception e1)
	{
		player.sendMessage(ChatColor.RED + "Sensor range must be an integer");
		player.getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()).setTypeId(0);
		player.getWorld().dropItem(new Location(player.getWorld(),e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), si);
		return;
	}*/
	//Sensor
	public void findsensor(Player player, Location from, Location to)
	{
	//	int fx = (int)Math.floor(from.getX());
	//	int fy = (int)Math.floor(from.getY());
	//	int fz = (int)Math.floor(from.getZ());
		
		int tx = (int)Math.floor(to.getX());
		int ty = (int)Math.floor(to.getY());
		int tz = (int)Math.floor(to.getZ());
		BlockState c;
		Sign s;
		//end = false;
		for(int k = 0; k < sensorlength; k++)
		{
			for(int g = -1; g <= 1; g++){
			if(player.getWorld().getBlockAt(tx-2-k, ty, tz+g).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx-2-k, ty, tz+g).getData() == 0x4)
				{
					c = player.getWorld().getBlockAt(tx-2-k, ty, tz+g).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx-2-k, ty, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx-k, ty, tz+g).getType().equals(Material.LEVER))
							{
								//Triggering levers this way would also trigger onredstone change, but it bypasses the bukkit api..
								//net.minecraft.server.Block.byId[player.getWorld().getBlockTypeIdAt(tx-k, ty, tz+g)].interact(((CraftWorld)player.getWorld()).getHandle(), tx-k, ty, tz+g, null);
								//end = true;
								int d = player.getWorld().getBlockAt(tx-k, ty, tz+g).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx-k, ty, tz+g).setData((byte) nd);
								    //etc.getServer().updateBlockPhysics(tx-k, ty, tz+g, nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx-k, ty, tz+g, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if(st[0].equalsIgnoreCase("[Sensor") && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx-2-k, ty, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx-k, ty, tz+g).getType().equals(Material.LEVER))
												{
													//Triggering levers this way would also trigger onredstone change, but it bypasses the bukkit api..
													//net.minecraft.server.Block.byId[player.getWorld().getBlockTypeIdAt(tx-k, ty, tz+g)].interact(((CraftWorld)player.getWorld()).getHandle(), tx-k, ty, tz+g, null);
													//end = true;
													int d = player.getWorld().getBlockAt(tx-k, ty, tz+g).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx-k, ty, tz+g).setData((byte) nd);
													    //etc.getServer().updateBlockPhysics(tx-k, ty, tz+g, nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx-k, ty, tz+g, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+2+k, ty, tz+g).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+2+k, ty, tz+g).getData() == 0x5)
				{
					c = player.getWorld().getBlockAt(tx+2+k, ty, tz+g).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+2+k, ty, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+k, ty, tz+g).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx+k, ty, tz+g).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+k, ty, tz+g).setData((byte) nd);
									//etc.getServer().updateBlockPhysics(tx+k, ty, tz+g, nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+k, ty, tz+g, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+2+k, ty, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+k, ty, tz+g).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx+k, ty, tz+g).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+k, ty, tz+g).setData((byte) nd);
														//etc.getServer().updateBlockPhysics(tx+k, ty, tz+g, nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+k, ty, tz+g, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+g, ty, tz-2-k).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+g, ty, tz-2-k).getData() == 0x2)
				{
					c = player.getWorld().getBlockAt(tx+g, ty, tz-2-k).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+g, ty, tz-2-k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+g, ty, tz-k).getType().equals(Material.LEVER))
							{
								//end = true;
							int d = player.getWorld().getBlockAt(tx+g, ty, tz-k).getData();
							int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+g, ty, tz-k).setData((byte) nd);
									//etc.getServer().updateBlockPhysics(tx+g, ty, tz-k, nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty, tz-k, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+g, ty, tz-2-k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+g, ty, tz-k).getType().equals(Material.LEVER))
												{
													//end = true;
												int d = player.getWorld().getBlockAt(tx+g, ty, tz-k).getData();
												int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+g, ty, tz-k).setData((byte) nd);
														//etc.getServer().updateBlockPhysics(tx+g, ty, tz-k, nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty, tz-k, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+g, ty, tz+2+k).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+g, ty, tz+2+k).getData() == 0x3)
				{
					c = player.getWorld().getBlockAt(tx+g, ty, tz+2+k).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+g, ty, tz+2+k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+g, ty, tz+k).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx+g, ty, tz+k).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+g, ty, tz+k).setData((byte) nd);
									//etc.getServer().updateBlockPhysics(tx+g, ty, tz+k, nd);
									//params = plugin (fortification), runnable class, delay time in server ticks (20 = ~1 second)
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty, tz+k, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+g, ty, tz+2+k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+g, ty, tz+k).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx+g, ty, tz+k).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+g, ty, tz+k).setData((byte) nd);
														//etc.getServer().updateBlockPhysics(tx+g, ty, tz+k, nd);
														//params = plugin (fortification), runnable class, delay time in server ticks (20 = ~1 second)
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty, tz+k, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			//Test everything again +1y....
			if(player.getWorld().getBlockAt(tx-2-k, ty+1, tz+g).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx-2-k, ty+1, tz+g).getData() == 0x4)
				{
					c = player.getWorld().getBlockAt(tx-2-k, ty+1, tz+g).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx-2-k, ty+1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx-k, ty+1, tz+g).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx-k, ty+1, tz+g).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx-k, ty+1, tz+g).setData((byte) nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx-k, ty+1, tz+g, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx-2-k, ty+1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx-k, ty+1, tz+g).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx-k, ty+1, tz+g).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx-k, ty+1, tz+g).setData((byte) nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx-k, ty+1, tz+g, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+2+k, ty+1, tz+g).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+2+k, ty+1, tz+g).getData() == 0x5)
				{
					c = player.getWorld().getBlockAt(tx+2+k, ty+1, tz+g).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+2+k, ty+1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+k, ty+1, tz+g).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx+k, ty+1, tz+g).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+k, ty+1, tz+g).setData((byte) nd);
									//etc.getServer().updateBlockPhysics(tx+k, ty, tz+g, nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+k, ty, tz+g, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+2+k, ty+1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+k, ty+1, tz+g).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx+k, ty+1, tz+g).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+k, ty+1, tz+g).setData((byte) nd);
														//etc.getServer().updateBlockPhysics(tx+k, ty, tz+g, nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+k, ty, tz+g, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+g, ty+1, tz-2-k).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+g, ty+1, tz-2-k).getData() == 0x2)
				{
					c = player.getWorld().getBlockAt(tx+g, ty+1, tz-2-k).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+g, ty+1, tz-2-k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+g, ty+1, tz-k).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx+g, ty+1, tz-k).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+g, ty+1, tz-k).setData((byte) nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty+1, tz-k, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+g, ty+1, tz-2-k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+g, ty+1, tz-k).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx+g, ty+1, tz-k).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+g, ty+1, tz-k).setData((byte) nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty+1, tz-k, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+g, ty+1, tz+2+k).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+g, ty+1, tz+2+k).getData() == 0x3)
				{
					c = player.getWorld().getBlockAt(tx+g, ty+1, tz+2+k).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+g, ty+1, tz+2+k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+g, ty+1, tz+k).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx+g, ty+1, tz+k).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+g, ty+1, tz+k).setData((byte) nd);
									//etc.getServer().updateBlockPhysics(tx+g, ty, tz+k, nd);
									//params = plugin (fortification), runnable class, delay time in server ticks (20 = ~1 second)
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty+1, tz+k, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+g, ty+1, tz+2+k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+g, ty+1, tz+k).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx+g, ty+1, tz+k).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+g, ty+1, tz+k).setData((byte) nd);
														//etc.getServer().updateBlockPhysics(tx+g, ty, tz+k, nd);
														//params = plugin (fortification), runnable class, delay time in server ticks (20 = ~1 second)
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty+1, tz+k, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			//test again with -1y
			if(player.getWorld().getBlockAt(tx-2-k, ty-1, tz+g).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx-2-k, ty-1, tz+g).getData() == 0x4)
				{
					c = player.getWorld().getBlockAt(tx-2-k, ty-1, tz+g).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx-2-k, ty-1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx-k, ty-1, tz+g).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx-k, ty-1, tz+g).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx-k, ty-1, tz+g).setData((byte) nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx-k, ty-1, tz+g, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx-2-k, ty-1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx-k, ty-1, tz+g).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx-k, ty-1, tz+g).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx-k, ty-1, tz+g).setData((byte) nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx-k, ty-1, tz+g, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+2+k, ty-1, tz+g).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+2+k, ty-1, tz+g).getData() == 0x5)
				{
					c = player.getWorld().getBlockAt(tx+2+k, ty-1, tz+g).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+2+k, ty-1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+k, ty-1, tz+g).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx+k, ty-1, tz+g).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+k, ty-1, tz+g).setData((byte) nd);
									//etc.getServer().updateBlockPhysics(tx+k, ty, tz+g, nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+k, ty-1, tz+g, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+2+k, ty-1, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+k, ty-1, tz+g).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx+k, ty-1, tz+g).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+k, ty-1, tz+g).setData((byte) nd);
														//etc.getServer().updateBlockPhysics(tx+k, ty, tz+g, nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+k, ty-1, tz+g, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+g, ty-1, tz-2-k).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+g, ty-1, tz-2-k).getData() == 0x2)
				{
					c = player.getWorld().getBlockAt(tx+g, ty-1, tz-2-k).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+g, ty-1, tz-2-k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+g, ty-1, tz-k).getType().equals(Material.LEVER))
							{
								//end = true;
							int d = player.getWorld().getBlockAt(tx+g, ty-1, tz-k).getData();
							int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+g, ty-1, tz-k).setData((byte) nd);
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty-1, tz-k, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+g, ty-1, tz-2-k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+g, ty-1, tz-k).getType().equals(Material.LEVER))
												{
													//end = true;
												int d = player.getWorld().getBlockAt(tx+g, ty-1, tz-k).getData();
												int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+g, ty-1, tz-k).setData((byte) nd);
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty-1, tz-k, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}
			if(player.getWorld().getBlockAt(tx+g, ty-1, tz+2+k).getType().equals(Material.WALL_SIGN))
			{
				if(player.getWorld().getBlockAt(tx+g, ty-1, tz+2+k).getData() == 0x3)
				{
					c = player.getWorld().getBlockAt(tx+g, ty-1, tz+2+k).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]"))
					{
						if(foundplayer(player, tx+g, ty-1, tz+2+k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
						{
							if(player.getWorld().getBlockAt(tx+g, ty-1, tz+k).getType().equals(Material.LEVER))
							{
								//end = true;
								int d = player.getWorld().getBlockAt(tx+g, ty-1, tz+k).getData();
								int nd = d | 0x8;
								if(nd != d)
								{
									player.getWorld().getBlockAt(tx+g, ty-1, tz+k).setData((byte) nd);
									//etc.getServer().updateBlockPhysics(tx+g, ty, tz+k, nd);
									//params = plugin (fortification), runnable class, delay time in server ticks (20 = ~1 second)
									fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty-1, tz+k, player.getWorld()), 50);
									break;
								}
							}
						}
					}
					else
					{
						//Sensor
						int detectRange;
						String[] st = new String[2];
						try
						{
							st = s.getLine(1).split(":");
							if(st[1].isEmpty())
							{
								//continue on to static range sensors
							}
							else
							{
								if((st[0].equalsIgnoreCase("[Sensor")) && st[1].endsWith("]"))
								{
									String i = st[1].replace(']', ' ');
									i.trim();
									detectRange = Integer.parseInt(i);
									//Make sure sensor does not exceed range limit
									if(detectRange > fort.getSensorlength())
									{
										detectRange = fort.getSensorlength();
										s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
										if(st[0].equalsIgnoreCase("[Sensor") && k < detectRange)
										{
											if(foundplayer(player, tx+g, ty-1, tz+2+k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
											{
												if(player.getWorld().getBlockAt(tx+g, ty-1, tz+k).getType().equals(Material.LEVER))
												{
													//end = true;
													int d = player.getWorld().getBlockAt(tx+g, ty-1, tz+k).getData();
													int nd = d | 0x8;
													if(nd != d)
													{
														player.getWorld().getBlockAt(tx+g, ty-1, tz+k).setData((byte) nd);
														//etc.getServer().updateBlockPhysics(tx+g, ty, tz+k, nd);
														//params = plugin (fortification), runnable class, delay time in server ticks (20 = ~1 second)
														fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+g, ty-1, tz+k, player.getWorld()), 50);
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						catch(Exception e1)
						{
							//Range isn't an int, no good way to resolve this outside of just making the sensor not work
							return;
						}
					}
				}
			}

		}//
		}
		/*********************************************
		 * 
		 * Upward and downward facing sensors
		 * 
		 *********************************************/
		for(int k = 0; k < sensorlength; k++)
		{
			for(int g = -1; g <= 1; g++)
			{
				for(int h = -1; h <= 1; h++)
				{
					if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getType().equals(Material.WALL_SIGN))
					{
						if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getData() == 0x4)
						{
							c = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[UpSensor]") || s.getLine(1).equalsIgnoreCase("[uSensor]"))
							{
								if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
								{
									if(player.getWorld().getBlockAt(tx+h+2, ty-1-k, tz+g).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h+2, ty-1-k, tz+g).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h+2, ty-1-k, tz+g).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h+2, ty-1-k, tz+g, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [UpSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[uSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[uSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
													{
														if(player.getWorld().getBlockAt(tx+h+2, ty-1-k, tz+g).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h+2, ty-1-k, tz+g).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h+2, ty-1-k, tz+g).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h+2, ty-1-k, tz+g, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}//end detect sign direction
						else if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getData() == 0x2)
						{
							c = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[UpSensor]") || s.getLine(1).equalsIgnoreCase("[uSensor]"))
							{
								if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
								{
									if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g+2).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g+2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g+2).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty-1-k, tz+g+2, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [UpSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[uSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[uSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
													{
														if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g+2).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g+2).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g+2).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty-1-k, tz+g+2, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}
						else if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getData() == 0x3)
						{
							c = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[UpSensor]") || s.getLine(1).equalsIgnoreCase("[uSensor]"))
							{
								if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
								{
									if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g-2).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g-2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g-2).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty-1-k, tz+g-2, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [UpSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[uSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[uSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
													{
														if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g-2).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g-2).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g-2).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty-1-k, tz+g-2, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}
						else if(player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getData() == 0x5)
						{
							c = player.getWorld().getBlockAt(tx+h, ty-1-k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[UpSensor]") || s.getLine(1).equalsIgnoreCase("[uSensor]"))
							{
								if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
								{
									if(player.getWorld().getBlockAt(tx+h-2, ty-1-k, tz+g).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h-2, ty-1-k, tz+g).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h-2, ty-1-k, tz+g).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h-2, ty-1-k, tz+g, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [UpSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[uSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[uSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty-1-k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
													{
														if(player.getWorld().getBlockAt(tx+h-2, ty-1-k, tz+g).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h-2, ty-1-k, tz+g).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h-2, ty-1-k, tz+g).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h-2, ty-1-k, tz+g, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}
					}//end detection segment
					/*
					 * [DownSensor]
					 */
					if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getType().equals(Material.WALL_SIGN))
					{
						if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getData() == 0x4)
						{
							c = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[DownSensor]") || s.getLine(1).equalsIgnoreCase("[dSensor]"))
							{
								if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3))){
									if(player.getWorld().getBlockAt(tx+h+2, ty+1+k, tz+g).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h+2, ty+1+k, tz+g).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h+2, ty+1+k, tz+g).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h+2, ty+1+k, tz+g, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [UpSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[dSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[dSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3))){
														if(player.getWorld().getBlockAt(tx+h+2, ty+1+k, tz+g).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h+2, ty+1+k, tz+g).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h+2, ty+1+k, tz+g).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h+2, ty+1+k, tz+g, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}//end detect sign direction
						else if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getData() == 0x2)
						{
							c = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[DownSensor]") || s.getLine(1).equalsIgnoreCase("[dSensor]"))
							{
								if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
								{
									if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g+2).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g+2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g+2).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty+1+k, tz+g+2, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [dSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[dSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[dSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
													{
														if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g+2).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g+2).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g+2).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty+1+k, tz+g+2, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}
						else if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getData() == 0x3)
						{
							c = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[DownSensor]") || s.getLine(1).equalsIgnoreCase("[dSensor]"))
							{
								if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
								{
									if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g-2).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g-2).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g-2).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty+1+k, tz+g-2, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [dSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[dSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[dSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
													{
														if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g-2).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g-2).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g-2).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h, ty+1+k, tz+g-2, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}
						else if(player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getData() == 0x5)
						{
							c = player.getWorld().getBlockAt(tx+h, ty+1+k, tz+g).getState();
							s = (Sign)c;
							if(s.getLine(1).equalsIgnoreCase("[DownSensor]") || s.getLine(1).equalsIgnoreCase("[dSensor]"))
							{
								if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
								{
									if(player.getWorld().getBlockAt(tx+h-2, ty+1+k, tz+g).getType().equals(Material.LEVER))
									{
										int d = player.getWorld().getBlockAt(tx+h-2, ty+1+k, tz+g).getData();
										int nd = d | 0x8;
										if(nd != d)
										{
											player.getWorld().getBlockAt(tx+h-2, ty+1+k, tz+g).setData((byte) nd);
											fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h-2, ty+1+k, tz+g, player.getWorld()), 50);
											break;
										}
									}
								}
							}//end = [dSensor]
							else
							{
								//Sensor
								int detectRange;
								String[] st = new String[2];
								try
								{
									st = s.getLine(1).split(":");
									if(st[1].isEmpty())
									{
										//continue on to static range sensors
									}
									else
									{
										if((st[0].equalsIgnoreCase("[dSensor")) && st[1].endsWith("]"))
										{
											String i = st[1].replace(']', ' ');
											i.trim();
											detectRange = Integer.parseInt(i);
											//Make sure sensor does not exceed range limit
											if(detectRange > fort.getSensorlength())
											{
												detectRange = fort.getSensorlength();
												s.setLine(1, st[0] + ":" + Integer.toString(detectRange) + "]");
												if(st[0].equalsIgnoreCase("[dSensor") && k < detectRange)
												{
													if(foundplayer(player, tx+h, ty+1+k, tz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3)))
													{
														if(player.getWorld().getBlockAt(tx+h-2, ty+1+k, tz+g).getType().equals(Material.LEVER))
														{
															int d = player.getWorld().getBlockAt(tx+h-2, ty+1+k, tz+g).getData();
															int nd = d | 0x8;
															if(nd != d)
															{
																player.getWorld().getBlockAt(tx+h-2, ty+1+k, tz+g).setData((byte) nd);
																fort.getServer().getScheduler().scheduleSyncDelayedTask(fort, new LeverOff(tx+h-2, ty+1+k, tz+g, player.getWorld()), 50);
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								catch(Exception e1)
								{
									//Range isn't an int, no good way to resolve this outside of just making the sensor not work
									return;
								}
							}
						}
					}//end detection segment
				}//for h
			}//for g
		}//for k
		
		//If player was found to not be entering sensor area, check to see if they are leaving one.
		//All of this code is obsolete due to addition of timer to turn off sensor.
		//Note: if this code is re-enabled in the future it does not include upward or downward facing sensors
		/*
		if(!end){
			if(tx == fx && ty == fy && tz == fz){
				//player hasn't moved enough to matter, no need to turn off the switch
			}
			else
			{
			for(int k = 0; k < sensorlength; k++){
				for(int g = -1; g <= 1; g++){
				if(player.getWorld().getBlockTypeIdAt(fx-2-k, fy, fz+g) == 68){
					if(player.getWorld().getBlockAt(fx-2-k, fy, fz+g).getData() == 0x4){
					c = player.getWorld().getBlockAt(fx-2-k, fy, fz+g).getState();
					s = (Sign)c;
					if(s.getLine(1).equalsIgnoreCase("[Sensor]")){
						if(foundplayer(player, fx-2-k, fy, fz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3))){
							if(player.getWorld().getBlockTypeIdAt(fx-k, fy, fz+g)==69)
							{
							int d = player.getWorld().getBlockAt(fx-k, fy, fz+g).getData();
							int nd = d & 0x7;
							if(nd != d){
								player.getWorld().getBlockAt(fx-k, fy, fz+g).setData((byte) nd);
						//	etc.getServer().updateBlockPhysics(fx-k, fy, fz+g, nd);
							break;
							}
							}
							}
						}
					}
				}
				if(player.getWorld().getBlockTypeIdAt(fx+2+k, fy, fz+g) == 68){
					if(player.getWorld().getBlockAt(fx+2+k, fy, fz+g).getData() == 0x5){
						c = player.getWorld().getBlockAt(fx+2+k, fy, fz+g).getState();
						s = (Sign)c;
						if(s.getLine(1).equalsIgnoreCase("[Sensor]")){
							if(foundplayer(player, fx+2+k, fy, fz+g, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3))){
								if(player.getWorld().getBlockTypeIdAt(fx+k, fy, fz+g)==69)
								{
								int d = player.getWorld().getBlockAt(fx+k, fy, fz+g).getData();
								int nd = d & 0x7;
								if(nd != d){
									player.getWorld().getBlockAt(fx+k, fy, fz+g).setData((byte) nd);
						//		etc.getServer().updateBlockPhysics(fx+k, fy, fz+g, nd);
								break;
								}
								}
								}
							}
						}
				}
				if(player.getWorld().getBlockTypeIdAt(fx+g, fy, fz-2-k) == 68){
					if(player.getWorld().getBlockAt(fx+g, fy, fz-2-k).getData() == 0x2){
						c = player.getWorld().getBlockAt(fx+g, fy, fz-2-k).getState();
						s = (Sign)c;
						if(s.getLine(1).equalsIgnoreCase("[Sensor]")){
							if(foundplayer(player, fx+g, fy, fz-2-k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3))){
								if(player.getWorld().getBlockTypeIdAt(fx+g, fy, fz-k)==69)
								{
								int d = player.getWorld().getBlockAt(fx+g, fy, fz-k).getData();
								int nd = d & 0x7;
								if(nd != d){
									player.getWorld().getBlockAt(fx+g, fy, fz-k).setData((byte) nd);
						//		etc.getServer().updateBlockPhysics(fx+g, fy, fz-k, nd);
								break;
								}
								}
								}
							}
						}
				}
				if(player.getWorld().getBlockTypeIdAt(fx+g, fy, fz+2+k) == 68){
					if(player.getWorld().getBlockAt(fx+g, fy, fz+2+k).getData() == 0x3){
						c = player.getWorld().getBlockAt(fx+g, fy, fz+2+k).getState();
						s = (Sign)c;
						if(s.getLine(1).equalsIgnoreCase("[Sensor]")){
							if(foundplayer(player, fx+g, fy, fz+2+k, s.getLine(0), s.getLine(1), s.getLine(2), s.getLine(3))){
								if(player.getWorld().getBlockTypeIdAt(fx+g, fy, fz+k)==69)
								{
								int d = player.getWorld().getBlockAt(fx+g, fy, fz+k).getData();
								int nd = d & 0x7;
								if(nd != d){
									player.getWorld().getBlockAt(fx+g, fy, fz+k).setData((byte) nd);
						//		etc.getServer().updateBlockPhysics(fx+g, fy, fz+k, nd);
								break;
								}
								}
								}
							}
						}
				}
			}
			}
			}
		}//end if(end)*/
	}
	
	//returning true will stop teleportation, use for teleblocking shield.
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) 
	{
		//for sensor detect
		findsensor(e.getPlayer(), e.getFrom(), e.getTo());
		
		if(!e.getPlayer().hasPermission("fortification.ignoreteleblock") || !e.getPlayer().isOp())
		{
			if(findteleblock(e.getFrom(), e.getTo()))
			{
				e.getPlayer().sendMessage(ChatColor.RED + teleblockstring);
			//	e.getPlayer().teleport(e.getFrom());
				e.setCancelled(true);
			}
		}
	}
}