package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.entidades.EstadoPedido;
import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.servicios.PedidoService;
import com.proyectoweb.Juledtoys.servicios.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/backoffice/admin/pedidos")
public class BackofficePedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listarPedidos(@RequestParam(required = false) String estado,
                               @RequestParam(required = false) String busqueda,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
                               Model model) {
        
        List<Pedido> pedidos;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            pedidos = pedidoService.buscar(busqueda);
        } else if (estado != null && !estado.isEmpty()) {
            try {
                EstadoPedido estadoEnum = EstadoPedido.valueOf(estado);
                if (fechaInicio != null && fechaFin != null) {
                    pedidos = pedidoService.buscarPorEstado(estadoEnum);
                } else {
                    pedidos = pedidoService.buscarPorEstado(estadoEnum);
                }
            } catch (IllegalArgumentException e) {
                pedidos = pedidoService.listarTodos();
            }
        } else if (fechaInicio != null && fechaFin != null) {
            pedidos = pedidoService.buscarPorFechas(fechaInicio, fechaFin);
        } else {
            pedidos = pedidoService.listarTodos();
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("estados", EstadoPedido.values());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("busqueda", busqueda);
        
        return "backoffice/pedidos";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        model.addAttribute("pedido", pedido);
        return "backoffice/pedido-detalle";
    }

    @PostMapping("/crear")
    public String crearPedido(@Valid @ModelAttribute Pedido pedido,
                             BindingResult result,
                             @RequestParam Long clienteId,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", 
                "Error en los datos del pedido: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/backoffice/admin/pedidos";
        }

        try {
            // Asignar cliente
            Cliente cliente = clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            pedido.setCliente(cliente);

            pedidoService.guardar(pedido);
            redirectAttributes.addFlashAttribute("success", 
                "Pedido creado exitosamente con número: " + pedido.getNumeroPedido());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear el pedido: " + e.getMessage());
        }

        return "redirect:/backoffice/admin/pedidos";
    }

    @PostMapping("/{id}/actualizar-estado")
    public String actualizarEstado(@PathVariable Long id,
                                  @RequestParam String nuevoEstado,
                                  RedirectAttributes redirectAttributes) {
        try {
            EstadoPedido estado = EstadoPedido.valueOf(nuevoEstado);
            pedidoService.actualizarEstado(id, estado);
            redirectAttributes.addFlashAttribute("success", 
                "Estado del pedido actualizado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Estado de pedido no válido");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar el estado: " + e.getMessage());
        }

        return "redirect:/backoffice/admin/pedidos";
    }

    @PostMapping("/{id}/editar")
    public String editarPedido(@PathVariable Long id,
                              @Valid @ModelAttribute Pedido pedidoEditado,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", 
                "Error en los datos: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/backoffice/admin/pedidos";
        }

        try {
            Pedido pedidoExistente = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            // Actualizar campos editables
            pedidoExistente.setDireccionEnvio(pedidoEditado.getDireccionEnvio());
            pedidoExistente.setTelefonoContacto(pedidoEditado.getTelefonoContacto());
            pedidoExistente.setMetodoPago(pedidoEditado.getMetodoPago());
            pedidoExistente.setObservaciones(pedidoEditado.getObservaciones());
            pedidoExistente.setFechaEntregaEstimada(pedidoEditado.getFechaEntregaEstimada());

            pedidoService.guardar(pedidoExistente);
            redirectAttributes.addFlashAttribute("success", "Pedido actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar el pedido: " + e.getMessage());
        }

        return "redirect:/backoffice/admin/pedidos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarPedido(@PathVariable Long id, 
                                RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            // Solo permitir eliminar pedidos cancelados o pendientes
            if (pedido.getEstado() == EstadoPedido.ENTREGADO || 
                pedido.getEstado() == EstadoPedido.EN_CAMINO) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar un pedido en estado " + pedido.getEstado().getDescripcion());
                return "redirect:/backoffice/admin/pedidos";
            }

            pedidoService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Pedido eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar el pedido: " + e.getMessage());
        }

        return "redirect:/backoffice/admin/pedidos";
    }
}
