const socket = new WebSocket("ws://localhost:8000/websocket");
console.log("WebSocket instance created:", socket);
    socket.onopen = function(event) {
        console.log("Connected to WebSocket server.");
         sendMessage('Hello server!');
    };

    socket.onmessage = function(event) {
        if (event.data === "Idle timeout warning") {
            console.log("Received idle timeout warning from server.");
        } else {
            console.log("Received message: " + event.data);
        }
    };

    socket.onclose = function(event) {
        console.log("WebSocket connection closed.");
    };

    socket.onerror = function(error) {
        console.error('WebSocket error:', error.message);
    };

    // Function to send a message to the WebSocket server
    function sendMessage(message) {
        socket.send(message);
        console.log('Message sent to server:', message);
    }

