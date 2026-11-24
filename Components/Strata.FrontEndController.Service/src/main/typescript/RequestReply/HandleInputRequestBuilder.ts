import {AbstractServiceRequestBuilder} from "strata.foundation.core/Transfer";
import type {HandleInputRequest}      from "./HandleInputRequest";
import type {FrontEndInput}           from "./FrontEndInput";

export
class HandleInputRequestBuilder<I extends FrontEndInput>
    extends AbstractServiceRequestBuilder<HandleInputRequest<I>>
{
    private input: I;

    constructor()
    {
        super();
        this.input = null;
    }

    setInput(input: I): HandleInputRequestBuilder<I>
    {
        this.input = input;
        return this;
    }

    getInput(): I
    {
        return this.input;
    }

    build(): HandleInputRequest<I>
    {
        const request: HandleInputRequest<I> =
            {
                requestId: this.getRequestId(),
                timestamp: this.getTimestamp().toString(),
                input:     this.getInput()
            };

        return request;
    }

    public static newBuilder<I extends FrontEndInput>(): HandleInputRequestBuilder<I>
    {
        return new HandleInputRequestBuilder<I>();
    }
}
