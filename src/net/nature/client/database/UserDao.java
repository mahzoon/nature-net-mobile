package net.nature.client.database;

import org.droidpersistence.dao.DroidDao;
import org.droidpersistence.dao.TableDefinition;

import android.database.sqlite.SQLiteDatabase;

public class UserDao extends DroidDao<User,Long> {
    public UserDao(TableDefinition<User> tableDefinition, SQLiteDatabase database) {
            super(User.class, tableDefinition, database);
    }
}
