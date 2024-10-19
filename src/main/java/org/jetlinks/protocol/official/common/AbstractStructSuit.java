package org.jetlinks.protocol.official.common;

import org.jetlinks.protocol.official.binary2.BinaryFeatureCodeExtractor;
import org.jetlinks.protocol.official.binary2.StructDeclaration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public abstract class AbstractStructSuit {

    private final String  name;

    private final String  version;

    private final String  description;

    private final Map<String, StructDeclaration> idxByNameMap;

    public AbstractStructSuit(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.idxByNameMap = new HashMap<>();
    }

    public void addStructDeclaration(StructDeclaration structDcl) {
        idxByNameMap.put(structDcl.getName(), structDcl);
    }

    public StructDeclaration getStructDeclaration(String name) {
        return idxByNameMap.get(name);
    }

    public Iterable<StructDeclaration>  structDeclarations() {
        return idxByNameMap.values();
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "AbstractStructSuit{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
