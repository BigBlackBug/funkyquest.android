package com.newresources.funkyquest.api.progress;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamProgress extends OutputStream {

	private final OutputStream output;

	private final WriteListener writeListener;

	private long bytesWritten = 0;

	public OutputStreamProgress(OutputStream output, WriteListener writeListener) {
		this.output = output;
		this.writeListener = writeListener;
	}

	@Override
	public void write(int b) throws IOException {
		output.write(b);
		bytesWritten++;
		writeListener.onWrite(bytesWritten);
	}

	@Override
	public void write(byte[] b) throws IOException {
		output.write(b);
		bytesWritten += b.length;
		writeListener.onWrite(bytesWritten);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		output.write(b, off, len);
		bytesWritten += len;
		writeListener.onWrite(bytesWritten);
	}

	@Override
	public void flush() throws IOException {
		output.flush();
	}

	@Override
	public void close() throws IOException {
		output.close();
	}
}