Consigna
========

## Algo de contexto

Las aplicaciones web se dividen usualmente en frontend y backend, que se
corresponden a los componentes ejecutados en el cliente y en el servidor. El
backend, ejecutado en el servidor, implementa los diferentes procesos de
negocios, que usualmente involucran interactuar con alguna base de datos y con
otros servicios. Recibe requests de mÃºltiples clientes al mismo tiempo, las
encola y eventualmente envÃ­a las respuestas correspondientes. El frontend, por
otra parte, es el encargado de la visualizaciÃ³n de los componentes grÃ¡ficos en
el browser, de enviar los pedidos (HTTP Requests) al servidor y de la conexiÃ³n
entre los distintos servicios provistos por el backend.

Un patrÃ³n comÃºnmente utilizado para implementar aplicaciones web (y en general
para aplicaciones que tenga una interfaz de usuario) es el de
Model-View-Controller. En este patrÃ³n, los modelos (Models) son los componentes
principales y expresan los comportamientos de la aplicaciÃ³n, es decir
implementan las reglas de negocios y administran los datos de la misma. En
aplicaciones simples, suele existir un mapeo uno a uno entre tablas en la base
de datos y modelos. Las vistas (Views) manejan la interacciÃ³n con el usuario y
determinan cÃ³mo se renderiza la informaciÃ³n. Luego de una acciÃ³n, las vistas
envÃ­an requests a los controladores. Los controladores (Controllers) son las
funciones que abstraen la lÃ³gica de la aplicaciÃ³n y la interacciÃ³n entre los
objetos.

Si bien, histÃ³ricamente los lÃ­mites entre frontend y backend han estado
claramente marcados, en los Ãºltimos aÃ±os el aumento en las capacidades de
procesamiento del lado del cliente (e.g. mejores engines de Javascript) ha
provocado que cada vez mÃ¡s funcionalidades que originalmente se implementan en
el servidor se estÃ©n implementando en el cliente. Como consecuencia, se
desarrollaron numerosos frameworks que permiten implementar mucho de la lÃ³gica
de la aplicaciÃ³n, como AngularJS, React o Vue.

AsÃ­ como muchas funcionalidades se implementan ahora en el frontend, es muy
comÃºn el desarrollo de aplicaciones web sin un frontend definido que proveen
acceso a sus funcionalidades a travÃ©s de un conjunto bien definido de
interfaces que se denomina API. Este tipo de aplicaciones, denominadas
servicios web, es muy comÃºn en la actualidad y permite la generaciÃ³n de nuevos
productos mediante la integraciÃ³n de diferentes servicios web. Por ejemplo, se
utilizan cuando una empresa es la encarga desarrollar el backend y otra el
frontend.  En estos casos, el frontend y el backend se comunican a travÃ©s de
interfaces llamadas APIs, que definen un conjunto de *requests* posibles y sus
*responses* esperados.

