package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Compra;
import com.proyectoweb.Juledtoys.entidades.Proveedor;
import com.proyectoweb.Juledtoys.repositorios.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    // Obtener todas las compras
    public List<Compra> obtenerTodas() {
        return compraRepository.findAll();
    }

    // Obtener por ID
    public Optional<Compra> obtenerPorId(Long id) {
        return compraRepository.findById(id);
    }

    // Obtener compras por proveedor
    public List<Compra> obtenerPorProveedor(Proveedor proveedor) {
        return compraRepository.findByProveedorOrderByFechaCompraDesc(proveedor);
    }

    // Obtener por número de orden
    public Optional<Compra> obtenerPorNumeroOrden(String numeroOrden) {
        return compraRepository.findByNumeroOrden(numeroOrden);
    }

    // Obtener por estado
    public List<Compra> obtenerPorEstado(String estado) {
        return compraRepository.findByEstadoOrderByFechaCompraDesc(estado);
    }

    // Crear compra
    @Transactional
    public Compra crear(Compra compra) {
        return compraRepository.save(compra);
    }

    // Actualizar compra
    @Transactional
    public Compra actualizar(Compra compra) {
        return compraRepository.save(compra);
    }

    // Eliminar compra
    @Transactional
    public void eliminar(Long id) {
        compraRepository.deleteById(id);
    }

    // Cambiar estado de compra
    @Transactional
    public void cambiarEstado(Long id, String nuevoEstado) {
        Optional<Compra> compraOpt = compraRepository.findById(id);
        if (compraOpt.isPresent()) {
            Compra compra = compraOpt.get();
            compra.setEstado(nuevoEstado);
            compraRepository.save(compra);
        }
    }
}
