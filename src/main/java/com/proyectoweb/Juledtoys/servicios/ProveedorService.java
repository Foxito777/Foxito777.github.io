package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Proveedor;
import com.proyectoweb.Juledtoys.repositorios.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    // Obtener todos los proveedores
    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    // Obtener proveedores activos
    public List<Proveedor> obtenerActivos() {
        return proveedorRepository.findByActivoTrue();
    }

    // Obtener por ID
    public Optional<Proveedor> obtenerPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    // Obtener por RUC
    public Optional<Proveedor> obtenerPorRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc);
    }

    // Buscar por razón social
    public List<Proveedor> buscarPorRazonSocial(String query) {
        return proveedorRepository.buscarPorRazonSocial(query);
    }

    // Búsqueda general
    public List<Proveedor> buscarGeneral(String query) {
        if (query == null || query.trim().isEmpty()) {
            return obtenerTodos();
        }
        return proveedorRepository.buscarGeneral(query);
    }

    // Crear proveedor
    @Transactional
    public Proveedor crear(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    // Actualizar proveedor
    @Transactional
    public Proveedor actualizar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    // Eliminar proveedor
    @Transactional
    public void eliminar(Long id) {
        proveedorRepository.deleteById(id);
    }

    // Activar/Desactivar proveedor
    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
        if (proveedorOpt.isPresent()) {
            Proveedor proveedor = proveedorOpt.get();
            proveedor.setActivo(activo);
            proveedorRepository.save(proveedor);
        }
    }

    // Contar proveedores activos
    public long contarActivos() {
        return proveedorRepository.countByActivoTrue();
    }

    // Contar total proveedores
    public long contarTotal() {
        return proveedorRepository.count();
    }
}
