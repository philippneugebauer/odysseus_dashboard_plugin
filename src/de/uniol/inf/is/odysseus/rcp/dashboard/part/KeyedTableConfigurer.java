package de.uniol.inf.is.odysseus.rcp.dashboard.part;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPartConfigurer;
import de.uniol.inf.is.odysseus.rcp.dashboard.DashboardPartUtil;

public class KeyedTableConfigurer extends AbstractDashboardPartConfigurer<KeyedTableDashboardPart> {

	private KeyedTableDashboardPart dashboardPart;

	@Override
	public void init(KeyedTableDashboardPart dashboardPartToConfigure, Collection<IPhysicalOperator> roots) {
		this.dashboardPart = dashboardPartToConfigure;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createAttributesControls(topComposite);
		createKeyAttribbutesControls(topComposite);
		createMaxDataControls(topComposite);
		createTitleControls(topComposite);
		createAgeControls(topComposite);
	}

	private void createAgeControls(Composite topComposite) {
		Button ageCheckBox = DashboardPartUtil.createCheckBox(topComposite, "Show age", dashboardPart.isShowAge());
		ageCheckBox.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button)e.widget;
				dashboardPart.setShowAge(b.getSelection());
			}
		});
		
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		ageCheckBox.setLayoutData(gd);
		
		DashboardPartUtil.createLabel(topComposite, "Maximum colored age (ms)");
		Text maxAgeText = DashboardPartUtil.createText(topComposite, String.valueOf(dashboardPart.getMaxAgeMillis()));
		maxAgeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		maxAgeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text text = (Text)e.widget;
				try {
					dashboardPart.setMaxAgeMillis(Long.valueOf(text.getText()));
				} catch( Throwable ignored ) {
				}
			}
		});
	}

	private void createKeyAttribbutesControls(Composite topComposite) {
		DashboardPartUtil.createLabel(topComposite, "Key attributes");
		final Text attributeListsText = DashboardPartUtil.createText(topComposite, dashboardPart.getKeyAttributes());
		attributeListsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		attributeListsText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dashboardPart.setKeyAttributes(attributeListsText.getText());
				fireListener();
			}
		});
	}

	private void createTitleControls(Composite topComposite) {
		DashboardPartUtil.createLabel(topComposite, "Title");
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
		DashboardPartUtil.createLabel(topComposite, "Max Data Count");
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
