package eu.keray.buttfucc;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import sun.misc.Unsafe;

public class Magic {

	public static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static double getLastYaw(Entity ent) {
		return ((CraftEntity) ent).getHandle().getYRot();
	}

}
