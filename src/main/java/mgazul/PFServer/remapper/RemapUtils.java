package mgazul.PFServer.remapper;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;
import mgazul.PFServer.CatServer;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.NodeType;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

public class RemapUtils {
    public static final String NMS_PREFIX = "net/minecraft/server/";
    public static final String NMS_VERSION = CatServer.getNativeVersion();
    // Classes
    public static String reverseMapExternal(Class<?> name) {
        return reverseMap(name).replace('$', '.').replace('/', '.');
    }

    public static String reverseMap(Class<?> name) {
        return reverseMap(Type.getInternalName(name));
    }

    public static String reverseMap(String check) {
        return (String)ReflectionTransformer.classDeMapping.getOrDefault(check, check);
    }

    // Methods
    public static String mapMethod(Class<?> inst, String name, Class<?>... parameterTypes) {
        String result = mapMethodInternal(inst, name, parameterTypes);
        if (result != null) {
            return result;
        }
        return name;
    }

    /**
     * Recursive method for finding a method from superclasses/interfaces
     */
    public static String mapMethodInternal(Class<?> inst, String name, Class<?>... parameterTypes) {
        String match = reverseMap(inst) + "/" + name + " ";

        for (Entry<String, String> entry : ReflectionTransformer.jarMapping.methods.entrySet()) {
            if (entry.getKey().startsWith(match)) {
                // Check type to see if it matches
                String[] str = entry.getKey().split("\\s+");
                int i = 0;
                for (Type type : Type.getArgumentTypes(str[1])) {
                    String typename = type.getSort() == 9 ? type.getInternalName() : type.getClassName();
                    if (i >= parameterTypes.length || !typename.equals(reverseMapExternal(parameterTypes[i]))) {
                        i=-1;
                        break;
                    }
                    i++;
                }

                if (i >= parameterTypes.length)
                    return entry.getValue();
            }
        }

        // Search interfaces
        ArrayList<Class<?>> parents = new ArrayList<Class<?>>();
        parents.add(inst.getSuperclass());
        parents.addAll(Arrays.asList(inst.getInterfaces()));

        for (Class<?> superClass : parents) {
            if (superClass == null) continue;
            mapMethodInternal(superClass, name, parameterTypes);
        }

        return null;
    }

    public static String trydeClimb(Multimap map, NodeType type, String owner, String name, String desc, int access) {
        Collection colls = map.get(name);
        Iterator var7 = colls.iterator();

        String tSign;
        String tDesc;
        String tOwner;
        int tIndex;
        do {
            if (!var7.hasNext()) {
                return null;
            }

            String value = (String)var7.next();
            tSign = value;
            tDesc = null;
            if (type == NodeType.METHOD) {
                String[] tInfo = value.split(" ");
                tSign = tInfo[0];
                tDesc = tInfo.length > 1 ? remapDesc(tInfo[1]) : tDesc;
            }

            tIndex = tSign.lastIndexOf(47);
            tOwner = mapClass(tSign.substring(0, tIndex == -1 ? tSign.length() : tIndex));
        } while(!tOwner.equals(owner) || !Objects.equal(desc, tDesc));

        return tSign.substring(tIndex == -1 ? 0 : tIndex + 1);
    }

    public static String remapDesc(String pMethodDesc) {
        Type[] tTypes = Type.getArgumentTypes(pMethodDesc);

        for(int i = tTypes.length - 1; i >= 0; --i) {
            String tTypeDesc = tTypes[i].getDescriptor();
            if (tTypeDesc.endsWith(";")) {
                int tIndex = tTypeDesc.indexOf("L");
                String tMappedName = mapClass(tTypeDesc.substring(tIndex + 1, tTypeDesc.length() - 1));
                tMappedName = "L" + tMappedName + ";";
                if (tIndex > 0 && tIndex != 0) {
                    tMappedName = tTypeDesc.substring(0, tIndex);
                }

                tTypes[i] = Type.getType(tMappedName);
            }
        }

        return Type.getMethodDescriptor(Type.getType(mapClass(getTypeDesc(Type.getReturnType(pMethodDesc)))), tTypes);
    }

    public static String mapClass(String pBukkitClass) {
        String tRemapped = JarRemapper.mapTypeName(pBukkitClass, ReflectionTransformer.jarMapping.packages, ReflectionTransformer.jarMapping.classes, pBukkitClass);
        if (tRemapped.equals(pBukkitClass) && pBukkitClass.startsWith("net/minecraft/server/") && !pBukkitClass.contains(NMS_VERSION)) {
            String tNewClassStr = "net/minecraft/server/" + NMS_VERSION + "/" + pBukkitClass.substring("net/minecraft/server/".length());
            return JarRemapper.mapTypeName(tNewClassStr, ReflectionTransformer.jarMapping.packages, ReflectionTransformer.jarMapping.classes, pBukkitClass);
        } else {
            return tRemapped;
        }
    }

    public static String getTypeDesc(Type pType) {
        try {
            return pType.getInternalName();
        } catch (NullPointerException var2) {
            return pType.toString();
        }
    }
}
