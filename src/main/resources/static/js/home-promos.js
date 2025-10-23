/**
 * Home · Carrusel de Promociones Mejorado
 * - Consume /api/promociones/activas
 * - Renderiza dinámicamente un carrusel Bootstrap 5
 * - Incluye manejo de errores mejorado y funcionalidades adicionales
 */

document.addEventListener("DOMContentLoaded", () => {
    initPromoCarousel();
});

function initPromoCarousel() {
    const container = document.getElementById("promoCarouselContainer");
    if (!container) {
        console.warn("⚠️ No se encontró el contenedor promoCarouselContainer");
        return;
    }

    // Mostrar loading
    showLoadingState(container);

    // Fetch promociones con reintentos
    fetchPromocionesWithRetry(container, 3);
}

function showLoadingState(container) {
    container.innerHTML = `
        <div class="text-center py-5">
            <div class="spinner-border text-warning" role="status">
                <span class="visually-hidden">Cargando promociones...</span>
            </div>
            <p class="mt-2 text-muted">Cargando promociones...</p>
        </div>
    `;
}

function fetchPromocionesWithRetry(container, maxRetries) {
    let attempts = 0;

    function attemptFetch() {
        attempts++;
        console.log(`🔄 Intento ${attempts} de cargar promociones...`);

        fetch("/api/promociones/activas")
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(promos => {
                console.log(`✅ Promociones cargadas exitosamente:`, promos);
                renderCarousel(container, promos);
            })
            .catch(error => {
                console.error(`❌ Error en intento ${attempts}:`, error);
                
                if (attempts < maxRetries) {
                    console.log(`🔁 Reintentando en 2 segundos...`);
                    setTimeout(attemptFetch, 2000);
                } else {
                    showErrorState(container, error);
                }
            });
    }

    attemptFetch();
}

function showErrorState(container, error) {
    container.innerHTML = `
        <div class="alert alert-warning text-center py-4" role="alert">
            <i class="fas fa-exclamation-triangle fa-2x mb-3 text-warning"></i>
            <h5>No se pudieron cargar las promociones</h5>
            <p class="mb-3">Error: ${error.message}</p>
            <button class="btn btn-warning" onclick="initPromoCarousel()">
                <i class="fas fa-redo me-2"></i>Reintentar
            </button>
        </div>
    `;
}

function renderCarousel(container, promos) {
    if (!promos || promos.length === 0) {
        showEmptyState(container);
        return;
    }

    console.log(`🎠 Renderizando ${promos.length} promociones`);
    
    const carouselId = "promoCarousel";
    const indicadores = generateIndicators(promos.length, carouselId);
    const slides = generateSlides(promos);

    container.innerHTML = `
        <div id="${carouselId}" class="carousel slide shadow-lg rounded-4 mb-1" 
             data-bs-ride="carousel" data-bs-interval="6000">
            ${indicadores}
            <div class="carousel-inner">
                ${slides}
            </div>
            ${generateControls(carouselId)}
        </div>
    `;

    // Inicializar carousel con configuración avanzada
    initAdvancedCarousel(carouselId);
}

function showEmptyState(container) {
    container.innerHTML = `
        <div class="alert alert-info text-center py-4" role="alert">
            <i class="fas fa-info-circle fa-2x mb-3 text-info"></i>
            <h5>No hay promociones activas</h5>
            <p class="mb-0">¡Mantente atento a nuestras próximas ofertas!</p>
        </div>
    `;
}

function generateIndicators(count, carouselId) {
    if (count <= 1) return '';
    
    const indicators = Array.from({ length: count }, (_, i) => 
        `<button type="button" data-bs-target="#${carouselId}" data-bs-slide-to="${i}" 
                 ${i === 0 ? 'class="active" aria-current="true"' : ''} 
                 aria-label="Promoción ${i + 1}"></button>`
    ).join('');

    return `<div class="carousel-indicators">${indicators}</div>`;
}

