package com.company;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import static java.lang.Math.*;
import java.util.*;

public class Main {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        FileChannel fc = null;
        RandomAccessFile raf = null;
        //StringBuilder sb;

        if (args.length != 1) {
            System.out.println("Usage: Ntfs filename");
            System.exit(1);
        }
        /*
        sb = new StringBuilder();
        int[] foo = {129,4,229,33};
        for (int b: foo) {
            sb.insert(0,String.format("%02X", b));
        }
        System.out.println(sb.toString());
        System.exit(0);
        */
        try {
            raf = new RandomAccessFile(args[0], "r");
            fc = raf.getChannel();
            Filesystem fs = new Filesystem(fc);


            fs.demo();
            //fs.displayFs();
        }
        catch (FileNotFoundException x) {
            System.out.println("FNF exp: "+x.getMessage());
        }
        catch (IOException x) {
            System.out.println("IO exp: "+x.getMessage());
        }
        finally {
            if (raf != null)
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
        }
    }

}
