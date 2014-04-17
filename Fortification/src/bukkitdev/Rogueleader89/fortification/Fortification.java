package bukkitdev.Rogueleader89.fortification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;



/**
*
* @author Rogueleader89
*/
public class Fortification extends JavaPlugin  
{
	protected static final Logger log = Logger.getLogger("Minecraft");
	private String name = "fortification";
	private String version = "0.95";
	private int flamelength = 5;
	private String flameturretblockId = "NETHERRACK";
	private int weblength;
	private String webturretblockId = "GLOWSTONE";
	private int webtime;
	private int sensorlength = 8;
	private int maxtraplength = 5;
	private int sendlength = 5;
	private int teleblockrange = 10;
	private String teleblockId = "OBSIDIAN";
	private String arrowturretId = "BRICK";
	private int chestrange = 5;
	private String chestshieldId = "0";
	private int sensorBroadcastDist = 75;
	private String[] trapblocks;
	private boolean replacetrap = true;
	private boolean sendremovetext = false;
	private boolean commandsend = false;
	private boolean sendoverwrite = true;
	private boolean sendoverwritescommands = true;
	private String teleblockstring = "A mysterious force blocks your teleportation!";
	private boolean factionsEnabled = false;
	private boolean permissionsEnabled = true;
	private boolean msgOnlyBuilder;
//	private File f = new File(getDataFolder().getPath() + "/config.yml");

	private FileConfiguration config;
	//private FileConfiguration config;
	private FortificationListener l;
	private FortificationPlayerListener pl;
	//public PermissionHandler permissions;
	
	private double arrowturretCost;
	private double webturretCost;
	private double flameturretCost;
	private double sensorCost;
	private double chestshieldCost;
	private double teleblockshieldCost;
	private double sendsignCost;
	private double equalsignCost;
	private double msgsignCost;
	private double trapdoorCost;
	private double transmitterCost;
	private double receiverCost;
	private int radioRange;
	private String telepadBlockId;
	private String telepadTowerId;
	private String telepadTowerTopId;
	private String telepadSupportId;
	private int telepadMaxLength;
	private int telepadMaxHeight;
	private boolean radioCrossWorld;
	private boolean econ;
	private Economy economy;
	private boolean townyEnabled = false;
	private boolean spoutEnabled = false;
	
	private String root = new File("").getAbsolutePath();
    private File pluginDir = new File(root, "plugins"+File.separator+"Fortification");
    private File file= new File(pluginDir, "data.txt");
    private File teleData = new File(pluginDir, "telepadData.txt");
    
    private List<Receiver> recList = new ArrayList<Receiver>();
    private List<Telepad> padList = new ArrayList<Telepad>();
	
