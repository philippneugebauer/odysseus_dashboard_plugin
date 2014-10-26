package de.uniol.inf.is.odysseus.rcp.dashboard.part;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPunctuation;
import de.uniol.inf.is.odysseus.core.securitypunctuation.ISecurityPunctuation;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPart;

public class TextDashboardPart extends AbstractDashboardPart {

	private final List<String> pendingElements = Lists.newLinkedList();

	private int receivedElements;
	private Text text;
	private Thread updateThread;

	private boolean showHeartbeats = false;
	private int maxElements = 10;
	private long updateInterval = 1000;

	@Override
	public void createPartControl(final Composite parent, ToolBar toolbar) {
		text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		text.setEditable(false);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		updateThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!parent.isDisposed()) {
					final Display disp = PlatformUI.getWorkbench().getDisplay();
					if (!disp.isDisposed()) {
						disp.asyncExec(new Runnable() {
							@Override
							public void run() {
								if (!text.isDisposed()) {
									refreshText();
								}
							}
						});
					}

					waiting(updateInterval);
				}
			}

		});

		updateThread.setName("StreamList Updater");
		updateThread.start();
	}

	@Override
	public void punctuationElementRecieved(IPhysicalOperator senderOperator, IPunctuation punctuation, int port) {
		synchronized (pendingElements) {
			if (!punctuation.isHeartbeat() || showHeartbeats) {
				pendingElements.add("Punctuation: " + punctuation);
				if (!isInfinite() && pendingElements.size() > maxElements) {
					pendingElements.remove(0);
				}
			}
		}
	}

	@Override
	public void securityPunctuationElementRecieved(IPhysicalOperator senderOperator, ISecurityPunctuation sp, int port) {
		punctuationElementRecieved(senderOperator, sp, port);
	}

	@Override
	public void streamElementRecieved(IPhysicalOperator senderOperator, IStreamObject<?> element, int port) {
		synchronized (pendingElements) {
			pendingElements.add(element != null ? element.toString() : "null");
			if (!isInfinite() && pendingElements.size() > maxElements) {
				pendingElements.remove(0);
			}
		}
	}

	

	public boolean isShowHeartbeats() {
		return showHeartbeats;
	}

	public void setShowHeartbeats(boolean showHeartbeats) {
		this.showHeartbeats = showHeartbeats;
	}

	public int getMaxElements() {
		return maxElements;
	}

	public void setMaxElements(int maxElements) {
		this.maxElements = maxElements;
	}

	public long getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	private boolean isInfinite() {
		return maxElements < 0;
	}
	
	@Override
	public void onLoad(Map<String, String> saved) {
		showHeartbeats = Boolean.valueOf(saved.get("ShowHeartbeats"));
		updateInterval = Long.valueOf(saved.get("UpdateInterval"));
		maxElements = Integer.valueOf(saved.get("MaxElements"));
	}
	
	@Override
	public Map<String, String> onSave() {
		Map<String, String> saveMap = Maps.newHashMap();
		saveMap.put("ShowHeartbeats", String.valueOf(showHeartbeats));
		saveMap.put("UpdateInterval", String.valueOf(updateInterval));
		saveMap.put("MaxElements", String.valueOf(maxElements));
		return saveMap;
	}

	private void refreshText() {
		synchronized (pendingElements) {
			if (pendingElements.isEmpty()) {
				return;
			}

			for (final String element : pendingElements) {
				text.append(element + "\n");
				receivedElements++;
				if (!isInfinite() ) {
					while(receivedElements > maxElements) {
						String txt = text.getText();
						final int pos = txt.indexOf("\n");
						txt = txt.substring(pos + 1);
						text.setText(txt);
						receivedElements--;
					}
				}
			}
			text.setSelection(text.getCharCount());
			pendingElements.clear();
		}
	}

	private static void waiting(long length) {
		try {
			Thread.sleep(length);
		} catch (final InterruptedException e) {
		}
	}
}
