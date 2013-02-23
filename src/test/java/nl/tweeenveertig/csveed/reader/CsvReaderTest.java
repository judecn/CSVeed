package nl.tweeenveertig.csveed.reader;

import nl.tweeenveertig.csveed.api.Row;
import nl.tweeenveertig.csveed.bean.BeanReader;
import nl.tweeenveertig.csveed.report.CsvException;
import nl.tweeenveertig.csveed.test.model.*;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class CsvReaderTest {

    @Test
    public void readUnmapped() {
        Reader reader = new StringReader(
            "alpha;beta;gamma\n"+
            "\"row 1, cell 1\";\"row 1, cell 2\";\"row 1, cell 3\"\n"+
            "\"row 2, cell 1\";\"row 2, cell 2\";\"row 2, cell 3\"\n"+
            "\"row 3, cell 1\";\"row 3, cell 2\";\"row 3, cell 3\""
        );
        BeanReader<BeanWithMultipleStrings> beanReader = new BeanReader<BeanWithMultipleStrings>(BeanWithMultipleStrings.class);
        List<Row> rows = beanReader.readUnmapped(reader);
        assertEquals(3, rows.size());
    }

    @Test
    public void getBeans() {
        Reader reader = new StringReader(
            "alpha;beta;gamma\n"+
            "\"row 1, cell 1\";\"row 1, cell 2\";\"row 1, cell 3\"\n"+
            "\"row 2, cell 1\";\"row 2, cell 2\";\"row 2, cell 3\"\n"+
            "\"row 3, cell 1\";\"row 3, cell 2\";\"row 3, cell 3\""
        );
        BeanReader<BeanWithMultipleStrings> beanReader = new BeanReader<BeanWithMultipleStrings>(BeanWithMultipleStrings.class);
        List<BeanWithMultipleStrings> beans = beanReader.read(reader);
        assertEquals(3, beans.size());
        BeanWithMultipleStrings bean = beans.get(0);
        assertEquals("row 1, cell 1", bean.getGamma());
        assertEquals("row 1, cell 2", bean.getBeta());
        assertEquals("row 1, cell 3", bean.getAlpha());
    }

    @Test
    public void tabSeparated() {
        Reader reader = new StringReader(
            "alpha;beta;gamma\n"+
            "'\\'row\\' 1, cell 1'\t'row 1, cell 2'\t'row 1, cell 3'\r"+
            "'\\'row\\' 2, cell 1'\t'row 2, cell 2'\t'row 2, cell 3'\r"+
            "'\\'row\\' 3, cell 1'\t'row 3, cell 2'\t'row 3, cell 3'"
        );
        BeanReader<BeanWithAlienSettings> beanReader = new BeanReader<BeanWithAlienSettings>(BeanWithAlienSettings.class);
        List<BeanWithAlienSettings> beans = beanReader.read(reader);
        assertEquals(3, beans.size());
        BeanWithAlienSettings bean = beans.get(0);
        assertEquals("'row' 1, cell 1", bean.getGamma());
        assertEquals("row 1, cell 2", bean.getBeta());
        assertEquals("row 1, cell 3", bean.getAlpha());
    }

    @Test
    public void variousDataTypes() {
        Reader reader = new StringReader(
            "text;year;number;date;year and month\n"+
            "\"a bit of text\";1984;42.42;1972-01-13;2013-04\n"
        );
        BeanReader<BeanWithVariousTypes> beanReader = new BeanReader<BeanWithVariousTypes>(BeanWithVariousTypes.class);
        List<BeanWithVariousTypes> beans = beanReader.read(reader);
        assertEquals(1, beans.size());
        BeanWithVariousTypes bean = beans.get(0);
        assertEquals("a bit of text", bean.getText());
        assertEquals((Integer)1984, bean.getYear());
        assertEquals(42.42, bean.getNumber());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("1972-01-13", formatter.format(bean.getDate()));
        formatter = new SimpleDateFormat("yyyy-MM");
        assertEquals("2013-04", formatter.format(bean.getYearMonth()));
    }

    @Test
    public void noHeader() {
        Reader reader = new StringReader(
            "\"a bit of text\";1984;42.42;1972-01-13;2013-04\n"
        );
        BeanReader<BeanWithoutHeader> beanReader = new BeanReader<BeanWithoutHeader>(BeanWithoutHeader.class);
        List<BeanWithoutHeader> beans = beanReader.read(reader);
        assertEquals(1, beans.size());
    }
    
    @Test
    public void nameMatching() {
        Reader reader = new StringReader(
            "street;city;postal code;ignore this\n"+
            "\"Some street\";\"Some city\";\"Some postal code\";\"Some ignoring\""
        );
        BeanReader<BeanWithNameMatching> beanReader = new BeanReader<BeanWithNameMatching>(BeanWithNameMatching.class);
        List<BeanWithNameMatching> beans = beanReader.read(reader);
        assertEquals(1, beans.size());
        BeanWithNameMatching bean = beans.get(0);
        assertEquals("Some street", bean.getLine1());
        assertEquals("Some city", bean.getLine2());
        assertEquals("Some postal code", bean.getLine3());
    }

    @Test
    public void indexMatching() {
        Reader reader = new StringReader(
            "\"line-1\";\"line0\";\"line1\";\"line2\";\"line3\""
        );
        BeanReader<BeanWithCustomIndexes> beanReader = new BeanReader<BeanWithCustomIndexes>(BeanWithCustomIndexes.class);
        BeanWithCustomIndexes bean = beanReader.readLine(reader);
        assertEquals("line0", bean.getLine0());
        assertEquals("line1", bean.getLine1());
        assertEquals("line2", bean.getLine2());
        assertEquals("line3", bean.getLine3());
    }
    
    @Test
    public void numberOfIgnores() {
        Reader reader = new StringReader(
            "14;28;42"
        );
        BeanReader<BeanLotsOfIgnores> beanReader = new BeanReader<BeanLotsOfIgnores>(BeanLotsOfIgnores.class);
        BeanLotsOfIgnores bean = beanReader.readLine(reader);
        assertEquals((Integer)14, bean.getTakeThis1());
        assertEquals((Integer)28, bean.getPickThis1());
        assertEquals((Integer)42, bean.getChooseThis1());
        assertNull(bean.getDitchThat1());
        assertNull(bean.getLeaveThat1());
    }

    @Test
    public void customPropertyEditor() {
        Reader reader = new StringReader(
            "\"some text\""
        );
        BeanReader<BeanWithConverter> beanReader = new BeanReader<BeanWithConverter>(BeanWithConverter.class);
        BeanWithConverter bean = beanReader.readLine(reader);
        assertEquals("some text", bean.getBean().getName());
    }

    @Test(expected = CsvException.class)
    public void illegalToken() {
        Reader reader = new StringReader(
            "\"alpha\";\"beta\";\"gamma\"a\n"
        );
        BeanReader<BeanSimple> beanReader = new BeanReader<BeanSimple>(BeanSimple.class);
        beanReader.read(reader);
    }

    @Test(expected = CsvException.class)
    public void beanMappingError() {
        Reader reader = new StringReader(
            "text;year;number;date;year and month\n"+
            "\"a bit of text\";UNEXPECTED TEXT!!!;42.42;1972-01-13;2013-04\n"
        );
        BeanReader<BeanWithVariousTypes> beanReader = new BeanReader<BeanWithVariousTypes>(BeanWithVariousTypes.class);
        beanReader.read(reader);
    }

    @Test(expected = CsvException.class)
    public void cannotConvertToNonStandardObject() {
        Reader reader = new StringReader(
            "\"can I convert this to a simple bean?\""
        );
        BeanReader<BeanWithNonStandardObject> beanReader = new BeanReader<BeanWithNonStandardObject>(BeanWithNonStandardObject.class);
        beanReader.read(reader);
    }

    @Test(expected = CsvException.class)
    public void nonInstantiableBean() {
        Reader reader = new StringReader(
                "\"can I convert this to a simple bean?\""
        );
        BeanReader<BeanWithoutNoArgPublicConstructor> beanReader = new BeanReader<BeanWithoutNoArgPublicConstructor>(BeanWithoutNoArgPublicConstructor.class);
        beanReader.read(reader);
    }

}
