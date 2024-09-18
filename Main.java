import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Usuario {
    private String nombreUsuario;
    private String contrasena;
    private String rol;

    public Usuario(String nombreUsuario, String contrasena, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public String toString() {
        return nombreUsuario + "," + contrasena + "," + rol;
    }

    public static Usuario fromString(String str) {
        String[] parts = str.split(",");
        return new Usuario(parts[0], parts[1], parts[2]);
    }
}


class SistemaDeUsuarios {
    private HashMap<String, Usuario> usuariosRegistrados;
    private Usuario usuarioActual;
    private static final String ARCHIVO_USUARIOS = "usuarios.txt";

    public SistemaDeUsuarios() {
        usuariosRegistrados = new HashMap<>();
        cargarUsuarios();
    }

    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        if (usuariosRegistrados.containsKey(nombreUsuario)) {
            Usuario usuario = usuariosRegistrados.get(nombreUsuario);
            if (usuario.getContrasena().equals(contrasena)) {
                usuarioActual = usuario;
                System.out.println("Sesión iniciada como " + usuarioActual.getRol());
                return true;
            } else {
                System.out.println("Contraseña incorrecta.");
            }
        } else {
            System.out.println("Usuario no registrado.");
        }
        return false;
    }

    public void cerrarSesion() {
        System.out.println("Sesión cerrada.");
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void agregarUsuario(String nombreUsuario, String contrasena, String rol) {
        if (usuariosRegistrados.containsKey(nombreUsuario)) {
            System.out.println("El usuario ya está registrado.");
        } else {
            Usuario nuevoUsuario = new Usuario(nombreUsuario, contrasena, rol);
            usuariosRegistrados.put(nombreUsuario, nuevoUsuario);
            guardarUsuarios();
            System.out.println("Nuevo usuario registrado: " + nombreUsuario);
        }
    }

    private void cargarUsuarios() {
        File archivo = new File("usuarios.txt");
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader("usuarios.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Usuario usuario = Usuario.fromString(line);
                    usuariosRegistrados.put(usuario.getNombreUsuario(), usuario);
                }
            } catch (IOException e) {
                System.out.println("Error al cargar el archivo de usuarios: " + e.getMessage());
            }
        } else {
            System.out.println("El archivo de usuarios no existe. Se creará uno nuevo cuando se agreguen usuarios.");
        }
    }

    private void guardarUsuarios() {
        File archivo = new File("usuarios.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("usuarios.txt"))) {
            for (Usuario usuario : usuariosRegistrados.values()) {
                writer.write(usuario.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo de usuarios: " + e.getMessage());
        }
    }
}


class Habitacion {
    private int numero;
    private String tipo;
    private boolean ocupada;

    public Habitacion(int numero, String tipo, boolean ocupada) {
        this.numero = numero;
        this.tipo = tipo;
        this.ocupada = ocupada;
    }

    public int getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void reservar() {
        if (!ocupada) {
            ocupada = true;
            System.out.println("Habitación " + numero + " ha sido reservada.");
        } else {
            System.out.println("La habitación " + numero + " ya está ocupada.");
        }
    }

    public void liberar() {
        if (ocupada) {
            ocupada = false;
            System.out.println("Habitación " + numero + " ha sido liberada.");
        } else {
            System.out.println("La habitación " + numero + " ya está disponible.");
        }
    }

    public void mostrarEstado() {
        String estado = ocupada ? "Ocupada" : "Disponible";
        System.out.println("Habitación " + numero + " (" + tipo + ") - " + estado);
    }

    @Override
    public String toString() {
        return numero + "," + tipo + "," + (ocupada ? "Ocupada" : "Disponible");
    }

    public static Habitacion fromString(String str) {
        String[] parts = str.split(",");
        int numero = Integer.parseInt(parts[0]);
        String tipo = parts[1];
        boolean ocupada = parts[2].equalsIgnoreCase("Ocupada");
        return new Habitacion(numero, tipo, ocupada);
    }
}


class SistemaDeReservas {
    private ArrayList<Habitacion> habitaciones;
    private static final String ARCHIVO_HABITACIONES = "habitaciones.txt";

    public SistemaDeReservas() {
        habitaciones = new ArrayList<>();
        cargarHabitaciones();
    }

    public void reservarHabitacion(int numero) {
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.getNumero() == numero) {
                habitacion.reservar();
                guardarHabitaciones(); 
                return;
            }
        }
        System.out.println("La habitación " + numero + " no existe.");
    }

    public void liberarHabitacion(int numero, Usuario usuario) {
        if (usuario.getRol().equalsIgnoreCase("Administrador") || usuario.getRol().equalsIgnoreCase("Empleado")) {
            for (Habitacion habitacion : habitaciones) {
                if (habitacion.getNumero() == numero) {
                    habitacion.liberar();
                    guardarHabitaciones(); 
                    return;
                }
            }
            System.out.println("La habitación " + numero + " no existe.");
        } else {
            System.out.println("No tiene permisos para liberar una habitación.");
        }
    }

    public void consultarEstadoHabitaciones() {
        for (Habitacion habitacion : habitaciones) {
            habitacion.mostrarEstado();
        }
    }

    public void consultarHabitacionesDisponiblesPorTipo(String tipo) {
        boolean encontrado = false;
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.getTipo().equalsIgnoreCase(tipo) && !habitacion.isOcupada()) {
                habitacion.mostrarEstado();
                encontrado = true;
            }
        }
        if (!encontrado) {
            System.out.println("No hay habitaciones disponibles del tipo " + tipo + ".");
        }
    }

    private void cargarHabitaciones() {
        File archivo = new File("habitaciones.txt");
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader("habitaciones.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Habitacion habitacion = Habitacion.fromString(line);
                    habitaciones.add(habitacion);
                }
            } catch (IOException e) {
                System.out.println("Error al cargar el archivo de habitaciones: " + e.getMessage());
            }
        } else {
            System.out.println("El archivo de habitaciones no existe. Creando uno nuevo con habitaciones predefinidas.");
            agregarHabitacionesPredefinidas();
        }
    }

    private void agregarHabitacionesPredefinidas() {
        habitaciones.add(new Habitacion(101, "Sencilla", false));
        habitaciones.add(new Habitacion(102, "Doble", false));
        habitaciones.add(new Habitacion(103, "Suite", false));
        guardarHabitaciones(); 
    }

    private void guardarHabitaciones() {
        File archivo = new File("habitaciones.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("habitaciones.txt"))) {
            for (Habitacion habitacion : habitaciones) {
                writer.write(habitacion.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo de habitaciones: " + e.getMessage());
        }
    }
}


public class Main {
    public static void main(String[] args) {
        SistemaDeUsuarios sistemaUsuarios = new SistemaDeUsuarios();
        SistemaDeReservas sistemaReservas = new SistemaDeReservas();
        Scanner scanner = new Scanner(System.in);

        int opcion;
        do {
            System.out.println("\nSistema del Hotel:");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Agregar nuevo usuario");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                opcion = 0;
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese su nombre de usuario: ");
                    String nombreUsuario = scanner.nextLine();
                    System.out.print("Ingrese su contraseña: ");
                    String contrasena = scanner.nextLine();

                    if (sistemaUsuarios.iniciarSesion(nombreUsuario, contrasena)) {
                        Usuario usuarioActual = sistemaUsuarios.getUsuarioActual();
                        int subOpcion;
                        do {
                            System.out.println("\nSistema de Reservas del Hotel:");
                            System.out.println("1. Consultar estado de habitaciones");
                            System.out.println("2. Consultar habitaciones disponibles por tipo");
                            System.out.println("3. Reservar habitación");
                            
                            if (!usuarioActual.getRol().equalsIgnoreCase("Cliente")) {
                                System.out.println("4. Liberar habitación");
                            }

                            System.out.println("5. Cerrar sesión");
                            System.out.print("Seleccione una opción: ");
                            try {
                                subOpcion = Integer.parseInt(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                                subOpcion = 0;
                                continue;
                            }

                            switch (subOpcion) {
                                case 1:
                                    sistemaReservas.consultarEstadoHabitaciones();
                                    break;
                                case 2:
                                    System.out.print("Ingrese el tipo de habitación que desea consultar: ");
                                    String tipo = scanner.nextLine();
                                    sistemaReservas.consultarHabitacionesDisponiblesPorTipo(tipo);
                                    break;
                                case 3:
                                    System.out.print("Ingrese el número de la habitación a reservar: ");
                                    int numReservar;
                                    try {
                                        numReservar = Integer.parseInt(scanner.nextLine());
                                    } catch (NumberFormatException e) {
                                        System.out.println("Entrada inválida. Por favor, ingrese un número.");
                                        break;
                                    }
                                    sistemaReservas.reservarHabitacion(numReservar);
                                    break;
                                case 4:
                                    if (!usuarioActual.getRol().equalsIgnoreCase("Cliente")) {
                                        System.out.print("Ingrese el número de la habitación a liberar: ");
                                        int numLiberar;
                                        try {
                                            numLiberar = Integer.parseInt(scanner.nextLine());
                                        } catch (NumberFormatException e) {
                                            System.out.println("Entrada inválida. Por favor, ingrese un número.");
                                            break;
                                        }
                                        sistemaReservas.liberarHabitacion(numLiberar, usuarioActual);
                                    } else {
                                        System.out.println("No tiene permisos para liberar una habitación.");
                                    }
                                    break;
                                case 5:
                                    sistemaUsuarios.cerrarSesion();
                                    break;
                                default:
                                    System.out.println("Opción no válida.");
                                    break;
                            }
                        } while (subOpcion != 5);
                    }
                    break;
                case 2:
                    System.out.print("Ingrese un nombre de usuario para el nuevo usuario: ");
                    String nuevoUsuario = scanner.nextLine();
                    System.out.print("Ingrese una contraseña: ");
                    String nuevaContrasena = scanner.nextLine();
                    System.out.print("Ingrese el rol (Cliente, Empleado, Administrador): ");
                    String nuevoRol = scanner.nextLine();
                    sistemaUsuarios.agregarUsuario(nuevoUsuario, nuevaContrasena, nuevoRol);
                    break;
                case 3:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (opcion != 3);

        scanner.close();
    }
}
