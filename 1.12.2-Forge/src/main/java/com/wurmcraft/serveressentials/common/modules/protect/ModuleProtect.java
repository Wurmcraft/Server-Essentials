package com.wurmcraft.serveressentials.common.modules.protect;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.protect.event.ProtectionEvents;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim.ClaimType;
import com.wurmcraft.serveressentials.common.modules.protect.models.Position;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionClaim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionPos;
import com.wurmcraft.serveressentials.common.modules.protect.models.TrustInfo;
import com.wurmcraft.serveressentials.common.modules.protect.utils.RegionHelper;
import java.util.HashMap;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Protect")
public class ModuleProtect {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new ProtectionEvents());
    RegionClaim region = new RegionClaim("",new RegionPos(0,0,0,0),new Claim[] {new Claim("148cf139-dd14-4bf4-97a2-08305dfef0a9",new TrustInfo[]{},new Position(-16,0,-16),new Position(16,256,16),new HashMap<>(),
        ClaimType.BASIC,new HashMap<>())});
    SECore.dataLoader.register(DataType.CLAIM, RegionHelper.convert(region.regionPos),region);
  }

  public void reload() {
  }
}
