package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.event.InventoryTrackingEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;

public class PlayerInventory extends InventoryBasic {

    public final EntityPlayerMP viewer;
    public final EntityPlayerMP owner;
    public boolean allowUpdate;
    public boolean echest;
    private boolean canModify;

    public PlayerInventory(EntityPlayerMP owner, EntityPlayerMP viewer) {
        super(owner.getName() + "'s inventory", false, owner.inventory.mainInventory.size());
        this.owner = owner;
        this.viewer = viewer;
        canModify = RankUtils.hasPermission(SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, viewer.getGameProfile().getId().toString(), new Account()), "command.invseee.modify");
    }

    public PlayerInventory(EntityPlayerMP owner, EntityPlayerMP viewer, boolean echest) {
        super(owner.getName() + "'s inventory", false, owner.inventory.mainInventory.size());
        this.owner = owner;
        this.viewer = viewer;
        this.echest = echest;
        if (echest) {
            canModify = RankUtils.hasPermission(SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, viewer.getGameProfile().getId().toString(), new Account()), "command.echest.modify");
        } else {
            canModify = RankUtils.hasPermission(SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, viewer.getGameProfile().getId().toString(), new Account()), "command.invseee.modify");
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (!echest) {
            allowUpdate = false;
            for (int id = 0; id < owner.inventory.mainInventory.size(); ++id) {
                setInventorySlotContents(id, owner.inventory.mainInventory.get(id));
            }
            allowUpdate = true;
            InventoryTrackingEvents.openPlayerInventory.put(player, this);
            super.openInventory(player);
        } else {
            allowUpdate = false;
            for (int id = 0; id < owner.getInventoryEnderChest().getSizeInventory(); ++id) {
                setInventorySlotContents(id, owner.getInventoryEnderChest().getStackInSlot(id));
            }
            allowUpdate = true;
            InventoryTrackingEvents.openPlayerInventory.put(player, this);
            super.openInventory(player);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (!echest) {
            if (allowUpdate) {
                for (int id = 0; id < owner.inventory.mainInventory.size(); ++id) {
                    owner.inventory.mainInventory.set(id, getStackInSlot(id));
                }
            }
            InventoryTrackingEvents.openPlayerInventory.remove(player);
            markDirty();
            super.closeInventory(player);
        } else {
            if (allowUpdate) {
                for (int id = 0; id < owner.getInventoryEnderChest().getSizeInventory(); ++id) {
                    owner.getInventoryEnderChest().setInventorySlotContents(id, getStackInSlot(id));
                }
            }
            InventoryTrackingEvents.openPlayerInventory.remove(player);
            markDirty();
            super.closeInventory(player);
        }
    }

    @Override
    public void markDirty() {
        if (canModify) {
            if (!echest) {
                super.markDirty();
                if (allowUpdate) {
                    for (int id = 0; id < owner.inventory.mainInventory.size(); ++id) {
                        owner.inventory.mainInventory.set(id, getStackInSlot(id));
                    }
                }
            } else {
                super.markDirty();
                if (allowUpdate) {
                    for (int id = 0; id < owner.getInventoryEnderChest().getSizeInventory(); ++id) {
                        owner.getInventoryEnderChest()
                                .setInventorySlotContents(id, getStackInSlot(id));
                    }
                }
            }
        }
    }

    public void update() {
        if (!echest) {
            allowUpdate = false;
            for (int id = 0; id < owner.inventory.mainInventory.size(); ++id) {
                setInventorySlotContents(id, owner.inventory.mainInventory.get(id));
            }
            allowUpdate = true;
            markDirty();
        } else {
            allowUpdate = false;
            for (int id = 0; id < owner.inventory.mainInventory.size(); ++id) {
                setInventorySlotContents(id, owner.getInventoryEnderChest().getStackInSlot(id));
            }
            allowUpdate = true;
            markDirty();
        }
    }
}