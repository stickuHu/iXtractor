package iXtractor;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class LibraryParser {
	
	private String libraryFileName;
	private File libraryFile;
	private XMLInputFactory xmlif;
	
	/**
	 * 
	 */
	public LibraryParser() {}
	
	/**
	 * @param libraryFileName
	 * @param libraryFile
	 */
	public LibraryParser(String libraryFileName, File libraryFile) {
		this.libraryFile = libraryFile;
		this.libraryFileName = libraryFileName;
		createInputFactory();
	}

	/**
	 * XML Input Factory creation (for factorization purpose)
	 * TODO: Review all XMLInputFactory property (any useless/missing?)
	 */
	private void createInputFactory() {
		xmlif = XMLInputFactory.newInstance();
		xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
		xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);		
		//System.out.println("Factory: " + xmlif);
	}
	
	/**
	 * Extract from Library, the name of all Playlists
	 * 
	 * @return
	 */
	public Map<Integer, String> getPlaylistsName() {
		Map<Integer, String> playlistNameMap = new HashMap <Integer, String>();
		
        try{                                                
            XMLStreamReader xmlr = xmlif.createXMLStreamReader(libraryFileName, new FileInputStream(libraryFile));	
            int eventType = xmlr.getEventType();	//when XMLStreamReader is created, it is positioned at START_DOCUMENT event.           
                        
            int index = 0, key = 0, playlists = 0, name = 0; 
            
            while (xmlr.hasNext()) {
                eventType = xmlr.next();                   
                // printEventType(eventType);
                
				if ((name == 0) && (playlists == 0) && (key == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 1;
					// System.out.println(" <key> has been founded " );
				} else if ((name == 0) && (playlists == 0) && (key == 1) 
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Playlists"))) {
					playlists = 1;
					// System.out.println(" <key>playlists</key> has been founded " );
				} else if ((name == 0) && (playlists == 0) && (key == 1)) {
					// System.out.println(" <key> has been dismissed " );
					key = 0;
				} else if ((name == 0) && (playlists == 1) && (key == 1)  
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Name"))) {
					// System.out.println(" <key>Name</key> has been found " );
					name = 1;
				} else if ((name == 1) && (playlists == 1) && (key == 1) 
						&& (eventType == XMLEvent.CHARACTERS)) {
					// printText(xmlr);
					index ++;
					playlistNameMap.put(index, xmlr.getText());
					name = 0;
					// System.out.println(" <key>name</key><string>XXX</string> has been founded " );
				}  				
            } // end while
        } catch(XMLStreamException ex) {
            System.out.println(ex.getMessage());
            if(ex.getNestedException() != null) ex.getNestedException().printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
		
		return playlistNameMap;
	}
	
	/**
	 * Extract from Library, the track IDs of a Playlist 
	 * 
	 * @param playlistName
	 * @return
	 */
	public List<String> getPlaylistTrackIDs(String playlistName) {
		List<String> tracksList = new ArrayList<String>();
		
        try{                                                
        	XMLStreamReader xmlr = xmlif.createXMLStreamReader(libraryFileName, new FileInputStream(libraryFile));	
            int eventType = xmlr.getEventType();           
            
            int key = 0, playlists = 0, name = 0, items = 0, track = 0, characName = 0, stop = 0; 
            
            while (xmlr.hasNext() && stop == 0) {
                eventType = xmlr.next();                   
                //printEventType(eventType);
                
				if ((track == 0) && (items == 0) && (characName == 0) && (name == 0) && (playlists == 0) && (key == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 1;
					// System.out.println(" <key> has been founded " );
				} else if ((track == 0) && (items == 0) && (characName == 0) && (name == 0) && (playlists == 0) && (key == 1) 
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Playlists"))) {
					playlists = 1;
					// System.out.println(" <key>playlists</key> has been founded "
					// );
				} else if ((track == 0) && (items == 0) && (characName == 0) && (name == 0) && (playlists == 0) && (key == 1)) {
					// System.out.println(" <key> has been dismissed " );
					key = 0;
				} else if ((track == 0) && (items == 0) && (characName == 0) && (name == 0) && (playlists == 1) && (key == 1)  
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Name"))) {
					// System.out.println(" <key>Name</key> has been found " );
					name = 1;
				} else if ((track == 0) && (items == 0) && (characName == 0) && (name == 1) && (playlists == 1) && (key == 1) 
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals(playlistName))) {
					characName = 1;
					// System.out.println(" <key>name</key><string>XXX</string> has been founded " );
				} else if ((track == 0) && (items == 0) && (characName == 1) && (name == 1) && (playlists == 1) && (key == 1)   
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Playlist Items"))) {
					// System.out.println(" <key>Playlist Items</key> has been found " );
					items = 1;
				} else if ((track == 0) && (items == 1)  && (characName == 1) && (name == 1) && (playlists == 1) && (key == 1)   
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Track ID"))) {
					// System.out.println(" <key>Playlist Items</key> has been found " );
					track = 1;
				} else if  ((track == 1) && (items == 1)  && (characName == 1) && (name == 1) && (playlists == 1) && (key == 1) 
						&& (eventType == XMLEvent.CHARACTERS)) {
					// printText(xmlr);
					tracksList.add(xmlr.getText());
					track = 0; 
					// System.out.println(" <key>Track ID</key><integer>XXX</integer> has been founded "�;
				} else if ((track == 0) && (items == 1) && (characName == 1) && (name == 1) && (playlists == 1) && (key == 1)
						&& (eventType == XMLEvent.END_ELEMENT)
						&& (xmlr.getName().toString().equals("array"))) {
					stop = 1; 	
				} 		
            } // end while
        } catch(XMLStreamException ex) {
            System.out.println(ex.getMessage());
            if(ex.getNestedException() != null)ex.getNestedException().printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
		return tracksList;
	}
	
	/**
	 * Extract from Library, the location of a Track ID
	 * TODO : Same function with an input-given list of TrackIDs
	 * TODO : error management
	 * 
	 * @param iTrackID
	 * @return
	 */
	public String getTrackLocation(String iTrackID) {
		String trackLoc = null;
		
        try{                                                
        	XMLStreamReader xmlr = xmlif.createXMLStreamReader(libraryFileName, new FileInputStream(libraryFile));	
            int eventType = xmlr.getEventType();           
            
            int key = 0, tracks = 0, trackFound = 0, trackId = 0, integer = 0, trackType = 0, string = 0, file = 0, location = 0, stop = 0;
            
            while (xmlr.hasNext() && stop == 0) {
                eventType = xmlr.next();                   
                //printEventType(eventType);
                
                // KEEP SEARCHING FOR "<key>Tracks"
				if ((key == 0) && (tracks == 0) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 1; 
				} else if ((key == 1) && (tracks == 0) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Tracks"))) {
					tracks = 1; // NOW SEARCHING FOR "<key>XXXX", where XXX is equal to iTrackID
					//System.out.println("NOW SEARCHING FOR <key>XXXX");
				} else if ((key == 1) && (tracks == 0) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)) {
					key--; // KEEP SEARCHING FOR "<key>Tracks"
				} else if ((key == 1) && (tracks == 1) && (trackFound == 0)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 2;
				} else if ((key == 2) && (tracks == 1) && (trackFound == 0)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals(iTrackID))) {
					trackFound = 1; // NOW SEARCHING FOR "<key>Track ID"
				} else if ((key == 2) && (tracks == 1) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)) {
					key--; // KEEP SEARCHING FOR "<key>XXXX", where XXX is equal to iTrackID
				} else if ((key == 2) && (tracks == 1) && (trackFound == 1) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 3;
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Track ID"))) {
					trackId = 1; // NOW SEARCHING FOR "<integer>XXXX", where XXX is equal to iTrackID
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)) {
					key--; // KEEP SEARCHING FOR "<key>Track ID"
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1) 
						&& (trackId == 1) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("integer"))) {
					integer = 1;
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1) 
						&& (trackId == 1) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)) {
					stop = 1;	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, <integer> START_ELEMENT should be found !
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals(iTrackID))) {
					trackFound = 2; // NOW SEARCHING FOR "<key>Track Type"
					//System.out.println("NOW SEARCHING FOR <key>Track Type");
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)) {
					stop = 1; 	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, iTrackID CHARACTER value should be found !
				} else if ((key == 3) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 4;  
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Track Type"))) {
					trackType = 1;  // NOW SEARCHING FOR "<string>File"
					//System.out.println("NOW SEARCHING FOR <string>File");
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)) {
					key--;  // KEEP SEARCHING FOR "<key>Track Type"
				}  else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("string"))) {
					string = 1;  
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)) {
					stop = 1;	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, <string> START_ELEMENT should be found !
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 0) && (location == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("File"))) {
					file = 1;  // NOW SEARCHING FOR "<key>Location"
					//System.out.println("NOW SEARCHING FOR <key>Location");
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 0) && (location == 0)) {
					stop = 1;	// STOP SEARCHING 
								// RAISE AN EXCEPTION: If an element different from CHARACTERS has been encountered
								// LOG A WARNING IF Track Type is not a file !
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 5;  
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Location"))) {
					location = 1;  // NOW SEARCHING FOR "<string>XXXX", where XXX is the track location 
					//System.out.println("NOW SEARCHING FOR <string>XXXX, where XXX is the track location");
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 0)) {
					key--;  // KEEP SEARCHING FOR "<key>Location"
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 1)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("string"))) {
					string = 2;  
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 1)
						&& (eventType == XMLEvent.START_ELEMENT)) {
					stop = 1; 	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, <string> START_ELEMENT should be found !
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 2) 
						&& (file == 1) && (location == 1)
						&& (eventType == XMLEvent.CHARACTERS)) {
					trackLoc = xmlr.getText();
					stop = 1;
					//System.out.println("Track Location Found: " + trackLoc);
					// TODO: special treatment for escape character
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 2) 
						&& (file == 1) && (location == 1)) {
					stop = 1; 	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, trackLocation CHARACTERS should be found !
				}
            } // end while
        } catch(XMLStreamException ex) {
            System.out.println(ex.getMessage());
            if(ex.getNestedException() != null)ex.getNestedException().printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
		
		return trackLoc;
	}
	
	/**
	 * @param trackIDsList
	 * @return
	 */
	public List<String> getTracksLocationDumb(List <String> trackIDsList) {
		List<String> tracksLocations = new ArrayList<String>();
		
        for (String trackID : trackIDsList) {
        	tracksLocations.add(getTrackLocation(trackID));
        }
        
		return tracksLocations;
	}
	
	public List<Path> getTracksLocation(List <String> trackIDsList) {
		List<Path> tracksLocations = new ArrayList<Path>();
		
		try{                                                
        	XMLStreamReader xmlr = xmlif.createXMLStreamReader(libraryFileName, new FileInputStream(libraryFile));	
            int eventType = xmlr.getEventType();           
            
            int key = 0, tracks = 0, trackFound = 0, trackId = 0, integer = 0, trackType = 0, string = 0, file = 0, location = 0, locFound = 0, endDic = 0, stop = 0;
            URL url = null;
            
            while (xmlr.hasNext() && stop == 0) {
                eventType = xmlr.next();                   
                //printEventType(eventType);
                
                // KEEP SEARCHING FOR "<key>Tracks"
				if ((key == 0) && (tracks == 0) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 1; 
				} else if ((key == 1) && (tracks == 0) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Tracks"))) {
					tracks = 1; // NOW SEARCHING FOR "<key>XXXX", where XXX is equal to iTrackID
					//System.out.println("NOW SEARCHING FOR <key>XXXX");
				} else if ((key == 1) && (tracks == 0) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)) {
					key--; // KEEP SEARCHING FOR "<key>Tracks"
				} else if ((key == 1) && (tracks == 1) && (trackFound == 0) ///////////// RESTART POINT /////////
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) /*&& (endDic == 0)*/ // If any Location has already been found (locFound) and reset endDic == 1, if not endDic = 0
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 2;
					if (endDic == 1) endDic = 0; // Means we are entering a new track !
				} else if ((key == 2) && (tracks == 1) && (trackFound == 0)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (trackIDsList.contains(xmlr.getText()))) {
					trackFound = 1; // NOW SEARCHING FOR "<key>Track ID"
				} else if ((key == 2) && (tracks == 1) && (trackFound == 0) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)) {
					key--; // KEEP SEARCHING FOR "<key>XXXX", where XXX is equal to iTrackID
				} else if ((key == 2) && (tracks == 1) && (trackFound == 1) 
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 3;
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Track ID"))) {
					trackId = 1; // NOW SEARCHING FOR "<integer>XXXX", where XXX is equal to iTrackID
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)) {
					key--; // KEEP SEARCHING FOR "<key>Track ID"
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1) 
						&& (trackId == 1) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("integer"))) {
					integer = 1;
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1) 
						&& (trackId == 1) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)) {
					stop = 1;	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, <integer> START_ELEMENT should be found !
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (trackIDsList.contains(xmlr.getText()))) {
					trackFound = 2; // NOW SEARCHING FOR "<key>Track Type"
					//System.out.println("NOW SEARCHING FOR <key>Track Type");
				} else if ((key == 3) && (tracks == 1) && (trackFound == 1)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)) {
					stop = 1; 	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, iTrackID CHARACTER value should be found !
				} else if ((key == 3) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 4;  
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Track Type"))) {
					trackType = 1;  // NOW SEARCHING FOR "<string>File"
					//System.out.println("NOW SEARCHING FOR <string>File");
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)) {
					key--;  // KEEP SEARCHING FOR "<key>Track Type"
				}  else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("string"))) {
					string = 1;  
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)) {
					stop = 1;	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, <string> START_ELEMENT should be found !
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("File"))) {
					file = 1;  // NOW SEARCHING FOR "<key>Location"
					//System.out.println("NOW SEARCHING FOR <key>Location");
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 0)) {
					stop = 1;	// STOP SEARCHING 
								// RAISE AN EXCEPTION: If an element different from CHARACTERS has been encountered
								// LOG A WARNING IF Track Type is not a file !
				} else if ((key == 4) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("key"))) {
					key = 5;  
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 0)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)
						&& (xmlr.getText().equals("Location"))) {
					location = 1;  // NOW SEARCHING FOR "<string>XXXX", where XXX is the track location 
					//System.out.println("NOW SEARCHING FOR <string>XXXX, where XXX is the track location");
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 0)
						&& (locFound == 0) && (endDic == 0)) {
					key--;  // KEEP SEARCHING FOR "<key>Location"
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 1)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)
						&& (xmlr.getName().toString().equals("string"))) {
					string = 2;  
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 1) 
						&& (file == 1) && (location == 1)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.START_ELEMENT)) {
					stop = 1; 	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, <string> START_ELEMENT should be found !
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 2) 
						&& (file == 1) && (location == 1)
						&& (locFound == 0) && (endDic == 0)
						&& (eventType == XMLEvent.CHARACTERS)) {
					//System.out.println("Track Location Found: " + xmlr.getText());
					url = new URL(URLDecoder.decode(xmlr.getText(), "UTF-8"));
					//System.out.println("Track Path: " + Paths.get(url.getPath().toString()));
					tracksLocations.add(Paths.get(url.getPath()));
					url = null;
					locFound = 1;
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 2) 
						&& (file == 1) && (location == 1)
						&& (locFound == 0) && (endDic == 0)) {
					stop = 1; 	// STOP SEARCHING OR EVEN BETTER, RAISE AN EXCEPTION
								// XML HAS AN INCOHERENT CONTENT, trackLocation CHARACTERS should be found !
				} else if ((key == 5) && (tracks == 1) && (trackFound == 2)
						&& (trackId == 1) && (integer == 1) 
						&& (trackType == 1) && (string == 2) 
						&& (file == 1) && (location == 1)
						&& (locFound == 1) && (endDic == 0)
						&& (eventType == XMLEvent.END_ELEMENT)
						&& (xmlr.getName().toString().equals("dict"))) {
					endDic = 1;
					// NOW SEARCHING FOR A NEW TRACK OF LIST, Reset Parsing state consequently
					key = 1; tracks = 1; trackFound = 0; trackId = 0; integer = 0; 
					trackType = 0;  string = 0;  file = 0; location = 0; locFound = 0;
				} else if ((key == 1) && (tracks == 1) && (trackFound == 0)
						&& (trackId == 0) && (integer == 0) 
						&& (trackType == 0) && (string == 0) 
						&& (file == 0) && (location == 0)
						&& (locFound == 0) && (endDic == 1)
						&& (eventType == XMLEvent.END_ELEMENT)
						&& (xmlr.getName().toString().equals("dict"))) {
					stop = 1; // WE'VE REACH THE END OF "TRACKS" <DICT></DICT> BLOC
				}
            } // end while
        } catch(XMLStreamException ex) {
            System.out.println(ex.getMessage());
            if(ex.getNestedException() != null)ex.getNestedException().printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
	        
		return tracksLocations;
	}
	
	/**
	 * Display the Name of all Playlists
	 * 
	 * @param playlistsMap
	 */
	public void displayPlaylists(Map<Integer, String> playlistsMap) {
		for (Map.Entry<Integer, String> entry : playlistsMap.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}	
	}
	
	/**
	 * Display Track IDs of a Playlist
	 * 
	 * @param playlistName
	 * @param plTrackMap
	 */
	public void displayPlaylistTrackIDs(String playlistName, Map<String, List<String>> plTrackMap) {
		// TODO: below extraction of it should be done outside this method !
		// (function's input should rather be a List<String>)
		List<String> trackIDslist = plTrackMap.get(playlistName);
		if (trackIDslist != null) {
			for (String trackID : trackIDslist) {
				System.out.println(trackID);
			}
		}
	}
	
	/**
	 * Display Track IDs of all Playlists
	 * 
	 * @param playlistsName
	 * @param playlistsTrackIDs
	 */
	public void displayAll(Map<Integer, String> playlistsName, Map<String, List<String>> playlistsTrackIDs) {        
        for (Map.Entry<Integer, String> playlistName : playlistsName.entrySet()) {
        	System.out.println("**" + playlistName.getKey() + " : " + playlistName.getValue());
        	
        	List<String> playlistTrackIDs = playlistsTrackIDs.get(playlistName.getValue());
			if (playlistTrackIDs != null) {
				for (String trackID : playlistTrackIDs) {
					System.out.println(trackID);
				}
			}
        }        
	}
}
