package com.company;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 06/05/2013
 * Time: 11:47
 * To change this template use File | Settings | File Templates.
 */
public class NtfsAttributeData extends NtfsAttribute implements AttributeInterface {

    public NtfsAttributeData(ByteBuffer bb, MftRecord p) {
        super(bb, p);
        if (attrResident) {
            contents = new ResidentAttribute(bb, p);
        } else {
            contents = new NonResidentAttribute(bb, p);
        }

    }

    /**
     *
     */
    @Override
    public void printAttribute() {
        printAttributeHeader();
        System.out.format("%-16s:%d\n", "Attr length", contents.getAttrLength());
    }

    /**
     *
     * @return
     */
    public String toString() {
        return String.format("%-16s:%d\n", "Attr length", contents.getAttrLength());
    }

    /**
     *
     * @return
     */
    public String getAttributeName() {
        return "Data";
    }
}
