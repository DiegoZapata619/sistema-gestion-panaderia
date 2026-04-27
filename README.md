# Diseño de software

## Integrantes

- Diego Armin Zapata Góngora
- Andrés Antochiw Flores
- Edgar Emanuel Cetz Abarca
- José Manuel Gómez Chan
- Irving Eduardo Poot Moo

## Objetivo 

Implementar una aplicación tipo POS (point of sale) que cumpla con los requerimientos identificados en el planteamiento del problema, aplicando patrones de diseño y principios SOLID que permitan la escalibilidad del proyecto y futuras implementaciones para satisfacer necesidades emergentes

## Gestión de panaderia

El presente proyecto consiste en el desarrollo de un Sistema de Gestión de Panadería, diseñado para apoyar en la administración y control de las operaciones diarias de una panadería. El objetivo principal es crear una aplicación de software versátil, intuitiva y eficiente que permita optimizar procesos, reducir errores y mejorar la organización del negocio.

El sistema está enfocado en la gestión integral de los diferentes elementos que intervienen en una panadería. En primer lugar, permite la gestión de productos, facilitando la creación, edición y eliminación de un catálogo que incluye panes, pasteles, galletas y otros productos horneados. Cada producto puede contener información detallada como nombre, descripción, precio y disponibilidad.

Asimismo, el sistema incluye un módulo de toma de pedidos, donde los empleados pueden registrar las compras de los clientes seleccionando los productos y cantidades deseadas. El sistema se encarga automáticamente de calcular el total de la compra, lo que agiliza el proceso de venta y reduce posibles errores humanos.

Otra funcionalidad importante es el control de inventario, el cual permite llevar un registro actualizado de las existencias de los productos. Además, el sistema puede generar alertas cuando los niveles de inventario son bajos, ayudando a mantener el abastecimiento adecuado.

En cuanto al área de ventas, el sistema permite el registro de todas las transacciones realizadas, almacenando información relevante como fecha, productos vendidos y precios. También ofrece herramientas de búsqueda y consulta para revisar ventas anteriores.

El sistema también contempla la gestión de clientes, permitiendo registrar información de clientes frecuentes, almacenar datos de contacto y considerar sus preferencias. Esto facilita la implementación de programas de lealtad y la aplicación de descuentos o promociones.

Además, se incluye la funcionalidad de generación de facturas o recibos, donde se proporciona un comprobante detallado de cada compra realizada, incluyendo productos, precios y totales.

Por otro lado, el sistema cuenta con herramientas de reportes y análisis, generando informes de ventas en distintos periodos (diario, semanal, mensual y anual), así como análisis por producto o categoría. Esto contribuye a una mejor toma de decisiones dentro del negocio.

Finalmente, el sistema está diseñado con una interfaz de usuario intuitiva, pensada para ser fácil de usar por el personal de la panadería, utilizando la tecnologia JavaFX, lo que permite una interacción sencilla y eficiente.

En conjunto, este sistema busca mejorar la eficiencia operativa, facilitar la gestión del negocio y proporcionar una solución tecnológica accesible para la administración de una panadería.



## Estructura del Proyecto

