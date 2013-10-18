package com.company;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: visti
 * Date: 05/05/2013
 * Time: 19:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class AttrLocation {
    protected MftRecord parent;

    abstract public long getLong(long offset) throws CreatorException;

    abstract public int getInt(long offset) throws CreatorException;

    abstract public short getShort(long offset) throws CreatorException;

    abstract public byte getByte(long offset) throws CreatorException;

    abstract public String getString(long offset, int len) throws CreatorException;

    abstract public long getAttrLength();

    abstract public ByteBuffer readData(long offset, int len);

}
