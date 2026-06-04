//////////////////////////////////////////////////////////////////////////////
// IFrontEndControllerEventSource.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

import strata.server.core.domainevent.IDomainEventSource;

public
interface IFrontEndControllerEventSource
    extends
        IDomainEventSource<
            FrontEndController,
            IFrontEndControllerEvent,
            IFrontEndControllerEventObserver> {}

//////////////////////////////////////////////////////////////////////////////