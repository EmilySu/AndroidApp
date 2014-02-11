package ws.local;

import android.app.Activity;

/**
 * An interface for opening other Android apps to display the useful info
 *
 */
public interface OpenLocalApps
{
	/**
	 * Starts an intent to open Google Maps to display the input address
	 * @param address - the address to display on Google Maps
	 */
	public void openGoogleMaps(Activity activity, String address);
	
	/**
	 * Starts an intent to open the camera to take picture or record videos
	 */
	public void recordVisualNotes(Activity activity);
	
	/**
	 * Starts an intent to open the voice recorder app to record voice notes
	 */
	public void recordAudioNotes(Activity activity);
	
	/**
	 * Starts an intent to listen to the recorded audio note
	 * @param filename - the audio file path to listen, must be a valid 
	 * audio file
	 */
	public void viewAudioNotes(Activity activity, String filename);
	
	/**
	 * Starts an intent to watch the recorded video notes
	 * @param filename - the video file path, must be a valid video file
	 */
	public void viewVideoNotes(Activity activity, String filename);
}
