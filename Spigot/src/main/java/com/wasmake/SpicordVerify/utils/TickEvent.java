package com.wasmake.SpicordVerify.utils;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private UpdateCause cause;

    public TickEvent (UpdateCause cause){
        this.cause = cause;
    }

    public UpdateCause getCause() {
        return cause;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

