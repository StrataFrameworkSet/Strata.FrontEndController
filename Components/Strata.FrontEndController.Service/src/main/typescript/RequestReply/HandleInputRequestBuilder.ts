import {AbstractServiceRequestBuilder} from "strata.foundation.core/Transfer";
import type {HandleInputRequest}      from "./HandleInputRequest";
import type {FrontEndInput}           from "./FrontEndInput";

export
class HandleInputRequestBuilder
    extends AbstractServiceRequestBuilder<HandleInputRequest>
{
    private input: FrontEndInput;

    constructor()
    {
        super();
        this.input = null;
    }

    setInput(input: FrontEndInput): HandleInputRequestBuilder
    {
        this.input = input;
        return this;
    }

    getInput(): FrontEndInput
    {
        return this.input;
    }

    build(): HandleInputRequest
    {
        const request: HandleInputRequest =
            {
                requestId: this.getRequestId(),
                timestamp: this.getTimestamp().toString(),
                input:     this.getInput()
            };

        return request;
    }

    public static newBuilder(): HandleInputRequestBuilder
    {
        return new HandleInputRequestBuilder();
    }
}