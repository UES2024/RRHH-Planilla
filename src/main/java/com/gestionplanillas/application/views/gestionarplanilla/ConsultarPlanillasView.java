package com.gestionplanillas.application.views.gestionarplanilla;
import com.gestionplanillas.application.data.Planilla;
import com.gestionplanillas.application.data.RegistroPlanilla;
import com.gestionplanillas.application.services.PlanillaService; // Inyectar el servicio
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField; // filtro de ID de planilla
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink; // navegar a la vista de registro
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.vaadin.lineawesome.LineAwesomeIconUrl;


@PermitAll
@PageTitle("Consultar Planillas")
@Route("consultar-planillas")
@Menu(order = 3, icon = LineAwesomeIconUrl.MONEY_BILL_WAVE_SOLID)
@Uses(Icon.class)
public class ConsultarPlanillasView extends Composite<VerticalLayout> {
    private final PlanillaService planillaService;

    // Componentes para los filtros
    private TextField idPlanillaFilter;
    private DatePicker fechaInicioCorteFilter;
    private DatePicker fechaFinCorteFilter;
    private Button buscarButton;
    private Button limpiarFiltrosButton;

    // Botón para generar nueva planilla
    private Button generarNuevaPlanillaButton;

    // Grid para mostrar las planillas
    private Grid<Planilla> grid = new Grid<>(Planilla.class);


    public ConsultarPlanillasView(PlanillaService planillaService) {
        this.planillaService = planillaService;

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().addClassName("consultar-planillas-view");
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        H3 viewTitle = new H3("Consulta y Gestión de Planillas");
        viewTitle.getStyle().set("margin-top", "2em");
        getContent().add(viewTitle);

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidthFull();
        filterLayout.addClassName(Gap.MEDIUM);
        filterLayout.setWidth("100%");
        filterLayout.setHeight("min-content");
        filterLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        filterLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        idPlanillaFilter = new TextField("ID Planilla");
        idPlanillaFilter.setPlaceholder("Filtrar por ID");
        idPlanillaFilter.setWidth("min-content");

        fechaInicioCorteFilter = new DatePicker("Fecha Inicio de Corte");
        fechaInicioCorteFilter.setWidth("min-content");

        fechaFinCorteFilter = new DatePicker("Fecha Fin de Corte");
        fechaFinCorteFilter.setWidth("min-content");

        buscarButton = new Button("Buscar");
        buscarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buscarButton.addClickListener(e -> updateList());

        limpiarFiltrosButton = new Button("Limpiar Filtros");
        limpiarFiltrosButton.addClickListener(e -> clearFilters());

        filterLayout.add(idPlanillaFilter, fechaInicioCorteFilter, fechaFinCorteFilter, buscarButton, limpiarFiltrosButton);
        getContent().add(filterLayout);

        HorizontalLayout generateButtonLayout = new HorizontalLayout();
        generateButtonLayout.setWidthFull();
        generateButtonLayout.addClassName(Gap.MEDIUM);
        generateButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        RouterLink registrarLink = new RouterLink("", RegistrarPlanillaView.class);
        generarNuevaPlanillaButton = new Button("Generar Nueva Planilla");
        generarNuevaPlanillaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generarNuevaPlanillaButton.setIcon(new Icon(LineAwesomeIconUrl.PLUS_SOLID));
        registrarLink.add(generarNuevaPlanillaButton);

        generateButtonLayout.add(registrarLink);
        getContent().add(generateButtonLayout);

        H5 h5GridTitle = new H5("Resultados de la Búsqueda de Planillas");
        h5GridTitle.setWidth("max-content");
        getContent().add(h5GridTitle);


        configureGrid();
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "1");
        getContent().add(grid);

        // Cargar datos iniciales
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("planilla-grid");
        grid.setColumns("idPlanilla", "fecha_inicio_corte", "fecha_fin_corte");

