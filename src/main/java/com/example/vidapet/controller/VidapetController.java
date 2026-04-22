package com.example.vidapet.controller;

import com.example.vidapet.dao.ConsultaDAO;
import com.example.vidapet.dao.EspecieDAO;
import com.example.vidapet.service.VidapetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/")
public class VidapetController {

    private final VidapetService vidapetService;
    // اضافه شده برای حل مشکل ارور جستجو

    // سازنده اصلاح شده (Dependency Injection)
    public VidapetController(VidapetService vidapetService) {
        this.vidapetService = vidapetService;
    }

    // ======================================================
    // (INDEX)
    // ======================================================
    @GetMapping
    public String index() {
        return "index";
    }

    // ======================================================
    //  (PROPIETARIOS)
    // ======================================================
    @GetMapping("/propietarios")
    public String listarPropietarios(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("propietarios", vidapetService.listarPropietarios(search));
        return "propietarios";
    }

    @GetMapping("/propietarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Map<String, Object> propietario = new HashMap<>();
        propietario.put("id", "");
        propietario.put("nombre", "");
        propietario.put("apellido", "");
        propietario.put("telefono", "");
        propietario.put("email", "");
        model.addAttribute("propietario", propietario);
        return "propietario_form";
    }

    @PostMapping("/propietarios/guardar")
    public String guardarPropietario(@RequestParam String nombre, @RequestParam String apellido,
                                     @RequestParam String telefono, @RequestParam String email) {
        vidapetService.guardarPropietario(nombre, apellido, telefono, email);
        return "redirect:/propietarios";
    }

    @GetMapping("/propietarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("propietario", vidapetService.obtenerPropietarioPorId(id));
        return "propietario_form";
    }

    @PostMapping("/propietarios/actualizar")
    public String actualizarPropietario(@RequestParam Long id, @RequestParam String nombre,
                                        @RequestParam String apellido, @RequestParam String telefono,
                                        @RequestParam String email) {
        vidapetService.actualizarPropietario(id, nombre, apellido, telefono, email);
        return "redirect:/propietarios";
    }

    @GetMapping("/propietarios/eliminar/{id}")
    public String eliminarPropietario(@PathVariable Long id) {
        vidapetService.eliminarPropietario(id);
        return "redirect:/propietarios";
    }

