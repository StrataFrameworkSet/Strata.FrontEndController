//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerCreated.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

public
class FrontEndControllerCreated
    extends AbstractFrontEndControllerEvent
{
    public
    FrontEndControllerCreated(FrontEndController src)
    {
        super("FrontEndControllerCreated",src);
    }

    public
    FrontEndControllerCreated(String correlId,FrontEndController src)
    {
        super("FrontEndControllerCreated",correlId,src);
    }
}

//////////////////////////////////////////////////////////////////////////////
