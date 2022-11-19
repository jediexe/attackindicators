package com.jediexe.attackindicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Mod(name = Main.NAME, modid = Main.MODID, version = Main.VERSION, acceptedMinecraftVersions = "[1.7.10]", guiFactory = "com.jediexe.attackindicator.IndicatorGuiFactory")

public class Main{
	
	public static final String NAME = "LOTR Attack Indicator";
    public static final String MODID = "attackindicator";
    public static final String VERSION = "1.6";
    
    public static Configuration config = new Configuration(new File("config/indicator.cfg"));
    Property showInrangeP = config.get(Configuration.CATEGORY_GENERAL, "showInrange", true, "Show the indicator if an entity is in range");
	Property changesColorBasedOnAlignmentP = config.get(Configuration.CATEGORY_GENERAL, "changesColorBasedOnAlignment", true, "The indicator has different colors for allies, enemies, and neutrals by default. Set to false to disable");
	Property transparencyP = config.get(Configuration.CATEGORY_GENERAL, "transparency", 9, "The transparency of the indicator. 0 is the lowest and 10 is the highest. I recommend between 7 and 10.");
	Property scaleP = config.get(Configuration.CATEGORY_GENERAL, "scale", 2,  "The scale of the indicator. 1 is the lowest and 100 is the highest. I recommend 2 (small)");
	Property heightP = config.get(Configuration.CATEGORY_GENERAL, "height", 10, "The height of the indicator in relation to the crosshair. Negative is higher and positive is lower. I recommend 10 (just below crosshair)");
    Property blacklistedEntitiesp = config.get(Configuration.CATEGORY_GENERAL, "blacklistedEntities", "", "Blacklisted entities list, list entitity names seperated by commas");
	
    public static boolean showInrange;
    public static boolean changesColorBasedOnAlignment;
    public static int transparency;
    public static int scale;
    public static int height;
    public static String blacklistedEntities;
    
    public static void load(Configuration config) {
    	Property showInrangeP = config.get(Configuration.CATEGORY_GENERAL, "showInrange", true, "Show the indicator if an entity is in range");
    	Property changesColorBasedOnAlignmentP = config.get(Configuration.CATEGORY_GENERAL, "changesColorBasedOnAlignment", true, "The indicator has different colors for allies, enemies, and neutrals by default. Set to false to disable");
    	Property transparencyP = config.get(Configuration.CATEGORY_GENERAL, "transparency", 9, "The transparency of the indicator. 0 is the lowest and 10 is the highest. I recommend between 7 and 10.");
    	Property scaleP = config.get(Configuration.CATEGORY_GENERAL, "scale", 2,  "The scale of the indicator. 1 is the lowest and 100 is the highest. I recommend 2 (small)");
    	Property heightP = config.get(Configuration.CATEGORY_GENERAL, "height", 10, "The height of the indicator in relation to the crosshair. Negative is higher and positive is lower. I recommend 10 (just below crosshair)");
    	Property blacklistedEntitiesp = config.get(Configuration.CATEGORY_GENERAL, "blacklistedEntities", "", "Blacklisted entities list, list entitity names seperated by commas");
    	
    	showInrange = showInrangeP.getBoolean();
    	changesColorBasedOnAlignment = changesColorBasedOnAlignmentP.getBoolean();
    	transparency = transparencyP.getInt();
    	scale = scaleP.getInt();
    	height = heightP.getInt();
    	blacklistedEntities = blacklistedEntitiesp.getString();
    	if (config.hasChanged()) {
            config.save();
		}
    }
    
    public static void setupAndLoad(FMLPreInitializationEvent event) {
    	config.load();
    	Main.load(config);
    }
    
	@EventHandler
    public void preinit(FMLPreInitializationEvent event){
    	Main.setupAndLoad(event);
    }
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(AttackIndicator.instance);
		FMLCommonHandler.instance().bus().register(new ConfigChangedHandler());
	}
}
