package com.example.vidapet.controller;

import com.example.vidapet.service.VidapetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class VidapetController {

    private final VidapetService vidapetService;

    public VidapetController(VidapetService vidapetService) {
        this.vidapetService = vidapetService;
    }

    // ===== Página principal =====
    @GetMapping
    public String index() {
        return "index"; // index.html
    }

    /*---------------------------- PROPIETARIOS ----------------------------*/

    // ===== Listar todos los propietarios =====
    @GetMapping("/propietarios")
    public String listarPropietarios(Model model) {
        model.addAttribute("propietarios", vidapetService.listarPropietarios());
        return "propietarios"; // propietarios.html
    }

    // ===== Mostrar formulario para nuevo propietario =====
    @GetMapping("/propietarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Map<String, Object> propietario = new HashMap<>();
        propietario.put("id", "");
        propietario.put("nombre", "");
        propietario.put("apellido", "");
        propietario.put("telefono", "");
        propietario.put("email", "");
        model.addAttribute("propietario", propietario);
        return "propietario_form"; // formulario para crear propietario
    }

    // ===== Guardar nuevo propietario =====
    @PostMapping("/propietarios/guardar")
    public String guardarPropietario(@RequestParam String nombre,
                                     @RequestParam String apellido,
                                     @RequestParam String telefono,
                                     @RequestParam String email) {
        vidapetService.guardarPropietario(nombre, apellido, telefono, email);
        return "redirect:/propietarios";
    }

    // ===== Mostrar formulario para editar propietario existente =====
    @GetMapping("/propietarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Map<String,Object> propietario = vidapetService.obtenerPropietarioPorId(id);
        model.addAttribute("propietario", propietario);
        return "propietario_form"; // formulario para editar propietario
    }

    // ===== Actualizar propietario existente =====
    @PostMapping("/propietarios/actualizar")
    public String actualizarPropietario(@RequestParam Long id,
                                        @RequestParam String nombre,
                                        @RequestParam String apellido,
                                        @RequestParam String telefono,
                                        @RequestParam String email) {
        vidapetService.actualizarPropietario(id, nombre, apellido, telefono, email);
        return "redirect:/propietarios";
    }

    // ===== Eliminar propietario =====
    @GetMapping("/propietarios/eliminar/{id}")
    public String eliminarPropietario(@PathVariable Long id) {
        vidapetService.eliminarPropietario(id);
        return "redirect:/propietarios";
    }

    /*------------------------------- MASCOTAS ----------------------------------*/

    // ===== Listar todas las mascotas =====
    @GetMapping("/mascotas")
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "mascotas"; // mascotas.html
    }

    // ===== Mostrar formulario para nueva mascota =====
    @GetMapping("/mascotas/nuevo")
    public String nuevaMascota(Model model) {
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("id", "");
        mascota.put("nombre", "");
        mascota.put("especie", "");
        mascota.put("raza", "");
        mascota.put("edad", "");
        mascota.put("propietario_id", "");

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios()); // lista de propietarios
        return "mascota_form"; // formulario para crear mascota
    }

    // ===== Guardar nueva mascota =====
    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@RequestParam String nombre,
                                 @RequestParam String especie,
                                 @RequestParam String raza,
                                 @RequestParam int edad,
                                 @RequestParam Long propietario_id) {

        vidapetService.guardarMascota(nombre, especie, raza, edad, propietario_id);
        return "redirect:/mascotas";
    }

    // ===== Mostrar formulario para editar mascota existente =====
    @GetMapping("/mascotas/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        Map<String,Object> mascota = vidapetService.obtenerMascotaPorId(id);
        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios()); // lista de propietarios
        return "mascota_form"; // formulario para editar mascota
    }

    // ===== Actualizar mascota existente =====
    @PostMapping("/mascotas/actualizar")
    public String actualizarMascota(@RequestParam Long id,
                                    @RequestParam String nombre,
                                    @RequestParam String especie,
                                    @RequestParam String raza,
                                    @RequestParam int edad,
                                    @RequestParam Long propietario_id) {

        vidapetService.actualizarMascota(id, nombre, especie, raza, edad, propietario_id);
        return "redirect:/mascotas";
    }

    // ===== Eliminar mascota =====
    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        vidapetService.eliminarMascota(id);
        return "redirect:/mascotas";
    }

    /*---------------------------- TRATAMIENTOS ----------------------------*/

    // ===== Listar tratamientos =====
    @GetMapping("/tratamientos")
    public String listarTratamientos(Model model) {
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());
        return "tratamientos"; // tratamientos.html
    }

    // ===== Mostrar formulario para nuevo tratamiento =====
    @GetMapping("/tratamientos/nuevo")
    public String nuevoTratamiento(Model model) {
        Map<String, Object> tratamiento = new HashMap<>();
        tratamiento.put("id", "");
        tratamiento.put("tipo", "");
        tratamiento.put("duracion", "");
        tratamiento.put("consulta_id", "");
        model.addAttribute("tratamiento", tratamiento);
        return "tratamiento_form"; // formulario para crear tratamiento
    }

    // ===== Guardar nuevo tratamiento =====
    @PostMapping("/tratamientos/guardar")
    public String guardarTratamiento(@RequestParam String tipo,
                                     @RequestParam String duracion,
                                     @RequestParam Long consulta_id) {

        vidapetService.guardarTratamiento(tipo, duracion, consulta_id);
        return "redirect:/tratamientos";
    }

    // ===== Mostrar formulario para editar tratamiento existente =====
    @GetMapping("/tratamientos/editar/{id}")
    public String editarTratamiento(@PathVariable Long id, Model model) {
        Map<String,Object> tratamiento = vidapetService.obtenerTratamientoPorId(id);
        model.addAttribute("tratamiento", tratamiento);
        return "tratamiento_form"; // formulario para editar tratamiento
    }

    // ===== Actualizar tratamiento existente =====
    @PostMapping("/tratamientos/actualizar")
    public String actualizarTratamiento(@RequestParam Long id,
                                        @RequestParam String tipo,
                                        @RequestParam String duracion,
                                        @RequestParam Long consulta_id) {

        vidapetService.actualizarTratamiento(id, tipo, duracion, consulta_id);
        return "redirect:/tratamientos";
    }

    // ===== Eliminar tratamiento =====
    @GetMapping("/tratamientos/eliminar/{id}")
    public String eliminarTratamiento(@PathVariable Long id) {
        vidapetService.eliminarTratamiento(id);
        return "redirect:/tratamientos";
    }
}