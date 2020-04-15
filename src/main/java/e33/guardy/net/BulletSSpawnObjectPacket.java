package e33.guardy.net;

import e33.guardy.entity.BulletEntity;
import e33.guardy.init.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.UUID;

public class BulletSSpawnObjectPacket extends SSpawnObjectPacket implements IPacket<IClientPlayNetHandler> {
    public final static Logger LOGGER = LogManager.getLogger();

    public BulletSSpawnObjectPacket(int p_i50777_1_, UUID p_i50777_2_, double p_i50777_3_, double p_i50777_5_, double p_i50777_7_, float p_i50777_9_, float p_i50777_10_, EntityType<?> p_i50777_11_, int p_i50777_12_, Vec3d p_i50777_13_) {
        super(p_i50777_1_, p_i50777_2_, p_i50777_3_, p_i50777_5_, p_i50777_7_, p_i50777_9_, p_i50777_10_, p_i50777_11_, p_i50777_12_, p_i50777_13_);
    }

    @Override
    public void processPacket(@Nonnull IClientPlayNetHandler p_148833_1_) {
        this.processPacket();
    }

    public void processPacket(IServerPlayNetHandler p_148833_1_) {
        this.processPacket();
    }

    public void processPacket() {
        Minecraft mine = Minecraft.getInstance();
        BulletSSpawnObjectPacket packetIn = this;
//        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, mine); TODO maybe needed
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        EntityType<?> entitytype = packetIn.getType();
        Entity entity;

        LOGGER.info(entitytype);
        if (entitytype == EntityRegistry.BULLET) {
            entity = new BulletEntity(mine.world, null, d0, d1, d2, null);
        } else {
//            handler.handleSpawnObject(this);
            return;
        }

        int i = packetIn.getEntityID();
        entity.func_213312_b(d0, d1, d2);
        entity.rotationPitch = (float) (packetIn.getPitch() * 360) / 256.0F;
        entity.rotationYaw = (float) (packetIn.getYaw() * 360) / 256.0F;
        entity.setEntityId(i);
        entity.setUniqueId(packetIn.getUniqueId());
        mine.world.addEntity(i, entity);
    }
}
