package eu.keray.buttfucc;


import static org.bukkit.entity.EntityType.*;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.potion.*;

import eu.keray.PluginRuchanie;
import eu.keray.buttfucc.ValueMap.ReduceObserver;

public class Ruchanie implements Listener, Runnable, ReduceObserver<Entity> {

	private static Set<String> sukkubs = new HashSet<>();
	static {
		sukkubs.add("Sukkub");
		sukkubs.add("Keray");
	}
	
	ValueMap<Entity> wyruchane = new ValueMap<>(300);
	
    public Ruchanie(PluginRuchanie plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 120, 120);
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
    	boolean sneaking = event.getPlayer().isSneaking();
    	
    	if(!sneaking) {
    		Player ruchacz = event.getPlayer();
    		
    		if(ruchacz.getGameMode() == GameMode.SPECTATOR)
    			return;
    		if(ruchacz.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
    			return;
    		
    		Location loc = ruchacz.getLocation();
    		double myLookAngle = normalize(loc.getYaw());
    		
    		List<Entity> entities = ruchacz.getNearbyEntities(1, 1, 1);
    		
    		for(Entity ent : entities) {
    			
    			if(ent.isDead())
    				continue;
				if(ent.isInsideVehicle())
					continue;
				if(ent.isInvulnerable())
					continue;
    			
    			if(!(ent instanceof LivingEntity)) 
    				continue;
    			
				LivingEntity living = (LivingEntity) ent;
				
				if(living.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
					continue;
				if(living.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
					continue;
				if(living.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
					continue;
				if(living.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
					continue;
				
				Location eloc = ent.getLocation();
    			
    			float dx = (float) (eloc.getX() - loc.getX());
    			float dz = (float) (eloc.getZ() - loc.getZ());
    			
    			double targetAngle = normalize(Math.toDegrees(Math.atan2(-dx, dz)));
    			double targetLookAngle = normalize(Magic.getLastYaw(ent));
    			
    			if(Math.abs(normalize(targetAngle - myLookAngle)) > 30)
    				continue;
    			if(Math.abs(normalize(targetLookAngle - myLookAngle)) > 30)
    				continue;
    			
    			
    			int nval = living.hasPotionEffect(PotionEffectType.WEAKNESS)? 10 : wyruchane.increment(ent, 1);

    			if(ent instanceof Creeper && nval >= 5) {
        			Bukkit.broadcastMessage(ChatColor.GOLD + "Gracz " + ruchacz.getName() + " wyruchał creepera.");
    				Location el = ent.getLocation();
    				ent.remove();
    				ent.getWorld().createExplosion(el.getX(), el.getY() + 1, el.getZ(), 5, true, true);
    				continue;
    			}
    			
    			
    			if(nval >= 10) {
    				
    				EntityType type = living.getType();
    				
    				boolean brutalnie = false;
    				
    				if(living instanceof Player) {
    					if(sukkubs.contains(living.getName())) {
    						Player temp = ruchacz;
    						ruchacz = (Player) living;
    						living = temp;
    						brutalnie = true;
    					}
    				}
    				
    				boolean addSlowDigging = true;
    				
    				if(type == WITHER_SKELETON) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 30, 1, true, false, true));
    				}
    				
    				if(type == COD || type == TROPICAL_FISH || type == SALMON) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 30, 0, true, false, true));
    				}
    				
    				if(type == BAT) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 30, 0, true, false, true));
    				}
    				
    				if(type == BEE) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 30, 0, true, false, true));
    				}
    				
    				if(type == RABBIT) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 30, 1, true, false, true));
    				}
    				
    				if(type == BLAZE) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 30, 1, true, false, true));
    				}
    				
    				if(type == DONKEY) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 20 * 30, 1, true, false, true));
    				}
    				
    				if(type == PARROT) {
    					addSlowDigging = false;
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 30, 1, true, false, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 30, 0, true, false, true));
    				}
    				
    				
    				if(type == CAVE_SPIDER || type == SPIDER) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 30, 1, true, false, true));
    				}
    				
    				if(addSlowDigging) {
        				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 20, 1, true, false, true));
    				}
    				
    				if(type == CHICKEN) {
    					if(Math.random()<0.1) {
    						living.setCustomName("BAFOMET");
    						living.setCustomNameVisible(true);
            				living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 300, 0, true, false, true));
            				living.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 300, 2, true, false, true));
            				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 300, 2, true, false, true));
            				ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 300, 0, true, false, true));
//    						Bukkit.getOnlinePlayers().forEach((p)->{
//    							p.sendTitle("BAFOMET", "", 10, 70, 20);
//    						});
    					}
    				}
    				
    				
    				if(brutalnie) {
    					broadcastButtfucc((Player) living, ruchacz, true);
//        				String nazwa = getNazwa(ruchacz);
//    					Bukkit.broadcastMessage(ChatColor.GOLD + "Gracz " + living.getName() + " został brutalnie samozgwałcony przez " + nazwa + ".");
    				} else {
    					broadcastButtfucc(ruchacz, living, false);
//        				String nazwa = getNazwa(living);
//    					Bukkit.broadcastMessage(ChatColor.GOLD + "Gracz " + ruchacz.getName() + " wyruchał " + nazwa + ".");
    				}
        			
        			Sound ds = sounds.get(living.getType());
        			
        			if(ds != null) {
        				Bukkit.getOnlinePlayers().forEach(p->{
        					p.playSound(p.getLocation(), ds, 1f, 0.7f);
        				});
        			}
        			
        			
        			double x = loc.getX() / 2 + eloc.getX() / 2;
        			double z = loc.getZ() / 2 + eloc.getZ() / 2;
        			double y = loc.getY() + 1;
        			
        			ruchacz.getWorld().spawnParticle(Particle.CLOUD, x, y, z, 1024, 0.1, 0.1, 0.1, 0.15);
    				ruchacz.getWorld().playSound(ruchacz.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 0.5f);

    				if(brutalnie) {
    					living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 300, 10, false, true, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 300, 10, false, true, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 300, 10, false, true, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 300, 10, false, true, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 300, 10, false, true, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 300, 10, false, true, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 20 * 300, 10, false, true, true));
        				living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 300, 2, false, true, true));
    	    			ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 5, 0, true, true, true));
    				} else {
    					
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 30, 0, true, false, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 0, true, false, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 30, 0, true, false, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 30, 0, true, true, true));
		    			if(Math.random() < 0.1)
		    				living.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 30, 0, true, true, true));
		    			//living.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 1, true, true));
		    			living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 20, 1, true, true, true));
		    			
    				}
    				
