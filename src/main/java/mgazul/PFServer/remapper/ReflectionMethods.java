package mgazul.PFServer.remapper;

import mgazul.PFServer.CatServer;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionMethods {
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, true, ReflectionUtils.getCallerClassloader());
    }

    public static Class<?> forName(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        if (!className.startsWith("net.minecraft.server."+ CatServer.getNativeVersion())) return Class.forName(className, initialize, classLoader);
        className = ReflectionTransformer.jarMapping.classes.get(className.replace('.', '/')).replace('/', '.');
        return Class.forName(className, initialize, classLoader);
    }

    public static Field getField(Class inst, String name) throws NoSuchFieldException, SecurityException {
        return !inst.getName().startsWith("net.minecraft.") ? inst.getField(name) : inst.getField(ReflectionTransformer.remapper.mapFieldName(RemapUtils.reverseMap(inst), name, (String)null));
    }

    public static Field getDeclaredField(Class inst, String name) throws NoSuchFieldException, SecurityException {
        return !inst.getName().startsWith("net.minecraft.") ? inst.getDeclaredField(name) : inst.getDeclaredField(ReflectionTransformer.remapper.mapFieldName(RemapUtils.reverseMap(inst), name, (String)null));
    }

    public static Method getMethod(Class inst, String name, Class... parameterTypes) throws NoSuchMethodException, SecurityException {
        return !inst.getName().startsWith("net.minecraft.") ? inst.getMethod(name, parameterTypes) : inst.getMethod(RemapUtils.mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    public static Method getDeclaredMethod(Class inst, String name, Class... parameterTypes) throws NoSuchMethodException, SecurityException {
        return !inst.getName().startsWith("net.minecraft.") ? inst.getDeclaredMethod(name, parameterTypes) : inst.getDeclaredMethod(RemapUtils.mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    public static String getName(Field field) {
        return !field.getDeclaringClass().getName().startsWith("net.minecraft.") ? field.getName() : RemapUtils.demapFieldName(field);
    }

    public static String getName(Method method) {
        return !method.getDeclaringClass().getName().startsWith("net.minecraft.") ? method.getName() : RemapUtils.demapMethodName(method);
    }

    public static String getSimpleName(Class inst) {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getSimpleName();
        } else {
            String[] name = RemapUtils.reverseMapExternal(inst).split("\\.");
            return name[name.length - 1];
        }
    }

    public static Class loadClass(String pClazzName) throws ClassNotFoundException {
        return loadClass((ClassLoader)null, pClazzName);
    }

    public static Class loadClass(ClassLoader pLoader, String pClazzName) throws ClassNotFoundException {
        if (pClazzName.startsWith("net.minecraft.")) {
            pClazzName = RemapUtils.mapClass(pClazzName.replace('.', '/')).replace('/', '.');
        }

        return pLoader == null ? Class.forName(pClazzName) : pLoader.loadClass(pClazzName);
    }
}
