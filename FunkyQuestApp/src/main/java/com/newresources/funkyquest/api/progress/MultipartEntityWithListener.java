package com.newresources.funkyquest.api.progress;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class MultipartEntityWithListener extends MultipartEntity {

	private OutputStreamProgress outstream;

	private WriteListener writeListener;

	public MultipartEntityWithListener(WriteListener writeListener) {
		super();
		this.writeListener = writeListener;
	}

	public MultipartEntityWithListener(HttpMultipartMode mode, WriteListener writeListener) {
		super(mode);
		this.writeListener = writeListener;
	}

	public MultipartEntityWithListener(HttpMultipartMode mode, String boundary, Charset charset,
	                                   WriteListener writeListener) {
		super(mode, boundary, charset);
		this.writeListener = writeListener;
	}

	@Override
	public void writeTo(OutputStream os) throws IOException {
		this.outstream = new OutputStreamProgress(os, writeListener);
		super.writeTo(this.outstream);
	}

}