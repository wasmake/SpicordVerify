package com.wasmake.SpicordVerify.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {
    String username, discordID;
    int time;

    public boolean update(){
        time--;
        if(time == 0) return true;
        return false;
    }

}
