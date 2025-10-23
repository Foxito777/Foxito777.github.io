// ==================== TIENDA DEL FUTURO - JAVASCRIPT ==================== 

class TiendaVirtual {
    constructor() {
        this.productosCache = [];
        this.filtrosActivos = {
            categoria: '',
            precio: '',
            busqueda: '',
            ordenar: 'relevancia'
        };
        this.vistaActual = 'grid';
        this.paginaActual = 1;
        this.productosPorPagina = 12;
        this.productoModalActual = null;
        this.wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
        
        this.init();
    }

    init() {
        this.initAOS();
        this.bindEvents();
        this.cargarProductosIniciales();
        this.initSearchSuggestions();
        this.updateStats();
    }

    // ==================== INICIALIZACIÓN ==================== 
    initAOS() {
        if (typeof AOS !== 'undefined') {
            AOS.init({
                duration: 800,
                once: true,
                offset: 100
            });
        }
    }

    bindEvents() {
        // Filtros
        document.getElementById('searchInput')?.addEventListener('input', 
            this.debounce(this.handleSearch.bind(this), 300));
        document.getElementById('categoriaFilter')?.addEventListener('change', this.handleCategoryFilter.bind(this));
        document.getElementById('precioFilter')?.addEventListener('change', this.handlePriceFilter.bind(this));
        document.getElementById('ordenarSelect')?.addEventListener('change', this.handleSort.bind(this));
        document.getElementById('clearFilters')?.addEventListener('click', this.clearFilters.bind(this));

        // Vista
        document.getElementById('vistaGrid')?.addEventListener('click', () => this.cambiarVista('grid'));
        document.getElementById('vistaLista')?.addEventListener('click', () => this.cambiarVista('lista'));

        // Eventos globales
        document.addEventListener('click', this.handleGlobalClick.bind(this));
        document.addEventListener('keydown', this.handleKeyboard.bind(this));

        // Hero scroll
        window.scrollToProductos = () => {
            document.getElementById('productosSection')?.scrollIntoView({ 
                behavior: 'smooth' 
            });
        };
    }

    // ==================== CARGA DE PRODUCTOS ==================== 
    async cargarProductosIniciales() {
        this.showLoading();
        try {
            const response = await fetch('/api/productos/filtrar?' + new URLSearchParams(this.filtrosActivos));
            const data = await response.json();
            this.productosCache = data.productos || [];
            this.renderProductos();
            this.renderPaginacion(data.totalPaginas || 1);
            this.updateStats();
        } catch (error) {
            console.error('Error cargando productos:', error);
            this.showError();
        } finally {
            this.hideLoading();
        }
    }

    async filtrarProductos() {
        this.showLoading();
        try {
            const params = new URLSearchParams({
                ...this.filtrosActivos,
                pagina: this.paginaActual
            });
            
            const response = await fetch('/api/productos/filtrar?' + params);
            const data = await response.json();
            
            this.productosCache = data.productos || [];
            this.renderProductos();
            this.renderPaginacion(data.totalPaginas || 1);
            this.updateStats();
            
            // Scroll suave a los productos si no están visibles
            if (window.scrollY > document.getElementById('productosSection').offsetTop - 200) {
                document.getElementById('productosSection').scrollIntoView({ 
                    behavior: 'smooth' 
                });
            }
        } catch (error) {
            console.error('Error filtrando productos:', error);
            this.showError();
        } finally {
            this.hideLoading();
        }
    }

    // ==================== MANEJO DE FILTROS ==================== 
    handleSearch(event) {
        this.filtrosActivos.busqueda = event.target.value;
        this.paginaActual = 1;
        this.filtrarProductos();
        this.updateSearchSuggestions(event.target.value);
    }

    handleCategoryFilter(event) {
        this.filtrosActivos.categoria = event.target.value;
        this.paginaActual = 1;
        this.filtrarProductos();
    }

