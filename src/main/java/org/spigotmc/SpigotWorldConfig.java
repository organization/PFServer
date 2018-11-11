package org.spigotmc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class SpigotWorldConfig
{

    public final String worldName;
    public final YamlConfiguration config;
    public boolean verbose;

    public SpigotWorldConfig(String worldName)
    {
        this.worldName = worldName;
        this.config = SpigotConfig.config;
        init();
    }

    public void init()
    {
        this.verbose = getBoolean( "verbose", true );

        log( "-------- World Settings For [" + worldName + "] --------" );
        SpigotConfig.readConfig( SpigotWorldConfig.class, this );
    }

    public void log(String s)
    {
        if ( verbose )
        {
            Bukkit.getLogger().info( s );
        }
    }

    public void set(String path, Object val)
    {
        config.set( "world-settings.default." + path, val );
    }

    public boolean getBoolean(String path, boolean def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getBoolean( "world-settings." + worldName + "." + path, config.getBoolean( "world-settings.default." + path ) );
    }

    public double getDouble(String path, double def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getDouble( "world-settings." + worldName + "." + path, config.getDouble( "world-settings.default." + path ) );
    }

    public int getInt(String path, int def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getInt( "world-settings." + worldName + "." + path, config.getInt( "world-settings.default." + path ) );
    }

    public <T> List getList(String path, T def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return (List<T>) config.getList( "world-settings." + worldName + "." + path, config.getList( "world-settings.default." + path ) );
    }

    public String getString(String path, String def)
    {
        config.addDefault( "world-settings.default." + path, def );
        return config.getString( "world-settings." + worldName + "." + path, config.getString( "world-settings.default." + path ) );
    }

    // Crop growth rates
    public int cactusModifier;
    public int caneModifier;
    public int melonModifier;
    public int mushroomModifier;
    public int pumpkinModifier;
    public int saplingModifier;
    public int wheatModifier;
    public int wartModifier;
    public int vineModifier;
    public int cocoaModifier;
    public int getAndValidateGrowth(String crop)
    {
        int modifier = getInt( "growth." + crop.toLowerCase(java.util.Locale.ENGLISH) + "-modifier", 100 );
        if ( modifier == 0 )
        {
            log( "Cannot set " + crop + " growth to zero, defaulting to 100" );
            modifier = 100;
        }
        log( crop + " Growth Modifier: " + modifier + "%" );

        return modifier;
    }
    public void growthModifiers()
    {
        cactusModifier = getAndValidateGrowth( "Cactus" );
        caneModifier = getAndValidateGrowth( "Cane" );
        melonModifier = getAndValidateGrowth( "Melon" );
        mushroomModifier = getAndValidateGrowth( "Mushroom" );
        pumpkinModifier = getAndValidateGrowth( "Pumpkin" );
        saplingModifier = getAndValidateGrowth( "Sapling" );
        wheatModifier = getAndValidateGrowth( "Wheat" );
        wartModifier = getAndValidateGrowth( "NetherWart" );
        vineModifier = getAndValidateGrowth( "Vine" );
        cocoaModifier = getAndValidateGrowth( "Cocoa" );
    }

    public double itemMerge;
    public void itemMerge()
    {
        itemMerge = getDouble("merge-radius.item", 2.5 );
        log( "Item Merge Radius: " + itemMerge );
    }

    public double expMerge;
    public void expMerge()
    {
        expMerge = getDouble("merge-radius.exp", 3.0 );
        log( "Experience Merge Radius: " + expMerge );
    }

    public int viewDistance;
    public void viewDistance()
    {
        viewDistance = getInt( "view-distance", Bukkit.getViewDistance() );
        log( "View Distance: " + viewDistance );
    }

    public byte mobSpawnRange;
    public void mobSpawnRange()
    {
        mobSpawnRange = (byte) getInt( "mob-spawn-range", 4 );
        log( "Mob Spawn Range: " + mobSpawnRange );
    }

    public int itemDespawnRate;
    public void itemDespawnRate()
    {
        itemDespawnRate = getInt( "item-despawn-rate", 6000 );
        log( "Item Despawn Rate: " + itemDespawnRate );
    }

    public int animalActivationRange = 32;
    public int monsterActivationRange = 32;
    public int miscActivationRange = 16;
    public boolean tickInactiveVillagers = true;
    public void activationRange()
    {
        animalActivationRange = getInt( "entity-activation-range.animals", animalActivationRange );
        monsterActivationRange = getInt( "entity-activation-range.monsters", monsterActivationRange );
        miscActivationRange = getInt( "entity-activation-range.misc", miscActivationRange );
        tickInactiveVillagers = getBoolean( "entity-activation-range.tick-inactive-villagers", tickInactiveVillagers );
        log( "Entity Activation Range: An " + animalActivationRange + " / Mo " + monsterActivationRange + " / Mi " + miscActivationRange + " / Tiv " + tickInactiveVillagers );
    }

    public int playerTrackingRange = 48;
    public int animalTrackingRange = 48;
    public int monsterTrackingRange = 48;
    public int miscTrackingRange = 32;
    public int otherTrackingRange = 64;
    public void trackingRange()
    {
        playerTrackingRange = getInt( "entity-tracking-range.players", playerTrackingRange );
        animalTrackingRange = getInt( "entity-tracking-range.animals", animalTrackingRange );
        monsterTrackingRange = getInt( "entity-tracking-range.monsters", monsterTrackingRange );
        miscTrackingRange = getInt( "entity-tracking-range.misc", miscTrackingRange );
        otherTrackingRange = getInt( "entity-tracking-range.other", otherTrackingRange );
        log( "Entity Tracking Range: Pl " + playerTrackingRange + " / An " + animalTrackingRange + " / Mo " + monsterTrackingRange + " / Mi " + miscTrackingRange + " / Other " + otherTrackingRange );
    }

    public int hopperTransfer;
    public int hopperCheck;
    public int hopperAmount;
    public void hoppers()
    {
        // Set the tick delay between hopper item movements
        hopperTransfer = getInt( "ticks-per.hopper-transfer", 8 );
        if ( SpigotConfig.version < 11 )
        {
            set( "ticks-per.hopper-check", 1 );
        }
        hopperCheck = getInt( "ticks-per.hopper-check", 1 );
        hopperAmount = getInt( "hopper-amount", 1 );
        log( "Hopper Transfer: " + hopperTransfer + " Hopper Check: " + hopperCheck + " Hopper Amount: " + hopperAmount );
    }

    public boolean randomLightUpdates;
    public void lightUpdates()
    {
        randomLightUpdates = getBoolean( "random-light-updates", false );
        log( "Random Lighting Updates: " + randomLightUpdates );
    }

    public boolean saveStructureInfo;
    public void structureInfo()
    {
        saveStructureInfo = getBoolean( "save-structure-info", true );
        log( "Structure Info Saving: " + saveStructureInfo );
        if ( !saveStructureInfo )
        {
            log( "*** WARNING *** You have selected to NOT save structure info. This may cause structures such as fortresses to not spawn mobs!" );
            log( "*** WARNING *** Please use this option with caution, SpigotMC is not responsible for any issues this option may cause in the future!" );
        }
    }

    public int arrowDespawnRate;
    public void arrowDespawnRate()
    {
        arrowDespawnRate = getInt( "arrow-despawn-rate", 1200  );
        log( "Arrow Despawn Rate: " + arrowDespawnRate );
    }

    public boolean zombieAggressiveTowardsVillager;
    public void zombieAggressiveTowardsVillager()
    {
        zombieAggressiveTowardsVillager = getBoolean( "zombie-aggressive-towards-villager", true );
        log( "Zombie Aggressive Towards Villager: " + zombieAggressiveTowardsVillager );
    }

    public boolean nerfSpawnerMobs;
    public void nerfSpawnerMobs()
    {
        nerfSpawnerMobs = getBoolean( "nerf-spawner-mobs", false );
        log( "Nerfing mobs spawned from spawners: " + nerfSpawnerMobs );
    }

    public boolean enableZombiePigmenPortalSpawns;
    public void enableZombiePigmenPortalSpawns()
    {
        enableZombiePigmenPortalSpawns = getBoolean( "enable-zombie-pigmen-portal-spawns", true );
        log( "Allow Zombie Pigmen to spawn from portal blocks: " + enableZombiePigmenPortalSpawns );
    }

    public int dragonDeathSoundRadius;
    public void keepDragonDeathPerWorld()
    {
        dragonDeathSoundRadius = getInt( "dragon-death-sound-radius", 0 );
    }

    public int witherSpawnSoundRadius;
    public void witherSpawnSoundRadius()
    {
        witherSpawnSoundRadius = getInt( "wither-spawn-sound-radius", 0 );
    }

    public int villageSeed;
    public int largeFeatureSeed;
    public int monumentSeed;
    public int slimeSeed;
    public void initWorldGenSeeds()
    {
        villageSeed = getInt( "seed-village", 10387312 );
        largeFeatureSeed = getInt( "seed-feature", 14357617 );
        monumentSeed = getInt( "seed-monument", 10387313 );
        slimeSeed = getInt( "seed-slime", 987234911 );
        log( "Custom Map Seeds:  Village: " + villageSeed + " Feature: " + largeFeatureSeed + " Monument: "  + monumentSeed + " Slime: " + slimeSeed );
    }

    public float jumpWalkExhaustion;
    public float jumpSprintExhaustion;
    public float combatExhaustion;
    public float regenExhaustion;
    public float swimMultiplier;
    public float sprintMultiplier;
    public float otherMultiplier;
    public void initHunger()
    {
        if ( SpigotConfig.version < 10 )
        {
            set( "hunger.walk-exhaustion", null );
            set( "hunger.sprint-exhaustion", null );
            set( "hunger.combat-exhaustion", 0.1 );
            set( "hunger.regen-exhaustion", 6.0 );
        }

        jumpWalkExhaustion = (float) getDouble( "hunger.jump-walk-exhaustion", 0.05 );
        jumpSprintExhaustion = (float) getDouble( "hunger.jump-sprint-exhaustion", 0.2 );
        combatExhaustion = (float) getDouble( "hunger.combat-exhaustion", 0.1 );
        regenExhaustion = (float) getDouble( "hunger.regen-exhaustion", 6.0 );
        swimMultiplier =  (float) getDouble( "hunger.swim-multiplier", 0.01 );
        sprintMultiplier = (float) getDouble( "hunger.sprint-multiplier", 0.1 );
        otherMultiplier = (float) getDouble( "hunger.other-multiplier", 0.0 );
    }

    public int currentPrimedTnt = 0;
    public int maxTntTicksPerTick;
    public void maxTntPerTick() {
        if ( SpigotConfig.version < 7 )
        {
            set( "max-tnt-per-tick", 100 );
        }
        maxTntTicksPerTick = getInt( "max-tnt-per-tick", 100 );
        log( "Max TNT Explosions: " + maxTntTicksPerTick );
    }

    public int hangingTickFrequency;
    public void hangingTickFrequency()
    {
        hangingTickFrequency = getInt( "hanging-tick-frequency", 100 );
    }

    public int tileMaxTickTime;
    public int entityMaxTickTime;
    public void maxTickTimes()
    {
        tileMaxTickTime = getInt("max-tick-time.tile", 50);
        entityMaxTickTime = getInt("max-tick-time.entity", 50);
        log("Tile Max Tick Time: " + tileMaxTickTime + "ms Entity max Tick Time: " + entityMaxTickTime + "ms");
    }

    public double squidSpawnRangeMin;
    public void squidSpawnRange()
    {
        squidSpawnRangeMin = getDouble("squid-spawn-range.min", 45.0D);
    }
}
