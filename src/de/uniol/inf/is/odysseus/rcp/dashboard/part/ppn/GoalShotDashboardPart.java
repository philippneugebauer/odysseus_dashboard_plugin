package de.uniol.inf.is.odysseus.rcp.dashboard.part.ppn;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import de.uniol.inf.is.odysseus.core.collection.Tuple;
import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPunctuation;
import de.uniol.inf.is.odysseus.core.securitypunctuation.ISecurityPunctuation;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPart;

public class GoalShotDashboardPart extends AbstractDashboardPart {

	private static final Logger LOG = LoggerFactory
			.getLogger(GoalShotDashboardPart.class);
	private static final String ZERO = "0";

	private Font titleFont;
	private String title = "";

	Composite footballFieldComposite;
	Object leftHalfLockObject = new Object();
	Object rightHalfLockObject = new Object();
	List<GoalShotArea> leftHalf;
	List<GoalShotArea> rightHalf;

	boolean sidesChanged;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void createPartControl(Composite parent, ToolBar toolbar) {
		try (InputStream is = GoalShotDashboardPart.class
				.getResourceAsStream("/soccer_field.png");) {

			final Color yellow = Display.getCurrent().getSystemColor(
					SWT.COLOR_YELLOW);
			final Color red = Display.getCurrent()
					.getSystemColor(SWT.COLOR_RED);

			footballFieldComposite = new Composite(parent, SWT.NONE);
			GridData footballFieldGridData = new GridData(SWT.CENTER,
					SWT.CENTER, true, true, 1, 1);
			footballFieldGridData.widthHint = 278;
			footballFieldGridData.heightHint = 360;
			footballFieldComposite.setLayoutData(footballFieldGridData);
			footballFieldComposite.setLayout(null);
			footballFieldComposite.setBackground(Display.getCurrent()
					.getSystemColor(SWT.COLOR_WHITE));
			footballFieldComposite.setBackgroundImage(new Image(Display
					.getDefault(), is));
			footballFieldComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);

			if (!Strings.isNullOrEmpty(title)) {
				createLabel(footballFieldComposite);
			}

			leftHalf = new ArrayList<>();
			rightHalf = new ArrayList<>();

			GoalShotArea redOneOneArea = new GoalShotArea(-50, -33960, 17463,
					-22633, initializeLabel(red, 43, 40, 13, 15));
			leftHalf.add(redOneOneArea);
			GoalShotArea yellowOneOneArea = new GoalShotArea(-50, 22644, 17463,
					33965, initializeLabel(yellow, 216, 62, 19, 15));
			rightHalf.add(yellowOneOneArea);

			GoalShotArea redOneTwoArea = new GoalShotArea(17463, -33960, 34976,
					-22633, initializeLabel(red, 43, 151, 13, 15));
			leftHalf.add(redOneTwoArea);
			GoalShotArea yellowOneTwoArea = new GoalShotArea(17463, 22644,
					34976, 33965, initializeLabel(yellow, 216, 173, 19, 15));
			rightHalf.add(yellowOneTwoArea);

			GoalShotArea redOneThreeArea = new GoalShotArea(34976, -33960,
					52489, -22633, initializeLabel(red, 43, 272, 13, 15));
			leftHalf.add(redOneThreeArea);
			GoalShotArea yellowOneThreeArea = new GoalShotArea(34976, 22644,
					52489, 33965, initializeLabel(yellow, 222, 294, 19, 15));
			rightHalf.add(yellowOneThreeArea);

			GoalShotArea redTwoOneArea = new GoalShotArea(-50, -22633, 17463,
					-11317, initializeLabel(red, 128, 40, 13, 15));
			leftHalf.add(redTwoOneArea);
			GoalShotArea yellowTwoOneArea = new GoalShotArea(-50, 11322, 17463,
					22644, initializeLabel(yellow, 128, 62, 19, 15));
			rightHalf.add(yellowTwoOneArea);

			GoalShotArea redTwoTwoArea = new GoalShotArea(17463, -22633, 34976,
					-11317, initializeLabel(red, 128, 151, 13, 15));
			leftHalf.add(redTwoTwoArea);
			GoalShotArea yellowTwoTwoArea = new GoalShotArea(17463, 11322,
					34976, 22644, initializeLabel(yellow, 128, 173, 19, 15));
			rightHalf.add(yellowTwoTwoArea);

			GoalShotArea redTwoThreeArea = new GoalShotArea(34976, -22633,
					52489, -11317, initializeLabel(red, 128, 272, 13, 15));
			leftHalf.add(redTwoThreeArea);
			GoalShotArea yellowTwoThreeArea = new GoalShotArea(34976, 11322,
					52489, 22644, initializeLabel(yellow, 128, 294, 19, 15));
			rightHalf.add(yellowTwoThreeArea);

			GoalShotArea redThreeOneArea = new GoalShotArea(-50, -11317, 17463,
					0, initializeLabel(red, 216, 40, 13, 15));
			leftHalf.add(redThreeOneArea);
			GoalShotArea yellowThreeOneArea = new GoalShotArea(-50, 0, 8707,
					11322, initializeLabel(yellow, 43, 62, 19, 15));
			rightHalf.add(yellowThreeOneArea);

			GoalShotArea redThreeTwoArea = new GoalShotArea(17463, -11317,
					34976, 0, initializeLabel(red, 216, 151, 13, 15));
			leftHalf.add(redThreeTwoArea);
			GoalShotArea yellowThreeTwoArea = new GoalShotArea(17463, 0, 34976,
					11322, initializeLabel(yellow, 43, 173, 19, 15));
			rightHalf.add(yellowThreeTwoArea);

			GoalShotArea redThreeThreeArea = new GoalShotArea(34976, -11317,
					52489, 0, initializeLabel(red, 222, 272, 13, 15));
			leftHalf.add(redThreeThreeArea);
			GoalShotArea yellowThreeThreeArea = new GoalShotArea(34976, 0,
					52489, 11322, initializeLabel(yellow, 43, 294, 19, 15));
			rightHalf.add(yellowThreeThreeArea);
		} catch (IOException e) {
			// TODO show error message instead?
		}
	}

	private Label initializeLabel(Color color, int x, int y, int width,
			int height) {
		Label label = new Label(footballFieldComposite, SWT.NONE);
		label.setText(ZERO);
		label.setForeground(color);
		label.setBounds(x, y, width, height);
		return label;
	}

	private void createLabel(Composite topComposite) {
		Label label = new Label(topComposite, SWT.BOLD);
		label.setText(title);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setAlignment(SWT.CENTER);
		label.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));

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
		if (titleFont != null) {
			titleFont.dispose();
		}
		super.dispose();
	}

	@Override
	public void onLoad(Map<String, String> saved) {
		title = saved.get("Title");
	}

	@Override
	public Map<String, String> onSave() {
		Map<String, String> toSaveMap = Maps.newHashMap();
		toSaveMap.put("Title", title);
		return toSaveMap;
	}

	@Override
	public void onStart(Collection<IPhysicalOperator> physicalRoots)
			throws Exception {
		super.onStart(physicalRoots);

		if (physicalRoots.size() > 1) {
			LOG.warn("GoalShotDashboardPart supports only one query!");
		}
	}

	@Override
	public void punctuationElementRecieved(IPhysicalOperator senderOperator,
			IPunctuation punctuation, int port) {
		// do nothing
	}

	@Override
	public void securityPunctuationElementRecieved(
			IPhysicalOperator senderOperator, ISecurityPunctuation sp, int port) {
		// do nothing
	}

	@Override
	public void streamElementRecieved(IPhysicalOperator senderOperator,
			IStreamObject<?> element, int port) {
		if (element != null && element instanceof Tuple<?>) {
			Tuple<?> tuple = (Tuple<?>) element;

			// TODO: adapt for query changes
			int x = (int) tuple.getAttribute(6);
			int y = (int) tuple.getAttribute(7);
			int time = (int) tuple.getAttribute(0);

			if (!sidesChanged && time > 30) {
				halftimeChange();
				sidesChanged = true;
			}

			if (y >= 0) {
				synchronized (leftHalfLockObject) {
					handleLeftHalf(x, y);
				}
			} else {
				synchronized (rightHalfLockObject) {
					handleRightHalf(x, y);
				}
			}
		}
	}

	private void handleLeftHalf(int x, int y) {
		iterateOverListAndCheckMatch(leftHalf, x, y);
	}

	private void handleRightHalf(int x, int y) {
		iterateOverListAndCheckMatch(rightHalf, x, y);
	}

	private void iterateOverListAndCheckMatch(List<GoalShotArea> list, int x,
			int y) {
		for (GoalShotArea area : list) {
			if (area.isInsideAndIncrement(x, y)) {
				break;
			}
		}
	}

	private void halftimeChange() {
		for (int i = 0; i < 9; i++) {
			GoalShotArea leftArea = leftHalf.get(i);
			Label left = leftArea.getNumberLabel();
			GoalShotArea rightArea = leftHalf.get(i);
			Label right = rightArea.getNumberLabel();
			leftArea.setNumberLabel(right);
			rightArea.setNumberLabel(left);
		}
	}
}
