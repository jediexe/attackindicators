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
    public static final String VERSION = "1.3";
    
    public static boolean showInrange;
    public static String inrangeColor;
    public static float transparency;
    
    public void initConfiguration(FMLInitializationEvent event) {
    	Configuration config = new Configuration(new File("config/attackindicator.cfg"));
    	config.load();
    	showInrange = config.getBoolean("showInrange", "config", true, 
    			"Show the indicator if an entity is in range");
    	inrangeColor = config.getString("inrangeColor", "config", "", 
    			"The default color the indicator turns when an entity is in range. "
    			+ "If no color is provided, the legacy inrange indicator will display. "
    			+ "Use hex color and make sure to choose lighter colors. "
    			+ "Examples: FFFFFF (white), FF5B5D (red), FFBE4F (orange), FFFFAF (yellow), A9FFA8 (green), A8F5FF (light blue), A8B6FF (blue), BD66FF (purple), FF66C3 (pink)");
    	transparency = config.getFloat("transparency", "config", 0.9f, 0.0f, 1.0f, 
    			"The transparency of the indicator. 0.0 is the lowest and 1.0 is the highest. "
    			+ "I recommend between 0.7 and 1.0.");
    	config.save();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    	this.initConfiguration(event);
    	MinecraftForge.EVENT_BUS.register(AttackIndicator.instance);
    }
    
}
