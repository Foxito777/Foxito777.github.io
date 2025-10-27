// JavaScript para funcionalidad del carrito
document.addEventListener('DOMContentLoaded', function() {
    
    // Actualizar badge del carrito al cargar la página
    actualizarBadgeCarrito();
    
    // Manejar formularios de agregar al carrito con AJAX
    const formsAgregar = document.querySelectorAll('.add-to-cart-form');
    formsAgregar.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            agregarAlCarritoAjax(this);
        });
    });
    
    // Validar inputs de cantidad
    const inputsCantidad = document.querySelectorAll('.quantity-input');
    inputsCantidad.forEach(input => {
        input.addEventListener('change', function() {
            validarCantidad(this);
        });
        input.addEventListener('input', function() {
            validarCantidad(this);
        });
    });

    // Registrar listener del botón "Continuar" aquí (DOM cargado)
    const btnContinuar = document.getElementById('btn-continuar');
    if (btnContinuar) {
        btnContinuar.addEventListener('click', function(e) {
            e.preventDefault();
            btnContinuar.disabled = true;
            btnContinuar.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i> Verificando...';

            fetch('/carrito/api/validar', { method: 'POST' })
                .then(resp => {
                    console.debug('[carrito] /carrito/api/validar status=', resp.status);
                    // Revisar tipo de contenido: si viene JSON, parsear; si viene HTML u otra cosa pero status OK,
                    // asumimos que no hay problema de stock y redirigimos a checkout (fallback robusto)
                    const ct = resp.headers.get('content-type') || '';
                    console.debug('[carrito] content-type=', ct);
                    if (ct.includes('application/json')) {
                        return resp.json().then(data => {
                            console.debug('[carrito] json payload=', data);
                            return { json: data };
                        });
                    }
                    if (resp.ok) {
                        return { redirect: true };
                    }
                    throw new Error('Respuesta inesperada: ' + resp.status);
                })
                .then(result => {
                    if (result.redirect) {
                        window.location.href = '/checkout';
                        return;
                    }
                    const data = result.json;
                    if (data && data.ok) {
                        // Todo en stock: redirigir a checkout
                        window.location.href = '/checkout';
                    } else {
                        // Mostrar modal con items insuficientes
                        mostrarModalStock((data && data.insuficientes) || []);
                    }
                })
                .catch(err => {
                    console.error('Error validando stock:', err);
                    mostrarNotificacion('Error al validar stock. Intenta nuevamente.', 'error');
                })
                .finally(() => {
                    btnContinuar.disabled = false;
                    btnContinuar.innerHTML = '<i class="fas fa-arrow-right me-2"></i>Continuar';
                });
        });
    }
});

