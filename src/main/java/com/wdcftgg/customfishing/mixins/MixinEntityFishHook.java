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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook extends Entity {

    @Shadow
    public EntityFishHook.State currentState = EntityFishHook.State.FLYING;

    public MixinEntityFishHook(World worldIn) {
        super(worldIn);
    }

    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/EntityFishHook;move(Lnet/minecraft/entity/MoverType;DDD)V" // move 方法
            )
    )
    private void beforeMove(CallbackInfo ci) {

        BlockPos blockpos = new BlockPos(this);
        IBlockState iblockstate = this.world.getBlockState(blockpos);


        if (this.currentState == EntityFishHook.State.BOBBING && iblockstate.getMaterial().isLiquid())
        {
//            this.motionX *= 0.9D;
//            this.motionZ *= 0.9D;
            this.motionY += 0.03D;
        }

        if ((iblockstate.getMaterial() != Material.WATER) && iblockstate.getMaterial().isLiquid())
        {
            this.motionY += 0.03D;
        }
    }

    @ModifyVariable(
            method = "onUpdate",
            at = @At("STORE"),
            ordinal = 0
    )
    private float modifyF(float f) {
        if (f == 0.0F)  f = getLiquidHeight(this.world, this.getPosition());
        return f;
    }

    private float getLiquidHeight(World worldIn, BlockPos blockpos) {
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if(iblockstate.getMaterial().isLiquid()) {
            return BlockLiquid.getBlockLiquidHeight(iblockstate, worldIn, blockpos);
        }
        return 0.0f;
    }
}