    handlePriceFilter(event) {
        this.filtrosActivos.precio = event.target.value;
        this.paginaActual = 1;
        this.filtrarProductos();
    }

    handleSort(event) {
        this.filtrosActivos.ordenar = event.target.value;
        this.paginaActual = 1;
        this.filtrarProductos();
    }

    clearFilters() {
        // Limpiar filtros
        this.filtrosActivos = {
            categoria: '',
            precio: '',
            busqueda: '',
            ordenar: 'relevancia'
        };
        this.paginaActual = 1;

        // Limpiar UI
        document.getElementById('searchInput').value = '';
        document.getElementById('categoriaFilter').value = '';
        document.getElementById('precioFilter').value = '';
        document.getElementById('ordenarSelect').value = 'relevancia';

        // Recargar productos
        this.filtrarProductos();
    }

    // ==================== BÚSQUEDA INTELIGENTE ==================== 
    initSearchSuggestions() {
        const suggestions = [
            'LEGO', 'Star Wars', 'Marvel', 'DC Comics', 'Ferrari',
            'Mansion', 'Caza estelar', 'Figuras', 'Vehículos',
            'Construcción', 'Coleccionables', 'Anime'
        ];
        this.suggestions = suggestions;
    }

    updateSearchSuggestions(query) {
        const suggestionsContainer = document.getElementById('searchSuggestions');
        if (!suggestionsContainer || !query) {
            suggestionsContainer.style.display = 'none';
            return;
        }

        const filteredSuggestions = this.suggestions.filter(suggestion =>
            suggestion.toLowerCase().includes(query.toLowerCase())
        ).slice(0, 5);

        if (filteredSuggestions.length === 0) {
            suggestionsContainer.style.display = 'none';
            return;
        }

        suggestionsContainer.innerHTML = filteredSuggestions
            .map(suggestion => `
                <div class="suggestion-item" data-suggestion="${suggestion}">
                    <i class="fas fa-search me-2"></i>${suggestion}
                </div>
            `).join('');

        suggestionsContainer.style.display = 'block';

        // Bind click events
        suggestionsContainer.querySelectorAll('.suggestion-item').forEach(item => {
            item.addEventListener('click', () => {
                document.getElementById('searchInput').value = item.dataset.suggestion;
                this.filtrosActivos.busqueda = item.dataset.suggestion;
                this.filtrarProductos();
                suggestionsContainer.style.display = 'none';
            });
        });
    }

    // ==================== RENDERIZADO ==================== 
    renderProductos() {
        const container = document.getElementById('productosGrid');
        const noProductos = document.getElementById('noProductos');

        if (!this.productosCache || this.productosCache.length === 0) {
            container.innerHTML = '';
            noProductos.style.display = 'block';
            return;
        }

        noProductos.style.display = 'none';
        
        if (this.vistaActual === 'grid') {
            container.className = 'productos-grid';
            container.innerHTML = this.productosCache.map(producto => this.renderProductoCard(producto)).join('');
        } else {
            container.className = 'productos-lista';
            container.innerHTML = this.productosCache.map(producto => this.renderProductoLista(producto)).join('');
        }

        // Bind eventos de productos
        this.bindProductEvents();
    }