function generateSlides(promos) {
    return promos.map((promo, index) => {
        const isActive = index === 0 ? 'active' : '';
        const bgColor = promo.colorFondo || '#ff6b35';
        const textColor = promo.colorTexto || '#ffffff';
        
        return `
            <div class="carousel-item ${isActive}">
                <div class="promo-slide d-flex align-items-center justify-content-center gap-3 p-1 p-md-3 rounded-4"
                     style="background: linear-gradient(135deg, ${bgColor} 0%, ${adjustBrightness(bgColor, -20)} 100%); 
                            color: ${textColor}; min-height: 180px;">
                    <div class="promo-content text-center" style="flex: 1; max-width: 55%;">
                        ${promo.etiqueta ? generateBadge(promo.etiqueta, textColor, bgColor) : ''}
                        ${promo.titulo ? `<h2 class="promo-title mt-2 mb-1 fw-bold">${promo.titulo}</h2>` : ''}
                        ${promo.subtitulo ? `<h4 class="promo-subtitle mb-2 fw-normal">${promo.subtitulo}</h4>` : ''}
                        ${promo.descripcion ? `<p class="promo-description mb-3">${promo.descripcion}</p>` : ''}
                        ${generateCTA(promo, textColor)}
                    </div>
                    <div class="promo-image-container d-flex justify-content-center align-items-center px-3" style="flex: 1; max-width: 40%;">
                        ${generateImage(promo)}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function generateBadge(etiqueta, textColor, bgColor) {
    return `
        <span class="badge rounded-pill px-3 py-2 fw-semibold" 
              style="background: ${textColor}; color: ${bgColor}; font-size: 0.9rem;">
            ${etiqueta}
        </span>
    `;
}

function generateCTA(promo, textColor) {
    if (!promo.ctaTexto || !promo.ctaUrl) return '';
    
    return `
        <a class="btn btn-lg fw-semibold rounded-pill px-4 py-2 promo-cta" 
           href="${promo.ctaUrl}" 
           style="background: ${textColor}; color: ${promo.colorFondo || '#ff6b35'}; 
                  border: 2px solid ${textColor};">
            <i class="fas fa-shopping-cart me-2"></i>${promo.ctaTexto}
        </a>
    `;
}

function generateImage(promo) {
    if (!promo.imagen) return '';
    
    return `
        <img class="rounded-3 shadow mx-auto d-block" 
             src="${promo.imagen}" 
             alt="Promoción ${promo.titulo || ''}"
             style="max-height: 250px; max-width: 380px; object-fit: cover;"
             onerror="this.style.display='none'">
    `;
}

function generateControls(carouselId) {
    return `
        <button class="carousel-control-prev" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
            <span class="carousel-control-prev-icon bg-dark rounded-circle shadow" aria-hidden="true"></span>
            <span class="visually-hidden">Anterior</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
            <span class="carousel-control-next-icon bg-dark rounded-circle shadow" aria-hidden="true"></span>
            <span class="visually-hidden">Siguiente</span>
        </button>
    `;
}

function initAdvancedCarousel(carouselId) {
    const carouselElement = document.getElementById(carouselId);
    if (!carouselElement) return;

    // Configurar carousel avanzado
    const carousel = new bootstrap.Carousel(carouselElement, {
        interval: 6000,
        ride: 'carousel',
        pause: 'hover',
        wrap: true,
        keyboard: true,
        touch: true
    });

    // Agregar eventos personalizados
    carouselElement.addEventListener('slide.bs.carousel', function (event) {
        // Animación suave entre slides
        const currentSlide = event.target.querySelector('.carousel-item.active');
        const nextSlide = event.relatedTarget;
        
        if (currentSlide) {
            currentSlide.style.transition = 'transform 0.8s ease-in-out';
        }
        if (nextSlide) {
            nextSlide.style.transition = 'transform 0.8s ease-in-out';
        }
    });

    // Pausar en hover y reanudar al salir
    carouselElement.addEventListener('mouseenter', () => carousel.pause());
    carouselElement.addEventListener('mouseleave', () => carousel.cycle());

    // Soporte para gestos táctiles
    initTouchSupport(carouselElement, carousel);
    
    console.log(`✅ Carousel de promociones inicializado: ${carouselId}`);
}

function initTouchSupport(element, carousel) {
    let startX = 0;
    let threshold = 50;

    element.addEventListener('touchstart', (e) => {
        startX = e.touches[0].clientX;
    }, { passive: true });

    element.addEventListener('touchend', (e) => {
        if (!startX) return;
        
        const endX = e.changedTouches[0].clientX;
        const diff = startX - endX;

        if (Math.abs(diff) > threshold) {
            if (diff > 0) {
                carousel.next();
            } else {
                carousel.prev();
            }
        }
        startX = 0;
    }, { passive: true });
}

function adjustBrightness(hex, percent) {
    // Función utilitaria para ajustar el brillo de un color
    const num = parseInt(hex.replace("#", ""), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = (num >> 8 & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;
    return "#" + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
        (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
        (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1);
}

// Función para agregar una nueva promoción dinámicamente (si es necesario)
function agregarPromocionDinamica(promocion) {
    const container = document.getElementById("promoCarouselContainer");
    if (!container) return;

    // Re-fetch y re-render las promociones
    initPromoCarousel();
}

// Exponer funciones para uso global si es necesario
window.promoUtils = {
    initPromoCarousel,
    agregarPromocionDinamica
};
