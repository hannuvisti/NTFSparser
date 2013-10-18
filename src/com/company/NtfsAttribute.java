package com.company;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 19:02
 * To change this template use File | Settings | File Templates.
 */
public abstract class NtfsAttribute {
    public static final int ATTRIBUTE_TYPE = 0;
    public static final int ATTRIBUTE_LENGTH = 4;


    public int attrType;
    protected int attrLength;
    protected boolean attrResident;
    protected byte attrNameLength;
    protected int attrNameOffset;
    protected int attrFlags;
    protected int attrId;
    protected int attrSizeOfContent;
    public MftRecord parent;
    protected String attrName;
    protected AttrLocation contents;


    public NtfsAttribute(ByteBuffer bb, MftRecord p) {
        bb.order(ByteOrder.LITTLE_ENDIAN);
        attrType = bb.getInt(ATTRIBUTE_TYPE);
        attrLength = bb.getInt(ATTRIBUTE_LENGTH);
        attrResident = bb.get(8) == 0 ? true : false;
        attrNameLength = bb.get(9);
        attrNameOffset = bb.getShort(10);
        attrFlags = bb.getShort(12);
        attrId = bb.getShort(14);
        if (attrNameLength != 0)
            attrName = BufferTemp.getString(bb, attrNameOffset, attrNameLength);
        else
            attrName = "";
        parent = p;
    }

    /**
     *
     */
    public void printAttributeHeader() {
        System.out.println("--- Attribute ----");
        System.out.format("%-16s:%s\n", "Attr type", getAttributeName());
        System.out.format("%-16s:%s\n", "Attr name", attrName);
        System.out.format("%-16s:%s\n", "Resident", (attrResident ? "true" : "false"));
        System.out.format("%-16s:%d\n", "Length", attrLength);
    }

    /**
     *
     * @return
     */
    abstract public String getAttributeName();

    abstract public void printAttribute();


}
