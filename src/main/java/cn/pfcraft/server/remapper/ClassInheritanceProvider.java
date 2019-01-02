package cn.pfcraft.server.remapper;

import net.md_5.specialsource.provider.InheritanceProvider;

import java.util.Collection;
import java.util.HashSet;

import static cn.pfcraft.server.remapper.RemapUtils.reverseMap;

public class ClassInheritanceProvider implements InheritanceProvider {
    @Override
    public Collection<String> getParents(String className) {
        className = ReflectionTransformer.remapper.map(className);

        try {
            Collection<String> parents = new HashSet<>();
            Class<?> reference = Class.forName(className.replace('/', '.').replace('$', '.'), false, this.getClass().getClassLoader()/*ReflectionMethods.loader*/);
            Class<?> extend = reference.getSuperclass();
            if (extend != null) {
                parents.add(reverseMap(extend));
            }

            for (Class<?> inter : reference.getInterfaces()) {
                if (inter != null) {
                    parents.add(reverseMap(inter));
                }
            }

            return parents;
        } catch (Exception e) {
            // Empty catch block
        }
        return null;
    }

}