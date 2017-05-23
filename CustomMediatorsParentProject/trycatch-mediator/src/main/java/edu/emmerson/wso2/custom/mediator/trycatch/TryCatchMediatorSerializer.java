package edu.emmerson.wso2.custom.mediator.trycatch;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractListMediatorSerializer;

/**
 * <pre>
 * &lt;trycatch&gt;
 *   &lt;try&gt;
 *      mediator+
 *   &lt;/try&gt;
 *   &lt;catch&gt;
 *      mediator+
 *   &lt;/catch&gt;
 * &lt;/trycatch&gt;
 * </pre>
 */
public class TryCatchMediatorSerializer  extends AbstractListMediatorSerializer {

    public OMElement serializeSpecificMediator(Mediator m) {

        if (!(m instanceof TryCatchMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }

        TryCatchMediator mediator = (TryCatchMediator) m;
        OMElement e = fac.createOMElement("trycatch", synNS);
        saveTracingState(e,mediator);
        serializeChildren(e, mediator.getList());
        return e;
    }

    public String getMediatorClassName() {
        return TryCatchMediator.class.getName();
    }
}
