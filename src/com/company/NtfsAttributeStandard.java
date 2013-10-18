package com.company;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class NtfsAttributeStandard extends NtfsAttribute implements AttributeInterface {

    private long attrCtime;
    private long attrAtime;
    private long attrMtime;
    private long attrRtime;
    private int versionNumber;
    private int classId;

    public NtfsAttributeStandard(ByteBuffer bb, MftRecord p) {
        super (bb, p);
        if (attrResident) {
            contents = new ResidentAttribute(bb, p);
        }
        else {
            return;
        }

        try {
            attrMtime = contents.getLong(16);
            attrCtime = contents.getLong(0);
            attrAtime = contents.getLong(8);
            attrRtime = contents.getLong(24);

        } catch (CreatorException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            versionNumber = contents.getInt(40);
            classId = contents.getInt(44);
        } catch (CreatorException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    /**
     *
     */
    public void printAttribute() {
        this.printAttributeHeader();
        System.out.format("%-16s:%d\n", "C time",attrCtime);
        System.out.format("%-16s:%d\n", "A time",attrAtime);
        System.out.format("%-16s:%d\n", "M time",attrMtime);
        System.out.format("%-16s:%d\n", "R time",attrRtime);
        System.out.format("%-16s:%d\n", "Version nbr",versionNumber);
        System.out.format("%-16s:%d\n", "Class id", classId);
    }

    /**
     *
     * @return
     */
    public String toString() {
        return String.format("%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n%-16s:%d\n", "C time", attrCtime, "A time", attrAtime,
                "M time", attrMtime, "R time", attrRtime,"Version nbr", versionNumber, "Class id", classId);
    }

    /**
     *
     * @return
     */
    public String getAttributeName() {
        return "Standard";
    }
}
