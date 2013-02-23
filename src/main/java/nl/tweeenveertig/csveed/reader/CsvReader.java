package nl.tweeenveertig.csveed.reader;

import nl.tweeenveertig.csveed.bean.instructions.BeanInstructions;
import nl.tweeenveertig.csveed.bean.instructions.BeanParser;
import nl.tweeenveertig.csveed.csv.parser.LineReader;
import nl.tweeenveertig.csveed.csv.structure.Row;
import nl.tweeenveertig.csveed.report.CsvException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader<T> {

    public static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

    private LineReader lineReader;

    private BeanInstructions<T> beanInstructions;

    private AbstractMappingStrategy<T> strategy;

    public CsvReader(Class<T> beanClass) {
        this(new BeanParser<T>().getBeanInstructions(beanClass));
    }

    public CsvReader(BeanInstructions<T> beanInstructions) {
        this.beanInstructions = beanInstructions;
        this.lineReader = new LineReader(this.beanInstructions.getCsvInstructions());
        LOG.info("- CSV config / mapping strategy: "+beanInstructions.getMappingStrategy());
    }

    public List<T> read(Reader reader) {
        List<T> beans = new ArrayList<T>();
        while (!isFinished()) {
            T bean = readLine(reader);
            if (bean != null) {
                beans.add(bean);
            }
        }
        return beans;
    }

    public T readLine(Reader reader) {
        Row unmappedRow = readLineUnmapped(reader);
        if (unmappedRow == null) {
            return null;
        }
        return getStrategy(unmappedRow).convert(instantiateBean(), unmappedRow, getCurrentLine());
    }

    protected AbstractMappingStrategy<T> getStrategy(Row unmappedRow) {
        if (strategy == null) {
            strategy = beanInstructions.createMappingStrategy();
            strategy.instruct(beanInstructions, unmappedRow);
        }
        return strategy;
    }

    private T instantiateBean() {
        try {
            return this.beanInstructions.newInstance();
        } catch (Exception err) {
            String errorMessage =
                    "Unable to instantiate the bean class "+this.beanInstructions.getBeanClass().getName()+
                    ". Does it have a no-arg public constructor?";
            LOG.error(errorMessage);
            throw new CsvException(errorMessage, err);
        }
    }

    public List<Row> readUnmapped(Reader reader) {
        return lineReader.read(reader);
    }

    public Row readLineUnmapped(Reader reader) {
        return lineReader.readLine(reader);
    }

    public int getCurrentLine() {
        return this.lineReader.getCurrentLine();
    }

    public boolean isFinished() {
        return lineReader.isFinished();
    }

}
