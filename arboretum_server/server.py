import socket
import threading

class Servidor:
    def __init__(self):
        self.host = socket.gethostbyname(socket.gethostname())
        self.port = 12345
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server.bind((self.host, self.port))
        self.partidas = {}
        self.lock = threading.Lock()

    def start(self):
        self.server.listen(5)
        print(f"Servidor iniciado. Esperando conexiones en {self.host}:{self.port}...")
        while True:
            client, address = self.server.accept()
            threading.Thread(target=self.handle_client, args=(client,)).start()

    def handle_client(self, client):
        print(f"Cliente conectado: {client}")
        while True:
            message = client.recv(1024).decode()
            if message:
                self.handle_game_message(client, message)
            else:
                break
        client.close()
        print(f"Cliente desconectado: {client}")

    def handle_game_message(self, client, message):
        parts = message.split("|")
        command = parts[0]

        if command == "CREATE":
            game_name = parts[1]
            self.lock.acquire()
            self.partidas[game_name] = [client]
            self.lock.release()
            self.print_partidas()
            client.send("GAME_CREATED|{}".format(game_name).encode())

        elif command == "JOIN":
            game_name = parts[1]
            if game_name in self.partidas:
                self.lock.acquire()
                self.partidas[game_name].append(client)
                self.lock.release()
                self.print_partidas()
                client.send("JOINED_GAME|{}".format(game_name).encode())
            else:
                client.send("GAME_NOT_FOUND".encode())

    def print_partidas(self):
        print("Partidas:")
        for game_name, clients in self.partidas.items():
            print("- {} - Jugadores: {}".format(game_name, len(clients)))


servidor = Servidor()
servidor.start()
