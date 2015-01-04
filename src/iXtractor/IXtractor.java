package iXtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author stickuhu
 *
 */
public class IXtractor {
	
	/**
	 * 
	 */
	public IXtractor() {
	}

	/**
	 * 
	 */
	private static void printUsage() {
        System.out.println("Usage: java IXtractor <iTunes XML library file path>");
    }
	
	/**
	 * Returns true in case userInput only contains numeric-characters
	 * 
	 * @param	myString
				The string to be evaluated
	 * @return	<tt>true</tt> if, and only if, a subsequence of the input
     *          sequence matches this matcher's pattern
	 */
	private static Boolean isNumericString(String myString) {
		Pattern pattern = Pattern.compile("^[0-9]+$");
		Matcher matcher = pattern.matcher(myString);
		return matcher.matches(); 
	}
	
	/**
	 * TODO: Allow user to chose several playlists
     * TODO: Refine playlists choice with more selection criterias
     * 
	 * @param	args
	 */
	public static void main(String[] args) {		
		String filename = null;
        File file = null;
        
        // Check Program Input Parameters
        try {
            filename = args[0];
            file = new File(filename);
            
            if (!file.exists()) {
            	System.out.println("File: " + filename + " does not exist.");
            	printUsage();
            	System.exit(1);
            } else
            	System.out.printf("Library File Name: %s%n", filename);
        } catch (ArrayIndexOutOfBoundsException e) {
            printUsage();
            System.exit(1);
        } catch (SecurityException e) {
        	e.printStackTrace();
        	System.out.println("SecurityException: Read Access of the file or directory is denied !");
        	String message = e.getMessage();
        	System.out.printf("Message: %s%n", message);
        	System.exit(1);
        }

        // Creates Library Parser
        LibraryParser parser = new LibraryParser(filename, file);
        
        // Extracts Music Playlist Names
        Map<Integer, String> playlistsNameMap = parser.getPlaylistsName();
        parser.displayPlaylists(playlistsNameMap);
        
        // Gets the Selected Playlist and the Target Directory from User
        Boolean chosen = false, terminate = false;
        String playlistName = null;
        Path targetDir = null;
        
        try (Scanner scanner = new Scanner(System.in)) {
        	while (!chosen && !terminate) {
        		System.out.println("Chose a Playlist number to export (or exit hiting 666)");
        		String userInput = scanner.nextLine();
        		if (isNumericString(userInput)) {
        			int playlistKey = Integer.parseInt(userInput);
	        		if (playlistKey == 666) {
	        			terminate = true;
	        		} else if (playlistsNameMap.containsKey(playlistKey)) {
	        			playlistName = playlistsNameMap.get(playlistKey);
	        			System.out.printf("The Choosen Playlist is name : %s%n", playlistName);
	
	        			while (!chosen && !terminate) {
	        				System.out.println("Please enter a target directory (or exit hiting 666)");
	        				userInput = scanner.nextLine();
	        				if (isNumericString(userInput)) {
	        					playlistKey = Integer.parseInt(userInput);
	        					if (playlistKey == 666) {
	            					terminate = true;
	            				}
	        				}
	        				else {
	        					targetDir = Paths.get(userInput);
	        					if (Files.exists(targetDir)) {
	        						chosen = true;
	        						System.out.println("Target Directory: " + targetDir.toString());
	        					}
	        				}
	        			} 
	        		} else {
	        			System.out.println("Sorry, the choosen Playlist's number is not correct!");
	        		}
        		} 
        	}
        }
        
        if (!terminate) {
        	// Extracts Selected Playlist's Tracks Locations
        	List<String> playlistsTrackIDs = parser.getPlaylistTrackIDs(playlistName);
        	List<Path> paths = parser.getTracksLocation(playlistsTrackIDs);

        	// Exports Tracks to the Target Directory
        	TrackDuplicator duplicator = new TrackDuplicator();
        	for (Path path : paths) {
        		try {
        			// Creates MD5 CheckSum of Track
        			String sourceMD5 = duplicator.createChecksum(path.toString());

        			// Copies Track to targeted Directory
        			Path copyPath = duplicator.copyTrack(path, targetDir);

        			// Creates MD5 CheckSum of Track Copy
        			if ((copyPath != null) && (duplicator.checkCopy(copyPath, sourceMD5))) 
        				System.out.println("Copy has succeeded !");
        			else System.out.println("Copy has failed !");
        		} catch (IOException e) {
        			System.out.printf("IOException raised during File Copy!");
        			e.printStackTrace();
        			String message = e.getMessage();
        			System.out.printf("Message: %s%n", message);
        			System.exit(1);
        		}
        	}
        } else
        	System.out.println("Terminating Execution...");
 	} // End main()	
 } 