    public void saveproperties()
	{
		config = this.getConfig();
		config.set("telepad-block-id", getTelepadBlockId());
		config.set("telepad-tower-id", getTelepadTowerId());
		config.set("telepad-tower-top-id", getTelepadTowerTopId());
		config.set("telepad-support-id", getTelepadSupportId());
		config.set("telepad-max-height", getTelepadMaxHeight());
		config.set("telepad-max-length", getTelepadMaxLength());
		config.set("arrowturret-block-id", getArrowturretId());
		config.set("flameturret-range", getFlamelength());
		config.set("sensor-range", getSensorlength());
		config.set("flameturret-block-id", getFlameturretblockId());
		config.set("webturret-block-id", getWebturretblockId());
		config.set("webturret-range", getWeblength());
		config.set("web-dissipation-time", getWebtime());
		config.set("trap-door-range", getMaxtraplength());
		//String[] allowedTrapBlocks = config.setString("allowed-trapdoor-blocks", "1,2,3,4,5,12,20,48").split(",");
		config.set("replace-trapdoor-blocks-on-power-off", isReplacetrap());
		config.set("send-sign-search-distance", getSendlength());
		config.set("send-signs-remove-original-text", isSendremovetext());
		config.set("allow-command-sends", isCommandsend());
		config.set("send-overwrites-existing-text", isSendoverwrite());
		config.set("send-overwrites-commands", isSendoverwritescommands());
		config.set("teleblock-shield-range", getTeleblockrange());
		config.set("teleblock-string", getTeleblockstring());
		config.set("teleblock-shield-block-Id", getTeleblockId());
		config.set("chest-shield-range", getChestrange());
		config.set("chest-shield-id", getChestshieldId());
		config.set("sensor-broadcast-dist", getSensorBroadcastDist());
		config.set("msg-builder-only", isMsgOnlyBuilder());
		config.set("radio-range", getRadioRange());
		config.set("radio-cross-world",canRadioCrossWorld());
		config.set("permissions-enabled", isPermissionsEnabled());
		config.set("spout-enabled", isSpoutEnabled());
		config.set("arrowturret-cost", getArrowturretCost());
		config.set("webturret-cost", getWebturretCost());
		config.set("flameturret-cost", getFlameturretCost());
		config.set("sensor-cost", getSensorCost());
		config.set("chestshield-cost", getChestshieldCost());
		config.set("teleblockshield-cost", getTeleblockshieldCost());
		config.set("sendsign-cost", getSendsignCost());
		config.set("equalsign-cost", getEqualsignCost());
		config.set("trapdoor-cost", getTrapdoorCost());
		config.set("transmitter-cost",getTransmitterCost());
		config.set("receiver-cost",getReceiverCost());
//		frostlength = properties.getInt("frost-turret-range", 5);
//		frostturretblockId = properties.getInt("frost-turret-block-Id", 79);
//		frostfreezeswater = properties.getBoolean("frost-turrets-freeze-water", false);
//		unfreezeTime = properties.getInt("movements-required-to-unfreeze-player", 30);
		
		//Split allowedTrapBlocks string into usable item id integers.
		String tdb = "";
		for (int i=0; i < getTrapblocks().length; i++ ) {
		tdb.concat("," + getTrapblocks()[i]);
		}
		getConfig().set("allowed-trapdoor-blocks", tdb);
	//	getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
    
	public void loadproperties()
	{
		config = this.getConfig();
		setTelepadBlockId(config.getString("telepad-block-id", "IRON_BLOCK"));
		setTelepadTowerId(config.getString("telepad-tower-id", "IRON_BLOCK"));
		setTelepadTowerTopId(config.getString("telepad-tower-top-id", "DIAMOND_BLOCK"));
		setTelepadSupportId(config.getString("telepad-support-id", "LAPIS_BLOCK"));
		setTelepadMaxHeight(config.getInt("telepad-max-height", 32));
		setTelepadMaxLength(config.getInt("telepad-max-length", 32));
		setArrowturretId(config.getString("arrowturret-block-id", "BRICK"));
		setFlamelength(config.getInt("flameturret-range", 5));
		setSensorlength(config.getInt("sensor-range", 8));
		setFlameturretblockId(config.getString("flameturret-block-id", "NETHERRACK"));
		setWebturretblockId(config.getString("webturret-block-id", "GLOWSTONE"));
		setWeblength(config.getInt("webturret-range", 5));
		setWebtime(config.getInt("web-dissipation-time", 10));
		setMaxtraplength(config.getInt("trap-door-range", 6));
		String[] allowedTrapBlocks = config.getString("allowed-trapdoor-blocks", "1,2,3,4,5,12,20,48").split(",");
		setReplacetrap(config.getBoolean("replace-trapdoor-blocks-on-power-off", true));
		setSendlength(config.getInt("send-sign-search-distance", 7));
		setSendremovetext(config.getBoolean("send-signs-remove-original-text", false));
		setCommandsend(config.getBoolean("allow-command-sends", true));
		setSendoverwrite(config.getBoolean("send-overwrites-existing-text", true));
		setSendoverwritescommands(config.getBoolean("send-overwrites-commands", true));
		setTeleblockrange(config.getInt("teleblock-shield-range", 10));
		setTeleblockstring(config.getString("teleblock-string", "A mysterious force blocks your teleportation!"));
		setTeleblockId(config.getString("teleblock-shield-block-Id", "OBSIDIAN"));
		setChestrange(config.getInt("chest-shield-range", 5));
		setChestshieldId(config.getString("chest-shield-id", "0"));
		setSensorBroadcastDist(config.getInt("sensor-broadcast-dist", 75));
		setMsgOnlyBuilder(config.getBoolean("msg-builder-only", true));
		setRadioRange(config.getInt("radio-range", 0));
		setRadioCrossWorld(config.getBoolean("radio-cross-world",true));
		setPermissionsEnabled(config.getBoolean("permissions-enabled", true));
		setSpoutEnabled(config.getBoolean("spout-enabled", false));

		setArrowturretCost(config.getDouble("arrowturret-cost", 0));
		setWebturretCost(config.getDouble("webturret-cost", 0));
		setFlameturretCost(config.getDouble("flameturret-cost", 0));
		setSensorCost(config.getDouble("sensor-cost", 0));
		setChestshieldCost(config.getDouble("chestshield-cost", 0));
		setTeleblockshieldCost(config.getDouble("teleblockshield-cost", 0));
		setSendsignCost(config.getDouble("sendsign-cost", 0));
		setEqualsignCost(config.getDouble("equalsign-cost", 0));
		setTrapdoorCost(config.getDouble("trapdoor-cost", 0));
		setTransmitterCost(config.getDouble("transmitter-cost",0));
		setReceiverCost(config.getDouble("receiver-cost",0));
//		frostlength = properties.getInt("frost-turret-range", 5);
//		frostturretblockId = properties.getInt("frost-turret-block-Id", 79);
//		frostfreezeswater = properties.getBoolean("frost-turrets-freeze-water", false);
//		unfreezeTime = properties.getInt("movements-required-to-unfreeze-player", 30);
		
		
		
		//Split allowedTrapBlocks string into usable item id integers.
		setTrapblocks(new String[allowedTrapBlocks.length]);
		for (int i=0; i < getTrapblocks().length; i++ ) 
		{
		try 
		{
			getTrapblocks()[i] = allowedTrapBlocks[i].trim();
		}
		catch (NumberFormatException nfe) 
		{
		getTrapblocks()[i] = "0";
		}
		}
		getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
/*	Old Permissions code
	private void permissionSetup(){
		if (permissions != null) {
		      return;
		    }

		    Plugin test = getServer().getPluginManager().getPlugin("Permissions");

		    if (test != null) {
		      permissions = ((Permissions)test).getHandler();
		      log.info("[Fortification]: Found and will use plugin " + ((Permissions)test).getDescription().getFullName());
		      setPermissionsEnabled(true);
		    } else {
		      log.info("[Fortification]: Permission system not detected, defaulting to bukkit's built in permissions.");
		      setPermissionsEnabled(false);
		    }
	}
	*/
	
	//Receiver data format = Recievingband,world,x,y,z
	public void saveData()
	{
		//Receiver Data
		try
		{
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file)); //FileWriter(file, true) prevents overriding
		    for(int i = 0; i < getReceiverList().size(); i++)
		    {
		    	//band,world,x,y,z
		    	bw.write(getReceiverList().get(i).getBand() + "," + getReceiverList().get(i).getLocation().getWorld().getName() + "," + getReceiverList().get(i).getLocation().getX() + "," + getReceiverList().get(i).getLocation().getY() + "," + getReceiverList().get(i).getLocation().getZ());
		    	bw.newLine();
		    }
		    bw.close();
		}
		catch(IOException ioe)
		{
		    //error
		}
		//Telepad Data
				try
				{
				    BufferedWriter bw = new BufferedWriter(new FileWriter(teleData)); //FileWriter(file, true) prevents overriding
				    for(int i = 0; i < getPadList().size(); i++)
				    {
				    	//recband,sendband,world,x,y,z,direction, t1x,t1y,t1z, s1x,s1y,s1z,s2x,s2y,s2z
				    	bw.write(getPadList().get(i).getRecBand() + "," + getPadList().get(i).getSendBand() + "," + getPadList().get(i).getLocation().getWorld().getName() + ","
				    	+ getPadList().get(i).getLocation().getX() + "," + getPadList().get(i).getLocation().getY() + "," + getPadList().get(i).getLocation().getZ() + ","
				    			+ getPadList().get(i).getDirection() + "," + getPadList().get(i).getFLTower().getLocation().getBlockX() + "," + getPadList().get(i).getFLTower().getLocation().getBlockY()
				    			 + "," + getPadList().get(i).getFLTower().getLocation().getBlockZ() + "," + getPadList().get(i).getFLTower().getS1Location().getBlockX()
				    			  + "," + getPadList().get(i).getFLTower().getS1Location().getBlockY() + "," + getPadList().get(i).getFLTower().getS1Location().getBlockZ() 
				    			  + "," + getPadList().get(i).getFLTower().getS2Location().getBlockX()
				    			  + "," + getPadList().get(i).getFLTower().getS2Location().getBlockY() + "," + getPadList().get(i).getFLTower().getS2Location().getBlockZ()
				    			  + "," + getPadList().get(i).getBLTower().getLocation().getBlockX()
				    			 + "," + getPadList().get(i).getBLTower().getLocation().getBlockY() + "," + getPadList().get(i).getBLTower().getLocation().getBlockZ()
				    			  + "," + getPadList().get(i).getBLTower().getS1Location().getBlockX()
				    			  + "," + getPadList().get(i).getBLTower().getS1Location().getBlockY() + "," + getPadList().get(i).getBLTower().getS1Location().getBlockZ() 
				    			  + "," + getPadList().get(i).getBLTower().getS2Location().getBlockX()
				    			  + "," + getPadList().get(i).getBLTower().getS2Location().getBlockY() + "," + getPadList().get(i).getBLTower().getS2Location().getBlockZ()
				    			  
				    			 + "," + getPadList().get(i).getBRTower().getLocation().getBlockX() + "," + getPadList().get(i).getBRTower().getLocation().getBlockY()
				    			 + "," + getPadList().get(i).getBRTower().getLocation().getBlockZ()
				    			  + "," + getPadList().get(i).getBRTower().getS1Location().getBlockX()
				    			  + "," + getPadList().get(i).getBRTower().getS1Location().getBlockY() + "," + getPadList().get(i).getBRTower().getS1Location().getBlockZ() 
				    			  + "," + getPadList().get(i).getBRTower().getS2Location().getBlockX()
				    			  + "," + getPadList().get(i).getBRTower().getS2Location().getBlockY() + "," + getPadList().get(i).getBRTower().getS2Location().getBlockZ()
				    			 
				    			 + "," + getPadList().get(i).getFRTower().getLocation().getBlockX()
				    			 + "," + getPadList().get(i).getFRTower().getLocation().getBlockY() + "," + getPadList().get(i).getFRTower().getLocation().getBlockZ()
				    	 + "," + getPadList().get(i).getFRTower().getS1Location().getBlockX()
		    			  + "," + getPadList().get(i).getFRTower().getS1Location().getBlockY() + "," + getPadList().get(i).getFRTower().getS1Location().getBlockZ() 
		    			  + "," + getPadList().get(i).getFRTower().getS2Location().getBlockX()
		    			  + "," + getPadList().get(i).getFRTower().getS2Location().getBlockY() + "," + getPadList().get(i).getFRTower().getS2Location().getBlockZ());
				    	bw.newLine();
				    }
				    bw.close();
				}
				catch(IOException ioe)
				{
				    //error
				}
	}
	
