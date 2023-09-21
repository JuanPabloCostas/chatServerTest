const { Server } = require('net');

const server = new Server();

const host = "0.0.0.0";
const END = 'END';

// 127.0.0.1:8000 -> 'Pepito'
// 127.0.0.1:9000 -> 'Juan'
const connections = new Map();



const error = (message) => {
    console.log(message);
}

const sendMessage = (fullMesage, origin) => {
    for (const socket of connections.keys()) {
        if (socket !== origin) {
            socket.write(`${fullMesage}`)
        }
    }
}

const showClients = () => {
    console.log(`Conections: `);
            
    console.log(connections.values());

}

const listen = (port) => {
    server.on("connection", (socket) => {
        const remoteSocket = `${socket.remoteAddress}:${socket.remotePort}`
        console.log(`New connection from ${remoteSocket}`);
        
        socket.setEncoding('utf-8');

        socket.on("data", (message) => {
            
            // console.log(message);
            if (!connections.has(socket)) {
                console.log(`Username ${message} set for connection ${remoteSocket}`);
                connections.set(socket, message)
                showClients();
            }
            else if (message === END) {
                socket.end();
                connections.delete(socket);
            } else {
                // for (const username of connections.values()){
                //     console.log(username);
                // };
                const fullMesage = `[${connections.get(socket)}]: ${message}`;
                console.log(`${remoteSocket} -> ${fullMesage}`);
                sendMessage(fullMesage, socket);
            }
        })

        socket.on("close", () => {
            console.log(`Connection with ${remoteSocket} closed`);
            showClients()
        })

        socket.on("error", (err) => {
            error("Client lost connection")
            connections.delete(socket);
        })
    })
    
    server.listen({ port, host }, () => {
        console.log(`Listening on port ${port}`);
    })

    server.on("error", (err) => {
        
        error(err.message)

    });

}

const main = () => {
    if (process.argv.length !== 3) {
        error(`Usage: node ${__filename} port`)
    }

    let port = process.argv[2];
    if (isNaN(port)) {
        error(`Invalid port ${port}`)
    }

    port = Number(port)

    listen(port)
}

if (require.main === module) {
    main()
    
}

