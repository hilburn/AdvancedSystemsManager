package advancedsystemsmanager.helpers;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.blocks.BlockTileElement;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.*;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RemapHelper
{
    private static Map<String, String> tileFactoryRemap = new HashMap<String, String>();
    private static Map<String, Remap> remapRegistry = new HashMap<String, Remap>();
    private static Map<String,Class<?>> teMappings = ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_" + "145855_i", "nameToClassMap");

    //##############################################
    //               DEPRECATED
    //##############################################
    public static final String OLD_PREFIX = "system_";
    public static final String OLD_MANAGER = OLD_PREFIX + "manager";
    public static final String OLD_CABLE = OLD_PREFIX + "cable";
    public static final String OLD_CABLE_RELAY = OLD_CABLE + "_relay";
    public static final String OLD_CABLE_OUTPUT = OLD_CABLE + "_output";
    public static final String OLD_CABLE_INPUT = OLD_CABLE + "_input";
    public static final String OLD_CABLE_CREATIVE = OLD_CABLE + "_creative";
    public static final String OLD_CABLE_VALVE = OLD_CABLE + "_valve";
    public static final String OLD_CABLE_VOID = OLD_CABLE + "_void";
    public static final String OLD_CABLE_BUD = OLD_CABLE + "_bud";
    public static final String OLD_CABLE_BLOCK_GATE = OLD_CABLE + "_block_gate";
    public static final String OLD_CABLE_FLUID_GATE = OLD_CABLE + "_fluid_gate";
    public static final String OLD_CABLE_CLUSTER = OLD_CABLE + "_cluster";
    public static final String OLD_CABLE_CAMO = OLD_CABLE + "_camo";
    public static final String OLD_CABLE_SIGN = OLD_CABLE + "_sign";
    public static final String OLD_CABLE_QUANTUM = OLD_CABLE + "_quantum";

    public static final String OLD_CABLE_RF = OLD_CABLE + "_rf";
    public static final String OLD_CABLE_AE = OLD_CABLE + "_ae";

    public static final String OLD_DUPLICATOR = OLD_PREFIX + "duplicator";
    public static final String OLD_LABELER = OLD_PREFIX + "labeler";
    public static final String OLD_REMOTE_ACCESS = OLD_PREFIX + "remote_access";

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

    public static void registerMappings()
    {
        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + MANAGER_NAME_TAG, MANAGER_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.MANAGER));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_NAME_TAG, BlockRegistry.cableElements[0], Names.CABLE_BLOCK));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_RELAY_NAME_TAG, CABLE_RELAY_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_RELAY));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_OUTPUT_NAME_TAG, CABLE_OUTPUT_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_OUTPUT));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_INPUT_NAME_TAG, CABLE_INPUT_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_INPUT));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_CREATIVE_NAME_TAG, CABLE_CREATIVE_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_CREATIVE));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_INTAKE_NAME_TAG, CABLE_INTAKE_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_VALVE));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_BUD_NAME_TAG, CABLE_BUD_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_BUD));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_BREAKER_NAME_TAG, CABLE_BREAKER_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_BLOCK_GATE));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_CLUSTER_NAME_TAG, CABLE_CLUSTER_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_CLUSTER));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_CAMOUFLAGE_NAME_TAG, CABLE_CAMOUFLAGE_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_CAMO));
//        registerMapping(new Remap(Mods.STEVESFACTORYMANAGER + ':' + CABLE_SIGN_NAME_TAG, CABLE_SIGN_TILE_ENTITY_TAG, BlockRegistry.cableElements[0], Names.CABLE_SIGN));
//        registerMapping(new Remap(Mods.STEVESADDONS + ':' + CABLE_RF, CABLE_RF, BlockRegistry.cableElements[0], Names.CABLE_RF));
//        registerMapping(new Remap(Mods.STEVESADDONS + ':' + CABLE_AE, CABLE_AE, BlockRegistry.cableElements[0], Names.CABLE_AE));
        registerMapping(new Remap(Mods.STEVESADDONS + ':' + DRIVE, ItemRegistry.duplicator));
        registerMapping(new Remap(Mods.STEVESADDONS + ':' + LABELER, ItemRegistry.labeler));
        registerMapping(new Remap(Reference.ID + ':' + OLD_MANAGER, OLD_MANAGER, BlockRegistry.cableElements[0], Names.MANAGER));
