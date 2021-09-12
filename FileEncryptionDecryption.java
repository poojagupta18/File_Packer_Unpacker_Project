
class FileEncryptionDecryption{

static void processFile(Cipher ci, String inFile, String outFile)
			throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
		try (
		FileInputStream in = new FileInputStream(inFile);
		FileOutputStream out = new FileOutputStream(outFile)) {
			byte[] ibuf = new byte[1024];
			int len;
			while ((len = in.read(ibuf)) != -1) {
				byte[] obuf = ci.update(ibuf, 0, len);
				if (obuf != null)
					out.write(obuf);
			}
			byte[] obuf = ci.doFinal();
			if (obuf != null)
				out.write(obuf);
		}
	}
}