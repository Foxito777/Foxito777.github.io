package com.proyectoweb.Juledtoys.config;

import com.proyectoweb.Juledtoys.entidades.Promocion;
import com.proyectoweb.Juledtoys.repositorios.PromocionRepositoryJPA;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("!production") // Desactivar en producción para evitar fallos por charset al iniciar
@Order(2) // Ejecutar después de ProductoSeed
public class PromocionSeed implements CommandLineRunner {

    private final PromocionRepositoryJPA promocionRepository;

    public PromocionSeed(PromocionRepositoryJPA promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @Override
    public void run(String... args) {
        // Solo inicializar si no hay promociones
        if (promocionRepository.count() == 0) {
            LocalDate hoy = LocalDate.now();

            Promocion promo1 = new Promocion(
                    "Oferta", "🎯 Mega Descuento LEGO", "Hasta 40% OFF en sets seleccionados",
                    "Aprovecha las ofertas de temporada en sets de colección. ¡Solo por tiempo limitado!",
                    "Comprar Ahora", "/productos",
                    "/Imagenes/Set-completos.png",
                    "#ff6b35", "#ffffff",
                    hoy.minusDays(2), hoy.plusDays(10),
                    "ACTIVA", "ALTA", true, 1
            );

            Promocion promo2 = new Promocion(
                    "Especial", "⭐ Colección Star Wars 3x2", "Sets únicos por tiempo limitado",
                    "Descubre la galaxia muy muy lejana con nuestros sets de Star Wars más populares",
                    "Explorar", "/productos",
                    "/Imagenes/Caza.png",
                    "#0066cc", "#ffffff",
                    hoy.minusDays(5), hoy.plusDays(5),
                    "ACTIVA", "MEDIA", true, 2
            );

            Promocion promo3 = new Promocion(
                    "Nuevo", "🏎️ Velocidad Extrema", "Autos de carrera con 50% OFF",
                    "Construye los autos más rápidos del mundo. Ferrari, RedBull y más marcas disponibles",
                    "Ver Ofertas", "/productos",
                    "/Imagenes/Ferrari.png",
                    "#dc3545", "#ffffff",
                    hoy.minusDays(1), hoy.plusDays(7),
                    "ACTIVA", "ALTA", true, 3
            );

            Promocion promo4 = new Promocion(
                    "Creatividad", "🧩 Bloques Creativos", "Para mentes innovadoras",
                    "Desarrolla la creatividad con nuestros bloques de construcción especiales",
                    "Descubrir", "/productos",
                    "/Imagenes/acc6.png",
                    "#28a745", "#ffffff",
                    hoy, hoy.plusDays(15),
                    "ACTIVA", "MEDIA", true, 4
            );

            promocionRepository.save(promo1);
            promocionRepository.save(promo2);
            promocionRepository.save(promo3);
            promocionRepository.save(promo4);

            System.out.println("✨ Promociones inicializadas en la base de datos");
        }
    }
}