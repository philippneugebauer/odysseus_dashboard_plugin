package de.uniol.inf.is.odysseus.rcp.dashboard.part.datagrid;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Preconditions;

import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPunctuation;
import de.uniol.inf.is.odysseus.core.securitypunctuation.ISecurityPunctuation;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPart;

public class DataGridDashboardPart extends AbstractDashboardPart {

	private static final String ROW_COUNT_SAVE_KEY = "__rowCount";
	private static final String COLUMN_COUNT_SAVE_KEY = "__columnCount";
	private static final int DEFAULT_ROW_COUNT = 3;
	private static final int DEFAULT_COLUMN_COUNT = 3;
	
	private int rowCount = DEFAULT_ROW_COUNT;
	private int columnCount = DEFAULT_COLUMN_COUNT;
	private DataGridModel model;
	
	private TableViewer tableViewer;
	private Boolean refreshing = false;
	private Boolean editing = false;
	private Composite rootComposite;
	
	public DataGridDashboardPart() {
		model = new DataGridModel(this, rowCount, columnCount);
	}
	
	@Override
	public final void createPartControl(Composite parent, ToolBar toolbar) {
		rootComposite = createRootComposite(parent);
		
		tableViewer = createTableViewer(rootComposite);
		configureTableViewer(tableViewer);
		createProviders(tableViewer);
		
		setInputAndCreateColumns(tableViewer);
	}
	
	public final int getColumCount() {
		return columnCount;
	}
	
	public final int getRowCount() {
		return rowCount;
	}

	protected Composite createRootComposite(Composite parent) {
		Composite rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		rootComposite.setLayout(new GridLayout(1, false));
		rootComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		return rootComposite;
	}
	
	protected TableViewer createTableViewer(Composite composite) {
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		TableViewer tableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		return tableViewer;
	}

	protected void configureTableViewer(TableViewer tableViewer) {
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(false);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		hideSelectionIfNeeded(tableViewer);
	}

