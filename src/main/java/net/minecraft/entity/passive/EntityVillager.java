package net.minecraft.entity.passive;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.datafix.*;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

public class EntityVillager extends EntityAgeable implements INpc, IMerchant
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DataParameter<Integer> PROFESSION = EntityDataManager.<Integer>createKey(EntityVillager.class, DataSerializers.VARINT);
    private int randomTickDivider;
    private boolean isMating;
    private boolean isPlaying;
    Village village;
    @Nullable
    private EntityPlayer buyingPlayer;
    @Nullable
    public MerchantRecipeList buyingList;
    private int timeUntilReset;
    private boolean needsInitilization;
    private boolean isWillingToMate;
    public int wealth;
    private java.util.UUID lastBuyingPlayer;
    public int careerId;
    private int careerLevel;
    private boolean isLookingForHome;
    private boolean areAdditionalTasksSet;
    public final InventoryBasic villagerInventory;
    private static final ITradeList[][][][] DEFAULT_TRADE_LIST_MAP = new ITradeList[][][][] {{{{new EmeraldForItems(Items.WHEAT, new PriceInfo(18, 22)), new EmeraldForItems(Items.POTATO, new PriceInfo(15, 19)), new EmeraldForItems(Items.CARROT, new PriceInfo(15, 19)), new ListItemForEmeralds(Items.BREAD, new PriceInfo(-4, -2))}, {new EmeraldForItems(Item.getItemFromBlock(Blocks.PUMPKIN), new PriceInfo(8, 13)), new ListItemForEmeralds(Items.PUMPKIN_PIE, new PriceInfo(-3, -2))}, {new EmeraldForItems(Item.getItemFromBlock(Blocks.MELON_BLOCK), new PriceInfo(7, 12)), new ListItemForEmeralds(Items.APPLE, new PriceInfo(-7, -5))}, {new ListItemForEmeralds(Items.COOKIE, new PriceInfo(-10, -6)), new ListItemForEmeralds(Items.CAKE, new PriceInfo(1, 1))}}, {{new EmeraldForItems(Items.STRING, new PriceInfo(15, 20)), new EmeraldForItems(Items.COAL, new PriceInfo(16, 24)), new ItemAndEmeraldToItem(Items.FISH, new PriceInfo(6, 6), Items.COOKED_FISH, new PriceInfo(6, 6))}, {new ListEnchantedItemForEmeralds(Items.FISHING_ROD, new PriceInfo(7, 8))}}, {{new EmeraldForItems(Item.getItemFromBlock(Blocks.WOOL), new PriceInfo(16, 22)), new ListItemForEmeralds(Items.SHEARS, new PriceInfo(3, 4))}, {new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL)), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 1), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 2), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 3), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 4), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 5), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 6), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 7), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 8), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 9), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 10), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 11), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 12), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 13), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 14), new PriceInfo(1, 2)), new ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 15), new PriceInfo(1, 2))}}, {{new EmeraldForItems(Items.STRING, new PriceInfo(15, 20)), new ListItemForEmeralds(Items.ARROW, new PriceInfo(-12, -8))}, {new ListItemForEmeralds(Items.BOW, new PriceInfo(2, 3)), new ItemAndEmeraldToItem(Item.getItemFromBlock(Blocks.GRAVEL), new PriceInfo(10, 10), Items.FLINT, new PriceInfo(6, 10))}}}, {{{new EmeraldForItems(Items.PAPER, new PriceInfo(24, 36)), new ListEnchantedBookForEmeralds()}, {new EmeraldForItems(Items.BOOK, new PriceInfo(8, 10)), new ListItemForEmeralds(Items.COMPASS, new PriceInfo(10, 12)), new ListItemForEmeralds(Item.getItemFromBlock(Blocks.BOOKSHELF), new PriceInfo(3, 4))}, {new EmeraldForItems(Items.WRITTEN_BOOK, new PriceInfo(2, 2)), new ListItemForEmeralds(Items.CLOCK, new PriceInfo(10, 12)), new ListItemForEmeralds(Item.getItemFromBlock(Blocks.GLASS), new PriceInfo(-5, -3))}, {new ListEnchantedBookForEmeralds()}, {new ListEnchantedBookForEmeralds()}, {new ListItemForEmeralds(Items.NAME_TAG, new PriceInfo(20, 22))}}, {{new EmeraldForItems(Items.PAPER, new PriceInfo(24, 36))}, {new EmeraldForItems(Items.COMPASS, new PriceInfo(1, 1))}, {new ListItemForEmeralds(Items.MAP, new PriceInfo(7, 11))}, {new TreasureMapForEmeralds(new PriceInfo(12, 20), "Monument", MapDecoration.Type.MONUMENT), new TreasureMapForEmeralds(new PriceInfo(16, 28), "Mansion", MapDecoration.Type.MANSION)}}}, {{{new EmeraldForItems(Items.ROTTEN_FLESH, new PriceInfo(36, 40)), new EmeraldForItems(Items.GOLD_INGOT, new PriceInfo(8, 10))}, {new ListItemForEmeralds(Items.REDSTONE, new PriceInfo(-4, -1)), new ListItemForEmeralds(new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new PriceInfo(-2, -1))}, {new ListItemForEmeralds(Items.ENDER_PEARL, new PriceInfo(4, 7)), new ListItemForEmeralds(Item.getItemFromBlock(Blocks.GLOWSTONE), new PriceInfo(-3, -1))}, {new ListItemForEmeralds(Items.EXPERIENCE_BOTTLE, new PriceInfo(3, 11))}}}, {{{new EmeraldForItems(Items.COAL, new PriceInfo(16, 24)), new ListItemForEmeralds(Items.IRON_HELMET, new PriceInfo(4, 6))}, {new EmeraldForItems(Items.IRON_INGOT, new PriceInfo(7, 9)), new ListItemForEmeralds(Items.IRON_CHESTPLATE, new PriceInfo(10, 14))}, {new EmeraldForItems(Items.DIAMOND, new PriceInfo(3, 4)), new ListEnchantedItemForEmeralds(Items.DIAMOND_CHESTPLATE, new PriceInfo(16, 19))}, {new ListItemForEmeralds(Items.CHAINMAIL_BOOTS, new PriceInfo(5, 7)), new ListItemForEmeralds(Items.CHAINMAIL_LEGGINGS, new PriceInfo(9, 11)), new ListItemForEmeralds(Items.CHAINMAIL_HELMET, new PriceInfo(5, 7)), new ListItemForEmeralds(Items.CHAINMAIL_CHESTPLATE, new PriceInfo(11, 15))}}, {{new EmeraldForItems(Items.COAL, new PriceInfo(16, 24)), new ListItemForEmeralds(Items.IRON_AXE, new PriceInfo(6, 8))}, {new EmeraldForItems(Items.IRON_INGOT, new PriceInfo(7, 9)), new ListEnchantedItemForEmeralds(Items.IRON_SWORD, new PriceInfo(9, 10))}, {new EmeraldForItems(Items.DIAMOND, new PriceInfo(3, 4)), new ListEnchantedItemForEmeralds(Items.DIAMOND_SWORD, new PriceInfo(12, 15)), new ListEnchantedItemForEmeralds(Items.DIAMOND_AXE, new PriceInfo(9, 12))}}, {{new EmeraldForItems(Items.COAL, new PriceInfo(16, 24)), new ListEnchantedItemForEmeralds(Items.IRON_SHOVEL, new PriceInfo(5, 7))}, {new EmeraldForItems(Items.IRON_INGOT, new PriceInfo(7, 9)), new ListEnchantedItemForEmeralds(Items.IRON_PICKAXE, new PriceInfo(9, 11))}, {new EmeraldForItems(Items.DIAMOND, new PriceInfo(3, 4)), new ListEnchantedItemForEmeralds(Items.DIAMOND_PICKAXE, new PriceInfo(12, 15))}}}, {{{new EmeraldForItems(Items.PORKCHOP, new PriceInfo(14, 18)), new EmeraldForItems(Items.CHICKEN, new PriceInfo(14, 18))}, {new EmeraldForItems(Items.COAL, new PriceInfo(16, 24)), new ListItemForEmeralds(Items.COOKED_PORKCHOP, new PriceInfo(-7, -5)), new ListItemForEmeralds(Items.COOKED_CHICKEN, new PriceInfo(-8, -6))}}, {{new EmeraldForItems(Items.LEATHER, new PriceInfo(9, 12)), new ListItemForEmeralds(Items.LEATHER_LEGGINGS, new PriceInfo(2, 4))}, {new ListEnchantedItemForEmeralds(Items.LEATHER_CHESTPLATE, new PriceInfo(7, 12))}, {new ListItemForEmeralds(Items.SADDLE, new PriceInfo(8, 10))}}}, {new ITradeList[0][]}};

    public EntityVillager(World worldIn)
    {
        this(worldIn, 0);
    }

    public EntityVillager(World worldIn, int professionId)
    {
        super(worldIn);
        this.villagerInventory = new InventoryBasic("Items", false, 8, (CraftVillager) this.getBukkitEntity());
        this.setProfession(professionId);
        this.setSize(0.6F, 1.95F);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(true);
    }

    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityEvoker.class, 12.0F, 0.8D, 0.8D));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityVindicator.class, 8.0F, 0.8D, 0.8D));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityVex.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIVillagerInteract(this));
        this.tasks.addTask(9, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    private void setAdditionalAItasks()
    {
        if (!this.areAdditionalTasksSet)
        {
            this.areAdditionalTasksSet = true;

            if (this.isChild())
            {
                this.tasks.addTask(8, new EntityAIPlay(this, 0.32D));
            }
            else if (this.getProfession() == 0)
            {
                this.tasks.addTask(6, new EntityAIHarvestFarmland(this, 0.6D));
            }
        }
    }

    protected void onGrowingAdult()
    {
        if (this.getProfession() == 0)
        {
            this.tasks.addTask(8, new EntityAIHarvestFarmland(this, 0.6D));
        }

        super.onGrowingAdult();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    // Spigot Start
    @Override
    public void inactiveTick() {
        // SPIGOT-3874
        if (world.spigotConfig.tickInactiveVillagers) {
            // SPIGOT-3894
            net.minecraft.world.chunk.Chunk startingChunk = this.world.getChunkIfLoaded(MathHelper.floor(this.posX) >> 4, MathHelper.floor(this.posZ) >> 4);
            if (!(startingChunk != null && startingChunk.areNeighborsLoaded(1))) {
                return;
            }
            this.updateAITasks(); // SPIGOT-3846
        }
        super.inactiveTick();
    }
    // Spigot End

    protected void updateAITasks()
    {
        if (--this.randomTickDivider <= 0)
        {
            BlockPos blockpos = new BlockPos(this);
            this.world.getVillageCollection().addToVillagerPositionList(blockpos);
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.village = this.world.getVillageCollection().getNearestVillage(blockpos, 32);

            if (this.village == null)
            {
                this.detachHome();
            }
            else
            {
                BlockPos blockpos1 = this.village.getCenter();
                this.setHomePosAndDistance(blockpos1, this.village.getVillageRadius());

                if (this.isLookingForHome)
                {
                    this.isLookingForHome = false;
                    this.village.setDefaultPlayerReputation(5);
                }
            }
        }

        if (!this.isTrading() && this.timeUntilReset > 0)
        {
            --this.timeUntilReset;

            if (this.timeUntilReset <= 0)
            {
                if (this.needsInitilization)
                {
                    for (MerchantRecipe merchantrecipe : this.buyingList)
                    {
                        if (merchantrecipe.isRecipeDisabled())
                        {
                            // merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                            int bonus = this.rand.nextInt(6) + this.rand.nextInt(6) + 2;
                            VillagerReplenishTradeEvent event = new VillagerReplenishTradeEvent((Villager) this.getBukkitEntity(), merchantrecipe.asBukkit(), bonus);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                merchantrecipe.increaseMaxTradeUses(event.getBonus());
                            }
                        }
                    }

                    this.populateBuyingList();
                    this.needsInitilization = false;

                    if (this.village != null && this.lastBuyingPlayer != null)
                    {
                        this.world.setEntityState(this, (byte)14);
                        this.village.modifyPlayerReputation(this.lastBuyingPlayer, 1);
                    }
                }

                this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        super.updateAITasks();
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;

        if (flag)
        {
            itemstack.interactWithEntity(player, this, hand);
            return true;
        }
        else if (!this.holdingSpawnEggOfClass(itemstack, this.getClass()) && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking())
        {
            if (this.buyingList == null)
            {
                this.populateBuyingList();
            }

            if (hand == EnumHand.MAIN_HAND)
            {
                player.addStat(StatList.TALKED_TO_VILLAGER);
            }

            if (!this.world.isRemote && !this.buyingList.isEmpty())
            {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);
            }
            else if (this.buyingList.isEmpty())
            {
                return super.processInteract(player, hand);
            }

            return true;
        }
        else
        {
            return super.processInteract(player, hand);
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(PROFESSION, 0);
    }

    public static void registerFixesVillager(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityVillager.class);
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackDataLists(EntityVillager.class, "Inventory"));
        fixer.registerWalker(FixTypes.ENTITY, (fixer1, compound, versionIn) -> {
            if (EntityList.getKey(EntityVillager.class).equals(new ResourceLocation(compound.getString("id"))) && compound.hasKey("Offers", 10))
            {
                NBTTagCompound nbttagcompound = compound.getCompoundTag("Offers");

                if (nbttagcompound.hasKey("Recipes", 9))
                {
                    NBTTagList nbttaglist = nbttagcompound.getTagList("Recipes", 10);

                    for (int i = 0; i < nbttaglist.tagCount(); ++i)
                    {
                        NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                        DataFixesManager.processItemStack(fixer1, nbttagcompound1, versionIn, "buy");
                        DataFixesManager.processItemStack(fixer1, nbttagcompound1, versionIn, "buyB");
                        DataFixesManager.processItemStack(fixer1, nbttagcompound1, versionIn, "sell");
                        nbttaglist.set(i, nbttagcompound1);
                    }
                }
            }

            return compound;
        });
    }

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("Profession", this.getProfession());
        compound.setString("ProfessionName", this.getProfessionForge().getRegistryName().toString());
        compound.setInteger("Riches", this.wealth);
        compound.setInteger("Career", this.careerId);
        compound.setInteger("CareerLevel", this.careerLevel);
        compound.setBoolean("Willing", this.isWillingToMate);

        if (this.buyingList != null)
        {
            compound.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                nbttaglist.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
            }
        }

        compound.setTag("Inventory", nbttaglist);
    }

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setProfession(compound.getInteger("Profession"));
        if (compound.hasKey("ProfessionName"))
        {
            net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession p =
                net.minecraftforge.fml.common.registry.ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(compound.getString("ProfessionName")));
            if (p == null)
                p = net.minecraftforge.fml.common.registry.ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:farmer"));
            this.setProfession(p);
        }
        this.wealth = compound.getInteger("Riches");
        this.careerId = compound.getInteger("Career");
        this.careerLevel = compound.getInteger("CareerLevel");
        this.isWillingToMate = compound.getBoolean("Willing");

        if (compound.hasKey("Offers", 10))
        {
            NBTTagCompound nbttagcompound = compound.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(nbttagcompound);
        }

        NBTTagList nbttaglist = compound.getTagList("Inventory", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            ItemStack itemstack = new ItemStack(nbttaglist.getCompoundTagAt(i));

            if (!itemstack.isEmpty())
            {
                this.villagerInventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
        this.setAdditionalAItasks();
    }

    public boolean canDespawn()
    {
        return false;
    }

    protected SoundEvent getAmbientSound()
    {
        return this.isTrading() ? SoundEvents.ENTITY_VILLAGER_TRADING : SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_VILLAGER;
    }

    public void setProfession(int professionId)
    {
        this.dataManager.set(PROFESSION, professionId);
        net.minecraftforge.fml.common.registry.VillagerRegistry.onSetProfession(this, professionId);
    }

    @Deprecated //Use Forge Variant below
    public int getProfession()
    {
        return Math.max((Integer) this.dataManager.get(PROFESSION), 0);
    }

    private net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof;
    public void setProfession(net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof)
    {
        this.prof = prof;
        this.setProfession(net.minecraftforge.fml.common.registry.VillagerRegistry.getId(prof));
    }

    public net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession getProfessionForge()
    {
        if (this.prof == null)
        {
            this.prof = net.minecraftforge.fml.common.registry.VillagerRegistry.getById(this.getProfession());
            if (this.prof == null)
                return net.minecraftforge.fml.common.registry.VillagerRegistry.getById(0); //Farmer
        }
        return this.prof;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if (key.equals(PROFESSION))
        {
            net.minecraftforge.fml.common.registry.VillagerRegistry.onSetProfession(this, this.dataManager.get(PROFESSION));
        }
    }

    public boolean isMating()
    {
        return this.isMating;
    }

    public void setMating(boolean mating)
    {
        this.isMating = mating;
    }

    public void setPlaying(boolean playing)
    {
        this.isPlaying = playing;
    }

    public boolean isPlaying()
    {
        return this.isPlaying;
    }

    public void setRevengeTarget(@Nullable EntityLivingBase livingBase)
    {
        super.setRevengeTarget(livingBase);

        if (this.village != null && livingBase != null)
        {
            this.village.addOrRenewAgressor(livingBase);

            if (livingBase instanceof EntityPlayer)
            {
                int i = -1;

                if (this.isChild())
                {
                    i = -3;
                }

                this.village.modifyPlayerReputation(livingBase.getUniqueID(), i);

                if (this.isEntityAlive())
                {
                    this.world.setEntityState(this, (byte)13);
                }
            }
        }
    }

    public void onDeath(DamageSource cause)
    {
        if (this.village != null)
        {
            Entity entity = cause.getTrueSource();

            if (entity != null)
            {
                if (entity instanceof EntityPlayer)
                {
                    this.village.modifyPlayerReputation(entity.getUniqueID(), -2);
                }
                else if (entity instanceof IMob)
                {
                    this.village.endMatingSeason();
                }
            }
            else
            {
                EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 16.0D);

                if (entityplayer != null)
                {
                    this.village.endMatingSeason();
                }
            }
        }

        super.onDeath(cause);
    }

    public void setCustomer(@Nullable EntityPlayer player)
    {
        this.buyingPlayer = player;
    }

    @Nullable
    public EntityPlayer getCustomer()
    {
        return this.buyingPlayer;
    }

    public boolean isTrading()
    {
        return this.buyingPlayer != null;
    }

    public boolean getIsWillingToMate(boolean updateFirst)
    {
        if (!this.isWillingToMate && updateFirst && this.hasEnoughFoodToBreed())
        {
            boolean flag = false;

            for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

                if (!itemstack.isEmpty())
                {
                    if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3)
                    {
                        flag = true;
                        this.villagerInventory.decrStackSize(i, 3);
                    }
                    else if ((itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT) && itemstack.getCount() >= 12)
                    {
                        flag = true;
                        this.villagerInventory.decrStackSize(i, 12);
                    }
                }

                if (flag)
                {
                    this.world.setEntityState(this, (byte)18);
                    this.isWillingToMate = true;
                    break;
                }
            }
        }

        return this.isWillingToMate;
    }

    public void setIsWillingToMate(boolean isWillingToMate)
    {
        this.isWillingToMate = isWillingToMate;
    }

    public void useRecipe(MerchantRecipe recipe)
    {
        recipe.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
        int i = 3 + this.rand.nextInt(4);

        if (recipe.getToolUses() == 1 || this.rand.nextInt(5) == 0)
        {
            this.timeUntilReset = 40;
            this.needsInitilization = true;
            this.isWillingToMate = true;

            if (this.buyingPlayer != null)
            {
                this.lastBuyingPlayer = this.buyingPlayer.getUniqueID();
            }
            else
            {
                this.lastBuyingPlayer = null;
            }

            i += 5;
        }

        if (recipe.getItemToBuy().getItem() == Items.EMERALD)
        {
            this.wealth += recipe.getItemToBuy().getCount();
        }

        if (recipe.getRewardsExp())
        {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + 0.5D, this.posZ, i));
        }

        if (this.buyingPlayer instanceof EntityPlayerMP)
        {
            CriteriaTriggers.VILLAGER_TRADE.trigger((EntityPlayerMP)this.buyingPlayer, this, recipe.getItemToSell());
        }
    }

    public void verifySellingItem(ItemStack stack)
    {
        if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20)
        {
            this.livingSoundTime = -this.getTalkInterval();
            this.playSound(stack.isEmpty() ? SoundEvents.ENTITY_VILLAGER_NO : SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Nullable
    public MerchantRecipeList getRecipes(EntityPlayer player)
    {
        if (this.buyingList == null)
        {
            this.populateBuyingList();
        }

        return this.buyingList;
    }

    public void populateBuyingList()
    {
        if (this.careerId != 0 && this.careerLevel != 0)
        {
            ++this.careerLevel;
        }
        else
        {
            this.careerId = this.getProfessionForge().getRandomCareer(this.rand) + 1;
            this.careerLevel = 1;
        }

        if (this.buyingList == null)
        {
            this.buyingList = new MerchantRecipeList();
        }

        int i = this.careerId - 1;
        int j = this.careerLevel - 1;
        java.util.List<ITradeList> trades = this.getProfessionForge().getCareer(i).getTrades(j);

        if (trades != null)
        {
            for (ITradeList entityvillager$itradelist : trades)
            {
                // CraftBukkit start
                // this is a hack. this must be done because otherwise, if
                // mojang adds a new type of villager merchant option, it will need to
                // have event handling added manually. this is better than having to do that.
                MerchantRecipeList list = new MerchantRecipeList();
                entityvillager$itradelist.addMerchantRecipe(this, list /*this.buyingList*/, this.rand);
                for (MerchantRecipe recipe : list) {
                    VillagerAcquireTradeEvent event = new VillagerAcquireTradeEvent((Villager) getBukkitEntity(), recipe.asBukkit());
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        this.buyingList.add(CraftMerchantRecipe.fromBukkit(event.getRecipe()).toMinecraft());
                    }
                }
                // CraftBukkit end
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void setRecipes(@Nullable MerchantRecipeList recipeList)
    {
    }

    public World getWorld()
    {
        return this.world;
    }

    public BlockPos getPos()
    {
        return new BlockPos(this);
    }

    public ITextComponent getDisplayName()
    {
        Team team = this.getTeam();
        String s = this.getCustomNameTag();

        if (s != null && !s.isEmpty())
        {
            TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(team, s));
            textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
            textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
            return textcomponentstring;
        }
        else
        {
            if (this.buyingList == null)
            {
                this.populateBuyingList();
            }

            String s1 = null;

            switch (this.getProfession())
            {
                case 0:

                    if (this.careerId == 1)
                    {
                        s1 = "farmer";
                    }
                    else if (this.careerId == 2)
                    {
                        s1 = "fisherman";
                    }
                    else if (this.careerId == 3)
                    {
                        s1 = "shepherd";
                    }
                    else if (this.careerId == 4)
                    {
                        s1 = "fletcher";
                    }

                    break;
                case 1:

                    if (this.careerId == 1)
                    {
                        s1 = "librarian";
                    }
                    else if (this.careerId == 2)
                    {
                        s1 = "cartographer";
                    }

                    break;
                case 2:
                    s1 = "cleric";
                    break;
                case 3:

                    if (this.careerId == 1)
                    {
                        s1 = "armor";
                    }
                    else if (this.careerId == 2)
                    {
                        s1 = "weapon";
                    }
                    else if (this.careerId == 3)
                    {
                        s1 = "tool";
                    }

                    break;
                case 4:

                    if (this.careerId == 1)
                    {
                        s1 = "butcher";
                    }
                    else if (this.careerId == 2)
                    {
                        s1 = "leather";
                    }

                    break;
                case 5:
                    s1 = "nitwit";
            }

            s1 = this.getProfessionForge().getCareer(this.careerId-1).getName();
            {
                ITextComponent itextcomponent = new TextComponentTranslation("entity.Villager." + s1);
                itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
                itextcomponent.getStyle().setInsertion(this.getCachedUniqueIdString());

                if (team != null)
                {
                    itextcomponent.getStyle().setColor(team.getColor());
                }

                return itextcomponent;
            }
        }
    }

    public float getEyeHeight()
    {
        return this.isChild() ? 0.81F : 1.62F;
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 12)
        {
            this.spawnParticles(EnumParticleTypes.HEART);
        }
        else if (id == 13)
        {
            this.spawnParticles(EnumParticleTypes.VILLAGER_ANGRY);
        }
        else if (id == 14)
        {
            this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY);
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles(EnumParticleTypes particleType)
    {
        for (int i = 0; i < 5; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 1.0D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        return this.finalizeMobSpawn(difficulty, livingdata, true);
    }

    public IEntityLivingData finalizeMobSpawn(DifficultyInstance p_190672_1_, @Nullable IEntityLivingData p_190672_2_, boolean p_190672_3_)
    {
        p_190672_2_ = super.onInitialSpawn(p_190672_1_, p_190672_2_);

        if (p_190672_3_)
        {
            net.minecraftforge.fml.common.registry.VillagerRegistry.setRandomProfession(this, this.world.rand);
        }

        this.setAdditionalAItasks();
        this.populateBuyingList();
        return p_190672_2_;
    }

    public void setLookingForHome()
    {
        this.isLookingForHome = true;
    }

    public EntityVillager createChild(EntityAgeable ageable)
    {
        EntityVillager entityvillager = new EntityVillager(this.world);
        entityvillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null);
        return entityvillager;
    }

    public boolean canBeLeashedTo(EntityPlayer player)
    {
        return false;
    }

    public void onStruckByLightning(@Nullable EntityLightningBolt lightningBolt)
    {
        if (!this.world.isRemote && !this.isDead)
        {
            EntityWitch entitywitch = new EntityWitch(this.world);
            entitywitch.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            entitywitch.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entitywitch)), (IEntityLivingData)null);
            entitywitch.setNoAI(this.isAIDisabled());

            if (this.hasCustomName())
            {
                entitywitch.setCustomNameTag(this.getCustomNameTag());
                entitywitch.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
            }

            this.world.spawnEntity(entitywitch);
            this.setDead();
        }
    }

    public InventoryBasic getVillagerInventory()
    {
        return this.villagerInventory;
    }

    protected void updateEquipmentIfNeeded(EntityItem itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();
        Item item = itemstack.getItem();

        if (this.canVillagerPickupItem(item))
        {
            ItemStack itemstack1 = this.villagerInventory.addItem(itemstack);

            if (itemstack1.isEmpty())
            {
                itemEntity.setDead();
            }
            else
            {
                itemstack.setCount(itemstack1.getCount());
            }
        }
    }

    private boolean canVillagerPickupItem(Item itemIn)
    {
        return itemIn == Items.BREAD || itemIn == Items.POTATO || itemIn == Items.CARROT || itemIn == Items.WHEAT || itemIn == Items.WHEAT_SEEDS || itemIn == Items.BEETROOT || itemIn == Items.BEETROOT_SEEDS;
    }

    public boolean hasEnoughFoodToBreed()
    {
        return this.hasEnoughItems(1);
    }

    public boolean canAbondonItems()
    {
        return this.hasEnoughItems(2);
    }

    public boolean wantsMoreFood()
    {
        boolean flag = this.getProfession() == 0;

        if (flag)
        {
            return !this.hasEnoughItems(5);
        }
        else
        {
            return !this.hasEnoughItems(1);
        }
    }

    private boolean hasEnoughItems(int multiplier)
    {
        boolean flag = this.getProfession() == 0;

        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3 * multiplier || itemstack.getItem() == Items.POTATO && itemstack.getCount() >= 12 * multiplier || itemstack.getItem() == Items.CARROT && itemstack.getCount() >= 12 * multiplier || itemstack.getItem() == Items.BEETROOT && itemstack.getCount() >= 12 * multiplier)
                {
                    return true;
                }

                if (flag && itemstack.getItem() == Items.WHEAT && itemstack.getCount() >= 9 * multiplier)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isFarmItemInInventory()
    {
        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

            if (!itemstack.isEmpty() && (itemstack.getItem() == Items.WHEAT_SEEDS || itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT || itemstack.getItem() == Items.BEETROOT_SEEDS))
            {
                return true;
            }
        }

        return false;
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
        if (super.replaceItemInInventory(inventorySlot, itemStackIn))
        {
            return true;
        }
        else
        {
            int i = inventorySlot - 300;

            if (i >= 0 && i < this.villagerInventory.getSizeInventory())
            {
                this.villagerInventory.setInventorySlotContents(i, itemStackIn);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public static class EmeraldForItems implements ITradeList
        {
            public final Item buyingItem;
            public final PriceInfo price;

            public EmeraldForItems(Item itemIn, PriceInfo priceIn)
            {
                this.buyingItem = itemIn;
                this.price = priceIn;
            }

            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
            {
                int i = 1;

                if (this.price != null)
                {
                    i = this.price.getPrice(random);
                }

                recipeList.add(new MerchantRecipe(new ItemStack(this.buyingItem, i, 0), Items.EMERALD));
            }
        }

    public interface ITradeList
    {
        void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random);
    }

    public static class ItemAndEmeraldToItem implements ITradeList
        {
            public final ItemStack buyingItemStack;
            public final PriceInfo buyingPriceInfo;
            public final ItemStack sellingItemstack;
            public final PriceInfo sellingPriceInfo;

            public ItemAndEmeraldToItem(Item p_i45813_1_, PriceInfo p_i45813_2_, Item p_i45813_3_, PriceInfo p_i45813_4_)
            {
                this.buyingItemStack = new ItemStack(p_i45813_1_);
                this.buyingPriceInfo = p_i45813_2_;
                this.sellingItemstack = new ItemStack(p_i45813_3_);
                this.sellingPriceInfo = p_i45813_4_;
            }

            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
            {
                int i = this.buyingPriceInfo.getPrice(random);
                int j = this.sellingPriceInfo.getPrice(random);
                recipeList.add(new MerchantRecipe(new ItemStack(this.buyingItemStack.getItem(), i, this.buyingItemStack.getMetadata()), new ItemStack(Items.EMERALD), new ItemStack(this.sellingItemstack.getItem(), j, this.sellingItemstack.getMetadata())));
            }
        }

    public static class ListEnchantedBookForEmeralds implements ITradeList
        {
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
            {
                Enchantment enchantment = (Enchantment)Enchantment.REGISTRY.getRandomObject(random);
                int i = MathHelper.getInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
                ItemStack itemstack = ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, i));
                int j = 2 + random.nextInt(5 + i * 10) + 3 * i;

                if (enchantment.isTreasureEnchantment())
                {
                    j *= 2;
                }

                if (j > 64)
                {
                    j = 64;
                }

                recipeList.add(new MerchantRecipe(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemstack));
            }
        }

    public static class ListEnchantedItemForEmeralds implements ITradeList
        {
            public final ItemStack enchantedItemStack;
            public final PriceInfo priceInfo;

            public ListEnchantedItemForEmeralds(Item p_i45814_1_, PriceInfo p_i45814_2_)
            {
                this.enchantedItemStack = new ItemStack(p_i45814_1_);
                this.priceInfo = p_i45814_2_;
            }

            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
            {
                int i = 1;

                if (this.priceInfo != null)
                {
                    i = this.priceInfo.getPrice(random);
                }

                ItemStack itemstack = new ItemStack(Items.EMERALD, i, 0);
                ItemStack itemstack1 = EnchantmentHelper.addRandomEnchantment(random, new ItemStack(this.enchantedItemStack.getItem(), 1, this.enchantedItemStack.getMetadata()), 5 + random.nextInt(15), false);
                recipeList.add(new MerchantRecipe(itemstack, itemstack1));
            }
        }
    //MODDERS DO NOT USE OR EDIT THIS IN ANY WAY IT WILL HAVE NO EFFECT, THIS IS JUST IN HERE TO ALLOW FORGE TO ACCESS IT
    @Deprecated
    public static ITradeList[][][][] GET_TRADES_DONT_USE(){ return DEFAULT_TRADE_LIST_MAP; }

    public static class ListItemForEmeralds implements ITradeList
        {
            public final ItemStack itemToBuy;
            public final PriceInfo priceInfo;

            public ListItemForEmeralds(Item par1Item, PriceInfo priceInfo)
            {
                this.itemToBuy = new ItemStack(par1Item);
                this.priceInfo = priceInfo;
            }

            public ListItemForEmeralds(ItemStack stack, PriceInfo priceInfo)
            {
                this.itemToBuy = stack;
                this.priceInfo = priceInfo;
            }

            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
            {
                int i = 1;

                if (this.priceInfo != null)
                {
                    i = this.priceInfo.getPrice(random);
                }

                ItemStack itemstack;
                ItemStack itemstack1;

                if (i < 0)
                {
                    itemstack = new ItemStack(Items.EMERALD);
                    itemstack1 = new ItemStack(this.itemToBuy.getItem(), -i, this.itemToBuy.getMetadata());
                }
                else
                {
                    itemstack = new ItemStack(Items.EMERALD, i, 0);
                    itemstack1 = new ItemStack(this.itemToBuy.getItem(), 1, this.itemToBuy.getMetadata());
                }

                recipeList.add(new MerchantRecipe(itemstack, itemstack1));
            }
        }

    public static class PriceInfo extends Tuple<Integer, Integer>
        {
            public PriceInfo(int p_i45810_1_, int p_i45810_2_)
            {
                super(p_i45810_1_, p_i45810_2_);

                if (p_i45810_2_ < p_i45810_1_)
                {
                    EntityVillager.LOGGER.warn("PriceRange({}, {}) invalid, {} smaller than {}", p_i45810_1_, p_i45810_2_, p_i45810_2_, p_i45810_1_);
                }
            }

            public int getPrice(Random rand)
            {
                return (Integer) this.getFirst() >= (Integer) this.getSecond() ? (Integer) this.getFirst() : (Integer) this.getFirst() + rand.nextInt((Integer) this.getSecond() - (Integer) this.getFirst() + 1);
            }
        }

    static class TreasureMapForEmeralds implements ITradeList
        {
            public final PriceInfo value;
            public final String destination;
            public final MapDecoration.Type destinationType;

            public TreasureMapForEmeralds(PriceInfo p_i47340_1_, String p_i47340_2_, MapDecoration.Type p_i47340_3_)
            {
                this.value = p_i47340_1_;
                this.destination = p_i47340_2_;
                this.destinationType = p_i47340_3_;
            }

            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
            {
                int i = this.value.getPrice(random);
                World world = merchant.getWorld();
                BlockPos blockpos = world.findNearestStructure(this.destination, merchant.getPos(), true);

                if (blockpos != null)
                {
                    ItemStack itemstack = ItemMap.setupNewMap(world, (double)blockpos.getX(), (double)blockpos.getZ(), (byte)2, true, true);
                    ItemMap.renderBiomePreviewMap(world, itemstack);
                    MapData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
                    itemstack.setTranslatableName("filled_map." + this.destination.toLowerCase(Locale.ROOT));
                    recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack));
                }
            }
        }
}