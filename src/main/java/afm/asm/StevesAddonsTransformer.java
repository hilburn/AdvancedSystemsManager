package afm.asm;

import advancedfactorymanager.AdvancedFactoryManager;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;

public class StevesAddonsTransformer implements IClassTransformer, Opcodes
{
    private enum TransformType
    {
        METHOD, FIELD, INNER_CLASS, MODIFY, MAKE_PUBLIC, DELETE
    }

    private enum Transformer
    {
        ACTIVATE_TRIGGER("activateTrigger", "(Lvswe/stevesfactory/components/FlowComponent;Ljava/util/EnumSet;)V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        return replace(list, "advancedfactorymanager/components/CommandExecutor", "advancedfactorymanager/components/CommandExecutorRF");
                    }
                },
        GET_GUI("getGui", "(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/client/gui/GuiScreen;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        return replace(list, "advancedfactorymanager/interfaces/GuiManager", "advancedfactorymanager/interfaces/GuiRFManager");
                    }
                },
        CREATE_TE("func_149915_a", "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        return replace(list, "advancedfactorymanager/blocks/TileEntityCluster", "advancedfactorymanager/blocks/TileEntityRFCluster");
                    }
                },
        MANAGER_INIT("<init>", "()V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (!(node instanceof LineNumberNode && ((LineNumberNode)node).line == 85) && node != list.getFirst())
                            node = node.getPrevious();
                        list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "addCopyButton", "(Lvswe/stevesfactory/blocks/TileEntityManager;)V", false));
                        return list;
                    }
                },
        ITEM_SETTING_LOAD("load", "(Lnet/minecraft/nbt/NBTTagCompound;)V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (node.getOpcode() != RETURN && node != list.getFirst()) node = node.getPrevious();
                        list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(node, new FieldInsnNode(GETFIELD, "advancedfactorymanager/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
                        list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "fixLoadingStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false));
                        list.insertBefore(node, new FieldInsnNode(PUTFIELD, "advancedfactorymanager/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
                        return list;
                    }
                },
        STRING_NULL_CHECK("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        LabelNode labelNode = null;
                        while (node != list.getFirst())
                        {
                            if (node instanceof JumpInsnNode)
                                labelNode = ((JumpInsnNode)node).label;
                            else if (node instanceof VarInsnNode && node.getOpcode() == ALOAD && ((VarInsnNode)node).var == 10)
                            {
                                list.insertBefore(node, new VarInsnNode(ALOAD, 10));
                                list.insertBefore(node, new JumpInsnNode(IFNULL, labelNode));
                                break;
                            }
                            node = node.getPrevious();
                        }
                        return list;
                    }
                },
        GET_DESCRIPTION("getDescription", "(Lvswe/stevesfactory/interfaces/GuiManager;)Ljava/lang/String;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node.getOpcode() == ASTORE)
                            {
                                list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                                list.insertBefore(node, new FieldInsnNode(GETFIELD, "advancedfactorymanager/blocks/ConnectionBlock", "tileEntity", "Lnet/minecraft/tileentity/TileEntity;"));
                                list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "fixToolTip", "(Ljava/lang/String;Lnet/minecraft/tileentity/TileEntity;)Ljava/lang/String;", false));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        ITEM_SEARCH("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode first = list.getFirst();
                        list.insertBefore(first, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(first, new VarInsnNode(ALOAD, 1));
                        list.insertBefore(first, new VarInsnNode(ILOAD, 2));
                        list.insertBefore(first, new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "updateItemSearch", "(Lvswe/stevesfactory/components/ComponentMenuItem;Ljava/lang/String;Z)Ljava/util/List;", false));
                        list.insertBefore(first, new InsnNode(ARETURN));
                        return list;
                    }
                },
        CONTAINER_SEARCH("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        LabelNode label = null;
                        while (node != null)
                        {
                            if (node instanceof JumpInsnNode)
                            {
                                label = ((JumpInsnNode)node).label;
                            }
                            if (node.getOpcode() == ALOAD && ((VarInsnNode)node).var == 8)
                            {
                                list.insertBefore(node, new VarInsnNode(ALOAD, 8));
                                list.insertBefore(node, new VarInsnNode(ALOAD, 1));
                                list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "containerAdvancedSearch", "(Lvswe/stevesfactory/blocks/ConnectionBlock;Ljava/lang/String;)Z", false));
                                list.insertBefore(node, new JumpInsnNode(IFNE, label));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        GET_PUBLIC_REGISTRATIONS("getRegistrations", "(Lvswe/stevesfactory/blocks/ClusterMethodRegistration;)Ljava/util/List;", TransformType.METHOD, TransformType.MAKE_PUBLIC),
        GET_REGISTRATIONS("getRegistrations", "(Lvswe/stevesfactory/blocks/ClusterMethodRegistration;)Ljava/util/List;", TransformType.METHOD, TransformType.DELETE),
        GET_RF_NODE("getTileEntity", "(Ljava/lang/Object;)Lstevesaddons/tileentities/TileEntityRFNode;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        InsnList result = new InsnList();
                        result.add(new VarInsnNode(ALOAD, 1));
                        result.add(new TypeInsnNode(CHECKCAST, "advancedfactorymanager/blocks/TileEntityCluster$Pair"));
                        result.add(new FieldInsnNode(GETFIELD, "advancedfactorymanager/blocks/TileEntityCluster$Pair", "te", "Lvswe/stevesfactory/blocks/TileEntityClusterElement;"));
                        result.add(new TypeInsnNode(CHECKCAST, "advancedfactorymanager/tileentities/TileEntityRFNode"));
                        result.add(new InsnNode(ARETURN));
                        return result;
                    }
                },
        PUBLIC_TE("te", "Lvswe/stevesfactory/blocks/TileEntityClusterElement;", TransformType.FIELD, TransformType.MAKE_PUBLIC),
        PUBLIC_PAIR("Pair"),
        REMOVE_COMPONENT("removeFlowComponent", "(I)V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        list.clear();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ILOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "removeFlowComponent", "(Lvswe/stevesfactory/blocks/TileEntityManager;I)V", false));
                        list.add(new InsnNode(RETURN));
                        return list;
                    }

                    @Override
                    protected void methodTransform(ClassNode node)
                    {
                        MethodNode methodNode = getMethod(node);
                        if (methodNode != null)
                        {
                            methodNode.instructions = modifyInstructions(methodNode.instructions);
                            methodNode.localVariables = null;
                            complete();
                        }
                    }
                },
        LOAD_DEFAULT("loadDefault", "()V")
                {
                    private Set<String> change = new HashSet<String>(Arrays.asList("largeOpenHitBox", "largeOpenHitBoxMenu", "autoBlacklist", "autoSide"));
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node instanceof FieldInsnNode && change.contains(((FieldInsnNode)node).name))
                            {
                                list.remove(node.getPrevious());
                                list.insertBefore(node, new InsnNode(ICONST_1));
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        READ_FROM_NBT("readFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;IZ)V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals(FMLForgePlugin.RUNTIME_DEOBF ? "func_74771_c" : "getByte"))
                            {
                                node = node.getPrevious();
                                list.remove(node.getNext());
                                list.insert(node, new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", FMLForgePlugin.RUNTIME_DEOBF? "func_74765_d":"getShort", "(Ljava/lang/String;)S", false));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        WRITE_TO_NBT("writeToNBT","(Lnet/minecraft/nbt/NBTTagCompound;Z)V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node.getOpcode() == I2B)
                            {
                                node = node.getPrevious();
                                list.remove(node.getNext());
                                list.insert(node, new InsnNode(I2S));
                            }
                            else if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals(FMLForgePlugin.RUNTIME_DEOBF?"func_74774_a":"setByte"))
                            {
                                node = node.getPrevious();
                                list.remove(node.getNext());
                                list.insert(node, new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", FMLForgePlugin.RUNTIME_DEOBF?"func_74777_a":"setShort", "(Ljava/lang/String;S)V", false));
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        BIT_HELPER_INIT("<clinit>","()V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node.getOpcode() == BIPUSH && ((IntInsnNode)node).operand == 39)
                            {
                                list.remove(node.getNext());
                                list.insert(node, new InsnNode(ICONST_4));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        IS_INSTANCE("isInstance", "(Lnet/minecraft/tileentity/TileEntity;)Z")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        list.clear();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, "advancedfactorymanager/blocks/ConnectionBlockType", "clazz", "Ljava/lang/Class;"));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "instanceOf", "(Ljava/lang/Class;Lnet/minecraft/tileentity/TileEntity;)Z", false));
                        list.add(new InsnNode(IRETURN));
                        return list;
                    }
                },
        IS_VISIBLE("isVisible", "()Z")
                {
                    @Override
                    public void transform(ClassNode node)
                    {
                        MethodNode isVisible = new MethodNode(ACC_PUBLIC, this.name, this.args, null, new String[0]);
                        isVisible.instructions.add(new VarInsnNode(ALOAD, 0));
                        isVisible.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "advancedfactorymanager/components/ComponentMenuInterval","getParent","()Lvswe/stevesfactory/components/FlowComponent;",false));
                        isVisible.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "advancedfactorymanager/components/FlowComponent", "getConnectionSet", "()Lvswe/stevesfactory/components/ConnectionSet;",false));
                        isVisible.instructions.add(new FieldInsnNode(GETSTATIC, "advancedfactorymanager/helpers/StevesEnum", "DELAYED", "Lvswe/stevesfactory/components/ConnectionSet;"));
                        LabelNode l1 = new LabelNode(new Label());
                        isVisible.instructions.add(new JumpInsnNode(IF_ACMPEQ, l1));
                        isVisible.instructions.add(new InsnNode(ICONST_1));
                        isVisible.instructions.add(new InsnNode(IRETURN));
                        isVisible.instructions.add(l1);
                        isVisible.instructions.add(new InsnNode(ICONST_0));
                        isVisible.instructions.add(new InsnNode(IRETURN));
                        node.methods.add(isVisible);
                    }
                },
        UPDATE_ENTITY(FMLForgePlugin.RUNTIME_DEOBF? "func_145845_h": "updateEntity", "()V")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node instanceof FieldInsnNode && ((FieldInsnNode)node).name.equals("timer"))
                            {
                                list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "afm/asm/StevesHooks", "tickTriggers", "(Lvswe/stevesfactory/blocks/TileEntityManager;)Lvswe/stevesfactory/blocks/TileEntityManager;", false));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                };

        protected String name;
        protected String args;
        protected TransformType type;
        protected TransformType action;

        Transformer(String name)
        {
            this(name, "", TransformType.INNER_CLASS, TransformType.MAKE_PUBLIC);
        }

        Transformer(String name, String args)
        {
            this(name, args, TransformType.METHOD, TransformType.MODIFY);
        }

        Transformer(String name, String args, TransformType type, TransformType action)
        {
            this.name = name;
            this.args = args;
            this.type = type;
            this.action = action;
        }

        protected InsnList modifyInstructions(InsnList list)
        {
            return list;
        }

        private static InsnList replace(InsnList list, String toReplace, String replace)
        {
            AbstractInsnNode node = list.getFirst();
            InsnList result = new InsnList();
            while (node != null)
            {
                result.add(checkReplace(node, toReplace, replace));
                node = node.getNext();
            }
            return result;
        }

        public String getName()
        {
            return name;
        }

        public String getArgs()
        {
            return args;
        }

        protected void methodTransform(ClassNode node)
        {
            MethodNode methodNode = getMethod(node);
            if (methodNode != null)
            {
                switch (action)
                {
                    case MODIFY:
                        methodNode.instructions = modifyInstructions(methodNode.instructions);
                        break;
                    case DELETE:
                        node.methods.remove(methodNode);
                        break;
                    case MAKE_PUBLIC:
                        methodNode.access = (methodNode.access & ~7) ^ 1;
                }
                complete();
            }
        }

        private void fieldTransform(ClassNode node)
        {
            FieldNode fieldNode = getField(node);
            if (fieldNode != null)
            {
                switch (action)
                {
                    case MODIFY:
                        modifyField(fieldNode);
                        break;
                    case DELETE:
                        node.fields.remove(fieldNode);
                        break;
                    case MAKE_PUBLIC:
                        fieldNode.access = (fieldNode.access & ~7) ^ 1;
                }
                complete();
            }
        }

        private void modifyField(FieldNode fieldNode)
        {
        }


        private void innerClassTransform(ClassNode node)
        {
            InnerClassNode innerClassNode = getInnerClass(node);
            if (innerClassNode != null)
            {
                switch (action)
                {
                    case MODIFY:
                        modifyInnerClass(innerClassNode);
                        break;
                    case DELETE:
                        node.innerClasses.remove(innerClassNode);
                        break;
                    case MAKE_PUBLIC:
                        innerClassNode.access = (innerClassNode.access & ~7) ^ 1;
                }
                complete();
            }
        }

        private void modifyInnerClass(InnerClassNode innerClassNode)
        {
        }

        public void transform(ClassNode node)
        {
            switch (this.type)
            {
                case METHOD:
                    methodTransform(node);
                    return;
                case FIELD:
                    fieldTransform(node);
                    return;
                case INNER_CLASS:
                    innerClassTransform(node);
            }
        }

        private static AbstractInsnNode checkReplace(AbstractInsnNode node, String toReplace, String replace)
        {
            if (node instanceof TypeInsnNode && ((TypeInsnNode)node).desc.equals(toReplace))
            {
                return new TypeInsnNode(NEW, replace);
            } else if (node instanceof MethodInsnNode && ((MethodInsnNode)node).owner.contains(toReplace))
            {
                return new MethodInsnNode(node.getOpcode(), replace, ((MethodInsnNode)node).name, ((MethodInsnNode)node).desc, false);
            }
            return node;
        }

        public void complete()
        {
            AdvancedFactoryManager.log.info("Applied " + this + " transformer");
        }

        public MethodNode getMethod(ClassNode classNode)
        {
            for (MethodNode method : classNode.methods)
            {
                if (method.name.equals(getName()) && method.desc.equals(getArgs()))
                {
                    return method;
                }
            }
            for (MethodNode method : classNode.methods)
            {
                if (method.desc.equals(getArgs()))
                {
                    return method;
                }
            }
            return null;
        }

        public FieldNode getField(ClassNode classNode)
        {
            for (FieldNode field : classNode.fields)
            {
                if (field.name.equals(getName()) && field.desc.equals(getArgs()))
                {
                    return field;
                }
            }
            return null;
        }

        public InnerClassNode getInnerClass(ClassNode classNode)
        {
            String name = classNode.name + "$" + getName();
            for (InnerClassNode inner : classNode.innerClasses)
            {
                if (name.equals(inner.name))
                {
                    return inner;
                }
            }
            return null;
        }
    }

    private enum ClassName
    {
        TE_MANAGER("advancedfactorymanager.tileentities.TileEntityManager", Transformer.ACTIVATE_TRIGGER, Transformer.GET_GUI, Transformer.MANAGER_INIT, Transformer.REMOVE_COMPONENT, Transformer.UPDATE_ENTITY),
        CLUSTER_BLOCK("advancedfactorymanager.blocks.BlockCableCluster", Transformer.CREATE_TE),
        ITEM_SETTING_LOAD("advancedfactorymanager.components.ItemSetting", Transformer.ITEM_SETTING_LOAD),
        COMPONENT_MENU_ITEM("advancedfactorymanager.components.ComponentMenuItem", Transformer.ITEM_SEARCH),
        CONNECTION_BLOCK("advancedfactorymanager.blocks.ConnectionBlock", Transformer.GET_DESCRIPTION),
        COMPONENT_MENU_CONTAINER("advancedfactorymanager.components.ComponentMenuContainer$2", Transformer.CONTAINER_SEARCH),
        CLUSTER_TILE("advancedfactorymanager.tileentities.TileEntityCluster", Transformer.PUBLIC_PAIR, Transformer.GET_PUBLIC_REGISTRATIONS),
        RF_CLUSTER_TILE("advancedfactorymanager.tileentities.TileEntityRFCluster", Transformer.GET_REGISTRATIONS, Transformer.GET_RF_NODE),
        CLUSTER_PAIR("advancedfactorymanager.tileentities.TileEntityCluster$Pair", Transformer.PUBLIC_TE),
        SETTINGS("advancedfactorymanager.settings.Settings", Transformer.LOAD_DEFAULT),
        CONTAINER_TYPES("advancedfactorymanager.components.ComponentMenuContainerTypes", Transformer.WRITE_TO_NBT, Transformer.READ_FROM_NBT),
        DATA_BIT_HELPER("advancedfactorymanager.network.DataBitHelper", Transformer.BIT_HELPER_INIT),
        CONNECTION_BLOCK_TYPE("advancedfactorymanager.blocks.ConnectionBlockType", Transformer.IS_INSTANCE),
        COMPONENT_MENU_INTERVAL("advancedfactorymanager.components.ComponentMenuInterval", Transformer.IS_VISIBLE);

        private String name;
        private Transformer[] transformers;

        ClassName(String name, Transformer... transformers)
        {
            this.name = name;
            this.transformers = transformers;
        }

        public String getName()
        {
            return name;
        }

        public Transformer[] getTransformers()
        {
            return transformers;
        }

        public byte[] transform(byte[] bytes)
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);

            AdvancedFactoryManager.log.log(Level.INFO, "Applying Transformer" + (transformers.length > 1 ? "s " : " ") + "to " + getName());

            for (Transformer transformer : getTransformers())
            {
                transformer.transform(classNode);
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }
    }

    private static Map<String, ClassName> classMap = new HashMap<String, ClassName>();

    static
    {
        for (ClassName className : ClassName.values()) classMap.put(className.getName(), className);
    }

    @Override
    public byte[] transform(String className, String className2, byte[] bytes)
    {
        ClassName clazz = classMap.get(className);
        if (clazz != null)
        {
            bytes = clazz.transform(bytes);
            classMap.remove(className);
        }
        return bytes;
    }
}
