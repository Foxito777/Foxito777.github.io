package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.entidades.Usuario;
import com.proyectoweb.Juledtoys.repositorios.ProductoRepositoryJPAInterface;
import com.proyectoweb.Juledtoys.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Servicio para inicializar datos de prueba en la base de datos H2
 */
@Component
public class InicializadorDatos implements CommandLineRunner {

    @Autowired
    private ProductoRepositoryJPAInterface productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo inicializar si no hay datos
        if (productoRepository.count() == 0) {
            inicializarProductos();
        }

        if (usuarioRepository.count() == 0) {
            inicializarUsuarios();
        }
    }

    private void inicializarProductos() {
        // Productos de LEGO Star Wars
        Producto legoStarWars1 = new Producto();
        legoStarWars1.setNombre("LEGO Star Wars Millennium Falcon");
        legoStarWars1.setDescripcion("Construye la nave más icónica de Star Wars con este increíble set de LEGO. Incluye figuras de Han Solo, Chewbacca y más.");
        legoStarWars1.setPrecio(new BigDecimal("899.99"));
        legoStarWars1.setCategoria("LEGO Star Wars");
        legoStarWars1.setStock(15);
        legoStarWars1.setImagen("caza-estelar.jpg");
        legoStarWars1.setDisponible(true);

        Producto legoStarWars2 = new Producto();
        legoStarWars2.setNombre("LEGO Star Wars Cantina de Mos Eisley");
        legoStarWars2.setDescripcion("Recrea las escenas épicas de la cantina de Mos Eisley con este detallado set de LEGO Star Wars.");
        legoStarWars2.setPrecio(new BigDecimal("579.99"));
        legoStarWars2.setCategoria("LEGO Star Wars");
        legoStarWars2.setStock(20);
        legoStarWars2.setImagen("lego-star-wars-cantina-mos-eisley-75290-4249661.webp");
        legoStarWars2.setDisponible(true);

        Producto legoStarWars3 = new Producto();
        legoStarWars3.setNombre("LEGO Star Wars Caza Estelar X-Wing");
        legoStarWars3.setDescripcion("Construye el legendario caza estelar X-Wing de Luke Skywalker con este emocionante set de LEGO.");
        legoStarWars3.setPrecio(new BigDecimal("299.99"));
        legoStarWars3.setCategoria("LEGO Star Wars");
        legoStarWars3.setStock(25);
        legoStarWars3.setImagen("Caza.png");
        legoStarWars3.setDisponible(true);

        // Productos de Colección
        Producto coleccion1 = new Producto();
        coleccion1.setNombre("Figura de Acción Premium Batman");
        coleccion1.setDescripcion("Figura coleccionable de Batman con articulaciones múltiples y accesorios detallados.");
        coleccion1.setPrecio(new BigDecimal("149.99"));
        coleccion1.setCategoria("Colección");
        coleccion1.setStock(30);
        coleccion1.setImagen("Coleccion.png");
        coleccion1.setDisponible(true);

        Producto coleccion2 = new Producto();
        coleccion2.setNombre("Set de Figuras de Superhéroes");
        coleccion2.setDescripcion("Colección completa de figuras de superhéroes con stands y accesorios exclusivos.");
        coleccion2.setPrecio(new BigDecimal("299.99"));
        coleccion2.setCategoria("Colección");
        coleccion2.setStock(12);
        coleccion2.setImagen("123.jpg");
        coleccion2.setDisponible(true);

        // Productos de LEGO Ninjago
        Producto ninjago1 = new Producto();
        ninjago1.setNombre("LEGO Ninjago Dragón de Fuego");
        ninjago1.setDescripcion("Construye el poderoso Dragón de Fuego con este emocionante set de LEGO Ninjago.");
        ninjago1.setPrecio(new BigDecimal("199.99"));
        ninjago1.setCategoria("LEGO Ninjago");
        ninjago1.setStock(18);
        ninjago1.setImagen("Ninja.png");
        ninjago1.setDisponible(true);

        Producto ninjago2 = new Producto();
        ninjago2.setNombre("LEGO Ninjago Templo de los Ninjas");
        ninjago2.setDescripcion("Templo detallado con múltiples habitaciones, trampas secretas y figuras de ninjas.");
        ninjago2.setPrecio(new BigDecimal("449.99"));
        ninjago2.setCategoria("LEGO Ninjago");
        ninjago2.setStock(10);
        ninjago2.setImagen("Ninja.png");
        ninjago2.setDisponible(true);

        // Productos de LEGO Creator
        Producto creator1 = new Producto();
        creator1.setNombre("LEGO Creator Ferrari F40");
        creator1.setDescripcion("Replica detallada del icónico Ferrari F40 con características auténticas y funciones realistas.");
        creator1.setPrecio(new BigDecimal("799.99"));
        creator1.setCategoria("LEGO Creator");
        creator1.setStock(8);
        creator1.setImagen("Ferrari.png");
        creator1.setDisponible(true);

        Producto creator2 = new Producto();
        creator2.setNombre("LEGO Creator Casa Mansión");
        creator2.setDescripcion("Impresionante mansión de tres pisos con muebles detallados y características realistas.");
        creator2.setPrecio(new BigDecimal("649.99"));
        creator2.setCategoria("LEGO Creator");
        creator2.setStock(6);
        creator2.setImagen("Mansion.png");
        creator2.setDisponible(true);

        // Libros y Comics
        Producto libro1 = new Producto();
        libro1.setNombre("Enciclopedia Visual LEGO");
        libro1.setDescripcion("Guía completa con la historia de LEGO, sets exclusivos y técnicas de construcción avanzadas.");
        libro1.setPrecio(new BigDecimal("89.99"));
        libro1.setCategoria("Libros");
        libro1.setStock(25);
        libro1.setImagen("Libro.png");
        libro1.setDisponible(true);

        // Accesorios
        Producto accesorio1 = new Producto();
        accesorio1.setNombre("Base de Exposición Premium");
        accesorio1.setDescripcion("Base giratoria con iluminación LED para exhibir tus sets de LEGO favoritos.");
        accesorio1.setPrecio(new BigDecimal("59.99"));
        accesorio1.setCategoria("Accesorios");
        accesorio1.setStock(35);
        accesorio1.setImagen("acc6.png");
        accesorio1.setDisponible(true);

        // Guardar todos los productos
        productoRepository.save(legoStarWars1);
        productoRepository.save(legoStarWars2);
        productoRepository.save(legoStarWars3);
        productoRepository.save(coleccion1);
        productoRepository.save(coleccion2);
        productoRepository.save(ninjago1);
        productoRepository.save(ninjago2);
        productoRepository.save(creator1);
        productoRepository.save(creator2);
        productoRepository.save(libro1);
        productoRepository.save(accesorio1);

        System.out.println("✅ Productos inicializados en la base de datos H2");
    }

    private void inicializarUsuarios() {
        // Crear usuario administrador
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setEmail("admin@juledtoys.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRol(Usuario.Rol.ADMIN);
        admin.setActivo(true);

        // Crear usuario de prueba
        Usuario usuarioPrueba = new Usuario();
        usuarioPrueba.setUsername("usuario");
        usuarioPrueba.setNombre("Usuario");
        usuarioPrueba.setApellido("Prueba");
        usuarioPrueba.setEmail("usuario@test.com");
        usuarioPrueba.setPassword(passwordEncoder.encode("usuario123"));
    usuarioPrueba.setRol(Usuario.Rol.VENDEDOR);
        usuarioPrueba.setActivo(true);

        usuarioRepository.save(admin);
        usuarioRepository.save(usuarioPrueba);

        System.out.println("✅ Usuarios inicializados en la base de datos H2");
        System.out.println("📧 Admin: admin@juledtoys.com / admin123");
        System.out.println("📧 Usuario: usuario@test.com / usuario123");
    }
}