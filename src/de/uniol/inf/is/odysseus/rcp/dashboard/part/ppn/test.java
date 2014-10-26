package de.uniol.inf.is.odysseus.rcp.dashboard.part.ppn;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class test extends Composite {

	private static final String ZERO = "0";
	private static final Color YELLOW = Display.getCurrent().getSystemColor(
			SWT.COLOR_YELLOW);
	private static final Color RED = Display.getCurrent().getSystemColor(
			SWT.COLOR_RED);

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public test(Composite parent, int style) {
		super(parent, style);
		setLayout(null);
		InputStream is = test.class.getResourceAsStream("/soccer_field.png");
		setBackgroundImage(new Image(Display.getDefault(), is));
		setBackgroundMode(SWT.INHERIT_DEFAULT);

		Label yellowOneOneQuadrant = new Label(this, SWT.NONE);
		yellowOneOneQuadrant.setBounds(43, 40, 13, 15);
		yellowOneOneQuadrant.setText(ZERO);
		yellowOneOneQuadrant.setForeground(YELLOW);

		Label redOneOneQuadrant = new Label(this, SWT.NONE);
		redOneOneQuadrant.setText(ZERO);
		redOneOneQuadrant.setBounds(43, 62, 19, 15);
		redOneOneQuadrant.setForeground(RED);

		Label yellowOneTwoQuadrant = new Label(this, SWT.NONE);
		yellowOneTwoQuadrant.setText(ZERO);
		yellowOneTwoQuadrant.setForeground(YELLOW);
		yellowOneTwoQuadrant.setBounds(43, 151, 13, 15);

		Label redOneTwoQuadrant = new Label(this, SWT.NONE);
		redOneTwoQuadrant.setText(ZERO);
		redOneTwoQuadrant.setForeground(RED);
		redOneTwoQuadrant.setBounds(43, 173, 19, 15);

		Label yellowOneThreeQuadrant = new Label(this, SWT.NONE);
		yellowOneThreeQuadrant.setText(ZERO);
		yellowOneThreeQuadrant.setForeground(YELLOW);
		yellowOneThreeQuadrant.setBounds(43, 272, 13, 15);

		Label redOneThreeQuadrant = new Label(this, SWT.NONE);
		redOneThreeQuadrant.setText(ZERO);
		redOneThreeQuadrant.setForeground(RED);
		redOneThreeQuadrant.setBounds(43, 294, 19, 15);

		Label yellowThreeThreeQuadrant = new Label(this, SWT.NONE);
		yellowThreeThreeQuadrant.setText(ZERO);
		yellowThreeThreeQuadrant.setForeground(YELLOW);
		yellowThreeThreeQuadrant.setBounds(222, 272, 13, 15);

		Label redThreeThreeQuadrant = new Label(this, SWT.NONE);
		redThreeThreeQuadrant.setText(ZERO);
		redThreeThreeQuadrant.setForeground(RED);
		redThreeThreeQuadrant.setBounds(222, 294, 19, 15);

		Label yellowThreeTwoQuadrant = new Label(this, SWT.NONE);
		yellowThreeTwoQuadrant.setText(ZERO);
		yellowThreeTwoQuadrant.setForeground(YELLOW);
		yellowThreeTwoQuadrant.setBounds(216, 151, 13, 15);

		Label redThreeTwoQuadrant = new Label(this, SWT.NONE);
		redThreeTwoQuadrant.setText(ZERO);
		redThreeTwoQuadrant.setForeground(RED);
		redThreeTwoQuadrant.setBounds(216, 173, 19, 15);

		Label yellowThreeOneQuadrant = new Label(this, SWT.NONE);
		yellowThreeOneQuadrant.setText(ZERO);
		yellowThreeOneQuadrant.setForeground(YELLOW);
		yellowThreeOneQuadrant.setBounds(216, 40, 13, 15);

		Label redThreeOneQuadrant = new Label(this, SWT.NONE);
		redThreeOneQuadrant.setText(ZERO);
		redThreeOneQuadrant.setForeground(RED);
		redThreeOneQuadrant.setBounds(216, 62, 19, 15);

		Label yellowTwoOneQuadrant = new Label(this, SWT.NONE);
		yellowTwoOneQuadrant.setText(ZERO);
		yellowTwoOneQuadrant.setForeground(YELLOW);
		yellowTwoOneQuadrant.setBounds(128, 40, 13, 15);

		Label redTwoOneQuadrant = new Label(this, SWT.NONE);
		redTwoOneQuadrant.setText(ZERO);
		redTwoOneQuadrant.setForeground(RED);
		redTwoOneQuadrant.setBounds(128, 62, 19, 15);

		Label yellowTwoTwoQuadrant = new Label(this, SWT.NONE);
		yellowTwoTwoQuadrant.setText(ZERO);
		yellowTwoTwoQuadrant.setForeground(YELLOW);
		yellowTwoTwoQuadrant.setBounds(128, 151, 13, 15);

		Label redTwoTwoQuadrant = new Label(this, SWT.NONE);
		redTwoTwoQuadrant.setText(ZERO);
		redTwoTwoQuadrant.setForeground(RED);
		redTwoTwoQuadrant.setBounds(128, 173, 19, 15);

		Label yellowTwoThreeQuadrant = new Label(this, SWT.NONE);
		yellowTwoThreeQuadrant.setText(ZERO);
		yellowTwoThreeQuadrant.setForeground(YELLOW);
		yellowTwoThreeQuadrant.setBounds(128, 272, 13, 15);

		Label redTwoThreeQuadrant = initializeLabel(RED);
		redTwoThreeQuadrant.setBounds(128, 294, 19, 15);
	}

	private Label initializeLabel(Color color) {
		Label label = new Label(this, SWT.NONE);
		label.setText(ZERO);
		label.setForeground(color);
		return label;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