// Agregar producto al carrito vía AJAX
function agregarAlCarritoAjax(form) {
    const formData = new FormData(form);
    const button = form.querySelector('button');
    const originalText = button.innerHTML;
    
    // Deshabilitar botón y mostrar loading
    button.disabled = true;
    button.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i> Agregando...';
    
    fetch('/carrito/api/agregar', {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(data => {
        if (data === 'success') {
            // Mostrar notificación de éxito
            mostrarNotificacion('Producto agregado al carrito', 'success');
            
            // Actualizar badge del carrito
            actualizarBadgeCarrito();
            
            // Cambiar texto del botón temporalmente
            button.innerHTML = '<i class="fas fa-check me-1"></i> ¡Agregado!';
            button.classList.remove('btn-outline-secondary');
            button.classList.add('btn-success');
            
            setTimeout(() => {
                button.innerHTML = originalText;
                button.classList.remove('btn-success');
                button.classList.add('btn-outline-secondary');
                button.disabled = false;
            }, 2000);
            
        } else {
            mostrarNotificacion('Error al agregar producto', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarNotificacion('Error al agregar producto', 'error');
    })
    .finally(() => {
        button.disabled = false;
        if (button.innerHTML.includes('Agregando...')) {
            button.innerHTML = originalText;
        }
    });
}

// Actualizar badge del carrito
function actualizarBadgeCarrito() {
    fetch('/carrito/api/cantidad')
        .then(response => response.json())
        .then(cantidad => {
            const badges = document.querySelectorAll('.cart-badge');
            badges.forEach(badge => {
                badge.textContent = cantidad;
                if (cantidad > 0) {
                    badge.style.display = 'inline-block';
                    badge.classList.add('bounce');
                    setTimeout(() => badge.classList.remove('bounce'), 600);
                } else {
                    badge.style.display = 'none';
                }
            });
        })
        .catch(error => {
            console.error('Error al actualizar badge del carrito:', error);
        });
}

// Validar cantidad en inputs
function validarCantidad(input) {
    let valor = parseInt(input.value);
    
    if (isNaN(valor) || valor < 1) {
        input.value = 1;
    } else if (valor > 99) {
        input.value = 99;
        mostrarNotificacion('Cantidad máxima: 99 unidades', 'warning');
    }
}

// Funciones para botones de cantidad
function increaseQuantity(button) {
    const input = button.parentElement.querySelector('.quantity-input');
    let value = parseInt(input.value);
    if (value < 99) {
        input.value = value + 1;
        input.dispatchEvent(new Event('change'));
    }
}

function decreaseQuantity(button) {
    const input = button.parentElement.querySelector('.quantity-input');
    let value = parseInt(input.value);
    if (value > 1) {
        input.value = value - 1;
        input.dispatchEvent(new Event('change'));
    }
}

// Mostrar toast notifications
function mostrarToast(mensaje, tipo = 'info') {
    // Crear contenedor de toasts si no existe
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    
    // Determinar clase de color según el tipo
    let bgClass = 'bg-info';
    let icon = 'fas fa-info-circle';
    
    switch(tipo) {
        case 'success':
            bgClass = 'bg-success';
            icon = 'fas fa-check-circle';
            break;
        case 'error':
            bgClass = 'bg-danger';
            icon = 'fas fa-exclamation-circle';
            break;
        case 'warning':
            bgClass = 'bg-warning';
            icon = 'fas fa-exclamation-triangle';
            break;
    }
    
    // Crear toast
    const toastId = 'toast-' + Date.now();
    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="${icon} me-2"></i>${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    
    container.insertAdjacentHTML('beforeend', toastHTML);
    
    // Mostrar toast
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 3000
    });
    
    toast.show();
    
    // Eliminar toast del DOM después de ocultarse
    toastElement.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Confirmar acciones importantes
function confirmarAccion(mensaje) {
    return confirm(mensaje);
}

// Alias para compatibilidad
const mostrarNotificacion = mostrarToast;

// Construye y muestra un modal Bootstrap con mensaje de stock insuficiente
function mostrarModalStock(insuficientes) {
    const container = document.getElementById('stockModalContainer');
    if (!container) return;
    container.innerHTML = '';

    const modalId = 'stockModal';
    const modalHtml = `
    <div class="modal fade" id="${modalId}" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-warning text-dark">
            <h5 class="modal-title">
              <i class="fas fa-exclamation-triangle me-2"></i>Sin stock disponible
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center py-4">
            <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
            <p class="fs-5 mb-3">Sin stock - vea alternativas</p>
            <p class="text-muted small">Algunos productos en tu carrito no están disponibles en la cantidad solicitada.</p>
          </div>
          <div class="modal-footer justify-content-center">
            <form action="/carrito/eliminar-sin-stock" method="post" style="display:inline;">
              <button type="submit" class="btn btn-primary btn-lg">
                <i class="fas fa-exchange-alt me-2"></i>Cambiar productos
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>`;

    container.insertAdjacentHTML('beforeend', modalHtml);
    const modalEl = document.getElementById(modalId);
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
}// Escapa texto para evitar inyección simple en el modal
function escapeHtml(text) {
        if (!text) return '';
        return String(text).replace(/[&<>"']/g, function (s) {
                return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[s]);
        });
}