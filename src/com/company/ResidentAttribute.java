package com.company;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public class ResidentAttribute extends AttrLocation {
    private int size;
    private int offset;
    private byte[] data;
    private ByteBuffer dataBuffer;

    /**
     *
     * @param bb
     * @param p
     */
    public ResidentAttribute(ByteBuffer bb, MftRecord p) {
        bb.order(ByteOrder.LITTLE_ENDIAN);
        size = bb.getInt(16);
        offset = bb.getShort(20);
        parent = p;

        byte[] tmp;
        tmp = bb.array();
        data = new byte[size];
        dataBuffer = ByteBuffer.allocate(size);
        dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        System.arraycopy(tmp, offset, data, 0, size);
        dataBuffer.put(data);
        //BufferTemp.hexDump(dataBuffer);

    }

    /**
     *
     * @param offset
     * @return
     * @throws CreatorException
     */
    public int getInt(long offset) throws CreatorException {
        int q;
        try {
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.getInt((int) offset);
            return q;
        } catch (BufferUnderflowException w) {
            throw new CreatorException("Offset out of range");
        }
    }

    /**
     *
     * @param offset
     * @return
     * @throws CreatorException
     */
    public short getShort(long offset) throws CreatorException {
        short q;
        try {
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.getShort((int) offset);
            System.out.format("ytes: %d,%d\n", dataBuffer.get((int)offset), dataBuffer.get((int)offset + 1));
            return q;
        } catch (BufferUnderflowException w) {
            throw new CreatorException("Offset out of range");
        }
    }

    /**
     *
     * @param offset
     * @return
     * @throws CreatorException
     */
    public byte getByte(long offset) throws CreatorException {
        byte q;
        try {
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.get((int)offset);

            return q;
        } catch (BufferUnderflowException w) {
            throw new CreatorException("Offset out of range");
        }
    }

    /**
     *
     * @param offset
     * @return
     * @throws CreatorException
     */
    public long getLong(long offset) throws CreatorException {
        long q;
        try {
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.getLong((int)offset);
            return q;
        } catch (BufferUnderflowException w) {
            throw new CreatorException("Offset out of range");
        }
    }

    /**
     *
     * @param offset
     * @param len
     * @return
     * @throws CreatorException
     */
    public String getString(long offset, int len) throws CreatorException {

        try {
            byte[] rArr = new byte[len * 2];
            byte[] sArr = Arrays.copyOfRange(dataBuffer.array(), (int)offset, (int)offset + (len * 2));
            /* Swap little-endian to big-endian unicode */
            for (int i = 0; i < len * 2; i += 2) {
                rArr[i + 1] = sArr[i];
                rArr[i] = sArr[i + 1];
            }

            String q = new String(rArr);

            return q;
        } catch (BufferUnderflowException w) {
            throw new CreatorException("Offset out of range");
        }
    }


    /**
     *
     * @return
     */
    public long getAttrLength() {
        return (long) size;
    }

    /**
     *
     * @param offset
     * @param len
     * @return
     */
    @Override
    public ByteBuffer readData(long offset, int len) {
        return dataBuffer;
    }
}
