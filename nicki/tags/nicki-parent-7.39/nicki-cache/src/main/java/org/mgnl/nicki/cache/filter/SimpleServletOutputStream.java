package org.mgnl.nicki.cache.filter;

import javax.servlet.ServletOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleServletOutputStream extends ServletOutputStream {
    private final OutputStream out;

    public SimpleServletOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }
}
