package edu.emmerson.wso2.custom.mediator.trycatch;

import java.util.Iterator;
import java.util.List;

import org.apache.synapse.ContinuationState;
import org.apache.synapse.FaultHandler;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.aspects.AspectConfiguration;
import org.apache.synapse.aspects.ComponentType;
import org.apache.synapse.aspects.flow.statistics.StatisticIdentityGenerator;
import org.apache.synapse.aspects.flow.statistics.collectors.OpenEventCollector;
import org.apache.synapse.aspects.flow.statistics.collectors.RuntimeStatisticCollector;
import org.apache.synapse.aspects.flow.statistics.data.artifact.ArtifactHolder;
import org.apache.synapse.continuation.ContinuationStackManager;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractListMediator;
import org.apache.synapse.mediators.FlowContinuableMediator;
import org.apache.synapse.mediators.MediatorFaultHandler;

public class TryCatchMediator extends AbstractListMediator implements FlowContinuableMediator {

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
			synLog.traceOrDebug("Start : TryCatch mediator");

			if (synLog.isTraceTraceEnabled()) {
				synLog.traceTrace("Message : " + synCtx.getEnvelope());
			}
		}

		boolean result = true;

		synLog.traceOrDebug("TryCatch - executing child mediators");
		ContinuationStackManager.addReliantContinuationState(synCtx, 0, getMediatorPosition());
		
		//MediatorFaultHandler mfh = null;
		try{
			configExecution(true);
			result = super.mediate(synCtx);
			Axis2MessageContext o = ((Axis2MessageContext) synCtx);//synCtx.getProperty("ERROR_CODE")SENDING_FAULT ERROR_MESSAGE
			System.out.println(result);
			System.out.println(synCtx.getProperty(SynapseConstants.PROXY_SERVICE));
			System.out.println(synCtx.getProperty(SynapseConstants.ERROR_CODE));
			System.out.println(synCtx.getProperty(SynapseConstants.ERROR_MESSAGE));
			System.out.println(synCtx.getProperty(SynapseConstants.SENDING_FAULT));
			System.out.println(synCtx.getProperty(SynapseConstants.ERROR_EXCEPTION));
			
			//System.out.println(o.getProperties().get("proxy.name"));
			//System.out.println(o.getProperties().get("LAST_SEQ_FAULT_HANDLER"));
			//System.out.println(o.getProperties().get("__SYNAPSE_RESPONSE_STATE__"));
			//mfh = (MediatorFaultHandler)o.getProperties().get("LAST_SEQ_FAULT_HANDLER");
			
		}catch(Throwable t){
			result = false;
			Throwable err = t.getCause() != null ? t.getCause() : t;
        	synCtx.setProperty(SynapseConstants.ERROR_CODE, SynapseConstants.DEFAULT_ERROR);
            synCtx.setProperty(SynapseConstants.ERROR_MESSAGE, err.getMessage().split("\n")[0]);
            synCtx.setProperty(SynapseConstants.ERROR_DETAIL, FaultHandler.getStackTrace(err));
            synCtx.setProperty(SynapseConstants.ERROR_EXCEPTION, t);
            synCtx.setProperty("ERROR_CLASS", err.getClass().getSimpleName());
		}
		
		if(result == false){
			//avoiding duplicate execution of catch statement
			configExecution(false);//mfh.handleFault(synCtx);
			result = super.mediate(synCtx);
			if(result){
				/*
				synCtx.setProperty(SynapseConstants.ERROR_CODE, null);
	            synCtx.setProperty(SynapseConstants.ERROR_MESSAGE, null);
	            synCtx.setProperty(SynapseConstants.ERROR_DETAIL, null);
	            synCtx.setProperty(SynapseConstants.ERROR_EXCEPTION, null);
	            synCtx.setProperty("ERROR_CLASS", null);
	            
	            synCtx.setProperty(SynapseConstants.SENDING_FAULT, Boolean.FALSE);
	            
	            Axis2MessageContext o = ((Axis2MessageContext) synCtx);
	            o.getFaultStack().removeAllElements();
	            o.getFaultStack().clear();
	            */
			}
		}
		if (result) {
			ContinuationStackManager.removeReliantContinuationState(synCtx);
		}

		synLog.traceOrDebug("End : TryCatch mediator");

		return result;
	}

	private void configExecution(boolean runtry) {
		List<Mediator> l = this.getList();
		Iterator<Mediator> i = l.iterator();
		while(i.hasNext()){
			Mediator m = i.next();
			if(m instanceof TryMediator){
				((TryMediator)m).setExecute(runtry);
			}else{
				((CatchMediator)m).setExecute(!runtry);
			}
		}
	}

	public boolean mediate(MessageContext synCtx, ContinuationState continuationState) {
		SynapseLog synLog = getLog(synCtx);

		if (synLog.isTraceOrDebugEnabled()) {
			synLog.traceOrDebug("TryCatch mediator : Mediating from ContinuationState");
		}

		boolean result;
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
}
