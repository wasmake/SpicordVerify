package com.wasmake.SpicordVerify.utils;

import eu.mcdb.spicord.api.addon.SimpleAddon;
import eu.mcdb.spicord.bot.DiscordBot;
import eu.mcdb.spicord.bot.command.DiscordBotCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.HierarchyException;

import java.awt.*;

public abstract class SpicordAddon extends SimpleAddon {

    private static SpicordAddon instance;
    private DiscordBot bot;
    public String guildID;
    public static SpicordAddon getInstance() {
        return instance;
    }

    public SpicordAddon(String guildID) {
        super("SpicordVerify","verify","wasmake");
        instance = this;
        this.guildID = guildID;
    }

    @Override
    public void onLoad(DiscordBot bot) {
        this.bot = bot;
        bot.onCommand("verify", this::verify);
        bot.onCommand("v", this::verify);
    }

    @Override
    public void onReady(DiscordBot bot){
        Guild guild = bot.getJda().getGuildById(guildID);
        if(guild.getRolesByName("Verified", true).isEmpty()){
            guild.getController().createRole().setName("Verified").setColor(Color.YELLOW);
            System.out.println("Bot created verified role as it was missing on the server.");
        } else {
            System.out.println("Found verified role.");
        }

    }

    public void addRole(String userID){
        Role role = bot.getJda().getRolesByName("Verified", true).get(0);
        User user = bot.getJda().getUserById(userID);
        if(user != null && role != null){
            Guild guild = bot.getJda().getGuildById(guildID);
            Member member = guild.getMember(bot.getJda().getUserById(userID));
            if(member != null){
                try {
                    guild.getController().addSingleRoleToMember(member, role).queue();
                    user.openPrivateChannel().queue( privateChannel -> {
                        privateChannel.sendMessage("You are now verified and your mc account was linked.").queue();
                    });
                } catch (HierarchyException e){
                    System.out.println("Looks like the bot doesnt have permission to assign roles.");
                }
            }
        }
    }

    public void requestDeclined(String userID){
        User user = bot.getJda().getUserById(userID);
        if(user != null){
            user.openPrivateChannel().queue( privateChannel -> {
                privateChannel.sendMessage("The request has expired or declined.").queue();
            });
        }
    }

    public abstract void verify(DiscordBotCommand command);
}
