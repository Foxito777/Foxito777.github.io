/**
 * Slider Principal - Funcionalidad avanzada
 * Juled Toys E-commerce
 */

document.addEventListener('DOMContentLoaded', function() {
    initMainSlider();
});

function initMainSlider() {
    const mainSlider = document.getElementById('mainSlider');
    
    if (!mainSlider) {
        console.error('❌ No se encontró el elemento mainSlider');
        return;
    }

    // Configuración del carousel
    const carousel = new bootstrap.Carousel(mainSlider, {
        interval: 5000,        // 5 segundos por slide
        ride: 'carousel',      // Auto-play
        pause: 'hover',        // Pausar al hacer hover
        wrap: true,            // Loop infinito
        keyboard: true,        // Navegación por teclado
        touch: true            // Soporte touch en móviles
    });

    // Precargar imágenes para mejor rendimiento
    preloadSliderImages(mainSlider);

    // Agregar eventos personalizados
    addSliderEvents(mainSlider, carousel);

    // Inicializar controles tactiles para móviles
    initTouchControls(mainSlider, carousel);

    // Indicador de progreso (opcional)
    initProgressIndicator(mainSlider);

    console.log('✅ Slider principal inicializado correctamente');
}

function preloadSliderImages(slider) {
    const slides = slider.querySelectorAll('.carousel-item');
    const imagesToPreload = [];

    slides.forEach(slide => {
        // Precargar imágenes de fondo
        const bgElement = slide.querySelector('[style*="background"]');
        if (bgElement) {
            const bgStyle = bgElement.style.backgroundImage;
            const imageUrl = bgStyle.match(/url\(['"]?([^'"]*)['"]?\)/);
            if (imageUrl && imageUrl[1]) {
                imagesToPreload.push(imageUrl[1]);
            }
        }

        // Precargar imágenes de productos
        const productImages = slide.querySelectorAll('img');
        productImages.forEach(img => {
            if (img.src) {
                imagesToPreload.push(img.src);
            }
        });
    });

    // Precargar todas las imágenes
    imagesToPreload.forEach(src => {
        const img = new Image();
        img.src = src;
    });

    console.log(`📸 Precargadas ${imagesToPreload.length} imágenes del slider`);
}

function addSliderEvents(slider, carousel) {
    // Evento al cambiar de slide
    slider.addEventListener('slide.bs.carousel', function(event) {
        const activeItem = event.target.querySelector('.carousel-item.active');
        const nextItem = event.relatedTarget;
        
        // Aplicar transiciones suaves
        if (activeItem) {
            activeItem.style.transition = 'transform 0.8s ease-in-out';
        }
        if (nextItem) {
            nextItem.style.transition = 'transform 0.8s ease-in-out';
        }

        // Actualizar indicadores personalizados
        updateCustomIndicators(event.to);
    });

    // Evento cuando el slide ha cambiado completamente
    slider.addEventListener('slid.bs.carousel', function(event) {
        // Reiniciar animaciones CSS
        resetSlideAnimations(event.relatedTarget);
    });

    // Pausar en hover y reanudar al salir
    slider.addEventListener('mouseenter', function() {
        carousel.pause();
    });

    slider.addEventListener('mouseleave', function() {
        carousel.cycle();
    });

    // Control por teclado mejorado
    document.addEventListener('keydown', function(event) {
        if (isSliderVisible(slider)) {
            switch(event.key) {
                case 'ArrowLeft':
                    event.preventDefault();
                    carousel.prev();
                    break;
                case 'ArrowRight':
                    event.preventDefault();
                    carousel.next();
                    break;
                case ' ': // Espacio para pausar/reanudar
                    event.preventDefault();
                    if (slider.classList.contains('paused')) {
                        carousel.cycle();
                        slider.classList.remove('paused');
                    } else {
                        carousel.pause();
                        slider.classList.add('paused');
                    }
                    break;
            }
        }
    });
}

function initTouchControls(slider, carousel) {
    let startX = 0;
    let startY = 0;
    let threshold = 50; // Mínimo de pixels para activar swipe

    slider.addEventListener('touchstart', function(e) {
        startX = e.touches[0].clientX;
        startY = e.touches[0].clientY;
    }, { passive: true });

    slider.addEventListener('touchend', function(e) {
        if (!startX || !startY) return;

        let endX = e.changedTouches[0].clientX;
        let endY = e.changedTouches[0].clientY;

        let diffX = startX - endX;
        let diffY = startY - endY;

        // Solo procesar si el movimiento horizontal es mayor que el vertical
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > threshold) {
                if (diffX > 0) {
                    // Swipe izquierda - siguiente slide
                    carousel.next();
                } else {
                    // Swipe derecha - slide anterior
                    carousel.prev();
                }
            }
        }

        // Resetear valores
        startX = 0;
        startY = 0;
    }, { passive: true });
}

