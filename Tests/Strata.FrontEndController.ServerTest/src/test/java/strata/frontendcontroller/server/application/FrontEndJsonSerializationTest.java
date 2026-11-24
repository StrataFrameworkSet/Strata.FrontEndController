//////////////////////////////////////////////////////////////////////////////
// FrontEndJsonSerializationTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.mapper.ObjectMapperSupplier;
import strata.frontendcontroller.service.sample.EchoInput;
import strata.frontendcontroller.service.sample.EchoOutput;
import strata.frontendcontroller.service.sample.GetEchoOutputReply;
import strata.frontendcontroller.service.sample.GetEchoOutputRequest;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class FrontEndJsonSerializationTest
{
    private ObjectMapper mapper;

    @BeforeEach
    public void
    setUp()
    {
        mapper = new ObjectMapperSupplier().get();
    }

    @Test
    public void
    testHandleEchoInputRequestRoundTrip() throws Exception
    {
        UUID    requestId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2024-01-02T03:04:05Z");

        HandleEchoInputRequest request =
            new HandleEchoInputRequest()
                .setRequestId(requestId)
                .setTimestamp(timestamp)
                .setInput(createInput());

        String json = mapper.writeValueAsString(request);

        HandleEchoInputRequest result = mapper.readValue(json,HandleEchoInputRequest.class);

        assertEquals(requestId,result.getRequestId());
        assertEquals(timestamp,result.getTimestamp());
        assertEquals("input-text",result.getInput().getText());
        assertEquals(42L,result.getInput().getUserId());
        assertEquals("src",result.getInput().getSource());
        assertEquals("cmd",result.getInput().getCommand());
        assertEquals(Optional.of("dest"),result.getInput().getDestination());
    }

    @Test
    public void
    testHandleEchoInputRequestIgnoresUnknownInputProperty() throws Exception
    {
        ObjectNode node = mapper.valueToTree(new HandleEchoInputRequest().setInput(createInput()));
        ((ObjectNode)node.get("input")).put("unknownInput","y");

        String json = mapper.writeValueAsString(node);

        HandleEchoInputRequest result = mapper.readValue(json,HandleEchoInputRequest.class);

        assertEquals("input-text",result.getInput().getText());
    }

    @Test
    public void
    testHandleEchoInputRequestRejectsUnknownTopLevelProperty()
    {
        ObjectNode node = mapper.valueToTree(new HandleEchoInputRequest().setInput(createInput()));
        node.put("unknownTopLevel","x");

        assertThrows(
            UnrecognizedPropertyException.class,
            () -> mapper.readValue(mapper.writeValueAsString(node),HandleEchoInputRequest.class));
    }

    @Test
    public void
    testHandleEchoInputReplyRoundTrip() throws Exception
    {
        HandleEchoInputReply reply =
            new HandleEchoInputReply()
                .setInput(createInput())
                .setOutput(createOutput());

        String json = mapper.writeValueAsString(reply);

        HandleEchoInputReply result = mapper.readValue(json,HandleEchoInputReply.class);

        assertInstanceOf(EchoInput.class,result.getInput());
        assertInstanceOf(EchoOutput.class,result.getOutput());
        assertEquals("input-text",result.getInput().getText());
        assertEquals("output-text",result.getOutput().getText());
    }

    @Test
    public void
    testGetEchoOutputRequestRoundTrip() throws Exception
    {
        UUID    requestId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2024-01-02T03:04:05Z");

        GetEchoOutputRequest request =
            new GetEchoOutputRequest()
                .setRequestId(requestId)
                .setTimestamp(timestamp)
                .setUserId(42L)
                .setSource("src")
                .setCommand("cmd")
                .setDestination("dest");

        String json = mapper.writeValueAsString(request);

        GetEchoOutputRequest result = mapper.readValue(json,GetEchoOutputRequest.class);

        assertEquals(requestId,result.getRequestId());
        assertEquals(timestamp,result.getTimestamp());
        assertEquals(42L,result.getUserId());
        assertEquals("src",result.getSource());
        assertEquals("cmd",result.getCommand());
        assertEquals("dest",result.getDestination());
    }

    @Test
    public void
    testGetEchoOutputReplyRoundTrip() throws Exception
    {
        GetEchoOutputReply reply = new GetEchoOutputReply().setOutput(createOutput());

        String json = mapper.writeValueAsString(reply);

        GetEchoOutputReply result = mapper.readValue(json,GetEchoOutputReply.class);

        assertInstanceOf(EchoOutput.class,result.getOutput());
        assertEquals(42L,result.getOutput().getUserId());
        assertEquals("src",result.getOutput().getSource());
        assertEquals(Optional.of("cmd"),result.getOutput().getCommand());
        assertEquals("output-text",result.getOutput().getText());
    }

    @Test
    public void
    testNoTypeProperty() throws Exception
    {
        String json = mapper.writeValueAsString(createInput());

        assertFalse(json.contains("\"@type\""));
        assertFalse(json.contains("\"type\""));
    }

    private EchoInput
    createInput()
    {
        return
            new EchoInput()
                .setUserId(42L)
                .setSource("src")
                .setCommand("cmd")
                .setDestination("dest")
                .setText("input-text");
    }

    private EchoOutput
    createOutput()
    {
        return
            new EchoOutput()
                .setUserId(42L)
                .setSource("src")
                .setCommand("cmd")
                .setDestination("dest")
                .setText("output-text");
    }
}

//////////////////////////////////////////////////////////////////////////////
