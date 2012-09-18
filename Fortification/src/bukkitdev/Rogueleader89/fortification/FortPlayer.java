/*FortPlayer.java
 * 
 * 
 * 
 */
package bukkitdev.Rogueleader89.fortification;
import org.bukkit.entity.Player;

public class FortPlayer {
	private Player p;
	private String name;
	private boolean ignoreAreaAlert;
	private boolean ignoreFactionAlert;
	private boolean online;
	private boolean ignoreTownAlert;
	private boolean ignoreNationAlert;
	
	public FortPlayer(Player fp)
	{
		p = fp;
		setName(p.getName());
		online = p.isOnline();
	}
	public void sendMessage(String message) {
		p.sendMessage(message);
	}
	public void setIgnoreAreaAlert(boolean ignoreAreaAlert) {
		this.ignoreAreaAlert = ignoreAreaAlert;
	}

	public boolean isIgnoreAreaAlert() {
		return ignoreAreaAlert;
	}

	public void setIgnoreFactionAlert(boolean ignoreFactionAlert) {
		this.ignoreFactionAlert = ignoreFactionAlert;
	}

	public boolean isIgnoreFactionAlert() {
		return ignoreFactionAlert;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Player getPlayer() {
		return p;
	}

	public boolean isOnline() {
		return online;
	}

	public void setIgnoreTownAlert(boolean ignoreTownAlert) {
		this.ignoreTownAlert = ignoreTownAlert;
	}

	public boolean isIgnoreTownAlert() {
		return ignoreTownAlert;
	}

	public void setIgnoreNationAlert(boolean ignoreNationAlert) {
		this.ignoreNationAlert = ignoreNationAlert;
	}

	public boolean isIgnoreNationAlert() {
		return ignoreNationAlert;
	}
}
