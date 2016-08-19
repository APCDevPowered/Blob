package net.yuxuan.utils.mode;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

public class PropertyNaked extends PropertyBase<PropertyNaked.Naked> {
    private final ImmutableSet<Naked> allowedValues = ImmutableSet.of();

    public static class Naked implements Comparable<Naked> {
        private Naked() {

        }

        @Override
        public int compareTo(Naked o) {
            throw new UnsupportedOperationException();
        }
    }

    public PropertyNaked(String name) {
        super(name, Naked.class);
    }

    @Override
    public Collection<Naked> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public String getName(Naked value) {
        throw new UnsupportedOperationException();
    }
}
