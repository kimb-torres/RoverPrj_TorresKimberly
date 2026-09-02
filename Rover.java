import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Rover {

    private static int cantidadRoversCreados = 0;
    private static List<Rover> listaRoversCreados = new ArrayList<>();

    private String nombreRover;
    private String codigoRover;

    private double potenciaInicial;
    private double potenciaDisponible;
    private double costoMovimiento = 0.50;
    private double costoDeteccionFuga = 0.25;

    private int posicionInicialX = 0;
    private int posicionInicialY = 0;
    private int posicionActualX;
    private int posicionActualY;

    private int recargasRealizadas = 0;
    private int recargasMaximas = 5;
    private int deteccionesFuga = 0;

    private List<List<String>> mandatosExitosos = new ArrayList<>();
    private List<List<String>> mandatosFallidos = new ArrayList<>();

    public Rover(String nombreRover) {
        this(nombreRover, 100.0);
    }

    public Rover(String nombreRover, double potencia) {
        if (potencia <= 0) {
            potencia = 100.0;
        }

        this.nombreRover = nombreRover;
        this.potenciaInicial = potencia;
        this.potenciaDisponible = potencia;

        this.posicionActualX = posicionInicialX;
        this.posicionActualY = posicionInicialY;

        cantidadRoversCreados++;
        this.codigoRover = "RVR-" + cantidadRoversCreados + "-" + (System.currentTimeMillis() % 100000);
        listaRoversCreados.add(this);
    }

   

    public void moverArriba() {
        moverEnDireccion("Arriba", 0, 1);
    }

    public void moverAbajo() {
        moverEnDireccion("Abajo", 0, -1);
    }

    public void moverIzquierda() {
        moverEnDireccion("Izquierda", -1, 0);
    }

    public void moverDerecha() {
        moverEnDireccion("Derecha", 1, 0);
    }

   
    // Evitar repetir 4 veces el mismo if/else de validación de potencia y fuga.
    private void moverEnDireccion(String nombreDireccion, int deltaX, int deltaY) {
        String mandato = "Mover a " + nombreDireccion;

        if (!hayPotenciaSuficiente()) {
            registrarMandatoFallido(mandato, "No Posible: Potencia Insuficiente");
            System.out.println(">> Movimiento fallido: Potencia insuficiente.");
            return;
        }

        if (detectarFuga()) {
            registrarMandatoFallido(mandato, "No Posible: Fuga detectada");
            System.out.println(">> Movimiento fallido: Se detectó una fuga.");
            return;
        }

        posicionActualX += deltaX;
        posicionActualY += deltaY;
        potenciaDisponible -= costoMovimiento;
        registrarMandatoExitoso(mandato, "Posible");
        System.out.println(">> Movimiento exitoso hacia " + nombreDireccion.toUpperCase() + ".");
    }

    public boolean hayPotenciaSuficiente() {
        return potenciaDisponible >= (costoMovimiento + costoDeteccionFuga);
    }

    private boolean detectarFuga() {
        deteccionesFuga++;
        potenciaDisponible -= costoDeteccionFuga;

        double numeroAleatorio = new Random().nextDouble();
        return numeroAleatorio >= 0.5;
    }

   

    public void cargarPotencia(int cantidad) {
        if (cantidad <= 0) {
            registrarMandatoFallido("Recarga Potencia", "No posible: la recarga debe ser mayor a 0");
            System.out.println(">> Recarga fallida: La cantidad debe ser mayor a 0.");
            return;
        }

        if (recargasRealizadas >= recargasMaximas) {
            registrarMandatoFallido("Recarga Potencia", "No posible: recargas en su limite");
            System.out.println(">> Recarga fallida: Alcanzado el límite de recargas (" + recargasMaximas + ").");
            return;
        }

        potenciaDisponible += cantidad;
        recargasRealizadas++;
        registrarMandatoExitoso("Recarga Potencia", "Posible: Recarga realizada");
        System.out.println(">> Recarga de " + cantidad + " realizada correctamente.");
    }

    public String potenciaActual() {
        return "Potencia Actual: " + potenciaDisponible;
    }

    

    public String posicionActual() {
        return "Posición (X,Y): (" + posicionActualX + "," + posicionActualY + ")";
    }

   

    private void registrarMandatoExitoso(String mandato, String estado) {
        mandatosExitosos.add(crearRegistro(mandato, estado));
    }

    private void registrarMandatoFallido(String mandato, String estado) {
        mandatosFallidos.add(crearRegistro(mandato, estado));
    }

    private List<String> crearRegistro(String mandato, String estado) {
        List<String> registro = new ArrayList<>();
        registro.add(obtenerFechaHora());
        registro.add(mandato);
        registro.add(estado);
        return registro;
    }

    private String obtenerFechaHora() {
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatoFecha.format(new Date());
    }

    

    public static int obtenerCantidadRoversCreados() {
        return cantidadRoversCreados;
    }

    public static List<Rover> obtenerListaRoversCreados() {
        return listaRoversCreados;
    }

    

    @Override
    public String toString() {
        StringBuilder estado = new StringBuilder();

        estado.append("Código Rover: ").append(codigoRover).append("\n");
        estado.append("Nombre Rover: ").append(nombreRover).append("\n");
        estado.append("Potencia Inicial: ").append(potenciaInicial).append("\n");
        estado.append("Potencia Disponible: ").append(potenciaDisponible).append("\n");
        estado.append("Recargas Disponibles: ").append(recargasMaximas - recargasRealizadas).append("\n");
        estado.append("Detecciones de Fuga Realizadas: ").append(deteccionesFuga).append("\n");
        estado.append("Posición Inicial: (").append(posicionInicialX).append(",").append(posicionInicialY).append(")\n");
        estado.append("Posición Actual: (").append(posicionActualX).append(",").append(posicionActualY).append(")\n");
        estado.append("-------------------------------------------\n");

        estado.append("MANDATOS EXITOSOS:\n");
        agregarListaMandatos(estado, mandatosExitosos);

        estado.append("-------------------------------------------\n");
        estado.append("MANDATOS FALLIDOS:\n");
        agregarListaMandatos(estado, mandatosFallidos);

        return estado.toString();
    }

    private void agregarListaMandatos(StringBuilder texto, List<List<String>> mandatos) {
        if (mandatos.isEmpty()) {
            texto.append("  (ninguno)\n");
            return;
        }

        for (List<String> mandato : mandatos) {
            texto.append("  [").append(mandato.get(0)).append("] ")
                 .append(mandato.get(1)).append(" -> ").append(mandato.get(2)).append("\n");
        }
    }

    --

    public static void main(String[] args) {
        Scanner lectorTeclado = new Scanner(System.in);

        System.out.print("Ingrese el nombre del Rover: ");
        String nombreUsuario = lectorTeclado.nextLine();

        System.out.print("¿Desea configurar la potencia inicial? (s/n): ");
        String respuesta = lectorTeclado.nextLine();

        Rover roverActivo;
        if (respuesta.equalsIgnoreCase("s")) {
            System.out.print("Ingrese la potencia inicial deseada: ");
            double potenciaUsuario = lectorTeclado.nextDouble();
            lectorTeclado.nextLine();
            roverActivo = new Rover(nombreUsuario, potenciaUsuario);
        } else {
            roverActivo = new Rover(nombreUsuario);
            System.out.println(">> Se asignó la potencia por defecto (100.0).");
        }

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n1. Mover Arriba");
            System.out.println("2. Mover Abajo");
            System.out.println("3. Mover Izquierda");
            System.out.println("4. Mover Derecha");
            System.out.println("5. Cargar Potencia");
            System.out.println("6. Ver Posición Actual");
            System.out.println("7. Ver Potencia Actual");
            System.out.println("8. Generar Reporte Completo");
            System.out.println("9. Salir");
            System.out.print("Seleccione una opción: ");

            if (!lectorTeclado.hasNextInt()) {
                System.out.println(">> Entrada inválida. Ingrese un número.");
                lectorTeclado.nextLine();
                continue;
            }

            int opcion = lectorTeclado.nextInt();
            lectorTeclado.nextLine();

            switch (opcion) {
                case 1:
                    roverActivo.moverArriba();
                    break;
                case 2:
                    roverActivo.moverAbajo();
                    break;
                case 3:
                    roverActivo.moverIzquierda();
                    break;
                case 4:
                    roverActivo.moverDerecha();
                    break;
                case 5:
                    System.out.print("Ingrese la cantidad a recargar: ");
                    if (lectorTeclado.hasNextInt()) {
                        int cantidad = lectorTeclado.nextInt();
                        lectorTeclado.nextLine();
                        roverActivo.cargarPotencia(cantidad);
                    } else {
                        System.out.println(">> Error: Ingrese un valor entero válido.");
                        lectorTeclado.nextLine();
                    }
                    break;
                case 6:
                    System.out.println(">> " + roverActivo.posicionActual());
                    break;
                case 7:
                    System.out.println(">> " + roverActivo.potenciaActual());
                    break;
                case 8:
                    System.out.println(roverActivo.toString());
                    break;
                case 9:
                    continuar = false;
                    System.out.println(">> Programa finalizado.");
                    break;
                default:
                    System.out.println(">> Opción no válida.");
            }
        }

        lectorTeclado.close();
    }
}