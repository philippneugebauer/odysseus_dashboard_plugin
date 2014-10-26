package de.uniol.inf.is.odysseus.rcp.dashboard.part.datagrid;

import com.google.common.base.Preconditions;

public class DataRow {

	private static int idCounter = 0;
	
	private final int id;
	private final int columnCount;
	
	private String[] text;
	private String[] value;
	
	public DataRow( int columnCount) {
		Preconditions.checkArgument(columnCount > 0, "Column count must be positive!");
		
		id = idCounter++;
		this.columnCount = columnCount;
		
		text = createEmptyStringArray(columnCount);
		value = createEmptyStringArray(columnCount);
	}
	
	private static String[] createEmptyStringArray(int count) {
		String[] array = new String[count];
		for( int i = 0; i < count; i++ ) {
			array[i] = "";
		}
		return array;
	}
	
	public int getColumnCount() {
		return columnCount;
	}
	
	public String getValue( int column ) {
		return value[column];
	}
	
	public String getText( int column ) {
		return text[column];
	}
	
	public void setText( int column, String text ) {
		this.text[column] = text;
	}
	
	public void setValue( int column, String text ) {
		this.value[column] = text;
	}

	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof DataRow )) {
			return false;
		}
		
		if( obj == this ) {
			return true;
		}
		
		DataRow other = (DataRow)obj;
		return other.id == id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
