package dev.cattails.util;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.Pair;

import javax.annotation.Nonnull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static dev.cattails.SurviveTheNight.INSTANCE;

public final class MobSpawn extends DelayedEntitySystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final Map<Integer, Map<String, Integer>> cycles = new HashMap<>();


    public MobSpawn() {
        super(43.0f);
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        World world = player.getWorld();
        if(world == null) return;

        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        LocalDateTime localDateTime = worldTimeResource.getGameDateTime();
        int day = localDateTime.getDayOfMonth();

        LOGGER.atInfo().log("Check: " + day + " | " + localDateTime.getHour() + ":" + localDateTime.getMinute());

        if(!cycles.containsKey(day)){
            Map<String, Integer> currentNight = new HashMap<>();
            currentNight.put("Void_Crawler_Mech", 1);
            currentNight.put("Root_Spirit_Mech", 1);
            currentNight.put("Bleached_Hound_Mech", 1);
            currentNight.put("Golem_Mech", 1);

            cycles.put(day, currentNight);
        }

        Map<String, Integer> currentNight = cycles.get(day);
        int CrawlerSpawns = currentNight.get("Void_Crawler_Mech");
        int RootSpawns = currentNight.get("Root_Spirit_Mech");
        int HoundSpawns = currentNight.get("Bleached_Hound_Mech");
        int GolemSpawns = currentNight.get("Golem_Mech");

        int NewCrawlSpawns = Math.toIntExact((int) CrawlerSpawns + Math.round(CrawlerSpawns * (1.0 + day * 0.3)));
        int NewRootSpawns = Math.toIntExact((int) RootSpawns + Math.round(RootSpawns * (1.0 + day * 0.3)));
        int NewHoundSpawns = Math.toIntExact((int) HoundSpawns + Math.round(HoundSpawns * (1.0 + day * 0.3)));
        int NewGolemSpawns = Math.toIntExact((int) GolemSpawns + Math.round(GolemSpawns * (1.0 + day * 0.3)));
        if(localDateTime.getHour() == 21){
            for(int i = 0; i <= CrawlerSpawns; i++){
                SpawnMob(world, playerRef, store, "Void_Crawler_Mech");
            }

            currentNight.put("Void_Crawler_Mech", NewCrawlSpawns);

            return;
        }

        if(localDateTime.getHour() == 23){
            for(int i = 0; i <= CrawlerSpawns; i++){
                SpawnMob(world, playerRef, store, "Void_Crawler_Mech");
            }
            currentNight.put("Void_Crawler_Mech", NewCrawlSpawns);

            for(int i = 0; i <= RootSpawns; i++){
                SpawnMob(world, playerRef, store, "Root_Spirit_Mech");
            }
            currentNight.put("Root_Spirit_Mech", NewRootSpawns);

            for(int i = 0; i <= HoundSpawns; i++){
                SpawnMob(world, playerRef, store, "Bleached_Hound_Mech");
            }
            currentNight.put("Bleached_Hound_Mech", NewHoundSpawns);

            return;
        }

        if(localDateTime.getHour() == 2){
            for(int i = 0; i <= RootSpawns; i++){
                SpawnMob(world, playerRef, store, "Root_Spirit_Mech");
            }
            currentNight.put("Root_Spirit_Mech", NewRootSpawns);

            for(int i = 0; i <= HoundSpawns; i++){
                SpawnMob(world, playerRef, store, "Bleached_Hound_Mech");
            }
            currentNight.put("Bleached_Hound_Mech", NewHoundSpawns);

            for(int i = 0; i <= GolemSpawns; i++){
                SpawnMob(world, playerRef, store, "Golem_Mech");
            }
            currentNight.put("Golem_Mech", NewGolemSpawns);

            return;
        }

        LOGGER.atInfo().log("No mobs spawned..");

    }

    public static void SpawnMob(World world, PlayerRef playerRef, Store<EntityStore> store, String mobName){

        world.execute(() -> {

            int xCord = (int) playerRef.getTransform().getPosition().getX();
            int yCord = (int) playerRef.getTransform().getPosition().getY();
            int zCord = (int) playerRef.getTransform().getPosition().getZ();

            double minRadius = 50.0;
            double maxRadius = 150.0;

            double angle = Math.random() * Math.PI * 2;
            double radius = minRadius + Math.random() * (maxRadius - minRadius);

            double spawnX = xCord + Math.cos(angle) * radius;
            double spawnZ = zCord + Math.sin(angle) * radius;

            NPCPlugin npcPlugin = NPCPlugin.get();
            int Mob = npcPlugin.getIndex(mobName);

            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(mobName);
            if(modelAsset == null){
                LOGGER.atWarning().log(mobName + " has no asset");
                return;
            }
            Model model = Model.createScaledModel(modelAsset, 1.0f);

            Pair<Ref<EntityStore>, NPCEntity> npcPair = npcPlugin.spawnEntity(
                    store,
                    Mob,
                    new Vector3d(spawnX, yCord+20, spawnZ), // the block coordinates
                    new Vector3f(0, 0, 0), // the rotation is in radiant....
                    model,
                    null // this is a callback after the entity spawn or smth like that
            );

        });

    }


}