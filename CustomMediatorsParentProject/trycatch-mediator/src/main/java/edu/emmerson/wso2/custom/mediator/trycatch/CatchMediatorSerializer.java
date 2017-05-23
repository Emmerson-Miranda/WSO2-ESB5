package edu.emmerson.wso2.custom.mediator.trycatch;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractListMediatorSerializer;

public class CatchMediatorSerializer extends AbstractListMediatorSerializer {

    public OMElement serializeSpecificMediator(Mediator m) {

        if (!(m instanceof CatchMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }

        CatchMediator mediator = (CatchMediator) m;
        OMElement e = fac.createOMElement("catch", synNS);
        saveTracingState(e,mediator);

        serializeChildren(e, mediator.getList());

        return e;
    }

    public String getMediatorClassName() {
        return CatchMediator.class.getName();
    }
}
