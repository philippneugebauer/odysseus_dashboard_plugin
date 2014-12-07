package de.uniol.inf.is.odysseus.rcp.dashboard.part.ppn;

import org.eclipse.swt.widgets.Label;

/**
 * This class represents the zones of the match field
 * and helps to locate and count the shots in the specific zones.
 *
 * @author Philipp Neugebauer
 *
 */
public class GoalShotArea {

	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private Label numberLabel;

	public GoalShotArea(int startX, int startY, int endX, int endY, Label label) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.numberLabel = label;
	}

	public Label getNumberLabel() {
		return numberLabel;
	}

	public void setNumberLabel(Label label) {
		this.numberLabel = label;
	}

	public boolean isInsideAndIncrement(int x, int y) {
		boolean isInside = startX <= x && endX > x && startY <= y && endY > y;
		if (isInside) {
			incrementLabelNumber();
		}
		return isInside;
	}

	private void incrementLabelNumber() {
		try {
			int number = Integer.parseInt(numberLabel.getText());
			numberLabel.setText(String.valueOf(number + 1));
		} catch (NumberFormatException e) {
			// do nothing
		}
	}
}
