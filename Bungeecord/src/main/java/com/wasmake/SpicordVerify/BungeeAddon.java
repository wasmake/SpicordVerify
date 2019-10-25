package com.wasmake.SpicordVerify;

import com.wasmake.SpicordVerify.utils.BungeeClickActions;
import com.wasmake.SpicordVerify.utils.SpicordAddon;
import eu.mcdb.spicord.bot.command.DiscordBotCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeAddon extends SpicordAddon {

    public BungeeAddon(){
        super(SpicordVerifyBungee.config.getString("bot.guildid"));
    }

    @Override
    public void verify(DiscordBotCommand command) {
        User user = command.getMessage().getAuthor();
        String msg = command.getMessage().getContentRaw();
        String mcuser = msg.replaceFirst("-verify", "").replaceFirst("-v", "");
        Guild guild = user.getMutualGuilds().get(0);
        if(guild.getMember(user).getRoles().contains(guild.getRolesByName("Verified", true).get(0))){
            command.getMessage().getChannel().sendMessage("You are already verified").queue();
            return;
        }
        if(msg.length() == 2){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(mcuser);
            if(p != null && p.isConnected()){
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

    public void sendAction(ProxiedPlayer p, final User user){
        p.sendMessage(color(SpicordVerifyBungee.config.getString("msg.request").replaceFirst("%usrnm%", user.getName())));
        TextComponent space = new TextComponent("  •  ");
        space.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        TextComponent inner = new TextComponent("         ");
        TextComponent a = BungeeClickActions.getInstance().sendActionMessage(p, SpicordVerifyBungee.config.getString("msg.button.accept"), true, player -> {
            //Accept button
            this.addRole(user.getId());
        });
        TextComponent b = BungeeClickActions.getInstance().sendActionMessage(p, SpicordVerifyBungee.config.getString("msg.button.decline"), true, player -> {
            //Deny button
            this.requestDeclined(user.getId());
        });

        p.sendMessage(inner, a, space, b);
    }

    public String color(String msg){
        return ChatColor.translateAlternateColorCodes('&', SpicordVerifyBungee.config.getString("prefix") + " " + msg);
    }

}
