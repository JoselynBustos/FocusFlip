# FocusFlip — Modo "No Molestar" por Inclinación y Sensores

Aplicación móvil Android desarrollada en **Kotlin + Jetpack Compose** que detecta automáticamente cuando el usuario está enfocado (colocando el teléfono **boca abajo** sobre el escritorio), activa el **Modo Enfoque OLED** sin distracciones y gestiona el historial de sesiones y metas diarias de estudio mediante **API REST (Retrofit 2)** y persistencia local con **Room Database (SQLite)** y **DataStore**.

---

## 📱 Concepto y Sensores Implementados

FocusFlip integra **3 sensores de hardware** a través de `SensorManager` y `SensorEventListener` en [`SensorViewModel.kt`](app/src/main/java/mx/edu/utez/focusflip/ui/viewmodels/SensorViewModel.kt):

1. **Acelerómetro / Giroscopio (`Sensor.TYPE_ACCELEROMETER` y `Sensor.TYPE_GYROSCOPE`)**:
   - Mide la inclinación del dispositivo sobre los ejes $X, Y, Z$ y la velocidad angular ($\text{rad/s}$).
   - Cuando el teléfono se coloca **boca abajo ($180^\circ$)** sobre la mesa ($Z \le -6.8\text{ m/s}^2$), inicia automáticamente el cronómetro en la vista **Enfoque OLED**.
   - Si el usuario levanta el teléfono antes de finalizar, detecta el cambio de inclinación y activa automáticamente la vista **Alerta de Pausa**.
2. **Sensor de Proximidad (`Sensor.TYPE_PROXIMITY`)**:
   - Detecta la cercanía con la superficie del escritorio ($0\text{ cm}$) al colocar el teléfono boca abajo.
3. **Sensor de Luz Ambiental (`Sensor.TYPE_LIGHT`)**:
   - Mide el nivel de iluminación en **lux (`lx`)** en tiempo real.
   - Confirma el estado boca abajo por oscuridad ($\le 15\text{ lx}$), activa el ahorro de batería en pantalla **OLED Negra (`#000000`)**, evalúa si la iluminación del lugar de estudio es óptima ($> 150\text{ lx}$) y reproduce alertas sonoras con `MediaPlayer`.

---

## 🗄️ CRUD Completo + API REST + Persistencia Local (Room & DataStore)

La aplicación se conecta a datos a través de **Retrofit 2 + Gson** ([`ApiService.kt`](app/src/main/java/mx/edu/utez/focusflip/data/remote/ApiService.kt) y [`RetrofitClient.kt`](app/src/main/java/mx/edu/utez/focusflip/data/remote/RetrofitClient.kt)) con respaldo local en **Room Database** ([`AppDatabase.kt`](app/src/main/java/mx/edu/utez/focusflip/data/local/AppDatabase.kt)) y **DataStore** ([`UserPreferencesRepository.kt`](app/src/main/java/mx/edu/utez/focusflip/data/local/UserPreferencesRepository.kt)):

- **Seguridad en Autenticación (Login y Registro)**:
  - Encriptación de contraseñas con hash **SHA-256** (`MessageDigest`).
  - Validación de campos obligatorios, formato de correo, longitud mínima y bloqueo de acceso a usuarios no registrados o duplicados.
- **Create (Crear sesión de estudio)**: Registra asignatura, duración medida por los sensores, nivel promedio de luz (`lx`) y notas de la sesión (`POST /api/sessions`).
- **Read (Consultar historial de horas enfocadas)**: Consulta la bitácora de sesiones (`#SES-01`, `#SES-02`, etc.), el tiempo total acumulado y la racha de días (`GET /api/sessions`).
- **Update (Editar sesión y Editar metas del día)**: Permite actualizar asignatura, minutos y notas de cualquier sesión (`PUT /api/sessions/{id}`) y **editar la meta diaria de concentración** (`PUT /api/goals`).
- **Delete (Borrar sesiones de prueba)**: Confirmación para eliminar sesiones en Room y en la API REST (`DELETE /api/sessions/{id}`).

---

## 🚀 Cómo Ejecutar

1. **En Android Studio**:
   - Abrir la carpeta del proyecto (`FocusFlip`).
   - Esperar la sincronización de Gradle y presionar **Run (`▶`)** en un dispositivo físico o emulador (API 33+).
2. **Servidor API REST (Opcional para pruebas en red local)**:
   - Ejecutar `python backend/server.py` (corre en el puerto `5000`).
