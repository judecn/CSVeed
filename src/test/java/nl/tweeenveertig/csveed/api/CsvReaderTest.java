package nl.tweeenveertig.csveed.api;

import nl.tweeenveertig.csveed.bean.ColumnNameMapper;
import nl.tweeenveertig.csveed.test.model.BeanVariousNotAnnotated;
import nl.tweeenveertig.csveed.test.propertyeditors.BeanSimplePropertyEditor;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class CsvReaderTest {
    
    @Test
    public void readLines() {
        Reader reader = new StringReader(
                "text,year,number,date,lines,year and month\n"+
                "'a bit of text',1983,42.42,1972-01-13,'line 1',2013-04\n"+
                "'more text',1984,42.42,1972-01-14,'line 1\nline 2',2014-04\n"+
                "# please ignore me\n"+
                "'and yet more text',1985,42.42,1972-01-15,'line 1\nline 2\nline 3',2015-04\n"
        );

        CsvReader<BeanVariousNotAnnotated> csvReader =
                new CsvReaderImpl<BeanVariousNotAnnotated>(reader, BeanVariousNotAnnotated.class)
                .setEscape('\\')
                .setQuote('\'')
                .setComment('#')
                .setEndOfLine(new char[]{'\n'})
                .setSeparator(',')
                .setStartRow(0)
                .setUseHeader(true)
                .setMapper(ColumnNameMapper.class)
                .ignoreProperty("ignoreMe")
                .mapColumnNameToProperty("text", "txt")
                .setRequired("txt", true)
                .mapColumnNameToProperty("year", "year")
                .mapColumnNameToProperty("number", "number")
                .mapColumnNameToProperty("date", "date")
                .setDate("date", "yyyy-MM-dd")
                .mapColumnNameToProperty("year and month", "yearMonth")
                .setDate("yearMonth", "yyyy-MM")
                .mapColumnNameToProperty("lines", "simple")
                .setConverter("simple", new BeanSimplePropertyEditor())
                ;

        List<BeanVariousNotAnnotated> beans = csvReader.readBeans();
        assertTrue(csvReader.isFinished());
        assertEquals(5, csvReader.getCurrentLine());
        assertEquals(3, beans.size());
    }

    @Test
    public void multipleHeaderReads() {
        Reader reader = new StringReader(
                "text;year;number;date;lines;year and month\n"+
                "\"a bit of text\";1983;42.42;1972-01-13;\"line 1\";2013-04\n"+
                "\"more text\";1984;42.42;1972-01-14;\"line 1\nline 2\";2014-04\n"+
                "\"and yet more text\";1985;42.42;1972-01-15;\"line 1\nline 2\nline 3\";2015-04\n"
        );
        CsvReader<BeanVariousNotAnnotated> csvReader =
                new CsvReaderImpl<BeanVariousNotAnnotated>(reader, BeanVariousNotAnnotated.class);

        assertNotNull(csvReader.readHeader());
        assertNotNull(csvReader.readHeader());
    }

    @Test
    public void startAtLaterLine() {
        Reader reader = new StringReader(
                "-- ignore line 1\n"+
                "-- ignore line 2\n"+
                "-- ignore line 3\n"+
                "text;year;number;date;lines;year and month\n"+
                "\"a bit of text\";1983;42.42;1972-01-13;\"line 1\";2013-04\n"+
                "\"more text\";1984;42.42;1972-01-14;\"line 1\nline 2\";2014-04\n"+
                "\"and yet more text\";1985;42.42;1972-01-15;\"line 1\nline 2\nline 3\";2015-04\n"
        );
        CsvReader<BeanVariousNotAnnotated> csvReader =
                new CsvReaderImpl<BeanVariousNotAnnotated>(reader, BeanVariousNotAnnotated.class)
                .setStartRow(3);
        List<Row> rows = csvReader.readLines();
        assertEquals(3, rows.size());
        assertEquals(7, csvReader.getCurrentLine());
    }


}