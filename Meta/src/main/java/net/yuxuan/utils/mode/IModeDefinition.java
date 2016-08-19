package net.yuxuan.utils.mode;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

public interface IModeDefinition<M extends IMode> {
    String getName();

    IMode getDefaultMode();

    Collection<IProperty<? extends Comparable<?>>> getProperties();

    ImmutableList<M> getModeList();
}
