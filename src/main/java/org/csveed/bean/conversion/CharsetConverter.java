package org.csveed.bean.conversion;

import java.nio.charset.Charset;

public class CharsetConverter extends AbstractConverter<Charset> {

    public CharsetConverter() {
        super(Charset.class);
    }

    @Override
    public Charset fromString(String text) throws Exception {
        if (text != null && !text.isEmpty()) {
            return Charset.forName(text);
        }
        return null;
    }

    @Override
    public String toString(Charset value) throws Exception {
        return value != null ? value.name() : "";
    }

}
