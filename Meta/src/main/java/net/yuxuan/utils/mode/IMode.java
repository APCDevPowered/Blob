package net.yuxuan.utils.mode;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;

public interface IMode {
    Collection<IProperty<? extends Comparable<?>>> getPropertyNames();

    <V extends Comparable<?>> V getValue(IProperty<V> property);

    <V extends Comparable<?>> IMode withProperty(IProperty<V> property, V value);

    ImmutableMap<IProperty<? extends Comparable<?>>, ? extends Comparable<?>> getProperties();

    String getName();

    ModeDefinition getModeDefinition();
}
