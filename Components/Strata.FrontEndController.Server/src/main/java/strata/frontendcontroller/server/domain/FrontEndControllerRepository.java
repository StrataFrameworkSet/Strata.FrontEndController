//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

import strata.foundation.core.collection.IMultiMap;
import strata.foundation.core.collection.ListValuedMultiMap;
import strata.server.core.repository.AbstractRepository;
import strata.server.core.unitofwork.IUnitOfWork;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public
class FrontEndControllerRepository
    extends AbstractRepository<Long,FrontEndController>
    implements IFrontEndControllerRepository
{
    public
    FrontEndControllerRepository(IUnitOfWork uow)
    {
        super(FrontEndController.class,"primaryId",uow);
        getUnitOfWork()
            .registerQuery(
                "findByFoo",
                "select e from FrontEndController e where e.foo = :foo")
            .registerQuery(
                "findAllByIdIn",
                "select e from FrontEndController e where e.primaryId IN :frontendcontrollerIds")
            .registerQuery(
                "findAllByFooIn",
                "select e from FrontEndController e where e.foo IN :foos");

    }

    @Override
    public Map<Long,Optional<FrontEndController>>
    findAllByIdIn(Set<Long> ids)
    {
        Map<Long,Optional<FrontEndController>> result =
            getUnitOfWork()
                .findManyByQuery(
                    FrontEndController.class,
                    "findAllByIdIn",
                    Map.of("frontendcontrollerIds",ids))
                .stream()
                .collect(
                    Collectors.toMap(
                        FrontEndController::getPrimaryId,
                        Optional::ofNullable));

        ids
            .stream()
            .filter(id -> !result.containsKey(id))
            .forEach(id -> result.put(id,Optional.empty()));

        return result;

    }

    @Override
    public List<FrontEndController>
    findAllByFoo(String foo)
    {
        return
            getUnitOfWork()
                .findManyByQuery(
                    FrontEndController.class,
                    "findAllByFoo",
                    Map.of("foo",foo))
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public IMultiMap<String,FrontEndController>
    findAllByFooIn(Set<String> foos)
    {
        ListValuedMultiMap<String,FrontEndController> result =
            new ListValuedMultiMap<>();

        getUnitOfWork()
            .findManyByQuery(
                FrontEndController.class,
                "findAllByFooIn",
                Map.of("foos",foos))
            .forEach(entity -> result.put(entity.getFoo(),entity));

        foos
            .stream()
            .filter(foo -> !result.containsKey(foo))
            .forEach(foo -> result.putAll(foo,List.of()));

        return result;
    }
}

//////////////////////////////////////////////////////////////////////////////