    renderProductoCard(producto) {
        const descuento = producto.descuento || 0;
        const nuevo = producto.nuevo || false;
        const rating = this.generateStars(producto.calificacion || 4.5);
        const stock = this.getStockStatus(producto.stock || 0);
        const enWishlist = this.wishlist.includes(producto.id);

        return `
            <div class="producto-card" data-product-id="${producto.id}" data-aos="fade-up">
                <div class="producto-imagen">
                    <img src="${producto.imagen}" alt="${producto.nombre}" loading="lazy">
                    
                    <div class="producto-badges">
                        ${nuevo ? '<span class="badge-nuevo">NUEVO</span>' : ''}
                        ${descuento > 0 ? `<span class="badge-descuento">${descuento}% OFF</span>` : ''}
                    </div>

                    <div class="acciones-rapidas">
                        <button class="accion-btn" title="Vista rápida" onclick="tienda.mostrarVistaRapida(${producto.id})">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="accion-btn ${enWishlist ? 'text-danger' : ''}" title="Favoritos" 
                                onclick="tienda.toggleWishlist(${producto.id})">
                            <i class="fa${enWishlist ? 's' : 'r'} fa-heart"></i>
                        </button>
                        <button class="accion-btn" title="Comparar" onclick="tienda.compararProducto(${producto.id})">
                            <i class="fas fa-balance-scale"></i>
                        </button>
                    </div>
                </div>

                <div class="producto-info">
                    <div class="producto-categoria">${producto.categoria}</div>
                    <h3 class="producto-nombre">${producto.nombre}</h3>
                    
                    <div class="producto-rating">
                        <div class="rating-stars">${rating}</div>
                        <span class="rating-text">(${producto.vendidos || 0})</span>
                    </div>

                    <div class="precio-container">
                        <span class="precio-actual">S/ ${producto.precio.toFixed(2)}</span>
                        ${producto.precioAnterior ? 
                            `<span class="precio-anterior">S/ ${producto.precioAnterior.toFixed(2)}</span>` : ''}
                    </div>

                    <div class="stock-info">
                        <span class="${stock.class}">${stock.text}</span>
                    </div>

                    <div class="acciones-producto">
                        <button class="btn btn-primary btn-agregar-carrito" 
                                onclick="tienda.agregarAlCarrito(${producto.id})"
                                ${producto.stock === 0 ? 'disabled' : ''}>
                            <i class="fas fa-cart-plus me-2"></i>
                            ${producto.stock === 0 ? 'Agotado' : 'Agregar'}
                        </button>
                        <button class="btn btn-vista-rapida" onclick="tienda.mostrarVistaRapida(${producto.id})">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    renderProductoLista(producto) {
        const descuento = producto.descuento || 0;
        const rating = this.generateStars(producto.calificacion || 4.5);
        const stock = this.getStockStatus(producto.stock || 0);

        return `
            <div class="producto-lista-item" data-product-id="${producto.id}" data-aos="fade-right">
                <div class="producto-lista-imagen">
                    <img src="${producto.imagen}" alt="${producto.nombre}" loading="lazy">
                </div>
                
                <div class="producto-lista-info">
                    <div>
                        <div class="producto-categoria">${producto.categoria}</div>
                        <h3 class="producto-nombre">${producto.nombre}</h3>
                        
                        <div class="producto-rating mb-2">
                            <div class="rating-stars">${rating}</div>
                            <span class="rating-text">(${producto.vendidos || 0} vendidos)</span>
                        </div>

                        <p class="text-muted mb-3">
                            ${this.generarDescripcionCorta(producto)}
                        </p>
                    </div>

                    <div>
                        <div class="precio-container mb-2">
                            <span class="precio-actual">S/ ${producto.precio.toFixed(2)}</span>
                            ${producto.precioAnterior ? 
                                `<span class="precio-anterior">S/ ${producto.precioAnterior.toFixed(2)}</span>` : ''}
                            ${descuento > 0 ? `<span class="badge-descuento">${descuento}% OFF</span>` : ''}
                        </div>

                        <div class="stock-info mb-3">
                            <span class="${stock.class}">${stock.text}</span>
                        </div>

                        <div class="acciones-producto">
                            <button class="btn btn-primary btn-agregar-carrito me-2" 
                                    onclick="tienda.agregarAlCarrito(${producto.id})"
                                    ${producto.stock === 0 ? 'disabled' : ''}>
                                <i class="fas fa-cart-plus me-2"></i>
                                ${producto.stock === 0 ? 'Agotado' : 'Agregar al Carrito'}
                            </button>
                            <button class="btn btn-outline-primary" onclick="tienda.mostrarVistaRapida(${producto.id})">
                                <i class="fas fa-eye me-2"></i>Vista Rápida
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    renderPaginacion(totalPaginas) {
        const container = document.getElementById('paginacion');
        if (!container || totalPaginas <= 1) {
            container.innerHTML = '';
            return;
        }

        let pagination = '<ul class="pagination">';
        
        // Botón anterior
        pagination += `
            <li class="page-item ${this.paginaActual === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="tienda.irAPagina(${this.paginaActual - 1})">
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // Páginas
        for (let i = 1; i <= totalPaginas; i++) {
            if (i === 1 || i === totalPaginas || (i >= this.paginaActual - 2 && i <= this.paginaActual + 2)) {
                pagination += `
                    <li class="page-item ${i === this.paginaActual ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="tienda.irAPagina(${i})">${i}</a>
                    </li>
                `;
            } else if (i === this.paginaActual - 3 || i === this.paginaActual + 3) {
                pagination += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }

        // Botón siguiente
        pagination += `
            <li class="page-item ${this.paginaActual === totalPaginas ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="tienda.irAPagina(${this.paginaActual + 1})">
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `;

        pagination += '</ul>';
        container.innerHTML = pagination;
    }

    // ==================== VISTA ==================== 
    cambiarVista(vista) {
        this.vistaActual = vista;
        
        // Actualizar botones
        document.getElementById('vistaGrid').classList.toggle('active', vista === 'grid');
        document.getElementById('vistaLista').classList.toggle('active', vista === 'lista');
        
        // Re-renderizar productos
        this.renderProductos();
    }

    irAPagina(pagina) {
        const totalPaginas = Math.ceil(this.productosCache.length / this.productosPorPagina);
        if (pagina < 1 || pagina > totalPaginas) return;
        
        this.paginaActual = pagina;
        this.filtrarProductos();
    }

    // ==================== VISTA RÁPIDA ==================== 
    async mostrarVistaRapida(productoId) {
        try {
            const response = await fetch(`/api/producto/detalle?id=${productoId}`);
            const producto = await response.json();
            
            if (!producto.id) {
                console.error('Producto no encontrado');
                return;
            }

            this.productoModalActual = producto;
            this.populateVistaRapidaModal(producto);
            
            const modal = new bootstrap.Modal(document.getElementById('vistaRapidaModal'));
            modal.show();
        } catch (error) {
            console.error('Error cargando detalles del producto:', error);
        }
    }

    populateVistaRapidaModal(producto) {
        document.getElementById('modalProductoImagen').src = producto.imagen;
        document.getElementById('modalProductoImagen').alt = producto.nombre;
        document.getElementById('modalProductoNombre').textContent = producto.nombre;
        document.getElementById('modalProductoCalificacion').innerHTML = this.generateStars(producto.calificacion || 4.5);
        document.getElementById('modalProductoPrecio').textContent = `S/ ${producto.precio.toFixed(2)}`;
        
        const precioAnterior = document.getElementById('modalProductoPrecioAnterior');
        if (producto.precioAnterior) {
            precioAnterior.textContent = `S/ ${producto.precioAnterior.toFixed(2)}`;
            precioAnterior.style.display = 'inline';
        } else {
            precioAnterior.style.display = 'none';
        }

        const descuentoBadge = document.getElementById('modalProductoDescuento');
        if (producto.descuento) {
            descuentoBadge.textContent = `${producto.descuento}% OFF`;
            descuentoBadge.style.display = 'inline';
        } else {
            descuentoBadge.style.display = 'none';
        }

        document.getElementById('modalProductoDescripcion').textContent = this.generarDescripcionCorta(producto);
        
        const stock = this.getStockStatus(producto.stock || 0);
        document.getElementById('modalProductoStock').innerHTML = `<span class="${stock.class}">${stock.text}</span>`;
        
        document.getElementById('cantidadModal').value = 1;
        document.getElementById('cantidadModal').max = producto.stock || 1;
    }

    // ==================== CARRITO ==================== 
    async agregarAlCarrito(productoId, cantidad = 1) {
        try {
            const response = await fetch('/carrito/agregar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `productoId=${productoId}&cantidad=${cantidad}`
            });

            if (response.ok) {
                this.mostrarNotificacion('Producto agregado al carrito', 'success');
                this.actualizarContadorCarrito();
                this.animarAgregarCarrito(productoId);
            } else {
                this.mostrarNotificacion('Error al agregar producto', 'error');
            }
        } catch (error) {
            console.error('Error agregando al carrito:', error);
            this.mostrarNotificacion('Error al agregar producto', 'error');
        }
    }

    agregarAlCarritoModal() {
        if (!this.productoModalActual) return;
        
        const cantidad = parseInt(document.getElementById('cantidadModal').value);
        this.agregarAlCarrito(this.productoModalActual.id, cantidad);
        
        // Cerrar modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('vistaRapidaModal'));
        modal.hide();
    }

    cambiarCantidad(delta) {
        const input = document.getElementById('cantidadModal');
        const newValue = parseInt(input.value) + delta;
        const max = parseInt(input.max);
        
        if (newValue >= 1 && newValue <= max) {
            input.value = newValue;
        }
    }

    async actualizarContadorCarrito() {
        try {
            const response = await fetch('/carrito/count');
            const count = await response.json();
            
            const badge = document.querySelector('.navbar .badge');
            if (badge) {
                badge.textContent = count;
                badge.style.display = count > 0 ? 'inline' : 'none';
            }
        } catch (error) {
            console.error('Error actualizando contador del carrito:', error);
        }
    }

    // ==================== WISHLIST ==================== 
    toggleWishlist(productoId) {
        const index = this.wishlist.indexOf(productoId);
        
        if (index > -1) {
            this.wishlist.splice(index, 1);
            this.mostrarNotificacion('Eliminado de favoritos', 'info');
        } else {
            this.wishlist.push(productoId);
            this.mostrarNotificacion('Agregado a favoritos', 'success');
        }
        
        localStorage.setItem('wishlist', JSON.stringify(this.wishlist));
        this.renderProductos(); // Re-renderizar para actualizar iconos
    }

    // ==================== UTILIDADES ==================== 
    generateStars(rating) {
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 !== 0;
        const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        
        let stars = '';
        for (let i = 0; i < fullStars; i++) {
            stars += '<i class="fas fa-star"></i>';
        }
        if (hasHalfStar) {
            stars += '<i class="fas fa-star-half-alt"></i>';
        }
        for (let i = 0; i < emptyStars; i++) {
            stars += '<i class="far fa-star"></i>';
        }
        
        return stars;
    }

    getStockStatus(stock) {
        if (stock === 0) {
            return { class: 'stock-agotado', text: 'Agotado' };
        } else if (stock <= 5) {
            return { class: 'stock-limitado', text: `Solo ${stock} disponibles` };
        } else {
            return { class: 'stock-disponible', text: 'Disponible' };
        }
    }

    generarDescripcionCorta(producto) {
        const descripciones = {
            'LEGO': 'Set de construcción LEGO original con piezas de alta calidad y manual de instrucciones detallado.',
            'Star Wars': 'Réplica oficial de Star Wars con detalles auténticos de la saga galáctica.',
            'Marvel': 'Figura coleccionable de Marvel con articulaciones y accesorios incluidos.',
            'Vehículos': 'Modelo detallado con características realistas y acabados premium.',
            'default': 'Producto de alta calidad con garantía y envío gratis en compras mayores a S/149.'
        };
        
        return descripciones[producto.categoria] || descripciones.default;
    }

    updateStats() {
        const stats = {
            total: this.productosCache.length,
            populares: this.productosCache.filter(p => p.vendidos > 100).length,
            ofertas: this.productosCache.filter(p => p.descuento > 0).length
        };

        this.animateCounter('totalProductos', stats.total);
        this.animateCounter('productosPopulares', stats.populares);
        this.animateCounter('ofertas', stats.ofertas);
    }

    animateCounter(elementId, target) {
        const element = document.getElementById(elementId);
        if (!element) return;

        let current = 0;
        const increment = target / 30;
        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }
            element.textContent = Math.floor(current);
        }, 50);
    }

    // ==================== EVENTOS ==================== 
    bindProductEvents() {
        // Eventos ya están en el HTML como onclick
    }

    handleGlobalClick(event) {
        // Cerrar sugerencias si se hace clic fuera
        if (!event.target.closest('.search-container')) {
            document.getElementById('searchSuggestions').style.display = 'none';
        }
    }

    handleKeyboard(event) {
        // ESC para cerrar modales
        if (event.key === 'Escape') {
            document.getElementById('searchSuggestions').style.display = 'none';
        }
    }

    // ==================== NOTIFICACIONES ==================== 
    mostrarNotificacion(mensaje, tipo = 'info') {
        // Crear elemento de notificación
        const notification = document.createElement('div');
        notification.className = `alert alert-${tipo === 'error' ? 'danger' : tipo} alert-dismissible fade show position-fixed`;
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        notification.innerHTML = `
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(notification);

        // Auto-remover después de 3 segundos
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 3000);
    }

