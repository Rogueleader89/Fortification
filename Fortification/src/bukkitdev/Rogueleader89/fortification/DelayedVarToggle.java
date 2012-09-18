package bukkitdev.Rogueleader89.fortification;
/*
 * This acts as a rather silly all purpose delaying class for changing variables related to a player such as
 * cooldown on areaalert and factionalert messages. Due to the lack of string switches in java prior to JDK 7
 * it uses an int to decide which variable to switch... Ideally, once java 7 is common this should be switched
 * to a string for readability purposes...
 * 0 = areaAlert
 * 1 = factionAlert
 */
public class DelayedVarToggle implements Runnable {
	FortPlayer p;
	int toggle;
	boolean val;
	
	public DelayedVarToggle(FortPlayer fp,int var, boolean value)
	{
		p = fp;
		toggle = var;
	}
	@Override
	public void run() {
		switch(toggle)
		{
			case 0://areaalert
				p.setIgnoreAreaAlert(val);
				break;
			case 1://factionalert
				p.setIgnoreFactionAlert(val);
				break;
			case 2://townalert
				p.setIgnoreTownAlert(val);
				break;
			case 4://nationalert
				p.setIgnoreNationAlert(val);
				break;
		}
		
	}

}
