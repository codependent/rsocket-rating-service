const {
    RSocketClient,
    JsonSerializer,
    IdentitySerializer,
} = require('rsocket-core');
const RSocketWebSocketClient = require('rsocket-websocket-client').default;
const route = 'request-rating';
let client = undefined;
let rSocket = undefined;

function main() {
    if (client !== undefined) {
        client.close();
    }
    client = new RSocketClient({
        serializers: {
            data: JsonSerializer,
            metadata: IdentitySerializer
        },
        setup: {
            // ms btw sending keepalive to server
            keepAlive: 60000,
            // ms timeout if no keepalive response
            lifetime: 180000,
            // format of `data`
            dataMimeType: 'application/json',
            // format of `metadata`
            metadataMimeType: 'message/x.rsocket.routing.v0',
        },
        transport: new RSocketWebSocketClient({
            url: 'ws://localhost:8080/rating-ws'
        }),
    });

    // Open the connection
    client.connect().subscribe({
        onComplete: socket => {
            // socket provides the rsocket interactions fire/forget, request/response,
            // request/stream, etc as well as methods to close the socket.
            rSocket = socket;
        },
        onError: error => {
            console.log("Connection has been refused due to ", error);
        },
        onSubscribe: cancel => {
            /* call cancel() to abort */
        }
    });
    document.getElementById('sendButton').addEventListener('click', requestRating);
}

function requestRating() {
    rSocket.requestResponse({
        data: {
            'songId': document.getElementById("songId").value
        },
        metadata: String.fromCharCode(route.length) + route
    }).subscribe({
        onComplete: () => {
            console.log('Complete')
        },
        onError: error => {
            console.log("Connection has been closed due to " + error);
        },
        onNext: payload => {
            console.log(payload.data);
        },
        onSubscribe: subscription => {
            //subscription.request(1)
            console.log("Subscribed")
        }
    });
}

document.addEventListener('DOMContentLoaded', main);
