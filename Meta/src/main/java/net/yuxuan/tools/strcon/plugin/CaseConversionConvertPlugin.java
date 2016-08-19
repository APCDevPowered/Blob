package net.yuxuan.tools.strcon.plugin;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import net.yuxuan.utils.ResettableIterator;
import net.yuxuan.utils.StringConsumer;
import net.yuxuan.tools.strcon.plugin.CaseConversionConvertPlugin.ModeDefinition.Mode;

public class CaseConversionConvertPlugin extends BaseConvertPlugin {

    private IMode mode;

    private static final PropertyBoolean USE_JAVA_IDENTIFIER_CONVERT = new PropertyBoolean(
            "USE_JAVA_IDENTIFIER_CONVERT");

    private static final ModeDefinition TO_LOWERCASE = new ModeDefinition("TO_LOWERCASE", USE_JAVA_IDENTIFIER_CONVERT);
    private static final ModeDefinition TO_UPPERCASE = new ModeDefinition("TO_UPPERCASE", USE_JAVA_IDENTIFIER_CONVERT);
    private static final ModeDefinition TO_FIRST_LETTER_LOWERCASE = new ModeDefinition("TO_FIRST_LETTER_LOWERCASE");
    private static final ModeDefinition TO_FIRST_LETTER_UPPERCASE = new ModeDefinition("TO_FIRST_LETTER_UPPERCASE");

    public static interface IModeDefinition<M extends IMode> {
        String getName();

        IMode getDefaultMode();

        Collection<IProperty<? extends Comparable<?>>> getProperties();

        ImmutableList<M> getModeList();
    }

    public static class ModeDefinition implements IModeDefinition<Mode> {
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
            } while(false);

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

