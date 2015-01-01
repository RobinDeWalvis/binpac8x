package net.quantuminfinity.binpac8x;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Binpac8x
{
	String name, msg;
	byte[] data;
	OutputStream os;
	
	public Binpac8x(InputStream is, OutputStream os, String name, String msg)
	{	
		try{
			this.data = getBytesFromInputStream(is);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		this.os = os;
		this.name = name.toUpperCase();
		this.msg = msg;
	}
	
	public void parse()
	{
		name = name.substring(0, name.length() > 8 ? 8: name.length());
		while (name.length() < 8)
			name += (char) 0;
		
		msg = msg.substring(0, msg.length() > 42 ? 42: msg.length());
		while (msg.length() < 42)
			msg += ' ';
		
		byteArray("**TI83F*");
		byteArray(26,10,0);
		byteArray(msg);
		
		int bsize = data.length;
		int bsize_hb = (int) Math.floor(bsize/256);
		int bsize_lb = bsize - 256 * bsize_hb;
		bsize += 2;
		byte[] bin = new byte[bsize];
		bin[0] = (byte) bsize_lb;
		bin[1] = (byte) bsize_hb;
		System.arraycopy(data, 0, bin, 2, data.length);
		bsize_hb = (int) Math.floor(bsize/256);
		bsize_lb = bsize - 256 * bsize_hb;
		
		int hsize = 17;
		int datasize = hsize + bsize;
		int size_hb = (int) Math.floor(datasize/256);
		int size_lb = datasize - 256*size_hb;
		Main.log("On-calc name: " + name);
		Main.log("Hidden msg: " + msg);
		
		byteArray(size_lb, size_hb, 13, 0, bsize_lb, bsize_hb, 6);
		byteArray(name);
		byteArray(0, 0, bsize_lb, bsize_hb);
		byteArray(bin);
		
		long chksum = 2*bsize_lb + 2*bsize_hb + 19;
		for (byte b:name.getBytes())
			chksum += (b & 0xFF);
		for (byte b:bin)
			chksum += (b & 0xFF);
		chksum = chksum % 65536;
		int cs_hb = (int) Math.floor(chksum/256);
		int cs_lb = (int) (chksum - 256*cs_hb);
		
		byteArray(cs_lb, cs_hb);
	}
	
	public void byteArray(byte... bytes)
	{
		try {
			os.write(bytes);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void byteArray(String bytes)
	{
		try {
			os.write(bytes.getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void byteArray(int... bytes)
	{
		try {
			for (int i=0; i<bytes.length; i++)
				os.write((byte) bytes[i]);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static byte[] getBytesFromInputStream(InputStream is) throws IOException
	{
		long streamLength = is.available();
		if (streamLength > Integer.MAX_VALUE)
		{
			is.close();
			Main.error("File is too large");
		}
		
		byte[] bytes = new byte[(int) streamLength];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
			offset += numRead;
		
		if (offset < bytes.length)
		{
			is.close();
			Main.error("Could not read file");
		}
		
		is.close();
		return bytes;
	}
}
