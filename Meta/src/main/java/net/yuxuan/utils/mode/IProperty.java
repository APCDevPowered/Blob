package net.yuxuan.utils.mode;

import java.util.Collection;

public interface IProperty<V extends Comparable<?>> {
    String getName();

    Collection<V> getAllowedValues();

    Class<V> getValueClass();

    String getName(V value);
}
