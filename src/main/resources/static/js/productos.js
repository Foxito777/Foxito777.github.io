// --- Lógica accesorios (simulado) ---
const productosAccesorios = [
    {
        id: 1,
        nombre: "Caza Estelar",
        tema: "Vehículos",
        piezas: 120,
        precio_actual: 299,
        precio_original: 39.99,
        descuento: 25,
        imagen_principal: "Imagenes/productos/Caza.png",
        stock: 10,
        descripcion: "Un caza estelar con diseño futurista y 120 piezas.",
    },
    {
        id: 2,
        nombre: "Auto Ferrari",
        tema: "Vehículos",
        piezas: 60,
        precio_actual: 100,
        precio_original: 19.99,
        descuento: 0,
        imagen_principal: "Imagenes/productos/Ferrari.png",
        stock: 0,
         descripcion: "Un auto Ferrari rojo de colección con 60 piezas."
    },
    {
        id: 3,
        nombre: "Mansión Malfoy",
        tema: "Construcción",
        piezas: 20,
        precio_actual: 185,
        precio_original: 21.99,
        descuento: 32,
        imagen_principal: "Imagenes/productos/Mansion.png",
        stock: 5,
        descripcion: "Una mansión inspirada en la casa de los Malfoy con 20 piezas."
    },
    {
        id: 4,
        nombre: "Super Granja Minecraft",
        tema: "Construcción",
        piezas: null,
        precio_actual: 225,
        precio_original: null,
        descuento: 0,
        imagen_principal: "Imagenes/productos/super-granja.png",
        stock: 12,
        descripcion: "Una granja temática de Minecraft con 225 piezas."
    }
];

// Únicos temas
function getAccesoriosTemas() {
    return [...new Set(productosAccesorios.map(p => p.tema).filter(Boolean))];
}

// Renderiza radios de tema si existe la sección accesorios
if (document.getElementById('temaRadios')) {
    const temas = getAccesoriosTemas();
    const temaRadios = document.getElementById('temaRadios');
    temas.forEach((tema, idx) => {
        const id = "tema" + (idx + 1);
        const div = document.createElement('div');
        div.className = "form-check";
        div.innerHTML = `
            <input class="form-check-input" type="radio" name="tema" value="${tema}" id="${id}">
            <label class="form-check-label" for="${id}">${tema}</label>
        `;
        temaRadios.appendChild(div);
    });
}

// Filtros accesorios
function filtrarProductosAccesorios() {
    if (!document.getElementById('productos')) return;
    const stock = document.getElementById('stockFilter')?.checked;
    const precioRadio = document.querySelector('input[name="precio"]:checked');
    const precio = precioRadio ? precioRadio.value : "";
    const temaRadio = document.querySelector('input[name="tema"]:checked');
    const tema = temaRadio ? temaRadio.value : "";

    let filtrados = productosAccesorios.slice();

    if (stock) filtrados = filtrados.filter(p => p.stock > 0);

    if (precio) {
        if (precio === '0-20') filtrados = filtrados.filter(p => p.precio_actual >= 0 && p.precio_actual <= 20);
        else if (precio === '20-50') filtrados = filtrados.filter(p => p.precio_actual > 20 && p.precio_actual <= 50);
        else if (precio === '50-100') filtrados = filtrados.filter(p => p.precio_actual > 50 && p.precio_actual <= 100);
        else if (precio === '100-200') filtrados = filtrados.filter(p => p.precio_actual > 100 && p.precio_actual <= 200);
        else if (precio === '200+') filtrados = filtrados.filter(p => p.precio_actual > 200);
    }

    if (tema) filtrados = filtrados.filter(p => p.tema === tema);

    renderProductosAccesorios(filtrados);
}

// Renderiza productos accesorios

 function renderProductosAccesorios(lista) {
    const contenedor = document.getElementById('productos');
    contenedor.innerHTML = "";
    if (!lista.length) {
        contenedor.innerHTML = `<div class="col-12"><p class="text-center text-muted">No hay accesorios que coincidan con los filtros.</p></div>`;
        return;
    }
    lista.forEach(p => {
        const col = document.createElement('div');
        col.className = "col";
        col.innerHTML = `
             <div class="card h-100 producto" onclick="mostrarDetalleProducto(${p.id})">
                <img src="${p.imagen_principal}" alt="${p.nombre}" class="card-img-top">
                <div class="card-body">
                    <h5 class="card-title">${p.nombre}</h5>
                    <p class="card-text">S/${p.precio_actual.toFixed(2)}</p>
                </div>
            </div>
        `;
        contenedor.appendChild(col);
    });
}

// Ventana Modal

function mostrarDetalleProducto(id) {
    const p = productosAccesorios.find(item => item.id === id);

    document.getElementById('detalleProductoTitulo').innerText = p.nombre;
    document.getElementById('detalleProductoImagen').src = p.imagen_principal;
    document.getElementById('detalleProductoDescripcion').innerText = p.descripcion || "Sin descripción";
    document.getElementById('detalleProductoPrecio').innerText = p.precio_actual.toFixed(2);

    // Mostrar modal
    let modal = new bootstrap.Modal(document.getElementById('detalleProductoModal'));
    modal.show();
}

// Filtros evento accesorios
if (document.getElementById('form-filtros')) {
    document.getElementById('form-filtros').addEventListener('submit', function(e){
        e.preventDefault();
        filtrarProductosAccesorios();
    });
    // Carga inicial
    filtrarProductosAccesorios();
}


// Comentar TODA la lógica de renderizado automático
/*
document.addEventListener("DOMContentLoaded", () => {
    filtrarProductosAccesorios();
});

if (false) {
    filtrarProductosAccesorios();
}
*/

// SOLO mantener el listener de agregar al carrito
document.addEventListener('click', e => {
    const btn = e.target.closest('.btn-add-cart');
    if(!btn) return;
    const id = btn.getAttribute('data-id');
    console.log('Agregar al carrito ID:', id);
});





