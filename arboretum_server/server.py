import socket
import threading
import random

class Room:
    def __init__(self, code):
        self.code = code
        self.clients = []
        self.max_clients = 4
        self.client_names = []
        self.points = {}

    def add_client(self, client_socket, client_name):
        if len(self.clients) < self.max_clients:
            self.clients.append(client_socket)
            self.client_names.append(client_name)
            self.points[client_name] = 0
            return True
        return False

    def start_game(self):
        if len(self.clients) >= 1:
            print(f"Game started in room {self.code}")
            # Aquí puedes implementar la lógica del juego

class Server:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.rooms = {}
    def handle_client(self, client_socket):
        client_name = client_socket.recv(1024).decode("utf-8")
        room_code = self.generate_room_code()

        print(f"Client {client_name} connected to room {room_code}")

        room = Room(room_code)
        self.rooms[room_code] = room
        room.add_client(client_socket, client_name)
        client_socket.sendall(room_code.encode("utf-8"))
        #while True:
            # Aquí puedes manejar las interacciones entre el cliente y la sala de juego

    def generate_room_code(self):
        code = ""
        while code == "" or code in self.rooms:
            code = str(random.randint(1000, 9999))
        return code

    def start(self):
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.bind((self.host, self.port))
        server_socket.listen(5)
        print(f"Server started on {self.host}:{self.port}")

        while True:
            client_socket, client_address = server_socket.accept()
            threading.Thread(target=self.handle_client, args=(client_socket,)).start()

if __name__ == "__main__":
    server = Server("127.0.0.1", 5000)
    server.start()
