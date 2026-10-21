//////////////////////////////////////////////////////////////////////////////
// FrontEndJsonSerializationTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.mapper.ObjectMapperSupplier;
import strata.frontendcontroller.service.requestreply.FrontEndInput;
import strata.frontendcontroller.service.requestreply.FrontEndInputMapper;
import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.FrontEndOutputMapper;
import strata.frontendcontroller.service.requestreply.GetOutputReply;
import strata.frontendcontroller.service.requestreply.HandleInputReply;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

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
        mapper.registerSubtypes(TestInput.class,TestOutput.class);
    }

    @Test
    public void
    testInputRoundTrip() throws Exception
    {
        TestInput input = createInput();

        String json = mapper.writeValueAsString(input);

        FrontEndInput result = mapper.readValue(json,FrontEndInput.class);

        TestInput actual = assertInstanceOf(TestInput.class,result);
        assertEquals(input.getUserId(),actual.getUserId());
        assertEquals(input.getSource(),actual.getSource());
        assertEquals(input.getCommand(),actual.getCommand());
        assertEquals(Optional.of("dest"),actual.getDestination());
        assertEquals(input.getPayload(),actual.getPayload());
    }

    @Test
    public void
    testOutputRoundTrip() throws Exception
    {
        TestOutput output = createOutput();

        String json = mapper.writeValueAsString(output);

        FrontEndOutput result = mapper.readValue(json,FrontEndOutput.class);

        TestOutput actual = assertInstanceOf(TestOutput.class,result);
        assertEquals(output.getUserId(),actual.getUserId());
        assertEquals(output.getSource(),actual.getSource());
        assertEquals(Optional.of("cmd"),actual.getCommand());
        assertEquals(output.getDestination(),actual.getDestination());
        assertEquals(output.getPayload(),actual.getPayload());
    }

    @Test
    public void
    testHandleInputRequestRoundTrip() throws Exception
    {
        UUID    requestId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2024-01-02T03:04:05Z");

        HandleInputRequest request = new HandleInputRequest();
        request.setRequestId(requestId);
        request.setTimestamp(timestamp);
        request.setInput(createInput());

        String json = mapper.writeValueAsString(request);

        HandleInputRequest result = mapper.readValue(json,HandleInputRequest.class);

        assertEquals(requestId,result.getRequestId());
        assertEquals(timestamp,result.getTimestamp());
        TestInput actual = assertInstanceOf(TestInput.class,result.getInput());
        assertEquals(42L,actual.getUserId());
        assertEquals("src",actual.getSource());
        assertEquals("cmd",actual.getCommand());
        assertEquals(Optional.of("dest"),actual.getDestination());
        assertEquals("input-payload",actual.getPayload());
        assertTrue(FrontEndInputMapper.of().map(TestInput.class,result).isPresent());
    }

    @Test
    public void
    testHandleInputReplyRoundTrip() throws Exception
    {
        HandleInputReply reply = new HandleInputReply();
        reply.setInput(createInput());
        reply.setOutput(createOutput());

        String json = mapper.writeValueAsString(reply);

        HandleInputReply result = mapper.readValue(json,HandleInputReply.class);

        TestInput input = assertInstanceOf(TestInput.class,result.getInput());
        TestOutput output = assertInstanceOf(TestOutput.class,result.getOutput());
        assertEquals("input-payload",input.getPayload());
        assertEquals("output-payload",output.getPayload());
        assertTrue(FrontEndInputMapper.of().map(TestInput.class,result).isPresent());
        assertTrue(FrontEndOutputMapper.of().map(TestOutput.class,result).isPresent());
    }

    @Test
    public void
    testGetOutputReplyRoundTrip() throws Exception
    {
        GetOutputReply reply = new GetOutputReply();
        reply.setOutput(createOutput());

        String json = mapper.writeValueAsString(reply);

        GetOutputReply result = mapper.readValue(json,GetOutputReply.class);

        TestOutput actual = assertInstanceOf(TestOutput.class,result.getOutput());
        assertEquals(42L,actual.getUserId());
        assertEquals("src",actual.getSource());
        assertEquals(Optional.of("cmd"),actual.getCommand());
        assertEquals("dest",actual.getDestination());
        assertEquals("output-payload",actual.getPayload());
        assertTrue(FrontEndOutputMapper.of().map(TestOutput.class,result).isPresent());
    }

    @Test
    public void
    testNoTypeDiscriminatorWritten() throws Exception
    {
        String json = mapper.writeValueAsString(createInput());

        assertFalse(json.contains("@class"));
        assertFalse(json.contains("@type"));
    }

    @Test
    public void
    testDeserializationFailsWithoutRegisteredSubtypes() throws Exception
    {
        ObjectMapper plain = new ObjectMapperSupplier().get();
        String       json  = plain.writeValueAsString(createInput());

        assertThrows(JsonProcessingException.class,() -> plain.readValue(json,FrontEndInput.class));
    }

    private TestInput
    createInput()
    {
        TestInput input = new TestInput();
        input.setUserId(42L);
        input.setSource("src");
        input.setCommand("cmd");
        input.setDestination("dest");
        input.setPayload("input-payload");
        return input;
    }

    private TestOutput
    createOutput()
    {
        TestOutput output = new TestOutput();
        output.setUserId(42L);
        output.setSource("src");
        output.setCommand("cmd");
        output.setDestination("dest");
        output.setPayload("output-payload");
        return output;
    }
}

//////////////////////////////////////////////////////////////////////////////
