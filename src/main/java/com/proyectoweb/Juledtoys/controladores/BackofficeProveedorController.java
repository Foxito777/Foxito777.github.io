package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Compra;
import com.proyectoweb.Juledtoys.entidades.EntregaProveedor;
import com.proyectoweb.Juledtoys.entidades.Proveedor;
import com.proyectoweb.Juledtoys.servicios.CompraService;
import com.proyectoweb.Juledtoys.servicios.EntregaProveedorService;
import com.proyectoweb.Juledtoys.servicios.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/backoffice/admin/proveedores")
public class BackofficeProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private CompraService compraService;

    @Autowired
    private EntregaProveedorService entregaProveedorService;

    // ========== PROVEEDORES ==========

    @GetMapping
    public String listarProveedores(@RequestParam(required = false) String q, Model model) {
        List<Proveedor> proveedores;
        
        if (q != null && !q.trim().isEmpty()) {
            proveedores = proveedorService.buscarGeneral(q);
        } else {
            proveedores = proveedorService.obtenerTodos();
        }

        model.addAttribute("proveedores", proveedores);
        model.addAttribute("totalProveedores", proveedores.size());
        model.addAttribute("proveedoresActivos", proveedorService.contarActivos());
        model.addAttribute("q", q);

        return "backoffice/proveedores";
    }

    @PostMapping("/crear")
    public String crearProveedor(@Valid @ModelAttribute Proveedor proveedor,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores";
        }

        // Verificar si ya existe un proveedor con ese RUC
        Optional<Proveedor> existente = proveedorService.obtenerPorRuc(proveedor.getRuc());
        if (existente.isPresent()) {
            redirectAttributes.addFlashAttribute("mensaje", "Ya existe un proveedor con el RUC: " + proveedor.getRuc());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores";
        }

        try {
            proveedorService.crear(proveedor);
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear proveedor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/backoffice/admin/proveedores";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarProveedor(@PathVariable Long id,
                                       @Valid @ModelAttribute Proveedor proveedor,
                                       BindingResult result,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores";
        }

        Optional<Proveedor> existente = proveedorService.obtenerPorId(id);
        if (existente.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor no encontrado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores";
        }

        try {
            Proveedor proveedorActual = existente.get();
            proveedorActual.setRazonSocial(proveedor.getRazonSocial());
            proveedorActual.setRuc(proveedor.getRuc());
            proveedorActual.setDireccion(proveedor.getDireccion());
            proveedorActual.setTelefono(proveedor.getTelefono());
            proveedorActual.setEmail(proveedor.getEmail());
            proveedorActual.setPersonaContacto(proveedor.getPersonaContacto());
            proveedorActual.setNotas(proveedor.getNotas());
            proveedorActual.setActivo(proveedor.getActivo());

            proveedorService.actualizar(proveedorActual);
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar proveedor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/backoffice/admin/proveedores";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            proveedorService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar proveedor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/backoffice/admin/proveedores";
    }

    // ========== COMPRAS ==========

    @GetMapping("/compras")
    public String listarCompras(@RequestParam(required = false) Long proveedorId,
                                @RequestParam(required = false) String estado,
                                Model model) {
        List<Compra> compras;

        if (proveedorId != null) {
            Optional<Proveedor> proveedor = proveedorService.obtenerPorId(proveedorId);
            if (proveedor.isPresent()) {
                compras = compraService.obtenerPorProveedor(proveedor.get());
            } else {
                compras = compraService.obtenerTodas();
            }
        } else if (estado != null && !estado.isEmpty()) {
            compras = compraService.obtenerPorEstado(estado);
        } else {
            compras = compraService.obtenerTodas();
        }

        model.addAttribute("compras", compras);
        model.addAttribute("proveedores", proveedorService.obtenerActivos());
        model.addAttribute("totalCompras", compras.size());

        return "backoffice/proveedores-compras";
    }

    @PostMapping("/compras/crear")
    public String crearCompra(@Valid @ModelAttribute Compra compra,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores/compras";
        }

        try {
            compraService.crear(compra);
            redirectAttributes.addFlashAttribute("mensaje", "Compra registrada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar compra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/backoffice/admin/proveedores/compras";
    }

    @PostMapping("/compras/actualizar/{id}")
    public String actualizarCompra(@PathVariable Long id,
                                    @ModelAttribute Compra compra,
                                    RedirectAttributes redirectAttributes) {
        Optional<Compra> existente = compraService.obtenerPorId(id);
        if (existente.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Compra no encontrada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores/compras";
        }

        try {
            Compra compraActual = existente.get();
            compraActual.setEstado(compra.getEstado());
            compraActual.setObservaciones(compra.getObservaciones());
            compraActual.setFechaRecepcion(compra.getFechaRecepcion());

            compraService.actualizar(compraActual);
            redirectAttributes.addFlashAttribute("mensaje", "Compra actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar compra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/backoffice/admin/proveedores/compras";
    }

    // ========== ENTREGAS ==========

    @GetMapping("/entregas")
    public String listarEntregas(@RequestParam(required = false) Long proveedorId,
                                 @RequestParam(required = false) String estado,
                                 Model model) {
        List<EntregaProveedor> entregas;

        if (proveedorId != null) {
            Optional<Proveedor> proveedor = proveedorService.obtenerPorId(proveedorId);
            if (proveedor.isPresent()) {
                entregas = entregaProveedorService.obtenerPorProveedor(proveedor.get());
            } else {
                entregas = entregaProveedorService.obtenerTodas();
            }
        } else if (estado != null && !estado.isEmpty()) {
            entregas = entregaProveedorService.obtenerPorEstado(estado);
        } else {
            entregas = entregaProveedorService.obtenerTodas();
        }

        model.addAttribute("entregas", entregas);
        model.addAttribute("proveedores", proveedorService.obtenerActivos());
        model.addAttribute("totalEntregas", entregas.size());

        return "backoffice/proveedores-entregas";
    }

    @PostMapping("/entregas/crear")
    public String crearEntrega(@Valid @ModelAttribute EntregaProveedor entrega,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores/entregas";
        }

        try {
            entregaProveedorService.crear(entrega);
            redirectAttributes.addFlashAttribute("mensaje", "Entrega registrada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar entrega: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/backoffice/admin/proveedores/entregas";
    }

    @PostMapping("/entregas/actualizar/{id}")
    public String actualizarEntrega(@PathVariable Long id,
                                     @ModelAttribute EntregaProveedor entrega,
                                     RedirectAttributes redirectAttributes) {
        Optional<EntregaProveedor> existente = entregaProveedorService.obtenerPorId(id);
        if (existente.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Entrega no encontrada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/backoffice/admin/proveedores/entregas";
        }

        try {
            EntregaProveedor entregaActual = existente.get();
            entregaActual.setEstado(entrega.getEstado());
            entregaActual.setObservaciones(entrega.getObservaciones());
            entregaActual.setFechaRecepcion(entrega.getFechaRecepcion());
            entregaActual.setRecibidoPor(entrega.getRecibidoPor());

            entregaProveedorService.actualizar(entregaActual);
            redirectAttributes.addFlashAttribute("mensaje", "Entrega actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar entrega: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/backoffice/admin/proveedores/entregas";
    }
}
