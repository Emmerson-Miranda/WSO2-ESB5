package edu.emmerson.wso2.custom.mediator.trycatch;

import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractListMediatorFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;
import org.apache.synapse.mediators.filters.InMediator;

/**
 * Factory for {@link InMediator} instances.
 * <p>
 * Configuration syntax:
 * <pre>
 * &lt;catch&gt;
 *    mediator+
 * &lt;/catch&gt;
 * </pre>
 */
public class CatchMediatorFactory extends AbstractListMediatorFactory {

    private static final QName CATCH_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "catch");

    public Mediator createSpecificMediator(OMElement elem, Properties properties) {
        CatchMediator mediator = new CatchMediator();
        // after successfully creating the mediator
        // set its common attributes such as tracing etc
        processAuditStatus(mediator,elem);
        addChildren(elem, mediator, properties);
        return mediator;
    }

    public QName getTagQName() {
        return CATCH_Q;
    }
}
