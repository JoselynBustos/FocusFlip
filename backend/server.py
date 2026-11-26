"""
Servidor API REST de FocusFlip.
Valida registro e inicio de sesión con SHA-256 (sin permitir acceso a usuarios no registrados).
Endpoints consumidos por Retrofit desde la app Android:
  - POST   /api/auth/register
  - POST   /api/auth/login
  - GET    /api/sessions
  - POST   /api/sessions
  - PUT    /api/sessions/<id>
  - DELETE /api/sessions/<id>
  - PUT    /api/goals
"""

from http.server import BaseHTTPRequestHandler, HTTPServer
import hashlib
import json
import urllib.parse

# Diccionario de usuarios registrados (vacío al inicio: requiere registrarse primero)
USERS = {}

SESSIONS = [
    {
        "id": 1,
        "oci_code": "#SES-01",
        "user_matricula": "20264A001",
        "asignatura": "Desarrollo de Apps Móviles",
        "duracion_minutos": 25,
        "notas": "Bocetos del prototipo integrador terminados en Classroom.",
        "fecha": "Hoy • 24 Nov 2026",
        "promedio_lux": 8.5,
        "sensor_modo": "Giroscopio 180° + Luz OLED"
    },
    {
        "id": 2,
        "oci_code": "#SES-02",
        "user_matricula": "20264A001",
        "asignatura": "Estructura de Datos",
        "duracion_minutos": 45,
        "notas": "Repaso de pilas y colas de memoria sin distracciones.",
        "fecha": "Ayer • 23 Nov 2026",
        "promedio_lux": 11.2,
        "sensor_modo": "Giroscopio 180° + Proximidad"
    }
]


def hash_password(raw: str) -> str:
    return hashlib.sha256(raw.encode("utf-8")).hexdigest()


class FocusFlipApiHandler(BaseHTTPRequestHandler):
    def _send_json(self, status_code, payload):
        body = json.dumps(payload).encode("utf-8")
        self.send_response(status_code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def _read_json(self):
        length = int(self.headers.get("Content-Length", "0"))
        if length <= 0:
            return {}
        raw = self.rfile.read(length).decode("utf-8")
        return json.loads(raw)

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path == "/api/sessions":
            self._send_json(200, SESSIONS)
        else:
            self._send_json(200, {"status": "FocusFlip REST API Online"})

    def do_POST(self):
        parsed = urllib.parse.urlparse(self.path)
        data = self._read_json()

        if parsed.path == "/api/auth/login":
            identifier = data.get("correo", "").strip().lower()
            password = data.get("password", "")
            found_user = None
            for u in USERS.values():
                if u["correo"].lower() == identifier or u["matricula"].lower() == identifier:
                    found_user = u
                    break

            if not found_user:
                self._send_json(404, {
                    "success": False,
                    "message": "Usuario no registrado. Crea una cuenta primero."
                })
                return

            if found_user["password_hash"] != hash_password(password):
                self._send_json(401, {
                    "success": False,
                    "message": "Contraseña incorrecta. Verifica tus datos."
                })
                return

            self._send_json(200, {
                "success": True,
                "message": "Inicio de sesión exitoso",
                "user": found_user
            })

        elif parsed.path == "/api/auth/register":
            matricula = data.get("matricula", "").strip().upper()
            nombre = data.get("nombre_completo", "").strip()
            correo = data.get("correo", "").strip().lower()
            password = data.get("password", "")

            if not matricula or not nombre or not correo or len(password) < 6:
                self._send_json(400, {
                    "success": False,
                    "message": "Completa todos los campos (contraseña mín. 6 caracteres)."
                })
                return

            for u in USERS.values():
                if u["correo"].lower() == correo or u["matricula"].upper() == matricula:
                    self._send_json(409, {
                        "success": False,
                        "message": "El correo o la matrícula ya están registrados."
                    })
                    return

            user = {
                "matricula": matricula,
                "nombre_completo": nombre,
                "correo": correo,
                "password_hash": hash_password(password),
                "daily_goal_minutes": 120,
                "created_at": "2026-11-24"
            }
            USERS[correo] = user
            self._send_json(201, {
                "success": True,
                "message": "Usuario registrado correctamente",
                "user": user
            })

        elif parsed.path == "/api/sessions":
            next_id = len(SESSIONS) + 1
            new_item = {
                "id": next_id,
                "oci_code": f"#SES-{next_id:02d}",
                "user_matricula": data.get("user_matricula", "20264A001"),
                "asignatura": data.get("asignatura", "Desarrollo de Apps Móviles"),
                "duracion_minutos": int(data.get("duracion_minutos", 25)),
                "notas": data.get("notas", ""),
                "fecha": data.get("fecha", "Hoy • 24 Nov 2026"),
                "promedio_lux": float(data.get("promedio_lux", 10.0)),
                "sensor_modo": data.get("sensor_modo", "Giroscopio 180° + Luz OLED")
            }
            SESSIONS.insert(0, new_item)
            self._send_json(201, new_item)
        else:
            self._send_json(404, {"error": "Endpoint no encontrado"})

    def do_PUT(self):
        parsed = urllib.parse.urlparse(self.path)
        data = self._read_json()

        if parsed.path.startswith("/api/sessions/"):
            session_id = int(parsed.path.split("/")[-1])
            for item in SESSIONS:
                if item["id"] == session_id:
                    item["asignatura"] = data.get("asignatura", item["asignatura"])
                    item["duracion_minutos"] = int(data.get("duracion_minutos", item["duracion_minutos"]))
                    item["notas"] = data.get("notas", item["notas"])
                    self._send_json(200, item)
                    return
            self._send_json(200, SESSIONS[0] if SESSIONS else {})

        elif parsed.path == "/api/goals":
            goal = int(data.get("daily_goal_minutes", 120))
            for u in USERS.values():
                u["daily_goal_minutes"] = goal
            self._send_json(200, {"success": True, "daily_goal_minutes": goal})
        else:
            self._send_json(404, {"error": "Endpoint no encontrado"})

    def do_DELETE(self):
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path.startswith("/api/sessions/"):
            session_id = int(parsed.path.split("/")[-1])
            global SESSIONS
            SESSIONS = [s for s in SESSIONS if s["id"] != session_id]
            self.send_response(204)
            self.end_headers()
        else:
            self._send_json(404, {"error": "Endpoint no encontrado"})


if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", 5000), FocusFlipApiHandler)
    print("FocusFlip REST API corriendo en http://0.0.0.0:5000")
    server.serve_forever()
