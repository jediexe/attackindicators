package com.jediexe.attackindicator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.client.LOTRAttackTiming;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRMod;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.fac.LOTRFaction;
import lotr.common.item.LOTRWeaponStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;

public class AttackIndicator {

	public static AttackIndicator instance = new AttackIndicator();
	
	public static ResourceLocation ally = new ResourceLocation("attackindicator:gui/ally.png");
	public static ResourceLocation enemy = new ResourceLocation("attackindicator:gui/enemy.png");
	public static ResourceLocation neutral = new ResourceLocation("attackindicator:gui/neutral.png");
	public static ResourceLocation meterTexture = new ResourceLocation("attackindicator:gui/attackMeter.png");
	public static RenderItem itemRenderer = new RenderItem();
	public static Minecraft mc = Minecraft.getMinecraft();

	float transparency = (float)Main.transparency/10.0f;
	public boolean inrange = false;
	int alignment=10;
	ItemStack item;
	double lerpX;
	double lerpU;
	
	public void entityhitAlignment (Entity entity){
		if (entity instanceof LOTREntityNPC) {
			LOTRFaction entityfaction = LOTRMod.getNPCFaction(entity);
			if (LOTRLevelData.getData(mc.thePlayer).getAlignment(entityfaction)<0.0) {
				alignment=-1;
			}
			if (LOTRLevelData.getData(mc.thePlayer).getAlignment(entityfaction)>0.0) {
				alignment=1;
			}
			if (LOTRLevelData.getData(mc.thePlayer).getAlignment(entityfaction)==0.0) {
				alignment=0;
			}
		}
		else {
			alignment=10;
		}
	}
	
	@SubscribeEvent
	public void renderIndicator(RenderGameOverlayEvent.Pre event) throws NullPointerException {
		if (mc.theWorld != null && mc.thePlayer != null && Loader.isModLoaded("lotr")) {
			if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
				int minX = event.resolution.getScaledWidth() / 2 - (Main.scale)*5;
				int maxX = event.resolution.getScaledWidth() / 2 + (Main.scale)*5;
				int maxY = event.resolution.getScaledHeight() / 2 + (Main.height);
				int minY = maxY - (Main.scale)*2;
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
						if (Main.changesColorBasedOnAlignment) {
							if (alignment==10) {
								mc.getTextureManager().bindTexture(meterTexture);
							}
							if (alignment==1) {
								mc.getTextureManager().bindTexture(ally);
							}
							if (alignment==-1) {
								mc.getTextureManager().bindTexture(enemy);
							}
							if (alignment==0) {
								mc.getTextureManager().bindTexture(neutral);
							}
						}
						else {
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
						if (Main.changesColorBasedOnAlignment) {
							GL11.glColor4f(1.0f, 1.0f, 1.0f, transparency);
							tessellator.startDrawingQuads();
							tessellator.addVertexWithUV(minX, minY, 0.0, minU, minV + maxV * 2.0);
							tessellator.addVertexWithUV(minX, maxY, 0.0, minU, maxV + maxV * 2.0);
							tessellator.addVertexWithUV(maxX, maxY, 0.0, maxU, maxV + maxV * 2.0);
							tessellator.addVertexWithUV(maxX, minY, 0.0, maxU, minV + maxV * 2.0);
							tessellator.draw();
						}
					}
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}
			}
			if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && Main.showInrange) {
				if ((Minecraft.getMinecraft().thePlayer.capabilities.isFlying && Main.whileFlying) || !Minecraft.getMinecraft().thePlayer.capabilities.isFlying) {
					if (!Minecraft.getMinecraft().thePlayer.isOnLadder()) {
						if (Minecraft.getMinecraft().objectMouseOver.entityHit!=null) {
							if (Minecraft.getMinecraft().objectMouseOver.entityHit.getEntityId()!=mc.thePlayer.getEntityId()) {
								String name = Minecraft.getMinecraft().objectMouseOver.entityHit.getCommandSenderName();
								String entitytype = name;
								if (name.contains(", the ")) {
									String[] entityname = name.split(", the ", 2);
									entitytype = entityname[1];
								}
								if (!Main.blacklistedEntities.toString().contains(entitytype)) {
									if (Minecraft.getMinecraft().thePlayer.isRiding()) {
										if (Minecraft.getMinecraft().thePlayer.ridingEntity!=null) {
											if (Minecraft.getMinecraft().objectMouseOver.entityHit==Minecraft.getMinecraft().thePlayer.ridingEntity) {
												inrange = false;
											}
											else {
												inrange = true;
												entityhitAlignment(Minecraft.getMinecraft().objectMouseOver.entityHit);
											}
										}
										else {
											inrange = true;
											entityhitAlignment(Minecraft.getMinecraft().objectMouseOver.entityHit);
										}
									}
									else {
										inrange = true;
										entityhitAlignment(Minecraft.getMinecraft().objectMouseOver.entityHit);
									}
								}
								else {
									inrange = false;
								}
							}
							else {
								inrange = false;
							}
						}
						else {
							inrange = false;
						}
					}
					else {
						inrange = false;
					}
				}
				else {
					inrange = false;
				}
			}
		}
	}

	public static void update() {
		float transparency = (float)Main.transparency/10.0f;
		double lerpX;
		double lerpU;
	}
}
