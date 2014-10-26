package de.uniol.inf.is.odysseus.rcp.dashboard.part;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.uniol.inf.is.odysseus.core.collection.Tuple;
import de.uniol.inf.is.odysseus.core.metadata.IStreamObject;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPhysicalOperator;
import de.uniol.inf.is.odysseus.core.physicaloperator.IPunctuation;
import de.uniol.inf.is.odysseus.core.securitypunctuation.ISecurityPunctuation;
import de.uniol.inf.is.odysseus.rcp.dashboard.AbstractDashboardPart;

public abstract class AbstractChartDashboardPart extends AbstractDashboardPart {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractChartDashboardPart.class);

	private JFreeChart chart;
	private Dataset dataset;
	private ChartComposite chartComposite;

	@Override
	public void createPartControl(Composite parent, ToolBar toolbar) {
		dataset = Preconditions.checkNotNull(createDataset(), "Created data set must not be null!");
		chart = Preconditions.checkNotNull(createChart(), "Created chart must not be null!");

		decorateChart(chart);

		chartComposite = new ChartComposite(parent, SWT.NONE, this.chart, true);
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public final JFreeChart getChart() {
		return chart;
	}

	public final Dataset getDataset() {
		return dataset;
	}

	@Override
	public void onStart(Collection<IPhysicalOperator> physicalRoots) throws Exception {
		super.onStart(physicalRoots);
		startChart(physicalRoots);
	}

	@Override
	public void punctuationElementRecieved(IPhysicalOperator senderOperator, final IPunctuation punctuation, final int port) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!chartComposite.isDisposed()) {
					addPunctuationToChart();
				}
			}
		});
	}

	@Override
	public void securityPunctuationElementRecieved(IPhysicalOperator senderOperator, final ISecurityPunctuation sp, final int port) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!chartComposite.isDisposed()) {
					addSecurityPunctuationToChart();
				}
			}
		});
	}

	@Override
	public void streamElementRecieved(final IPhysicalOperator senderOperator, final IStreamObject<?> element, final int port) {
		if (!(element instanceof Tuple)) {
			LOG.error("Lines DashboardPart only applyable for Tuples!");
			return;
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!chartComposite.isDisposed()) {
					addStreamElementToChart(senderOperator, (Tuple<?>) element, port);
				}
			}
		});
	}

	protected void addPunctuationToChart() {
	}

	protected void addSecurityPunctuationToChart() {
	}

	protected abstract void addStreamElementToChart(IPhysicalOperator senderOperator, Tuple<?> element, int port);

	protected abstract JFreeChart createChart();

	protected abstract Dataset createDataset();

	protected abstract void decorateChart(JFreeChart chart);

	protected abstract void startChart(Collection<IPhysicalOperator> physicalRoots) throws Exception;
}