	public void loadData()
	{
		//Receiver Data
		if(file.exists())
		{
			try
			{
			    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			    String l;
			    while((l=br.readLine()) != null)
			    {
			        //do your stuff
			    	String[] s = l.split(",");
			    	//receiver band, world, x, y, z
			    	getReceiverList().add(new Receiver(new Location(this.getServer().getWorld(s[1]),Double.parseDouble(s[2]),Double.parseDouble(s[3]),Double.parseDouble(s[4])), s[0]));
			    }
			    br.close();
			}
			catch(IOException ioe)
			{
				//error
			}
		}
		else
		{
			//No receivers exist, a file will be created when the server reboots
		}
		
		//Telepad Data
				if(file.exists())
				{
					try
					{
					    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(teleData)));
					    String l;
					    while((l=br.readLine()) != null)
					    {
					        //do your stuff
					    	String[] s = l.split(",");
					    	//receiver band, world, x, y, z -- Array out of bounds here. Arrayoutofboundsexception: 43 at line 355
					    	getPadList().add(new Telepad(this, new Location(this.getServer().getWorld(s[2]),Double.parseDouble(s[3]),Double.parseDouble(s[4]),Double.parseDouble(s[5])), 
					    			new TelepadTower(new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[6]),Integer.parseInt(s[7]),Integer.parseInt(s[8])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[9]),Integer.parseInt(s[10]),Integer.parseInt(s[11])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[12]),Integer.parseInt(s[13]),Integer.parseInt(s[14])), this), 
					    			new TelepadTower(new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[15]),Integer.parseInt(s[16]),Integer.parseInt(s[17])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[18]),Integer.parseInt(s[18]),Integer.parseInt(s[20])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[30]),Integer.parseInt(s[31]),Integer.parseInt(s[32])), this), 
					    			new TelepadTower(new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[33]),Integer.parseInt(s[34]),Integer.parseInt(s[35])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[36]),Integer.parseInt(s[37]),Integer.parseInt(s[38])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[39]),Integer.parseInt(s[40]),Integer.parseInt(s[41])), this), 
					    			new TelepadTower(new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[42]),Integer.parseInt(s[43]),Integer.parseInt(s[44])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[45]),Integer.parseInt(s[46]),Integer.parseInt(s[47])), new Location(this.getServer().getWorld(s[2]),Integer.parseInt(s[48]),Integer.parseInt(s[49]),Integer.parseInt(s[50])), this), 
					    			Byte.parseByte(s[6]), s[0], s[1]));
					    }
					    br.close();
					}
					catch(IOException ioe)
					{
						//error
					}
				}
				else
				{
					//No receivers exist, a file will be created when the server reboots
				}
	}
	
	public void factionSetup()
	{
		Plugin test = getServer().getPluginManager().getPlugin("Factions");
		if(test != null){
			log.info("[Fortification]: Factions plugin detected: faction filters enabled.");
			setFactionsEnabled(true);
			factionsEnabled = true;
		}
		else
		{
			log.info("[Fortification]: Factions plugin not detected, faction filters will be disabled.");
			setFactionsEnabled(false);
			factionsEnabled = false;
		}
	}
	
	public void townySetup(){
		Plugin test = getServer().getPluginManager().getPlugin("Towny");
		if(test != null)
		{
			log.info("[Fortification]: Towny plugin detected: towny filters enabled.");
			setTownyEnabled(true);
		}
		else{
			log.info("[Fortification]: Towny plugin not detected, towny filters will be disabled.");
			setTownyEnabled(false);
		}
	}
	
	public void econSetup()
	{
		Plugin test = getServer().getPluginManager().getPlugin("Vault");
		if(test != null)
		{
			RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			setEconomy(rsp.getProvider());
			log.info("[Fortification]: " + getEconomy().getName() + " detected: mechanism costs enabled.");
			setEcon(true);
		}
		else{
			//no economy plugin or something failed
			log.info("[Fortification]: No economy plugin found, defaulting to free mechanisms.");
			setEcon(false);
		}
	}

	@Override
	public void onDisable() {
		saveData();
		this.saveConfig();
	}

	@Override
	public void onEnable() 
	{
		this.saveDefaultConfig();
		getServer().getPluginManager();
		loadproperties();
		factionSetup();
		townySetup();
		loadData();
		l = new FortificationListener(this);
		pl = new FortificationPlayerListener(this);
		getServer().getPluginManager().registerEvents(l, this);
		getServer().getPluginManager().registerEvents(pl, this);
	/*	pm.registerEvent(Event.Type.PLAYER_MOVE, pl, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, pl, Priority.Normal, this);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, l, Priority.High, this);
		pm.registerEvent(Event.Type.SIGN_CHANGE, l, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pl, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, pl, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, pl, Priority.Normal, this);*/
		//permissionSetup();
		log.info(name + " " + version + " initialized");
		
		//Commands
		getCommand("fort").setExecutor(new CommandExecutor() 
		{
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
            {
            	Player player = null;
            	if(sender instanceof Player){
            		player = (Player)sender;
            	}
            	else
            	{
            		if(args.length > 0)
            		{
	            		if(args[0].equalsIgnoreCase("reload")){
	            			reloadConfig();
	            			loadproperties();
	            			factionSetup();
	            			townySetup();
	            			//permissionSetup();
	            		}
	            		else if(args[0].equalsIgnoreCase("radio"))
	            		{
	            			for(int i = 0; i < getReceiverList().size(); i++)
	            			{
	            				if(getReceiverList().get(i).getBand().equals(args[1]))
	            				{
	            					Location l = getReceiverList().get(i).getLocation();
	        						if(l.getBlock().getType().equals(Material.WALL_SIGN))
	        						{
	        							switch(l.getBlock().getData())
	        							{
	        							case 0x2:
	        								if(args.length > 2)
	    									{
		        								if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getType().equals(Material.LEVER))
		        								{
		        									if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
		        									{
		        										//set receiver as powered
		        										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getData();
		        										int nd = d | 0x8;
		        										if(nd != d)
		        										{
		        											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).setData((byte) nd);
		        										}
		        									}
		        									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	    									}
	        								else
	        								{
	        									//set receiver as powered
	    										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getData();
	    										int nd = d | 0x8;
	    										if(nd != d)
	    										{
	    											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).setData((byte) nd);
	    										}
	        								}
	        								break;
	        							case 0x3:
	        								if(args.length > 2)
	    									{
		        								if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getType().equals(Material.LEVER))
		        								{
		        									if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
		        									{
		        										//set receiver as powered
		        										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getData();
		        										int nd = d | 0x8;
		        										if(nd != d)
		        										{
		        											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).setData((byte) nd);
		        										}
		        									}
		        									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	    									}
	        								else
	        								{
	        									//set receiver as powered
	    										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getData();
	    										int nd = d | 0x8;
	    										if(nd != d)
	    										{
	    											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).setData((byte) nd);
	    										}
	        								}
	        								break;
	        							case 0x4:
	        								if(args.length > 2)
	    									{
		        								if(l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getType().equals(Material.LEVER))
		        								{
		        									if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
		        									{
		        										//set receiver as powered
		        										int d = l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getData();
		        										int nd = d | 0x8;
		        										if(nd != d)
		        										{
		        											l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
		        										}
		        									}
		        									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	    									}
	        								else
	        								{
	        									//set receiver as powered
	    										int d = l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getData();
	    										int nd = d | 0x8;
	    										if(nd != d)
	    										{
	    											l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
	    										}
	        								}
	        								break;
	        							case 0x5:
	        								if(args.length > 2)
	    									{
		        								if(l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getType().equals(Material.LEVER))
		        								{
		        									if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
		        									{
		        										//set receiver as powered
		        										int d = l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getData();
		        										int nd = d | 0x8;
		        										if(nd != d)
		        										{
		        											l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
		        										}
		        									}
		        									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	    									}
	        								else
	        								{
	        									//set receiver as powered
	    										int d = l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getData();
	    										int nd = d | 0x8;
	    										if(nd != d)
	    										{
	    											l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
	    										}
	        								}
	        								break;
	        							}
	        						}
	            				}
	            			}
	            		}
	            		else{
	            			//sender.sendMessage("This command is only available to ingame players.");
	            			log.info("This command is only available to ingame players.");
	            		}
            		}
            		else{
            			//sender.sendMessage("This command is only available to ingame players.");
            			log.info("This command is only available to ingame players.");
            		}
            		
            	}
            	if(player != null){
            		if(args.length == 0){
		            	player.sendMessage("You have the following fortification permissions:");
		   
		            	if(player.hasPermission( "fortification.msgsign") || player.hasPermission( "fortification.*")){
							player.sendMessage(ChatColor.GOLD + "/fort msgsign");
						}
		            	if(player.hasPermission( "fortification.turret.*") || player.hasPermission( "fortification.*")){
							player.sendMessage(ChatColor.GOLD + "/fort turret");
						}
		            	if(player.hasPermission( "fortification.turret.arrow") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
							player.sendMessage(ChatColor.GOLD + "/fort arrowturret");
						}
		            	if(player.hasPermission( "fortification.turret.flame") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
							player.sendMessage(ChatColor.GOLD + "/fort flameturret");
						}
		            	if(player.hasPermission( "fortification.turret.web") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
							player.sendMessage(ChatColor.GOLD + "/fort webturret");
						}
		            	if(player.hasPermission( "fortification.equalsign") || player.hasPermission( "fortification.*")){
							player.sendMessage(ChatColor.GOLD + "/fort equalsign");
						}
		            	if(player.hasPermission( "fortification.trapdoor") || player.hasPermission( "fortification.*")){
							player.sendMessage(ChatColor.GOLD + "/fort trapdoor");
						}
		            	if(player.hasPermission( "fortification.sensor") || player.hasPermission( "fortification.*")){
							player.sendMessage(ChatColor.GOLD + "/fort sensor");
						}
		            	if(player.hasPermission( "fortification.sendsign") || player.hasPermission( "fortification.*")){
							player.sendMessage(ChatColor.GOLD + "/fort sendsign");
						}
		            	if(player.hasPermission( "fortification.shield.teleblock") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.shield.*")){
							player.sendMessage(ChatColor.GOLD + "/fort teleblock");
						}
		            	if(player.hasPermission( "fortification.shield.chest") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.shield.*")){
							player.sendMessage(ChatColor.GOLD + "/fort chest");
						}
            		}
            		else if(args.length > 0){
            			if(args[0].equalsIgnoreCase("costs")){
            				if(isEcon()){
            				player.sendMessage(ChatColor.GOLD + "Mechanism Prices:");
            				   
                        	if(player.hasPermission( "fortification.msgsign") || player.hasPermission( "fortification.*")){
            					player.sendMessage(ChatColor.GOLD + "Message Sign: " + msgsignCost);
            				}
                        	if(player.hasPermission( "fortification.turret.flame") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
            					player.sendMessage(ChatColor.GOLD + "Flame Turret: " + flameturretCost);
            				}
                        	if(player.hasPermission( "fortification.turret.arrow") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
            					player.sendMessage(ChatColor.GOLD + "Arrow Turret: " + arrowturretCost);
            				}
                        	if(player.hasPermission( "fortification.turret.web") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
            					player.sendMessage(ChatColor.GOLD + "Web Turret: " + webturretCost);
            				}
                        	if(player.hasPermission( "fortification.equalsign") || player.hasPermission( "fortification.*")){
            					player.sendMessage(ChatColor.GOLD + "Equals Sign: " + equalsignCost);
            				}
                        	if(player.hasPermission( "fortification.trapdoor") || player.hasPermission( "fortification.*")){
            					player.sendMessage(ChatColor.GOLD + "Trap Door: " + trapdoorCost);
            				}
                        	if(player.hasPermission( "fortification.sensor") || player.hasPermission( "fortification.*")){
            					player.sendMessage(ChatColor.GOLD + "Sensor: " + sensorCost);
            				}
                        	if(player.hasPermission( "fortification.sendsign") || player.hasPermission( "fortification.*")){
            					player.sendMessage(ChatColor.GOLD + "Send Sign: " + sendsignCost);
            				}
                        	if(player.hasPermission( "fortification.shield.teleblock") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.shield.*")){
            					player.sendMessage(ChatColor.GOLD + "Teleblock Shield: " + teleblockshieldCost);
            				}
                        	if(player.hasPermission( "fortification.shield.chest") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.shield.*")){
            					player.sendMessage(ChatColor.GOLD + "Chest Shield: " + chestshieldCost);
            				}
            			}
            				else{
            					player.sendMessage(ChatColor.GOLD + "This server is not running a supported economy plugin.");
            				}
            			}
            			if(args[0].equalsIgnoreCase("reload"))
            			{
            				reloadConfig();
                			loadproperties();
                			factionSetup();
                			townySetup();
            			}
				if(args[0].equalsIgnoreCase("msgsign"))
				{
					if(!player.hasPermission( "fortification.msgsign") || !player.hasPermission( "fortification.*"))
					{
						player.sendMessage(ChatColor.RED + "You do not have permission to build message signs.");
					}
					else{
					player.sendMessage(ChatColor.GOLD + "Sends the text on lines 3 and 4 of the sign to the player on line 1 of the sign. Note: Player name must be full and exact.");
					}
				}
				if(args[0].equalsIgnoreCase("teleblock")){
					if(!player.hasPermission( "fortification.shield.teleblock") || !player.hasPermission( "fortification.*") || !player.hasPermission( "fortification.shield.*")){
						player.sendMessage(ChatColor.RED + "You do not have permission to build teleblock shields.");
					}
					else{
					player.sendMessage(ChatColor.GOLD + "Creates a shield that blocks teleportation within " + teleblockrange + " blocks of it. Must be built on " + Material.getMaterial(teleblockId).name());
					}
				}
				if(args[0].equalsIgnoreCase("arrowturret")){
					if(!player.hasPermission( "fortification.turret.arrow") || !player.hasPermission( "fortification.*") || !player.hasPermission( "fortification.turret.*")){
						player.sendMessage(ChatColor.RED + "You do not have permission to build arrow turrets.");
					}
					else{
					player.sendMessage(ChatColor.GOLD + "Fires an arrow in the direction it is facing. Must be built on " + Material.getMaterial(arrowturretId).name());
					}
				}
				if(args[0].equalsIgnoreCase("equalssign")){
					if(!player.hasPermission( "fortification.equalsign") || !player.hasPermission( "fortification.*")){
						player.sendMessage(ChatColor.RED + "You do not have permission to build equals signs.");
					}
					else{
					player.sendMessage(ChatColor.GOLD + "Compares the first line of the sign to the third line, activates the lever on the back of the sign block if they are equal.");
					}
				}
				if(args[0].equalsIgnoreCase("sendsign")){
				if(!player.hasPermission( "fortification.sendsign") || !player.hasPermission( "fortification.*")){
						player.sendMessage(ChatColor.RED + "You do not have permission to build send signs.");
					}
					else{
					player.sendMessage(ChatColor.GOLD + "Send signs send the text from one sign to another when they recieve redstone power.");
					player.sendMessage(ChatColor.GOLD + "Send signs are placed on the line you want to move. So if you wanted to move the first line of a sign below");
					player.sendMessage(ChatColor.GOLD + "the send sign to a sign above it you would type [Send U] on the first line. If left to right then [Send R] and so on.");
					if(sendoverwrite){
					player.sendMessage(ChatColor.GOLD + "Text sent to another sign via a send sign will overwrite the existing text on the chosen line of that sign.");
					}
					else{
						player.sendMessage(ChatColor.GOLD + "Text will not be moved to the destination sign if text already exists on the targetted line.");
					}
					if(!commandsend){
						player.sendMessage(ChatColor.GOLD + "Sign commands can not be sent to other signs via a send sign.");
					}
					}
				}
				if(args[0].equalsIgnoreCase("trapdoor")){
					if(!player.hasPermission( "fortification.trapdoor") || !player.hasPermission( "fortification.*")){
						player.sendMessage(ChatColor.RED + "You do not have permission to build trap doors.");
					}
					else{
					player.sendMessage(ChatColor.GOLD + "Trap doors remove blocks of the type they are made of up to " + maxtraplength + " blocks infront of them when they recieve redstone power.");
					if(replacetrap){
						player.sendMessage(ChatColor.GOLD + "When powered off, all air blocks within the trap door's range are replaced by blocks of the type they are made of.");
					}
					player.sendMessage(ChatColor.GOLD + "They may be made out of materials with the following Ids:");
					for(int i = 0; i < trapblocks.length; i++){
						player.sendMessage(ChatColor.GOLD + config.getString("allowed-trapdoor-blocks"));
					}
					}
				}
				if(args[0].equalsIgnoreCase("flameturret")) {
					if(player.hasPermission( "fortification.turret.flame") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
				        		player.sendMessage(ChatColor.GOLD + "Lights the first " + flamelength + " blocks infront of the turret on fire. Flame turrets must be made out of block id " + flameturretblockId + ". Flames only appear on smooth, level ground.");
				    }
					else{
						player.sendMessage(ChatColor.RED + "you do not have permission to build flame turrets.");
					}
				}
				if(args[0].equalsIgnoreCase("webturret")){
					if(player.hasPermission( "fortification.turret.web") || player.hasPermission( "fortification.*") || player.hasPermission( "fortification.turret.*")){
				        		player.sendMessage(ChatColor.GOLD + "Fires web out " + weblength + " blocks infront of the turret. Web turrets must be made out of block id " + webturretblockId + ". The web lasts " + Integer.toString(webtime) + " seconds.");
					}
				else{
					player.sendMessage(ChatColor.RED + "you do not have permission to build web turrets.");
				}
				}
				
				if(args[0].equalsIgnoreCase("sensor")) {
					if(player.hasPermission( "fortification.sensor") || player.hasPermission( "fortification.*")){
					        	if(args.length > 1){
					        	if(args[1].equalsIgnoreCase("playerdetect")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if a player listed on line 3 or line 4 is detected.");
					        	}
					        	else if(args[1].equalsIgnoreCase("playerignore")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is not listed on line 3 or line 4.");
					        	}
					        	else if(args[1].equalsIgnoreCase("factiondetect")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if a player that is in a faction listed on lines 3 or 4 is detected.");
					        	}
					        	else if(args[1].equalsIgnoreCase("factionignore")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is not in a faction listed on lines 3 or 4.");
					        	}
					        	else if(args[1].equalsIgnoreCase("default")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers any time a player is detected within " + sensorlength + " blocks in the direction the sensor is facing.");
					        	}
					        	else if(args[1].equalsIgnoreCase("itemdetect")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is carrying an item of one of the ids specified on line 3 or 4.");
					        	}
					        	else if(args[1].equalsIgnoreCase("itemignore")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is not carrying an item of one of the ids specified on line 3 or 4.");
					        	}
					        	else if(args[1].equalsIgnoreCase("weapondetect")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is carrying a weapon: sword or bow.");
					        	}
					        	else if(args[1].equalsIgnoreCase("weapondetect")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is not carrying a weapon: sword or bow.");
					        	}
					        	else if(args[1].equalsIgnoreCase("areaalert")){
					        		player.sendMessage(ChatColor.GOLD + "Sends out local message " + sensorBroadcastDist + " blocks distance from the sensor. Triggers on detecting any player except the two listed on lines 3 and 4.");
					        	}
					        	else if(args[1].equalsIgnoreCase("factionalert")){
					        		player.sendMessage(ChatColor.GOLD + "Sends a message to all members of the factions listed on lines 3 and 4. Triggers on detecting any player except those in the factions listed.");
					        	}
					        	else if(args[1].equalsIgnoreCase("tooldetect")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is carrying a tool: pick, shovel, axe, or shears.");
					        	}
					        	else if(args[1].equalsIgnoreCase("toolignore")){
					        		player.sendMessage(ChatColor.GOLD + "Triggers only if the detected player is not carrying a tool: pick, shovel, axe, or shears.");
					        	}
					        	else {
					        		player.sendMessage(ChatColor.RED + "Invalid sensor type, type /fort sensor for a list of sensor types.");
					        	}
					        	}
					        	else{
					        	
					          player.sendMessage(ChatColor.GOLD + "Sensor Types: playerdetect, playerignore, factiondetect, factionignore, itemdetect, itemignore, weapondetect, weaponignore, tooldetect, toolignore, areaalert, factionalert, default.");
					          player.sendMessage(ChatColor.GOLD + "To learn more about a specific sensor type /fort sensor [type]");
					          player.sendMessage(ChatColor.GOLD + "Sensors can detect players up to " + sensorlength + " blocks infront of them and 1 block above or below the level of the sensor.");
					        	}
					}
					        else {
					          player.sendMessage(ChatColor.RED + "You do not have permission to build sensors.");
					        }
	            		}
				}
            		if(args[0].equalsIgnoreCase("radio") && args.length > 1)
            		{
            			for(int i = 0; i < getReceiverList().size(); i++)
            			{
            				if(getReceiverList().get(i).getBand().equals(args[1]))
            				{
            					Location l = getReceiverList().get(i).getLocation();
            					if(!canRadioCrossWorld() && !player.getWorld().getName().equals(l.getWorld().getName()))
            					{
            						//can't contact across worlds
            					}
            					else if(player.getLocation().distance(l) > getRadioRange() && getRadioRange() != 0)
            					{
            						//the radio does not have enough range to reach the receiver
            					}
            					else
            					{
	        						if(l.getBlock().getType().equals(Material.WALL_SIGN))
	        						{
	        							switch(l.getBlock().getData())
	        							{
	        							case 0x2:
	        								if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getType().equals(Material.LEVER))
	        								{
	        									if(args.length > 2)
	        									{
	        										if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
	            									{
	            										//set receiver as powered
	            										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getData();
	            										int nd = d | 0x8;
	            										if(nd != d)
	            										{
	            											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).setData((byte) nd);
	            										}
	            									}
	            									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	        									else
	        									{
	        										//set receiver as powered
	        										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).getData();
	        										int nd = d | 0x8;
	        										if(nd != d)
	        										{
	        											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 2).setData((byte) nd);
	        										}
	        									}
	        								}
	        								break;
	        							case 0x3:
	        								if(args.length > 2)
	    									{
		        								if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getType().equals(Material.LEVER))
		        								{
		        									if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
		        									{
		        										//set receiver as powered
		        										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getData();
		        										int nd = d | 0x8;
		        										if(nd != d)
		        										{
		        											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).setData((byte) nd);
		        										}
		        									}
		        									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	    									}
	        								else
	        								{
	        									//set receiver as powered
	    										int d = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).getData();
	    										int nd = d | 0x8;
	    										if(nd != d)
	    										{
	    											l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 2).setData((byte) nd);
	    										}
	        								}
	        								break;
	        							case 0x4:
	        								if(args.length > 2)
	    									{
		        								if(l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getType().equals(Material.LEVER))
		        								{
		        									if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
		        									{
		        										//set receiver as powered
		        										int d = l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getData();
		        										int nd = d | 0x8;
		        										if(nd != d)
		        										{
		        											l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
		        										}
		        									}
		        									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	    									}
	        								else
	        								{
	        									//set receiver as powered
	    										int d = l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).getData();
	    										int nd = d | 0x8;
	    										if(nd != d)
	    										{
	    											l.getWorld().getBlockAt(l.getBlockX() + 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
	    										}
	        								}
	        								break;
	        							case 0x5:
	        								if(args.length > 2)
	    									{
		        								if(l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getType().equals(Material.LEVER))
		        								{
		        									if(args[2] == null || args[2].equals("") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
		        									{
		        										//set receiver as powered
		        										int d = l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getData();
		        										int nd = d | 0x8;
		        										if(nd != d)
		        										{
		        											l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
		        										}
		        									}
		        									else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
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
	    									}
	        								else
	        								{
	        									//set receiver as powered
	    										int d = l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).getData();
	    										int nd = d | 0x8;
	    										if(nd != d)
	    										{
	    											l.getWorld().getBlockAt(l.getBlockX() - 2, l.getBlockY(), l.getBlockZ()).setData((byte) nd);
	    										}
	        								}
	        								break;
	        							}
	        						}
            					}
            				}
            			}
            		}
            }
            	return true;
            }
        });
	}
	public void setFlamelength(int flamelength) {
		this.flamelength = flamelength;
	}
	public int getFlamelength() {
		return flamelength;
	}
	public void setFlameturretblockId(String flameturretblockId) {
		this.flameturretblockId = flameturretblockId;
	}
	public String getFlameturretblockId() {
		return flameturretblockId;
	}
	public void setSensorlength(int sensorlength) {
		this.sensorlength = sensorlength;
	}
	public int getSensorlength() {
		return sensorlength;
	}
	public void setMaxtraplength(int maxtraplength) {
		this.maxtraplength = maxtraplength;
	}
	public int getMaxtraplength() {
		return maxtraplength;
	}
	public void setSendlength(int sendlength) {
		this.sendlength = sendlength;
	}
	public int getSendlength() {
		return sendlength;
	}
	public void setTeleblockrange(int teleblockrange) {
		this.teleblockrange = teleblockrange;
	}
	public int getTeleblockrange() {
		return teleblockrange;
	}
	public void setTeleblockId(String teleblockId) {
		this.teleblockId = teleblockId;
	}
	public String getTeleblockId() {
		return teleblockId;
	}
