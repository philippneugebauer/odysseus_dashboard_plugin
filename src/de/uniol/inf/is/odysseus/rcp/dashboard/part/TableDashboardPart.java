/*******************************************************************************
 * Copyright 2012 The Odysseus Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.uniol.inf.is.odysseus.rcp.dashboard.part;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uniol.inf.is.odysseus.core.collection.Tuple;
import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPunctuation;
import de.uniol.inf.is.odysseus.core.sdf.schema.SDFSchema;
import de.uniol.inf.is.odysseus.core.securitypunctuation.ISecurityPunctuation;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPart;

public class TableDashboardPart extends AbstractDashboardPart {

	private static final Logger LOG = LoggerFactory.getLogger(TableDashboardPart.class);

	private IPhysicalOperator operator;

	private TableViewer tableViewer;
	private TableColumnLayout tableColumnLayout;
	private Composite tableComposite;
	private Font titleFont;
	
	private String[] attributes;
	private int[] positions;
	private boolean refreshing = false;

	private final List<Tuple<?>> data = Lists.newArrayList();

	private String attributeList = "*";
	private int maxData = 10;
	private String title = "";
	
	
	public String getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(String attributeList) {
		this.attributeList = attributeList;
	}

	public int getMaxData() {
		return maxData;
	}

	public void setMaxData(int maxData) {
		this.maxData = maxData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void createPartControl(Composite parent, ToolBar toolbar) {
		if (Strings.isNullOrEmpty(attributeList)) {
			new Label(parent, SWT.NONE).setText("Attribute List is invalid!");
			return;
		}
		
		attributes = determineAttributes(attributeList);
		
		Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		topComposite.setLayout(new GridLayout(1, false));
		topComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		if( !Strings.isNullOrEmpty(title)) {
			createLabel(topComposite);
		}
		
		tableComposite = new Composite(topComposite, SWT.NONE);
		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		final Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		parent.layout();
	}
	
	private void createLabel(Composite topComposite) {
		Label label = new Label(topComposite, SWT.BOLD);
		label.setText(title);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setAlignment(SWT.CENTER);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		titleFont = createBoldFont(label.getFont());
		label.setFont(titleFont);
	}

	private static Font createBoldFont(Font baseFont) {
		FontData[] fontData = baseFont.getFontData();
		fontData[0].setStyle(SWT.BOLD);
		return new Font(Display.getCurrent(), fontData[0]);
	}
	
	@Override
	public void dispose() {
		if( titleFont != null ) {
			titleFont.dispose();
		}
		super.dispose();
	}

	@Override
	public void onLoad(Map<String, String> saved) {
		attributeList = saved.get("Attributes");
		maxData = Integer.valueOf(saved.get("MaxData"));
		title = saved.get("Title");
	}

	@Override
	public Map<String, String> onSave() {
		Map<String, String> toSaveMap = Maps.newHashMap();
		toSaveMap.put("Attributes", attributeList);
		toSaveMap.put("MaxData", String.valueOf(maxData));
		toSaveMap.put("Title", title);
		return toSaveMap;
	}

	@Override
	public void onStart(Collection<IPhysicalOperator> physicalRoots) throws Exception {
		super.onStart(physicalRoots);

		if (physicalRoots.size() > 1) {
			LOG.warn("Table DashboardPart only supports one query!");
		}

		operator = physicalRoots.iterator().next();
		positions = determinePositions(operator.getOutputSchema(), attributes);
		refreshAttributesList( operator.getOutputSchema() ); // if attributes was = "*"
		
		deleteColumns();
		final int colCount = positions.length;
		for (int i = 0; i < colCount; i++) {

			final int finalI = i;

			final TableViewerColumn fileNameColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			fileNameColumn.getColumn().setText(attributes[i]);
			tableColumnLayout.setColumnData(fileNameColumn.getColumn(), new ColumnWeightData(5, 25, true));
			fileNameColumn.setLabelProvider(new CellLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					final Tuple<?> tuple = (Tuple<?>) cell.getElement();
					final Object attrValue = tuple.getAttributes()[positions[finalI]];
					cell.setText(attrValue != null ? attrValue.toString() : "null");
				}
			});
		}

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(data);
		tableViewer.refresh();
		tableViewer.getTable().redraw();
		
		tableComposite.layout();
	}

	private void deleteColumns() {
		disposeAllColumns(tableViewer.getTable());
	}
	
	private static void disposeAllColumns(Table table) {
		while( table.getColumnCount() > 0 ) {
			table.getColumns()[0].dispose();
		}
	}

	@Override
	public void punctuationElementRecieved(IPhysicalOperator senderOperator, IPunctuation punctuation, int port) {
	}

	@Override
	public void securityPunctuationElementRecieved(IPhysicalOperator senderOperator, ISecurityPunctuation sp, int port) {
	}

	@Override
	public void streamElementRecieved(IPhysicalOperator senderOperator, IStreamObject<?> element, int port) {
		if( element != null ) {
			synchronized( data ) {
				data.add(0, (Tuple<?>) element);
				if (data.size() > maxData) {
					data.remove(data.size() - 1);
				}
			}
			
			if( !refreshing && tableViewer.getInput() != null) {
				refreshing = true;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						synchronized( data ) {
							if (!tableViewer.getTable().isDisposed()) {
								tableViewer.refresh();
							}
							refreshing = false;
						}
					}
				});
			}
		}
	}

	private void refreshAttributesList(SDFSchema outputSchema) {
		if( attributes.length == 0 ) {
			attributes = new String[outputSchema.size()];
			for( int i = 0; i< attributes.length; i++ ) {
				attributes[i] = outputSchema.getAttribute(i).getAttributeName();
			}
		}
	}

	private static String[] determineAttributes(final String attributeList) {
		if (attributeList.trim().equalsIgnoreCase("*")) {
			return new String[0];
		}
		
		String[] attributes = attributeList.trim().split(",");
		for (int i = 0; i < attributes.length; i++) {
			attributes[i] = attributes[i].trim();
		}
		return attributes;
	}

	private static int[] determinePositions(SDFSchema outputSchema, String[] attributes2) {
		int[] positions = null;
		if( attributes2.length > 0 ) {
			positions = new int[attributes2.length];
			for (int i = 0; i < attributes2.length; i++) {
				Optional<Integer> optPosition = getPosition(outputSchema, attributes2[i]);
				if( optPosition.isPresent() ) {
					positions[i] = optPosition.get();
				} else {
					throw new RuntimeException("Could not position of " + attributes2[i]);
				}
			}	
		} else {
			positions = new int[outputSchema.size()];
			for( int i = 0; i < positions.length; i++ ) {
				positions[i] = i;
			}
		}
		return positions;
	}

	private static Optional<Integer> getPosition(SDFSchema outputSchema, String attribute ) {
		for (int j = 0; j < outputSchema.size(); j++) {
			if (outputSchema.get(j).getAttributeName().equals(attribute)) {
				return Optional.of(j);
			}
		}
		return Optional.absent();
	}

}
