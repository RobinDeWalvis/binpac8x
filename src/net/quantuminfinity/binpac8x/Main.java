package net.quantuminfinity.binpac8x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main
{
	public Main(String inloc, String outloc)
	{
		File in = new File(inloc);
		File out = new File(outloc);
		String name = in.getName().substring(0, in.getName().indexOf('.'));
		create(in, out, name, "");
	}
	
	public Main(String inloc, String outloc, String name)
	{
		File in = new File(inloc);
		File out = new File(outloc);
		create(in, out, name, "");
	}
	
	public Main(String inloc, String outloc, String name, String msg)
	{
		File in = new File(inloc);
		File out = new File(outloc);
		create(in, out, name, msg);
	}
	
	public void create(File in, File out, String name, String msg)
	{
		if (!in.exists())
			error("Input does not exist!");
		if (!in.isFile())
			error("Input is not a file!");
		
		if (out.exists())
			out.delete();
		
		InputStream fin;
		OutputStream fout;
		try {
			out.createNewFile();
			fin = new FileInputStream(in);
			fout = new FileOutputStream(out);
			
			Binpac8x bp8x = new Binpac8x(fin, fout, name, msg);
			bp8x.parse();
			
			fout.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void log(String text)
	{
		System.out.println(text);
	}
	
	public static void error(String text)
	{
		System.err.println("Error: "+text);
		System.exit(-1);
	}
	
	public static void main(String[] args)
	{
		if (args.length >= 4)
			new Main(args[0], args[1], args[2], args[3]);
		else if (args.length >= 3)
			new Main(args[0], args[1], args[2]);
		else if (args.length >= 2)
			new Main(args[0], args[1]);
		else
			error("Usage: <source .bin (with extension)> <dest .8xp (with extension)> [name] [hidden message (42 chars)]");
	}
}
