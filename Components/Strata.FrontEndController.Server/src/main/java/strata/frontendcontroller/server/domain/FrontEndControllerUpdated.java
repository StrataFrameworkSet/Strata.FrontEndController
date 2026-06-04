//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerUpdated.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

public
class FrontEndControllerUpdated
    extends AbstractFrontEndControllerEvent
{
    public
    FrontEndControllerUpdated(FrontEndController src)
    {
        super("FrontEndControllerUpdated",src);
    }

    public
    FrontEndControllerUpdated(String correlId,FrontEndController src)
    {
        super("FrontEndControllerUpdated",correlId,src);
    }
}

//////////////////////////////////////////////////////////////////////////////
