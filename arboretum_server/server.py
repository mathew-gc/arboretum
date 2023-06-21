import socket
import threading

class Servidor:
    def __init__(self):
        self.host = socket.gethostbyname(socket.gethostname())  # Obtiene la dirección IP del host
        self.port = 12345  # Puerto en el que se va a escuchar
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # Crea un objeto de socket TCP/IP
        self.server.bind((self.host, self.port))  # Vincula el socket a la dirección IP y puerto especificados
        self.partidas = {}  # Diccionario para almacenar las partidas y los clientes
        self.lock = threading.Lock()  # Objeto de bloqueo para sincronización

    def start(self):
        self.server.listen(5)  # Comienza a escuchar conexiones entrantes, permitiendo hasta 5 conexiones en espera
        print(f"Servidor iniciado. Esperando conexiones en {self.host}:{self.port}...")
        while True:
            client, address = self.server.accept()  # Acepta una conexión entrante y obtiene el objeto de socket del cliente
            threading.Thread(target=self.handle_client, args=(client,)).start()  # Crea un hilo para manejar al cliente

    def handle_client(self, client):
        print(f"Cliente conectado: {client}")
        while True:
            message = client.recv(1024).decode()  # Recibe un mensaje del cliente
            if message:
                self.handle_game_message(client, message)  # Procesa el mensaje del cliente
            else:
                break
        client.close()  # Cierra la conexión con el cliente
        print(f"Cliente desconectado: {client}")

    def handle_game_message(self, client, message):
        parts = message.split("|")  # Divide el mensaje en partes separadas por el caracter '|'
        command = parts[0]  # Obtiene el comando del mensaje

        if command == "CREATE":  # Si el comando es 'CREATE'
            game_name = parts[1]  # Obtiene el nombre de la partida del mensaje
            self.lock.acquire()  # Adquiere el bloqueo para evitar accesos simultáneos a las partidas
            self.partidas[game_name] = [client]  # Crea una nueva partida y agrega al cliente que la creó
            self.lock.release()  # Libera el bloqueo
            self.print_partidas()  # Imprime las partidas en la consola
            client.send("GAME_CREATED|{}".format(game_name).encode())  # Envía una respuesta al cliente

        elif command == "JOIN":  # Si el comando es 'JOIN'
            game_name = parts[1]  # Obtiene el nombre de la partida del mensaje
            if game_name in self.partidas:  # Verifica si la partida existe
                self.lock.acquire()  # Adquiere el bloqueo
                self.partidas[game_name].append(client)  # Agrega al cliente a la partida existente
                self.lock.release()  # Libera el bloqueo
                self.print_partidas()  # Imprime las partidas en la consola
                client.send("JOINED_GAME|{}".format(game_name).encode())  # Envía una respuesta al cliente
            else:
                client.send("GAME_NOT_FOUND".encode())  # Envía un mensaje de error al cliente

    def print_partidas(self):
        print("Partidas:")
        for game_name, clients in self.partidas.items():  # Itera sobre el diccionario de partidas
            print("- {} - Jugadores: {}".format(game_name, len(clients)))  # Imprime el nombre de la partida y el número de jugadores

servidor = Servidor()  # Crea una instancia de la clase Servidor
servidor.start()  # Inicia el servidor
