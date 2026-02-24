package com.aluraChallenge.LiterAlura.principal;
import com.aluraChallenge.LiterAlura.model.Autor;
import com.aluraChallenge.LiterAlura.model.DatosLibro;
import com.aluraChallenge.LiterAlura.model.DatosResultados;
import com.aluraChallenge.LiterAlura.model.Libro;
import com.aluraChallenge.LiterAlura.repository.AutorRepository;
import com.aluraChallenge.LiterAlura.repository.LibroRepository;
import com.aluraChallenge.LiterAlura.service.ConsumoAPI;
import com.aluraChallenge.LiterAlura.service.ConvierteDatos;
import org.springframework.stereotype.Component;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/?search=";

    private LibroRepository libroRepositorio;
    private AutorRepository autorRepositorio;

    public Principal(LibroRepository libroRepositorio, AutorRepository autorRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    === L I T E R A L U R A ===
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    --- OPCIONES EXTRA ---
                    6 - Top 10 libros más descargados
                    7 - Buscar autor por nombre
                    8 - Estadísticas de descargas
                    0 - Salir
                    ===========================
                    Elija una opción:""";
            System.out.println(menu);

            try {
                opcion = Integer.parseInt(teclado.nextLine());

                switch (opcion) {
                    case 1 -> buscarLibroWeb();
                    case 2 -> listarLibrosRegistrados();
                    case 3 -> listarAutoresRegistrados();
                    case 4 -> listarAutoresVivosEnAnio();
                    case 5 -> listarLibrosPorIdioma();
                    case 6 -> top10LibrosMasDescargados();
                    case 7 -> buscarAutorPorNombre();
                    case 8 -> generarEstadisticas();
                    case 0 -> System.out.println("Cerrando la aplicación... ¡Hasta pronto!");
                    default -> System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private void buscarLibroWeb() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        var tituloLibro = teclado.nextLine();

        var json = consumoApi.obtenerDatos(URL_BASE + tituloLibro.replace(" ", "%20"));
        var datosBusqueda = conversor.obtenerDatos(json, DatosResultados.class);

        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibro datosLibro = libroBuscado.get();
            System.out.println("Libro encontrado: " + datosLibro.titulo());

            var datosAutor = datosLibro.autores().get(0);
            Autor autor = autorRepositorio.findByNombreIgnoreCase(datosAutor.nombre())
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor(datosAutor);
                        return autorRepositorio.save(nuevoAutor); // Guardamos el nuevo autor
                    });

            Libro libro = new Libro(datosLibro, autor);
            try {
                libroRepositorio.save(libro);
                System.out.println("¡Libro guardado exitosamente en la base de datos!");
            } catch (Exception e) {
                System.out.println("El libro ya se encuentra registrado en la base de datos.");
            }

        } else {
            System.out.println("Libro no encontrado en Gutendex.");
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepositorio.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados aún.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingrese el año que desea consultar:");
        try {
            int anio = Integer.parseInt(teclado.nextLine());
            List<Autor> autoresVivos = autorRepositorio.autoresVivosEnUnAnio(anio);
            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio);
            } else {
                autoresVivos.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            System.out.println("Año inválido. Ingrese un número.");
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar los libros:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués
                """);
        String idioma = teclado.nextLine();
        List<Libro> librosPorIdioma = libroRepositorio.findByIdioma(idioma);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros registrados en ese idioma.");
        } else {
            System.out.println("Cantidad de libros encontrados en '" + idioma + "': " + librosPorIdioma.size());
            librosPorIdioma.forEach(System.out::println);
        }
    }
    private void top10LibrosMasDescargados() {
        System.out.println("--- TOP 10 LIBROS MÁS DESCARGADOS ---");
        List<Libro> libros = libroRepositorio.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros en la base de datos para generar un Top 10.");
            return;
        }

        libros.stream()
                .sorted((l1, l2) -> l2.getNumeroDeDescargas().compareTo(l1.getNumeroDeDescargas()))
                .limit(10)
                .forEach(l -> System.out.println(
                        l.getTitulo() + " - Descargas: " + l.getNumeroDeDescargas()));
    }
    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre (o parte del nombre) del autor que desea buscar:");
        String nombre = teclado.nextLine();

        Optional<Autor> autorBuscado = autorRepositorio.findByNombreIgnoreCase(nombre);

        if (autorBuscado.isPresent()) {
            System.out.println("Autor encontrado:");
            System.out.println(autorBuscado.get());
        } else {

            System.out.println("No se encontró ningún autor con ese nombre exacto en la base de datos.");
        }
    }

    private void generarEstadisticas() {
        System.out.println("--- ESTADÍSTICAS DE DESCARGAS ---");
        List<Libro> libros = libroRepositorio.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros suficientes para calcular estadísticas.");
            return;
        }

        DoubleSummaryStatistics estadisticas = libros.stream()
                .filter(l -> l.getNumeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));

        System.out.println("Cantidad de libros evaluados: " + estadisticas.getCount());
        System.out.println("Promedio de descargas: " + Math.round(estadisticas.getAverage()));
        System.out.println("Libro con más descargas (Máximo): " + estadisticas.getMax());
        System.out.println("Libro con menos descargas (Mínimo): " + estadisticas.getMin());
    }
}
