//////////////////////////////////////////////////////////////////////////////
// ModelMapperTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Tag("CommitStage")
public
class ModelMapperTest
{
    private static ApplicationContext  context;
    private ModelMapper                mapper;

    @BeforeAll
    public static void
    initialize()
    {
        context =
            new AnnotationConfigApplicationContext(
                TestConfiguration.class);
    }

    @BeforeEach
    public void
    setUp()
    {
        mapper = context.getBean(ModelMapper.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        mapper = null;
    }

    @Test
    public void
    testMapperInitialized()
    {
        Assertions.assertNotNull(mapper);
    }
}

//////////////////////////////////////////////////////////////////////////////
