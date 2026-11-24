package com.wdcftgg.customfishing.entity;

import com.wdcftgg.customfishing.CFConfig;
import com.wdcftgg.customfishing.crt.FishingConditionInit;
import com.wdcftgg.customfishing.crt.impl.FishingCondition;
import com.wdcftgg.customfishing.util.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class FishingCatch {
    private FishingCatch() {}

    private static Fluid fluidAt(Block block) {
        if (block instanceof IFluidBlock) return ((IFluidBlock) block).getFluid();
        return FluidRegistry.lookupFluidForBlock(block);
    }

    private static boolean matches(FishingCondition rule, EntityFishHook hook, BlockPos pos, Fluid fluid) {
        if (rule.fluid != null && rule.fluid != fluid) return false;
        if (rule.dimension != null && rule.dimension != hook.dimension) return false;
        if (rule.isDay != null && rule.isDay != hook.world.isDaytime()) return false;
        if (rule.altitudeBegin != null && hook.posY < rule.altitudeBegin) return false;
        if (rule.altitudeEnd != null && hook.posY > rule.altitudeEnd) return false;
        if (rule.biome != null && !rule.biome.isEmpty()
                && !rule.biome.equals(hook.world.getBiome(pos).getRegistryName().toString())) return false;
        EntityPlayer player = hook.getAngler();
        if (!rule.fishRods.isEmpty() && !rule.fishRods.contains(player.getHeldItemMainhand().getItem())
                && !rule.fishRods.contains(player.getHeldItemOffhand().getItem())) return false;
        return rule.fishBaits.isEmpty() || rule.fishBaits.stream()
                .anyMatch(item -> !Tools.inventoryGetItem(player.inventory, item).isEmpty());
    }

    public static boolean canBite(EntityFishHook hook, BlockPos pos) {
        IBlockState state = hook.world.getBlockState(pos);
        Fluid fluid = fluidAt(state.getBlock());
        return allowsDefault(state, fluid) || FishingConditionInit.getAllFishingCondition().stream()
                .anyMatch(rule -> matches(rule, hook, pos, fluid));
    }

    private static boolean allowsDefault(IBlockState state, Fluid fluid) {
        return fluid == FluidRegistry.WATER || (CFConfig.FishingInAnyLiquid
                && (state.getMaterial().isLiquid() || state.getBlock() instanceof IFluidBlock));
    }

    public static int reelIn(EntityFishHook hook, BlockPos pos, int luck, boolean inGround, Random random) {
        IBlockState state = hook.world.getBlockState(pos);
        Fluid fluid = fluidAt(state.getBlock());
        EntityPlayer player = hook.getAngler();
        List<FishingCondition> matches = new ArrayList<>();
        if (!state.getMaterial().isLiquid() && !(state.getBlock() instanceof IFluidBlock)) return inGround ? 2 : 0;
        for (FishingCondition rule : FishingConditionInit.getAllFishingCondition()) {
            if (matches(rule, hook, pos, fluid)) matches.add(rule);
        }
        if (matches.isEmpty() && !allowsDefault(state, fluid)) return inGround ? 2 : 0;

        LootContext context = new LootContext.Builder((WorldServer) hook.world)
                .withLuck(luck + player.getLuck()).withPlayer(player).withLootedEntity(hook).build();
        List<ItemStack> items = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        List<FishingCondition> rewards = new ArrayList<>();
        if (matches.isEmpty()) {
            items.addAll(hook.world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING)
                    .generateLootForPools(random, context));
        } else {
            if (CFConfig.OneAtATime) Collections.shuffle(matches, random);
            for (FishingCondition rule : matches) {
                if (rule.chance != null && random.nextFloat() >= rule.chance) continue;
                if (rule.lootRes != null) {
                    items.addAll(hook.world.getLootTableManager().getLootTableFromLocation(rule.lootRes)
                            .generateLootForPools(random, context));
                } else if (rule.entity != null) {
                    Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(rule.entity), hook.world);
                    if (entity == null) {
                        LogManager.getLogger("customfishing").error("未知的钓鱼奖励实体 {}", rule.entity);
                        continue;
                    }
                    if (!rule.entityNbt.isEmpty()) entity.readFromNBT(rule.entityNbt);
                    entities.add(entity);
                } else {
                    items.add(rule.itemStack.copy());
                }
                rewards.add(rule);
                if (CFConfig.OneAtATime) break;
            }
        }

        ItemFishedEvent event = new ItemFishedEvent(items, inGround ? 2 : 1, hook);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.getRodDamage();
        for (FishingCondition reward : rewards) {
            if (reward.damage != null) Tools.damageFishRod(reward.fishRods, player, reward.damage);
            Tools.removeFishBaits(reward.fishBaits, player.inventory);
        }
        double speed = CFConfig.getliquidSpeedMap().getOrDefault(state.getBlock().getRegistryName().toString(), 0F);
        for (ItemStack stack : items) {
            EntityItem item = new EntityItem(hook.world, hook.posX, hook.posY, hook.posZ, stack);
            NBTTagCompound data = item.writeToNBT(new NBTTagCompound());
            data.setShort("Health", (short) 1000);
            item.readEntityFromNBT(data);
            item.setEntityInvulnerable(CFConfig.itemInvulnerable);
            entities.add(item);
            if (stack.getItem() == Items.FISH || stack.getItem() == Items.COOKED_FISH) {
                player.addStat(StatList.FISH_CAUGHT, 1);
            }
        }
        double dx = player.posX - hook.posX;
        double dy = player.posY - hook.posY;
        double dz = player.posZ - hook.posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double horizontal = Math.hypot(dx, dz);
        double pull = horizontal == 0 ? 0.1D : 0.1D + speed / horizontal;
        for (Entity entity : entities) {
            entity.setPosition(hook.posX, hook.posY, hook.posZ);
            entity.motionX = dx * pull;
            entity.motionY = dy * 0.1D + Math.sqrt(distance) * 0.08D + speed;
            entity.motionZ = dz * pull;
            hook.world.spawnEntity(entity);
            hook.world.spawnEntity(new EntityXPOrb(hook.world, player.posX, player.posY + 0.5D,
                    player.posZ + 0.5D, random.nextInt(6) + 1));
        }
        return event.getRodDamage();
    }
}
