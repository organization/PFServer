package mgazul.PFServer.remapper;

import mgazul.PFServer.CatServer;
import net.md_5.specialsource.JarRemapper;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

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
        String match = reverseMap(inst) + "/" + name;
        ReflectionTransformer.jarMapping.methods.entrySet();
        Collection colls = ReflectionTransformer.methodFastMapping.get(match);
        Iterator var5 = colls.iterator();

        String value;
        int i;
        do {
            if (!var5.hasNext()) {
                return null;
            }

            value = (String)var5.next();
            String[] str = value.split("\\s+");
            i = 0;
            Type[] var9 = Type.getArgumentTypes(str[1]);
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                Type type = var9[var11];
                String typename = type.getSort() == 9 ? type.getInternalName() : type.getClassName();
                if (i >= parameterTypes.length || !typename.equals(reverseMapExternal(parameterTypes[i]))) {
                    i = -1;
                    break;
                }

                ++i;
            }
        } while(i < parameterTypes.length);

        return (String)ReflectionTransformer.jarMapping.methods.get(value);
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

    public static String demapFieldName(Field field) {
        String name = field.getName();
        String match = reverseMap(field.getDeclaringClass());
        Collection colls = ReflectionTransformer.fieldDeMapping.get(name);
        Iterator var4 = colls.iterator();

        String value;
        do {
            if (!var4.hasNext()) {
                return name;
            }

            value = (String)var4.next();
        } while(!value.startsWith(match));

        String[] matched = value.split("\\/");
        String rtr = matched[matched.length - 1];
        return rtr;
    }

    public static String demapMethodName(Method method) {
        String name = method.getName();
        String match = reverseMap(method.getDeclaringClass());
        Collection colls = ReflectionTransformer.methodDeMapping.get(name);
        Iterator var4 = colls.iterator();

        String value;
        do {
            if (!var4.hasNext()) {
                return name;
            }

            value = (String)var4.next();
        } while(!value.startsWith(match));

        String[] matched = value.split("\\s+")[0].split("\\/");
        String rtr = matched[matched.length - 1];
        return rtr;
    }
}
