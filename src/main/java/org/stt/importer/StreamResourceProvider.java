package org.stt.importer;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface StreamResourceProvider extends Closeable {

	Writer provideAppendingWriter() throws IOException;
	
	/**
	 * does not append
	 */
	Writer provideTruncatingWriter() throws IOException;
	
	Reader provideReader() throws FileNotFoundException;
	
	/**
	 * Should close all provided writers and the reader if they are not yet closed.
	 */
	@Override
	void close();
}
