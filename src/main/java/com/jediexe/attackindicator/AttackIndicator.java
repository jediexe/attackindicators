package com.jediexe.attackindicator;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.client.LOTRAttackTiming;
import lotr.common.item.LOTRWeaponStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class AttackIndicator {

	public static AttackIndicator instance = new AttackIndicator();
	
	public static ResourceLocation meterTexture = new ResourceLocation("attackindicator:gui/attackMeter.png");
	public static ResourceLocation indicatorTexture = new ResourceLocation("attackindicator:gui/indicator.png");
	public static RenderItem itemRenderer = new RenderItem();
	public static Minecraft mc = Minecraft.getMinecraft();
	
	int errorlog = 0;
	String hex = "#" + Main.inrangeColor.toUpperCase();
	float transparency = Main.transparency;
	public boolean inrange = false;
	ItemStack item;
	double lerpX;
	double lerpU;
	
	@SubscribeEvent
	public void renderIndicator(RenderGameOverlayEvent.Pre event) throws NullPointerException {
		if (mc.theWorld != null && mc.thePlayer != null && Loader.isModLoaded("lotr")) {
			if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
				int minX = event.resolution.getScaledWidth() / 2 - 10;
				int maxX = event.resolution.getScaledWidth() / 2 + 10;
				int maxY = event.resolution.getScaledHeight() / 2 + 10;
				int minY = maxY - 4;
				double minU = 0.0;
				double maxU = 1.0;
				double minV = 0.0;
				double maxV = 0.0625;
				item = mc.thePlayer.getHeldItem();
				int fullAttackTime = LOTRAttackTiming.fullAttackTime;
				int prevAttackTime = LOTRAttackTiming.prevAttackTime;
				int attackTime = LOTRWeaponStats.getAttackTimePlayer(item);
				if (inrange==true || prevAttackTime>0) {
					if (fullAttackTime==0) {
						lerpX = maxX + (minX - maxX) * prevAttackTime/attackTime;
						lerpU = maxU + (minU - maxU) * prevAttackTime/attackTime;
					}
					else {
						lerpX = maxX + (minX - maxX) * prevAttackTime/fullAttackTime;
						lerpU = maxU + (minU - maxU) * prevAttackTime/fullAttackTime;
					}
					GL11.glPushMatrix();
					GL11.glEnable(GL11.GL_BLEND);
					Tessellator tessellator = Tessellator.instance;
					if (Main.showInrange) {
						try {
							mc.getTextureManager().bindTexture(indicatorTexture);
							Color.decode(hex);
						}
						catch(NumberFormatException e) {
							if (errorlog<1) {
								System.err.println("No valid hex value provided in the config! Reverting to legacy texture.");
							}
							errorlog+=1;
							mc.getTextureManager().bindTexture(meterTexture);
						}
					}
					else if (!Main.showInrange) {
						mc.getTextureManager().bindTexture(meterTexture);
					}
					GL11.glColor4f(1.0f, 1.0f, 1.0f, transparency);
					tessellator.startDrawingQuads();
					tessellator.addVertexWithUV(minX, minY, 0.0, minU, minV);
					tessellator.addVertexWithUV(minX, maxY, 0.0, minU, maxV);
					tessellator.addVertexWithUV(maxX, maxY, 0.0, maxU, maxV);
					tessellator.addVertexWithUV(maxX, minY, 0.0, maxU, minV);
					tessellator.draw();
					tessellator.startDrawingQuads();
					tessellator.addVertexWithUV(lerpX, minY, 0.0, lerpU, minV + maxV);
					tessellator.addVertexWithUV(lerpX, maxY, 0.0, lerpU, maxV + maxV);
					tessellator.addVertexWithUV(maxX, maxY, 0.0, maxU, maxV + maxV);
					tessellator.addVertexWithUV(maxX, minY, 0.0, maxU, minV + maxV);
					tessellator.draw();
					if (inrange==true && fullAttackTime==0 && Main.showInrange) {
						try {
							float Red = (Color.decode(hex).getRed())/255.0f;
							float Green = (Color.decode(hex).getGreen())/255.0f;
							float Blue = (Color.decode(hex).getBlue())/255.0f;
							GL11.glColor4f(Red, Green, Blue, transparency);
						}
						catch (NumberFormatException e){
							GL11.glColor4f(1.0f, 1.0f, 1.0f, transparency);
						}
						tessellator.startDrawingQuads();
						tessellator.addVertexWithUV(minX, minY, 0.0, minU, minV + maxV * 2.0);
						tessellator.addVertexWithUV(minX, maxY, 0.0, minU, maxV + maxV * 2.0);
						tessellator.addVertexWithUV(maxX, maxY, 0.0, maxU, maxV + maxV * 2.0);
						tessellator.addVertexWithUV(maxX, minY, 0.0, maxU, minV + maxV * 2.0);
						tessellator.draw();
					}
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}
			}
			if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && Main.showInrange) {
				if (!Minecraft.getMinecraft().thePlayer.isOnLadder()) {
					Entity entityhit = Minecraft.getMinecraft().objectMouseOver.entityHit;
					if (entityhit!=null) {
						if (Minecraft.getMinecraft().thePlayer.isRiding()) {
							if (Minecraft.getMinecraft().thePlayer.ridingEntity!=null) {
								if (entityhit==Minecraft.getMinecraft().thePlayer.ridingEntity) {
									inrange = false;
								}
								else {
									inrange = true;
								}
							}
							else {
								inrange = true;
							}
						}
						else {
							inrange = true;
						}
					}
					else {
						inrange = false;
					}
				}
			}
		}
	}
}
