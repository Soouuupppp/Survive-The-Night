package dev.cattails.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.util.PlayerBuffStore;

import javax.annotation.Nonnull;

import static dev.cattails.SurviveTheNight.INSTANCE;

public class BuffCommand extends AbstractPlayerCommand {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public BuffCommand() {
        super("buff", "add buffs");
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world){
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerBuffStore playerBuffStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerBuffsStoreComponent());

        world.execute(() -> {

            playerBuffStore.addHealthBoost(5);

            LOGGER.atInfo().log("Ran buff command: " + playerBuffStore.getHealthBoost());

        });


    }

}
