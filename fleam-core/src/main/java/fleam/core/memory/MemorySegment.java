package fleam.core.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public abstract class MemorySegment {
    protected static final Logger LOG = LoggerFactory.getLogger(MemorySegment.class);

    protected static final Unsafe UNSAFE = MemoryUtils.UNSAFE;

    protected static final long BYTE_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    protected static final Boolean IS_LITTLE_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

    // --------------------------------------------------------------------------------------------

    protected final byte[] heapMemory;

    protected long address;

    protected final long addressLimit;

    protected final int size;

    protected final Object owner;


    MemorySegment(byte[] buffer, Object owner){
        this.heapMemory = buffer;
        this.address = BYTE_ARRAY_BASE_OFFSET;
        this.size = buffer.length;
        this.addressLimit = this.address + this.size;
        this.owner = owner;
    }

    MemorySegment(long offHeapAddress, int size, Object owner){
        this.heapMemory = null;
        this.address = offHeapAddress;
        this.size = size;
        this.addressLimit = this.address + this.size;
        this.owner = owner;
    }

    public void free(){
        address = addressLimit + 1;
    }

    public abstract ByteBuffer wrap(int offset, int length);

    public abstract byte get(int index);

    public abstract void put(int index, byte b);

    /**
     * get to
     *
     * get data from this to target
     * @param index
     * @param dst
     */

    public abstract void get(int index, byte[] dst);

    /**
     * put from
     *
     * put data from src to this
     * @param index
     * @param src
     */
    public abstract void put(int index, byte[] src);

    public abstract void get(int index, byte[] dst, int offset, int length);

    public abstract void put(int index, byte[] src, int offset, int length);


    /**
     * Reads one byte at the given position and returns its boolean
     * representation.
     *
     * @param index The position from which the memory will be read.
     * @return The boolean value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 1.
     */
    public abstract boolean getBoolean(int index);

    /**
     * Writes one byte containing the byte value into this buffer at the given
     * position.
     *
     * @param index The position at which the memory will be written.
     * @param value The char value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 1.
     */
    public abstract void putBoolean(int index, boolean value);

    /**
     * Reads a char value from the given position, in the system's native byte order.
     *
     * @param index The position from which the memory will be read.
     * @return The char value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 2.
     */
    @SuppressWarnings("restriction")
    public final char getChar(int index) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 2) {
            return UNSAFE.getChar(heapMemory, pos);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("This segment has been freed.");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Reads an character value (16 bit, 2 bytes) from the given position, in little-endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getChar(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getChar(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The character value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final char getCharLittleEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return getChar(index);
        } else {
            return Character.reverseBytes(getChar(index));
        }
    }

    /**
     * Reads an character value (16 bit, 2 bytes) from the given position, in big-endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getChar(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getChar(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The character value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final char getCharBigEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return Character.reverseBytes(getChar(index));
        } else {
            return getChar(index);
        }
    }

    /**
     * Writes a char value to teh given position, in the system's native byte order.
     *
     * @param index The position at which the memory will be written.
     * @param value The char value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 2.
     */
    @SuppressWarnings("restriction")
    public final void putChar(int index, char value) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 2) {
            UNSAFE.putChar(heapMemory, pos, value);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("segment has been freed");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Writes the given character (16 bit, 2 bytes) to the given position in little-endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putChar(int, char)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putChar(int, char)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The short value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final void putCharLittleEndian(int index, char value) {
        if (IS_LITTLE_ENDIAN) {
            putChar(index, value);
        } else {
            putChar(index, Character.reverseBytes(value));
        }
    }

    /**
     * Writes the given character (16 bit, 2 bytes) to the given position in big-endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putChar(int, char)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putChar(int, char)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The short value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final void putCharBigEndian(int index, char value) {
        if (IS_LITTLE_ENDIAN) {
            putChar(index, Character.reverseBytes(value));
        } else {
            putChar(index, value);
        }
    }

    /**
     * Reads two memory at the given position, composing them into a short value
     * according to the current byte order.
     *
     * @param index The position from which the memory will be read.
     * @return The short value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 2.
     */
    public final short getShort(int index) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 2) {
            return UNSAFE.getShort(heapMemory, pos);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("segment has been freed");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Reads an short integer value (16 bit, 2 bytes) from the given position, in little-endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getShort(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getShort(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The short value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final short getShortLittleEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return getShort(index);
        } else {
            return Short.reverseBytes(getShort(index));
        }
    }

    /**
     * Reads an short integer value (16 bit, 2 bytes) from the given position, in big-endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getShort(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getShort(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The short value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final short getShortBigEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return Short.reverseBytes(getShort(index));
        } else {
            return getShort(index);
        }
    }

    /**
     * Writes the given short value into this buffer at the given position, using
     * the native byte order of the system.
     *
     * @param index The position at which the value will be written.
     * @param value The short value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 2.
     */
    public final void putShort(int index, short value) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 2) {
            UNSAFE.putShort(heapMemory, pos, value);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("segment has been freed");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Writes the given short integer value (16 bit, 2 bytes) to the given position in little-endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putShort(int, short)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putShort(int, short)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The short value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final void putShortLittleEndian(int index, short value) {
        if (IS_LITTLE_ENDIAN) {
            putShort(index, value);
        } else {
            putShort(index, Short.reverseBytes(value));
        }
    }

    /**
     * Writes the given short integer value (16 bit, 2 bytes) to the given position in big-endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putShort(int, short)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putShort(int, short)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The short value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment size minus 2.
     */
    public final void putShortBigEndian(int index, short value) {
        if (IS_LITTLE_ENDIAN) {
            putShort(index, Short.reverseBytes(value));
        } else {
            putShort(index, value);
        }
    }

    /**
     * Reads an int value (32bit, 4 bytes) from the given position, in the system's native byte order.
     * This method offers the best speed for integer reading and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The int value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final int getInt(int index) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 4) {
            return UNSAFE.getInt(heapMemory, pos);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("segment has been freed");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Reads an int value (32bit, 4 bytes) from the given position, in little-endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getInt(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getInt(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The int value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final int getIntLittleEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return getInt(index);
        } else {
            return Integer.reverseBytes(getInt(index));
        }
    }

    /**
     * Reads an int value (32bit, 4 bytes) from the given position, in big-endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getInt(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getInt(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The int value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final int getIntBigEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return Integer.reverseBytes(getInt(index));
        } else {
            return getInt(index);
        }
    }

    /**
     * Writes the given int value (32bit, 4 bytes) to the given position in the system's native
     * byte order. This method offers the best speed for integer writing and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The int value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final void putInt(int index, int value) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 4) {
            UNSAFE.putInt(heapMemory, pos, value);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("segment has been freed");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Writes the given int value (32bit, 4 bytes) to the given position in little endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putInt(int, int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putInt(int, int)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The int value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final void putIntLittleEndian(int index, int value) {
        if (IS_LITTLE_ENDIAN) {
            putInt(index, value);
        } else {
            putInt(index, Integer.reverseBytes(value));
        }
    }

    /**
     * Writes the given int value (32bit, 4 bytes) to the given position in big endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putInt(int, int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putInt(int, int)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The int value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final void putIntBigEndian(int index, int value) {
        if (IS_LITTLE_ENDIAN) {
            putInt(index, Integer.reverseBytes(value));
        } else {
            putInt(index, value);
        }
    }

    /**
     * Reads a long value (64bit, 8 bytes) from the given position, in the system's native byte order.
     * This method offers the best speed for long integer reading and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The long value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final long getLong(int index) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 8) {
            return UNSAFE.getLong(heapMemory, pos);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("segment has been freed");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Reads a long integer value (64bit, 8 bytes) from the given position, in little endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getLong(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getLong(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The long value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final long getLongLittleEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return getLong(index);
        } else {
            return Long.reverseBytes(getLong(index));
        }
    }


    /**
     * Writes the given long value (64bit, 8 bytes) to the given position in the system's native
     * byte order. This method offers the best speed for long integer writing and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The long value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putLong(int index, long value) {
        final long pos = address + index;
        if (index >= 0 && pos <= addressLimit - 8) {
            UNSAFE.putLong(heapMemory, pos, value);
        }
        else if (address > addressLimit) {
            throw new IllegalStateException("segment has been freed");
        }
        else {
            // index is in fact invalid
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Writes the given long value (64bit, 8 bytes) to the given position in little endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putLong(int, long)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putLong(int, long)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The long value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putLongLittleEndian(int index, long value) {
        if (IS_LITTLE_ENDIAN) {
            putLong(index, value);
        } else {
            putLong(index, Long.reverseBytes(value));
        }
    }

    /**
     * Writes the given long value (64bit, 8 bytes) to the given position in big endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putLong(int, long)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putLong(int, long)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The long value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putLongBigEndian(int index, long value) {
        if (IS_LITTLE_ENDIAN) {
            putLong(index, Long.reverseBytes(value));
        } else {
            putLong(index, value);
        }
    }

    /**
     * Reads a single-precision floating point value (32bit, 4 bytes) from the given position, in the system's
     * native byte order. This method offers the best speed for float reading and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The float value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    /**
     * Reads a single-precision floating point value (32bit, 4 bytes) from the given position, in little endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getFloat(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getFloat(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The long value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final float getFloatLittleEndian(int index) {
        return Float.intBitsToFloat(getIntLittleEndian(index));
    }

    /**
     * Reads a single-precision floating point value (32bit, 4 bytes) from the given position, in big endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getFloat(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getFloat(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The long value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final float getFloatBigEndian(int index) {
        return Float.intBitsToFloat(getIntBigEndian(index));
    }

    /**
     * Writes the given single-precision float value (32bit, 4 bytes) to the given position in the system's native
     * byte order. This method offers the best speed for float writing and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The float value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 4.
     */
    public final void putFloat(int index, float value) {
        putInt(index, Float.floatToRawIntBits(value));
    }

    /**
     * Writes the given single-precision float value (32bit, 4 bytes) to the given position in little endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putFloat(int, float)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putFloat(int, float)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The long value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putFloatLittleEndian(int index, float value) {
        putIntLittleEndian(index, Float.floatToRawIntBits(value));
    }

    /**
     * Writes the given single-precision float value (32bit, 4 bytes) to the given position in big endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putFloat(int, float)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putFloat(int, float)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The long value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putFloatBigEndian(int index, float value) {
        putIntBigEndian(index, Float.floatToRawIntBits(value));
    }

    /**
     * Reads a double-precision floating point value (64bit, 8 bytes) from the given position, in the system's
     * native byte order. This method offers the best speed for double reading and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The double value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    /**
     * Reads a double-precision floating point value (64bit, 8 bytes) from the given position, in little endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getDouble(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getDouble(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The long value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final double getDoubleLittleEndian(int index) {
        return Double.longBitsToDouble(getLongLittleEndian(index));
    }

    /**
     * Reads a double-precision floating point value (64bit, 8 bytes) from the given position, in big endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getDouble(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getDouble(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The long value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final double getDoubleBigEndian(int index) {
        return Double.longBitsToDouble(getLongBigEndian(index));
    }

    /**
     * Writes the given double-precision floating-point value (64bit, 8 bytes) to the given position in the
     * system's native byte order. This method offers the best speed for double writing and should be used
     * unless a specific byte order is required. In most cases, it suffices to know that the
     * byte order in which the value is written is the same as the one in which it is read
     * (such as transient storage in memory, or serialization for I/O and network), making this
     * method the preferable choice.
     *
     * @param index The position at which the memory will be written.
     * @param value The double value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putDouble(int index, double value) {
        putLong(index, Double.doubleToRawLongBits(value));
    }

    /**
     * Writes the given double-precision floating-point value (64bit, 8 bytes) to the given position in little endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putDouble(int, double)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putDouble(int, double)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The long value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putDoubleLittleEndian(int index, double value) {
        putLongLittleEndian(index, Double.doubleToRawLongBits(value));
    }

    /**
     * Writes the given double-precision floating-point value (64bit, 8 bytes) to the given position in big endian
     * byte order. This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #putDouble(int, double)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #putDouble(int, double)} is the preferable choice.
     *
     * @param index The position at which the value will be written.
     * @param value The long value to be written.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final void putDoubleBigEndian(int index, double value) {
        putLongBigEndian(index, Double.doubleToRawLongBits(value));
    }

    /**
     * Reads a long integer value (64bit, 8 bytes) from the given position, in big endian byte order.
     * This method's speed depends on the system's native byte order, and it
     * is possibly slower than {@link #getLong(int)}. For most cases (such as
     * transient storage in memory or serialization for I/O and network),
     * it suffices to know that the byte order in which the value is written is the same as the
     * one in which it is read, and {@link #getLong(int)} is the preferable choice.
     *
     * @param index The position from which the value will be read.
     * @return The long value at the given position.
     *
     * @throws IndexOutOfBoundsException Thrown, if the index is negative, or larger then the segment
     *                                   size minus 8.
     */
    public final long getLongBigEndian(int index) {
        if (IS_LITTLE_ENDIAN) {
            return Long.reverseBytes(getLong(index));
        } else {
            return getLong(index);
        }
    }


    // -------------------------------------------------------------------------
    //                     Bulk Read and Write Methods
    // -------------------------------------------------------------------------

    public abstract void get(DataOutput out, int offset, int length) throws IOException;

    /**
     * Bulk put method. Copies length memory from the given DataInput to the
     * memory starting at position offset.
     *
     * @param in The DataInput to get the data from.
     * @param offset The position in the memory segment to copy the chunk to.
     * @param length The number of bytes to get.
     *
     * @throws IOException Thrown, if the DataInput encountered a problem upon reading,
     *                     such as an End-Of-File.
     */
    public abstract void put(DataInput in, int offset, int length) throws IOException;

    /**
     * Bulk get method. Copies {@code numBytes} bytes from this memory segment, starting at position
     * {@code offset} to the target {@code ByteBuffer}. The bytes will be put into the target buffer
     * starting at the buffer's current position. If this method attempts to write more bytes than
     * the target byte buffer has remaining (with respect to {@link ByteBuffer#remaining()}),
     * this method will cause a {@link java.nio.BufferOverflowException}.
     *
     * @param offset The position where the bytes are started to be read from in this memory segment.
     * @param target The ByteBuffer to copy the bytes to.
     * @param numBytes The number of bytes to copy.
     *
     * @throws IndexOutOfBoundsException If the offset is invalid, or this segment does not
     *           contain the given number of bytes (starting from offset), or the target byte buffer does
     *           not have enough space for the bytes.
     * @throws ReadOnlyBufferException If the target buffer is read-only.
     */
    public abstract void get(int offset, ByteBuffer target, int numBytes);

    /**
     * Bulk put method. Copies {@code numBytes} bytes from the given {@code ByteBuffer}, into
     * this memory segment. The bytes will be read from the target buffer
     * starting at the buffer's current position, and will be written to this memory segment starting
     * at {@code offset}.
     * If this method attempts to read more bytes than
     * the target byte buffer has remaining (with respect to {@link ByteBuffer#remaining()}),
     * this method will cause a {@link java.nio.BufferUnderflowException}.
     *
     * @param offset The position where the bytes are started to be written to in this memory segment.
     * @param source The ByteBuffer to copy the bytes from.
     * @param numBytes The number of bytes to copy.
     *
     * @throws IndexOutOfBoundsException If the offset is invalid, or the source buffer does not
     *           contain the given number of bytes, or this segment does
     *           not have enough space for the bytes (counting from offset).
     */
    public abstract void put(int offset, ByteBuffer source, int numBytes);

    /**
     * Bulk copy method. Copies {@code numBytes} bytes from this memory segment, starting at position
     * {@code offset} to the target memory segment. The bytes will be put into the target segment
     * starting at position {@code targetOffset}.
     *
     * @param offset The position where the bytes are started to be read from in this memory segment.
     * @param target The memory segment to copy the bytes to.
     * @param targetOffset The position in the target memory segment to copy the chunk to.
     * @param numBytes The number of bytes to copy.
     *
     * @throws IndexOutOfBoundsException If either of the offsets is invalid, or the source segment does not
     *           contain the given number of bytes (starting from offset), or the target segment does
     *           not have enough space for the bytes (counting from targetOffset).
     */
    public final void copyTo(int offset, MemorySegment target, int targetOffset, int numBytes) {
        final byte[] thisHeapRef = this.heapMemory;
        final byte[] otherHeapRef = target.heapMemory;
        final long thisPointer = this.address + offset;
        final long otherPointer = target.address + targetOffset;

        if ((numBytes | offset | targetOffset) >= 0 &&
                thisPointer <= this.addressLimit - numBytes && otherPointer <= target.addressLimit - numBytes) {
            UNSAFE.copyMemory(thisHeapRef, thisPointer, otherHeapRef, otherPointer, numBytes);
        }
        else if (this.address > this.addressLimit) {
            throw new IllegalStateException("this memory segment has been freed.");
        }
        else if (target.address > target.addressLimit) {
            throw new IllegalStateException("target memory segment has been freed.");
        }
        else {
            throw new IndexOutOfBoundsException(
                    String.format("offset=%d, targetOffset=%d, numBytes=%d, address=%d, targetAddress=%d",
                            offset, targetOffset, numBytes, this.address, target.address));
        }
    }

    // -------------------------------------------------------------------------
    //                      Comparisons & Swapping
    // -------------------------------------------------------------------------

    /**
     * Compares two memory segment regions.
     *
     * @param seg2 Segment to compare this segment with
     * @param offset1 Offset of this segment to start comparing
     * @param offset2 Offset of seg2 to start comparing
     * @param len Length of the compared memory region
     *
     * @return 0 if equal, -1 if seg1 &lt; seg2, 1 otherwise
     */
    public final int compare(MemorySegment seg2, int offset1, int offset2, int len) {
        while (len >= 8) {
            long l1 = this.getLongBigEndian(offset1);
            long l2 = seg2.getLongBigEndian(offset2);

            if (l1 != l2) {
                return (l1 < l2) ^ (l1 < 0) ^ (l2 < 0) ? -1 : 1;
            }

            offset1 += 8;
            offset2 += 8;
            len -= 8;
        }
        while (len > 0) {
            int b1 = this.get(offset1) & 0xff;
            int b2 = seg2.get(offset2) & 0xff;
            int cmp = b1 - b2;
            if (cmp != 0) {
                return cmp;
            }
            offset1++;
            offset2++;
            len--;
        }
        return 0;
    }

    /**
     * Swaps bytes between two memory segments, using the given auxiliary buffer.
     *
     * @param tempBuffer The auxiliary buffer in which to put data during triangle swap.
     * @param seg2 Segment to swap bytes with
     * @param offset1 Offset of this segment to start swapping
     * @param offset2 Offset of seg2 to start swapping
     * @param len Length of the swapped memory region
     */
    public final void swapBytes(byte[] tempBuffer, MemorySegment seg2, int offset1, int offset2, int len) {
        if ((offset1 | offset2 | len | (tempBuffer.length - len)) >= 0) {
            final long thisPos = this.address + offset1;
            final long otherPos = seg2.address + offset2;

            if (thisPos <= this.addressLimit - len && otherPos <= seg2.addressLimit - len) {
                // this -> temp buffer
                UNSAFE.copyMemory(this.heapMemory, thisPos, tempBuffer, BYTE_ARRAY_BASE_OFFSET, len);

                // other -> this
                UNSAFE.copyMemory(seg2.heapMemory, otherPos, this.heapMemory, thisPos, len);

                // temp buffer -> other
                UNSAFE.copyMemory(tempBuffer, BYTE_ARRAY_BASE_OFFSET, seg2.heapMemory, otherPos, len);
                return;
            }
            else if (this.address > this.addressLimit) {
                throw new IllegalStateException("this memory segment has been freed.");
            }
            else if (seg2.address > seg2.addressLimit) {
                throw new IllegalStateException("other memory segment has been freed.");
            }
        }

        // index is in fact invalid
        throw new IndexOutOfBoundsException(
                String.format("offset1=%d, offset2=%d, len=%d, bufferSize=%d, address1=%d, address2=%d",
                        offset1, offset2, len, tempBuffer.length, this.address, seg2.address));
    }

}
