package advancedsystemsmanager.helpers;

import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.*;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class RemapHelper
{
    private static Map<String, Remap> remapRegistry = new HashMap<String, Remap>();
    private static Map<String,Class<?>> teMappings = ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_" + "145855_i", "nameToClassMap");

    private static final String NBT_PROTOCOL_VERSION = "ProtocolVersion";
    private static final String MANAGER_TILE_ENTITY_TAG = "TileEntityMachineManagerName";
    private static final String MANAGER_NAME_TAG = "BlockMachineManagerName";
    private static final String CABLE_NAME_TAG = "BlockCableName";
    private static final String CABLE_RELAY_TILE_ENTITY_TAG = "TileEntityCableRelayName";
    private static final String CABLE_RELAY_NAME_TAG = "BlockCableRelayName";
    private static final String CABLE_OUTPUT_TILE_ENTITY_TAG = "TileEntityCableOutputName";
    private static final String CABLE_OUTPUT_NAME_TAG = "BlockCableOutputName";
    private static final String CABLE_INPUT_TILE_ENTITY_TAG = "TileEntityCableInputName";
    private static final String CABLE_INPUT_NAME_TAG = "BlockCableInputName";
    private static final String CABLE_CREATIVE_TILE_ENTITY_TAG = "TileEntityCableCreativeName";
    private static final String CABLE_CREATIVE_NAME_TAG = "BlockCableCreativeName";
    private static final String CABLE_INTAKE_TILE_ENTITY_TAG = "TileEntityCableIntakeName";
    private static final String CABLE_INTAKE_NAME_TAG = "BlockCableIntakeName";
    private static final String CABLE_BUD_TILE_ENTITY_TAG = "TileEntityCableBUDName";
    private static final String CABLE_BUD_NAME_TAG = "BlockCableBUDName";
    private static final String CABLE_BREAKER_TILE_ENTITY_TAG = "TileEntityCableBreakerName";
    private static final String CABLE_BREAKER_NAME_TAG = "BlockCableBreakerName";
    private static final String CABLE_CLUSTER_TILE_ENTITY_TAG = "TileEntityCableClusterName";
    private static final String CABLE_CLUSTER_NAME_TAG = "BlockCableClusterName";
    private static final String CABLE_CAMOUFLAGE_TILE_ENTITY_TAG = "TileEntityCableCamouflageName";
    private static final String CABLE_CAMOUFLAGE_NAME_TAG = "BlockCableCamouflageName";
    private static final String CABLE_SIGN_TILE_ENTITY_TAG = "TileEntityCableSignName";
    private static final String CABLE_SIGN_NAME_TAG = "BlockCableSignName";
    private static final String DRIVE = "duplicator";
    private static final String LABELER = "labeler";
    private static final String CABLE_RF = "cable_rf";
    private static final String CABLE_AE = "cable_ae";

    static
    {
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + MANAGER_NAME_TAG, MANAGER_TILE_ENTITY_TAG, BlockRegistry.blockManager, TileEntityManager.class));
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_NAME_TAG, BlockRegistry.cable));
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_RELAY_NAME_TAG, CABLE_RELAY_TILE_ENTITY_TAG, BlockRegistry.cableRelay, TileEntityRelay.class));
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_OUTPUT_NAME_TAG, CABLE_OUTPUT_TILE_ENTITY_TAG, BlockRegistry.cableOutput, TileEntityEmitter.class));
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_INPUT_NAME_TAG, CABLE_INPUT_TILE_ENTITY_TAG, BlockRegistry.cableInput, TileEntityReceiver.class));
//        registerMapping(CABLE_CREATIVE_NAME_TAG, BlockRegistry.cableCreative);
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_INTAKE_NAME_TAG, CABLE_INTAKE_TILE_ENTITY_TAG, BlockRegistry.cableValve, TileEntityValve.class));
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_BUD_NAME_TAG, CABLE_BUD_TILE_ENTITY_TAG, BlockRegistry.cableBUD, TileEntityBUD.class));
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_BREAKER_NAME_TAG, CABLE_BREAKER_TILE_ENTITY_TAG, BlockRegistry.cableBlockGate, TileEntityBlockGate.class));
//        registerMapping(CABLE_CLUSTER_NAME_TAG, BlockRegistry.cableCluster);
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_CAMOUFLAGE_NAME_TAG, CABLE_CAMOUFLAGE_TILE_ENTITY_TAG, BlockRegistry.cableCamouflage, TileEntityCamouflage.class));
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_SIGN_NAME_TAG, CABLE_SIGN_TILE_ENTITY_TAG, BlockRegistry.cableSign, TileEntitySignUpdater.class));
        if (BlockRegistry.cableRFNode != null)
        {
            registerMapping(new Remap(Mods.STEVESADDONS + ':' + CABLE_RF, CABLE_RF, BlockRegistry.cableRFNode, TileEntityRFNode.class));
        }
        if (BlockRegistry.cableAENode != null)
        {
            registerMapping(new Remap(Mods.STEVESADDONS + ':' + CABLE_AE, CABLE_AE, BlockRegistry.cableAENode, TileEntityAENode.class));
        }
        registerMapping(new Remap(Mods.STEVESADDONS + ':' + DRIVE, ItemRegistry.duplicator));
        registerMapping(new Remap(Mods.STEVESADDONS + ':' + LABELER, ItemRegistry.labeler));
    }

    public static void registerMapping(Remap thing)
    {
        remapRegistry.put(thing.getName(), thing);
    }

    public static void handleRemap(FMLMissingMappingsEvent event)
    {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll())
        {
            Remap remap = remapRegistry.get(mapping.name);
            if (remap != null)
            {
                remap.remap(mapping);
            }
        }
    }

    private static class Remap
    {
        private String oldName;
        private String oldTEName;
        private Block newBlock;
        private Item newItem;
        private Class newTileEntity;

        private Remap(String oldName, Block newBlock)
        {
            this(oldName, null, newBlock, Item.getItemFromBlock(newBlock), null);
        }

        private Remap(String oldName, Block newBlock, Class newTileEntity)
        {
            this(oldName, oldName, newBlock, Item.getItemFromBlock(newBlock), newTileEntity);
        }

        private Remap(String oldName, String oldTEName, Block newBlock, Class newTileEntity)
        {
            this(oldName, oldTEName, newBlock, Item.getItemFromBlock(newBlock), newTileEntity);
        }

        private Remap(String oldName, Item newItem)
        {
            this(oldName, null, null, newItem, null);
        }

        private Remap(String oldName, String oldTEName, Block newBlock, Item newItem, Class newTileEntity)
        {
            this.oldName = oldName;
            this.oldTEName = oldTEName;
            this.newBlock = newBlock;
            this.newItem = newItem;
            this.newTileEntity = newTileEntity;
        }

        public String getName()
        {
            return oldName;
        }

        public void remap(FMLMissingMappingsEvent.MissingMapping mapping)
        {
            if (newBlock != null)
            {
                if (mapping.type == GameRegistry.Type.BLOCK)
                {
                    mapping.remap(newBlock);
                    if (newTileEntity != null)
                    {
                        teMappings.put(oldTEName, newTileEntity);
                    }
                } else
                {
                    mapping.remap(newItem);
                }
            } else if (newItem != null && mapping.type == GameRegistry.Type.ITEM)
            {
                mapping.remap(newItem);
            } else
            {
                mapping.ignore();
            }
        }
    }
}
