package org.metamechanists.quaptics.implementation.multiblocks.beacons.components;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.quaptics.connections.ConnectionGroup;
import org.metamechanists.quaptics.connections.ConnectionPoint;
import org.metamechanists.quaptics.connections.ConnectionPointType;
import org.metamechanists.quaptics.connections.Link;
import org.metamechanists.quaptics.implementation.base.ConnectedBlock;
import org.metamechanists.quaptics.implementation.Settings;
import org.metamechanists.quaptics.items.Lore;
import org.metamechanists.quaptics.items.Tier;
import org.metamechanists.quaptics.utils.BlockStorageAPI;
import org.metamechanists.quaptics.utils.Keys;
import org.metamechanists.quaptics.utils.id.complex.ConnectionGroupId;
import org.metamechanists.quaptics.utils.models.ModelBuilder;
import org.metamechanists.quaptics.utils.models.components.ModelCuboid;

import java.util.List;
import java.util.Optional;


public class BeaconPowerSupply extends ConnectedBlock {
    public static final Settings BEACON_POWER_SUPPLY_1_SETTINGS = Settings.builder()
            .tier(Tier.INTERMEDIATE)
            .operatingPowerHidden(true)
            .build();
    public static final Settings BEACON_POWER_SUPPLY_2_SETTINGS = Settings.builder()
            .tier(Tier.ADVANCED)
            .operatingPowerHidden(true)
            .build();

    public static final SlimefunItemStack BEACON_POWER_SUPPLY_1 = new SlimefunItemStack(
            "QP_BEACON_POWER_SUPPLY_1",
            Material.GRAY_CONCRETE,
            "&dBeacon Power Supply &5I",
            Lore.create(BEACON_POWER_SUPPLY_1_SETTINGS,
                    Lore.multiblockComponent()));
    public static final SlimefunItemStack BEACON_POWER_SUPPLY_2 = new SlimefunItemStack(
            "QP_BEACON_POWER_SUPPLY_2",
            Material.GRAY_CONCRETE,
            "&dBeacon Power Supply &5II",
            Lore.create(BEACON_POWER_SUPPLY_2_SETTINGS,
                    Lore.multiblockComponent()));

    public BeaconPowerSupply(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final Settings settings) {
        super(itemGroup, item, recipeType, recipe, settings);
    }

    @Override
    protected float getConnectionRadius() {
        return 1.0F;
    }
    @Override
    protected DisplayGroup initModel(@NotNull final Location location, @NotNull final Player player) {
        return new ModelBuilder()
                .add("main", new ModelCuboid()
                        .material(Material.GRAY_CONCRETE)
                        .size(1.1F, 1.0F, 1.1F)
                        .rotation(Math.PI/4))
                .add("panel1", new ModelCuboid()
                        .material(settings.getTier().concreteMaterial)
                        .size(0.8F, 0.7F, 1.2F)
                        .rotation(Math.PI/4))
                .add("panel2", new ModelCuboid()
                        .material(settings.getTier().concreteMaterial)
                        .size(1.2F, 0.7F, 0.8F)
                        .rotation(Math.PI/4))
                .buildAtBlockCenter(location);
    }
    @Override
    protected List<ConnectionPoint> initConnectionPoints(final ConnectionGroupId groupId, final Player player, final Location location) {
        return List.of(
                new ConnectionPoint(ConnectionPointType.INPUT, groupId, "input", location.clone().toCenterLocation().add(new Vector(0, 0, -getConnectionRadius()))));
    }
    @Override
    protected void initBlockStorage(final @NotNull Location location) {
        BlockStorageAPI.set(location, Keys.BS_INPUT_POWER, 0.0);
    }

    @Override
    public void onInputLinkUpdated(@NotNull final ConnectionGroup group, @NotNull final Location location) {
        if (doBurnoutCheck(group, "input")) {
            return;
        }

        final Optional<Link> link = getLink(location, "input");
        if (link.isEmpty()) {
            return;
        }

        final double power = link.get().getPower();
        final double frequency = link.get().getFrequency();

        BlockStorageAPI.set(location, Keys.BS_POWER, power);
        BlockStorageAPI.set(location, Keys.BS_FREQUENCY, frequency);
    }
}
