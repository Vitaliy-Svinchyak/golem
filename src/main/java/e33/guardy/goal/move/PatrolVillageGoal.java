package e33.guardy.goal.move;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.debug.TimeMeter;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.util.ToStringHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class PatrolVillageGoal extends Goal {
    final static Logger LOGGER = LogManager.getLogger();

    private World world;
    private ShootyEntity shooty;
    public List<ChunkPos> villageChunks = null;

    public PatrolVillageGoal(ShootyEntity creatureIn) {
        this.shooty = creatureIn;
        this.world = this.shooty.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        return this.world.isAreaLoaded(this.shooty.getPosition(), 100); // TODO
    }

    public boolean shouldContinueExecuting() {
        return !this.shooty.getNavigator().noPath(); // TODO
    }

    public void startExecuting() {
        if (this.villageChunks == null) {
            TimeMeter.moduleStart("PatrolVillageGoal");
            BlockPos shootyPosition = this.shooty.getPosition();
            ChunkPos startChunk = world.getChunk(shootyPosition).getPos();
            this.villageChunks = this.getVillageChunks(startChunk);
            TimeMeter.moduleEnd("PatrolVillageGoal");
        }
    }

    protected List<ChunkPos> getVillageChunks(ChunkPos start) {
        Map<String, Boolean> usedPoses = Maps.newHashMap();
        List<ChunkPos> villageChunks = Lists.newArrayList(start);
        List<ChunkPos> tempVillageChunks = Lists.newArrayList(start);
        int iteration = 0;

        while (tempVillageChunks.size() > 0) {

            List<ChunkPos> tempVillageChunksIteration = Lists.newArrayList();

            for (ChunkPos chunk : tempVillageChunks) {
                int chunkX = chunk.getXStart();
                int chunkZ = chunk.getZStart();
                List<Chunk> chunkVariants = Lists.newArrayList(
                        this.world.getChunkAt(new BlockPos(chunkX - 15, 5, chunkZ)),
                        this.world.getChunkAt(new BlockPos(chunkX + 15, 5, chunkZ)),

                        this.world.getChunkAt(new BlockPos(chunkX, 5, chunkZ - 15)),
                        this.world.getChunkAt(new BlockPos(chunkX, 5, chunkZ + 15))
                );

                for (Chunk chunkVar : chunkVariants) {
                    ChunkPos pos = chunkVar.getPos();
                    if (usedPoses.get(ToStringHelper.toString(pos)) == null) {
                        TimeMeter.start("PatrolVillageGoal", "getChunk");
                        IChunk chunkObj = this.world.getChunk(pos.getXStart(), pos.getZStart(), ChunkStatus.STRUCTURE_STARTS, true);
                        TimeMeter.end("PatrolVillageGoal", "getChunk");

                        if (chunkObj != null && this.isVillageChunk(chunkObj)) {
                            tempVillageChunksIteration.add(pos);
                        }
                    }

                    usedPoses.put(ToStringHelper.toString(pos), true);
                }
            }


            tempVillageChunks = tempVillageChunksIteration;
            villageChunks.addAll(tempVillageChunksIteration);
            iteration++;

            if (iteration >= 3) {
                LOGGER.error("Too many iterations!");
                break;
            }

        }

        return villageChunks;
    }

    private boolean isVillageChunk(IChunk chunk) {
        TimeMeter.start("PatrolVillageGoal", "isVillageChunk");
        for (StructureStart str : chunk.getStructureStarts().values()) {
            if (str.getStructure() instanceof VillageStructure || str.getStructure() instanceof MineshaftStructure) {
                TimeMeter.end("PatrolVillageGoal", "isVillageChunk");
                return true;
            }
        }
        TimeMeter.end("PatrolVillageGoal", "isVillageChunk");
        return false;
    }
}
