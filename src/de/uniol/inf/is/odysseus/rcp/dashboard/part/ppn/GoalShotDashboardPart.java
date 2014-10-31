package de.uniol.inf.is.odysseus.rcp.dashboard.part.ppn;

import java.io.IOException;
import java.io.InputStream;
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
	Label yellowTwoThreeQuadrant;
	Label yellowThreeOneQuadrant;
	Label redThreeOneQuadrant;
	Label yellowTwoOneQuadrant;
	Label redTwoOneQuadrant;
	Label yellowTwoTwoQuadrant;
	Label redTwoTwoQuadrant;
	Label yellowOneOneQuadrant;
	Label redOneOneQuadrant;
	Label yellowOneTwoQuadrant;
	Label redOneTwoQuadrant;
	Label yellowOneThreeQuadrant;
	Label redOneThreeQuadrant;
	Label yellowThreeThreeQuadrant;
	Label redThreeThreeQuadrant;
	Label yellowThreeTwoQuadrant;
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

			yellowOneOneQuadrant = initializeLabel(yellow, 43, 40, 13, 15);
			redOneOneQuadrant = initializeLabel(red, 43, 62, 19, 15);

			yellowOneTwoQuadrant = initializeLabel(yellow, 43, 151, 13, 15);
			redOneTwoQuadrant = initializeLabel(red, 43, 173, 19, 15);

			yellowOneThreeQuadrant = initializeLabel(yellow, 43, 272, 13, 15);
			redOneThreeQuadrant = initializeLabel(red, 43, 294, 19, 15);

			yellowThreeThreeQuadrant = initializeLabel(yellow, 222, 272, 13, 15);
			redThreeThreeQuadrant = initializeLabel(red, 222, 294, 19, 15);

			yellowThreeTwoQuadrant = initializeLabel(yellow, 216, 151, 13, 15);
			redThreeTwoQuadrant = initializeLabel(red, 216, 173, 19, 15);

			yellowThreeOneQuadrant = initializeLabel(yellow, 216, 40, 13, 15);
			redThreeOneQuadrant = initializeLabel(red, 216, 62, 19, 15);

			yellowTwoOneQuadrant = initializeLabel(yellow, 128, 40, 13, 15);
			redTwoOneQuadrant = initializeLabel(red, 128, 62, 19, 15);

			yellowTwoTwoQuadrant = initializeLabel(yellow, 128, 151, 13, 15);
			redTwoTwoQuadrant = initializeLabel(red, 128, 173, 19, 15);

			yellowTwoThreeQuadrant = initializeLabel(yellow, 128, 272, 13, 15);
			redTwoThreeQuadrant = initializeLabel(red, 128, 294, 19, 15);
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

	private void incrementNumber(Label label) {
		try {
			int number = Integer.parseInt(label.getText());
			label.setText(String.valueOf(number + 1));
		} catch (NumberFormatException e) {
			// do nothing
		}
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
			synchronized (data) {
				data.add(0, (Tuple<?>) element);
			}

		}
	}

}
