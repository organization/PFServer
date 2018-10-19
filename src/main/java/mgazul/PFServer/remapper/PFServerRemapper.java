package mgazul.PFServer.remapper;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.NodeType;

public class PFServerRemapper extends JarRemapper {
    public PFServerRemapper(JarMapping jarMapping){
        super(jarMapping);
    }

    public String mapSignature(String signature, boolean typeSignature) {
        try {
            return super.mapSignature(signature, typeSignature);
        } catch (Exception e) {
            return signature;
        }
    }

    public String demapFieldName(String owner, String name, int access) {
        String mapped = RemapUtils.trydeClimb(ReflectionTransformer.fieldDeMapping, NodeType.FIELD, owner, name, (String)null, access);
        return mapped == null ? name : mapped;
    }

    public String demapMethodName(String owner, String name, String desc, int access) {
        String mapped = RemapUtils.trydeClimb(ReflectionTransformer.methodDeMapping, NodeType.METHOD, owner, name, desc, access);
        return mapped == null ? name : mapped;
    }
}
