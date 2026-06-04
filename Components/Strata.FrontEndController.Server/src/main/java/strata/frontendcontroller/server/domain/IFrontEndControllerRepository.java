//////////////////////////////////////////////////////////////////////////////
// IFrontEndControllerRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

import strata.foundation.core.collection.IMultiMap;
import strata.server.core.repository.IRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public
interface IFrontEndControllerRepository
    extends IRepository<Long,FrontEndController>
{
    Map<Long,Optional<FrontEndController>>
    findAllByIdIn(Set<Long> ids);

    List<FrontEndController>
    findAllByFoo(String foo);
    
    IMultiMap<String,FrontEndController>
    findAllByFooIn(Set<String> foos);
}

//////////////////////////////////////////////////////////////////////////////