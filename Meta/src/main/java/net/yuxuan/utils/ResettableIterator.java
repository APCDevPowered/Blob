package net.yuxuan.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ResettableIterator<E> implements Iterator<E> {

    private final Iterator<E> innerIterator;
    private final List<E> cache;
    private int cacheIndex = -1;

    private ResettableIterator(Iterator<E> iterator) {
        innerIterator = iterator;
        cache = new ArrayList<E>();
    }

    @Override
    public boolean hasNext() {
        if (cacheIndex != -1) {
            if (cacheIndex < cache.size()) {
                return true;
            } else {
                cacheIndex = -1;
            }
        }
        return innerIterator.hasNext();
    }

    @Override
    public E next() {
        if (cacheIndex != -1) {
            if (cacheIndex < cache.size()) {
                return cache.get(cacheIndex++);
            } else {
                cacheIndex = -1;
            }
        }
        E e = innerIterator.next();
        cache.add(e);
        return e;
    }

    public void reset() {
        if(cache.size() != 0) {
            cacheIndex = 0;
        }
    }

    public static <E> ResettableIterator<E> warp(Iterator<E> iterator) {
        return new ResettableIterator<E>(iterator);
    }
}
