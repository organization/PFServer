package mgazul.PFServer.remapper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.provider.JointProvider;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;

public class ReflectionTransformer {
    public static final String DESC_ReflectionMethods = Type.getInternalName(ReflectionMethods.class);
    public static JarMapping jarMapping;
    public static PFServerRemapper remapper;
    public static final HashMap classDeMapping = Maps.newHashMap();
    public static final Multimap methodDeMapping = ArrayListMultimap.create();
    public static final Multimap fieldDeMapping = ArrayListMultimap.create();

    public static void init() {
        jarMapping = MappingLoader.loadMapping();
        JointProvider provider = new JointProvider();
        provider.add(new ClassInheritanceProvider());
        jarMapping.setFallbackInheritanceProvider(provider);
        remapper = new PFServerRemapper(jarMapping);
        jarMapping.classes.forEach((k, v) -> {
            String var10000 = (String)classDeMapping.put(v, k);
        });
        jarMapping.methods.forEach((k, v) -> {
            methodDeMapping.put(v, k);
        });
        jarMapping.fields.forEach((k, v) -> {
            fieldDeMapping.put(v, k);
        });
    }

    /**
     * Convert code from using Class.X methods to our remapped versions
     */
    public static byte[] transform(byte[] code) {
        ClassReader reader = new ClassReader(code); // Turn from bytes into visitor
        ClassNode node = new ClassNode();
        reader.accept(node, 0); // Visit using ClassNode

        for (MethodNode method : node.methods) { // Taken from SpecialSource
            ListIterator<AbstractInsnNode> insnIterator = method.instructions.iterator();
            while (insnIterator.hasNext()) {
                AbstractInsnNode next = (AbstractInsnNode)insnIterator.next();
                if (next instanceof MethodInsnNode) {
                    MethodInsnNode insn = (MethodInsnNode)next;
                switch (insn.getOpcode()) {
                    case Opcodes.INVOKEVIRTUAL:
                        remapVirtual(insn);
                        break;
                    case Opcodes.INVOKESTATIC:
                        remapForName(insn);
                }
                    if (insn.name.equals("getName") && insn.getOpcode() >= 182 && insn.getOpcode() <= 186) {
                        if (insn.owner.equals("java/lang/reflect/Field")) {
                            insn.owner = DESC_ReflectionMethods;
                            insn.name = "getName";
                            insn.setOpcode(Opcodes.INVOKESTATIC);
                            insn.desc = "(Ljava/lang/reflect/Field;)Ljava/lang/String;";
                        } else if (insn.owner.equals("java/lang/reflect/Method")) {
                            insn.owner = DESC_ReflectionMethods;
                            insn.name = "getName";
                            insn.setOpcode(Opcodes.INVOKESTATIC);
                            insn.desc = "(Ljava/lang/reflect/Method;)Ljava/lang/String;";
                        }
                    }

                    if (insn.owner.equals("java/lang/ClassLoader") && insn.name.equals("loadClass")) {
                        insn.owner = DESC_ReflectionMethods;
                        insn.name = "loadClass";
                        insn.desc = "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;";
                        insn.setOpcode(Opcodes.INVOKESTATIC);
                    }

                    if (insn.owner.equals("javax/script/ScriptEngineManager") && insn.desc.equals("()V") && insn.name.equals("<init>")) {
                        insn.desc = "(Ljava/lang/ClassLoader;)V";
                        method.instructions.insertBefore(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;"));
                        ++method.maxStack;
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(0/*ClassWriter.COMPUTE_FRAMES*/);
        node.accept(writer); // Convert back into bytes
        return writer.toByteArray();
    }

    public static void remapForName(AbstractInsnNode insn) {
        MethodInsnNode method = (MethodInsnNode) insn;
        if (!method.owner.equals("java/lang/Class") || !method.name.equals("forName")) return;
        method.owner = DESC_ReflectionMethods;
    }

    public static void remapVirtual(AbstractInsnNode insn) {
        MethodInsnNode method = (MethodInsnNode) insn;

        if (!method.owner.equals("java/lang/Class") ||
                !(method.name.equals("getField") || method.name.equals("getDeclaredField") ||
                method.name.equals("getMethod") || method.name.equals("getDeclaredMethod"))|| method.name.equals("getSimpleName"))
            return;

        Type returnType = Type.getReturnType(method.desc);

        ArrayList<Type> args = new ArrayList<Type>();
        args.add(Type.getObjectType(method.owner));
        args.addAll(Arrays.asList(Type.getArgumentTypes(method.desc)));

        method.setOpcode(Opcodes.INVOKESTATIC);
        method.owner = DESC_ReflectionMethods;
        method.desc = Type.getMethodDescriptor(returnType, args.toArray(new Type[args.size()]));
    }
}
