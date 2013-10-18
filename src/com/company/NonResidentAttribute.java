package com.company;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 06/05/2013
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class NonResidentAttribute extends AttrLocation {
    public static final int INTEGER_LENGTH = 4;
    public static final int LONG_LENGTH = 8;
    public static final int SHORT_LENGTH = 2;
    public static final int BYTE_LENGTH = 1;
    private Vector<Long> clusterList = new Vector<Long>();
    private long allocatedSizeOfContent;
    private long actualSizeOfContent;
    private long initialisedSizeOfContent;
    private int compressionUnitSize;

    /**
     *
     * @param bb
     * @param p
     */
    public NonResidentAttribute(ByteBuffer bb, MftRecord p) {
        bb.order(ByteOrder.LITTLE_ENDIAN);

        long startVirtualCluster = bb.getLong(16);
        long endVirtualCluster = bb.getLong(24);
        int runlistOffset = bb.getShort(32);
        long runLength, runOffset;
        StringBuilder sb;
        compressionUnitSize = bb.getShort(34);
        allocatedSizeOfContent = bb.getLong(40);
        actualSizeOfContent = bb.getLong(48);
        initialisedSizeOfContent = bb.getLong(56);
        parent = p;

        /*
        System.out.format("%d,%d,%d,%d,%d,%d,%d\n", startVirtualCluster,endVirtualCluster,runlistOffset,compressionUnitSize,allocatedSizeOfContent,
                actualSizeofContent,initialisedSizeOfContent);
        */

        if (startVirtualCluster != 0) {
            System.out.println("Non-zero vc start");
            return;
        }
        int i = 0;
        int tempInt;
        int ptr = runlistOffset;

        //byte[] crArr = bb.array();
        int nibble,nibbleOffset,nibbleLength=0;

        while (i <= endVirtualCluster) {
            nibble = bb.get(ptr);
            nibbleOffset = (nibble & 240) >> 4;
            nibbleLength = nibble & 15;
            if (nibbleOffset == 0)
                break;
            ptr++;
            sb = new StringBuilder();

            while (nibbleLength > 0) {
                nibbleLength--;
                tempInt = bb.get(ptr) & 0xFF;
                sb.insert(0,String.format("%02X", tempInt));
                ptr++;
            }
            runLength = Long.parseLong(sb.toString(),16);

            sb = new StringBuilder();
            while (nibbleOffset > 0) {
                nibbleOffset--;
                tempInt = bb.get(ptr) & 0xFF;
                sb.insert(0,String.format("%02X", tempInt));
                ptr++;
            }

            runOffset = Long.parseLong(sb.toString(),16);

            i += runLength;
            for (long cluster = runOffset; cluster < runOffset+runLength; cluster++) {
                clusterList.add(cluster);

            }
        }
    }

    /**
     *
     * @param clusterNumber
     * @return
     */
    public ByteBuffer readCluster(long clusterNumber) {

        int clusterSize = this.parent.parent.getClusterSize();
        FileChannel channel = this.parent.parent.getChannel();
        ByteBuffer result = ByteBuffer.allocate(clusterSize);
        try {
            channel.position(clusterSize*clusterNumber);
            channel.read(result);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
        return result;
    }

    /**
     *
     * @param offset
     * @param length
     * @return
     */
    public ByteBuffer readData(long offset, int length) {

        int clusterSize = this.parent.parent.getClusterSize();
        int cluster;
        int lengthCounter=0;
        int lengthTemp;

        ByteBuffer readBuffer = ByteBuffer.allocate(clusterSize);

        if (length < 1) {
            System.out.println("must readData at least 1 byte");
            System.exit(1);
        }
        ByteBuffer result = ByteBuffer.allocate(length);


        if (offset+length > actualSizeOfContent) {
            System.out.println("Seeking too far");
            System.exit(1);
        }

        // First cluster - may be partial
        cluster = (int) (offset / clusterSize);
        readBuffer = readCluster(clusterList.get(cluster));
        lengthTemp = length > clusterSize - (int) (offset % clusterSize) ? clusterSize - (int) (offset % clusterSize) : length;

        result.put(readBuffer.array(), (int)(offset % clusterSize), lengthTemp);
        lengthCounter = clusterSize - (int) (offset % clusterSize);
        while (lengthCounter < length) {
            cluster++;
            readBuffer.clear();
            readBuffer = readCluster(clusterList.get(cluster));
            if (length-lengthCounter > clusterSize) {
                result.put(readBuffer.array());
                lengthCounter += clusterSize;
            }
            else {
                result.put(readBuffer.array(),0,length-lengthCounter);
                lengthCounter = length;
            }
        }
        return result;

    }

    /**
     *
     * @param offset
     * @return
     * @throws CreatorException
     */
    @Override
    public int getInt(long offset) throws CreatorException {
        int q;
        ByteBuffer dataBuffer;
        try {
            dataBuffer = readData(offset, INTEGER_LENGTH);
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.getInt(0);
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
    @Override
    public long getLong(long offset) throws CreatorException {
        long q;
        ByteBuffer dataBuffer;
        try {
            dataBuffer = readData(offset, LONG_LENGTH);
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.getLong(0);
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
    @Override
    public short getShort(long offset) throws CreatorException {
        short q;
        ByteBuffer dataBuffer;
        try {
            dataBuffer = readData(offset, SHORT_LENGTH);
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.getShort(0);
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
    @Override
    public byte getByte(long offset) throws CreatorException {
        byte q;
        ByteBuffer dataBuffer;
        try {
            dataBuffer = readData(offset, BYTE_LENGTH);
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            q = dataBuffer.get(0);
            return q;
        } catch (BufferUnderflowException w) {
            throw new CreatorException("Offset out of range");
        }
    }

    /**
     *
     * @param offset
     * @param len
     * @return String
     * @throws CreatorException
     */
    @Override
    public String getString(long offset, int len) throws CreatorException {
        ByteBuffer dataBuffer;
        try {
            dataBuffer = readData(offset, len*2);
            byte[] rArr = new byte[len * 2];
            byte[] sArr = Arrays.copyOfRange(dataBuffer.array(), (int) offset, (int) offset + (len * 2));
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

    public long getAttrLength() {
        return actualSizeOfContent;
    }
}
