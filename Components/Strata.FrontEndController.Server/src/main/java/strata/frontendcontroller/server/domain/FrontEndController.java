//////////////////////////////////////////////////////////////////////////////
// FrontEndController.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

import strata.server.core.entity.AbstractEntityAndDomainEventSource;

public
class FrontEndController
    extends
        AbstractEntityAndDomainEventSource<
            Long,
            FrontEndController,
            IFrontEndControllerEvent,
            IFrontEndControllerEventObserver>
    implements IFrontEndControllerEventSource
{
    private String foo;

    public
    FrontEndController()
    {
        super();
        foo = null;
    }

    @Override
    public FrontEndController
    setPrimaryId(Long primaryId)
    {
        return super.setPrimaryId(primaryId);
    }

    @Override
    public Long
    getPrimaryId()
    {
        return super.getPrimaryId();
    }

    public FrontEndController
    setFoo(String f)
    {
        foo = f;
        return this;
    }

    public String
    getFoo() { return foo; }

    public FrontEndController
    notifyCreated() { return super.notify(new FrontEndControllerCreated(this)); }

    public FrontEndController
    notifyUpdated() { return super.notify(new FrontEndControllerUpdated(this)); }

    public FrontEndController
    notifyDestroyed() { return super.notify(new FrontEndControllerDestroyed(this)); }

    @Override
    protected FrontEndController
    getSelf()
    {
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
