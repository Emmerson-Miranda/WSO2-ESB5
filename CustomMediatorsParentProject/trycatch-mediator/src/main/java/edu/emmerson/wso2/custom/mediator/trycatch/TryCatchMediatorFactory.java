package edu.emmerson.wso2.custom.mediator.trycatch;

import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractListMediatorFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;

/**
 * <p>Creates a try-catch mediator instance to handle errors. The <b>catch</b> block only be executed if the code inside <b>try</b> raise an exception.</p>
 *
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
public class TryCatchMediatorFactory  extends AbstractListMediatorFactory {

    private static final QName TRYCATCH_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "trycatch");

    public Mediator createSpecificMediator(OMElement elem, Properties properties) {
        TryCatchMediator mediator = new TryCatchMediator();
        // after successfully creating the mediator
        // set its common attributes such as tracing etc
        processAuditStatus(mediator,elem);
        addChildren(elem, mediator, properties);
        return mediator;
    }

    public QName getTagQName() {
        return TRYCATCH_Q;
    }
}
