package dev.cattails.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.util.PlayerBuffStore;
import dev.cattails.util.PlayerStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

import java.util.Map;
import java.util.Objects;

import static dev.cattails.SurviveTheNight.INSTANCE;
import static dev.cattails.events.PlayerDeathSystem.DEAD_PLAYERS;
import static dev.cattails.util.PlayerBuffStore.BuffPrices;
import static dev.cattails.util.PlayerBuffStore.Buffs;

public class BuffStorePage extends InteractiveCustomUIPage<BuffStorePage.BuffStorePageData> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();


    public static class BuffStorePageData {

        public String action;

        public static final BuilderCodec<BuffStorePage.BuffStorePageData> CODEC =
                BuilderCodec.builder(BuffStorePage.BuffStorePageData.class, BuffStorePage.BuffStorePageData::new)
                        .append(new KeyedCodec<>
                                        ("Action", Codec.STRING),
                                (BuffStorePage.BuffStorePageData o, String v) -> o.action = v,
                                (BuffStorePage.BuffStorePageData o) -> o.action)
                        .add().build();
    }

    public BuffStorePage(@Nonnull PlayerRef playerRef) {

        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BuffStorePage.BuffStorePageData.CODEC);

    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store){

        commandBuilder.append("Pages/Store.ui");

        String HealthIcon = "UI/Custom/Pages/HealthIcon.png";
        String StaminaIcon = "UI/Custom/Pages/StaminaIcon.png";
        String UltIcon = "UI/Custom/Pages/UltIcon.png";
        String RevivesIcon = "UI/Custom/Pages/RevivesIcon.png";

        commandBuilder.set("#HealthIcon.AssetPath", HealthIcon);
        commandBuilder.set("#StaminaIcon.AssetPath", StaminaIcon);
        commandBuilder.set("#UltimateIcon.AssetPath", UltIcon);
        commandBuilder.set("#RevivesIcon.AssetPath", RevivesIcon);

        for (var buffEntry : Buffs.entrySet()) {
            String buffName = buffEntry.getKey();
            Map<Integer, Integer> levels = buffEntry.getValue();

            for (var levelEntry : levels.entrySet()) {
                int level = levelEntry.getKey();
                int value = levelEntry.getValue();

                String selector = "#" + buffName + level;
                String selectorPrice = "#" + buffName + level + "Price";

                int priceValue = BuffPrices.getOrDefault(buffName, Map.of()).getOrDefault(level, 1);

                eventBuilder.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        selector,
                        new EventData().append("Action", buffName + ":" + level)
                );

                if(Objects.equals(buffName, "Revives")){
                    if(level == 3){
                        commandBuilder.set(selector + ".Text", "Revive: " + value + "+");
                    } else {
                        commandBuilder.set(selector + ".Text", "Revive: " + value);
                    }
                } else {
                    commandBuilder.set(selector + ".Text", String.valueOf(value));
                }

                commandBuilder.set(selectorPrice + ".TextSpans", Message.raw("$" + String.format("%,d", priceValue)));

            }
        }

    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @NonNullDecl BuffStorePage.BuffStorePageData data) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) {
            LOGGER.atWarning().log("Player is null");
            return;
        }

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        PlayerStore playerStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerStoreComponent());
        PlayerBuffStore playerBuffStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerBuffsStoreComponent());

        String action = data.action;
        String[] parts = action.split(":", 2);
        if (parts.length != 2) {
            LOGGER.atWarning().log("Invalid action parameter: " + action);
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        String buffName = parts[0];
        int level;
        try {
            level = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ignored) {
            LOGGER.atWarning().log("Invalid level number: " + parts[1]);
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        Map<Integer, Integer> BuffLevels = Buffs.get(buffName);
        Map<Integer, Integer> BuffPriceLevels = BuffPrices.get(buffName);
        if (BuffLevels == null || BuffPriceLevels == null) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        };

        Integer BuffValue = BuffLevels.get(level);
        Integer BuffPrice = BuffPriceLevels.get(level);
        if (BuffValue == null || BuffPrice == null) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        };


        if(Objects.equals(buffName, "Health")){

            if(playerBuffStore.canAfford(BuffPrice)){
                playerBuffStore.spendCurrency(BuffPrice);
                playerBuffStore.addHealthBoost(BuffValue);
                player.sendMessage(Message.raw("Bought: " + "Health Regen" + level + " | " + playerBuffStore.getHealthBoost() + " (+" + BuffValue + ")"));
            } else {
                player.sendMessage(Message.raw("Cant afford.."));
            }
        }

        if(Objects.equals(buffName, "Stamina")){
            if(playerBuffStore.canAfford(BuffPrice)){
                playerBuffStore.spendCurrency(BuffPrice);
                playerBuffStore.addStaminaBoost(BuffValue);
                player.sendMessage(Message.raw("Bought: " + "Stamina Regen" + level + " | " + playerBuffStore.getStaminaBoost() + " (+" + BuffValue + ")"));
            } else {
                player.sendMessage(Message.raw("Cant afford.."));
            }
        }

        if(Objects.equals(buffName, "Ultimate")){
            if(playerBuffStore.canAfford(BuffPrice)){
                playerBuffStore.spendCurrency(BuffPrice);
                playerBuffStore.addUltimateBoost(BuffValue);
                player.sendMessage(Message.raw("Bought: " + "Ultimate Regen" + level + " | " + playerBuffStore.getUltimateBoost() + " (+" + BuffValue + ")"));
            } else {
                player.sendMessage(Message.raw("Cant afford.."));
            }
        }

        if(Objects.equals(buffName, "Revives")){
            if(playerBuffStore.canAfford(BuffPrice)){
                playerBuffStore.spendCurrency(BuffPrice);
                playerBuffStore.addRevives(BuffValue);
                player.sendMessage(Message.raw("Bought: " + "Revive" + level));

                for (PlayerRef deadRef : DEAD_PLAYERS) {
                    LOGGER.atInfo().log("Dead Player: " + deadRef.getUsername());
                    Player deadPlayer = store.getComponent(deadRef.getReference(), Player.getComponentType());
                    if (deadPlayer == null) continue;

                    LOGGER.atInfo().log("Dead Player contied");

                    Player.setGameMode(deadRef.getReference(), GameMode.Adventure, store);
                    playerStore.setGhostMode(false);
                    DEAD_PLAYERS.remove(deadRef);

                    player.sendMessage(Message.raw("You have been revived by: " + player.getDisplayName()));
                }
            } else {
                player.sendMessage(Message.raw("Cant afford.."));
            }


        }

        player.getPageManager().setPage(ref, store, Page.None);

    }

}
