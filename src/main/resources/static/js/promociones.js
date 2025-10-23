// Gestión de Promociones - Backoffice
// Replica el flujo utilizado en Gestión de Productos.

document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = "/backoffice/admin/promociones";
  const modalEditarEl = document.getElementById("modalEditarPromocion");
  const modalEliminarEl = document.getElementById("modalEliminarPromocion");
  const formEditar = document.getElementById("formEditarPromocion");
  const formEliminar = document.getElementById("formEliminarPromocion");
  const nombreEliminar = document.getElementById("nombrePromocionEliminar");

  const showModal = (element) => {
    if (!element) return;
    const modal = new bootstrap.Modal(element);
    modal.show();
    return modal;
  };

  const setInputValue = (id, value) => {
    const input = document.getElementById(id);
    if (!input) return;
    input.value = value ?? "";
  };

  async function cargarPromocion(id) {
    const response = await fetch(`${BASE_URL}/${id}`, {
      headers: { Accept: "application/json" },
    });
    if (!response.ok) {
      throw new Error(`Respuesta inválida (${response.status})`);
    }
    return response.json();
  }

  document.querySelectorAll(".btn-editar").forEach((button) => {
    button.addEventListener("click", async () => {
      const id = button.getAttribute("data-promocion-id");
      if (!id) return;

      try {
        const promo = await cargarPromocion(id);

        if (formEditar) {
          formEditar.action = `${BASE_URL}/${id}/editar`;
        }

        setInputValue("editEtiqueta", promo.etiqueta);
        setInputValue("editTitulo", promo.titulo);
        setInputValue("editSubtitulo", promo.subtitulo);
        setInputValue("editDescripcion", promo.descripcion);
        setInputValue("editCtaTexto", promo.ctaTexto);
        setInputValue("editCtaUrl", promo.ctaUrl);
        setInputValue("editInicio", promo.inicio ?? "");
        setInputValue("editFin", promo.fin ?? "");
        setInputValue("editPrioridad", promo.prioridad ?? "MEDIA");
        setInputValue("editEstado", promo.estado ?? "ACTIVA");
        setInputValue("editOrden", promo.orden ?? 1);
        setInputValue("editColorFondo", promo.colorFondo ?? "#ff6b35");
        setInputValue("editColorTexto", promo.colorTexto ?? "#ffffff");
        setInputValue("editImagenActual", promo.imagen ?? "");

        const checkDestacado = document.getElementById("editDestacado");
        if (checkDestacado) {
          checkDestacado.checked = !!promo.destacado;
        }

        showModal(modalEditarEl);
      } catch (error) {
        console.error(error);
        alert("Error al cargar los datos de la promoción.");
      }
    });
  });

  document.querySelectorAll(".btn-eliminar").forEach((button) => {
    button.addEventListener("click", () => {
      const id = button.getAttribute("data-promocion-id");
      const titulo = button.getAttribute("data-promocion-titulo") || "";

      if (formEliminar) {
        formEliminar.action = `${BASE_URL}/${id}/eliminar`;
      }
      if (nombreEliminar) {
        nombreEliminar.textContent = titulo;
      }

      showModal(modalEliminarEl);
    });
  });

  // Auto-hide alerts after 5 seconds
  setTimeout(() => {
    document.querySelectorAll(".alert").forEach((alertEl) => {
      try {
        new bootstrap.Alert(alertEl).close();
      } catch (err) {
        console.debug("No se pudo cerrar la alerta", err);
      }
    });
  }, 5000);
});