```plaintext
sistema-gestion-panaderia/
├── README.md
├── pom.xml
├── .gitignore
├── Clientes.csv
├── Productos.csv
├── Users.csv
│
├── src/
│   └── main/
│       ├── java/
│       │   └── org/
│       │       └── panaderia/
│       │           ├── Main.java
│       │           │
│       │           ├── controllers/
│       │           │   ├── AdminController.java
│       │           │   ├── ClienteController.java
│       │           │   ├── EmployeeController.java
│       │           │   ├── InventoryController.java
│       │           │   ├── LoginController.java
│       │           │   └── MenuController.java
│       │           │
│       │           ├── model/
│       │           │   ├── Administrador.java
│       │           │   ├── Cliente.java
│       │           │   ├── ClienteDAO.java
│       │           │   ├── Empleado.java
│       │           │   ├── MetodoPago.java
│       │           │   ├── Pedido.java
│       │           │   ├── ProductDAO.java
│       │           │   ├── Producto.java
│       │           │   ├── Rol.java
│       │           │   └── Usuario.java
│       │           │
│       │           └── Servicios/
│       │               ├── Autenticador.java
│       │               ├── ClienteServicio.java
│       │               ├── Encriptador.java
│       │               ├── LectorArchivos.java
│       │               ├── LectorCSVCliente.java
│       │               ├── LectorCSVUsuario.java
│       │               └── Validador.java
│       │
│       └── resources/
│           ├── images/
│           └── views/
│               ├── LoginView.fxml
│               ├── adminView.fxml
│               ├── clientes.fxml
│               ├── employeeView.fxml
│               ├── inventoryView.fxml
│               └── usersManagementView.fxml
│
└── .idea/
```


# 3.1 Requisitos Funcionales 

## 3.1.1. Gestión de productos 

El sistema debe gestionar el catálogo completo de productos de la panadería. 

RF-3.1.1.1: El sistema debe permitir la creación de nuevos productos en el catálogo de tipo: 

   Panes 

   Pasteles 

   Galletas  

   Otros productos horneados 

RF-3.1.1.2: El sistema debe permitir la modificación y eliminación de productos existentes en el catálogo. 

RF-3.1.1.3: El sistema debe clasificar los productos por categoría. 

RF-3.1.1.4: El sistema debe validar que no existan productos con datos obligatorios vacíos o precios inválidos. 

RF-3.1.1.5: El sistema debe permitir consultar el detalle completo de cada producto. 

 

## 3.1.2 Toma de Pedidos 

RF-3.1.2.1: El sistema debe permitir al empleado registrar pedidos seleccionando uno o varios productos y su cantidad. 

RF-3.1.2.2: El sistema debe calcular automáticamente el subtotal y el total del pedido. 

RF-3.1.2.3: El sistema debe validar la disponibilidad de existencias antes de confirmar un pedido. 

RF-3.1.2.4: El sistema debe permitir asociar un pedido a un cliente registrado o marcarlo como venta general. 

RF-3.1.2.5: El sistema debe permitir cancelar o modificar un pedido antes de su confirmación definitiva. 

## 3.1.3 Inventario y Control de Stock 

RF-3.1.3.1: El sistema debe registrar y actualizar las existencias de productos terminados. 

RF-3.1.3.2: El sistema debe descontar automáticamente las existencias cuando una venta o pedido sea confirmado. 

RF-3.1.3.3: El sistema debe permitir al administrador ajustar manualmente existencias cuando sea necesario. 

RF-3.1.3.4: El sistema debe generar alertas de inventario bajo cuando la cantidad disponible alcance o descienda del mínimo definido. 

RF-3.1.3.5: El sistema debe permitir consultar el stock actual de cada producto. 

## 3.1.4 Registro de Ventas 

RF-3.1.4.1: El sistema debe registrar cada venta con fecha, hora, productos, cantidades, subtotal, descuentos aplicados y total final. 

RF-3.1.4.2: El sistema debe almacenar un historial de ventas para consulta posterior. 

RF-3.1.4.3: El sistema debe permitir al administrador y, según permisos, al empleado consultar ventas anteriores. 

RF-3.1.4.4: El sistema debe permitir filtrar ventas por fecha, cliente o producto. 

RF-3.1.4.5: El cierre de una venta debe actualizar inventario y generar el comprobante correspondiente. 

## 3.1.5 Gestión de Clientes 

RF-3.1.5.1: El sistema debe permitir registrar clientes habituales con datos de identificación y contacto. 

RF-3.1.5.2: El sistema debe permitir modificar y consultar la información de clientes. 

