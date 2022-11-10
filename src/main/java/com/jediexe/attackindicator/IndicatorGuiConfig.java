package com.jediexe.attackindicator;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class IndicatorGuiConfig extends GuiConfig {
	
	public IndicatorGuiConfig(GuiScreen parentScreen) {
		super(parentScreen, 
				new ConfigElement(Main.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Main.MODID, 
				false, false, Main.MODID);
	}
	
}