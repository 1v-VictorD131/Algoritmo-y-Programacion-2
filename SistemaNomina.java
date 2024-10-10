//Sistema Nomina Victor Diaz y Santiago Sandoval

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class HorasInvalidasException extends Exception {
    public HorasInvalidasException(String mensaje) {
        super(mensaje);
    }
}

class SalarioInvalido extends Exception {
    public SalarioInvalido(String mensaje) {
        super(mensaje);
    }
}

abstract class Empleado {
    private String nombre;
    private double salario;
    private int horasTrabajadas;

    public Empleado(String nombre, double salario) throws SalarioInvalido {
        if (salario < 0) {
            throw new SalarioInvalido("El salario debe ser mayor o igual a 0.");
        }
        this.nombre = nombre;
        this.salario = salario;
        this.horasTrabajadas = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public double getSalario() {
        return salario;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setSalario(double salario) throws SalarioInvalido {
        if (salario < 0) {
            throw new SalarioInvalido("El salario debe ser mayor o igual a 0.");
        }
        this.salario = salario;
    }

    public void registrarHoras(int horas) throws HorasInvalidasException {
        if (horas < 0) {
            throw new HorasInvalidasException("Las horas trabajadas no pueden ser negativas.");
        }
        this.horasTrabajadas += horas;
    }

    public abstract double calcularSalario();

    public abstract String getTipo();

    public String getDetalles() {
        return getTipo() + "," + getNombre() + "," + calcularSalario();
    }
}

class EmpleadoFijo extends Empleado {
    public EmpleadoFijo(String nombre, double salario) throws SalarioInvalido {
        super(nombre, salario);
    }

    @Override
    public double calcularSalario() {
        return getSalario();
    }

    @Override
    public String getTipo() {
        return "EmpleadoFijo";
    }
}

class EmpleadoTemporal extends Empleado {
    private double pagoPorHora;

    public EmpleadoTemporal(String nombre, double pagoPorHora) throws SalarioInvalido {
        super(nombre, 0);
        if (pagoPorHora < 0) {
            throw new SalarioInvalido("El pago por hora debe ser mayor o igual a 0.");
        }
        this.pagoPorHora = pagoPorHora;
    }

    @Override
    public double calcularSalario() {
        return getHorasTrabajadas() * pagoPorHora;
    }

    @Override
    public String getTipo() {
        return "EmpleadoTemporal";
    }
}

public class SistemaNomina {
    public static void main(String[] args) {
        ArrayList<Empleado> empleados = leerEmpleadosDesdeArchivo();
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("----- Sistema de Nómina -----");
            System.out.println("1. Registrar empleado fijo");
            System.out.println("2. Registrar empleado temporal");
            System.out.println("3. Ingresar horas trabajadas");
            System.out.println("4. Calcular nómina");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcion) {
                case 1:
                    registrarEmpleadoFijo(empleados, scanner);
                    break;
                case 2:
                    registrarEmpleadoTemporal(empleados, scanner);
                    break;
                case 3:
                    ingresarHorasTrabajadas(empleados, scanner);
                    break;
                case 4:
                    generarReciboNomina(empleados);
                    break;
                case 5:
                    continuar = false;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
                    break;
            }
        }
        scanner.close();
    }

    private static void registrarEmpleadoFijo(ArrayList<Empleado> empleados, Scanner scanner) {
        try {
            System.out.print("Nombre del empleado fijo: ");
            String nombreFijo = scanner.nextLine();
            System.out.print("Salario fijo: ");
            double salarioFijo = scanner.nextDouble();
            scanner.nextLine(); 
            EmpleadoFijo empleadoFijo = new EmpleadoFijo(nombreFijo, salarioFijo);
            empleados.add(empleadoFijo);
            System.out.println("Empleado fijo registrado con éxito.");
            guardarEmpleadosEnArchivo(empleados); 
        } catch (SalarioInvalido e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarEmpleadoTemporal(ArrayList<Empleado> empleados, Scanner scanner) {
        try {
            System.out.print("Nombre del empleado temporal: ");
            String nombreTemporal = scanner.nextLine();
            System.out.print("Pago por hora: ");
            double pagoPorHora = scanner.nextDouble();
            scanner.nextLine(); 
            EmpleadoTemporal empleadoTemporal = new EmpleadoTemporal(nombreTemporal, pagoPorHora);
            empleados.add(empleadoTemporal);
            System.out.println("Empleado temporal registrado con éxito.");
            guardarEmpleadosEnArchivo(empleados); 
        } catch (SalarioInvalido e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void ingresarHorasTrabajadas(ArrayList<Empleado> empleados, Scanner scanner) {
        System.out.print("Ingrese el nombre del empleado: ");
        String nombre = scanner.nextLine();
        Empleado empleado = buscarEmpleado(empleados, nombre);
        if (empleado != null) {
            try {
                System.out.print("Horas trabajadas: ");
                int horas = scanner.nextInt();
                scanner.nextLine(); 
                empleado.registrarHoras(horas);
                System.out.println("Horas registradas con éxito.");
                guardarEmpleadosEnArchivo(empleados); 
            } catch (HorasInvalidasException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Empleado no encontrado.");
        }
    }

    private static void generarReciboNomina(ArrayList<Empleado> empleados) {
        System.out.println("----- Recibo de Nómina -----");
        for (Empleado emp : empleados) {
            double salario = emp.calcularSalario();
            System.out.println("Empleado: " + emp.getNombre() + ", Salario: " + salario);
        }
    }

    private static Empleado buscarEmpleado(ArrayList<Empleado> empleados, String nombre) {
        for (Empleado emp : empleados) {
            if (emp.getNombre().equalsIgnoreCase(nombre)) {
                return emp;
            }
        }
        return null;
    }

    private static void guardarEmpleadosEnArchivo(ArrayList<Empleado> empleados) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("empleados.txt"))) {
            for (Empleado empleado : empleados) {
                writer.write(empleado.getDetalles());
                writer.newLine();
            }
            System.out.println("Empleados guardados correctamente en el archivo.");
        } catch (IOException e) {
            System.out.println("Error al guardar los empleados: " + e.getMessage());
        }
    }

    private static ArrayList<Empleado> leerEmpleadosDesdeArchivo() {
        ArrayList<Empleado> empleados = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("empleados.txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                String tipo = datos[0];
                String nombre = datos[1];
                double salario = Double.parseDouble(datos[2]);

                if ("EmpleadoFijo".equals(tipo)) {
                    empleados.add(new EmpleadoFijo(nombre, salario));
                } else if ("EmpleadoTemporal".equals(tipo)) {
                    EmpleadoTemporal empTemporal = new EmpleadoTemporal(nombre, salario);
                    empleados.add(empTemporal);
                }
            }
            System.out.println("Empleados cargados desde el archivo.");
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado. Iniciando lista vacía.");
        } catch (IOException | SalarioInvalido e) {
            System.out.println("Error al leer los empleados: " + e.getMessage());
        }
        return empleados;
    }
}

