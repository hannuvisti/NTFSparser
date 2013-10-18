package com.company;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
public class BufferTemp {
    public static void printBuffer(ByteBuffer bb)  {

	/*
	try {
	    Charset charset = Charset.defaultCharset();
	    CharsetDecoder decoder = charset.newDecoder();
	    CharBuffer charBuffer = decoder.decode(bb);
	    String s = charBuffer.toString();
	    System.out.println(s.length());
	}
	catch (CharacterCodingException e) {
	    System.out.println("exception: "+ e.getMessage());
	    } */
        bb.flip();
        while (bb.hasRemaining())
            System.out.print((char) bb.get());
    }
    public static void hexDump(ByteBuffer bb) {
        int i = 0;
        int cuml = 0;
        byte b;
        char[] c = new char[16];


        bb.flip();

        System.out.format("%4d:", cuml);
        while (bb.hasRemaining()) {
            b = bb.get();
            System.out.format("%02x ", b);
            if (b >= ' ' && b <= 'z')
                c[i] = (char) b;
            else
                c[i] = '.';

            if (i++ == 15)  {
                String s = new String(c);
                cuml += 16;
                System.out.format(" | %s\n%4d:", s,cuml);
                i = 0;


            }
        }
        int fillerLength = (16-i) * 3+2;

        System.out.format("%"+fillerLength+"s %s\n", "|" , new String(Arrays.copyOfRange(c, 0, i)));
    }

    public static String getString(ByteBuffer bb, int offset, int len) {
        try {
            byte[] rArr = new byte[len*2];
            byte[] sArr = Arrays.copyOfRange(bb.array(), offset, offset + (len*2));
            /* Swap little-endian to big-endian unicode */
            for (int i=0;i < len*2;i += 2) {
                rArr[i+1] = sArr[i];
                rArr[i] = sArr[i+1];
            }

            String q = new String(rArr);

            return q;
        }
        catch (BufferUnderflowException w)  {
            return "";
        }
    }

}

