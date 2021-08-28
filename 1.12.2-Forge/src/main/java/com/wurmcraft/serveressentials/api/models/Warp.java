package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.models.local.Home;

public class Warp extends Home {

    public Warp(double x, double y, double z, int dim, double pitch, double yaw, String name) {
        super(x, y, z, dim, pitch, yaw, name);
    }
}
