package de.uniol.inf.is.odysseus.rcp.dashboard.part;

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

public class TableConfigurer extends AbstractDashboardPartConfigurer<TableDashboardPart> {

	private TableDashboardPart dashboardPart;

	@Override
	public void init(TableDashboardPart dashboardPartToConfigure, Collection<IPhysicalOperator> roots ) {
		this.dashboardPart = dashboardPartToConfigure;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createAttributesControls(topComposite);
		createMaxDataControls(topComposite);
		createTitleControls(topComposite);
	}

	private void createTitleControls(Composite topComposite) {
		DashboardPartUtil.createLabel( topComposite, "Title");
		final Text titleText = DashboardPartUtil.createText(topComposite, dashboardPart.getTitle());
		titleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		titleText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dashboardPart.setTitle(titleText.getText());
				fireListener();
			}
		});
	}

	private void createMaxDataControls(Composite topComposite) {
		DashboardPartUtil.createLabel( topComposite, "Max Data Count");
		final Text maxDataText = DashboardPartUtil.createText(topComposite, String.valueOf(dashboardPart.getMaxData()));
		maxDataText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		maxDataText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dashboardPart.setMaxData(Integer.valueOf(maxDataText.getText()));
				fireListener();
			}
		});
	}

	private void createAttributesControls(Composite topComposite) {
		DashboardPartUtil.createLabel(topComposite, "Attributes");
		final Text attributesInput = DashboardPartUtil.createText(topComposite, dashboardPart.getAttributeList());
		attributesInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		attributesInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dashboardPart.setAttributeList(attributesInput.getText());
				fireListener();
			}
		});
	}

	@Override
	public void dispose() {

	}

}
