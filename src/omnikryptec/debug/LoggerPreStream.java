package omnikryptec.debug;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import omnikryptec.debug.Logger.ErrorLevel;

public class LoggerPreStream extends FilterOutputStream{
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	private ErrorLevel default_level = ErrorLevel.INFO;
	
	public LoggerPreStream(OutputStream out) {
		super(out);
	}
	
	@Override
	public void write(final int idx) throws IOException {
			beforeWrite();
			out.write(idx);
	}

	@Override
	public void write(final byte[] bts) throws IOException {
			beforeWrite();
			out.write(bts);		
	}

	@Override
	public void write(final byte[] bts, final int st, final int end) throws IOException {
			beforeWrite();
			out.write(bts, st, end);		
	}

	private void beforeWrite() throws IOException {
		if(this.formatter!=null){
			out.write((LocalDateTime.now().format(formatter)).getBytes());
		}
		out.write((" ["+default_level.name()+"]").getBytes());
	}
	
	public void setDateTimeFormatter(DateTimeFormatter formatter){
		this.formatter = formatter;
	}
	
	public void setDefaultErrorLevel(ErrorLevel defaultlvl){
		this.default_level = defaultlvl;
	}

}
