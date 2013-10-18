package com.company;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 18:58
 * To change this template use File | Settings | File Templates.
 */
public class MftRecord {
    public static final int ATTR_STANDARD = 0x10;
    public static final int ATTR_ATTRLIST = 0x20;
    public static final int ATTR_FILENAME = 0x30;
    public static final int ATTR_DATA = 0x80;
    public static final int END_OF_ATTRIBUTES = 0xffffffff;
    public static final int MFT_SIZE = 1024;

    private int updateOffset;
    private int fixup;
    private long logSequence;
    private int sequence;
    private int hardLink;
    private int attrOffset;
    private int flags;
    private int mftSize;
    private int allocSize;
    private long fileReference;
    private int nextAttribute;
    public int mftNumber;
    private FileChannel channel;
    public String fileName;
    public Filesystem parent;
    public Vector<NtfsAttribute> attrList = new Vector<NtfsAttribute>();

    /**
     *
     * @param bb
     */
    private void parseAttributes(ByteBuffer bb) {
        int position = 0;
        int attrType = 0;
        int attrLength = 0;
        ByteBuffer bBuf;

        bb.order(ByteOrder.LITTLE_ENDIAN);
        while ((attrType = bb.getInt(position)) != END_OF_ATTRIBUTES) {
            attrLength = bb.getInt(position + 4);
            bBuf = ByteBuffer.allocate(attrLength);
            bBuf.put(bb.array(), position, attrLength);
            switch (attrType) {
                case ATTR_STANDARD:
                    NtfsAttributeStandard attrSt = new NtfsAttributeStandard(bb, this);
                    attrList.add(attrSt);
                    //attrSt.printAttribute();
                    break;
                case ATTR_FILENAME:
                    NtfsAttributeFilename AttrFn = new NtfsAttributeFilename(bBuf, this);
                    attrList.add(AttrFn);
                    //AttrFn.printAttribute();
                    break;
                case ATTR_DATA:
                    NtfsAttributeData AttrDa = new NtfsAttributeData(bBuf, this);
                    attrList.add(AttrDa);
                    //AttrDa.printAttribute();
                    break;
                default:
                    break;
            }
            position += attrLength;
        }


    }

    /**
     *
     * @param bb
     */
    private void parseRecord(ByteBuffer bb) {
        bb.order(ByteOrder.LITTLE_ENDIAN);
        updateOffset = bb.getShort(4);
        fixup = bb.getShort(6);
        logSequence = bb.getLong(8);
        hardLink = bb.getShort(18);
        attrOffset = bb.getShort(20);
        flags = bb.getShort(22);
        mftSize = bb.getInt(24);
        allocSize = bb.getInt(28);
        fileReference = bb.getLong(32);
        nextAttribute = bb.getShort(40);
        mftNumber = bb.getInt(44);

        // Fixup array adjustments
        int x = updateOffset + 2;
        int y = fixup - 1;
        byte[] tempArray1 = new byte[510];
        byte[] tempArray2;
        byte[] f = new byte[2];
        ByteBuffer fixedBuffer = ByteBuffer.allocate(MFT_SIZE);
        tempArray2 = bb.array();
        for (int z = 0; z < y; z++) {
            f[0] = tempArray2[x];
            f[1] = tempArray2[x + 1];
            x += 2;
            System.arraycopy(tempArray2, z * 512, tempArray1, 0, 510);
            fixedBuffer.put(tempArray1);
            fixedBuffer.put(f);
        }
        if (attrOffset != 0) {
            tempArray2 = fixedBuffer.array();
            ByteBuffer tempBuffer3 = ByteBuffer.allocate(mftSize - attrOffset);
            tempBuffer3.put(tempArray2, attrOffset, mftSize - attrOffset);
            parseAttributes(tempBuffer3);
        }
    }

    /**
     *
     * @param fc
     * @param bb
     * @param p
     */
    public MftRecord(FileChannel fc, ByteBuffer bb, Filesystem p) {
        parseRecord(bb);
        channel = fc;
        parent = p;
    }

    /**
     *
     * @param bb
     * @param p
     */

    public MftRecord(ByteBuffer bb, Filesystem p) {
        parseRecord(bb);
        channel = null;
        parent = p;
    }

    /**
     *
     * @return
     */
    public String toString() {
        String s;
        s = String.format("%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n",
                "Update offset", updateOffset, "Fixup", fixup, "Log sequence", logSequence, "Attribute offset", attrOffset,
                "Flags", flags, "Mft size", mftSize, "Allocated size", allocSize, "Next attribute", nextAttribute,
                "File reference", fileReference, "MFT number", mftNumber);
        return s;

    }

    /**
     *
     * @return
     * @throws CreatorException
     */
    public NtfsAttribute findDataAttribute() throws CreatorException {
        for (NtfsAttribute a : attrList) {
            if (a.attrType == ATTR_DATA)
                return a;
        }
        throw new CreatorException("No data attribute");

    }

}