//        registerMapping(Reference.ID + ':' + OLD_CABLE, null, BlockRegistry.cableElements[0], Names.CABLE_BLOCK);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_RELAY, OLD_CABLE_RELAY, BlockRegistry.cableElements[0], Names.CABLE_RELAY);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_OUTPUT, OLD_CABLE_OUTPUT, BlockRegistry.cableElements[0], Names.CABLE_OUTPUT);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_INPUT, OLD_CABLE_INPUT, BlockRegistry.cableElements[0], Names.CABLE_INPUT);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_CREATIVE, OLD_CABLE_CREATIVE, BlockRegistry.cableElements[0], Names.CABLE_CREATIVE);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_VALVE, OLD_CABLE_VALVE, BlockRegistry.cableElements[0], Names.CABLE_VALVE);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_BUD, OLD_CABLE_BUD, BlockRegistry.cableElements[0], Names.CABLE_BUD);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_BLOCK_GATE, OLD_CABLE_BLOCK_GATE, BlockRegistry.cableElements[0], Names.CABLE_BLOCK_GATE);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_CLUSTER, OLD_CABLE_CLUSTER, BlockRegistry.cableElements[0], Names.CABLE_CLUSTER);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_CAMO, OLD_CABLE_CAMO, BlockRegistry.cableElements[0], Names.CABLE_CAMO);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_SIGN, OLD_CABLE_SIGN, BlockRegistry.cableElements[0], Names.CABLE_SIGN);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_RF, OLD_CABLE_RF, BlockRegistry.cableElements[0], Names.CABLE_RF);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_AE, OLD_CABLE_AE, BlockRegistry.cableElements[0], Names.CABLE_AE);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_VOID, OLD_CABLE_VOID, BlockRegistry.cableElements[0], Names.CABLE_VOID);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_FLUID_GATE, OLD_CABLE_FLUID_GATE, BlockRegistry.cableElements[0], Names.CABLE_FLUID_GATE);
//        registerMapping(Reference.ID + ':' + OLD_CABLE_QUANTUM, OLD_CABLE_QUANTUM, BlockRegistry.cableElements[0], Names.CABLE_QUANTUM);
        registerMapping(new Remap(Reference.ID + ':' + OLD_DUPLICATOR, ItemRegistry.duplicator));
        registerMapping(new Remap(Reference.ID + ':' + OLD_LABELER, ItemRegistry.labeler));
        registerMapping(new Remap(Reference.ID + ':' + OLD_REMOTE_ACCESS, ItemRegistry.remoteAccessor));
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

    public static String getNewTile(String oldName)
    {
        return tileFactoryRemap.get(oldName);
    }

    private static class Remap
    {
        private String oldName;
        private String oldTEName;
        private Block newBlock;
        private Item newItem;
        private String newTile;

        private Remap(String oldName, Block newBlock, String newTileEntity)
        {
            this(oldName, oldName, newBlock, null, newTileEntity);
        }

        private Remap(String oldName, String oldTEName, Block newBlock, String newTileEntity)
        {
            this(oldName, oldTEName, newBlock, null, newTileEntity);
        }

        private Remap(String oldName, Item newItem)
        {
            this(oldName, null, null, newItem, null);
        }

        private Remap(String oldName, String oldTEName, Block newBlock, Item newItem, String newTile)
        {
            this.oldName = oldName;
            this.oldTEName = oldTEName;
            this.newBlock = newBlock;
            this.newItem = newItem;
            this.newTile = newTile;
            tileFactoryRemap.put(oldTEName, newTile);
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
                    if (newTile != null)
                    {
                        AdvancedSystemsManager.log.info(String.format("remapping %s to %s", oldName, newTile));
                        teMappings.put(oldTEName, TileEntityRemapping.class);
                    }
                } else
                {
                    mapping.skipItemBlock();
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
