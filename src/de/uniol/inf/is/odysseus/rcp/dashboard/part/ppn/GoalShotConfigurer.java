package de.uniol.inf.is.odysseus.rcp.dashboard.part.ppn;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPartConfigurer;
import de.uniol.inf.is.odysseus.rcp.dashboard.DashboardPartUtil;

/**
 * This class is the configuration file for the GoalShotDashboardPart.
 * The user written title is saved by the class and given to
 * the GoalShotDashboardPart
 *
 * @author Philipp Neugebauer
 *
 */
public class GoalShotConfigurer extends
		AbstractDashboardPartConfigurer<GoalShotDashboardPart> {

	private GoalShotDashboardPart dashboardPart;

	@Override
	public void init(GoalShotDashboardPart dashboardPartToConfigure,
			Collection<IPhysicalOperator> roots) {
		this.dashboardPart = dashboardPartToConfigure;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTitleControls(topComposite);
	}

	private void createTitleControls(Composite topComposite) {
		DashboardPartUtil.createLabel(topComposite, "Title");
		final Text titleText = DashboardPartUtil.createText(topComposite,
				dashboardPart.getTitle());
		titleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		titleText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dashboardPart.setTitle(titleText.getText());
				fireListener();
			}
		});
	}

	@Override
	public void dispose() {

	}

}
