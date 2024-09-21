package com.wdcftgg.customfishing.cilent;

import com.wdcftgg.customfishing.CustomFishing;
import com.wdcftgg.customfishing.entity.CFEntityFishHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCFFish extends RenderFish {

    public static final Factory FACTORY = new Factory();

    public RenderCFFish(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    public static class Factory implements IRenderFactory<CFEntityFishHook> {

        @Override
        public Render<? super CFEntityFishHook> createRenderFor(RenderManager manager) {
            return new RenderCFFish(manager);
        }
    }
}