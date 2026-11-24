import {AbstractServiceRequestBuilder} from "strata.foundation.core/Transfer";
import type {GetOutputRequest}        from "./GetOutputRequest";

export
class GetOutputRequestBuilder
    extends AbstractServiceRequestBuilder<GetOutputRequest>
{
    private userId:      number;
    private source:      string;
    private command:     string;
    private destination: string;

    constructor()
    {
        super();
        this.userId      = null;
        this.source      = null;
        this.command     = null;
        this.destination = null;
    }

    setUserId(userId: number): GetOutputRequestBuilder
    {
        this.userId = userId;
        return this;
    }

    setSource(source: string): GetOutputRequestBuilder
    {
        this.source = source;
        return this;
    }

    setCommand(command: string): GetOutputRequestBuilder
    {
        this.command = command;
        return this;
    }

    setDestination(destination: string): GetOutputRequestBuilder
    {
        this.destination = destination;
        return this;
    }

    getUserId(): number
    {
        return this.userId;
    }

    getSource(): string
    {
        return this.source;
    }

    getCommand(): string
    {
        return this.command;
    }

    getDestination(): string
    {
        return this.destination;
    }

    build(): GetOutputRequest
    {
        const request: GetOutputRequest =
            {
                requestId:   this.getRequestId(),
                timestamp:   this.getTimestamp().toString(),
                userId:      this.getUserId(),
                source:      this.getSource(),
                command:     this.getCommand(),
                destination: this.getDestination()
            };

        return request;
    }

    public static newBuilder(): GetOutputRequestBuilder
    {
        return new GetOutputRequestBuilder();
    }
}