Por ejemplo, [MercadoLibre tiene una
API](https://developers.mercadolibre.com.ar/es_ar/api-docs-es) que permite a
desarrolladores externos a la empresa interactuar con su base de datos
directamente, sin tener que utilizar la interfaz grÃ¡fica de la web. Por
ejemplo, podemos consultar todas las categorÃ­as de productos si hacemos un get
a la URL https://api.mercadolibre.com/sites/MLA/categories

    $ curl https://api.mercadolibre.com/sites/MLA/categories

## El proyecto

En este laboratorio vamos a implementar una API RESTful para un servicio de
delivery, al estilo Rappi, PedidosYA, ifood o Deliveroo. En esta API, los
proveedores suben sus productos, que los consumidores pueden ver y seleccionar
para incluir en un pedido. Una vez que el pedido estÃ¡ finalizado, la aplicaciÃ³n
se encarga de cobrarlo al consumidor, entregarlo y pagarle al proveedor el
dinero correspondiente.

Se les proveerÃ¡ de un esqueleto inicial con cÃ³digo base sobre el cuÃ¡l construir
su aplicatiÃ³n y la especificaciÃ³n de la API (i.e. la especificaciÃ³n de cada
"endpoint").

El backend estarÃ¡ implementado en [Cask](http://www.lihaoyi.com/cask/), un
microframework web del lenguaje de programaciÃ³n
[Scala](https://www.scala-lang.org/), de diseÃ±o simple inspirado en el
framework web [Flask](http://flask.pocoo.org/) de Python.

TendrÃ¡n que completar el esqueleto implementando las funciones que procesan las
peticiones HTTP (GET o POST) que vienen del frontend, y tendrÃ¡n que definir
modelos de datos (i.e. tipos de datos) utilizando programaciÃ³n orientada a
objetos.

No contaremos con un frontend, por lo que todas las consultas deberÃ¡n ser
realizadas a travÃ©s de un navegador (e.g. utilizando `postman`) o por consola
con herramientas como `curl` o [`httpie`](https://httpie.org/).

### Algunas simplificaciones

Para mantener simple nuestro prototipo, tomaremos algunas decisiones de diseÃ±o
que no son Ã³ptimas si estuviÃ©ramos implementando un producto real:

- La "base de datos" estarÃ¡ manejada mediante un
  [singleton](https://en.wikipedia.org/wiki/Singleton_pattern), mientras que
  las "tablas" serÃ¡n instancias de una clase. Casi toda la implementaciÃ³n de la
  base de datos serÃ¡ dada, simplemente tienen que adaptarla a su diseÃ±o de
  objetos.
- Los datos serÃ¡n guardados como archivos [JSON](https://www.json.org/). Esto
  es, guardaremos los objetos (usuarios registrados, ubicaciones, pedidos y
  productos) en formato JSON. De esta forma, no hay que preocuparse por
  configurar un motor de base de datos.
- En lugar de usar alguna interfaz a google maps, las ubicaciones serÃ¡n simples
  strings predefinidos con nombres como â€œParque Sarmientoâ€ o â€œAlta CÃ³rdobaâ€ y
  coordenadas.
- No es necesario manejar los casos de uso con errores "elegantemente" (e.g.,
  verificaciÃ³n de direcciones de email con formato real). Sin embargo, es
  necesario comprobar que todos los elementos usados una consulta existan en la
  base de datos.
- No se pedirÃ¡n contraseÃ±as para los usuarios (sÃ³lo como punto estrella).
- La aplicaciÃ³n no requerirÃ¡ login/logout, eso quedarÃ¡ como punto estrella.
- A la hora de listar los proveedores para un consumidor en particular,
  listaremos solamente aquellos que tengan la misma direcciÃ³n. El campo de
  mÃ¡xima distancia se utilizarÃ¡ sÃ³lo para los puntos estrellas.
- Dejaremos que cask maneje los errores originados cuando una consulta no tiene
  suficientes argumentos. Esto devolverÃ¡ un error 500 ante una consulta mal
  formada, en lugar de un error 400.

## Requerimientos Funcionales

1. Registro de usuarios. Existen dos tipos de usuarios: consumidores
   (consumers) y proveedores (providers) y un usuario solo puede ser de un
   tipo. Un consumidor estÃ¡ definido por username y una direcciÃ³n. Por su
   parte, un proveedor estÃ¡ definido por un username, una direcciÃ³n, un nombre
   del negocio y una distancia mÃ¡xima de delivery. El username define
   univocamente tanto a los consumidores como a los proveedores, y no pueden
   existir consumidores y proveedores con el mismo username. De igual manera,
   el nombre de la tienda de un proveedor debe ser unÃ­voco entre proveedores.
2. Los consumidores y proveedores comenzarÃ¡n con un balance de 0 que sÃ³lo serÃ¡
   modificado cuando se complete un pedido. El total del pedido serÃ¡ restado al
   saldo del consumidor (quien manejarÃ¡ saldo negativo) y sumado al del
   proveedor.
3. A partir de una direcciÃ³n obtener un listado de proveedores que hagan
   delivery hasta esa direcciÃ³n particular.
4. Crear un pedido (order) a un Ãºnico proveedor. Este pedido puede contener
   mÃºltiples items. Cuando la orden es creada, se debe actualizar el saldo del
   consumidor, proveedor y el estado del pedido.
5. Consultar el listado de pedidos realizados por un consumidor en particular.
6. Crear y modificar un menÃº compuesto de Ã­tems. Cada Ã­tem debe tener una
   descripciÃ³n y un precio.
7. Consultar el listado de pedidos recibidos por un proveedor y sus respectivos
   estados.
8. Marcar un pedido como entregado. [Esta acciÃ³n cambia el estado del pedido a
   â€˜deliveredâ€™].
9. Consultar la informaciÃ³n de un usuario: username, direcciÃ³n, balance, etc.

## ImplementaciÃ³n

Por sobre todo, tengan en cuenta que se evaluarÃ¡ el diseÃ±o de las clases y
singletons, la utilizaciÃ³n de conceptos como herencia, encapsulamiento,
polimorfismo o sobrecarga y el uso adecuado de mÃ©todos.

La interfaz de base de datos que se les darÃ¡ se encargarÃ¡ de la mayorÃ­a de las
operaciones de lectura o escritura a disco. Sin embargo, ustedes deberÃ¡n
implementar formas de acceder a los datos en las estructuras de sus modelos
(e.g. mÃ©todos para filtrar elementos, verificar que elementos existan, etc.).
Para esto se les da una lista de mÃ©todos que tendrÃ¡n que implementar ustedes.

### DefiniciÃ³n de la API


| Method | URL                                 | Params                                                                                      | Code - Response                                                                                                                                                                                                                    |
|--------|-------------------------------------|---------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GET    | /api/locations                      |                                                                                             | 200 - [{id: int, name: string, coordX: int, coordY: int}]                                                                                                                                                                          |
| POST   | /api/locations                      | {name: string, coordX: int, coordY: int }                                                   | 200 - id ; 409 - existing location name                                                                                                                                                                                            |
| GET    | /api/consumers                      |                                                                                             | 200 - [{id: int, username: string, locationId: int, balance:int}]                                                                                                                                                                               |
| POST   | /api/consumers                      | {username: string, locationName: string}                                                    | 200 - id ; 404 - non existing location ; 409 - existing username                                                                                                                                                                   |
| GET    | /api/providers                      | {locationName?: string}                                                                     | 200 - [{id: int, username: string, locationId: int, storeName: string, maxDeliveryDistance: int, balance:int}] ; 404 - non existing location                                                                                                    |
| POST   | /api/providers                      | {username: string, storeName: string, locationName: string, maxDeliveryDistance: int}       | 200 - id ; 400 - negative maxDeliveryDistance ; 404 - non existing location ; 409 - existing username/storeName                                                                                                                    |
| POST   | /api/users/delete/{username:string} |                                                                                             | 200 "Ok" ; 404 - non existing user                                                                                                                                                                                                 |
| GET    | /api/items                          | {providerUsername?: string}                                                                 | 200 - [{id: int, name: string, price: float, description: string, providerId: int}] ; 404 - non existing provider                                                                                                                  |
| POST   | /api/items                          | {name: string, description: string, price: float, providerUsername: string}                 | 200 - id ; 400 - negative price ; 404 - non existing provider ; 409 - existing item for provider                                                                                                                                   |
| POST   | /api/items/delete/{id:int}          |                                                                                             | 200 "Ok" ; 404 - non existing item                                                                                                                                                                                                 |
| GET    | /api/orders                         | {username: string}                                                                          | 200 - [{id: int, consumerId: int, consumerUsername: string, consumerLocation: string, providerId: int, providerStoreName: string, orderTotal: float, status: option(â€˜payedâ€™, â€˜deliveredâ€™, â€˜finishedâ€™)}*] ; 404 - non existing user |
| GET    | /api/orders/detail/{id:int}         |                                                                                             | 200 - [{id: int, name: string, description: string, price: float, amount: int}] ; 404 - non existing order                                                                                                                         |
| POST   | /api/orders                         | {providerUsername: string, consumerUsername: string, items: [{name: string, amount: int}+]} | 200 - id ; 400 - negative amount ; 404 - non existing consumer/provider/item for provider                                                                                                                                          |
| POST   | /api/orders/delete/{id:int}         |                                                                                             | 200 - "Ok" ; 404 - non existing order                                                                                                                                                                                              |
| POST   | /api/orders/deliver/{id:int}        |                                                                                             | 200 - "Ok" ; 404 - non existing order                                                                                                                                                                                              |
| POST   | /api/login**                        | {username: string, password: string}                                                        | 200 - {id: int, isProvider: bool} ; 401 - non existing user ; 403 - incorrect password                                                                                                                                             |
| POST   | /api/logout**                       |                                                                                             | 200 - "Ok"                                                                                                                                                                                                                         |

\* El estado finished es para el punto estrella de comentarios.
\*\* Punto estrella de autenticaciÃ³n.

## Esqueleto del laboratorio

El laboratorio les provee un esqueleto base que tendrÃ¡n que completar:

    .
    â”œâ”€â”€ build.sbt
    â”œâ”€â”€ project
    â”‚Â Â  â”œâ”€â”€ build.properties
    â”‚Â Â  â””â”€â”€ Dependencies.scala
    â””â”€â”€ src
        â””â”€â”€ main
         Â Â  â””â”€â”€ scala
         Â Â      â”œâ”€â”€ app
         Â Â      â”‚Â Â  â”œâ”€â”€ package.scala
         Â Â      â”‚Â Â  â””â”€â”€ RestfulAPIServer.scala
         Â Â      â””â”€â”€ models
         Â Â          â”œâ”€â”€ db
         Â Â          â”‚Â Â  â”œâ”€â”€ Database.scala
         Â Â          â”‚Â Â  â””â”€â”€ DatabaseTable.scala
         Â Â          â”œâ”€â”€ Location.scala
         Â Â          â”œâ”€â”€ Model.scala
         Â Â          â””â”€â”€ package.scala

### Archivos del esqueleto

1. `build.sbt`: Archivo de configuraciÃ³n de [SBT](https://www.scala-sbt.org/).
   AquÃ­ pueden agregar librerÃ­as. TendrÃ¡n que leer la documentaciÃ³n de SBT para
   saber como agregar nuevas librerÃ­as. Fuera de eso, todas las librerÃ­as
   necesarias ya estÃ¡n agregadas, sÃ³lo Ãºsenlo en casos de puntos estrella.
2. `project/`: Ignoren este directorio. Es necesario para el correcto
   funcionamiento de SBT, pero no deberÃ­an tocarlo salvo excepciones muy
   puntuales con librerÃ­as.
3. `app/package.scala` y `models/package.scala`: Estos archivos definen los
   "package objects", que es una manera elegante en Scala de declarar
   constantes, variables o mÃ©todos de utilidad que sean globales a todo el
   paquete. No hagan abuso de su uso, pero puede que necesiten utilizarlo.
4. `app/RestfulAPIServer.scala`: AquÃ­ se definen los controladores (API
   Endpoints) de la aplicaciÃ³n REST.
5. `models/db/Database.scala`: Interfaz con la "base de datos". Es un
   `singleton` que se encarga de las operaciones de lectura/escritura a disco.
   La lÃ³gica no tienen que tocarla, pero deberÃ¡n agregar entradas a las tablas
   de sus modelos (ver `Location` como ejemplo).
6. `models/db/DatabaseTable.scala`: Clase para las tablas de la base de datos.
   No es necesario modificarla (si lean y entiendan lo que hace).
7. `models/Model.scala`: Trait base de sus modelos (clases). Y trait base de
   sus [companion
   objects](https://docs.scala-lang.org/tour/singleton-objects.html). Vean la
   implementaciÃ³n de `models/Location.scala` para entender mejor. En particular

Regla general, si un mÃ©todo, variable, constante, etc. estÃ¡ definido por `???`,
es que tienen que implementarlo ustedes.  AdemÃ¡s de eso, el esqueleto no es
exhaustivo, puede que requieran mÃ¡s implementaciones (e.g. en
`ModelCompanion`).

## Requerimientos No funcionales

AdemÃ¡s de los requerimientos funcionales, el laboratorio deberÃ¡ cumplir los
siguientes requerimientos no funcionales:

### ImplementaciÃ³n:

- No tiene que fallar con un error 500. Los 400 son aceptables, mientras
  que sean intencionales (e.g. los dados en la definiciÃ³n de la API).
- Deben respetar el encapsulamiento de *TODOS* los atributos y mÃ©todos.
  Recuerden que hay distintos niveles de encapsulamiento. Usen el que crean
  conveniente. Si dan permisos de escritura/lectura o hacen pÃºblico un mÃ©todo,
  deben poder justificar por quÃ© eso era necesario.
- Prefieran el uso de `val` en lugar de `var`. Traten de hacer las cosas
  inmutables cuando sea posible y eviten caer en prÃ¡cticas de programaciÃ³n
  imperativa (i.e. eviten el uso de estados internos cuÃ¡nto puedan). Scala es
  un lenguaje principalmente funcional y deben poder utilizarlo como tal.
- El sistema de inferencia de tipos de Scala es muy bueno y en la mayorÃ­a de
  los casos va a poder descifrar quÃ© es lo que estÃ¡n declarando. AÃºn asÃ­ es
  recomendable (y en cierto punto obligatorio) que declaren los tipos
  explÃ­citamente, especialmente en funciones y/o mÃ©todos, y en atributos.
  Pueden obviarlo para variables de uso interno o cuando la definiciÃ³n del
  tipo de dato vuelva el cÃ³digo muy complejo.
- Aprendan a utilizar las herramientas que les brinda Scala. Sobre todo a la
  hora de trabajar con colecciones (listas, diccionarios, conjuntos, etc). No
  recurran a programaciÃ³n imperativa cuando las cosas se pueden resolver de
  manera funcional, utilizando expresiones de programaciÃ³n de alto orden (e.g.
  hagan uso de `map`, `filter`, `fold`, etc.)
- Los puntos estrella son extras. No necesitan hacerse para aprobar el lab. Si
  entregan un laboratorio con puntos estrella implementados y no estÃ¡ el
  laboratorio base con todas sus funcionalidades se les considerarÃ¡ mal. De
  esto se desprende que no deben utilizar ninguna librerÃ­a extra que les
  facilite el trabajo de implementaciÃ³n de los modelos (funciones como `all`,
  `filter`, etc.). Deben implementarlas manualmente para aprobar el
  laboratorio, y eventualmente reimplementarlas en un branch aparte de un punto
  estrella luego de que hayan terminado la implementaciÃ³n base.

### Estilo:

- El estilo de cÃ³digo es vÃ¡lido si el cÃ³digo es legible y estÃ¡ prolijo. Traten
  de no pasar de las 80 columnas, y jamÃ¡s sobrepasen las 120.
- Hagan buen uso de espacios e indentaciones. Nunca utilicen tabs, siempre
  prefieran espacios. Scala suele indentarse con un espacio de `2` como base.
- Todos los archivos deben tener estilo consistente.
- El objetivo de clases, atributos y el output de mÃ©todos deben estar
  documentados en inglÃ©s. No exageren tampoco, **good code is the best
  documentation**.
- Deben respetar la estructura original del proyecto, agregando nuevos archivos
  en los directorios correspondientes.
- Por sobre todas las cosas, siempre recuerden
  [KISS](https://en.wikipedia.org/wiki/KISS_principle)

### Entrega:

- Fecha de entrega: hasta el **10/05/2018** a las 23:59:59.999


DeberÃ¡n crear un tag indicando el release para corregir.

    git tag -a lab-2 -m "Entrega Laboratorio 2"
    git push origin lab-2

**Si no estÃ¡ el tag no se corrige**. Tampoco se consideran commits posteriores
al tag.

En caso de agregar puntos estrella, deben hacer dos tags, uno con la entrega
y otro (o varios) con los puntos estrella. Esto nos permite evaluar la versiÃ³n
bÃ¡sica en caso de que tengan un error en las modificaciones posteriores.

### Informe:

Junto con el cÃ³digo, deberÃ¡n presentar un informe en un archivo INFORME.md que incluya:

- Decisiones de diseÃ±o relevantes. Por ejemplo, si algÃºn punto de la consigna
  les pareciÃ³ ambigÃ¼o, reporten quÃ© interpretaciÃ³n siguienron.
- Puntos estrella que hayan realizado y cÃ³mo diseÃ±aron la soluciÃ³n.
- Si utilizaron alguno de los siguientes conceptos en el proyecto (en su cÃ³digo
  o en el esqueleto ya dado) y quÃ© habrÃ­an tenido que hacer si esta
  caracterÃ­stica no estuviera disponible en el lenguaje. Un pÃ¡rrafo por
  concepto es suficiente.
    1. Encapsulamiento
    2. Herencia, clases abstractas y traits.
    3. Sobrecarga de operadores
    4. Polimorfismo

## Recomendaciones y algunos links de utilidad

- Â¡Busquen en Google antes de implementar!
- Â¡Comprueben despuÃ©s de implementar cada funciÃ³n, no quieran escribir todo y
  probar al final!
- Primero implementen los "casos de Ã©xito" con cÃ³digo 200 de todos los
  endpoints, luego implementen los errores.
- La consola de `sbt` es su amiga. Ãšsenla para cargar lo que vayan haciendo.
- Saber que tipos toma y devuelve cada funciÃ³n/mÃ©todo es una gran parte del
  trabajo.
- Si la mÃ¡quina lo soporta, les recomendamos usar IntelliJ Idea como IDE, con
  el plugin de scala. Este IDE facilita la escritura de cÃ³digo y realiza
  algunas comprobacionesde consistencia.


Algunos links de interÃ©s:

- [From Python to Scala](https://crscardellino.github.io/archive/): GuÃ­a
  escrita por el profesor Cristian Cardellino. Es una iniciaciÃ³n a Scala desde
  Python. No estÃ¡ completa ni es exhaustiva (y puede estar un poco
  desactualizada), pero cubre con lo bÃ¡sico para comenzar. En el link estÃ¡ el
  archivo del blog de Cristian, y de ahi pueden acceder a todas las entradas
  (aÃ±o 2014). Disclaimer, estÃ¡ en inglÃ©s, pero le pueden preguntar a Cristian
  ante cualquier duda.
- [DocumentaciÃ³n Oficial de Scala](https://docs.scala-lang.org/): Es muy buena
  y es la referencia sobre la que siempre se tienen que basar. Cualquier
  consulta sobre la [API de Scala](https://docs.scala-lang.org/api/all.html)
  puede ser resuelta en este lugar. Pero ademÃ¡s se ofrecen varios aspectos mÃ¡s
  bÃ¡sicos como el [Tour of
  Scala](https://docs.scala-lang.org/tour/tour-of-scala.html): que cubre mÃ¡s
  que suficiente todos lo necesario que van a tener que utilizar en el
  laboratorio.
- [The Neophyte's Guide to
  Scala](https://danielwestheide.com/scala/neophytes.html): es una guÃ­a
  avanzada de Scala, pero sumamente recomendable si les interesa saber mÃ¡s del
  lenguaje.  EstÃ¡ ligeramente desactualizada, pero trabaja sobre conceptos
  fundamentales del lenguaje que no van a cambiar por mÃ¡s que las versiones
  cambien.
- [DocumentaciÃ³n de Cask](http://www.lihaoyi.com/cask/): Es la documentaciÃ³n
  base del framework que utilizarÃ¡n. Es escasa, pero no es muy complejo lo que
  tienen que hacer tampoco. No duden en consultar a los profesores sobre cÃ³mo
  hacer cualquier cosa.
- [Sobre mÃ©todos GET y
  POST](http://blog.micayael.com/2011/02/09/metodos-get-vs-post-del-http/):
  Para leer y entender un poco mÃ¡s sobre los conceptos que vienen detrÃ¡s de las
  REST APIs.
- [Tips para el desarrollo utilizando
  POO](https://scotch.io/bar-talk/s-o-l-i-d-the-first-five-principles-of-object-oriented-design)


## Puntos estrella

Para implementar puntos estrella se puede cambiar cualquier parte de la
implementaciÃ³n. Es posible que deban modificar la especificaciÃ³n de la API.
Dichos cambios deberÃ¡n ser documentados en el README, junto con una
justificaciÃ³n de por quÃ© asÃ­ lo decidieron y cÃ³mo efectivamente comprobar que
el punto estrella funciona.

Los puntos estrella deberÃ¡n ser entregados en branchs aparte que deberÃ¡n ser
correctamente taggeados. AcÃ¡ va un listado de los puntos estrella posibles (son
bienvenidos de hacer mÃ¡s cosas si asÃ­ lo desean). Los puntos 3 y 4 son extra
difÃ­ciles, por lo que suman mayor cantidad de puntos:

### 1- Comentarios

Proveer las funcionalidades para que los consumidores puedan puntuar y dejar
comentarios de texto sobre la comida. Esta acciÃ³n marca el pedido con el estado
`finished`. Los consumidores serÃ¡n capaces de ver el rating de los proveedores y
listar todos los comentarios anteriores. Los proveedores podrÃ¡n acceder a esta
informaciÃ³n desde su pÃ¡gina de perfil.

### 2- Radio de entrega

Al listar los proveedores, mostrar sÃ³lo aquellos que hagan deliveries a la
direcciÃ³n del usuario, independientemente de que estÃ©n en la misma direcciÃ³n o
no.  Hint: utilizar el atributo de distancia mÃ¡xima de delivery y el sistema de
coordenadas. Para calcular la distancia deberÃ¡n hacer uso de [taxicab
geometry](https://en.wikipedia.org/wiki/Taxicab_geometry).

### 3- Full authentication

DeberÃ¡n utilizar autenticaciÃ³n. Para ello el usuario deberÃ¡ hacer `login` y
`logout` de la aplicaciÃ³n mediante el uso de [JSON Web
Tokens](https://jwt.io/). Pueden utilizar la librerÃ­a que deseen para esta
aplicaciÃ³n. Por otro lado, las contraseÃ±as deberÃ¡n estar correctamente
encriptadas, utilizando alguna librerÃ­a de hash. En particular, se deberÃ¡
pedir autorizaciÃ³n para hacer las operaciones de escritura (los mÃ©todos POST).

### 4- Bases de datos reales

Utilizar algÃºn motor real de base de datos (puede ser bien con motores
complejos como MySQL o PostgreSQL, o bien con SQLite). Para ello deberÃ¡n
reimplementar la interfaz `Database` de manera tal que sea un
[ORM](https://en.wikipedia.org/wiki/Object-relational_mapping), o bien utilizar
algÃºn tipo de librerÃ­a que se encargue de eso.