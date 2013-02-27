package nl.tweeenveertig.csveed.bean;

import nl.tweeenveertig.csveed.line.LineReaderInstructions;
import nl.tweeenveertig.csveed.line.LineReaderInstructionsImpl;

import java.beans.PropertyEditor;

public class BeanReaderInstructionsImpl implements BeanReaderInstructions {

    private LineReaderInstructions lineReaderInstructions = new LineReaderInstructionsImpl();

    private BeanProperties properties;

    private Class<? extends AbstractMapper> mappingStrategy = ColumnIndexMapper.class;

    private Class beanClass;

    public BeanReaderInstructionsImpl(Class beanClass) {
        this.properties = new BeanProperties(beanClass);
        this.beanClass = beanClass;
    }

    public LineReaderInstructions getLineReaderInstructions() {
        return this.lineReaderInstructions;
    }

    public BeanReaderInstructions setUseHeader(boolean useHeader) {
        this.lineReaderInstructions.setUseHeader(useHeader);
        return this;
    }

    public BeanReaderInstructions setStartRow(int startRow) {
        this.lineReaderInstructions.setStartRow(startRow);
        return this;
    }

    public BeanReaderInstructions setEscape(char symbol) {
        this.lineReaderInstructions.setEscape(symbol);
        return this;
    }

    public BeanReaderInstructions setQuote(char symbol) {
        this.lineReaderInstructions.setQuote(symbol);
        return this;
    }

    public BeanReaderInstructions setSeparator(char symbol) {
        this.lineReaderInstructions.setSeparator(symbol);
        return this;
    }

    public BeanReaderInstructions setEndOfLine(char[] symbols) {
        this.lineReaderInstructions.setEndOfLine(symbols);
        return this;
    }

    public BeanReaderInstructions setMapper(Class<? extends AbstractMapper> mapper) {
        this.mappingStrategy = mapper;
        return this;
    }

    public BeanReaderInstructions setDate(String propertyName, String dateFormat) {
        this.getProperties().setDate(propertyName, dateFormat);
        return this;
    }

    public BeanReaderInstructions setRequired(String propertyName, boolean required) {
        this.getProperties().setRequired(propertyName, required);
        return this;
    }

    public BeanReaderInstructions setConverter(String propertyName, PropertyEditor converter) {
        this.getProperties().setConverter(propertyName, converter);
        return this;
    }

    public BeanReaderInstructions ignoreProperty(String propertyName) {
        this.getProperties().ignoreProperty(propertyName);
        return this;
    }

    public BeanReaderInstructions mapColumnIndexToProperty(int columnIndex, String propertyName) {
        this.getProperties().mapIndexToProperty(columnIndex, propertyName);
        return this;
    }

    public BeanReaderInstructions mapColumnNameToProperty(String columnName, String propertyName) {
        this.getProperties().mapNameToProperty(columnName, propertyName);
        return this;
    }

    public Class<? extends AbstractMapper> getMappingStrategy() {
        return this.mappingStrategy;
    }

    public BeanProperties getProperties() {
        return this.properties;
    }

    public Class getBeanClass() {
        return this.beanClass;
    }

}
