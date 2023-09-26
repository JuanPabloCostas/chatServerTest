// Importación del módulo 'net' para crear un servidor TCP
const { Server } = require('net');

// Creación de una instancia del servidor
const server = new Server();

// Definición de la dirección de host y la marca de fin de mensaje
const host = "0.0.0.0";
const END = 'END';

// Mapa que se utilizará para almacenar las conexiones de los clientes
const connections = new Map();

// Función para manejar mensajes de error
const error = (message) => {
    console.log(message);
}

// Función para enviar mensajes a todos los clientes excepto al origen
const sendMessage = (fullMesage, origin) => {
    for (const socket of connections.keys()) {
        if (socket !== origin) {
            socket.write(`${fullMesage}`)
        }
    }
}

// Función para mostrar la lista de clientes conectados
const showClients = () => {
    console.log(`Conections: `);
    console.log(connections.values());
}

// Función para configurar el servidor para escuchar en un puerto específico
const listen = (port) => {
    server.on("connection", (socket) => {
        // Obtener la dirección remota y el puerto del cliente
        const remoteSocket = `${socket.remoteAddress}:${socket.remotePort}`;
        console.log(`New connection from ${remoteSocket}`);
        
        // Establecer la codificación de los datos entrantes como UTF-8
        socket.setEncoding('utf-8');

        // Manejar datos entrantes desde el cliente
        socket.on("data", (message) => {
            // Comprobar si el cliente ya tiene un nombre de usuario
            if (!connections.has(socket)) {
                console.log(`Username ${message} set for connection ${remoteSocket}`);
                connections.set(socket, message)
                showClients();
            }
            else if (message === END) {
                // Si el mensaje es 'END', cerrar la conexión y eliminar el cliente
                socket.end();
                connections.delete(socket);
            } else {
                // Crear un mensaje completo con el nombre de usuario y el mensaje
                const fullMesage = `[${connections.get(socket)}]: ${message}`;
                console.log(`${remoteSocket} -> ${fullMesage}`);
                // Enviar el mensaje a todos los clientes
                sendMessage(fullMesage, socket);
            }
        })

        // Manejar el evento de cierre de la conexión
        socket.on("close", () => {
            console.log(`Connection with ${remoteSocket} closed`);
            showClients()
        })

        // Manejar errores en la conexión del cliente
        socket.on("error", (err) => {
            error("Client lost connection")
            connections.delete(socket);
        })
    })
    
    // Configurar el servidor para escuchar en el puerto especificado
    server.listen({ port, host }, () => {
        console.log(`Listening on port ${port}`);
    })

    // Manejar errores en el servidor
    server.on("error", (err) => {
        error(err.message)
    });
}

// Función principal que se ejecuta cuando se inicia el programa
const main = () => {
    // Verificar que se proporcionó el número correcto de argumentos
    if (process.argv.length !== 3) {
        error(`Usage: node ${__filename} port`)
    }

    // Obtener el puerto desde los argumentos de la línea de comandos
    let port = process.argv[2];
    if (isNaN(port)) {
        error(`Invalid port ${port}`)
    }

    // Convertir el puerto a un número entero
    port = Number(port)

    // Iniciar el servidor en el puerto especificado
    listen(port)
}

// Verificar si el script se está ejecutando como programa principal y, en ese caso, llamar a la función principal
if (require.main === module) {
    main()
}
