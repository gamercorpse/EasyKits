package com.gamercorpse.easykits.models;

import java.util.UUID;

public class KitCooldown {

    private final UUID uuid;
    private final String kitId;
    private long expires;

    public KitCooldown(UUID uuid,
                       String kitId,
                       long expires) {

        this.uuid = uuid;
        this.kitId = kitId;
        this.expires = expires;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getKitId() {
        return kitId;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }
}