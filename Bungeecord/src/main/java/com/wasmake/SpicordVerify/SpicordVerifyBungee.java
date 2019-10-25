package com.wasmake.SpicordVerify;

import com.wasmake.SpicordVerify.utils.TickEvent;
import com.wasmake.SpicordVerify.utils.UpdateCause;
import com.wasmake.SpicordVerify.utils.config.UniversalConfiguration;
import eu.mcdb.spicord.Spicord;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class SpicordVerifyBungee extends Plugin {
    int minute = 240;
    int halfhour = 7200;
    int second = 4;

    public static UniversalConfiguration config;

    @Override
    public void onEnable(){
        getTicker();
        initConfig();
        Spicord.getInstance().getAddonManager().registerAddon(new BungeeAddon());
    }

    @Override
    public void onDisable(){

    }

    public void getTicker(){
        getProxy().getScheduler().schedule(this, new Runnable() {

            @Override
            public void run() {
                second--;
                minute--;
                halfhour--;
                if(second == 0){
                    second = 20;
                    getProxy().getPluginManager().callEvent(new TickEvent(UpdateCause.SECOND));
                }
                if (minute == 0) {
                    minute = 1200;
                    getProxy().getPluginManager().callEvent(new TickEvent(UpdateCause.MINUTE));
                }
                if (halfhour == 0) {
                    halfhour = 36000;
                    getProxy().getPluginManager().callEvent(new TickEvent(UpdateCause.HALFHOUR));
                }

                getProxy().getPluginManager().callEvent(new TickEvent(UpdateCause.TICK));
            }

        }, 0, TimeUnit.MILLISECONDS);
    }

    public void initConfig() {
        File file = new File(getDataFolder(), "config.yml");
        config = new UniversalConfiguration(file);

        config.addDefault("bot.guildid", "YOUR-GUILD-ID", "Place the guild ID of your discord server.");

        config.addDefault("prefix", "&7[&6Spicord&7]");
        config.addDefault("msg.request", "&7You got a verify-inquiry. Please &aaccept or &cdecline &7it. You have 30 seconds to accept it. Discord User> &b%usrnm%");
        config.addDefault("msg.button.accept", "&a&lAccept");
        config.addDefault("msg.button.decline", "&7&lDecline");
        config.addDefault("msg.success", "&aGreat! your Minecraft account is now linked to our discord server!");
        config.addDefault("msg.error", "&cHuh... the request has expired, please try again!");

        config.options().copyDefaults(true);
        config.getEConfig().setNewLinePerKey(true);
        config.save();
    }

}
