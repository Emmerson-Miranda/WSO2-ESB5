package edu.emmerson.wso2.custom.mediator.trycatch;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractListMediatorSerializer;

public class TryMediatorSerializer extends AbstractListMediatorSerializer {

    public OMElement serializeSpecificMediator(Mediator m) {

        if (!(m instanceof TryMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }

        TryMediator mediator = (TryMediator) m;
        OMElement e = fac.createOMElement("try", synNS);
        saveTracingState(e,mediator);

        serializeChildren(e, mediator.getList());

        return e;
    }

    public String getMediatorClassName() {
        return TryMediator.class.getName();
    }
}
