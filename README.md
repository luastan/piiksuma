# Piiksuma &middot; [![Build Status](https://travis-ci.com/luastan/piiksuma.svg?token=ZH9Kst8PtvBThQjCiYhF&branch=develop)](https://travis-ci.com/luastan/piiksuma)
Proyecto de bases de datos

## Enlaces chulis :)
- [Ejemplo de arquitectura de un _"Twitter"_](https://github.com/donnemartin/system-design-primer/blob/master/solutions/system_design/twitter/README.md)
- [Tutorial de JUnit](https://www.tutorialspoint.com/junit/junit_test_framework.htm) (Lo vamos a usar el año que viene...)
- [Tutorial de Mockito](https://www.tutorialspoint.com/mockito/mockito_junit_integration.htm)
- [Paths en Java](https://www.baeldung.com/java-path)
- [Patrones de diseño en Java](https://www.journaldev.com/1827/java-design-patterns-example-tutorial) :exclamation:
- [Github Markdown Emojis](https://gist.github.com/rxaviers/7360908#file-gistfile1-md) :fire:

## Desarrollo de software
- https://es.wikipedia.org/wiki/Desarrollo_%C3%A1gil_de_software
- https://es.wikipedia.org/wiki/Scrum_(desarrollo_de_software)
- https://es.wikipedia.org/wiki/Kanban_(desarrollo)

## Diagramas de Cacoo
- Diagrama de prueba: https://cacoo.com/diagrams/hPLvHeCUMNMiuGHq/56830
- Diagrama de casos de uso: https://cacoo.com/diagrams/bZatDb44sVS4y8fq/E45CD

## Carpeta de Drive
- Proyecto: https://drive.google.com/drive/folders/1VeIF8h1xAgP7jFYru67I-2t3v5YcIQFJ?usp=sharing


## Dependencias
### Docker
#### Instalación
Es importante seguir todos los pasos...
 * **Ubuntu**:
    1. [Instalación](https://docs.docker.com/install/linux/docker-ce/ubuntu/)
    2. [Configuración](https://docs.docker.com/install/linux/linux-postinstall/)
    3. [Docker-Compose](https://docs.docker.com/compose/install/) (Puede instalarse con pip tammbién)

 * **Windows**:
    1. [Instalación](https://docs.docker.com/docker-for-windows/install/#install-docker-desktop-for-windows-desktop-app)
    2. Docker para windows ya incluye docker-compose
### Gradle
Todos los comandos de gradle inicializan la base de datos de Docker. Se puede instalar gradle, pero no es necesario al tener ya un [Gradle Wrapper](https://docs.gradle.org/5.3.1/userguide/gradle_wrapper.html).
```bash
# Ejecuta la app desde el main
./gradlew run

# Ejecuta tods los tests
./gradlew test

# Limpieza en caso de que sea necesario
./gradlew clean

# Para abrir el docker sin ejecutar la app
./gradlew composeUp

# Para cerrar el docker
./gradlew composeDown
```
**Nota**: En Windows el script es `./gradlew.bat`

En Intellij con indicar que quieres ejecutar con gradle y usar las tasks **run** o **test** es suficiente. Si se mockea la base de datos, no es necesario ejecutar los tests con gradle; pero si se hacen con gradle, luego se puede usar la opción **Open Gradle test report** que abre en el navegador info detallada acerca de los tests.
