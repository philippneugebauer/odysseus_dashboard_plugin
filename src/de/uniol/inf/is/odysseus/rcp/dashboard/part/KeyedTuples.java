package de.uniol.inf.is.odysseus.rcp.dashboard.part;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uniol.inf.is.odysseus.core.collection.Tuple;

public class KeyedTuples {

	private static final Logger LOG = LoggerFactory.getLogger(KeyedTuples.class);
	
	private final Map<Integer, Long> hashTimestampMap = Maps.newHashMap();
	private final Map<Integer, Integer> hashPositionMap = Maps.newHashMap();
	private final List<Tuple<?>> tuplesForTable = Lists.newLinkedList();
	private final int[] keyAttributeIndices;
	
	private int maxData = 0;

	public KeyedTuples( int maxData ) {
		this.keyAttributeIndices = null;
		this.maxData = maxData;
	}
	
	public KeyedTuples( int maxData, int[] keyAttributeIndices ) {
		Preconditions.checkNotNull(keyAttributeIndices, "array of keyattribute indices must not be null!");
		
		this.keyAttributeIndices = keyAttributeIndices;
		this.maxData = maxData;
	}
	
	public void setMaxData(int maxData) {
		this.maxData = maxData;
	}
	
	public int getMaxData() {
		return maxData;
	}
	
	public long getAge(Tuple<?> tuple) {
		Preconditions.checkNotNull(tuple, "Tuple to get age must not be null!");
		
		int hash = determineHashOfTuple(tuple);
		Long timestamp = hashTimestampMap.get(hash);
		
		return timestamp != null ? System.currentTimeMillis() - timestamp : 0;
	}

	private int determineHashOfTuple(Tuple<?> tuple) {
		if (keyAttributeIndices != null && keyAttributeIndices.length > 0) {
			return tuple.restrictedHashCode(keyAttributeIndices);
		} 
		return tuple.hashCode();
	}
	
	public void add( Tuple<?> tuple ) {
		int hash = determineHashOfTuple(tuple);
		hashTimestampMap.put(hash, System.currentTimeMillis());

		Integer currentPosition = hashPositionMap.get(hash);
		if (currentPosition == null) {
			currentPosition = addTupleSorted(tuplesForTable, tuple, keyAttributeIndices);
			for (Object someHash : hashPositionMap.keySet().toArray()) {
				Integer somePosition = hashPositionMap.get(someHash);
				if (somePosition >= currentPosition) {
					hashPositionMap.put((Integer) someHash, somePosition + 1);
				}
			}
			
			hashPositionMap.put(hash, currentPosition);
		} else {
			tuplesForTable.set(currentPosition, tuple);
		}

		while( maxData > 0 && tuplesForTable.size() > maxData ) {
			long oldest = Long.MAX_VALUE;
			Object oldestHash = null;
			for (Integer hash2 : hashTimestampMap.keySet()) {
				Long ts = hashTimestampMap.get(hash2);
				if (oldestHash == null || ts < oldest) {
					oldestHash = hash2;
					oldest = ts;
				}
			}

			int positionToRemove = hashPositionMap.get(oldestHash);
			tuplesForTable.remove(positionToRemove);
			hashTimestampMap.remove(oldestHash);
			hashPositionMap.remove(oldestHash);

			for (Object someHash : hashPositionMap.keySet().toArray()) {
				Integer somePosition = hashPositionMap.get(someHash);
				if (somePosition > positionToRemove) {
					hashPositionMap.put((Integer) someHash, somePosition - 1);
				}
			}	
		}
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static int addTupleSorted(List<Tuple<?>> list, Tuple<?> tuple, int[] indices) {
		if (indices != null && indices.length > 0) {
			Comparable[] valuesOfTuple = getAttributeValues(tuple, indices);

			int currentPos = 0;
			for (Tuple<?> tupleInList : list) {

				for (int i = 0; i < indices.length; i++) {
					Comparable value = tupleInList.getAttribute(indices[i]);
					Comparable otherValue = valuesOfTuple[i];

					try {
						int cmp = value.compareTo(otherValue);
						if (cmp > 0) {
							list.add(currentPos, tuple);
							return currentPos;
						} else if (cmp < 0) {
							break; // next tuple
						}
					} catch (Throwable t) {
						LOG.error("Cannot compare " + value + " with " + otherValue, t);
					}
				}

				currentPos++;
			}
		}

		list.add(tuple);
		return list.size() - 1;
	}

	@SuppressWarnings("rawtypes")
	private static Comparable[] getAttributeValues(Tuple<?> tuple, int[] indices) {
		Comparable[] values = new Comparable[indices.length];
		for (int i = 0; i < indices.length; i++) {
			values[i] = tuple.getAttribute(indices[i]);
		}
		return values;
	}
	
	public List<Tuple<?>> getTuplesForTable() {
		return tuplesForTable;
	}
}
