package net.nature.client.database;

import org.droidpersistence.dao.DroidDao;
import org.droidpersistence.dao.TableDefinition;

import android.database.sqlite.SQLiteDatabase;

public class NoteDao extends DroidDao<Note,Long> {
    public NoteDao(TableDefinition<Note> tableDefinition, SQLiteDatabase database) {
            super(Note.class, tableDefinition, database);
    }
}
