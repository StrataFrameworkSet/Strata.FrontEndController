//////////////////////////////////////////////////////////////////////////////
// AbstractFrontEndControllerEvent.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

import strata.server.core.domainevent.AbstractDomainEvent;

public abstract
class AbstractFrontEndControllerEvent
    extends AbstractDomainEvent<FrontEndController>
    implements IFrontEndControllerEvent
{
    protected
    AbstractFrontEndControllerEvent(String nm,FrontEndController src)
    {
        super(nm,src);
    }

    protected
    AbstractFrontEndControllerEvent(String nm,String correlId,FrontEndController src)
    {
        super(nm,correlId,src);
    }
}

//////////////////////////////////////////////////////////////////////////////
