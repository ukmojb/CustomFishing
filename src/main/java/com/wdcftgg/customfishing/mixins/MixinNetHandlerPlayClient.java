package com.wdcftgg.customfishing.mixins;


import com.wdcftgg.customfishing.entity.CFEntityFishHook;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.server.SPacketSpawnObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Shadow
    private WorldClient world;


    @Inject(method = "handleSpawnObject",at = @At(value = "TAIL", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;handleSpawnObject(Lnet/minecraft/network/play/server/SPacketSpawnObject;)V"))
    public void handleSpawnObject(SPacketSpawnObject packetIn)
    {
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        Entity entity = null;

        if (packetIn.getType() == 90)
        {
            Entity entity1 = this.world.getEntityByID(packetIn.getData());

            if (entity1 instanceof EntityPlayer)
            {
                entity = new CFEntityFishHook(this.world, (EntityPlayer)entity1, d0, d1, d2);
            }

            packetIn.setData(0);
        }

        if (entity != null && packetIn.getType() == 90)
        {
            EntityTracker.updateServerPosition(entity, d0, d1, d2);
            entity.rotationPitch = (float)(packetIn.getPitch() * 360) / 256.0F;
            entity.rotationYaw = (float)(packetIn.getYaw() * 360) / 256.0F;
            Entity[] aentity = entity.getParts();

            if (aentity != null)
            {
                int i = packetIn.getEntityID() - entity.getEntityId();

                for (Entity entity2 : aentity)
                {
                    entity2.setEntityId(entity2.getEntityId() + i);
                }
            }

            entity.setEntityId(packetIn.getEntityID());
            entity.setUniqueId(packetIn.getUniqueId());
            this.world.addEntityToWorld(packetIn.getEntityID(), entity);

            if (packetIn.getData() > 0)
            {
                if (packetIn.getType() == 60 || packetIn.getType() == 91)
                {
                    Entity entity3 = this.world.getEntityByID(packetIn.getData() - 1);

                    if (entity3 instanceof EntityLivingBase && entity instanceof EntityArrow)
                    {
                        ((EntityArrow)entity).shootingEntity = entity3;
                    }
                }

                entity.setVelocity((double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            }
        }
    }



}
