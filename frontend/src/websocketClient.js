import { Client } from "@stomp/stompjs";

export function createStompClient(onConnectCallback) {
    const client = new Client({
        brokerURL: "http://localhost:8080/ws",
        reconnectDelay: 5000,
        debug: (str) => {
            console.log("STOMP:", str);
        },
        onConnect: onConnectCallback
    });

    client.activate();
    return client;
}