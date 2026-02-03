package dev.cattails.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.cattails.util.PlayerBuffStore;
import it.unimi.dsi.fastutil.Pair;

import javax.annotation.Nonnull;

import java.util.Random;

import static dev.cattails.SurviveTheNight.INSTANCE;

public class SpawnCommand extends AbstractPlayerCommand {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public SpawnCommand() {
        super("spawn", "spawn mobs");
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world){

        Player player = store.getComponent(ref, Player.getComponentType());

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
            int Golem = npcPlugin.getIndex("Golem_Mech");

            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Golem_Mech");
            if(modelAsset == null){
                LOGGER.atWarning().log("Golem_Mech not found");
                return;
            }
            Model model = Model.createScaledModel(modelAsset, 1.0f);

            Pair<Ref<EntityStore>, NPCEntity> npcPair = npcPlugin.spawnEntity(
                    store,
                    Golem,
                    new Vector3d(spawnX, yCord+20, spawnZ), // the block coordinates
                    new Vector3f(0, 0, 0), // the rotation is in radiant....
                    model,
                    null // this is a callback after the entity spawn or smth like that
            );

            LOGGER.atInfo().log("Ran Spawn command");

        });


    }

}
