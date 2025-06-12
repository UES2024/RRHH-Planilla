package com.gestionplanillas.application.views.viewcontrolempleados;

import com.gestionplanillas.application.data.Cargo;
import com.gestionplanillas.application.data.ContratoEmpleado;
import com.gestionplanillas.application.data.Empleado;
import com.gestionplanillas.application.dto.EmpleadoDTO;
import com.gestionplanillas.application.repository.CargoRepository;
import com.gestionplanillas.application.repository.EmpleadoRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.math.BigDecimal;
import java.time.LocalDate;

@PageTitle("View Control Empleados")
@Route("")
@Menu(order = 1, icon = LineAwesomeIconUrl.ADDRESS_BOOK_SOLID)
@PermitAll
@Uses(Icon.class)
public class ViewControlEmpleadosView extends Composite<VerticalLayout> {

    private final EmpleadoRepository empleadoRepository;
    private final CargoRepository cargoRepository;
    private final Grid<EmpleadoDTO> grid = new Grid<>(EmpleadoDTO.class, false);

    @Autowired
    public ViewControlEmpleadosView(CargoRepository cargoRepository, EmpleadoRepository empleadoRepository) {
        this.cargoRepository = cargoRepository;
        this.empleadoRepository = empleadoRepository;

        HorizontalLayout layoutFiltros = new HorizontalLayout();
        HorizontalLayout layoutTabla = new HorizontalLayout();

        TextField tfNombreFiltro = new TextField("Filtrar por Nombre");
        ComboBox<Cargo> cbPuestoFiltro = new ComboBox<>("Filtrar por Puesto");
        cbPuestoFiltro.setItems(cargoRepository.findAll());
        cbPuestoFiltro.setItemLabelGenerator(Cargo::getNombreCargo);

        Button btnAplicar = new Button("Aplicar Filtros");
        Button btnLimpiar = new Button("Limpiar Filtros");

        Button btnAgregar = new Button("Agregar Nuevo Empleado", new Icon("plus"));
        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        btnAgregar.addClickListener(e -> abrirDialogoNuevoEmpleado());

        layoutFiltros.add(tfNombreFiltro, cbPuestoFiltro, btnAplicar, btnLimpiar, btnAgregar);

        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(EmpleadoDTO::getIdEmpleado).setHeader("Código");
        grid.addColumn(EmpleadoDTO::getNombres).setHeader("Nombre");
        grid.addColumn(EmpleadoDTO::getApellidos).setHeader("Apellido");
        grid.addColumn(EmpleadoDTO::getNombreCargo).setHeader("Cargo");
        grid.addColumn(EmpleadoDTO::getFechaContrato).setHeader("Fecha de Contrato");
        grid.addColumn(EmpleadoDTO::getSalario).setHeader("Salario");

        grid.addComponentColumn(empleado -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
            editButton.getElement().setProperty("title", "Editar");
            editButton.addClickListener(ev -> abrirDialogoEditarEmpleado(empleado.getIdEmpleado()));
            return editButton;
        }).setHeader("Acciones").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);

        layoutTabla.add(grid);
        layoutTabla.setWidthFull();

        btnAplicar.addClickListener(e -> {
            String nombre = tfNombreFiltro.getValue();
            Cargo cargo = cbPuestoFiltro.getValue();
            Long idCargo = (cargo != null) ? cargo.getIdCargo() : null;
            grid.setItems(empleadoRepository.buscarEmpleados(nombre, idCargo));
        });

        btnLimpiar.addClickListener(e -> {
            tfNombreFiltro.clear();
            cbPuestoFiltro.clear();
            grid.setItems(empleadoRepository.buscarEmpleados(null, null));
        });

        getContent().add(layoutFiltros, layoutTabla);
        grid.setItems(empleadoRepository.buscarEmpleados(null, null));
    }

    private void abrirDialogoNuevoEmpleado() {
        Dialog dialog = new Dialog("Nuevo Empleado");

        TextField nombreField = new TextField("Nombres");
        TextField apellidoField = new TextField("Apellidos");

        Button btnGuardar = new Button("Guardar", e -> {
            String nombre = nombreField.getValue();
            String apellido = apellidoField.getValue();

            if (nombre.isBlank() || apellido.isBlank()) {
                Notification.show("Nombre y Apellido son obligatorios");
                return;
            }

            Empleado emp = new Empleado();
            emp.setNombres(nombre);
            emp.setApellidos(apellido);
            empleadoRepository.save(emp);

            Notification.show("Empleado guardado");
            grid.setItems(empleadoRepository.buscarEmpleados(null, null));
            dialog.close();
        });

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());

        dialog.add(new VerticalLayout(nombreField, apellidoField, new HorizontalLayout(btnGuardar, btnCancelar)));
        dialog.open();
    }

    private void abrirDialogoEditarEmpleado(Long idEmpleado) {
        Dialog dialog = new Dialog("Editar Empleado");

        Empleado emp = empleadoRepository.findById(idEmpleado).orElse(null);
        if (emp == null) {
            Notification.show("Empleado no encontrado");
            return;
        }

        // Usar un array como wrapper para que sea effectively final
        final ContratoEmpleado[] contratoWrapper = new ContratoEmpleado[1];
        contratoWrapper[0] = emp.getContratoEmpleado();

        TextField tfNombre = new TextField("Nombres", emp.getNombres());
        TextField tfApellido = new TextField("Apellidos", emp.getApellidos());
        ComboBox<Cargo> cbCargo = new ComboBox<>("Cargo");
        cbCargo.setItems(cargoRepository.findAll());
        cbCargo.setItemLabelGenerator(Cargo::getNombreCargo);
        cbCargo.setValue(contratoWrapper[0] != null ? contratoWrapper[0].getCargo() : null);

        DatePicker dpFecha = new DatePicker("Fecha Contrato");
        dpFecha.setValue((contratoWrapper[0] != null && contratoWrapper[0].getFechaContrato() != null)
                ? contratoWrapper[0].getFechaContrato().toLocalDate() : null);

        TextField tfSalario = new TextField("Salario");
        tfSalario.setValue((contratoWrapper[0] != null && contratoWrapper[0].getSalarioBaseMensual() != null)
                ? contratoWrapper[0].getSalarioBaseMensual().toString() : "");

        Button btnGuardar = new Button("Guardar", e -> {
            String nuevoNombre = tfNombre.getValue();
            String nuevoApellido = tfApellido.getValue();

            if (nuevoNombre != null && !nuevoNombre.isBlank()) {
                emp.setNombres(nuevoNombre);
            }

            if (nuevoApellido != null && !nuevoApellido.isBlank()) {
                emp.setApellidos(nuevoApellido);
            }
            empleadoRepository.save(emp);

            if (contratoWrapper[0] == null) {
                contratoWrapper[0] = new ContratoEmpleado();
                contratoWrapper[0].setEmpleado(emp);
            }

            contratoWrapper[0].setCargo(cbCargo.getValue());
            LocalDate fecha = dpFecha.getValue();
            contratoWrapper[0].setFechaContrato(fecha != null ? fecha.atStartOfDay() : null);
            contratoWrapper[0].setSalarioBaseMensual(new BigDecimal(tfSalario.getValue()));
            emp.setContratoEmpleado(contratoWrapper[0]);

            empleadoRepository.save(emp);
            Notification.show("Empleado actualizado");
            grid.setItems(empleadoRepository.buscarEmpleados(null, null));
            dialog.close();
        });

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());

        dialog.add(new VerticalLayout(tfNombre, tfApellido, cbCargo, dpFecha, tfSalario,
                new HorizontalLayout(btnGuardar, btnCancelar)));
        dialog.open();
    }

}
