package net.yuxuan.utils.mode;

public abstract class PropertyBase<V extends Comparable<?>> implements IProperty<V> {
    private final String name;
    private final Class<V> valueClass;

    protected PropertyBase(String name, Class<V> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<V> getValueClass() {
        return valueClass;
    }
}