	private void hideSelectionIfNeeded(final TableViewer tableViewer) {
		tableViewer.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if( tableViewer.getTable().getItem(new Point(e.x,e.y)) == null ) {
					tableViewer.setSelection(new StructuredSelection());
					setEditingMode(false);
				}
			}
		});
	}

	private static void createColumnLabelProvider(TableViewerColumn column, final int index) {
		column.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				DataRow row = (DataRow)cell.getElement();
				cell.setText(row.getValue(index));
			}
		});
	}

	protected void createProviders(TableViewer tableViewer) {
		tableViewer.setContentProvider(new DataGridContentProvider());
	}

	@Override
	public void streamElementRecieved(IPhysicalOperator senderOperator, IStreamObject<?> element, int port) {
		model.updateValues(senderOperator, element);
	}

	@Override
	public void punctuationElementRecieved(IPhysicalOperator senderOperator, IPunctuation point, int port) {
		// do nothing
	}

	@Override
	public void securityPunctuationElementRecieved(IPhysicalOperator senderOperator, ISecurityPunctuation sp, int port) {
		// do nothing
	}
	
	public void refresh() {
		if (isEditingMode() || tableViewer == null || tableViewer.getTable().isDisposed()) {
			return;
		}

		synchronized (refreshing) {
			
			if (!refreshing) {
				refreshing = true;

				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						synchronized (refreshing) {
							try {
								if (!tableViewer.getTable().isDisposed()) {
									tableViewer.refresh();
								}
							} finally {
								refreshing = false;
							}
						}
					}
				});
			}
			
		}
	}
	
	private boolean isEditingMode() {
		boolean val;
		synchronized( editing ) {
			val = editing;
		}
		return val;
	}

	private void setEditingMode( boolean edit) {
		synchronized(editing) {
			editing = edit;
		}
	}
	
	public void setRowAndColumnCounts( int rowCount, int columnCount ) {
		Preconditions.checkArgument( rowCount > 0, "Row count must be positive");
		Preconditions.checkArgument( columnCount > 0, "Column count must be positive");
		
		if( tableViewer != null ) {
			deleteColumns(tableViewer);
		}
		
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.model = new DataGridModel( this, this.rowCount, this.columnCount, model );
		
		if( tableViewer != null ) {
			setInputAndCreateColumns(tableViewer);
			
			tableViewer.getTable().getParent().layout();
			tableViewer.refresh();
			
			fireChangeEvent();
		}
	}
	
	private void setInputAndCreateColumns(TableViewer tableViewer) {
		tableViewer.setInput(model);
		
		createColumns(tableViewer);
		createCellModifier(tableViewer, model);
		
		tableViewer.setColumnProperties(createColumnPropertiesStringArray(columnCount));
		tableViewer.setCellEditors(createCellEditorsArray(columnCount, tableViewer));
		
		tableViewer.getTable().layout();
		refresh();
	}
	
	private void createCellModifier(TableViewer tableViewer, final DataGridModel model) {
		tableViewer.setCellModifier(new ICellModifier() {

			@Override
			public boolean canModify(Object element, String property) {
				return true;
			}

			@Override
			public Object getValue(Object element, String property) {
				DataRow row = (DataRow)element;
				int rowIndex = model.getRowIndex(row);
				int columnIndex = Integer.valueOf(property);
				
				return model.getText(rowIndex, columnIndex);
			}

			@Override
			public void modify(Object element, String property, Object value) {
				setEditingMode(false);
				
				TableItem tableItem = (TableItem)element;
				DataRow row = (DataRow)tableItem.getData();
				int rowIndex = model.getRowIndex(row);
				int columnIndex = Integer.valueOf(property);
				
				String oldText = model.getText(rowIndex, columnIndex);
				String newText = value.toString();
				
				if( !newText.equals(oldText)) {
					model.setText(value.toString(), rowIndex, columnIndex);
					fireChangeEvent();
				}
			}
		});
		
	}

	private void createColumns(TableViewer tableViewer) {
		TableColumnLayout tableColumnLayout = (TableColumnLayout) tableViewer.getTable().getParent().getLayout();
		
		for( int i = 0; i < columnCount; i++ ) {
			TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
			tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(5, 25, true));
			
			createColumnLabelProvider(column, i);
		}
	}

	private CellEditor[] createCellEditorsArray(int count, TableViewer viewer) {
		CellEditor[] editors = new CellEditor[count];
		for( int i = 0; i < count; i++ ) {
			editors[i] = new TextCellEditor(viewer.getTable()) {
				@Override
				public void activate() {
					setEditingMode(true);
					
					super.activate();
				}
				
				@Override
				public void deactivate() {
					setEditingMode(false);
					
					super.deactivate();
				}
			};
		}
		return editors;
	}

	private static String[] createColumnPropertiesStringArray(int count) {
		String[] texts = new String[count];
		for (int i = 0; i < count; i++) {
			texts[i] = String.valueOf(i);
		}
		return texts;
	}
	
	private static void deleteColumns(TableViewer tableViewer) {
		Table table = tableViewer.getTable();
		
		try {
			table.setRedraw(false);
			disposeAllColumns(table);
			
		} finally {
			table.setRedraw(true);
		}
	}

	private static void disposeAllColumns(Table table) {
		while( table.getColumnCount() > 0 ) {
			table.getColumns()[0].dispose();
		}
	}
	
	@Override
	public void onLoad(Map<String, String> saved) {
		this.rowCount = tryParseInt(saved.get(ROW_COUNT_SAVE_KEY), DEFAULT_ROW_COUNT);
		this.columnCount = tryParseInt(saved.get(COLUMN_COUNT_SAVE_KEY), DEFAULT_COLUMN_COUNT);
		
		model = new DataGridModel( this, rowCount, columnCount);
		model.onLoad(saved);
	}
	
	private static int tryParseInt(String string, int defValue) {
		try {
			return Integer.valueOf(string);
		} catch( Throwable t ) {
			return defValue;
		}
	}

	@Override
	public Map<String, String> onSave() {
		Map<String, String> savedRows = model.onSave();
		savedRows.put(COLUMN_COUNT_SAVE_KEY, String.valueOf(columnCount));
		savedRows.put(ROW_COUNT_SAVE_KEY, String.valueOf(rowCount));
		return savedRows;
	}
	
	@Override
	public void onStart(Collection<IPhysicalOperator> physicalRoots) throws Exception {
		super.onStart(physicalRoots);
		
		model.setSchemaFromOperators(physicalRoots);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		model.clearSchemaFromOperators();
	}
}
