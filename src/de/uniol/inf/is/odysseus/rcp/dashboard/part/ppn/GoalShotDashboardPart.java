package de.uniol.inf.is.odysseus.rcp.dashboard.part.ppn;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import com.google.common.collect.Lists;
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

	private final List<Tuple<?>> data = Lists.newArrayList();

	private Font titleFont;
	private String title = "";

	Composite footballFieldComposite;
	Label redTwoThreeQuadrant;
	Label redThreeOneQuadrant;
	Label redTwoOneQuadrant;
	Label redTwoTwoQuadrant;
	Label redOneOneQuadrant;
	Label redOneTwoQuadrant;
	Label redOneThreeQuadrant;
	Label redThreeThreeQuadrant;
	Label redThreeTwoQuadrant;

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

			GoalShotArea yellowOneOneArea = new GoalShotArea(-50, -33960,
					17463, -22633, initializeLabel(yellow, 43, 40, 13, 15));
			// TODO!!
			GoalShotArea redOneOneArea = new GoalShotArea(-50, -33960, 8707,
					-22633, initializeLabel(red, 43, 62, 19, 15));

			GoalShotArea yellowOneTwoArea = new GoalShotArea(17463, -33960,
					34976, -22633, initializeLabel(yellow, 43, 151, 13, 15));
			redOneTwoQuadrant = initializeLabel(red, 43, 173, 19, 15);

			GoalShotArea yellowOneThreeArea = new GoalShotArea(34976, -33960,
					52489, -22633, initializeLabel(yellow, 43, 272, 13, 15));
			redOneThreeQuadrant = initializeLabel(red, 43, 294, 19, 15);

			GoalShotArea yellowTwoOneArea = new GoalShotArea(-50, -22633,
					17463, -11317, initializeLabel(yellow, 128, 40, 13, 15));
			redTwoOneQuadrant = initializeLabel(red, 128, 62, 19, 15);

			GoalShotArea yellowTwoTwoArea = new GoalShotArea(17463, -22633,
					34976, -11317, initializeLabel(yellow, 128, 151, 13, 15));
			redTwoTwoQuadrant = initializeLabel(red, 128, 173, 19, 15);

			GoalShotArea yellowTwoThreeArea = new GoalShotArea(34976, -22633,
					52489, -11317, initializeLabel(yellow, 128, 272, 13, 15));
			redTwoThreeQuadrant = initializeLabel(red, 128, 294, 19, 15);

			GoalShotArea yellowThreeOneArea = new GoalShotArea(-50, -11317,
					17463, 0, initializeLabel(yellow, 216, 40, 13, 15));
			redThreeOneQuadrant = initializeLabel(red, 216, 62, 19, 15);

			GoalShotArea yellowThreeTwoArea = new GoalShotArea(17463, -11317,
					34976, 0, initializeLabel(yellow, 216, 151, 13, 15));
			redThreeTwoQuadrant = initializeLabel(red, 216, 173, 19, 15);

			GoalShotArea yellowThreeThreeArea = new GoalShotArea(34976, -11317,
					52489, 0, initializeLabel(yellow, 222, 272, 13, 15));
			redThreeThreeQuadrant = initializeLabel(red, 222, 294, 19, 15);
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
		if (element != null) {
			Serializable teamId = element.getAdditionalContent("team_id");

			synchronized (data) {
				data.add(0, (Tuple<?>) element);
			}
		}
	}
}