function initProgressIndicator(slider) {
    // Crear barra de progreso (opcional)
    const progressBar = document.createElement('div');
    progressBar.className = 'slider-progress-bar';
    progressBar.innerHTML = '<div class="slider-progress-fill"></div>';
    
    // Estilos CSS para la barra de progreso
    const style = document.createElement('style');
    style.textContent = `
        .slider-progress-bar {
            position: absolute;
            bottom: 0;
            left: 0;
            width: 100%;
            height: 4px;
            background: rgba(255, 255, 255, 0.3);
            z-index: 10;
        }
        .slider-progress-fill {
            height: 100%;
            background: #ff9800;
            width: 0%;
            transition: width 0.1s ease;
        }
    `;
    document.head.appendChild(style);
    slider.appendChild(progressBar);

    const progressFill = progressBar.querySelector('.slider-progress-fill');
    let progressInterval;

    function startProgress() {
        let progress = 0;
        progressFill.style.width = '0%';
        
        progressInterval = setInterval(() => {
            progress += 2; // 2% cada 100ms = 5 segundos total
            progressFill.style.width = progress + '%';
            
            if (progress >= 100) {
                clearInterval(progressInterval);
            }
        }, 100);
    }

    function resetProgress() {
        clearInterval(progressInterval);
        progressFill.style.width = '0%';
    }

    // Iniciar progreso
    startProgress();

    // Eventos para manejar el progreso
    slider.addEventListener('slide.bs.carousel', resetProgress);
    slider.addEventListener('slid.bs.carousel', startProgress);
    
    slider.addEventListener('mouseenter', () => {
        clearInterval(progressInterval);
    });
    
    slider.addEventListener('mouseleave', startProgress);
}

function updateCustomIndicators(slideIndex) {
    // Actualizar indicadores personalizados si los hay
    const indicators = document.querySelectorAll('#mainSlider .carousel-indicators button');
    indicators.forEach((indicator, index) => {
        if (index === slideIndex) {
            indicator.classList.add('active');
            indicator.setAttribute('aria-current', 'true');
        } else {
            indicator.classList.remove('active');
            indicator.removeAttribute('aria-current');
        }
    });
}

function resetSlideAnimations(slide) {
    // Remover y re-agregar clases de animación para reiniciarlas
    const animatedElements = slide.querySelectorAll('h1, p, .btn, img');
    animatedElements.forEach(element => {
        element.style.animation = 'none';
        element.offsetHeight; // Trigger reflow
        element.style.animation = null;
    });
}

function isSliderVisible(slider) {
    const rect = slider.getBoundingClientRect();
    return rect.top < window.innerHeight && rect.bottom > 0;
}

// Función para agregar un nuevo slide dinámicamente (si es necesario)
function addSlide(imageUrl, title, description, buttonText, buttonLink) {
    const slider = document.getElementById('mainSlider');
    const carouselInner = slider.querySelector('.carousel-inner');
    const indicators = slider.querySelector('.carousel-indicators');
    
    const slideIndex = carouselInner.children.length;
    
    // Crear nuevo slide
    const newSlide = document.createElement('div');
    newSlide.className = 'carousel-item';
    newSlide.innerHTML = `
        <div class="d-flex align-items-center justify-content-center w-100"
             style="min-height: 420px; background: url('${imageUrl}') center center / cover no-repeat; position: relative;">
            <div style="background:rgba(0,0,0,0.4);position:absolute;inset:0;z-index:1;"></div>
            <div class="container position-relative" style="z-index:2;">
                <div class="row align-items-center justify-content-center">
                    <div class="col-lg-8 text-center">
                        <h1 class="fw-bold display-5 mb-3 text-white" style="font-family: 'Lilita One', cursive;">
                            ${title}
                        </h1>
                        <p class="lead mb-4 text-white">${description}</p>
                        <a href="${buttonLink}" class="btn btn-warning btn-lg rounded-pill px-4 py-2">
                            ${buttonText}
                        </a>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Crear nuevo indicador
    const newIndicator = document.createElement('button');
    newIndicator.type = 'button';
    newIndicator.setAttribute('data-bs-target', '#mainSlider');
    newIndicator.setAttribute('data-bs-slide-to', slideIndex);
    newIndicator.setAttribute('aria-label', `Slide ${slideIndex + 1}`);
    
    // Agregar elementos
    carouselInner.appendChild(newSlide);
    indicators.appendChild(newIndicator);
    
    console.log(`✅ Nuevo slide agregado: ${title}`);
}

// Exportar funciones para uso global si es necesario
window.sliderUtils = {
    addSlide,
    initMainSlider
};