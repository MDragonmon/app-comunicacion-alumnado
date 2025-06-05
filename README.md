# Aplicación dirigida al alumnado de Educación Infantil y Primaria que tengan dificultades de comunicación.

_Breve descripción._

## Índice

- [Título del Proyecto](#título-del-proyecto)
  - [Índice](#índice)
    - [1. Introducción 📖](#1-introducción-)
    - [2. Objetivos del proyecto 📦](#2-objetivos-del-proyecto-)
    - [3. Análisis y especificación de requisitos ⌨️](#3-análisis-y-especificación-de-requisitos-️)
      - [3.1 Requisitos funcionales](#31-requisitos-funcionales)
      - [3.2 Requisitos no funcionales](#32-requisitos-no-funcionales)
      - [3.3 Casos de uso](#33-casos-de-uso)
    - [4. Manual de instalación 🔧](#4-manual-de-instalación-)
    - [5. Uso de la herramienta ⚙️](#5-uso-de-la-herramienta-️)
    - [6. Gestión de la información y datos 🖇️](#6-gestión-de-la-información-y-datos-️)
    - [7. Conclusiones 💭](#7-conclusiones-)
    - [8. Otros ❕](#8-otros-)
      - [8.1 Autor ✒️](#81-autor-️)
      - [8.2 Licencia 📄](#82-licencia-)

### 1. Introducción 📖

_Esta idea está planteada como una herramienta dirigida hacia el alumnado con dificultades comunicativas del segundo ciclo de Educación Infantil y Eduación Primaria en su totalidad. Como objetivo principal tenemos el solucionar en aula los problemas de comunicacion que puedan surgir entre el alumando, el docente y/o compañeros de clase._

### 2. Objetivos del proyecto 📦

_- Fomentar la participación del alumnado._
_- Facilitar la comunicación del alumnado._
_- Garantizar la adaptabilidad para distintos niveles de competencia lingüística._

### 3. Análisis y especificación de requisitos ⌨️

_- Perfiles de usuario (profesores, alumnado o familias)._

_- Representaciones gráficas (pictogramas)._

_- Unión de representaciones gráficas a sonidos grabados o por síntesis de voz._

_- Reproducción de frases construidas por el alumnado._

_- Módulo de seguimiento del uso y progresos._



#### 3.2 Requisitos no funcionales

_- Configuración multilingüe (castellano, gallego e inglés)._

_- Interfaz simple e intuitiva._

_- Alta disponibilidad y tiempos de respuesta cortos._

_- Compatibilidad multiplataforma._

_- Tolerancia a errores en la interacción táctil._

_- Accesibilidad visual y auditiva._

_- Cumplimiento de normativas de protección de datos (GDPR)._

#### 3.3 Casos de uso

_- Caso 1: El usuario selecciona una serie de pictogramas para expresar una frase ("Tengo hambre")._

_- Caso 2: Un educador crea un tablero con pictogramas personalizados para una actividad._

_- Caso 3: La app reproduce la frase construida utilizando voz sintética._

_- Caso 5: El sistema registra el uso y genera informes de progreso._



### 4. Manual de instalación 🔧

_Instalación para entorno local (React Native + Expo):._

```bash
npm install -g expo-cli
```

_Este comando instala Expo, una plataforma para desarrollar y ejecutar apps móviles._

```bash
git clone https://github.com/MDragonmon/app-comunicacion-alumnado.git
cd app-comunicacion-alumnado
npm install
```

_Se descarga el repositorio, se accede a la carpeta del proyecto y se instalan las dependencias._

```bash
expo start
```

_Lanza la app en modo desarrollo, con opción de abrir en emulador o dispositivo móvil._

### 5. Uso de la herramienta ⚙️

_Al iniciar la aplicación, se presenta un menú principal con dos secciones principales: Comunicación y Configuración._

_En Comunicación, el usuario accede a tableros de pictogramas organizados por categorías (comida, emociones, acciones, etc.)._

_En Configuración, se personaliza el idioma, sonidos, pictogramas y perfiles de usuario._

### 6. Gestión de la información y datos 🖇️

_Modelo entidad-relación:_

_Usuarios_ (id, nombre, tipo, fecha_creación)

_Pictogramas_ (id, nombre, imagen, sonido)

_Uso_ (id, usuario_id, pictograma_id, fecha_uso)

### 7. Conclusiones 💭

_Gracias a esta aplicación podremos incluir de una forma efectiva a los alumnos con dificultades de comunicación._

### 8. Otros ❕

#### 8.1 Autor ✒️

- **Luis Antonio Saburido Dasilva** - _Desarrollador_ - [github](https://github.com/MDragonmon)
- **Bárbara Teyali Ceballos Ekobo** - _Centro de Ideas y Ayudante_

#### 8.2 Licencia 📄

_Este software está disponible únicamente para uso personal, educativo o de investigación. Queda expresamente prohibido su uso comercial o su redistribución con fines lucrativos sin autorización previa del autor._

