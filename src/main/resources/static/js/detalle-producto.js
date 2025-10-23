document.addEventListener('DOMContentLoaded', function() {
    // Cambiar imagen principal al hacer clic en miniatura
    const thumbnails = document.querySelectorAll('.thumbnail-item');
    const mainImage = document.getElementById('mainImage');
    
    thumbnails.forEach(thumbnail => {
        thumbnail.addEventListener('click', function() {
            // Remover active de todos
            thumbnails.forEach(t => t.classList.remove('active'));
            // Agregar active al clickeado
            this.classList.add('active');
            
            // Cambiar imagen principal usando el data-image
            const newSrc = this.getAttribute('data-image');
            if (newSrc) {
                mainImage.src = newSrc;
            }
        });
    });
    
    // Manejar errores de carga de imágenes en miniaturas
    const thumbnailImages = document.querySelectorAll('.thumbnail-item img');
    thumbnailImages.forEach(img => {
        img.addEventListener('error', function() {
            // Si falla cargar la miniatura, usar la imagen principal
            const productId = window.location.pathname.split('/').pop();
            this.src = `/Imagenes/Set/Set${productId}.png`;
        });
    });
    
    // Agregar al carrito con animación mejorada
    const btnAgregar = document.querySelector('.add-to-cart-btn');
    if (btnAgregar) {
        btnAgregar.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const cantidad = document.querySelector('.quantity-input').value;
            console.log(`Agregar ${cantidad} unidades del producto ID: ${id}`);
            
            // Animación del botón
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-check me-2"></i>¡Agregado!';
            this.style.background = '#28a745';
            this.disabled = true;
            
            // Efecto de "volando al carrito"
            const rect = this.getBoundingClientRect();
            const flyingIcon = document.createElement('div');
            flyingIcon.innerHTML = '<i class="fas fa-shopping-cart"></i>';
            flyingIcon.style.cssText = `
                position: fixed;
                left: ${rect.left + rect.width/2}px;
                top: ${rect.top + rect.height/2}px;
                z-index: 9999;
                color: #28a745;
                font-size: 1.5rem;
                pointer-events: none;
                animation: flyToCart 1s ease-out forwards;
            `;
            document.body.appendChild(flyingIcon);
            
            setTimeout(() => {
                flyingIcon.remove();
                this.innerHTML = originalText;
                this.style.background = '#495057';
                this.disabled = false;
            }, 2000);
        });
    }
    
    // Control de cantidad con botones + y -
    const quantityInput = document.querySelector('.quantity-input');
    if (quantityInput) {
        // Crear botones + y -
        const quantityContainer = quantityInput.parentElement;
        quantityContainer.style.display = 'flex';
        quantityContainer.style.alignItems = 'center';
        quantityContainer.style.gap = '0.5rem';
        
        const minusBtn = document.createElement('button');
        minusBtn.innerHTML = '<i class="fas fa-minus"></i>';
        minusBtn.className = 'btn btn-outline-secondary btn-sm quantity-btn';
        minusBtn.type = 'button';
        
        const plusBtn = document.createElement('button');
        plusBtn.innerHTML = '<i class="fas fa-plus"></i>';
        plusBtn.className = 'btn btn-outline-secondary btn-sm quantity-btn';
        plusBtn.type = 'button';
        
        quantityContainer.insertBefore(minusBtn, quantityInput);
        quantityContainer.appendChild(plusBtn);
        
        minusBtn.addEventListener('click', () => {
            const currentValue = parseInt(quantityInput.value);
            if (currentValue > 1) {
                quantityInput.value = currentValue - 1;
            }
        });
        
        plusBtn.addEventListener('click', () => {
            const currentValue = parseInt(quantityInput.value);
            const maxValue = parseInt(quantityInput.getAttribute('max')) || 10;
            if (currentValue < maxValue) {
                quantityInput.value = currentValue + 1;
            }
        });
    }
    
    // Funciones adicionales de navegación
    function compartirProducto() {
        const productName = document.querySelector('.product-main-title').textContent;
        const currentUrl = window.location.href;
        
        if (navigator.share) {
            navigator.share({
                title: productName + ' - Juled TOYS',
                text: 'Mira este increíble producto en Juled TOYS',
                url: currentUrl
            });
        } else {
            // Fallback para navegadores que no soportan Web Share API
            navigator.clipboard.writeText(currentUrl).then(() => {
                alert('¡Enlace copiado al portapapeles!');
            });
        }
    }
    
    function agregarFavoritos() {
        const productId = document.querySelector('.add-to-cart-btn').getAttribute('data-id');
        console.log('Agregar a favoritos producto ID:', productId);
        
        // Cambiar icono temporalmente
        const heartBtns = document.querySelectorAll('[onclick="agregarFavoritos()"] i');
        heartBtns.forEach(btn => {
            btn.classList.remove('fas', 'fa-heart');
            btn.classList.add('fas', 'fa-heart');
            btn.style.color = '#dc3545';
        });
        
        alert('¡Producto agregado a favoritos!');
        
        // TODO: Implementar lógica real de favoritos
    }
    
    function seguirComprando() {
        // Redirigir al catálogo con el filtro de la categoría actual
        const categoria = document.querySelector('.category-badge').textContent;
        window.location.href = `/productos?categoria=${encodeURIComponent(categoria)}`;
    }
    
    // Mejorar la funcionalidad del carrito
    document.addEventListener('DOMContentLoaded', function() {
        // Actualizar contador del carrito en navbar
        function actualizarContadorCarrito() {
            const badge = document.querySelector('.badge-cart');
            if (badge) {
                let currentCount = parseInt(badge.textContent) || 0;
                badge.textContent = currentCount + 1;
                
                // Animación del badge
                badge.style.transform = 'scale(1.3)';
                setTimeout(() => {
                    badge.style.transform = 'scale(1)';
                }, 200);
            }
        }
        
        // Modificar el evento del botón agregar al carrito
        const btnAgregar = document.querySelector('.add-to-cart-btn');
        if (btnAgregar) {
            btnAgregar.addEventListener('click', function() {
                // Actualizar contador
                actualizarContadorCarrito();
                
                // Mostrar toast de confirmación
                mostrarToast('¡Producto agregado al carrito!', 'success');
            });
        }
    });
    
    // Función para mostrar toasts
    function mostrarToast(mensaje, tipo = 'info') {
        // Crear toast si no existe
        if (!document.querySelector('.toast-container')) {
            const toastContainer = document.createElement('div');
            toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
            toastContainer.style.zIndex = '9999';
            document.body.appendChild(toastContainer);
        }
        
        const toastHtml = `
            <div class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <i class="fas fa-check-circle text-success me-2"></i>
                    <strong class="me-auto">Juled TOYS</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
                </div>
                <div class="toast-body">
                    ${mensaje}
                </div>
            </div>
        `;
        
        const toastContainer = document.querySelector('.toast-container');
        toastContainer.insertAdjacentHTML('beforeend', toastHtml);
        
        const toast = new bootstrap.Toast(toastContainer.lastElementChild);
        toast.show();
        
        // Remover toast después de que se oculte
        toastContainer.lastElementChild.addEventListener('hidden.bs.toast', function() {
            this.remove();
        });
    }
});
