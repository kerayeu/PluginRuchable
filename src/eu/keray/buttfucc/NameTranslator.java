package eu.keray.buttfucc;

import org.bukkit.entity.*;

public class NameTranslator {
    
    private String getEntityNamed(Entity ent) {
    	String text = ent.getName();

    	if(ent instanceof Ageable) {
    		if(((Ageable) ent).isAdult() == false) {
    			text = "Baby " + text;
    		}
    	}
    	
    	if(ent.getCustomName() != null) {
    		text = text + " " + ent.getCustomName();
    	}
    	
		return text;
    }
	
	public String getButtfuccedString(Player player, Entity buttfucced, boolean brutal) {
		String entityName = getEntityNamed(buttfucced);
		return entityName + " was " + (brutal? "brutally " : "") + "buttfucked by " + player.getName();
	}
}
