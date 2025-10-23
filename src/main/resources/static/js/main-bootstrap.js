// --- Lógica accesorios (simulado) ---
const productosAccesorios = [
    {
        id: 1,
        nombre: "Set Bloques Creativos",
        tema: "Construcción",
        piezas: 120,
        precio_actual: 29.99,
        precio_original: 39.99,
        descuento: 25,
        imagen_principal: "Imagenes/accesorio1.png",
        stock: 10
    },
    {
        id: 2,
        nombre: "Auto de Carreras",
        tema: "Vehículos",
        piezas: 60,
        precio_actual: 19.99,
        precio_original: 19.99,
        descuento: 0,
        imagen_principal: "Imagenes/accesorio2.png",
        stock: 0
    },
    {
        id: 3,
        nombre: "Kit Creativo de Colores",
        tema: "Arte",
        piezas: 20,
        precio_actual: 14.99,
        precio_original: 21.99,
        descuento: 32,
        imagen_principal: "Imagenes/accesorio3.png",
        stock: 5
    },
    {
        id: 4,
        nombre: "Guantes de Juego",
        tema: "Ropa",
        piezas: null,
        precio_actual: 9.99,
        precio_original: null,
        descuento: 0,
        imagen_principal: "Imagenes/accesorio4.png",
        stock: 12
    }
    // ... más accesorios si deseas
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
            <div class="card h-100">
                <a href="set${p.id}.html" class="text-decoration-none text-dark">
                    <img src="${p.imagen_principal}" alt="${p.nombre}">
                    <h5 class="card-title">${p.nombre}${p.tema ? ' - '+p.tema : ''}${p.piezas ? ' - '+p.piezas+' Piezas' : ''}</h5>
                    <p class="card-text">
                        S/${p.precio_actual.toFixed(2)}
                        ${p.precio_original && p.precio_original > p.precio_actual ? `<span class="precio-original">S/${p.precio_original.toFixed(2)}</span>` : ""}
                        ${p.descuento > 0 ? `<span class="descuento">-${p.descuento}%</span>` : ""}
                    </p>
                    ${p.stock <= 0 ? `<span class="badge bg-danger">AGOTADO</span>` : ""}
                    <button class="btn btn-success btn-sm mt-2 btn-carrito" data-id="${p.id}">Agregar al carrito</button>
                </a>
            </div>
        `;
        contenedor.appendChild(col);
    });
    // Agregar lógica de carrito a nuevos botones
    document.querySelectorAll('.btn-carrito').forEach(function(btn){
        btn.addEventListener('click',function(e){
            e.preventDefault();
            agregarAlCarrito(btn.dataset.id);
        });
    });
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

// --- Buscar productos (simulado) ---
const productosBusqueda = [
    {
        id: 101,
        nombre: "Super Granja Minecraft",
        precio_actual: 225,
        precio_original: 239,
        descuento: 6,
        imagen_principal: "Imagenes/super-granja.png",
        tema: "Minecraft"
    },
    {
        id: 102,
        nombre: "Auto Ferrari",
        precio_actual: 100,
        precio_original: 120,
        descuento: 17,
        imagen_principal: "Imagenes/Ferrari.png",
        tema: "Autos"
    }
    // ... más si deseas
];

// Renderizar resultados de búsqueda
function renderResultadosBusqueda(q) {
    if (!document.getElementById('resultados-busqueda')) return;
    const lista = productosBusqueda.filter(p =>
        p.nombre.toLowerCase().includes(q.toLowerCase()) ||
        (p.tema && p.tema.toLowerCase().includes(q.toLowerCase()))
    );
    const contenedor = document.getElementById('resultados-busqueda');
    contenedor.innerHTML = "";
    if (!lista.length) {
        contenedor.innerHTML = `<p>No se encontraron productos que coincidan.</p>`;
        return;
    }
    const wrap = document.createElement('div');
    wrap.className = "productos-busqueda";
    lista.forEach(p => {
        const card = document.createElement('div');
        card.className = "producto-card";
        card.innerHTML = `
            <a href="set${p.id}.html">
              <img src="${p.imagen_principal}" alt="${p.nombre}">
              <h3>${p.nombre}</h3>
            </a>
            <div>
                <span class="precio">S/${p.precio_actual.toFixed(2)}</span>
                ${p.precio_original > p.precio_actual ? `<span class="precio-original">S/${p.precio_original.toFixed(2)}</span>` : ""}
                ${p.descuento ? `<span class="descuento">-${p.descuento}%</span>` : ""}
            </div>
            <div style="font-size:0.95em;color:#444;margin:8px 0;">${p.tema}</div>
        `;
        wrap.appendChild(card);
    });
    contenedor.appendChild(wrap);
}

// Evento buscar
if (document.getElementById('form-buscar')) {
    document.getElementById('form-buscar').addEventListener('submit',function(e){
        e.preventDefault();
        const q = document.getElementById('input-buscar').value || "";
        renderResultadosBusqueda(q);
    });
    // Inicial vacío
    renderResultadosBusqueda("");
}

// --- Función agregar al carrito (simulado frontend) ---
function agregarAlCarrito(id) {
    alert('Producto agregado al carrito (simulado)');
    // Aquí puedes guardar en localStorage o mostrar badge en el carrito
}

// --- Funciones de la landing principal ---
// Mostrar alerta al comprar (landing principal)
document.querySelectorAll('.btn-outline-warning').forEach(function(btn) {
    btn.addEventListener('click', function(e) {
        e.preventDefault();
        alert('¡Producto agregado al carrito!');
    });
});

// WhatsApp flotante animado
const whatsappFloat = document.querySelector('.btn-success.position-fixed');
if (whatsappFloat) {
    whatsappFloat.addEventListener('mouseenter', function() {
        whatsappFloat.style.transform = "scale(1.08)";
    });
    whatsappFloat.addEventListener('mouseleave', function() {
        whatsappFloat.style.transform = "scale(1)";
    });
}

// Modal login validación simple
const loginModal = document.getElementById('loginModal');
if (loginModal) {
    loginModal.querySelector('form').addEventListener('submit', function(e) {
        e.preventDefault();
        alert('Inicio de sesión simulado');
        const modal = bootstrap.Modal.getInstance(loginModal);
        modal.hide();
    });
}

// Buscador universal en todas las páginas
if (document.getElementById('form-buscar')) {
  document.getElementById('form-buscar').addEventListener('submit', function(e){
    e.preventDefault();
    const q = document.getElementById('input-buscar').value || "";
    // Redirige a buscar.html con el término como parámetro GET
    window.location.href = "buscar.html?q=" + encodeURIComponent(q);
  });
}

// Si estás en buscar.html, muestra los resultados con el parámetro
function getQueryParam(name) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(name) || "";
}

if (document.getElementById('resultados-busqueda')) {
  const term = getQueryParam('q');
  renderResultadosBusqueda(term);
}