//	    			living.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 10, 0, true, true));
//	    			ruchacz.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 10, 0, true, true));
	    			
	    			if(living instanceof Creature) {
	    				((Creature) living).setTarget(ruchacz);
	    			}

    				if(living instanceof Player) {
    					
    					String locale = ((Player) living).getLocale();
    					
    					boolean isPolish = locale != null && (locale.contains("pl") || locale.contains("PL"));
    					
    	    			if(Math.random() < 0.05) {
    	    				Drowned drowned = (Drowned) living.getWorld().spawnEntity(living.getLocation(), EntityType.DROWNED);
    	    				drowned.setBaby();
    	    				drowned.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 300, 3, true, true, true));
    	    				drowned.setCustomName("Gunwielek");
    	    				drowned.setCustomNameVisible(true);
    	    			}
    	    			
    	    			String title = isPolish ? "Masz ból dupy" : "Pain in the ass";
    	    			String sub = null;
    	    			
	    				if(brutalnie) {
	    					sub = isPolish ? "to był brutalny samogwałt" : "that was brutal";
	    				} else {
	    					sub = (isPolish ? "wyruchał cię " : "you got buttfucked by ") + ruchacz.getName();
	    				}
	    				((Player) living).sendTitle(title, sub, 20, 20 * 6, 20);
	    			}
	    			
    				wyruchane.remove(ent, 0);
    			} else {
    				ruchacz.getWorld().playSound(ruchacz.getLocation(), nval%2==0? Sound.BLOCK_PISTON_EXTEND : Sound.BLOCK_PISTON_CONTRACT, 1f, 1.35f);
    			}
    		}
    	}
    }
    
    private NameTranslator translatorEN = new NameTranslator();
    private NameTranslator translatorPL = new NameTranslatorPL();
    
    private List<Player> tempEnglishPlayers = new ArrayList<>();
    private List<Player> tempPolishPlayers = new ArrayList<>();
    
    private void broadcastButtfucc(Player fuccer, Entity fucced, boolean brutal) {
    	Bukkit.getOnlinePlayers().forEach((plr)->{
    		String locale = plr.getLocale();
    		if( locale != null && (locale.contains("pl") || locale.contains("PL")) ) {
    			tempPolishPlayers.add(plr);
    		} else {
    			tempEnglishPlayers.add(plr);
    		}
    	});
    	
    	if(!tempEnglishPlayers.isEmpty()) {
    		String broadcast = ChatColor.GOLD + translatorEN.getButtfuccedString(fuccer, fucced, brutal);
    		tempEnglishPlayers.forEach(plr -> plr.sendMessage(broadcast));
        	tempEnglishPlayers.clear();
    	}
		String broadcastPL = ChatColor.GOLD + translatorPL.getButtfuccedString(fuccer, fucced, brutal);
		Bukkit.getConsoleSender().sendMessage(broadcastPL);
    	if(!tempPolishPlayers.isEmpty()) {
    		tempPolishPlayers.forEach(plr -> plr.sendMessage(broadcastPL));
        	tempPolishPlayers.clear();
    	}
    	
    }
    

    private static final Map<EntityType, Sound> sounds = new HashMap<>();
    static {

    	sounds.put(EntityType.BLAZE, Sound.ENTITY_BLAZE_DEATH);
    	sounds.put(EntityType.BOAT, Sound.ENTITY_BOAT_PADDLE_LAND);
    	sounds.put(EntityType.CAVE_SPIDER, Sound.ENTITY_SPIDER_DEATH);
    	sounds.put(EntityType.CHICKEN, Sound.ENTITY_CHICKEN_DEATH);
    	sounds.put(EntityType.SHEEP, Sound.ENTITY_SHEEP_DEATH);
    	sounds.put(EntityType.COW, Sound.ENTITY_COW_DEATH);
    	sounds.put(EntityType.ENDERMAN, Sound.ENTITY_ENDERMAN_DEATH);
    	sounds.put(EntityType.PLAYER, Sound.ENTITY_DONKEY_AMBIENT);
    	sounds.put(EntityType.BEE, Sound.ENTITY_BEE_LOOP_AGGRESSIVE);
    	sounds.put(EntityType.WOLF, Sound.ENTITY_WOLF_HOWL);
    	sounds.put(EntityType.VILLAGER, Sound.ENTITY_VILLAGER_DEATH);
    	sounds.put(EntityType.HORSE, Sound.ENTITY_HORSE_DEATH);
    }
    
    
    private static double normalize(double angle) {
		if(angle > 180)
			angle -= 360;
		if(angle < -180)
			angle += 360;
		return angle;
    }
    
	@Override
	public void run() {
		wyruchane.reduce(this);
	}

	@Override
	public void removed(Entity key) { }
}
