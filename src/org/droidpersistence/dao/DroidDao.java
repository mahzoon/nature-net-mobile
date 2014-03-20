/**
 * @author Douglas Cavalheiro (doug.cav@ig.com.br)
 */
package org.droidpersistence.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.droidpersistence.model.FieldModel;
import org.droidpersistence.util.DroidUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public abstract class DroidDao<T, ID extends Serializable> {
	
	private TableDefinition<T> tableDefinition;
	private String insertStatement; 		
	private String tableName;
	private String[] arrayColumns;
	private Field[] fieldDefinition;
	private SQLiteDatabase database;
	private SQLiteStatement statement;
	private String idColumn;
	private final Class<T> model;
	private List<FieldModel> listFieldModels;
	
	/**Create a instance of Dao class, setting the model, definition of model and the database*/
	public DroidDao(Class<T> model, TableDefinition<T> tableDefinition, SQLiteDatabase database){
		this.model = model;
		this.database = database;
						
		try {
			this.tableDefinition = tableDefinition;
		} catch (Exception e) {
			e.printStackTrace();
		}	
		setArrayColumns(getTableDefinition().getArrayColumns());
		setTableName(getTableDefinition().getTableName());
		setFieldDefinition(getTableDefinition().getFieldDefinition());
		createInsertStatement(getTableDefinition().getTableName(), getTableDefinition().getArrayColumns());
		setIdColumn(getTableDefinition().getPK());
		setListFieldModels(getTableDefinition().getLIST_FIELD_MODEL());
		
		if(getInsertStatement().trim() != ""){
			statement = this.database.compileStatement(getInsertStatement());
		}
		
	}

	/**Delete object*/
	public boolean delete(ID id) {
		boolean result = false;
			try{
				database.delete(getTableName(), getIdColumn() + " = ?", 
						new String[] { String.valueOf(id) } );
				result = true;
			}catch(Exception e){
				e.printStackTrace();				
			}			
		return result;
	}

	/**Delete All*/
	public boolean delete() {
		boolean result = false;
			try{
				database.delete(getTableName(), null, null );
				result = true;
			}catch(Exception e){
				e.printStackTrace();				
			}			
		return result;
	}
	
	/**Get a object by id*/
	public T get(ID id) {
		T object = null;
		Cursor cursor = database.query(getTableName(), getArrayColumns(), 
				getIdColumn() + " = ?", new String[]{String.valueOf(id)}, null, null, "1");
		if(cursor.moveToFirst()){
			try {
				object = buildDataFromCursor(cursor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(! cursor.isClosed()){
			cursor.close();
		}		
		return object;
	}


	/**List all items*/
	public List<T> getAll() {		
		List<T> objectList = new ArrayList<T>();
		Cursor cursor = database.query(getTableName(), getArrayColumns(), 
				null, null, null, null, "1");
		if(cursor.getCount() > 0){
			if(cursor.moveToFirst()){
				try {
					do{
						T object = buildDataFromCursor(cursor);
						if(object != null){
							objectList.add(object);
						}
					}
					while(cursor.moveToNext());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
		if(! cursor.isClosed()){
			cursor.close();
		}		
		return objectList;
	}
	
	/**List items by clause*/
	public List<T> getAllbyClause(String clause, String[] clauseArgs, String groupBy, String having, String orderBy) {		
		List<T> objectList = new ArrayList<T>();
		Cursor cursor = database.query(getTableName(), getArrayColumns(), 
				clause, clauseArgs, groupBy, having, orderBy);
		if(cursor.moveToFirst()){
			try {
				do{
					T object = buildDataFromCursor(cursor);
					if(object != null){
						objectList.add(object);
					}
				}
				while(cursor.moveToNext());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(! cursor.isClosed()){
			cursor.close();
		}		
		return objectList;
	}


	/**Get an Object by clause*/
	public T getByClause(String clause, String[] clauseArgs) {
		T object = null;
		Cursor cursor = database.query(getTableName(), getArrayColumns(), 
				clause, clauseArgs, null, null, "1");
		if(cursor.moveToFirst()){
			try {
				object = buildDataFromCursor(cursor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(! cursor.isClosed()){
			cursor.close();
		}		
		return object;
		
	}

	/**Saves the Object*/
	public long save(T object) throws Exception{
		long result = 0;
		
		if(getTableDefinition().getPK() == ""){
			statement.clearBindings();
			
			for(int e = 0; e < getArrayColumns().length; e++){
				for(int i = 0; i < object.getClass().getDeclaredMethods().length; i++){
					Method method = object.getClass().getDeclaredMethods()[i];
					if(method.getName().equalsIgnoreCase("get"+getArrayColumns()[e])){
						i = object.getClass().getDeclaredMethods().length;
						Type type = method.getReturnType();	
						try{
							if(type == int.class){
								Integer output = (Integer) method.invoke(object);						
								statement.bindLong(e+1, output.longValue());
							}else if(type == Long.class || type == Short.class || type == long.class){
								Long output = (Long) method.invoke(object);
								statement.bindLong(e+1, output);
							}else if(type == Double.class || type == double.class || type == float.class){
								Double output = (Double) method.invoke(object);
								statement.bindDouble(e+1, output);
							}else if(type == String.class){
								String output = (String) method.invoke(object);
								statement.bindString(e+1, output);
							}else if(type == Date.class){
								Date output = (Date) method.invoke(object);
								statement.bindString(e+1, DroidUtils.convertDateToString(output));
							}else if(type == byte[].class){
								byte[] output = (byte[]) method.invoke(object);
								statement.bindBlob(e+1, output);
							}else{
								statement.bindNull(e+1);
							}						
													
						}catch(Exception ex){
							throw new Exception(" Failed to invoke the method "+method.getName()+", cause:"+ex.getMessage());
						}
					}else if (method.getName().equalsIgnoreCase("is"+getArrayColumns()[e])){
						i = object.getClass().getDeclaredMethods().length;
						Type type = method.getReturnType();	
						try{
							if(type == Boolean.class || type == boolean.class){
								long output = 0;			
									if ((Boolean) method.invoke(object)){
										output = 1;
									}else{
										output = 0;
									}
								statement.bindLong(e+1, output);					
							}else{
								statement.bindNull(e+1);
							}												
						}catch(Exception ex){
							throw new Exception(" Failed to invoke the method "+method.getName()+", cause:"+ex.getMessage());
						}
					}
				}

			}
			
			result = statement.executeInsert();	
		}else{
			final ContentValues values = new ContentValues();
			String value;
			for(int e = 0; e < getListFieldModels().size(); e++){
				FieldModel fieldModel = getListFieldModels().get(e);
				for(int i = 0; i < object.getClass().getDeclaredMethods().length; i++){
					Method method = object.getClass().getDeclaredMethods()[i];
					if(method.getName().equalsIgnoreCase("get"+fieldModel.getFieldName())||
							method.getName().equalsIgnoreCase("is"+fieldModel.getFieldName())
							){
						i = object.getClass().getDeclaredMethods().length;
						Object outputMethod = method.invoke(object);
						Type type = method.getReturnType();	
						//helps if the return type of method is Date (java.Utils)
						if(type == Date.class){
							Date date = (Date) outputMethod;
							value = DroidUtils.convertDateToString(date);
						}else if (type == boolean.class){
							if (Boolean.parseBoolean(outputMethod.toString())){
								value = "1";
							}else{
								value = "0";
							}
						}else{
							value = outputMethod.toString();
						}
							
						values.put(fieldModel.getColumnName(), value );
					}
				}
			}
			
			result = database.insert(getTableName(), null, values);
		}
		
		
		return result;
	}


	/**Update the Object by ID*/
	public void update(T object, ID id) throws Exception{
		final ContentValues values = new ContentValues();
		String value;
		for(int e = 0; e < getListFieldModels().size(); e++){
			FieldModel fieldModel = getListFieldModels().get(e);
			for(int i = 0; i < object.getClass().getDeclaredMethods().length; i++){
				Method method = object.getClass().getDeclaredMethods()[i];
				if(method.getName().equalsIgnoreCase("get"+fieldModel.getFieldName()) ||
						method.getName().equalsIgnoreCase("is"+fieldModel.getFieldName())){
					i = object.getClass().getDeclaredMethods().length;
					Object outputMethod = method.invoke(object);
					Type type = method.getReturnType();	
					//helps if the return type of method is Date (java.Utils)
					if(type == Date.class){
						Date date = (Date) outputMethod;
						value = DroidUtils.convertDateToString(date);
					}else if (type == boolean.class){
						if (Boolean.parseBoolean(outputMethod.toString())){
							value = "1";
						}else{
							value = "0";
						}
					}else{
						value = outputMethod.toString();
					}
					values.put(fieldModel.getColumnName(), value );
				}
			}
		}
			
		database.update(getTableName(), values, getIdColumn() + " = ?", 
				new String[]{String.valueOf(id)});
	}
	
	/**Update the Object by Clause*/
	public void update(T object, String clause, String[] clauseArgs) throws Exception{
		final ContentValues values = new ContentValues();
		String value;
		for(int e = 0; e < getListFieldModels().size(); e++){
			FieldModel fieldModel = getListFieldModels().get(e);
			for(int i = 0; i < object.getClass().getDeclaredMethods().length; i++){
				Method method = object.getClass().getDeclaredMethods()[i];
				if(method.getName().equalsIgnoreCase("get"+fieldModel.getFieldName()) ||
						method.getName().equalsIgnoreCase("is"+fieldModel.getFieldName())){
					i = object.getClass().getDeclaredMethods().length;
					Object outputMethod = method.invoke(object);
					Type type = method.getReturnType();	
					//helps if the return type of method is Date (java.Utils)
					if(type == Date.class){
						Date date = (Date) outputMethod;
						value = DroidUtils.convertDateToString(date);
					}else if (type == boolean.class){
						if (Boolean.parseBoolean(outputMethod.toString())){
							value = "1";
						}else{
							value = "0";
						}
					}else{
						value = outputMethod.toString();
					}
						
					values.put(fieldModel.getColumnName(), value );
				}
			}
		}
			
		database.update(getTableName(), values, clause, 
				clauseArgs);
	}

	public String getInsertStatement() {
		return insertStatement;
	}

	public void setInsertStatement(String insertStatement) {
		this.insertStatement = insertStatement;
	}
	
	public TableDefinition<T> getTableDefinition() {
		return tableDefinition;
	}

	public void setTableDefinition(TableDefinition<T> tableDefinition) {
		this.tableDefinition = tableDefinition;
	}

	/**Build a insert statement to the model*/ 
	private void createInsertStatement(String tableName, String[] columns){
		StringBuffer values = new StringBuffer();
		StringBuffer tableColumns = new StringBuffer();
		
		for(int i = 0; i < columns.length; i++){
			if(i > 0){
				if(i < columns.length){
					values.append(",");
					tableColumns.append(",");
				}
			}
			values.append("?");
			tableColumns.append(columns[i]);
		}		
		setInsertStatement("insert into " + tableName + "(" + tableColumns + ") " + "values ( " + values + ")");
	}
	
	/**Transforms the row in a Object*/
	public T buildDataFromCursor(Cursor cursor) throws Exception{
		T object = null;
		
		Field[] fields = getFieldDefinition();		  		
		if(cursor != null){
			object = this.model.newInstance();
			
			Method[] methods = object.getClass().getMethods();
			
			for(int i = 0; i < cursor.getColumnCount(); i++){				
				
				try{						
													
					for (int e = 0; e < methods.length; e++){							
						
						if(methods[e].getName().trim().equalsIgnoreCase("set"+fields[i].getName())){							
							Method method = methods[e]; 
							e = methods.length;
							Type type = method.getParameterTypes()[0];				  
							if(type == int.class || type == Integer.class){
								method.invoke(object, Long.valueOf(cursor.getLong(i)).intValue() );
							}else if(type == Long.class || type == long.class){
								method.invoke(object, cursor.getLong(i));
							}else if(type == Double.class || type == double.class){
								method.invoke(object, cursor.getDouble(i));
							}else if(type == float.class){
								method.invoke(object, cursor.getFloat(i));
							}else if(type == String.class){
								method.invoke(object, cursor.getString(i));
							}else if(type == Date.class){
								method.invoke(object, DroidUtils.convertStringToDate(cursor.getString(i)));
							}else if(type == Short.class){
								method.invoke(object, cursor.getShort(i));
							}else if(type == Boolean.class || type == boolean.class){
								method.invoke(object, (cursor.getInt(i) == 1));
							}else{
								method.invoke(object, cursor.getBlob(i));
							}
						}
					}
				}catch(Exception e){
					throw new Exception(" Failed to cast a object, maybe a method not declared, cause:"+e.getMessage()); 
				}												
			}
		}
		if(object.getClass().getDeclaredFields().length == 0){
			throw new Exception("Cannot be cast a no field object!");
		}
		return (T) object;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String[] getArrayColumns() {
		return arrayColumns;
	}

	public void setArrayColumns(String[] arrayColumns) {
		this.arrayColumns = arrayColumns;
	}

	public Field[] getFieldDefinition() {
		return fieldDefinition;
	}

	public void setFieldDefinition(Field[] fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	public String getIdColumn() {
		return idColumn;
	}

	public void setIdColumn(String idColumn) {
		this.idColumn = idColumn;
	}

	public List<FieldModel> getListFieldModels() {
		return listFieldModels;
	}

	public void setListFieldModels(List<FieldModel> listFieldModels) {
		this.listFieldModels = listFieldModels;
	}
	
	

}
