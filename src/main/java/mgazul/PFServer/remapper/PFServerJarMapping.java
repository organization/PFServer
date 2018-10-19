package mgazul.PFServer.remapper;

import com.google.common.base.Objects;
import net.md_5.specialsource.InheritanceMap;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.NodeType;
import net.md_5.specialsource.provider.InheritanceProvider;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.objectweb.asm.Type;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PFServerJarMapping extends JarMapping {

    private InheritanceMap _inheritanceMap = null;
    private InheritanceProvider _fallbackInheritanceProvider = null;
    private Set _excludedPackages = null;
    public static final String NMS_PREFIX = "net/minecraft/server/";
    public static final String NMS_VERSION = "v1_12_R1";

    public PFServerJarMapping() {
        this._inheritanceMap = (InheritanceMap)ReflectionHelper.getPrivateValue(JarMapping.class, this, "inheritanceMap");
        this._excludedPackages = (Set)ReflectionHelper.getPrivateValue(JarMapping.class, this, "excludedPackages");
    }

    public void setInheritanceMap(InheritanceMap inheritanceMap) {
        super.setInheritanceMap(inheritanceMap);
        this._inheritanceMap = inheritanceMap;
    }

    public void setFallbackInheritanceProvider(InheritanceProvider fallbackInheritanceProvider) {
        super.setFallbackInheritanceProvider(fallbackInheritanceProvider);
        this._fallbackInheritanceProvider = fallbackInheritanceProvider;
    }

    public String trydeClimb(Map map, NodeType type, String owner, String name, String desc, int access) {
        Iterator var7 = map.entrySet().iterator();

        if (map.containsKey(name)) {
            String tSign = (String)map.get(name);
            String tDesc = null;
            if (type == NodeType.METHOD) {
                String[] tInfo = tSign.split(" ");
                tSign = tInfo[0];
                tDesc = tInfo.length > 1 ? this.remapDesc(tInfo[1]) : tDesc;
            }

            int tIndex = tSign.lastIndexOf(47);
            String tOwner = this.mapClass(tSign.substring(0, tIndex == -1 ? tSign.length() : tIndex));
            if (tOwner.equals(owner) && Objects.equal(desc, tDesc)) {
                return tSign.substring(tIndex == -1 ? 0 : tIndex + 1);
            }
        }

        return null;
    }

    public String remapDesc(String pMethodDesc) {
        Type[] tTypes = Type.getArgumentTypes(pMethodDesc);

        for(int i = tTypes.length - 1; i >= 0; --i) {
            String tTypeDesc = tTypes[i].getDescriptor();
            if (tTypeDesc.endsWith(";")) {
                int tIndex = tTypeDesc.indexOf("L");
                String tMappedName = this.mapClass(tTypeDesc.substring(tIndex + 1, tTypeDesc.length() - 1));
                tMappedName = "L" + tMappedName + ";";
                if (tIndex > 0 && tIndex != 0) {
                    tMappedName = tTypeDesc.substring(0, tIndex);
                }

                tTypes[i] = Type.getType(tMappedName);
            }
        }

        return Type.getMethodDescriptor(Type.getType(this.mapClass(this.getTypeDesc(Type.getReturnType(pMethodDesc)))), tTypes);
    }

    public String mapClass(String pBukkitClass) {
        String tRemapped = JarRemapper.mapTypeName(pBukkitClass, this.packages, this.classes, pBukkitClass);
        if (tRemapped.equals(pBukkitClass) && pBukkitClass.startsWith("net/minecraft/server/") && !pBukkitClass.contains("v1_12_R1")) {
            String tNewClassStr = "net/minecraft/server/v1_12_R1/" + pBukkitClass.substring("net/minecraft/server/".length());
            return JarRemapper.mapTypeName(tNewClassStr, this.packages, this.classes, pBukkitClass);
        } else {
            return tRemapped;
        }
    }

    public String getTypeDesc(Type pType) {
        try {
            return pType.getInternalName();
        } catch (NullPointerException var3) {
            return pType.toString();
        }
    }
}
