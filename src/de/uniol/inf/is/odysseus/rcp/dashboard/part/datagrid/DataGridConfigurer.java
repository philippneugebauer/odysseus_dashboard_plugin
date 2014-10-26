package de.uniol.inf.is.odysseus.rcp.dashboard.part.datagrid;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPartConfigurer;
import de.uniol.inf.is.odysseus.rcp.dashboard.DashboardPartUtil;

public class DataGridConfigurer extends AbstractDashboardPartConfigurer<DataGridDashboardPart> {

	private static final int MAX_COUNTER_VALUE = 100;
	private static final int VALUE_PAGE_INCREMENT = 10;
	
	private DataGridDashboardPart dashboardPart;
	
	@Override
	public void init(DataGridDashboardPart dashboardPartToConfigure, Collection<IPhysicalOperator> roots) {
		dashboardPart = dashboardPartToConfigure;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite rootComposite = createRootComposite(parent);
		
		Label rowsLabel = DashboardPartUtil.createLabel(rootComposite, "Rows");
		rowsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Spinner rowsSpinner = DashboardPartUtil.createSpinner(rootComposite, 1, MAX_COUNTER_VALUE);
		rowsSpinner.setPageIncrement(VALUE_PAGE_INCREMENT);
		rowsSpinner.setSelection(dashboardPart.getRowCount());
		
		Label columnsLabel = DashboardPartUtil.createLabel(rootComposite, "Columns");
		columnsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Spinner columnsSpinner = DashboardPartUtil.createSpinner(rootComposite, 1, MAX_COUNTER_VALUE);
		columnsSpinner.setPageIncrement(VALUE_PAGE_INCREMENT);
		columnsSpinner.setSelection(dashboardPart.getColumCount());
		
		rowsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateDashboardPart(rowsSpinner.getSelection(), columnsSpinner.getSelection());
			}
		});
		columnsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateDashboardPart(rowsSpinner.getSelection(), columnsSpinner.getSelection());
			}
		});
	}

	private static Composite createRootComposite(Composite parent) {
		Composite composite = new Composite( parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		return composite;
	}

	private void updateDashboardPart(int rowCount, int columnCount) {
		dashboardPart.setRowAndColumnCounts(rowCount, columnCount);
	}
	
	@Override
	public void dispose() {
		
	}

}
