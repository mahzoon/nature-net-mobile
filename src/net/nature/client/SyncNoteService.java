package net.nature.client;

import net.nature.client.database.Note;

public interface SyncNoteService {
	
	// upload a node, return the sync id obtained by the server, if successful,
	// if not successful, return sync id = null, sync id is the id generated
	// by the online service
	String upload(Note note);
	
	// update the content of the note, assuming the note has already been
	// previously uploaded, and the sync id is available
	// return if update is successful
	boolean update(Note note);
}
