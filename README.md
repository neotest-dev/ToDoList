# 📝 ToDoList – App Android con Firebase

> Aplicación móvil para la gestión de tareas personales, desarrollada en Android Studio usando Kotlin y Firebase.  
> Incluye autenticación, CRUD en Firestore, notificaciones programadas y monitoreo con Crashlytics y Analytics.

---

## ⚠️ Seguridad

Por seguridad, este repositorio **no incluye el archivo** `google-services.json`.

---

## 🚀 ¿Cómo ejecutar el proyecto en tu máquina?

Si clonaste este repositorio y quieres que la app funcione correctamente, sigue estos pasos para **configurar tu propio Firebase**:

### 🔧 Paso 1: Crear un proyecto en Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto (puedes llamarlo `ToDoList` o como prefieras)

---

### 📱 Paso 2: Registrar la app Android

1. Dentro de Firebase, haz clic en **Agregar app Android**
2. Usa el mismo `applicationId` que aparece en el archivo `build.gradle` del proyecto (por ejemplo: `com.todolist.app`)
3. Agrega tu **SHA-1**, que puedes obtener ejecutando:

   ```bash
   ./gradlew signingReport
### 🔹 Paso 2: Registrar tu app Android

1. En Firebase Console, haz clic en "Agregar aplicación" y selecciona Android.
2. Completa los datos del formulario:
   - Nombre del paquete (`applicationId`): Usa el que aparece en el archivo `build.gradle` del proyecto (por ejemplo: `com.todolist.app`).
   - (Opcional) Apodo de la app y certificado de depuración: puedes dejarlos en blanco al inicio.
3. Ejecuta el comando `gradlew signingReport` en tu terminal (o desde la terminal de Android Studio) para obtener tu SHA-1.
4. Copia el SHA-1 que aparece y agrégalo en el formulario de Firebase.
5. Descarga el archivo `google-services.json` que se genera.
6. Coloca ese archivo dentro del proyecto, en la carpeta:

   `/app/google-services.json`

---

### 🔹 Paso 3: Activar los servicios necesarios en Firebase

1. Ve a Authentication > Método de acceso y habilita:
   - Google Sign-In (u otros métodos que necesites)
2. Ve a Cloud Firestore:
   - Crea la base de datos
   - Puedes usar el modo de prueba mientras desarrollas la app
3. (Opcional) Activa también los siguientes servicios según tus necesidades:
   - Firebase Cloud Messaging (notificaciones)
   - Crashlytics (monitoreo de errores)
   - Firebase Analytics

---

### 🔹 Paso 4: Configurar el Web Client ID (solo si usas Google Sign-In)

1. Ve a Authentication > Método de acceso > Google
2. Copia el valor del ID de cliente web
3. Abre el archivo `res/values/strings.xml` en tu proyecto
4. Reemplaza el contenido de la línea:

   `<string name="default_web_client_id">TU_CLIENT_ID_WEB</string>`



## 🚀 Funcionalidades

✔️ Registro e inicio de sesión con **correo y Google**  
✔️ Gestión de tareas: **crear, leer, actualizar y eliminar (CRUD)**  
✔️ **Filtros personalizados** para visualizar tareas por estado  
✔️ **Notificaciones programadas** con `AlarmManager` y Firebase Cloud Messaging  
✔️ **Sincronización en tiempo real** con Cloud Firestore  
✔️ Integración con **Firebase Crashlytics** para monitoreo de errores  
✔️ Análisis de uso mediante **Google Analytics for Firebase**

---

## 🛠️ Tecnologías Utilizadas

- 🧑‍💻 Kotlin + Android XML
- 🔐 Firebase Authentication
- 🔥 Cloud Firestore
- 📲 Firebase Cloud Messaging (FCM)
- ⏰ AlarmManager
- 🧩 Crashlytics
- 📊 Analytics for Firebase

---

## ⚙️ Instalación y Ejecución

1. Clona este repositorio:
   ```bash
   git clone https://github.com/neotest-dev/ToDoList.git
2. Abre el proyecto en **Android Studio**
3. Verifica que tengas el archivo `google-services.json` del autor en la carpeta `/app` y tu SHA1 en su firebase de neotest-dev sino regresa al inicio y haz los pasos.
4. Ejecuta el proyecto en un emulador o dispositivo real

---

## 📸 Capturas de Pantalla


| Login                            | Lista de tareas                   | Notificación                             |
|----------------------------------|-----------------------------------|------------------------------------------|
| ![Login](screenshots/login.jpeg) | ![Tareas](screenshots/tasks.jpeg) | ![Notificación](screenshots/notify.jpeg) |

---

## 👥 Colaboradores

- [@neotest-dev](https://github.com/neotest-dev)
- [@julio20950](https://github.com/julio20950)

---

## 🗂️ Releases

Este proyecto se organizó por sprints y versiones.

📄 Puedes revisar el historial completo en la pestaña de [Releases](https://github.com/neotest-dev/ToDoList/releases)

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos como parte de un curso universitario.  
Queda libre para uso educativo, personal y de aprendizaje.

---
