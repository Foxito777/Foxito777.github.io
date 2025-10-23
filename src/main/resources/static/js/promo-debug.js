/**
 * Debug y utilidades para el sistema de promociones
 * Juled Toys E-commerce
 */

// Función para verificar el estado del sistema de promociones
function debugPromos() {
    console.log("=== DEBUG PROMOCIONES ===");
    
    // Verificar contenedor
    const container = document.getElementById("promoCarouselContainer");
    console.log("🔍 Contenedor encontrado:", !!container);
    if (container) {
        console.log("📦 Contenido del contenedor:", container.innerHTML);
    }
    
    // Verificar API endpoint
    console.log("🌐 Testeando endpoint /api/promociones/activas...");
    fetch("/api/promociones/activas")
        .then(response => {
            console.log("📡 Status de respuesta:", response.status);
            console.log("🔗 URL completa:", response.url);
            return response.json();
        })
        .then(data => {
            console.log("📊 Datos recibidos:", data);
            console.log("📈 Cantidad de promociones:", data.length);
            data.forEach((promo, index) => {
                console.log(`  ${index + 1}. ${promo.titulo} - Estado: ${promo.estado}`);
            });
        })
        .catch(error => {
            console.error("❌ Error en API:", error);
        });
        
    // Verificar Bootstrap
    console.log("🥾 Bootstrap disponible:", typeof bootstrap !== 'undefined');
    if (typeof bootstrap !== 'undefined') {
        console.log("🎠 Carousel de Bootstrap:", typeof bootstrap.Carousel !== 'undefined');
    }
}

// Función para forzar recarga de promociones
function reloadPromos() {
    console.log("🔄 Forzando recarga de promociones...");
    if (window.promoUtils && window.promoUtils.initPromoCarousel) {
        window.promoUtils.initPromoCarousel();
    } else {
        console.log("📥 Usando función global...");
        initPromoCarousel();
    }
}

// Función para agregar promoción de prueba
function addTestPromo() {
    const testPromo = {
        id: 999,
        etiqueta: "TEST",
        titulo: "Promoción de Prueba",
        subtitulo: "Solo para testing",
        descripcion: "Esta es una promoción generada dinámicamente para pruebas",
        ctaTexto: "Ver Prueba",
        ctaUrl: "/productos",
        imagen: "/Imagenes/Logo.png",
        colorFondo: "#ff9800",
        colorTexto: "#ffffff",
        estado: "ACTIVA",
        destacado: true,
        orden: 999
    };
    
    console.log("➕ Agregando promoción de prueba:", testPromo);
    
    // Simular que agregamos la promoción a la API (esto sería real en un backend)
    if (window.promoUtils && window.promoUtils.agregarPromocionDinamica) {
        window.promoUtils.agregarPromocionDinamica(testPromo);
    } else {
        console.log("⚠️ No se encontró la función para agregar promociones");
    }
}

// Función para mostrar estadísticas del carrusel
function promoStats() {
    const carousel = document.getElementById("promoCarousel");
    if (!carousel) {
        console.log("❌ No hay carrusel de promociones activo");
        return;
    }
    
    const slides = carousel.querySelectorAll('.carousel-item');
    const activeSlide = carousel.querySelector('.carousel-item.active');
    const indicators = carousel.querySelectorAll('.carousel-indicators button');
    
    console.log("📊 ESTADÍSTICAS DEL CARRUSEL:");
    console.log(`  📽️ Total de slides: ${slides.length}`);
    console.log(`  ▶️ Slide activo: ${Array.from(slides).indexOf(activeSlide) + 1}`);
    console.log(`  🎯 Indicadores: ${indicators.length}`);
    console.log(`  🎨 Clases CSS aplicadas:`, carousel.className);
}

// Función para probar navegación del carrusel
function testCarouselNavigation() {
    const carousel = document.getElementById("promoCarousel");
    if (!carousel) {
        console.log("❌ No hay carrusel para probar");
        return;
    }
    
    console.log("🧪 Iniciando prueba de navegación...");
    
    const bsCarousel = bootstrap.Carousel.getInstance(carousel);
    if (!bsCarousel) {
        console.log("❌ No se pudo obtener instancia de Bootstrap Carousel");
        return;
    }
    
    // Probar navegación automática
    console.log("➡️ Ir al siguiente slide...");
    bsCarousel.next();
    
    setTimeout(() => {
        console.log("⬅️ Ir al slide anterior...");
        bsCarousel.prev();
    }, 2000);
    
    setTimeout(() => {
        console.log("⏸️ Pausar carrusel...");
        bsCarousel.pause();
    }, 4000);
    
    setTimeout(() => {
        console.log("▶️ Reanudar carrusel...");
        bsCarousel.cycle();
    }, 6000);
}

// Función para verificar imágenes
function checkPromoImages() {
    console.log("🖼️ Verificando imágenes de promociones...");
    
    const images = document.querySelectorAll('#promoCarousel img');
    console.log(`📸 Encontradas ${images.length} imágenes`);
    
    images.forEach((img, index) => {
        const testImg = new Image();
        testImg.onload = () => {
            console.log(`✅ Imagen ${index + 1} cargada: ${img.src}`);
        };
        testImg.onerror = () => {
            console.log(`❌ Error al cargar imagen ${index + 1}: ${img.src}`);
        };
        testImg.src = img.src;
    });
}

// Función para limpiar y resetear promociones
function resetPromos() {
    const container = document.getElementById("promoCarouselContainer");
    if (container) {
        container.innerHTML = '<p class="text-center">Reseteando promociones...</p>';
        setTimeout(() => {
            initPromoCarousel();
        }, 1000);
    }
}

// Exponer funciones de debug al scope global
window.promoDebug = {
    debug: debugPromos,
    reload: reloadPromos,
    addTest: addTestPromo,
    stats: promoStats,
    testNav: testCarouselNavigation,
    checkImages: checkPromoImages,
    reset: resetPromos
};

// Auto-ejecutar debug si estamos en modo desarrollo
if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    setTimeout(() => {
        console.log("🔧 Modo desarrollo detectado. Ejecutando debug automático...");
        console.log("💡 Usa window.promoDebug para acceder a las funciones de debug");
        debugPromos();
    }, 2000);
}