package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nonnull;

/**
 * StructGroup的左边界作为锚点
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/29
 * @since V3.1.0
 */
public class GroupBeginAnchor implements DynamicAnchor {

    @Nonnull
    private final GroupDeclaration  targetGroup;

    private StructInstance          currentStructInst;

    private short     groupIdx = 0;

    public GroupBeginAnchor(@Nonnull GroupDeclaration targetGroup) {
        this.targetGroup = targetGroup;
    }

    @Override
    public short getAbsoluteOffset(short relativeOffset) {
        short rst = (short)(this.groupIdx * targetGroup.getSize() + targetGroup.getOffset() + relativeOffset);
        return rst;
    }

    @Override
    public void bind(StructInstance structInst) {
        this.currentStructInst = structInst;
    }

    @Override
    public void bind(StructInstance structInst, short groupIdx) {
        this.groupIdx = groupIdx;
        this.currentStructInst = structInst;
    }
}
