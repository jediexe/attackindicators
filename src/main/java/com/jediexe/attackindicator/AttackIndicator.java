package com.jediexe.attackindicator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import lotr.client.LOTRAttackTiming;
import lotr.client.LOTRTickHandlerClient;
import lotr.common.LOTRConfig;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRMod;
import lotr.common.LOTRPlayerData;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.entity.npc.LOTRTradeable;
import lotr.common.fac.LOTRAlignmentValues;
import lotr.common.fac.LOTRFaction;
import lotr.common.item.LOTRWeaponStats;
import lotr.common.world.LOTRWorldProvider;
import lotr.common.world.spawning.LOTRSpawnerNPCs;
import lotr.common.world.spawning.LOTRTravellingTraderSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

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
	
	public void entityhitAlignment (Entity entity) throws NullPointerException {
		if (mc==null) return;
		if (mc.thePlayer==null) return;
		if (mc.objectMouseOver==null) return;
		if (mc.objectMouseOver.entityHit==null) return;
		if (mc.objectMouseOver.entityHit==mc.thePlayer) return;
		if (!mc.objectMouseOver.entityHit.isEntityAlive()) return;
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
				if (Main.showAlignment) {
					if ((mc.theWorld.provider instanceof LOTRWorldProvider || LOTRConfig.alwaysShowAlignment) && Minecraft.isGuiEnabled()) {
						if (FMLClientHandler.instance().isGUIOpen(GuiChat.class) || mc.currentScreen != null) return;
						if ((mc.currentScreen == null || mc.currentScreen instanceof lotr.client.gui.LOTRGuiMessage) && !mc.gameSettings.keyBindPlayerList.getIsKeyPressed() && !mc.gameSettings.showDebugInfo) {
							LOTRPlayerData pd = LOTRLevelData.getData(mc.thePlayer);
							if (pd==null) return;
							if (pd.getViewingFaction()==null) return;
							ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
							int width = resolution.getScaledWidth();
							resolution.getScaledHeight();
							int x = width / 2 + LOTRConfig.alignmentXOffset;
							int y = 4 + LOTRConfig.alignmentYOffset;
							int textX = Math.round(x);
							int textY = Math.round(y + 27.0f + 4.0F);
							String align = LOTRAlignmentValues.formatAlignForDisplay(pd.getAlignment(pd.getViewingFaction())) + "";
							LOTRTickHandlerClient.drawAlignmentText(mc.fontRenderer, textX - mc.fontRenderer.getStringWidth(align) / 2, textY + mc.fontRenderer.FONT_HEIGHT + 3, align, 1.0f);
						}
					}
				}
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
			try {
				if (mc==null) return;
				if (mc.thePlayer==null) return;
				if (mc.objectMouseOver==null) return;
				if (mc.objectMouseOver.entityHit==null) {
					inrange = false;
					return;
				}
				if (mc.objectMouseOver.entityHit==mc.thePlayer) {
					inrange = false;
					return;
				}
				if (!mc.objectMouseOver.entityHit.isEntityAlive()) {
					inrange = false;
					return;
				}
				if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && Main.showInrange) {
					if ((mc.thePlayer.capabilities.isFlying && Main.whileFlying ) || !mc.thePlayer.capabilities.isFlying) {
						if (mc.objectMouseOver.entityHit.getCommandSenderName()==null) return;
						String name = mc.objectMouseOver.entityHit.getCommandSenderName();
						String entitytype = name;
						if (name.contains(", the ")) {
							String[] entityname = name.split(", the ", 2);
							entitytype = entityname[1];
						}
						if (Main.blacklistedEntities.toString().contains(entitytype)) return;
						if (mc.thePlayer.isRiding()) {
							if (mc.thePlayer.ridingEntity==null) return;
							if (mc.objectMouseOver.entityHit==mc.thePlayer.ridingEntity) {
								inrange = false;
								return;
							}
							if (mc.objectMouseOver.entityHit==mc.thePlayer) {
								inrange = false;
								return;
							}
							inrange = true;
							entityhitAlignment(Minecraft.getMinecraft().objectMouseOver.entityHit);
						}
						if (!mc.thePlayer.isRiding()) {
							inrange = true;
							entityhitAlignment(Minecraft.getMinecraft().objectMouseOver.entityHit);
						}
					}
				}
			}
			catch (Exception e) {
				inrange = false;
				System.err.println(e);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onClientChatReceived(ClientChatReceivedEvent event) {
		if (!Main.showTraderCoords) return;
		String msg = event.message.getUnformattedText();
		if (msg.contains(" has arrived nearby") || msg.contains(" has arrived near " + mc.thePlayer.getCommandSenderName())) {
			for (int i=0; i<mc.theWorld.loadedEntityList.size(); i++) {
				Entity e = (Entity)mc.theWorld.loadedEntityList.get(i);
				if (e instanceof LOTRTradeable) {
					String tradername = msg.split(" has arrived near")[0];
					System.out.println(tradername);
					if (e.toString().contains(tradername)) {
						event.setCanceled(true);
						System.out.println(e);
						mc.thePlayer.addChatMessage(new ChatComponentText(event.message.getFormattedText() + " at: \2473x" + Math.round(e.posX) + " y" + Math.round(e.posY) + " z" + Math.round(e.posZ)));
					}
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
