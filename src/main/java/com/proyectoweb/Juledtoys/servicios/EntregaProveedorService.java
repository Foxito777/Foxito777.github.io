package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.EntregaProveedor;
import com.proyectoweb.Juledtoys.entidades.Proveedor;
import com.proyectoweb.Juledtoys.repositorios.EntregaProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EntregaProveedorService {

    @Autowired
    private EntregaProveedorRepository entregaProveedorRepository;

    // Obtener todas las entregas
    public List<EntregaProveedor> obtenerTodas() {
        return entregaProveedorRepository.findAll();
    }

    // Obtener por ID
    public Optional<EntregaProveedor> obtenerPorId(Long id) {
        return entregaProveedorRepository.findById(id);
    }

    // Obtener entregas por proveedor
    public List<EntregaProveedor> obtenerPorProveedor(Proveedor proveedor) {
        return entregaProveedorRepository.findByProveedorOrderByFechaEntregaDesc(proveedor);
    }

    // Obtener por número de guía
    public Optional<EntregaProveedor> obtenerPorNumeroGuia(String numeroGuia) {
        return entregaProveedorRepository.findByNumeroGuia(numeroGuia);
    }

    // Obtener por estado
    public List<EntregaProveedor> obtenerPorEstado(String estado) {
        return entregaProveedorRepository.findByEstadoOrderByFechaEntregaDesc(estado);
    }

    // Crear entrega
    @Transactional
    public EntregaProveedor crear(EntregaProveedor entrega) {
        return entregaProveedorRepository.save(entrega);
    }

    // Actualizar entrega
    @Transactional
    public EntregaProveedor actualizar(EntregaProveedor entrega) {
        return entregaProveedorRepository.save(entrega);
    }

    // Eliminar entrega
    @Transactional
    public void eliminar(Long id) {
        entregaProveedorRepository.deleteById(id);
    }

    // Cambiar estado de entrega
    @Transactional
    public void cambiarEstado(Long id, String nuevoEstado) {
        Optional<EntregaProveedor> entregaOpt = entregaProveedorRepository.findById(id);
        if (entregaOpt.isPresent()) {
            EntregaProveedor entrega = entregaOpt.get();
            entrega.setEstado(nuevoEstado);
            entregaProveedorRepository.save(entrega);
        }
    }
}
