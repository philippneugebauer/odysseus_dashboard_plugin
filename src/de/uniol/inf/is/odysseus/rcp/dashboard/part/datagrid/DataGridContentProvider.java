package de.uniol.inf.is.odysseus.rcp.dashboard.part.datagrid;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DataGridContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	@Override
	public Object[] getElements(Object inputElement) {
		
		DataGridModel model = (DataGridModel)inputElement;
		DataRow[] rows = model.getRows();
		
		return rows;
	}

}
