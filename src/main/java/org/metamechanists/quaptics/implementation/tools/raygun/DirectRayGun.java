package org.metamechanists.quaptics.implementation.tools.raygun;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.metamechanists.quaptics.beams.DeprecatedBeamStorage;
import org.metamechanists.quaptics.beams.beam.LifetimeDirectBeam;
import org.metamechanists.quaptics.implementation.Settings;
import org.metamechanists.quaptics.items.Lore;
import org.metamechanists.quaptics.items.Tier;
import org.metamechanists.quaptics.utils.transformations.TransformationUtils;


public class DirectRayGun extends AbstractRayGun {
    public static final Settings RAY_GUN_3_SETTINGS = Settings.builder()
            .tier(Tier.INTERMEDIATE)
            .chargeCapacity(100000.0)
            .outputPower(300.0)
            .range(56)
            .damage(6)
            .projectileMaterial(Material.LIME_CONCRETE)
            .build();
    public static final Settings RAY_GUN_4_SETTINGS = Settings.builder()
            .tier(Tier.INTERMEDIATE)
            .chargeCapacity(1000000.0)
            .outputPower(2000.0)
            .range(56)
            .damage(12)
            .projectileMaterial(Material.LIME_CONCRETE)
            .build();

    public static final SlimefunItemStack RAY_GUN_3 = new SlimefunItemStack(
            "QP_RAY_GUN_3",
            Material.GOLDEN_HORSE_ARMOR,
            "&bRay Gun &3III",
            Lore.buildChargeableLore(RAY_GUN_3_SETTINGS, 0,
                    "&7● &eRight Click &7to fire"));
    public static final SlimefunItemStack RAY_GUN_4 = new SlimefunItemStack(
            "QP_RAY_GUN_4",
            Material.DIAMOND_HORSE_ARMOR,
            "&bRay Gun &3IV",
            Lore.buildChargeableLore(RAY_GUN_4_SETTINGS, 0,
                    "&7● &eRight Click &7to fire"));

    public DirectRayGun(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final Settings settings) {
        super(itemGroup, item, recipeType, recipe, settings);
    }

    @Override
    public void fireRayGun(final Player player, final Location eyeLocation, final Location handLocation, final Location target) {
        final RayTraceResult result = eyeLocation.getWorld().rayTrace(
                eyeLocation.clone(), Vector.fromJOML(TransformationUtils.getDisplacement(eyeLocation, target)),
                settings.getRange(), FluidCollisionMode.NEVER, true, 0.095F,
                entity -> !entity.getUniqueId().equals(player.getUniqueId())
                        && !(entity instanceof Display)
                        && !(entity instanceof Interaction)
                        && !(entity instanceof ArmorStand));

        if (result == null) {
            DeprecatedBeamStorage.deprecate(new LifetimeDirectBeam(settings.getProjectileMaterial(), handLocation, target, 0.095F, 0, 5));
            return;
        }

        final Vector position = result.getHitPosition();
        DeprecatedBeamStorage.deprecate(new LifetimeDirectBeam(
                settings.getProjectileMaterial(),
                handLocation,
                new Location(handLocation.getWorld(), position.getX(), position.getY(), position.getZ()),
                0.095F,
                0,
                5));

        if (result.getHitEntity() instanceof final Damageable damageable
                && Slimefun.getProtectionManager().hasPermission(player, damageable.getLocation(), io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction.ATTACK_ENTITY)) {
            damageable.damage(settings.getDamage());
        }
    }
}
