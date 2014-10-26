package de.uniol.inf.is.odysseus.rcp.dashboard.part.datagrid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uniol.inf.is.odysseus.core.collection.Tuple;
import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.sdf.schema.SDFAttribute;
import de.uniol.inf.is.odysseus.core.sdf.schema.SDFSchema;

public final class DataGridModel {

	private static final Logger LOG = LoggerFactory.getLogger(DataGridModel.class);

	private static final String ATTRIBUTE_TEXT_PREFIX = "=";
	private static final String ATTRIBUTE_SOURCE_SEPARATOR = ".";
	private static final String NO_VALUE_TEXT = "<no value>";
	private static final String INVALID_ATTRIBITE_TEXT = "<undefined>";
	private static final String UNKNOWN_TEXT = "<unknown>";
	private static final String NULL_TEXT = "<null>";

	private final DataRow[] rows;

	private final GridPositionMap positionMap = new GridPositionMap();
	private final List<SDFSchema> schemas = Lists.newArrayList();
	private final DataGridDashboardPart dashboardPart;

	public DataGridModel( DataGridDashboardPart dashboardPart, int rowCount, int columnCount ) {
		this(dashboardPart, rowCount, columnCount, null);
	}
	
	public DataGridModel(DataGridDashboardPart dashboardPart, int rowCount, int columnCount, DataGridModel model) {
		Preconditions.checkArgument(rowCount > 0, "Rowcount must be positive");
		Preconditions.checkArgument(columnCount > 0, "Columncount must be positive");
		Preconditions.checkNotNull(dashboardPart, "DashboardPart must not be null!");

		this.rows = createDataRowsArray(rowCount, columnCount);
		this.dashboardPart = dashboardPart;
		
		if( model != null ) {
			copyRowsFromOtherModel(model);
		}
	}

	private void copyRowsFromOtherModel(DataGridModel model) {
		for( int i = 0; i < Math.min(model.rows.length, this.rows.length); i++ ) {
			for( int j = 0; j < Math.min(model.rows[i].getColumnCount(), this.rows[i].getColumnCount()); j++) {
				setText(model.rows[i].getText(j), i, j);
				setValue(i, j, model.rows[i].getValue(j));
			}
		}
	}

	private static DataRow[] createDataRowsArray(int rowCount, int columnCount) {
		DataRow[] rows = new DataRow[rowCount];
		for (int i = 0; i < rowCount; i++) {
			rows[i] = new DataRow(columnCount);
		}
		return rows;
	}

	public DataRow[] getRows() {
		return rows;
	}

	public int getRowIndex(DataRow row) {
		Preconditions.checkNotNull( row, "Row to get index from must not be null!");
		
		for (int i = 0; i < rows.length; i++) {
			if (rows[i].equals(row)) {
				return i;
			}
		}

		return -1;
	}

	public String getValue(int row, int column) {
		checkIndices(row, column);
		
		return rows[row].getValue(column);
	}

	public String getText(int row, int column) {
		checkIndices(row, column);
		
		return rows[row].getText(column);
	}

	public void setText(String newText, int row, int column) {
		checkIndices(row, column);
		
		String oldText = rows[row].getText(column);
		GridPosition position = new GridPosition(row, column);

		positionMap.remove(oldText, position);
		positionMap.add(newText, position);

		if (newText != null) {
			if (!newText.startsWith(ATTRIBUTE_TEXT_PREFIX)) {
				setValue(row, column, newText);
			} else if( !isAttributeValid(newText.substring(1))) {
				
				if( !schemas.isEmpty() ) {
					setValue(row, column, INVALID_ATTRIBITE_TEXT);
				} else {
					setValue( row, column, UNKNOWN_TEXT );
				}
				
			} else {
				setValue(row, column, NO_VALUE_TEXT);
			}
		}

		rows[row].setText(column, newText);
	}