            protected Mode(String name,
                    ImmutableMap<IProperty<? extends Comparable<?>>, Comparable<?>> propertyValueMap) {
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

    public static interface IMode {
        Collection<IProperty<? extends Comparable<?>>> getPropertyNames();

        <V extends Comparable<?>> V getValue(IProperty<V> property);

        <V extends Comparable<?>> IMode withProperty(IProperty<V> property, V value);

        ImmutableMap<IProperty<? extends Comparable<?>>, ? extends Comparable<?>> getProperties();

        String getName();

        ModeDefinition getModeDefinition();
    }

    public static interface IProperty<V extends Comparable<?>> {
        String getName();

        Collection<V> getAllowedValues();

        Class<V> getValueClass();

        String getName(V value);
    }

    public abstract static class PropertyBase<V extends Comparable<?>> implements IProperty<V> {
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

    public static class PropertyBoolean extends PropertyBase<Boolean> {
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

    public static class PropertyNaked extends PropertyBase<PropertyNaked.Naked> {
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

    private JCaseConversionConvertPluginSetting setting = new JCaseConversionConvertPluginSetting();

    public CaseConversionConvertPlugin() {
        setName("CaseConversionConverter");
    }

    public Component getSettingComponent() {
        return setting;
    }

    private int indexOfUppercaseLetter(String string, int fromIndex) {
        Matcher matcher = Pattern.compile("\\p{javaUpperCase}").matcher(string);
        if (matcher.find(fromIndex)) {
            return matcher.start();
        } else {
            return -1;
        }
    }

    @Override
    public boolean process(StringConsumer sc, StringBuilder rb) {
        if (mode == null) {
            return false;
        } else if (mode.getModeDefinition() == TO_LOWERCASE) {
            sc.eatPattern(Pattern.compile("[\\s\\S]*"));
            if (mode.getValue(USE_JAVA_IDENTIFIER_CONVERT)) {
                String string = sc.getLastEatString();
                int startSearchUnderlineIndex = 0;
                int underlineIndex = -1;
                boolean isFirstWord = true;
                while ((underlineIndex = string.indexOf('_', startSearchUnderlineIndex)) != -1) {
                    if (isFirstWord) {
                        rb.append(string.substring(0, underlineIndex == 0 ? 1 : underlineIndex).toLowerCase());
                        isFirstWord = false;
                    } else {
                        if (startSearchUnderlineIndex == underlineIndex) {
                            rb.append("_");
                        } else {
                            int firstLetter = string.codePointAt(startSearchUnderlineIndex);
                            rb.appendCodePoint(Character.toUpperCase(firstLetter));
                            rb.append(string.substring((Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1)
                                    + startSearchUnderlineIndex, underlineIndex).toLowerCase());
                        }
                    }
                    startSearchUnderlineIndex = underlineIndex + 1;
                }
                if (isFirstWord) {
                    rb.append(string.substring(startSearchUnderlineIndex, string.length()).toLowerCase());
                    isFirstWord = false;
                } else {
                    if (startSearchUnderlineIndex < string.length()) {
                        int firstLetter = string.codePointAt(startSearchUnderlineIndex);
                        rb.appendCodePoint(Character.toUpperCase(firstLetter));
                        rb.append(string.substring(
                                (Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1) + startSearchUnderlineIndex,
                                string.length()).toLowerCase());
                    } else {
                        rb.append("_");
                    }
                }
            } else {
                rb.append(sc.getLastEatString().toLowerCase());
            }
            if (sc.eatEOF().isSuccess() == false) {
                return false;
            }
        } else if (mode.getModeDefinition() == TO_UPPERCASE) {
            sc.eatPattern(Pattern.compile("[\\s\\S]*"));
            if (mode.getValue(USE_JAVA_IDENTIFIER_CONVERT)) {
                String string = sc.getLastEatString();
                int startSearchUppercaseLetterIndex = 0;
                int uppercaseLetterIndex = -1;
                boolean isFirstWord = true;
                while ((uppercaseLetterIndex = indexOfUppercaseLetter(string, startSearchUppercaseLetterIndex)) != -1) {
                    rb.append(string.substring(startSearchUppercaseLetterIndex - (isFirstWord ? 0 : 1),
                            (isFirstWord ? ++uppercaseLetterIndex : uppercaseLetterIndex)).toUpperCase());
                    rb.append(isFirstWord ? "" : "_");
                    startSearchUppercaseLetterIndex = uppercaseLetterIndex + 1;
                    if (isFirstWord) {
                        isFirstWord = false;
                    }
                }
                rb.append(string.substring(startSearchUppercaseLetterIndex - (isFirstWord ? 0 : 1), string.length())
                        .toUpperCase());
            } else {
                rb.append(sc.getLastEatString().toUpperCase());
            }
            if (sc.eatEOF().isSuccess() == false) {
                return false;
            }
        } else if (mode.getModeDefinition() == TO_FIRST_LETTER_LOWERCASE) {
            sc.eatPattern(Pattern.compile("[\\s\\S]*"));
            String string = sc.getLastEatString();
            if (!string.isEmpty()) {
                int firstLetter = string.codePointAt(0);
                rb.appendCodePoint(Character.toLowerCase(firstLetter));
                rb.append(string.substring(Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1, string.length()));
            }
            if (sc.eatEOF().isSuccess() == false) {
                return false;
            }
        } else if (mode.getModeDefinition() == TO_FIRST_LETTER_UPPERCASE) {
            sc.eatPattern(Pattern.compile("[\\s\\S]*"));
            String string = sc.getLastEatString();
            if (!string.isEmpty()) {
                int firstLetter = string.codePointAt(0);
                rb.appendCodePoint(Character.toUpperCase(firstLetter));
                rb.append(string.substring(Character.isSupplementaryCodePoint(firstLetter) ? 2 : 1, string.length()));
            }
            if (sc.eatEOF().isSuccess() == false) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public class JCaseConversionConvertPluginSetting extends JPanel {
        private static final long serialVersionUID = 1L;

        public JCaseConversionConvertPluginSetting() {

            setLayout(new GridLayout(1, 2));

            JPanel buttonPanel = new JPanel() {
                private static final long serialVersionUID = 1L;

                private Component currentModeSettingComponent;
                private JPanel noModeSettingPanel;

                {
                    noModeSettingPanel = new JPanel();
                    noModeSettingPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                    noModeSettingPanel.setBorder(BorderFactory.createTitledBorder("Info"));

                    JLabel noModeSettingLabel = new JLabel("NoModeSetting");
                    noModeSettingPanel.add(noModeSettingLabel);

                    ButtonGroup buttonGroup = new ButtonGroup();

                    JRadioButton radioButtonToLowercase = new JRadioButton();
                    radioButtonToLowercase.setText("toLowercase");
                    buttonGroup.add(radioButtonToLowercase);
                    radioButtonToLowercase.addItemListener(new ItemListener() {

                        protected JCheckBox useJavaIdentifierConverterCheckBox;

                        private JPanel radioButtonToLowercaseSettingPanel = new JPanel() {
                            private static final long serialVersionUID = 1L;

                            {
                                useJavaIdentifierConverterCheckBox = new JCheckBox();
                                useJavaIdentifierConverterCheckBox.setText("useJavaIdentifierConverter");
                                useJavaIdentifierConverterCheckBox.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        if (e.getStateChange() == ItemEvent.SELECTED) {
                                            mode = TO_LOWERCASE.getDefaultMode()
                                                    .withProperty(USE_JAVA_IDENTIFIER_CONVERT, true);
                                        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                                            mode = TO_LOWERCASE.getDefaultMode()
                                                    .withProperty(USE_JAVA_IDENTIFIER_CONVERT, false);
                                        }
                                    }
                                });
                                add(useJavaIdentifierConverterCheckBox);
                            }
                        };

                        {
                            radioButtonToLowercaseSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                            radioButtonToLowercaseSettingPanel
                                    .setBorder(BorderFactory.createTitledBorder("ModeSetting"));
                        }

                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                mode = TO_LOWERCASE.getDefaultMode().withProperty(USE_JAVA_IDENTIFIER_CONVERT,
                                        useJavaIdentifierConverterCheckBox.isSelected());
                                setModeSettingComponent(radioButtonToLowercaseSettingPanel);
                            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                                setModeSettingComponent(null);
                            }
                        }
                    });
                    add(radioButtonToLowercase);

                    JRadioButton radioButtonToUppercase = new JRadioButton();
                    radioButtonToUppercase.setText("toUppercase");
                    buttonGroup.add(radioButtonToUppercase);
                    radioButtonToUppercase.addItemListener(new ItemListener() {

                        protected JCheckBox useJavaIdentifierConverterCheckBox;

                        private JPanel radioButtonToUppercaseSettingPanel = new JPanel() {
                            private static final long serialVersionUID = 1L;

                            {
                                useJavaIdentifierConverterCheckBox = new JCheckBox();
                                useJavaIdentifierConverterCheckBox.setText("useJavaIdentifierConverter");
                                useJavaIdentifierConverterCheckBox.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        if (e.getStateChange() == ItemEvent.SELECTED) {
                                            mode = TO_UPPERCASE.getDefaultMode()
                                                    .withProperty(USE_JAVA_IDENTIFIER_CONVERT, true);
                                        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                                            mode = TO_UPPERCASE.getDefaultMode()
                                                    .withProperty(USE_JAVA_IDENTIFIER_CONVERT, false);
                                        }
                                    }
                                });
                                add(useJavaIdentifierConverterCheckBox);
                            }
                        };

                        {
                            radioButtonToUppercaseSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                            radioButtonToUppercaseSettingPanel
                                    .setBorder(BorderFactory.createTitledBorder("ModeSetting"));
                        }

                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                mode = TO_UPPERCASE.getDefaultMode().withProperty(USE_JAVA_IDENTIFIER_CONVERT,
                                        useJavaIdentifierConverterCheckBox.isSelected());
                                setModeSettingComponent(radioButtonToUppercaseSettingPanel);
                            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                                setModeSettingComponent(null);
                            }
                        }
                    });
                    add(radioButtonToUppercase);

