package com.company;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 21:39
 * To change this template use File | Settings | File Templates.
 */
public class NtfsAttributeFilename extends NtfsAttribute implements AttributeInterface {
    private long logicalFileSize;
    private int nameLength;
    private String fileName;

    public NtfsAttributeFilename(ByteBuffer bb, MftRecord p) {
        super (bb, p);
        if (attrResident) {
            contents = new ResidentAttribute(bb, p);
        }
        else
            return;
        try {
            logicalFileSize = contents.getLong(40);
        } catch (CreatorException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            nameLength = contents.getByte(64);
            fileName = contents.getString(66,nameLength);
            p.fileName = fileName;
        } catch (CreatorException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    /**
     *
     */
    public void printAttribute() {
        this.printAttributeHeader();
        System.out.format("%-16s:%d\n", "Logical size",logicalFileSize);
        System.out.format("%-16s:%d\n", "Name length",nameLength);
        System.out.format("%-16s:%s\n", "File name",fileName);

    }

    /**
     *
     * @return
     */
    public String toString() {
        return String.format("%-16s:%d\n%-16s:%d\n%-16s:%s\n", "Logical size", logicalFileSize, "Name length", nameLength, "File name", fileName);

    }

    /**
     *
     * @return
     */
    public String getAttributeName() {
        return "File name";
    }
}