import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Packer
{
	FileOutputStream outstream = null;

	String ValidExt[] = {".txt",".c",".java",".cpp"};

	public Packer(String src, String Dest) throws Exception
	{
		System.out.println("Inside Packer");
		System.out.println(src);
		System.out.println(Dest);
		String Magic = "11";
		byte arr[] = Magic.getBytes();
		File outfile =new File(Dest);
		
		File infile = null;
		outstream = new FileOutputStream(Dest);
		outstream.write(arr, 0, arr.length);

		File folder = new File(src);

		System.setProperty("user.dir",src); //sets current directory 

		listAllFiles(src);

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
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);

		FileEncryptionDecryption.processFile(ci, src, Dest);
	}

	public void listAllFiles(String path)
	{
		try
		(Stream<Path> paths = Files.walk(Paths.get(path)))  //Automatic option for finally 
		{
			paths.forEach(filePath ->
			{
				if (Files.isRegularFile(filePath))
				
				{
					try
					{
						String name = filePath.getFileName().toString();  //Only name is getting e.g demo.txt as
						
						System.out.println("Only filename = " + name);   //My add
						
						String ext = name.substring(name.lastIndexOf("."));

						List<String> list = Arrays.asList(ValidExt);

						if(list.contains(ext))
						{
							File file=new File(filePath.getFileName().toString());
							
							String filename = path +"\\"+ file;
							
							System.out.println("Full path of "+ name + " is "+ filename);
							Pack(filename);
							//Pack(file.getAbsolutePath());
						}
					}
					catch (Exception e)
					{
						System.out.println("Baherchi");
						System.out.println(e);
					}
				}
			});
		}
		catch (IOException e)
		{
			System.out.println("Baherchi");
			System.out.println(e);
		}
	}

	public void Pack(String filePath)
	{
		FileInputStream instream = null;


		try
		{
			byte[] buffer = new byte[1024];

			int length;

			byte temp[] = new byte[100];  //for header 

			File fobj = new File(filePath); //inode

			String Header = filePath+" "+fobj.length();

			for (int i = Header.length(); i < 100; i++)
				Header += " ";

			temp = Header.getBytes();

			instream = new FileInputStream(filePath);

			outstream.write(temp, 0, temp.length);

			while ((length = instream.read(buffer)) > 0)
			{
				outstream.write(buffer, 0, length);
			}

			instream.close();
		}
		catch(Exception e)
		{
			System.out.println("pack function");
			System.out.println(e);
		}
	}
}