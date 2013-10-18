package com.company;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class Filesystem {

    public static final int ATTR_DATA = 0x80;
    public static final int MFT_SIZE = 1024;

    private FileChannel channel;
    private long mft1;
    private long mft2;
    private long fsSize;
    private int mftSize;
    private int clusterSize;
    private int sectorSize;
    private Vector<MftRecord> mftList = new Vector<MftRecord> ();

    /**
     *
     * @param fc
     */
    public Filesystem(FileChannel fc)  {
        ByteBuffer bb = ByteBuffer.allocate(512);

        mft1 = 0;
        mft2 = 0;
        channel = fc;
        try {
            channel.position(0);
            if (channel.read(bb) != 512) {
                System.out.println("poskelleen meni");
            }
            bb.order(ByteOrder.LITTLE_ENDIAN);
            mft1 = bb.getLong(48);
            mft2 = bb.getLong(56);
            sectorSize = bb.getShort(11);
            clusterSize = bb.get(13)*sectorSize;
            fsSize = bb.getLong(40);
            int tmp = bb.get(64);

            if (tmp < 0)
                mftSize = (int) Math.pow(2,Math.abs(tmp));
            else
                mftSize = tmp * clusterSize;
            readMft0();
        }

        catch (IOException x) {
            System.out.println("IO exp: "+x.getMessage());
        }
    }

    /**
     *
     */

    private void readMft0()  {
        ByteBuffer bb = ByteBuffer.allocate(mftSize);
        MftRecord Mft0;
        NtfsAttribute a;

        // Read Mft record 0
        try {
            channel.position(mft1 * clusterSize);

            if (channel.read(bb) != MFT_SIZE) {
                System.out.format("Can't read MFT\n");
                System.exit(1);
            }
            Mft0 = new MftRecord(bb,this);
            mftList.addElement(Mft0);
            try {
                a = Mft0.findDataAttribute();
                // Read the rest
                bb.clear();
                for (int i = 1024; i < a.contents.getAttrLength();i += MFT_SIZE) {
                    bb.clear();
                    bb = a.contents.readData(i,1024);

                    mftList.addElement(new MftRecord(bb,this));
                }
                //BufferTemp.hexDump(bb);
            } catch (CreatorException e) {
                System.out.println("Mft0 does not contain data, exiting");
                System.exit(1);
            }

        }
        catch (IOException x) {
            System.out.println("IO exp: "+x.getMessage());
            System.exit(1);
        }


    }




    /**
     *
     * @return
     */
    public String toString() {
        return String.format("%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n",
                "MFT1", mft1, "MFT2", mft2, "Cluster size", clusterSize, "Sector size", sectorSize,
                "MFT rec size", mftSize, "FS size", fsSize);
    }

    /**
     *
     * @return
     */
    public int getClusterSize() {
        return clusterSize;
    }

    /**
     *
     * @return
     */
    public FileChannel getChannel() {
        return channel;
    }

    /**
     *
     */
    public void demo() {
        NtfsAttribute a;
        long fileSize = 0;
        for (MftRecord m : mftList) {
            try {
                a = m.findDataAttribute();
                fileSize = a.contents.getAttrLength();
            } catch (CreatorException e) {
                fileSize = 0;
            }
            finally {
                 System.out.format("%-5d:%-12d:%s\n", m.mftNumber, fileSize, m.fileName);
            }
        }



    }
}

