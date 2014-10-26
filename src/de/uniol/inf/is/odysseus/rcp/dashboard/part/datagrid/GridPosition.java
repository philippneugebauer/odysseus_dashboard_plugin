package de.uniol.inf.is.odysseus.rcp.dashboard.part.datagrid;

class GridPosition {
	public final int row;
	public final int column;
	
	public GridPosition( int row, int column ) {
		this.row = row;
		this.column = column;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof GridPosition)) {
			return false;
		}
		if( obj == this ) {
			return true;
		}
		
		GridPosition other = (GridPosition)obj;
		return other.row == row && other.column == column;
	}  
	
	@Override
	public int hashCode() {
		return 31 + row * 31 + column;
	}
}

