package generaloss.resourceflow.stream;

import generaloss.rawlist.*;
import generaloss.spatialmath.EulerAngles;
import generaloss.spatialmath.Quaternion;
import generaloss.spatialmath.vector.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.util.UUID;

public class BinaryInputStream extends DataInputStream {
    
    public BinaryInputStream(InputStream in) {
        super(in);
    }

    public BinaryInputStream(byte[] bytes) {
        super(new ByteArrayInputStream(bytes));
    }


    public String readByteString() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        final byte[] bytes = this.readBytes(length);
        return new String(bytes);
    }

    public String readCharString() throws IOException {
        return new String(this.readCharArray());
    }

    public String readUTFString() throws IOException {
        return super.readUTF();
    }


    public byte[] readBytes(int length) throws IOException {
        return super.readNBytes(length);
    }

    public short[] readShorts(int length) throws IOException {
        final short[] array = new short[length];
        for(int i = 0; i < array.length; i++)
            array[i] = super.readShort();
        return array;
    }

    public int[] readInts(int length) throws IOException {
        final int[] array = new int[length];
        for(int i = 0; i < array.length; i++)
            array[i] = super.readInt();
        return array;
    }

    public long[] readLongs(int length) throws IOException {
        final long[] array = new long[length];
        for(int i = 0; i < array.length; i++)
            array[i] = super.readLong();
        return array;
    }

    public float[] readFloats(int length) throws IOException {
        final float[] array = new float[length];
        for(int i = 0; i < array.length; i++)
            array[i] = super.readFloat();
        return array;
    }

    public double[] readDoubles(int length) throws IOException {
        final double[] array = new double[length];
        for(int i = 0; i < array.length; i++)
            array[i] = super.readDouble();
        return array;
    }

    public boolean[] readBools(int length) throws IOException {
        final boolean[] array = new boolean[length];
        for(int i = 0; i < array.length; i++)
            array[i] = super.readBoolean();
        return array;
    }

    public char[] readChars(int length) throws IOException {
        final char[] array = new char[length];
        for(int i = 0; i < array.length; i++)
            array[i] = super.readChar();
        return array;
    }

    public String[] readByteStrings(int length) throws IOException {
        final String[] array = new String[length];
        for(int i = 0; i < array.length; i++)
            array[i] = this.readByteString();
        return array;
    }

    public String[] readCharStrings(int length) throws IOException {
        final String[] array = new String[length];
        for(int i = 0; i < array.length; i++)
            array[i] = this.readCharString();
        return array;
    }

    public String[] readUTFStrings(int length) throws IOException {
        final String[] array = new String[length];
        for(int i = 0; i < array.length; i++)
            array[i] = this.readUTFString();
        return array;
    }


    public byte[] readByteArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readBytes(length);
    }

    public short[] readShortArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readShorts(length);
    }

    public int[] readIntArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readInts(length);
    }

    public long[] readLongArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readLongs(length);
    }

    public float[] readFloatArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readFloats(length);
    }

    public double[] readDoubleArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readDoubles(length);
    }

    public boolean[] readBoolArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readBools(length);
    }

    public char[] readCharArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readChars(length);
    }

    public String[] readByteStringArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readByteStrings(length);
    }

    public String[] readCharStringArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readCharStrings(length);
    }

    public String[] readUTFStringArray() throws IOException {
        final int length = super.readInt();
        if(length < 0)
            return null;

        return this.readUTFStrings(length);
    }


    public ByteBuffer readDirectByteBuffer() throws IOException {
        final byte[] array = this.readByteArray();
        if(array == null)
            return null;

        final ByteBuffer buffer = ByteBuffer.allocateDirect(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public ByteBuffer readByteBuffer() throws IOException {
        final byte[] array = this.readByteArray();
        if(array == null)
            return null;

        final ByteBuffer buffer = ByteBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public ShortBuffer readShortBuffer() throws IOException {
        final short[] array = this.readShortArray();
        if(array == null)
            return null;

        final ShortBuffer buffer = ShortBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public IntBuffer readIntBuffer() throws IOException {
        final int[] array = this.readIntArray();
        if(array == null)
            return null;

        final IntBuffer buffer = IntBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public LongBuffer readLongBuffer() throws IOException {
        final long[] array = this.readLongArray();
        if(array == null)
            return null;

        final LongBuffer buffer = LongBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public FloatBuffer readFloatBuffer() throws IOException {
        final float[] array = this.readFloatArray();
        if(array == null)
            return null;

        final FloatBuffer buffer = FloatBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public DoubleBuffer readDoubleBuffer() throws IOException {
        final double[] array = this.readDoubleArray();
        if(array == null)
            return null;

        final DoubleBuffer buffer = DoubleBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public CharBuffer readCharBuffer() throws IOException {
        final char[] array = this.readCharArray();
        if(array == null)
            return null;

        final CharBuffer buffer = CharBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }


    public BoolList readBoolList() throws IOException {
        final boolean[] array = this.readBoolArray();
        if(array == null)
            return null;

        return new BoolList(array);
    }

    public ByteList readByteList() throws IOException {
        final byte[] array = this.readByteArray();
        if(array == null)
            return null;

        return new ByteList(array);
    }

    public CharList readCharList() throws IOException {
        final char[] array = this.readCharArray();
        if(array == null)
            return null;

        return new CharList(array);
    }

    public DoubleList readDoubleList() throws IOException {
        final double[] array = this.readDoubleArray();
        if(array == null)
            return null;

        return new DoubleList(array);
    }

    public FloatList readFloatList() throws IOException {
        final float[] array = this.readFloatArray();
        if(array == null)
            return null;

        return new FloatList(array);
    }

    public IntList readIntList() throws IOException {
        final int[] array = this.readIntArray();
        if(array == null)
            return null;

        return new IntList(array);
    }

    public LongList readLongList() throws IOException {
        final long[] array = this.readLongArray();
        if(array == null)
            return null;

        return new LongList(array);
    }

    public ShortList readShortList() throws IOException {
        final short[] array = this.readShortArray();
        if(array == null)
            return null;

        return new ShortList(array);
    }

    public StringList readByteStringList() throws IOException {
        final String[] array = this.readByteStringArray();
        if(array == null)
            return null;

        return new StringList(array);
    }

    public StringList readCharStringList() throws IOException {
        final String[] array = this.readCharStringArray();
        if(array == null)
            return null;

        return new StringList(array);
    }

    public StringList readUTFStringList() throws IOException {
        final String[] array = this.readUTFStringArray();
        if(array == null)
            return null;

        return new StringList(array);
    }


    public BoolList readBoolList(BoolList dst) throws IOException {
        final boolean[] array = this.readBoolArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public ByteList readByteList(ByteList dst) throws IOException {
        final byte[] array = this.readByteArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public CharList readCharList(CharList dst) throws IOException {
        final char[] array = this.readCharArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public DoubleList readDoubleList(DoubleList dst) throws IOException {
        final double[] array = this.readDoubleArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public FloatList readFloatList(FloatList dst) throws IOException {
        final float[] array = this.readFloatArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public IntList readIntList(IntList dst) throws IOException {
        final int[] array = this.readIntArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public LongList readLongList(LongList dst) throws IOException {
        final long[] array = this.readLongArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public ShortList readShortList(ShortList dst) throws IOException {
        final short[] array = this.readShortArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public StringList readByteStringList(StringList dst) throws IOException {
        final String[] array = this.readByteStringArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public StringList readCharStringList(StringList dst) throws IOException {
        final String[] array = this.readCharStringArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }

    public StringList readUTFStringList(StringList dst) throws IOException {
        final String[] array = this.readUTFStringArray();
        if(array == null)
            return null;

        return dst.clear().add(array);
    }


    public Vec2i readVec2i(Vec2i dst) throws IOException {
        return dst.set(
            super.readInt(),
            super.readInt()
        );
    }

    public Vec2f readVec2f(Vec2f dst) throws IOException {
        return dst.set(
            super.readFloat(),
            super.readFloat()
        );
    }

    public Vec2d readVec2d(Vec2d dst) throws IOException {
        return dst.set(
            super.readDouble(),
            super.readDouble()
        );
    }
    
    public Vec3i readVec3i(Vec3i dst) throws IOException {
        return dst.set(
            super.readInt(),
            super.readInt(),
            super.readInt()
        );
    }
    
    public Vec3f readVec3f(Vec3f dst) throws IOException {
        return dst.set(
            super.readFloat(),
            super.readFloat(),
            super.readFloat()
        );
    }

    public Vec3d readVec3d(Vec3d dst) throws IOException {
        return dst.set(
            super.readDouble(),
            super.readDouble(),
            super.readDouble()
        );
    }

    public Vec4i readVec4i(Vec4i dst) throws IOException {
        return dst.set(
            super.readInt(),
            super.readInt(),
            super.readInt(),
            super.readInt()
        );
    }

    public Vec4f readVec4f(Vec4f dst) throws IOException {
        return dst.set(
            super.readFloat(),
            super.readFloat(),
            super.readFloat(),
            super.readFloat()
        );
    }

    public Vec4d readVec4d(Vec4d dst) throws IOException {
        return dst.set(
            super.readDouble(),
            super.readDouble(),
            super.readDouble(),
            super.readDouble()
        );
    }


    public Vec2i readVec2i() throws IOException {
        return this.readVec2i(new Vec2i());
    }

    public Vec2f readVec2f() throws IOException {
        return this.readVec2f(new Vec2f());
    }

    public Vec2d readVec2d() throws IOException {
        return this.readVec2d(new Vec2d());
    }

    public Vec3i readVec3i() throws IOException {
        return this.readVec3i(new Vec3i());
    }

    public Vec3f readVec3f() throws IOException {
        return this.readVec3f(new Vec3f());
    }

    public Vec3d readVec3d() throws IOException {
        return this.readVec3d(new Vec3d());
    }

    public Vec4i readVec4i() throws IOException {
        return this.readVec4i(new Vec4i());
    }

    public Vec4f readVec4f() throws IOException {
        return this.readVec4f(new Vec4f());
    }

    public Vec4d readVec4d() throws IOException {
        return this.readVec4d(new Vec4d());
    }


    public EulerAngles readEulerAngles(EulerAngles dst) throws IOException {
        return dst.set(
            super.readFloat(),
            super.readFloat(),
            super.readFloat()
        );
    }

    public EulerAngles readEulerAngles() throws IOException {
        return this.readEulerAngles(new EulerAngles());
    }


    public Quaternion readQuaternion(Quaternion dst) throws IOException {
        return dst.set(
            super.readFloat(),
            super.readFloat(),
            super.readFloat(),
            super.readFloat()
        );
    }

    public Quaternion readQuaternion() throws IOException {
        return this.readQuaternion(new Quaternion());
    }


    public UUID readUUID() throws IOException {
        return new UUID(super.readLong(), super.readLong());
    }
    
}
