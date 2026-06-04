import {IFrontEndControllerServiceClient} from "./IFrontEndControllerServiceClient";
import {ICompletionStage} from "strata.foundation.core/Concurrent";
import {AbstractRestClient} from "strata.client.core/Service";
import {Guid} from "guid-typescript";
import {AbstractServiceRequest} from "strata.foundation.core/Transfer";
import {Instant} from "@js-joda/core";
import {
    HandleInputRequest,
    HandleInputReply,
    GetOutputRequest,
    GetOutputReply
} from "strata.frontendcontroller.service/RequestReply";

export
class FrontEndControllerServiceClient
    extends AbstractRestClient
    implements IFrontEndControllerServiceClient
{
    constructor(baseUrl: string)
    {
        super(baseUrl);
        this.setHeader("Content-Type","application/json");
        this.setHeader("Access-Control-Allow-Origin","*");
    }

    setAuthorization(accessToken: string): IFrontEndControllerServiceClient
    {
        if (super.hasHeader("Authorization"))
            super.clearHeader("Authorization");

        super.setHeader("Authorization","Bearer " + accessToken);
        return this;
    }

    close(): void
    {
        super.clearHeaders();
    }

    handleInput(request: HandleInputRequest): ICompletionStage<HandleInputReply>
    {
        console.log("FrontEndControllerServiceClient.handleInput");
        return this.doPost<HandleInputReply,HandleInputRequest>(
            "handle-input",
            this.initializeRequest(request));
    }

    getOutput(request: GetOutputRequest): ICompletionStage<GetOutputReply>
    {
        console.log("FrontEndControllerServiceClient.getOutput");
        return this.doPost<GetOutputReply,GetOutputRequest>(
            "get-output",
            this.initializeRequest(request));
    }

    private initializeRequest<R extends AbstractServiceRequest>(request: R): R
    {
        console.log("FrontEndControllerServiceClient.initializeRequest");
        if (request.requestId == null)
            request.requestId = Guid.create().toString();

        if (request.timestamp == null)
            request.timestamp = Instant.now().toString();

        return request;
    }
}