                    JRadioButton radioButtonToFirstLetterLowercase = new JRadioButton();
                    radioButtonToFirstLetterLowercase.setText("toFirstLetterLowercase");
                    buttonGroup.add(radioButtonToFirstLetterLowercase);
                    radioButtonToFirstLetterLowercase.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                mode = TO_FIRST_LETTER_LOWERCASE.getDefaultMode();
                            } else if (e.getStateChange() == ItemEvent.DESELECTED) {

                            }
                        }
                    });
                    add(radioButtonToFirstLetterLowercase);

                    JRadioButton radioButtonToFirstLetterUppercase = new JRadioButton();
                    radioButtonToFirstLetterUppercase.setText("toFirstLetterUppercase");
                    buttonGroup.add(radioButtonToFirstLetterUppercase);
                    radioButtonToFirstLetterUppercase.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                mode = TO_FIRST_LETTER_UPPERCASE.getDefaultMode();
                            } else if (e.getStateChange() == ItemEvent.DESELECTED) {

                            }
                        }
                    });
                    add(radioButtonToFirstLetterUppercase);

                    radioButtonToLowercase.setSelected(true);
                }

                private void setModeSettingComponent(Component component) {
                    if (component != null) {
                        if (currentModeSettingComponent != null) {
                            JCaseConversionConvertPluginSetting.this.remove(currentModeSettingComponent);
                        } else {
                            JCaseConversionConvertPluginSetting.this.remove(noModeSettingPanel);
                        }
                        currentModeSettingComponent = component;
                        JCaseConversionConvertPluginSetting.this.add(component,
                                JCaseConversionConvertPluginSetting.this.getComponentCount());
                        JCaseConversionConvertPluginSetting.this.updateUI();
                    } else {
                        if (currentModeSettingComponent != null) {
                            JCaseConversionConvertPluginSetting.this.remove(currentModeSettingComponent);
                            JCaseConversionConvertPluginSetting.this.add(noModeSettingPanel,
                                    JCaseConversionConvertPluginSetting.this.getComponentCount());
                            JCaseConversionConvertPluginSetting.this.updateUI();
                            currentModeSettingComponent = null;
                        }
                    }
                }
            };
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBorder(BorderFactory.createTitledBorder("Mode"));
            add(buttonPanel, 0);
        }
    }
}
