package com.wdcftgg.customfishing.mixins;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook extends Entity {

    @Shadow
    public EntityFishHook.State currentState = EntityFishHook.State.FLYING;

    private float newf = 0;

    public MixinEntityFishHook(World worldIn) {
        super(worldIn);
    }


    @Inject(
            method = "init",
            at = @At(
                    value = "HEAD"
            )
    )
    private void init(EntityPlayer player, CallbackInfo ci)
    {
        this.isImmuneToFire = true;
    }

    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "HEAD",
                    target = "Lnet/minecraft/entity/projectile/EntityFishHook;move(Lnet/minecraft/entity/MoverType;DDD)V"
            )
    )
    public void moveUp(CallbackInfo ci) {

        BlockPos blockpos = new BlockPos(this);
        IBlockState iblockstate = this.world.getBlockState(blockpos);

        if (!iblockstate.getMaterial().isLiquid())
        {
            this.motionY -= 0.031D;
        }

    }


    @ModifyConstant(
            method = "onUpdate",
            constant = @Constant(doubleValue = 0.03D)
    )
    private double modifyMotionYConstant(double original) {
        return 0; // 将 `0.03D` 替换为 `c`
    }




    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "HEAD",
                    target = "Lnet/minecraft/block/material/Material;WATER:Lnet/minecraft/block/material/Material;",
                    ordinal = 1
            )
    )
    private void getOtherLiquidHeight(CallbackInfo ci) {

        BlockPos blockpos = new BlockPos(this);
        IBlockState iblockstate = this.world.getBlockState(blockpos);

        newf = getLiquidHeight(this.world, blockpos);

    }

    @ModifyVariable(
            method = "onUpdate",
            at = @At("STORE"),
            ordinal = 0
    )
    private float modifyF(float f) {
        return newf;
    }

    private float getLiquidHeight(World worldIn, BlockPos blockpos) {
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if(iblockstate.getMaterial().isLiquid()) {
            return BlockLiquid.getBlockLiquidHeight(iblockstate, worldIn, blockpos);
        }
        return 0.0f;
    }
}