/*	public void setFrostlength(int frostlength) {
		this.frostlength = frostlength;
	}
	public int getFrostlength() {
		return frostlength;
	}
	public void setFrostturretblockId(int frostturretblockId) {
		this.frostturretblockId = frostturretblockId;
	}
	public int getFrostturretblockId() {
		return frostturretblockId;
	}
	public void setUnfreezeTime(int unfreezeTime) {
		this.unfreezeTime = unfreezeTime;
	}
	public int getUnfreezeTime() {
		return unfreezeTime;
	}
	public void setFrostfreezeswater(boolean frostfreezeswater) {
		this.frostfreezeswater = frostfreezeswater;
	}
	public boolean isFrostfreezeswater() {
		return frostfreezeswater;
	}*/
	public void setReplacetrap(boolean replacetrap) {
		this.replacetrap = replacetrap;
	}
	public boolean isReplacetrap() {
		return replacetrap;
	}
	public void setSendremovetext(boolean sendremovetext) {
		this.sendremovetext = sendremovetext;
	}
	public boolean isSendremovetext() {
		return sendremovetext;
	}
	public void setCommandsend(boolean commandsend) {
		this.commandsend = commandsend;
	}
	public boolean isCommandsend() {
		return commandsend;
	}
	public void setSendoverwrite(boolean sendoverwrite) {
		this.sendoverwrite = sendoverwrite;
	}
	public boolean isSendoverwrite() {
		return sendoverwrite;
	}
	public void setSendoverwritescommands(boolean sendoverwritescommands) {
		this.sendoverwritescommands = sendoverwritescommands;
	}
	public boolean isSendoverwritescommands() {
		return sendoverwritescommands;
	}
	public void setTeleblockstring(String teleblockstring) {
		this.teleblockstring = teleblockstring;
	}
	public String getTeleblockstring() {
		return teleblockstring;
	}
	public void setTrapblocks(String[] trapblocks) 
	{
		this.trapblocks = trapblocks;
	}
	public String[] getTrapblocks() 
	{
		return trapblocks;
	}
	public void setFactionsEnabled(boolean factionsEnabled) {
		this.factionsEnabled = factionsEnabled;
	}
	public boolean isFactionsEnabled() {
		return factionsEnabled;
	}
	public void setPermissionsEnabled(boolean permissionsEnabled) {
		this.permissionsEnabled = permissionsEnabled;
	}
	public boolean isPermissionsEnabled() {
		return permissionsEnabled;
	}
	public void setChestshieldId(String chestshieldId) {
		this.chestshieldId = chestshieldId;
	}
	public String getChestshieldId() {
		return chestshieldId;
	}
	public void setChestrange(int chestrange) {
		this.chestrange = chestrange;
	}
	public int getChestrange() {
		return chestrange;
	}
	public void setWeblength(int weblength) {
		this.weblength = weblength;
	}
	public int getWeblength() {
		return weblength;
	}
	public void setWebturretblockId(String webturretblockId) {
		this.webturretblockId = webturretblockId;
	}
	public String getWebturretblockId() {
		return webturretblockId;
	}
	public void setWebtime(int webtime) {
		this.webtime = webtime;
	}
	public int getWebtime() {
		return webtime;
	}
	public void setWebturretCost(double d) {
		this.webturretCost = d;
	}
	public double getWebturretCost() {
		return webturretCost;
	}
	public void setFlameturretCost(double d) {
		this.flameturretCost = d;
	}
	public double getFlameturretCost() {
		return flameturretCost;
	}
	public void setSensorCost(double d) {
		this.sensorCost = d;
	}
	public double getSensorCost() {
		return sensorCost;
	}
	public void setChestshieldCost(double d) {
		this.chestshieldCost = d;
	}
	public double getChestshieldCost() {
		return chestshieldCost;
	}
	public void setTeleblockshieldCost(double d) {
		this.teleblockshieldCost = d;
	}
	public double getTeleblockshieldCost() {
		return teleblockshieldCost;
	}
	public void setSendsignCost(double d) {
		this.sendsignCost = d;
	}
	public double getSendsignCost() {
		return sendsignCost;
	}
	public void setEqualsignCost(double d) {
		this.equalsignCost = d;
	}
	public double getEqualsignCost() {
		return equalsignCost;
	}
	public void setMsgsignCost(int msgsignCost) {
		this.msgsignCost = msgsignCost;
	}
	public double getMsgsignCost() {
		return msgsignCost;
	}
	public void setTrapdoorCost(double d) {
		this.trapdoorCost = d;
	}
	public double getTrapdoorCost() {
		return trapdoorCost;
	}
	public void setEcon(boolean econ) {
		this.econ = econ;
	}
	public boolean isEcon() {
		return econ;
	}
	public void setArrowturretId(String string) {
		this.arrowturretId = string;
	}
	public String getArrowturretId() {
		return arrowturretId;
	}
	public void setArrowturretCost(double arrowturretCost) {
		this.arrowturretCost = arrowturretCost;
	}
	public double getArrowturretCost() {
		return arrowturretCost;
	}
	public void setSensorBroadcastDist(int sensorBroadcastDist) {
		this.sensorBroadcastDist = sensorBroadcastDist;
	}
	public int getSensorBroadcastDist() {
		return sensorBroadcastDist;
	}
	public void setMsgOnlyBuilder(boolean msgOnlyBuilder) {
		this.msgOnlyBuilder = msgOnlyBuilder;
	}
	public boolean isMsgOnlyBuilder() {
		return msgOnlyBuilder;
	}
	public void setTownyEnabled(boolean townyEnabled) {
		this.townyEnabled = townyEnabled;
	}
	public boolean isTownyEnabled() {
		return townyEnabled;
	}
	public void setEconomy(Economy economy) {
		this.economy = economy;
	}
	public Economy getEconomy() {
		return economy;
	}
	public void setSpoutEnabled(boolean spoutEnabled) {
		this.spoutEnabled = spoutEnabled;
	}
	public boolean isSpoutEnabled() {
		return spoutEnabled;
	}

	public void setReceiverList(List<Receiver> recList) {
		this.recList = recList;
	}

	public List<Receiver> getReceiverList() {
		return recList;
	}

	public void setTransmitterCost(double transmitterCost) {
		this.transmitterCost = transmitterCost;
	}

	public double getTransmitterCost() {
		return transmitterCost;
	}

	public void setReceiverCost(double receiverCost) {
		this.receiverCost = receiverCost;
	}

	public double getReceiverCost() {
		return receiverCost;
	}

	public void setRadioRange(int radioRange) {
		this.radioRange = radioRange;
	}

	public int getRadioRange() {
		return radioRange;
	}

	public void setRadioCrossWorld(boolean radioCrossWorld) {
		this.radioCrossWorld = radioCrossWorld;
	}

	public boolean canRadioCrossWorld() {
		return radioCrossWorld;
	}

	public void setTelepadBlockId(String string) {
		this.telepadBlockId = string;
	}

	public String getTelepadBlockId() {
		return telepadBlockId;
	}

	public void setTelepadTowerId(String string) {
		this.telepadTowerId = string;
	}

	public String getTelepadTowerId() {
		return telepadTowerId;
	}

	public void setTelepadTowerTopId(String string) {
		this.telepadTowerTopId = string;
	}

	public String getTelepadTowerTopId() {
		return telepadTowerTopId;
	}

	public void setTelepadSupportId(String telepadSupportId) {
		this.telepadSupportId = telepadSupportId;
	}

	public String getTelepadSupportId() {
		return telepadSupportId;
	}

	public void setTelepadMaxLength(int telepadMaxLength) {
		this.telepadMaxLength = telepadMaxLength;
	}

	public int getTelepadMaxLength() {
		return telepadMaxLength;
	}

	public void setTelepadMaxHeight(int telepadMaxHeight) {
		this.telepadMaxHeight = telepadMaxHeight;
	}

	public int getTelepadMaxHeight() {
		return telepadMaxHeight;
	}

	public void setPadList(List<Telepad> padList) {
		this.padList = padList;
	}

	public List<Telepad> getPadList() {
		return padList;
	}
}