package com.jediexe.attackindicator;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import lotr.client.LOTRAttackTiming;
import lotr.client.LOTRTickHandlerClient;
import lotr.common.item.LOTRWeaponStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class AttackIndicator {

	public static AttackIndicator instance = new AttackIndicator();
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static ResourceLocation meterTexture = new ResourceLocation("attackindicator:gui/attackMeter.png");
	public static RenderItem itemRenderer = new RenderItem();
	
	public boolean inrange = false;
	ItemStack item;
	
	double lerpX;
	double lerpU;
	
	@SubscribeEvent
	public void renderIndicator(RenderGameOverlayEvent.Pre event) throws NullPointerException {
		if (mc.theWorld != null && mc.thePlayer != null) {
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
					mc.getTextureManager().bindTexture(meterTexture);
					GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
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
					if (inrange==true && fullAttackTime==0) {
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
			if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
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
