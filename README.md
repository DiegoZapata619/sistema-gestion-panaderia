# Diseño de software

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


## Casos de uso

### Administrador
![This is an alt text.](/src/main/resources/images/Administradores.jpeg "This is a sample image.")

### Empleado
![This is an alt text.](/src/main/resources/images/Empleados.jpeg "This is a sample image.")