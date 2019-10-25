package com.wasmake.SpicordVerify.utils;

import net.md_5.bungee.api.plugin.Event;

public class TickEvent extends Event {

    private UpdateCause cause;

    public TickEvent (UpdateCause cause){
        this.cause = cause;
    }

    public UpdateCause getCause() {
        return cause;
    }

}

