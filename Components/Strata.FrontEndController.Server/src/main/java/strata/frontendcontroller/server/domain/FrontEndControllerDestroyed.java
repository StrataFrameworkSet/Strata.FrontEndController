//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerDestroyed.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

public
class FrontEndControllerDestroyed
    extends AbstractFrontEndControllerEvent
{
    public
    FrontEndControllerDestroyed(FrontEndController src)
    {
        super("FrontEndControllerDestroyed",src);
    }

    public
    FrontEndControllerDestroyed(String correlId,FrontEndController src)
    {
        super("FrontEndControllerDestroyed",correlId,src);
    }
}

//////////////////////////////////////////////////////////////////////////////