    // ======================================================
    // (MASCOTAS)
    // ======================================================
    @GetMapping("/mascotas")
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "mascotas";
    }

    @GetMapping("/mascotas/nuevo")
    public String nuevaMascota(Model model) {
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("id", "");
        mascota.put("nombre", "");
        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));
        model.addAttribute("especies", vidapetService.listarEspecies(null));
        return "mascota_form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@RequestParam String nombre, @RequestParam Long especie_id,
                                 @RequestParam String raza, @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
                                 @RequestParam(required = false) Long propietario_id,
                                 @RequestParam(required = false) MultipartFile foto, RedirectAttributes ra) throws Exception {

        if (propietario_id == null) {
            ra.addFlashAttribute("error", "Debe seleccionar un propietario válido.");
            return "redirect:/mascotas/nuevo";
        }

        String filePath = handleFileUpload(foto);
        vidapetService.guardarMascota(nombre, especie_id, raza, LocalDate.parse(fechaNacimientoStr), propietario_id, filePath);
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        Map<String, Object> mascota = vidapetService.obtenerMascotaPorId(id);
        if (mascota == null) return "redirect:/mascotas";

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));
        model.addAttribute("especies", vidapetService.listarEspecies(null));
        return "mascota_form";
    }

    @PostMapping("/mascotas/actualizar")
    public String actualizarMascota(@RequestParam Long id, @RequestParam String nombre, @RequestParam Long especie_id,
                                    @RequestParam String raza, @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
                                    @RequestParam(required = false) Long propietario_id,
                                    @RequestParam(required = false) MultipartFile foto, RedirectAttributes ra) throws Exception {

        if (propietario_id == null) {
            ra.addFlashAttribute("error", "Debe seleccionar un propietario.");
            return "redirect:/mascotas/editar/" + id;
        }

        String filePath = handleFileUpload(foto);
        vidapetService.actualizarMascota(id, nombre, especie_id, raza, LocalDate.parse(fechaNacimientoStr), propietario_id, filePath);
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        vidapetService.eliminarMascota(id);
        return "redirect:/mascotas";
    }

    // ======================================================
    //  (CONSULTAS)
    // ======================================================
    @GetMapping("/consultas")
    public String listarConsultas(@RequestParam(required = false) String search, Model model) {
        List<Map<String, Object>> consultas = (search != null && !search.isBlank())
                ? vidapetService.buscarConsultasSeguro(search)
                : vidapetService.listarConsultas();

        model.addAttribute("consultas", consultas);
        model.addAttribute("search", search);
        return "consultas";
    }

    @GetMapping("/consultas/dashboard")
    public String dashboardConsultas(Model model) {
        model.addAttribute("consultas", vidapetService.listarConsultasConTratamientos());
        return "consultas";
    }

    @GetMapping("/consultas/{id}")
    public String verDetalleConsulta(@PathVariable int id, Model model) {
        Map<String, Object> data = vidapetService.obtenerConsultaDetalleCompleto(id);
        if (data == null) return "redirect:/consultas";
        model.addAttribute("data", data);
        return "consulta_detalle";
    }

    @GetMapping("/consultas/nueva-desde-cita/{citaId}")
    public String nuevaDesdeCita(@PathVariable int citaId, Model model) {
        Map<String, Object> cita = vidapetService.obtenerCita(citaId);
        if (cita == null) return "redirect:/citas";

        // گرفتن ID حیوان برای نمایش اطلاعات در فرم (بدون ذخیره در جدول Consulta)
        Integer mascotaId = (Integer) cita.get("mascota_id");

        Map<String, Object> data = vidapetService.obtenerDetalleParaNuevaConsulta(mascotaId, citaId);

        Map<String, Object> consulta = new HashMap<>();
        consulta.put("cita_id", citaId);
        consulta.put("diagnostico", ""); // دیگر mascota_id را اینجا نمی‌گذاریم

        model.addAttribute("consulta", consulta);
        model.addAttribute("data", data);
        model.addAttribute("tratamientos", new ArrayList<>());
        return "consulta_form";
    }

    @PostMapping("/consultas/guardar-desde-cita")
    public String guardarConsultaDesdeCita(
            @RequestParam int cita_id,
            @RequestParam String diagnostico,
            @RequestParam(required = false) List<String> tratamientos,
            @RequestParam(required = false) List<LocalDate> fecha_inicio,
            @RequestParam(required = false) List<LocalDate> fecha_fin,
            @RequestParam(required = false) List<String> observaciones
    ) {
        // ۱. ذخیره معاینه (فقط با cita_id)
        int consultaId = vidapetService.guardarConsultaDesdeCita(cita_id, diagnostico);

        // ۲. ذخیره درمان‌ها در صورت وجود
        if (tratamientos != null && !tratamientos.isEmpty()) {
            saveTratamientosList(consultaId, tratamientos, fecha_inicio, fecha_fin, observaciones);
        }

        return "redirect:/citas";
    }

    @GetMapping("/consultas/eliminar/{id}")
    public String eliminarConsulta(@PathVariable int id) {
        vidapetService.eliminarConsulta(id);
        return "redirect:/consultas";
    }
    // ======================================================
    //  (CITAS)
    // ======================================================
    @GetMapping("/citas")
    public String getCitas(@RequestParam(required = false) String mascota, @RequestParam(required = false) String propietario,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                           @RequestParam(required = false, defaultValue = "asc") String orden, Model model) {

        model.addAttribute("citas", vidapetService.listarCitas(mascota, propietario, fecha, orden));
        model.addAttribute("mascota", mascota);
        model.addAttribute("propietario", propietario);
        model.addAttribute("fecha", fecha);
        model.addAttribute("orden", orden);
        return "citas";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCita(Model model) {
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        model.addAttribute("veterinarios", vidapetService.listarVeterinarios(null));
        return "cita_form";
    }

    @PostMapping("/citas/guardar")
    public String guardarCita(@RequestParam int mascota_id, @RequestParam int propietario_id,
                              @RequestParam(required = false) Integer veterinario_id,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
                              @RequestParam String nota) {
        vidapetService.guardarCita(mascota_id, propietario_id, veterinario_id, fecha, nota);
        return "redirect:/citas";
    }

    @GetMapping("/citas/estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable int id, @PathVariable String estado) {
        vidapetService.cambiarEstadoCita(id, estado);
        return "redirect:/citas";
    }

    @GetMapping("/citas/eliminar/{id}")
    public String eliminarCita(@PathVariable("id") int id) { // حتماً PathVariable باشد
        vidapetService.eliminarCita(id);
        return "redirect:/citas";
    }
    // در کلاس VidapetController

    @GetMapping("/citas/editar/{id}")
    public String editarCita(@PathVariable int id, Model model) {
        // گرفتن اطلاعات نوبت
        Map<String, Object> cita = vidapetService.obtenerCita(id);

        model.addAttribute("cita", cita);
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));
        model.addAttribute("veterinarios", vidapetService.listarVeterinarios(null));

        return "cita_form"; // باز کردن فرم نوبت برای ویرایش
    }
    // ======================================================
    // (VETERINARIOS)
    // ======================================================
    @GetMapping("/veterinarios")
    public String listarVeterinarios(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("veterinarios", vidapetService.listarVeterinarios(search));
        return "veterinarios";
    }
    // در کلاس VidapetController

    @GetMapping("/veterinarios/nuevo")
    public String mostrarFormularioNuevoVeterinario(Model model) {
        Map<String, Object> veterinario = new HashMap<>();
        veterinario.put("id", "");
        veterinario.put("nombre", "");
        veterinario.put("apellido", "");
        veterinario.put("telefono", "");
        veterinario.put("email", "");
        veterinario.put("especialidad", "");
        veterinario.put("codigo_colegiado", "");

        model.addAttribute("veterinario", veterinario);
        return "veterinario_form"; // مطمئن شوید فایلی به این نام در templates دارید
    }

    @PostMapping("/veterinarios/guardar")
    public String guardarVeterinario(@RequestParam String nombre, @RequestParam String apellido, @RequestParam String telefono,
                                     @RequestParam String email, @RequestParam String especialidad, @RequestParam String codigo_colegiado) {
        vidapetService.guardarVeterinario(nombre, apellido, telefono, email, especialidad, codigo_colegiado);
        return "redirect:/veterinarios";
    }
    // در کلاس VidapetController بخش Veterinarios

    // نمایش فرم ویرایش با داده‌های قبلی
    @GetMapping("/veterinarios/editar/{id}")
    public String mostrarFormularioEditarVeterinario(@PathVariable Long id, Model model) {
        Map<String, Object> veterinario = vidapetService.obtenerVeterinarioPorId(id);
        model.addAttribute("veterinario", veterinario);
        return "veterinario_form"; // نام فایل HTML فرم شما
    }

    // دریافت اطلاعات جدید و ثبت در دیتابیس
    @PostMapping("/veterinarios/actualizar")
    public String actualizarVeterinario(@RequestParam Long id,
                                        @RequestParam String nombre,
                                        @RequestParam String apellido,
                                        @RequestParam String telefono,
                                        @RequestParam String email,
                                        @RequestParam String especialidad,
                                        @RequestParam String codigo_colegiado) {

        vidapetService.actualizarVeterinario(id, nombre, apellido, telefono, email, especialidad, codigo_colegiado);
        return "redirect:/veterinarios";
    }

    // حذف دامپزشک
    @GetMapping("/veterinarios/eliminar/{id}")
    public String eliminarVeterinario(@PathVariable Long id) {
        vidapetService.eliminarVeterinario(id);
        return "redirect:/veterinarios";
    }

    // ======================================================
    // 🧬 بخش گونه‌ها (ESPECIES)
    // ======================================================
    @Controller
    @RequestMapping("/especies")
    public class EspecieController {

        @Autowired
        private VidapetService vidapetService;

        // ===== LISTAR =====
        @GetMapping
        public String listarEspecies(@RequestParam(required = false) String search,
                                     Model model) {
            model.addAttribute("especies", vidapetService.listarEspecies(search));
            model.addAttribute("search", search);
            return "especies";
        }

        @GetMapping("/nuevo")
        public String nuevo(Model model) {
            model.addAttribute("especie", new HashMap<>()); // ✔ چون Map استفاده می‌کنی
            return "especie_form";
        }

        @GetMapping("/editar/{id}")
        public String editar(@PathVariable Long id, Model model) {

            Map<String, Object> especie = vidapetService.obtenerPorId(id);

            model.addAttribute("especie", especie);

            return "especie_form";
        }

        // ===== GUARDAR =====
        @PostMapping("/guardar")
        public String guardar(@RequestParam String nombre,
                              @RequestParam("archivoFoto") MultipartFile archivoFoto,
                              RedirectAttributes ra) throws Exception {

            if (nombre == null || nombre.isBlank()) {
                ra.addFlashAttribute("error", "Nombre obligatorio");
                return "redirect:/especies/nuevo";
            }

            String path = handleFileUpload(archivoFoto);

            vidapetService.guardarEspecie(nombre, path);

            return "redirect:/especies";
        }



        // ===== ACTUALIZAR =====
        @PostMapping("/actualizar")
        public String actualizar(@RequestParam Long id,
                                 @RequestParam String nombre,
                                 @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto)
                throws Exception {

            String path = handleFileUpload(archivoFoto);

            vidapetService.actualizarEspecie(id, nombre, path);

            return "redirect:/especies";
        }

        // ===== ELIMINAR =====
        @GetMapping("/eliminar/{id}")
        public String eliminar(@PathVariable Long id) {
            vidapetService.eliminarEspecie(id);
            return "redirect:/especies";
        }

        // ===== FILE UPLOAD (helper) =====
        private String handleFileUpload(MultipartFile file) throws Exception {
            if (file == null || file.isEmpty()) return null;

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String path = "uploads/" + fileName;

            file.transferTo(new java.io.File(path));

            return path;
        }
    }
    // ======================================================
    // 🛠 متدهای کمکی (HELPER METHODS) - برای جلوگیری از تکرار کد
    // ======================================================
    private String handleFileUpload(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) return "";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
        Files.write(uploadDir.resolve(fileName), file.getBytes());
        return "uploads/" + fileName;
    }

    private void saveTratamientosList(int consultaId, List<String> treatments, List<LocalDate> starts, List<LocalDate> ends, List<String> notes) {
        if (treatments != null) {
            for (int i = 0; i < treatments.size(); i++) {
                vidapetService.guardarTratamiento(consultaId, treatments.get(i),
                        starts != null ? starts.get(i) : null,
                        ends != null ? ends.get(i) : null,
                        notes != null ? notes.get(i) : null);
            }
        }
    }
    // اضافه کردن در انتهای VidapetController
    @GetMapping("/api/mascotas/{id}/propietario")
    @ResponseBody // این خط بسیار مهم است (چون صفحه وب برنمی‌گرداند، فقط دیتا می‌دهد)
    public Map<String, Object> getPropietarioByMascota(@PathVariable Long id) {
        // شما باید این متد را در سرویس خود داشته باشید
        return vidapetService.obtenerPropietarioPorMascota(id);
    }
}