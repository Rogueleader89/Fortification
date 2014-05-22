package bukkitdev.Rogueleader89.fortification;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SensorEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private String sensorFilter, arg1, arg2;
	private boolean triggerSensor = false;
	private Player player;
	
	public SensorEvent(String sensorFilter, Player player, String arg1, String arg2)
	{
		this.sensorFilter = sensorFilter;
		this.setPlayer(player);
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList() 
	{
	    return handlers;
	}

	public String getSensorFilter() {
		return sensorFilter;
	}

	public void setSensorFilter(String sensorFilter) {
		this.sensorFilter = sensorFilter;
	}

	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	public boolean isTriggered() {
		return triggerSensor;
	}

	public void triggerSensor() {
		this.triggerSensor = true;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