    animarAgregarCarrito(productoId) {
        const productCard = document.querySelector(`[data-product-id="${productoId}"]`);
        if (productCard) {
            productCard.style.transform = 'scale(0.95)';
            setTimeout(() => {
                productCard.style.transform = 'scale(1)';
            }, 200);
        }
    }

    // ==================== LOADING ==================== 
    showLoading() {
        document.getElementById('loadingSpinner').style.display = 'block';
        document.getElementById('productosGrid').style.opacity = '0.5';
    }

    hideLoading() {
        document.getElementById('loadingSpinner').style.display = 'none';
        document.getElementById('productosGrid').style.opacity = '1';
    }

    showError() {
        document.getElementById('productosGrid').innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                <h4>Error al cargar productos</h4>
                <p class="text-muted">Por favor, intenta nuevamente</p>
                <button class="btn btn-primary" onclick="tienda.cargarProductosIniciales()">
                    <i class="fas fa-redo me-2"></i>Reintentar
                </button>
            </div>
        `;
    }

    // ==================== DEBOUNCE ==================== 
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // ==================== MÉTODOS ADICIONALES ==================== 
    compararProducto(productoId) {
        this.mostrarNotificacion('Función de comparación próximamente', 'info');
    }

    rotarProducto() {
        const imagen = document.getElementById('modalProductoImagen');
        imagen.style.transform = imagen.style.transform === 'rotateY(180deg)' ? 'rotateY(0deg)' : 'rotateY(180deg)';
    }
}

// ==================== FUNCIONES GLOBALES ==================== 
window.rotarProducto = function() {
    window.tienda?.rotarProducto();
};

window.cambiarCantidad = function(delta) {
    window.tienda?.cambiarCantidad(delta);
};

window.agregarAlCarritoModal = function() {
    window.tienda?.agregarAlCarritoModal();
};

window.toggleWishlist = function(id) {
    window.tienda?.toggleWishlist(id);
};

// ==================== INICIALIZACIÓN ==================== 
document.addEventListener('DOMContentLoaded', function() {
    // Incluir AOS si no está disponible
    if (typeof AOS === 'undefined') {
        const aosScript = document.createElement('script');
        aosScript.src = 'https://unpkg.com/aos@2.3.1/dist/aos.js';
        aosScript.onload = () => {
            const aosCSS = document.createElement('link');
            aosCSS.rel = 'stylesheet';
            aosCSS.href = 'https://unpkg.com/aos@2.3.1/dist/aos.css';
            document.head.appendChild(aosCSS);
        };
        document.head.appendChild(aosScript);
    }

    // Inicializar tienda
    window.tienda = new TiendaVirtual();
    
    console.log('🚀 Tienda del Futuro inicializada correctamente');
});