	private boolean isAttributeValid(String attributeName) {
		for( SDFSchema schema : schemas ) {
			for( SDFAttribute attribute : schema ) {
				if( attribute.getAttributeName().equals(attributeName) || (attribute.getSourceName() + "." + attribute.getAttributeName()).equals(attributeName)) {
					return true;
				}
			}
		}
		return false;
	}

	public void updateValues(IPhysicalOperator senderOperator, IStreamObject<?> element) {
		if (!(element instanceof Tuple)) {
			LOG.error("Data Grid Model only supports tuple and not {}", element.getClass());
			return;
		}

		Tuple<?> tuple = (Tuple<?>) element;
		SDFSchema outputSchema = senderOperator.getOutputSchema();
		int attributeCount = outputSchema.getAttributes().size();

		for (int attributeIndex = 0; attributeIndex < attributeCount; attributeIndex++) {
			SDFAttribute attribute = outputSchema.get(attributeIndex);
			Object attributeValue = tuple.getAttribute(attributeIndex);
			String attributeValueText = attributeValue != null ? attributeValue.toString() : NULL_TEXT;

			String attributeName = attribute.getAttributeName();
			String attributeFullname = attribute.getSourceName() + ATTRIBUTE_SOURCE_SEPARATOR + attributeName;

			updateValue(attributeValueText, attributeName);
			updateValue(attributeValueText, attributeFullname);
		}
	}

	private void updateValue(String attributeValueText, String attributeName) {
		if (positionMap.contains(attributeName)) {
			List<GridPosition> positions = positionMap.get(attributeName);
			for (GridPosition position : positions) {
				setValue(position.row, position.column, attributeValueText);
			}
		}
	}

	private void setValue(int row, int column, String newValue) {
		String oldValue = rows[row].getValue(column);
		if (oldValue == null && newValue != null) {
			rows[row].setValue(column, newValue);
			dashboardPart.refresh();
			return;
		}

		if (oldValue != null && newValue == null) {
			rows[row].setValue(column, "");
			dashboardPart.refresh();
			return;
		}

		if (!newValue.equals(oldValue)) {
			rows[row].setValue(column, newValue);
			dashboardPart.refresh();
			return;
		}
	}

	public void onLoad(Map<String, String> saved) {
		for( String key : saved.keySet() ) {
			if( key.contains(";")) {
				String[] splittedKey = key.split(";");
				int rowIndex = Integer.valueOf(splittedKey[0]);
				int columnIndex = Integer.valueOf(splittedKey[1]);
				
				String text = saved.get(key);
				setText(text, rowIndex, columnIndex);
			} 
		}
	}

	public Map<String, String> onSave() {
		Map<String, String> saved = Maps.newHashMap();
		
		for( int i = 0; i < rows.length; i++ ) {
			for( int j = 0; j < rows[i].getColumnCount(); j++ ) {
				saved.put(i + ";" + j, rows[i].getText(j));
			}
		}
		
		return saved;
	}

	private void checkIndices(int row, int column) {
		Preconditions.checkArgument(row >= 0, "Row index must be positive, not %s", row);
		Preconditions.checkArgument(column >= 0, "Column index must be positive, not %s", column);
		Preconditions.checkArgument(row < rows.length, "Row must be lower than the maximum row index of %s", rows.length);
	}

	public void setSchemaFromOperators(Collection<IPhysicalOperator> operators) {
		schemas.clear();
		
		for( IPhysicalOperator operator : operators ) {
			schemas.add(operator.getOutputSchema());
		}
		
		refreshTexts();
	}

	private void refreshTexts() {
		for( int row = 0; row < rows.length; row++ ) {
			for( int column = 0; column < rows[row].getColumnCount(); column++ ) {
				// to trigger value checks...
				setText(getText(row, column), row, column);
			}
		}
		
		dashboardPart.refresh();
	}

	public void clearSchemaFromOperators() {
		schemas.clear();
		
		refreshTexts();
	}

}
