package net.yuxuan.utils.mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import net.yuxuan.utils.ResettableIterator;
import net.yuxuan.utils.mode.ModeDefinition.Mode;

public class ModeDefinition implements IModeDefinition<Mode> {
	private static final PropertyNaked NAKED_PROPERTY = new PropertyNaked("NAKED_PROPERTY");

	private final String name;
	private final ImmutableList<IProperty<? extends Comparable<?>>> propertyList;
	private final ImmutableList<? extends Mode> modeList;

	public ModeDefinition(String name) {
		this(name, NAKED_PROPERTY);
	}

	@SafeVarargs
	public ModeDefinition(String name, IProperty<? extends Comparable<?>>... propertyList) {
		this.name = name;
		if (propertyList.length <= 0) {
			throw new IllegalStateException();
		}
		Arrays.sort(propertyList, new Comparator<IProperty<? extends Comparable<?>>>() {
			@Override
			public int compare(IProperty<? extends Comparable<?>> property1,
					IProperty<? extends Comparable<?>> property2) {
				int result = property1.getName().compareTo(property2.getName());
				return result;
			}
		});
		this.propertyList = ImmutableList.copyOf(propertyList);

		List<Mode> modeList = new ArrayList<Mode>();

		Map<IProperty<? extends Comparable<?>>, Comparable<?>> propertyValueMap = new HashMap<IProperty<? extends Comparable<?>>, Comparable<?>>();
		Map<IProperty<? extends Comparable<?>>, ResettableIterator<? extends Comparable<?>>> propertyValueIteratorMap = new HashMap<IProperty<? extends Comparable<?>>, ResettableIterator<? extends Comparable<?>>>();
		for (IProperty<? extends Comparable<?>> property : propertyList) {
			ResettableIterator<? extends Comparable<?>> allowedValueIterator = ResettableIterator
					.warp(property.getAllowedValues().iterator());
			if (allowedValueIterator.hasNext()) {
				propertyValueMap.put(property, allowedValueIterator.next());
			}
			propertyValueIteratorMap.put(property, allowedValueIterator);
			modeList.add(createMode(name, propertyValueMap));
		}
		nextMode: do {
			for (IProperty<? extends Comparable<?>> property : propertyList) {
				ResettableIterator<? extends Comparable<?>> valueIterator = propertyValueIteratorMap.get(property);
				valueIterator.reset();
				while (valueIterator.hasNext()) {
					Comparable<?> value = valueIterator.next();
					if (value.equals(propertyValueMap.get(property))) {
						if (valueIterator.hasNext()) {
							propertyValueMap.put(property, valueIterator.next());
							modeList.add(createMode(name, propertyValueMap));
							continue nextMode;
						}
					}
				}
				valueIterator.reset();
				if (valueIterator.hasNext()) {
					propertyValueMap.put(property, valueIterator.next());
				}
			}
		} while (false);

		this.modeList = ImmutableList.copyOf(modeList);

		for (Mode mode : modeList) {
			mode.buildPropertyValueModeTable();
		}
	}

	private Mode createMode(String name, Map<IProperty<? extends Comparable<?>>, Comparable<?>> propertyValueMap) {
		return new Mode(name, ImmutableMap.copyOf(propertyValueMap));
	}

	@Override
	@SuppressWarnings("unchecked")
	public ImmutableList<Mode> getModeList() {
		return (ImmutableList<Mode>) this.modeList;
	}

	@Override
	public Mode getDefaultMode() {
		return (Mode) this.modeList.get(0);
	}

	@Override
	public Collection<IProperty<? extends Comparable<?>>> getProperties() {
		return this.propertyList;
	}

	@Override
	public String getName() {
		return name;
	}

	public class Mode implements IMode {
		private final String name;
		private final ImmutableMap<IProperty<? extends Comparable<?>>, Comparable<?>> propertyValueMap;
		protected ImmutableTable<IProperty<? extends Comparable<?>>, Comparable<?>, Mode> propertyValueModeTable;

		protected Mode(String name, ImmutableMap<IProperty<? extends Comparable<?>>, Comparable<?>> propertyValueMap) {
			this.name = name;
			this.propertyValueMap = propertyValueMap;
		}

		@Override
		public Collection<IProperty<? extends Comparable<?>>> getPropertyNames() {
			return Collections.unmodifiableCollection(this.propertyValueMap.keySet());
		}

		@Override
		public <V extends Comparable<?>> V getValue(IProperty<V> property) {
			if (!this.propertyValueMap.containsKey(property)) {
				throw new IllegalArgumentException(
						"Cannot get property " + property + " as it does not exist in " + this.getName());
			} else {
				return (V) property.getValueClass().cast(this.propertyValueMap.get(property));
			}
		}

		@Override
		public <V extends Comparable<?>> IMode withProperty(IProperty<V> property, V value) {
			if (!this.propertyValueMap.containsKey(property)) {
				throw new IllegalArgumentException(
						"Cannot set property " + property + " as it does not exist in " + this.getName());
			} else if (!property.getAllowedValues().contains(value)) {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on mode "
						+ this.getName() + ", it is not an allowed value");
			} else {
				return (IMode) (this.propertyValueMap.get(property) == value ? this
						: (IMode) this.propertyValueModeTable.get(property, value));
			}
		}

		@Override
		public ImmutableMap<IProperty<? extends Comparable<?>>, Comparable<?>> getProperties() {
			return this.propertyValueMap;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public ModeDefinition getModeDefinition() {
			return ModeDefinition.this;
		}

		public void buildPropertyValueModeTable() {
			if (propertyValueModeTable != null) {
				throw new IllegalStateException();
			} else {
				Table<IProperty<? extends Comparable<?>>, Comparable<?>, Mode> propertyValueModeTable = HashBasedTable
						.create();
				for (IProperty<? extends Comparable<?>> currentProperty : propertyList) {
					for (Comparable<?> currentValue : currentProperty.getAllowedValues()) {
						nextMode: for (Mode mode : modeList) {
							for (IProperty<? extends Comparable<?>> property : propertyList) {
								Comparable<?> value;
								if (property == currentProperty) {
									value = currentValue;
								} else {
									value = propertyValueMap.get(property);
								}
								if (!mode.propertyValueMap.get(property).equals(value)) {
									continue nextMode;
								}
							}
							propertyValueModeTable.put(currentProperty, currentValue, mode);
						}
					}
				}
				this.propertyValueModeTable = ImmutableTable.copyOf(propertyValueModeTable);
			}
		}
	}
}
