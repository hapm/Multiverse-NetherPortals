package com.onarandombox.MultiverseNetherPortals.utils;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.logging.Level;

public class MVLinkChecker {
    private MultiverseNetherPortals plugin;
    private MVWorldManager worldManager;

    public MVLinkChecker(MultiverseNetherPortals plugin) {
        this.plugin = plugin;
        this.worldManager = this.plugin.getCore().getMVWorldManager();
    }

    public Location findNewTeleportLocation(Location fromLocation, String worldstring, Player p) {
        MultiverseWorld tpto = this.worldManager.getMVWorld(worldstring);

        if (tpto == null) {
            this.plugin.log(Level.FINE, "Can't find world " + worldstring);
        } else if (!this.plugin.getCore().getMVPerms().canEnterWorld(p, tpto)) {
            this.plugin.log(Level.WARNING, "Player " + p.getName() + " can't enter world " + worldstring);
        } else if (!this.worldManager.isMVWorld(fromLocation.getWorld().getName())) {
            this.plugin.log(Level.WARNING, "World " + fromLocation.getWorld().getName() + " is not a Multiverse world");
        } else {
            this.plugin.log(Level.FINE, "Finding new teleport location for player " + p.getName() + " to world " + worldstring);

            // Set the output location to the same XYZ coords but different world
            double toScaling = this.worldManager.getMVWorld(tpto.getName()).getScaling();
            double fromScaling = this.worldManager.getMVWorld(fromLocation.getWorld().getName()).getScaling();
            if (fromScaling == 0) {
            	fromLocation = p.getBedSpawnLocation();
            }
            else if (toScaling == 0) {
            	fromLocation = tpto.getSpawnLocation();
            }
            else 
            {
            	fromLocation = this.getScaledLocation(fromLocation, fromScaling, toScaling);
            	fromLocation.setWorld(tpto.getCBWorld());
            }
            return fromLocation;
        }
        return null;
    }

    public void getNewTeleportLocation(PlayerPortalEvent event, Location fromLocation, String worldstring) {
        Location l = findNewTeleportLocation(fromLocation, worldstring, event.getPlayer());
        if (l != null)
        	event.setTo(l);
    }

    private Location getScaledLocation(Location fromLocation, double fromScaling, double toScaling) {
        double scaling = fromScaling / toScaling;
        fromLocation.setX(fromLocation.getX() * scaling);
        fromLocation.setZ(fromLocation.getZ() * scaling);
        return fromLocation;
    }
}
