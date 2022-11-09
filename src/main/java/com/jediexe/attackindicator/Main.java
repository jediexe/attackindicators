package com.jediexe.attackindicator;

import java.io.File;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(name = Main.NAME, modid = Main.MODID, version = Main.VERSION, acceptedMinecraftVersions = "[1.7.10]")

public class Main{
	
	public static final String NAME = "LOTR Attack Indicator";
    public static final String MODID = "attackindicator";
    public static final String VERSION = "1.4";
    
    public static boolean showInrange;
    public static boolean changesColorBasedOnAlignment;
    public static float transparency;
    public static int scale;
    public static int height;
    
    public void initConfiguration(FMLInitializationEvent event) {
    	Configuration config = new Configuration(new File("config/attackindicator.cfg"));
    	config.load();
    	showInrange = config.getBoolean("showInrange", "config", true, 
    			"Show the indicator if an entity is in range");
    	changesColorBasedOnAlignment = config.getBoolean("changesColorBasedOnAlignment", "config", true, 
    			"The indicator has different colors for allies, enemies, and neutrals by default. Set to false to disable");
    	transparency = config.getFloat("transparency", "config", 0.9f, 0.0f, 1.0f, 
    			"The transparency of the indicator. 0.0 is the lowest and 1.0 is the highest. "
    			+ "I recommend between 0.7 and 1.0.");
    	scale = config.getInt("scale", "config", 2, 1, 10, 
    			"The scale of the indicator. 1 is the lowest and 100 is the highest. "
    			+ "I recommend 2 (small)");
    	height = config.getInt("height", "config", 10, -300, 300, 
    			"The height of the indicator in relation to the crosshair. -300 is the highest and 300 is the lowest. "
    			+ "I recommend 10 (just below crosshair)");
    	config.save();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    	this.initConfiguration(event);
    	MinecraftForge.EVENT_BUS.register(AttackIndicator.instance);
    }
    
}
