import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Unpack
{
	FileOutputStream outstream = null;

	public Unpack(String src) throws Exception
	{
		unpack(src);
	}

	public void unpack(String src) throws Exception
	{

		// From the initialization vector, we create an IvParameterSpec which is
		// required when creating the Cipher.
		byte[] iv = new byte[128 / 8];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);

		IvParameterSpec ivspec = new IvParameterSpec(iv);

		// KeyGenerator provides the functionality of a secret (symmetric) key
		// generator.

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecretKey skey = kgen.generateKey();

		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.DECRYPT_MODE, skey, ivspec);

		FileEncryptionDecryption.processFile(ci, src, filePath);

		try
		{
			FileInputStream instream = new FileInputStream(filePath);

			byte header[]= new byte[100]; //vachayala
			int length = 0;

			byte Magic[] = new byte[12];
			instream.read(Magic,0,Magic.length);

			String Magicstr = new String(Magic);

			if(!Magicstr.equals("11"))
			{
				throw new InvalidFileException("Invalid packed file format");
			}

			while((length = instream.read(header,0,100)) > 0)
			{
				String str = new String(header);
				String ext = str.substring(str.lastIndexOf("\\"));  //to get last filename only from absolute path 
				ext = ext.substring(1);

				String[] words=ext.split(" ");   // \\s means space

				String filename = words[0];

				for(int i=0;i<words.length;i++)
				{
				System.out.println("Word ["+i +"] is "+ words[i]);
				}
				int size = Integer.parseInt(words[1]); //auto unboxing
 
				byte arr[] = new byte[size];

				instream.read(arr,0,size);

				FileOutputStream fout=new FileOutputStream(filename);
				fout.write(arr,0,size);
			}
		}
		catch(InvalidFileException obj)
		{
			throw new InvalidFileException("Invalid packed file format");
		}
		catch(Exception e)
		{
			System.out.println("Other File exception");
			e.printStackTrace();
		}
	}
}