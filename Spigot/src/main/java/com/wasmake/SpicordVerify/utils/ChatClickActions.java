package com.wasmake.SpicordVerify.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 * Class to send json messages to players
 * which can execute an action when clicked
 *
 * @author Wasmake
 */
public class ChatClickActions implements Listener {

    /**
     * The instance of the main class
     */
    private static Plugin plugin;
    /**
     * The singleton instance of the class
     */
    private static ChatClickActions instance;
    /**
     * Map linking an action UUID and the action
     */
    private final Map<UUID, ActionData> actionMap;

    /**
     * Private constructor
     * No new instances of the class
     * are needed to be created outside
     */
    private ChatClickActions() {
        actionMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Sets the instance of the plugin
     *
     * @param pluginInstance The plugin instance
     */
    public static void init(Plugin pluginInstance) {
        plugin = pluginInstance;
    }

    ////////////////////////////////////////////////////

    /**
     * Gets the instance of the class
     *
     * @return The class instance
     */
    public static ChatClickActions getInstance() {
        return instance == null ? (instance = new ChatClickActions()) : instance;
    }

    /**
     * Sends a clickable action message to a player
     *
     * @param player The player to send the message to
     * @param msg    The message to send to the player
     * @param expire Whether the action should expire after being used once
     * @param action The action to execute when the player clicks the message
     */
    public TextComponent sendActionMessage(Player player, String msg, boolean expire, PlayerAction action) {
        return sendActionMessage(player, new TextComponent(msg), expire, action);
    }

    /**
     * Sends a clickable action message to a player
     *
     * @param player    The player to send the message to
     * @param component The text component to send to the player
     * @param expire    Whether the action should expire after being used once
     * @param action    The action to execute when the player clicks the message
     */
    public TextComponent sendActionMessage(Player player, TextComponent component, boolean expire, PlayerAction action) {
        return sendActionMessage(player, new TextComponent[]{component}, expire, action);
    }

    /**
     * Sends clickable action messages to a player
     *
     * @param player     The player to send the message to
     * @param components The text components to send to the player
     * @param expire     Whether the action should expire after being used once
     * @param action     The action to execute when the player clicks the message
     */
    public TextComponent sendActionMessage(Player player, TextComponent[] components, boolean expire, PlayerAction action) {
        Validate.notNull(player, "Player cannot be null");
        Validate.notNull(components, "Components cannot be null");
        Validate.notNull(action, "Action cannot be null");

        UUID id = UUID.randomUUID();

        while (actionMap.keySet().contains(id)) {
            id = UUID.randomUUID();
        }

        actionMap.put(id, new ActionData(player.getUniqueId(), action, expire));

        for (BaseComponent component : components) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + id.toString()));
        }

        return components[0];
    }

    /**
     * Remove all the action messages associated with a player
     *
     * @param player The player who's actions should be removed
     */
    public void removeActionMessages(Player player) {
        for (Map.Entry<UUID, ActionData> entry : actionMap.entrySet()) {
            if (entry.getValue().getPlayerId().equals(player.getUniqueId())) {
                actionMap.remove(entry.getKey());
            }
        }
    }

    /* Listeners */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeActionMessages(event.getPlayer());
    }

    @EventHandler
    public void onChat(PlayerCommandPreprocessEvent event) {
        // The command entered
        String command = event.getMessage().split(" ")[0].substring(1);
        UUID id;
        try {
            id = UUID.fromString(command);
            if (!actionMap.containsKey(id)) {
                event.setCancelled(true);
            }
        } catch (IllegalArgumentException expected) {
            // They didn't enter a valid UUID
            return;
        }

        // The data associated with the UUID they entered
        ActionData data = actionMap.get(id);

        if (data == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        if (player.getUniqueId().equals(data.getPlayerId())) {
            // They entered a command linked with their data
            data.getAction().run(player);

            // This action should expire after being used once
            if (data.shouldExpire()) {
                actionMap.remove(id);
            }
        }
    }

    /**
     * Functional interface for
     * executing actions with
     * a player
     */
    @FunctionalInterface
    public interface PlayerAction {

        /**
         * Executes the desired action
         * on a player based upon implementation
         *
         * @param player The player to run the action for
         */
        void run(Player player);
    }

    /**
     * Class holding information about an action
     */
    private class ActionData {

        /**
         * The player to execute the action on
         */
        private final UUID playerId;

        /**
         * The action to execute
         */
        private final PlayerAction action;

        /**
         * Whether the action should expire
         */
        private final boolean expire;

        /**
         * ActionData constructor
         *
         * @param playerId The {@link UUID} of the player to execute the action on
         * @param action   The {@link PlayerAction} to execute
         */
        private ActionData(UUID playerId, PlayerAction action, boolean expire) {
            this.playerId = playerId;
            this.action = action;
            this.expire = expire;
        }

        /**
         * Gets the UUID of the player to execute the action upon
         *
         * @return The stored {@link UUID}
         */
        private UUID getPlayerId() {
            return playerId;
        }

        /**
         * Gets the action associated with this ActionData object
         *
         * @return The stored {@link PlayerAction}
         */
        private PlayerAction getAction() {
            return action;
        }

        /**
         * Whether the action should expire after being used once
         *
         * @return Whether the action should expire
         */
        private boolean shouldExpire() {
            return expire;
        }
    }
}