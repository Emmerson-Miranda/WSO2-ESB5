package edu.emmerson.wso2.custom.mediator.trycatch;

import org.apache.synapse.ContinuationState;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.aspects.AspectConfiguration;
import org.apache.synapse.aspects.ComponentType;
import org.apache.synapse.aspects.flow.statistics.StatisticIdentityGenerator;
import org.apache.synapse.aspects.flow.statistics.collectors.OpenEventCollector;
import org.apache.synapse.aspects.flow.statistics.collectors.RuntimeStatisticCollector;
import org.apache.synapse.aspects.flow.statistics.data.artifact.ArtifactHolder;
import org.apache.synapse.continuation.ContinuationStackManager;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractListMediator;
import org.apache.synapse.mediators.FlowContinuableMediator;

public class CatchMediator extends AbstractListMediator  {

	private boolean execute = false;

	/**
	 * Executes the list of sub/child mediators
	 *
	 * @param synCtx
	 *            the current message
	 * @return true if filter condition fails. else returns as per List mediator
	 *         semantics
	 */
	public boolean mediate(MessageContext synCtx) {

		if (synCtx.getEnvironment().isDebuggerEnabled()) {
			if (super.divertMediationRoute(synCtx)) {
				return true;
			}
		}

		SynapseLog synLog = getLog(synCtx);

		if (synLog.isTraceOrDebugEnabled()) {
			synLog.traceOrDebug("Start : Catch mediator");

			if (synLog.isTraceTraceEnabled()) {
				synLog.traceTrace("Message : " + synCtx.getEnvelope());
			}
		}

		boolean result = true;
		if (execute) {
			synLog.traceOrDebug("Catch - executing child mediators");
			ContinuationStackManager.addReliantContinuationState(synCtx, 0, getMediatorPosition());
			result = super.mediate(synCtx);
			if (result) {
				ContinuationStackManager.removeReliantContinuationState(synCtx);
			}
		} else {
			synLog.traceOrDebug("Catch - skiping children execution");
		}

		synLog.traceOrDebug("End : Catch mediator");

		return result;
	}

	public boolean mediate(MessageContext synCtx, ContinuationState continuationState) {
		SynapseLog synLog = getLog(synCtx);

		if (synLog.isTraceOrDebugEnabled()) {
			synLog.traceOrDebug("Catch mediator : Mediating from ContinuationState");
		}

		boolean result = true;
		if (!continuationState.hasChild()) {
			result = super.mediate(synCtx, continuationState.getPosition() + 1);
		} else {
			FlowContinuableMediator mediator = (FlowContinuableMediator) getChild(continuationState.getPosition());

			result = mediator.mediate(synCtx, continuationState.getChildContState());

			if (RuntimeStatisticCollector.isStatisticsEnabled()) {
				((Mediator) mediator).reportCloseStatistics(synCtx, null);
			}
		}
		return result;
	}

	@Override
	public boolean isContentAware() {
		return false;
	}

	public void init(SynapseEnvironment se) {
		super.init(se);
	}

	@Override
	public Integer reportOpenStatistics(MessageContext messageContext, boolean isContentAltering) {
		if (!messageContext.isResponse()) {
			return OpenEventCollector.reportFlowContinuableEvent(messageContext, getMediatorName(),
					ComponentType.MEDIATOR, getAspectConfiguration(), isContentAltering() || isContentAltering);
		}
		return null;
	}

	@Override
	public void setComponentStatisticsId(ArtifactHolder holder) {
		if (getAspectConfiguration() == null) {
			configure(new AspectConfiguration(getMediatorName()));
		}
		String mediatorId = StatisticIdentityGenerator.getIdForFlowContinuableMediator(getMediatorName(),
				ComponentType.MEDIATOR, holder);
		getAspectConfiguration().setUniqueId(mediatorId);
		setStatisticIdForMediators(holder);
		StatisticIdentityGenerator.reportingFlowContinuableEndEvent(mediatorId, ComponentType.MEDIATOR, holder);
	}

	public boolean isExecute() {
		return execute;
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}

}
