package e33.guardy.structure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class IntMap {
    final static Logger LOGGER = LogManager.getLogger();
    public static final int NO_VALUE = -1;

    private int[] keys;
    private int[] values;
    private int maxSize;
    private int currentSize;
    private boolean noRewrite;

    public IntMap(int size, boolean noRewrite) {
        this.keys = new int[size];
        this.values = new int[size];
        this.maxSize = size;
        this.currentSize = 0;
        this.noRewrite = noRewrite;
    }

    public int get(int key) {
        int index = this.getIndexOfKey(key);
        if (index >= 0) {
            return this.values[index];
        }

        return NO_VALUE;
    }

    public void put(int key, int value) {
        boolean sizeChanged = false;
        int index = this.currentSize;

        if (!this.noRewrite) {
            index = this.getIndexOfKey(key);
            if (index <= 0) {
                index = this.currentSize;
                sizeChanged = true;
            }
        }

        this.keys[index] = key;
        this.values[index] = value;
        if (sizeChanged) {
            this.currentSize++;
        }

        if (this.currentSize >= this.maxSize) {
            this.rehash();
        }
    }

    private int getIndexOfKey(int key) {
        return Arrays.binarySearch(this.keys, key);
    }

    private void rehash() {
        int newSize = this.maxSize * 2;
        LOGGER.info(newSize);
        this.maxSize = newSize;

        int[] oldKeys = this.keys;
        int[] oldValues = this.values;

        this.keys = new int[newSize];
        this.values = new int[newSize];
        System.arraycopy(oldKeys, 0, this.keys, 0, oldKeys.length);
        System.arraycopy(oldValues, 0, this.values, 0, oldValues.length);
    }
}
