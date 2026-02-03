package dev.cattails.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.util.PlayerBuffStore;

import javax.annotation.Nonnull;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static dev.cattails.SurviveTheNight.INSTANCE;

public class OverlayHud extends CustomUIHud {

    public OverlayHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder){

        commandBuilder.append("Huds/OverlayHud.ui");

        Ref<EntityStore> ref = getPlayerRef().getReference();
        if(ref == null) return;
        Store<EntityStore> store = ref.getStore();

        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if(player == null || playerRef == null) return;

        World world = player.getWorld();
        if(world == null) return;

        PlayerBuffStore playerBuffStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerBuffsStoreComponent());

        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        LocalDateTime localDateTime = worldTimeResource.getGameDateTime();
        String day = String.valueOf(localDateTime.getDayOfMonth());
        String time = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));


        String currencyIcon = "UI/Custom/Huds/CurrencyIcon.png";
        String HealthIcon = "UI/Custom/Huds/HealthIcon.png";
        String StaminaIcon = "UI/Custom/Huds/StaminaIcon.png";
        String UltIcon = "UI/Custom/Huds/UltIcon.png";
        commandBuilder.set("#CurrencyIcon.AssetPath", currencyIcon);
        commandBuilder.set("#HealthIcon.AssetPath", HealthIcon);
        commandBuilder.set("#StaminaIcon.AssetPath", StaminaIcon);
        commandBuilder.set("#UltIcon.AssetPath", UltIcon);
        commandBuilder.set("#Currency.TextSpans", Message.raw(String.valueOf(playerBuffStore.getCurrency())));
        commandBuilder.set("#DayValue.TextSpans", Message.raw(day));
        commandBuilder.set("#TimeValue.TextSpans", Message.raw(time));
        commandBuilder.set("#HealthStat.TextSpans", Message.raw("+" + playerBuffStore.getHealthBoost() + " HP / 5s"));
        commandBuilder.set("#StaminaStat.TextSpans", Message.raw("+" + playerBuffStore.getStaminaBoost() + " STAM / 5s"));
        commandBuilder.set("#UltStat.TextSpans", Message.raw("+" + playerBuffStore.getUltimateBoost() + " ULT / 5s"));

    }

}
