//////////////////////////////////////////////////////////////////////////////
// ITransactionalFrontEndControllerService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public
interface ITransactionalFrontEndControllerService
    extends IFrontEndControllerService
{}

//////////////////////////////////////////////////////////////////////////////
