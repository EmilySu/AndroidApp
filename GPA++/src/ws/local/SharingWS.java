package ws.local;

import android.app.Activity;

/**
 * A web service interface for sharing data to other android apps that use
 * web services
 *
 */
public interface SharingWS
{
	/**
	 * Starts an intent to send the file to an existing app that supports 
	 * sharing, such as email, dropbox, etc.
	 * @param filename - the file to share
	 */
	public void shareNotes(Activity activity, String filename);
}
