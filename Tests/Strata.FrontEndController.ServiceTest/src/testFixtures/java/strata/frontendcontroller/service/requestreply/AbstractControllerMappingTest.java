//////////////////////////////////////////////////////////////////////////////
// AbstractControllerMappingTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract
class AbstractControllerMappingTest
{
    @Test
    public void
    testAllOperationsMapped()
    {
        List<String> unmapped =
            findUnmappedOperations(getServiceInterface(),getControllerClass());

        Assertions.assertTrue(unmapped.isEmpty(),"unmapped operations: " + unmapped);
    }

    protected List<String>
    findUnmappedOperations(Class<?> service,Class<?> controller)
    {
        List<String> unmapped = new ArrayList<>();

        for (Method serviceMethod : service.getMethods())
        {
            if (serviceMethod.getDeclaringClass() == Object.class)
                continue;

            if (!isMapped(serviceMethod,controller))
                unmapped.add(serviceMethod.getName());
        }

        Collections.sort(unmapped);

        return unmapped;
    }

    protected abstract Class<?>
    getServiceInterface();

    protected abstract Class<?>
    getControllerClass();

    private boolean
    isMapped(Method serviceMethod,Class<?> controller)
    {
        Method controllerMethod;

        try
        {
            controllerMethod = controller.getMethod(serviceMethod.getName(),serviceMethod.getParameterTypes());
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }

        if (!serviceMethod.getReturnType().isAssignableFrom(controllerMethod.getReturnType()))
            return false;

        for (Annotation annotation : controllerMethod.getAnnotations())
        {
            if (annotation.annotationType().getSimpleName().endsWith("Mapping"))
                return true;
        }

        return false;
    }
}

//////////////////////////////////////////////////////////////////////////////
