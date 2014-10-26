package de.uniol.inf.is.odysseus.rcp.dashboard.part.datagrid;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GridPositionMap {

	private static final String ATTRIBUTE_TEXT_PREFIX = "=";
	private static final String ATTRIBUTE_SOURCE_SEPARATOR = ".";

	private final Map<String, List<GridPosition>> attributePositions = Maps.newHashMap();

	public void add(String newText, GridPosition position) {
		if (!Strings.isNullOrEmpty(newText)) {
			String newTextTrimmed = newText.trim();
			if (newTextTrimmed.startsWith(ATTRIBUTE_TEXT_PREFIX)) {
				String attributeFullname = newTextTrimmed.substring(ATTRIBUTE_TEXT_PREFIX.length());

				int pos = attributeFullname.indexOf(ATTRIBUTE_SOURCE_SEPARATOR);
				if (pos != -1) {
					String attributeName = attributeFullname.substring(pos + 1);
					addGridPosition(position, attributeName);
				}

				addGridPosition(position, attributeFullname);
			}
		}
	}

	public void remove(String oldText, GridPosition position) {
		if (!Strings.isNullOrEmpty(oldText)) {
			if (oldText.startsWith(ATTRIBUTE_TEXT_PREFIX)) {
				String attributeFullname = oldText.substring(ATTRIBUTE_TEXT_PREFIX.length());

				int pos = attributeFullname.indexOf(ATTRIBUTE_SOURCE_SEPARATOR);
				if (pos != -1) {
					String attributeName = attributeFullname.substring(pos + 1);
					removeGridPosition(position, attributeName);
				}

				removeGridPosition(position, attributeFullname);
			}
		}
	}
	
	public boolean contains( String key ) {
		return attributePositions.containsKey(key);
	}
	
	public List<GridPosition> get( String key ) {
		return attributePositions.get(key);
	}

	private void removeGridPosition(GridPosition position, String oldText) {
		if (attributePositions.containsKey(oldText)) {
			List<GridPosition> positions = attributePositions.get(oldText);
			positions.remove(position);
			if (positions.isEmpty()) {
				attributePositions.remove(oldText);
			}
		}
	}

	private void addGridPosition(GridPosition position, String text) {
		List<GridPosition> positionList;
		if (attributePositions.containsKey(text)) {
			positionList = attributePositions.get(text);
		} else {
			positionList = Lists.newArrayList();
			attributePositions.put(text, positionList);
		}
		positionList.add(position);
	}
}
