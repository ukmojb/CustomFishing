package com.wdcftgg.customfishing.mixins;

import com.wdcftgg.customfishing.entity.FishingCatch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook extends Entity {
    @Shadow private int ticksCatchable;
    @Shadow private int ticksCaughtDelay;
    @Shadow private int ticksCatchableDelay;
    @Shadow private int luck;
    @Shadow private boolean inGround;
    @Shadow public Entity caughtEntity;
    @Unique private BlockPos customfishing$bitePos;

    public MixinEntityFishHook(World world) {
        super(world);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void customfishing$protectHook(EntityPlayer player, CallbackInfo ci) {
        isImmuneToFire = true;
    }

    @Redirect(method = {"onUpdate", "catchingFish"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;"))
    private Material customfishing$fishingMaterial(IBlockState state) {
        Material material = state.getMaterial();
        return material.isLiquid() || state.getBlock() instanceof IFluidBlock ? Material.WATER : material;
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockLiquid;getBlockLiquidHeight(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)F"))
    private float customfishing$liquidHeight(IBlockState state, IBlockAccess access, BlockPos pos) {
        if (state.getBlock() instanceof IFluidBlock) {
            float fill = ((IFluidBlock) state.getBlock()).getFilledPercentage(world, pos);
            if (fill < 0) return posY >= pos.getY() + 1 + fill ? 1 : 0;
            return fill;
        }
        return state.getBlock().getBlockLiquidHeight(world, pos, state, state.getMaterial());
    }

    @Inject(method = "catchingFish", at = @At("HEAD"), cancellable = true)
    private void customfishing$checkBite(BlockPos pos, CallbackInfo ci) {
        if (!FishingCatch.canBite((EntityFishHook) (Object) this, pos)) {
            ticksCatchable = 0;
            ticksCaughtDelay = 0;
            ticksCatchableDelay = 0;
            customfishing$bitePos = null;
            ci.cancel();
        } else {
            customfishing$bitePos = pos;
        }
    }

    @Inject(method = "handleHookRetraction", at = @At("HEAD"), cancellable = true)
    private void customfishing$reelIn(CallbackInfoReturnable<Integer> ci) {
        if (!world.isRemote && caughtEntity == null && ticksCatchable > 0 && customfishing$bitePos != null) {
            EntityFishHook hook = (EntityFishHook) (Object) this;
            if (hook.getAngler() != null) {
                int damage = FishingCatch.reelIn(hook, customfishing$bitePos, luck, inGround, rand);
                setDead();
                ci.setReturnValue(damage);
            }
        }
    }
}
