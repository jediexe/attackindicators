package com.jediexe.attackindicator;

import java.io.File;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Mod(name = Main.NAME, modid = Main.MODID, version = Main.VERSION, acceptedMinecraftVersions = "[1.7.10]", guiFactory = "com.jediexe.attackindicator.IndicatorGuiFactory")

public class Main {
	
	public static final String NAME = "LOTR Attack Indicator";
    public static final String MODID = "attackindicator";
    public static final String VERSION = "1.6.3";
    
    public static Configuration config = new Configuration(new File("config/indicator.cfg"));

    public static boolean showInrange;
    public static boolean changesColorBasedOnAlignment;
    public static boolean whileFlying;
    public static boolean showAlignment;
    public static int transparency;
    public static int scale;
    public static int height;
    public static String blacklistedEntities;
    
    public static void load(Configuration config) {
    	Property showInrangeP = config.get(Configuration.CATEGORY_GENERAL, "showInrange", true, "Show the indicator if an entity is in range");
    	showInrange = showInrangeP.getBoolean();
    	Property changesColorBasedOnAlignmentP = config.get(Configuration.CATEGORY_GENERAL, "changesColorBasedOnAlignment", true, "The indicator has different colors for allies, enemies, and neutrals by default. Set to false to disable");
    	changesColorBasedOnAlignment = changesColorBasedOnAlignmentP.getBoolean();
    	Property whileFlyingP = config.get(Configuration.CATEGORY_GENERAL, "whileFlying", false, "Show the indicator while flying. Disabled by default due to conflicts with other mods that modify flying");
    	whileFlying = whileFlyingP.getBoolean();
    	Property showAlignmentP = config.get(Configuration.CATEGORY_GENERAL, "showAlignment", true, "QOL show exact alignment value under the alignment bar (in this mod until I can make a new addon)");
    	showAlignment = showAlignmentP.getBoolean();
    	Property transparencyP = config.get(Configuration.CATEGORY_GENERAL, "transparency", 8, "The transparency of the indicator. 0 is the lowest and 10 is the highest. I recommend between 7 and 10.");
    	transparency = transparencyP.getInt();
    	Property scaleP = config.get(Configuration.CATEGORY_GENERAL, "scale", 2,  "The scale of the indicator. 1 is the lowest and 100 is the highest. I recommend 2 (small)");
    	scale = scaleP.getInt();
    	Property heightP = config.get(Configuration.CATEGORY_GENERAL, "height", 10, "The height of the indicator in relation to the crosshair. Negative is higher and positive is lower. I recommend 10 (just below crosshair)");
    	height = heightP.getInt();
    	Property blacklistedEntitiesp = config.get(Configuration.CATEGORY_GENERAL, "blacklistedEntities", "", "Blacklisted entities list, list entitity names seperated by commas");
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
