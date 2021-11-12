package eu.keray;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import eu.keray.buttfucc.Ruchanie;

public class PluginRuchanie extends JavaPlugin implements Listener {
	
	boolean newVersionAvailable = false;
	String newVersionString = null;
	
	Ruchanie ruchanie;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		ruchanie = new Ruchanie(this);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	
    	if(player.isOp()) {
    		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
    			try {
    				HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    				
    				HttpRequest req = HttpRequest.newBuilder(new URI("https://dev.keray.eu/mc/ruchanie.txt")).GET().build();
    				HttpResponse<String> response = http.send(req, BodyHandlers.ofString());
    				
    				Version newVersion = new Version(response.body());
    				Version oldVersion = new Version(this.getDescription().getVersion());
    				
    				if(oldVersion.compareTo(newVersion) < 0) {
    					newVersionString = newVersion.toString();
    					newVersionAvailable = true;
    					
    		    		Bukkit.getScheduler().scheduleSyncDelayedTask(this, ()->{
    		    			
    		    			if(!player.isOnline())
    		    				return;
    		    			
    		        		String locale = player.getLocale();
    		        		if( locale != null && (locale.contains("pl") || locale.contains("PL")) ) {
    		        			player.sendMessage(ChatColor.DARK_AQUA + "Nowa wersja Ruchanie v" + newVersionString + " dostępna na: "
    		        					+ ChatColor.GREEN + ChatColor.UNDERLINE + "https://dev.keray.eu/mc/ruchanie.html");
    		        		} else {
    		        			player.sendMessage(ChatColor.DARK_AQUA + "New version Buttfucc v" + newVersionString + " available at: "
    		        					+ ChatColor.GREEN + ChatColor.UNDERLINE + "https://dev.keray.eu/mc/buttfucc.html");
    		        		}
    		    		}, 20 * 3);
    		    		
    				}
    			} catch (Exception e) {
    				//System.err.println("Ruchanie version checking error: " + e);
    			}
    		});
    	}
    }

}
