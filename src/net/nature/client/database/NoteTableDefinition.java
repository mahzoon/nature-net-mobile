package net.nature.client.database;

import org.droidpersistence.dao.TableDefinition;

public class NoteTableDefinition extends TableDefinition<Note>{

    public NoteTableDefinition() {
            super(Note.class);              
    }
}
