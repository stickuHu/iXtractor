package iXtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TrackDuplicator {

	/**
	 * 
	 */
	public TrackDuplicator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public void copyTrackStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	/**
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public void copyTrackChannel(File source, File dest) throws IOException {
	    FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
	    try {
	        sourceChannel = new FileInputStream(source).getChannel();
	        destChannel = new FileOutputStream(dest).getChannel();
	        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	       }finally{
	           sourceChannel.close();
	           destChannel.close();
	       }
	}
	
	/**
	 * @param source
	 * @param destdir
	 * @return
	 * @throws IOException
	 * @throws DirectoryNotEmptyException
	 */
	public Path copyTrack(Path source, Path destdir) throws IOException, DirectoryNotEmptyException {
		
		long elapseTime = 0, cpuTime = 0;
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		Boolean CPU = threadMXBean.isCurrentThreadCpuTimeSupported();
		
		Path path = null;
		CopyOption[] options = new CopyOption[] { 
				StandardCopyOption.COPY_ATTRIBUTES/*, StandardCopyOption.ATOMIC_MOVE*/ 
		};
		
		try {
			if (CPU) cpuTime = threadMXBean.getCurrentThreadCpuTime();
			elapseTime = System.currentTimeMillis();
			
			path = Files.copy(source, destdir.resolve(source.getFileName()), options);
			
			elapseTime = System.currentTimeMillis() - elapseTime;
			if (CPU) cpuTime = (threadMXBean.getCurrentThreadCpuTime() - cpuTime) / 1000000L;

			System.out.printf("Elapsed Time: %d ms%n", elapseTime);
			if (CPU) System.out.printf("CPU Time: %d ms%n", cpuTime);
		}
		catch (FileAlreadyExistsException e) {
			System.out.println("Already existing file! Ignore it.");

			System.out.printf("-Reason: %s%n", e.getReason());
			System.out.printf("-File: %s%n", e.getFile());
			System.out.printf("-Other File: %s%n", e.getOtherFile());
		}
		catch (IOException e) {
			System.out.println("IO Exception trying to copy any file");
			System.out.printf("-File: %s%n", source); // USE LOGGER INSTEAD ??
		}
		
		System.out.printf("Song copied at: %s%n", path);
		return path;
	}
			
	/**
	 * @param filePath
	 * @return
	 */
	public String createChecksum(String filePath) {

		byte[] md5 = null;
		String checkSum = null;
		
		// what to do with FileNotFoundException ???
		try (FileInputStream fileStream = new FileInputStream(filePath)) {		
			MessageDigest msgDigest = MessageDigest.getInstance("MD5");

			byte[] buffer = new byte[8192];
			int numOfBytesRead;
			
			while( (numOfBytesRead = fileStream.read(buffer)) > 0) {
				msgDigest.update(buffer, 0, numOfBytesRead);
			}
			md5 = msgDigest.digest();
		} catch (FileNotFoundException e) {
			System.out.println("Filenot Found Exception !");
			e.printStackTrace();
			String msg = e.getMessage();
			System.out.printf("Msg: %s%n", msg);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error while reading InputStream");
			System.out.printf("File: %s%n", filePath);
			//logger.log(Level.SEVERE, null, ex);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			String msg = e.getMessage();
			System.out.printf("Message: ", msg);
			System.out.println("Unable to find MD5 algorithm !");
			//logger.log(Level.SEVERE, null, ex);
		}

		// Because Byte is 8-bit signed 2's complement Integer, we need to decode it
		// previously to HexString transformation...
		/*String result = "";
	    for (int i=0; i < b.length; i++) {
	    	result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	    }*/
		// ... or we can use BigInteger, that expects signed 2's complement Integer
		if (md5 != null) {
			checkSum = new BigInteger(1, md5).toString(16);
			//System.out.printf("File: %s%n", filePath);
			//System.out.printf("CheckSum: %s%n", checkSum);
		}
		else {
			System.out.println("Caution: CheckSum String is null");
			// Find any Exception to throw !
		}
		
		return checkSum;
	}
	
	/**
	 * @param trackCopyFilePath
	 * @param trackSourceMD5
	 * @return
	 */
	public Boolean checkCopy(Path trackCopyFilePath, String trackSourceMD5) {
		Boolean copyStatus = false;
		
		// Creates Track Copy MD5 CheckSum
		String trackCopyMD5 = createChecksum(trackCopyFilePath.toString());
    	
    	if (trackCopyMD5.equals(trackSourceMD5))
    		copyStatus = true;
    	
    	//System.out.printf("Source: %s%n", trackSourceMD5);
    	//System.out.printf("Target: %s%n", trackCopyMD5);
    	
    	return copyStatus;
	}
}