RF-3.1.5.3: El sistema debe almacenar preferencias, observaciones o datos relevantes del cliente cuando aplique. 

RF-3.1.5.4: El sistema debe permitir asociar ventas y pedidos a clientes registrados. 

RF-3.1.5.5: El sistema debe permitir identificar clientes frecuentes susceptibles a beneficios o descuentos. 

## 3.1.6 Gestión de Facturas y Recibos 

RF-3.1.6.1: El sistema debe generar recibos o comprobantes de compra por cada venta realizada. 

RF-3.1.6.2: El comprobante debe incluir, como mínimo, fecha, folio interno, productos, cantidades, precios, descuentos y total. 

RF-3.1.6.3: El sistema debe permitir visualizar el comprobante antes de finalizar la operación. 

RF-3.1.6.4: El sistema debe conservar un registro del comprobante asociado a la venta. 

## 3.1.7 Descuentos y Promociones 

RF-3.1.7.1: El sistema debe permitir al administrador registrar descuentos y promociones. 

RF-3.1.7.2: El sistema debe permitir aplicar descuentos a productos específicos o al total de una compra. 

RF-3.1.7.3: El sistema debe calcular automáticamente el monto descontado y el total final. 

RF-3.1.7.4: El sistema debe validar vigencia, condiciones de aplicación y compatibilidad de promociones. 

RF-3.1.7.5: El sistema debe permitir registrar beneficios para clientes frecuentes cuando corresponda. 

## 3.1.8 Reportes de Ventas y Finanzas 

RF-3.1.8.1: El sistema debe generar reportes de ventas diarias, semanales, mensuales y anuales. 

RF-3.1.8.2: El sistema debe presentar resúmenes por producto, categoría y periodo de tiempo. 

RF-3.1.8.3: El sistema debe permitir al administrador consultar ingresos acumulados en un rango de fechas. 

RF-3.1.8.4: El sistema debe mostrar información suficiente para apoyar el análisis comercial y operativo. 

RF-3.1.8.5: El sistema debe permitir visualizar reportes dentro de la aplicación. 

## 3.1.9 Búsqueda y Consulta de Productos 

RF-3.1.9.1: El sistema debe permitir la búsqueda rápida de productos por nombre, categoría o identificador. 

RF-3.1.9.2: El sistema debe mostrar el detalle de cada producto encontrado, incluyendo precio, existencia y descripción. 

RF-3.1.9.3: El sistema debe permitir filtrar resultados según disponibilidad. 

RF-3.1.9.4: El sistema debe permitir consultas rápidas desde el módulo de ventas para agilizar la atención al cliente. 

## 3.1.10 Gestión de Usuarios y Roles 

RF-3.1.10.1: El sistema debe permitir al administrador registrar usuarios del sistema. 

RF-3.1.10.2: El sistema debe permitir asignar a cada usuario uno de los dos roles definidos: administrador o empleado. 

RF-3.1.10.3: El sistema debe permitir modificar datos de usuarios y restablecer sus credenciales según las políticas implementadas. 

RF-3.1.10.4: El sistema debe restringir el acceso a módulos y operaciones conforme al rol autenticado. 

RF-3.1.10.5: El sistema debe permitir activar o desactivar usuarios sin eliminar su historial asociado. 

# 3.2 Atributos de Calidad De Software 

## 3.2.1 Eficiencia 

RNF-3.2.1.1: El sistema debe responder en menos de 2 segundos en operaciones comunes de consulta, registro y navegación, bajo condiciones normales de uso. 

RNF-3.2.1.2: La carga de catálogos y listados debe optimizarse para evitar bloqueos perceptibles en la interfaz. 

RNF-3.2.1.3: La lectura y escritura de archivos .csv debe realizarse de forma controlada para reducir tiempos de espera y minimizar riesgos de corrupción. 

## 3.2.2 Seguridad 

RNF-3.2.2.1: El sistema debe requerir autenticación de usuario antes de permitir el acceso a funciones internas. 

