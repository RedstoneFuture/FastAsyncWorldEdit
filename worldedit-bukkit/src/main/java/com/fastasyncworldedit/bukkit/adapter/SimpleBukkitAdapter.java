package com.fastasyncworldedit.bukkit.adapter;

import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleBukkitAdapter extends CachedBukkitAdapter {

    private BlockData[][] blockDataCache;

    private boolean init() {
        if (blockDataCache != null) {
            return false;
        }
        this.blockDataCache = new BlockData[BlockTypes.size()][];
        blockDataCache[0] = new BlockData[]{Material.AIR.createBlockData()};
        return true;
    }

    @Override
    protected int[] getIbdToOrdinal() {
        return new int[Character.MAX_VALUE + 1];
    }

    @Override
    protected int[] getOrdinalToIbdID() {
        return new int[Character.MAX_VALUE + 1];
    }

    /**
     * Create a Bukkit BlockData from a WorldEdit BlockStateHolder
     *
     * @param block The WorldEdit BlockStateHolder
     * @return The Bukkit BlockData
     */
    @Override
    public <B extends BlockStateHolder<B>> BlockData adapt(B block) {
        try {
            checkNotNull(block);
            int typeId = block.getInternalBlockTypeId();
            BlockData[] dataCache = blockDataCache[typeId];
            if (dataCache == null) {
                BlockType type = BlockTypes.get(typeId);
                blockDataCache[typeId] = dataCache = new BlockData[type.getMaxStateId() + 1];
            }
            int propId = block.getInternalPropertiesId();
            BlockData blockData = dataCache[propId];
            if (blockData == null) {
                dataCache[propId] = blockData = Bukkit.createBlockData(block.getAsString());
            }
            return blockData;
        } catch (NullPointerException e) {
            if (init()) {
                return adapt(block);
            }
            throw e;
        }
    }

}
