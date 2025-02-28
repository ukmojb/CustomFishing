package com.wdcftgg.customfishing.entity;

import com.wdcftgg.customfishing.CFConfig;
import com.wdcftgg.customfishing.crt.FishingCondition;
import com.wdcftgg.customfishing.crt.FishingConditionInit;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class CFEntityFishHook extends EntityFishHook {
    static enum State
    {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;
    }

    private int ticksInGround;
    private int ticksInAir;
    private float fishApproachingAngle;
    private int lureSpeed;
    private int luck;
    private String liquidName = "";

    private boolean liquidIsLava = false;

    private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.<Integer>createKey(EntityFishHook.class, DataSerializers.VARINT);

    final static Field currentStateField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "field_190627_av");//
    final static Field inGroundField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "field_146051_au");
    final static Field ticksCatchableField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "field_146045_ax");//
    final static Field ticksCatchableDelayField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "field_146038_az");//
    final static Field ticksCaughtDelayField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "field_146040_ay");//

    //now thats what i call scuffed
    final static Class stateEnumField = EntityFishHook.class.getDeclaredClasses()[0];
    final static Object objFlying = stateEnumField.getEnumConstants()[0];
    final static Object objHooked = stateEnumField.getEnumConstants()[1];
    public final static Object objBobbing = stateEnumField.getEnumConstants()[2];

    @SideOnly(Side.CLIENT)
    public CFEntityFishHook(World worldIn, EntityPlayer player, double x, double y, double z) {
        super(worldIn, player, x, y, z);
        this.isImmuneToFire = true;
    }

    public CFEntityFishHook(World worldIn, EntityPlayer fishingPlayer) {
        super(worldIn, fishingPlayer);
        this.isImmuneToFire = true;
    }

    public CFEntityFishHook(World worldIn) {
        super(worldIn, worldIn.getPlayerEntityByUUID(Minecraft.getMinecraft().getSession().getProfile().getId()));
        this.isImmuneToFire = true;
    }

    public float getLiquidHeight(World worldIn, BlockPos blockpos) {
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if(iblockstate.getMaterial().isLiquid()) {
            liquidName = iblockstate.getBlock().getRegistryName().toString();
            return BlockLiquid.getBlockLiquidHeight(iblockstate, worldIn, blockpos);
        }
        return 0.0f;
    }

    public double getLiquidMotion() {
        return 0.3D;
    }

    public SoundEvent getSoundEvent() {
        return SoundEvents.ENTITY_BOBBER_SPLASH;
    }

    public EnumParticleTypes getBubbleParticle() {
        return EnumParticleTypes.WATER_BUBBLE;
    }
    public EnumParticleTypes getWakeParticle() {
        return EnumParticleTypes.WATER_WAKE;
    }
    public EnumParticleTypes getSplashParticle() {
        return EnumParticleTypes.WATER_SPLASH;
    }

    @Override
    public void setLureSpeed(int p_191516_1_)
    {
        this.lureSpeed = p_191516_1_;
    }


    @Override
    public boolean isInWater() {
        IBlockState iblockstate = this.world.getBlockState(this.getPosition());
        return iblockstate.getMaterial().isLiquid();
    }

    @Override
    public void setLuck(int p_191517_1_)
    {
        this.luck = p_191517_1_;
    }

    @Override
    protected void entityInit()
    {
        this.getDataManager().register(DATA_HOOKED_ENTITY, Integer.valueOf(0));
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (DATA_HOOKED_ENTITY.equals(key))
        {
            int i = ((Integer)this.getDataManager().get(DATA_HOOKED_ENTITY)).intValue();
            this.caughtEntity = i > 0 ? this.world.getEntityByID(i - 1) : null;
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    public void onUpdate() {
        if(!this.world.isRemote) this.setFlag(6, this.isGlowing());
        this.onEntityUpdate();//Should bypass FishHook update?
        this.isImmuneToFire = true;
        if(this.getAngler() == null) this.setDead();
        else if(this.world.isRemote || !shouldStopFishing()) {
            if(getInGround(this)) {
                this.ticksInGround++;

                if(this.ticksInGround >= 1200) {
                    this.setDead();
                    return;
                }
            }

            BlockPos blockpos = new BlockPos(this);
            float f = getLiquidHeight(this.world, blockpos);
            if(getHookState(this) == State.FLYING) {
                if(this.caughtEntity != null) {
                    this.motionX = 0.0D;
                    this.motionY = 0.0D;
                    this.motionZ = 0.0D;
                    setHookState(this, (Enum) objHooked);
                    return;
                }

                if(f > 0.0F) {
                    this.motionX *= 0.3D;
                    this.motionY *= 0.2D;
                    this.motionZ *= 0.3D;
                    setHookState(this, (Enum) objBobbing);
                    return;
                }

                if(!this.world.isRemote) checkCollision();

                if(!getInGround(this) && !this.onGround && !this.collidedHorizontally) this.ticksInAir++;
                else {
                    this.ticksInAir = 0;
                    this.motionX = 0.0D;
                    this.motionY = 0.0D;
                    this.motionZ = 0.0D;
                }
            }
            else {
                if(getHookState(this) == State.HOOKED_IN_ENTITY) {
                    if(this.caughtEntity != null) {
                        if(this.caughtEntity.isDead) {
                            this.caughtEntity = null;
                            setHookState(this, (Enum) objFlying);
                        }
                        else {
                            this.posX = this.caughtEntity.posX;
                            double d2 = (double)this.caughtEntity.height;
                            this.posY = this.caughtEntity.getEntityBoundingBox().minY + d2 * 0.8D;
                            this.posZ = this.caughtEntity.posZ;
                            this.setPosition(this.posX, this.posY, this.posZ);
                        }
                    }
                    return;
                }
                else if(getHookState(this) == State.BOBBING) {
                    this.motionX *= 0.9D;
                    this.motionZ *= 0.9D;
                    double d0 = this.posY + this.motionY - (double)blockpos.getY() - (double)f;

                    if(Math.abs(d0) < 0.1D) d0 += Math.signum(d0) * 0.1D;

                    this.motionY -= (d0 * (double)this.rand.nextFloat() * 0.2D - 0.035D);


                    if (!this.world.isRemote && f > 0.0F){
                        this.catchingFish(blockpos);
                    }
                }
            }


            IBlockState iblockstate = world.getBlockState(blockpos);
//            if (iblockstate.getMaterial() != Material.WATER && iblockstate.getMaterial().isLiquid()) this.motionY += getLiquidMotion();
            if(iblockstate.getMaterial().isLiquid()){
                this.motionY -= 0.05D;
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            updateRotation();
            this.motionX *= 0.92D;
            this.motionY *= 0.92D;
            this.motionZ *= 0.92D;
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    private void catchingFish(BlockPos pos) {
        WorldServer worldserver = (WorldServer)this.world;
        int i = 1;
        BlockPos blockpos = pos.up();

        if(this.rand.nextFloat() < 0.25F && this.world.isRainingAt(blockpos)) ++i;

        if(this.rand.nextFloat() < 0.5F && !this.world.canSeeSky(blockpos)) --i;

        if(getTicksCatchable(this) > 0) {
            setTicksCatchable(this, getTicksCatchable(this)-1);

            if(getTicksCatchable(this) <= 0) {
                setTicksCaughtDelay(this, 0);
                setTicksCatchableDelay(this, 0);
            }
            else{
                this.motionY -= getLiquidMotion() * (double)this.rand.nextFloat() * (double)this.rand.nextFloat();
            }
        }
        else if(getTicksCatchableDelay(this) > 0) {

            boolean haveCustomLiquid = false;

            for (FishingCondition fishingCondition : FishingConditionInit.getAllFishingCondition()) {
                if (haveCustomLiquid) break;
                boolean isFishingCondition = passFishingCondition(fishingCondition, liquidName, world.getBiome(this.getPosition()).getRegistryName(), world.provider.getDimension());


                haveCustomLiquid = (CFConfig.FishingInAnyLiquid || isFishingCondition);
            }

            if (liquidName == Blocks.WATER.getRegistryName().toString()) {
                haveCustomLiquid = true;
            }

            if (!haveCustomLiquid) return;


            setTicksCatchableDelay(this, getTicksCatchableDelay(this)-i);

            if(getTicksCatchableDelay(this) > 0) {
                this.fishApproachingAngle = (float)((double)this.fishApproachingAngle + this.rand.nextGaussian() * 4.0D);
                float f = this.fishApproachingAngle * 0.017453292F;
                float f1 = MathHelper.sin(f);
                float f2 = MathHelper.cos(f);
                double d0 = this.posX + (double)(f1 * (float)getTicksCatchableDelay(this) * 0.1F);
                double d1 = (double)((float)MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
                double d2 = this.posZ + (double)(f2 * (float)getTicksCatchableDelay(this) * 0.1F);

                BlockPos newPos = new BlockPos(d0, d1 - 1.0D, d2);
                if(getLiquidHeight(worldserver, newPos) != 0) {
                    if(this.rand.nextFloat() < 0.15F) worldserver.spawnParticle(getBubbleParticle(), d0, d1 - 0.10000000149011612D, d2, 1, (double)f1, 0.1D, (double)f2, 0.0D);

                    float f3 = f1 * 0.04F;
                    float f4 = f2 * 0.04F;
                    worldserver.spawnParticle(getWakeParticle(), d0, d1, d2, 0, (double)f4, 0.01D, (double)(-f3), 1.0D);
                    worldserver.spawnParticle(getWakeParticle(), d0, d1, d2, 0, (double)(-f4), 0.01D, (double)f3, 1.0D);
                }
            }
            else {
                this.motionY = (double)(-0.4F * MathHelper.nextFloat(this.rand, 0.6F, 1.0F));
                this.playSound(getSoundEvent(), 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                double d3 = this.getEntityBoundingBox().minY + 0.5D;
                worldserver.spawnParticle(getBubbleParticle(), this.posX, d3, this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.20000000298023224D);
                worldserver.spawnParticle(getWakeParticle(), this.posX, d3, this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.20000000298023224D);
                setTicksCatchable(this, MathHelper.getInt(this.rand, 20, 40));
            }
        }
        else if(getTicksCaughtDelay(this) > 0) {
            setTicksCaughtDelay(this, getTicksCaughtDelay(this)-i);
            float f5 = 0.15F;

            if(getTicksCaughtDelay(this) < 20) f5 = (float)((double)f5 + (double)(20 - getTicksCaughtDelay(this)) * 0.05D);
            else if (getTicksCaughtDelay(this) < 40) f5 = (float)((double)f5 + (double)(40 - getTicksCaughtDelay(this)) * 0.02D);
            else if (getTicksCaughtDelay(this) < 60) f5 = (float)((double)f5 + (double)(60 - getTicksCaughtDelay(this)) * 0.01D);

            if(this.rand.nextFloat() < f5) {
                float f6 = MathHelper.nextFloat(this.rand, 0.0F, 360.0F) * 0.017453292F;
                float f7 = MathHelper.nextFloat(this.rand, 25.0F, 60.0F);
                double d4 = this.posX + (double)(MathHelper.sin(f6) * f7 * 0.1F);
                double d5 = (double)((float)MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
                double d6 = this.posZ + (double)(MathHelper.cos(f6) * f7 * 0.1F);

                BlockPos newPos = new BlockPos((int) d4, (int) d5 - 1, (int) d6);
                if(getLiquidHeight(worldserver, newPos) != 0) worldserver.spawnParticle(getSplashParticle(), d4, d5, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
            }

            if(getTicksCaughtDelay(this) <= 0) {
                this.fishApproachingAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
                setTicksCatchableDelay(this, MathHelper.getInt(this.rand, 20, 80));
            }
        }
        else {
            setTicksCaughtDelay(this, MathHelper.getInt(this.rand, 100, 600));
            setTicksCaughtDelay(this, getTicksCaughtDelay(this)-this.lureSpeed * 20 * 5);
        }
    }

    private boolean shouldStopFishing() {
        ItemStack itemstack = this.getAngler().getHeldItemMainhand();
        ItemStack itemstack1 = this.getAngler().getHeldItemOffhand();
        boolean flag = itemstack.getItem() instanceof ItemFishingRod;

        if(!this.getAngler().isDead && this.getAngler().isEntityAlive() && flag && this.getDistanceSq(this.getAngler()) <= 1024.0D) return false;
        else {
            this.setDead();
            return true;
        }
    }

    private void updateRotation() {
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) ;

        while(this.rotationPitch - this.prevRotationPitch >= 180.0F)  this.prevRotationPitch += 360.0F;

        while(this.rotationYaw - this.prevRotationYaw < -180.0F) this.prevRotationYaw -= 360.0F;

        while(this.rotationYaw - this.prevRotationYaw >= 180.0F) this.prevRotationYaw += 360.0F;

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
    }

    private void checkCollision() {
        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
        vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if(raytraceresult != null) vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);

        Entity entity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
        double d0 = 0.0D;

        for(Entity entity1 : list) {
            if(this.canBeHooked(entity1) && (entity1 != this.getAngler() || this.ticksInAir >= 5)) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                if(raytraceresult1 != null) {
                    double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                    if(d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        if(entity != null) raytraceresult = new RayTraceResult(entity);

        if(raytraceresult != null && raytraceresult.typeOfHit != RayTraceResult.Type.MISS) {
            if(raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY) {
                this.caughtEntity = raytraceresult.entityHit;
                setHookedEntity();
            }
            else setInGround(this, true);
        }
    }

    @Override
    public int handleHookRetraction()
    {
        if (!this.world.isRemote && getAngler() != null)
        {
            int i = 0;

            net.minecraftforge.event.entity.player.ItemFishedEvent event = null;
            if (this.caughtEntity != null)
            {
                this.bringInHookedEntity();
                this.world.setEntityState(this, (byte)31);
                i = this.caughtEntity instanceof EntityItem ? 3 : 5;
            }
            else if (getTicksCatchable(this) > 0)
            {
                LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)this.world);
                lootcontext$builder.withLuck((float)this.luck + getAngler().getLuck()).withPlayer(getAngler()).withLootedEntity(this); // Forge: add player & looted entity to LootContext
                List<ItemStack> result = this.world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(this.rand, lootcontext$builder.build());

                List<FishingCondition> fishingConditionList = FishingConditionInit.getAllFishingCondition();

                Collections.shuffle(fishingConditionList);

                for (FishingCondition fishingCondition : FishingConditionInit.getAllFishingCondition()) {
                    Random random = new Random();
                    boolean isFishingCondition = passFishingCondition(fishingCondition, liquidName, world.getBiome(this.getPosition()).getRegistryName(), world.provider.getDimension());

                    if (CFConfig.FishingInAnyLiquid) {
                        result = (isFishingCondition)
                                ? world.getLootTableManager().getLootTableFromLocation(fishingCondition.getLootRes()).generateLootForPools(random, lootcontext$builder.build())
                                : result;
                    } else {
                        result = (isFishingCondition)
                                ? world.getLootTableManager().getLootTableFromLocation(fishingCondition.getLootRes()).generateLootForPools(random, lootcontext$builder.build())
                                : new ArrayList<>();
                    }
                }


                for (FishingCondition fishingCondition : fishingConditionList) {
                    Random random = new Random();
                    if (passFishingCondition(fishingCondition, liquidName, world.getBiome(this.getPosition()).getRegistryName(), world.provider.getDimension())) {
                        float rf = random.nextFloat();
                        if (rf <= fishingCondition.getChance()) {
                            result.add(fishingCondition.getItemStack());
                        }
                        if (CFConfig.OneAtATime) break;
                    }
                }

                event = new net.minecraftforge.event.entity.player.ItemFishedEvent(result, getInGround(this) ? 2 : 1, this);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled())
                {
                    this.setDead();
                    return event.getRodDamage();
                }


                for (ItemStack itemstack : result)
                {

                    EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, itemstack);
                    double d0 = getAngler().posX - this.posX;
                    double d1 = getAngler().posY - this.posY;
                    double d2 = getAngler().posZ - this.posZ;
                    double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    double addspeed = CFConfig.getliquidSpeedMap().containsKey(liquidName) ? CFConfig.getliquidSpeedMap().get(liquidName) : 1.0F;


                    entityitem.motionX = d0 * 0.1D + addspeed;
                    entityitem.motionY = d1 * 0.1D + (double)MathHelper.sqrt(d3) * 0.15D + addspeed;
                    entityitem.motionZ = d2 * 0.1D + addspeed;
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    nbtTagCompound = entityitem.writeToNBT(nbtTagCompound);
                    nbtTagCompound.setShort("Health", (short)1000);
                    entityitem.readEntityFromNBT(nbtTagCompound);
                    if (CFConfig.itemInvulnerable) entityitem.setEntityInvulnerable(true);
                    this.world.spawnEntity(entityitem);
                    getAngler().world.spawnEntity(new EntityXPOrb(getAngler().world, getAngler().posX, getAngler().posY + 0.5D, getAngler().posZ + 0.5D, this.rand.nextInt(6) + 1));
                    Item item = itemstack.getItem();

                    if (item == Items.FISH || item == Items.COOKED_FISH)
                    {
                        getAngler().addStat(StatList.FISH_CAUGHT, 1);
                    }
                }

                i = 1;
            }

            if (getInGround(this))
            {
                i = 2;
            }

            this.setDead();
            return event == null ? i : event.getRodDamage();
        }
        else
        {
            return 0;
        }
    }

    private boolean passFishingCondition(FishingCondition fishingCondition, String liquid, ResourceLocation biomeid, Integer dimid) {
        boolean pass1 = (fishingCondition.getLiquid() == null || fishingCondition.getLiquid().equals(liquid));
        boolean pass2 = (fishingCondition.getItemStack() == null || fishingCondition.getItemStack() != ItemStack.EMPTY);
        boolean pass3 = (fishingCondition.getLootRes().toString().equals("minecraft:") || world.getLootTableManager().getLootTableFromLocation(fishingCondition.getLootRes()) != LootTable.EMPTY_LOOT_TABLE);
        boolean pass4 = (fishingCondition.getDimid() == null || fishingCondition.getDimid().equals(dimid));
        boolean pass5 = (fishingCondition.getBiomeid() == "" || fishingCondition.getBiomeid().equals(biomeid.toString()));

        return pass1 && pass2 && pass3 && pass4 && pass5;
    }


    private void setHookedEntity() {
        this.getDataManager().set(DATA_HOOKED_ENTITY, Integer.valueOf(this.caughtEntity.getEntityId() + 1));
    }

    private State getHookState(EntityFishHook hook) {
        if(currentStateField == null) return null;

        try {
            currentStateField.setAccessible(true);
            Enum hookEnum = (Enum) currentStateField.get(hook);
            int hookStateOrdinal = hookEnum.ordinal();
            currentStateField.setAccessible(false);
            return State.values()[hookStateOrdinal];
        }
        catch(IllegalAccessException e) {
            currentStateField.setAccessible(false);
            e.printStackTrace();
            return null;
        }
    }
    public static void setHookState(EntityFishHook hook, Enum state) {
        if(currentStateField == null) return;

        try {
            currentStateField.setAccessible(true);
            currentStateField.set(hook, state);
            currentStateField.setAccessible(false);
            return;
        }
        catch(IllegalAccessException e) {
            currentStateField.setAccessible(false);
            e.printStackTrace();
            return;
        }
    }

    private Boolean getInGround(EntityFishHook hook) {
        if(inGroundField == null) return null;

        try {
            inGroundField.setAccessible(true);
            boolean inGround = inGroundField.getBoolean(hook);
            inGroundField.setAccessible(false);
            return inGround;
        }
        catch(IllegalAccessException e) {
            inGroundField.setAccessible(false);
            e.printStackTrace();
            return null;
        }
    }
    private void setInGround(EntityFishHook hook, Boolean value) {
        if(inGroundField == null) return;

        try {
            inGroundField.setAccessible(true);
            inGroundField.setBoolean(hook, value);
            inGroundField.setAccessible(false);
            return;
        }
        catch(IllegalAccessException e) {
            inGroundField.setAccessible(false);
            e.printStackTrace();
            return;
        }
    }

    private void setTicksCatchable(EntityFishHook hook, int ticks) {
        if(ticksCatchableField == null) return;

        try {
            ticksCatchableField.setAccessible(true);
            ticksCatchableField.setInt(hook, ticks);
            ticksCatchableField.setAccessible(false);
        }
        catch(IllegalArgumentException | IllegalAccessException e) {
            ticksCatchableField.setAccessible(false);
            e.printStackTrace();
        }
    }
    private int getTicksCatchable(EntityFishHook hook) {
        if(ticksCatchableField == null) return 0;

        try {
            ticksCatchableField.setAccessible(true);
            int ticks = ticksCatchableField.getInt(hook);
            ticksCatchableField.setAccessible(false);
            return ticks;
        }
        catch(IllegalArgumentException | IllegalAccessException e) {
            ticksCatchableField.setAccessible(false);
            e.printStackTrace();
            return 0;
        }
    }

    private void setTicksCatchableDelay(EntityFishHook hook, int ticks) {
        if(ticksCatchableDelayField == null) return;

        try {
            ticksCatchableDelayField.setAccessible(true);
            ticksCatchableDelayField.setInt(hook, ticks);
            ticksCatchableDelayField.setAccessible(false);
        }
        catch(IllegalArgumentException | IllegalAccessException e) {
            ticksCatchableDelayField.setAccessible(false);
            e.printStackTrace();
        }
    }
    private int getTicksCatchableDelay(EntityFishHook hook) {
        if(ticksCatchableDelayField == null) return 0;

        try {
            ticksCatchableDelayField.setAccessible(true);
            int ticks = ticksCatchableDelayField.getInt(hook);
            ticksCatchableDelayField.setAccessible(false);
            return ticks;
        }
        catch(IllegalArgumentException | IllegalAccessException e) {
            ticksCatchableDelayField.setAccessible(false);
            e.printStackTrace();
            return 0;
        }
    }

    private void setTicksCaughtDelay(EntityFishHook hook, int ticks) {
        if(ticksCaughtDelayField == null) return;

        try {
            ticksCaughtDelayField.setAccessible(true);
            ticksCaughtDelayField.setInt(hook, ticks);
            ticksCaughtDelayField.setAccessible(false);
        }
        catch(IllegalArgumentException | IllegalAccessException e) {
            ticksCaughtDelayField.setAccessible(false);
            e.printStackTrace();
        }
    }
    private int getTicksCaughtDelay(EntityFishHook hook) {
        if(ticksCaughtDelayField == null) return 0;

        try {
            ticksCaughtDelayField.setAccessible(true);
            int ticks = ticksCaughtDelayField.getInt(hook);
            ticksCaughtDelayField.setAccessible(false);
            return ticks;
        }
        catch(IllegalArgumentException | IllegalAccessException e) {
            ticksCaughtDelayField.setAccessible(false);
            e.printStackTrace();
            return 0;
        }
    }
}