RNF-3.2.2.2: El sistema debe aplicar control de acceso basado en roles para separar privilegios de administrador y empleado. 

RNF-3.2.2.3: Las contraseñas no deben almacenarse en texto plano; debe emplearse un mecanismo de resguardo seguro, como hash. 

RNF-3.2.2.4: El sistema debe evitar que un usuario ejecute acciones no autorizadas desde la interfaz o desde la lógica del negocio. 

## 3.2.3 Confiabilidad 

RNF-3.2.3.1: El sistema debe mantener consistencia entre ventas, inventario, clientes y productos después de cada operación confirmada. 

RNF-3.2.3.2: El sistema debe validar entradas antes de escribir información en los archivos .csv. 

RNF-3.2.3.3: En caso de error de lectura o escritura, el sistema debe notificar claramente al usuario y evitar estados parciales no controlados. 

RNF-3.2.3.4: El sistema debe recuperar correctamente la información persistida al iniciar la aplicación. 

## 3.2.4 Mantenibilidad 

RNF-3.2.4.1: El sistema debe desarrollarse con una estructura modular basada en MVC. 

RNF-3.2.4.2: La lógica de acceso a datos en archivos .csv debe estar desacoplada de la interfaz y de la lógica de negocio. 

RNF-3.2.4.3: El código debe ser legible, documentado y organizado para facilitar correcciones y ampliaciones futuras. 

RNF-3.2.4.4: La incorporación de nuevos campos o reportes debe poder realizarse con cambios localizados y controlados. 

## 3.2.5 Portabilidad 

RNF-3.2.5.1: El sistema debe poder ejecutarse en equipos con Windows, Linux o macOS, siempre que cuenten con Java compatible. 

RNF-3.2.5.2: La interfaz debe adaptarse correctamente a resoluciones de escritorio habituales sin perder funcionalidad ni legibilidad. 

RNF-3.2.5.3: La solución no debe depender de software propietario adicional distinto al entorno de ejecución de Java. 

## 3.2.6 Usabilidad 

RNF-3.2.6.1: La interfaz debe ser intuitiva y orientar al usuario en tareas frecuentes como venta, búsqueda y registro. 

RNF-3.2.6.2: Los formularios deben validar campos obligatorios y mostrar mensajes claros de error o confirmación. 

RNF-3.2.6.3: Las pantallas correspondientes a cada rol deben mostrar únicamente las opciones relevantes para reducir confusión. 

## 3.2.7 Restricciones de Datos 

RNF-3.2.7.1: Los archivos .csv utilizados por el sistema deben mantener una estructura definida y consistente entre sesiones. 

RNF-3.2.7.2: El sistema debe protegerse contra registros duplicados críticos, como usuarios o productos con identificadores repetidos. 

RNF-3.2.7.3: El sistema debe manejar adecuadamente caracteres especiales y acentos para evitar pérdida o corrupción de información. 

## Casos de uso

### Administrador
![This is an alt text.](/src/main/resources/images/Administradores.jpeg "This is a sample image.")

### Empleado
![This is an alt text.](/src/main/resources/images/Empleados.jpeg "This is a sample image.")

## Diagramas

### Clase

![This is an alt text.](/src/main/resources/images/ClassDiagram1.jpeg "This is a sample image.")

![This is an alt text.](/src/main/resources/images/ClassDiagram2.jpeg "This is a sample image.")


### Actividad

#### Login

![This is an alt text.](/src/main/resources/images/DiagramaLoginAct.jpeg "This is a sample image.")

#### Venta

![This is an alt text.](/src/main/resources/images/venta.jpeg "This is a sample image.")

### Secuence 

![This is an alt text.](/src/main/resources/images/secuence1.jpeg "This is a sample image.")

![This is an alt text.](/src/main/resources/images/secuence2.jpeg "This is a sample image.")
