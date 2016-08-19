package net.yuxuan.utils.mode;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

public class PropertyBoolean extends PropertyBase<Boolean> {
    private final ImmutableSet<Boolean> allowedValues = ImmutableSet.of(Boolean.FALSE, Boolean.TRUE);

    public PropertyBoolean(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Collection<Boolean> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public String getName(Boolean value) {
        return value.toString();
    }
}