        // Columna de acciones para ver detalles de la planilla
        grid.addComponentColumn(planilla -> {
            Button viewDetails = new Button("Ver Detalles");
            viewDetails.addThemeVariants(ButtonVariant.LUMO_SMALL);
            viewDetails.addClickListener(e -> showPlanillaDetailsDialog(planilla));
            return viewDetails;
        }).setHeader("Acciones").setAutoWidth(true).setFlexGrow(0); // No permitir que crezca el ancho de esta columna

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(query -> {
            Specification<Planilla> spec = (root, query1, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (!idPlanillaFilter.isEmpty()) {
                    try {
                        Long id = Long.parseLong(idPlanillaFilter.getValue());
                        predicates.add(cb.equal(root.get("idPlanilla"), id));
                    } catch (NumberFormatException e) {
                        Notification.show("ID de Planilla inválido. Por favor, introduce un número.", 3000, Notification.Position.MIDDLE);
                        return cb.and(cb.disjunction());
                    }
                }
                if (fechaInicioCorteFilter.getValue() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("fecha_inicio_corte"), Date.valueOf(fechaInicioCorteFilter.getValue())));
                }
                if (fechaFinCorteFilter.getValue() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("fecha_fin_corte"), Date.valueOf(fechaFinCorteFilter.getValue())));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };

            PageRequest pageable = PageRequest.of(
                    query.getPage(),
                    query.getPageSize(),
                    VaadinSpringDataHelpers.toSpringDataSort(query)
            );
            return planillaService.listPlanillas(pageable, spec).stream();
        });
    }

    private void clearFilters() {
        idPlanillaFilter.clear();
        fechaInicioCorteFilter.clear();
        fechaFinCorteFilter.clear();
        updateList();
    }

    private void showPlanillaDetailsDialog(Planilla planilla) {

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.setWidth("90%");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        dialogLayout.add(new H3("Detalles de Planilla ID: " + planilla.getIdPlanilla()));
        dialogLayout.add(new Span("Fecha Inicio Corte: " + planilla.getFecha_inicio_corte()));
        dialogLayout.add(new Span("Fecha Fin Corte: " + planilla.getFecha_fin_corte()));

        // Grid para mostrar los RegistroPlanilla asociados a esta planilla
        Grid<RegistroPlanilla> registroGrid = new Grid<>(RegistroPlanilla.class);

        // --- INICIO DE LA MODIFICACIÓN DE COLUMNAS DEL REGISTROGRID ---
        registroGrid.setColumns(
                // Información del empleado
                "idRegistroPlanilla",
                "contratoEmpleado.empleado.nombres",
                "contratoEmpleado.empleado.apellidos",
                "contratoEmpleado.salarioBaseMensual", // Salario Base Mensual del contrato

                // Cálculos de la planilla (directamente de RegistroPlanilla)
                "salarioBasePeriodo",       // Salario Base para el periodo de la planilla
                "montoHoraExtrasDiurnas",   // Monto de Horas Extra Diurnas
                "montoHorasExtrasNocturnas",// Monto de Horas Extra Nocturnas
                "totalDevengado",           // Total Devengado (Salario Bruto)

                // Deducciones de ley
                "isssEmpleado",             // ISSS del Empleado
                "afpEmpleado",              // AFP del Empleado
                "renta",                    // Impuesto sobre la Renta
                "totalDeducciones",         // Total de Deducciones

                // Salario Neto y Contribuciones Patronales
                "salarioNeto",              // Salario Neto a pagar al empleado
                "isssPatrono",              // ISSS del Patrono
                "afpPatrono",                // AFP del Patrono
                // Puedes añadir "aguinaldo" y "vacacion" si los estuvieras calculando y mostrando
                 "aguinaldo",
                 "vacacion"
        );

        // Ajustar los headers para que sean más legibles
       // registroGrid.addColumn(RegistroPlanilla::getIdRegistroPlanilla).setHeader("ID Planilla").setAutoWidth(true);
        registroGrid.getColumnByKey("idRegistroPlanilla").setHeader("ID Planilla");
        registroGrid.getColumnByKey("contratoEmpleado.empleado.nombres").setHeader("Nombres Empleado");
        registroGrid.getColumnByKey("contratoEmpleado.empleado.apellidos").setHeader("Apellidos Empleado");

        registroGrid.getColumnByKey("contratoEmpleado.salarioBaseMensual").setHeader("Salario Mensual");
        registroGrid.getColumnByKey("salarioBasePeriodo").setHeader("Salario Base Periodo");
        registroGrid.getColumnByKey("montoHoraExtrasDiurnas").setHeader("Monto H.E. Diurnas");
        registroGrid.getColumnByKey("montoHorasExtrasNocturnas").setHeader("Monto H.E. Nocturnas");
        registroGrid.getColumnByKey("totalDevengado").setHeader("Total Devengado");
        registroGrid.getColumnByKey("isssEmpleado").setHeader("ISSS Empleado");
        registroGrid.getColumnByKey("afpEmpleado").setHeader("AFP Empleado");
        registroGrid.getColumnByKey("renta").setHeader("Renta");
        registroGrid.getColumnByKey("totalDeducciones").setHeader("Total Deducciones");
        registroGrid.getColumnByKey("salarioNeto").setHeader("Salario Neto");
        registroGrid.getColumnByKey("isssPatrono").setHeader("ISSS Patrono");
        registroGrid.getColumnByKey("afpPatrono").setHeader("AFP Patrono");
        registroGrid.getColumnByKey("aguinaldo").setHeader("Aguinaldo");
        registroGrid.getColumnByKey("vacacion").setHeader("Vacación");

        Grid.Column<RegistroPlanilla> accionColumn = registroGrid.addComponentColumn(registro -> {
            Button imprimirButton = new Button("Imprimir Boleta");
            imprimirButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            imprimirButton.setIcon(new Icon(VaadinIcon.PRINT));
            imprimirButton.addClickListener(event -> {
                imprimirButton.getElement().executeJs(
                        "window.open($0, '_blank');",
                        "/boleta-pdf/" + registro.getIdRegistroPlanilla()
                );
            });
            return imprimirButton;
        }).setHeader("Acción").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);


        // Opcional: Auto-ajustar ancho de todas las columnas y permitir el desplazamiento
        registroGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        registroGrid.setAllRowsVisible(true); // Opcional: mostrar todas las filas si el volumen es bajo, si no, usa paginación en el grid

        // --- FIN DE LA MODIFICACIÓN ---

        Optional<Planilla> detailedPlanilla = planillaService.getPlanillaWithDetails(planilla.getIdPlanilla());

        if (detailedPlanilla.isPresent()) {
            Planilla currentPlanilla = detailedPlanilla.get();
            registroGrid.setItems(currentPlanilla.getRegistrosPlanilla());
        } else {
            Notification.show("No se pudieron cargar los detalles completos de la planilla.", 3000, Notification.Position.MIDDLE);

            dialog.close();
            return;
        }

        //registroGrid.setSizeFull();
        registroGrid.setWidthFull();
        registroGrid.setHeight("400px");

        dialogLayout.add(new H4("Registros de Empleados"));
        dialogLayout.add(registroGrid);

        Button closeButton = new Button("Cerrar", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialogLayout.add(new HorizontalLayout(closeButton));
        dialogLayout.setAlignSelf(FlexComponent.Alignment.END, closeButton);

        dialog.add(dialogLayout);
        dialog.open();
    }
}
