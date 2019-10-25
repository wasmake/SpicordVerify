package com.wasmake.SpicordVerify;

import com.wasmake.SpicordVerify.utils.ChatClickActions;
import com.wasmake.SpicordVerify.utils.SpicordAddon;
import eu.mcdb.spicord.bot.command.DiscordBotCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpigotAddon extends SpicordAddon {

    public SpigotAddon(){
        super(SpicordVerify.config.getString("bot.guildid"));
    }

    @Override
    public void verify(DiscordBotCommand command) {
        User user = command.getMessage().getAuthor();
        String msg = command.getMessage().getContentRaw();
        String mcuser = msg.replaceFirst("-verify", "").replaceFirst("-v", "");
        Guild guild = command.getGuild();
        if(guild.getMember(user).getRoles().contains(guild.getRolesByName("Verified", true).get(0))){
            command.getMessage().getChannel().sendMessage("You are already verified").queue();
            return;
        }
        if(msg.length() == 2){
            Player p = Bukkit.getPlayer(mcuser);
            if(p != null && p.isOnline()){
                //Player is valid and online
                sendAction(p, user);
            } else {
                //We had no luck while searching the player
                command.getMessage().getChannel().sendMessage("We had no luck finding that username online on the network.").queue();
            }

            return;
        }
        command.getMessage().getChannel().sendMessage("**Usage**: ***-verify <ign>***").queue();
    }

    public void sendAction(Player p, final User user){
        p.sendMessage(color(SpicordVerify.config.getString("msg.request").replaceFirst("%usrnm%", user.getName())));
        TextComponent space = new TextComponent("  •  ");
        space.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        TextComponent inner = new TextComponent("         ");
        TextComponent a = ChatClickActions.getInstance().sendActionMessage(p, SpicordVerify.config.getString("msg.button.accept"), true, player -> {
            //Accept button
            this.addRole(user.getId());
        });
        TextComponent b = ChatClickActions.getInstance().sendActionMessage(p, SpicordVerify.config.getString("msg.button.decline"), true, player -> {
            //Deny button
            this.requestDeclined(user.getId());
        });

        p.spigot().sendMessage(inner, a, space, b);
    }

    public String color(String msg){
        return ChatColor.translateAlternateColorCodes('&', SpicordVerify.config.getString("prefix") + " " + msg);
    }

}
