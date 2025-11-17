/**
 * PAGE LOADER - Juled TOYS
 * Muestra animación de carga mientras navega entre páginas
 */

document.addEventListener('DOMContentLoaded', function() {
    
    // Ocultar loader cuando la página termine de cargar
    const loader = document.getElementById('page-loader');
    if (loader) {
        // Pequeño delay para que se vea la animación
        setTimeout(() => {
            loader.classList.add('loaded');
        }, 300);
    }
    
    // Animar puntos suspensivos en "Cargando..."
    animateLoadingDots();
});

/**
 * Muestra el loader cuando se hace clic en enlaces de navegación
 */
function showLoader() {
    const loader = document.getElementById('page-loader');
    if (loader) {
        loader.classList.remove('loaded');
        loader.style.opacity = '1';
        loader.style.visibility = 'visible';
    }
}

/**
 * Intercepta clics en enlaces para mostrar loader
 * Solo aplica a navegación interna (misma página)
 */
function interceptNavigationLinks() {
    // Obtener todos los enlaces de navegación
    const navLinks = document.querySelectorAll('a[href]:not([target="_blank"]):not([href^="#"]):not([href^="mailto:"]):not([href^="tel:"])');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            
            // Ignorar enlaces externos o que abren en nueva pestaña
            if (href && !href.startsWith('http') && !href.startsWith('//')) {
                // Solo mostrar loader si no es un formulario POST
                const isFormLink = this.closest('form');
                if (!isFormLink) {
                    showLoader();
                }
            }
        });
    });
}

/**
 * Anima los puntos suspensivos "..." en el texto de carga
 */
function animateLoadingDots() {
    const dotsElement = document.querySelector('.loader-dots');
    if (!dotsElement) return;
    
    let dots = 0;
    setInterval(() => {
        dots = (dots + 1) % 4;
        dotsElement.textContent = '.'.repeat(dots);
    }, 500);
}

/**
 * Intercepta el botón "atrás" del navegador
 */
window.addEventListener('pageshow', function(event) {
    // Si viene del caché (botón atrás), ocultar loader inmediatamente
    if (event.persisted) {
        const loader = document.getElementById('page-loader');
        if (loader) {
            loader.classList.add('loaded');
        }
    }
});

/**
 * Si la página tarda mucho, ocultar loader automáticamente
 * (fallback de seguridad)
 */
window.addEventListener('load', function() {
    setTimeout(() => {
        const loader = document.getElementById('page-loader');
        if (loader && !loader.classList.contains('loaded')) {
            loader.classList.add('loaded');
        }
    }, 5000); // 5 segundos máximo
});

// Inicializar interceptor cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', interceptNavigationLinks);
} else {
    interceptNavigationLinks();
}

/**
 * Función global para mostrar loader manualmente
 * Uso: window.showPageLoader();
 */
window.showPageLoader = showLoader;

/**
 * Función global para ocultar loader manualmente
 * Uso: window.hidePageLoader();
 */
window.hidePageLoader = function() {
    const loader = document.getElementById('page-loader');
    if (loader) {
        loader.classList.add('loaded');
    }
};
