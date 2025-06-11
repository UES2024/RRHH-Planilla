package com.gestionplanillas.application.views.gestionarplanilla;
import com.gestionplanillas.application.data.Planilla;
import com.gestionplanillas.application.services.PlanillaService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;

@PermitAll
@Route("registrar-planillas")
@PageTitle("Registrar Planillas | Gestión de Planillas")
@SpringComponent
@UIScope
public class RegistrarPlanillaView extends Composite<VerticalLayout>{
    private final PlanillaService planillaService;

    private DatePicker fechaInicioCorte;
    private DatePicker fechaFinCorte;
    private Button generarPlanillaButton;

    public RegistrarPlanillaView(PlanillaService planillaService) {
        this.planillaService = planillaService;

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().addClassName("registrar-planilla-view");
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        H3 viewTitle = new H3("Generar Nueva Planilla por Período de Corte");
        viewTitle.getStyle().set("margin-top", "2em");
        getContent().add(viewTitle);

        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.addClassName("form-toolbar");
        formLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        formLayout.setSpacing(true);

        fechaInicioCorte = new DatePicker("Fecha Inicio de Corte");
        fechaInicioCorte.setRequiredIndicatorVisible(true);
        fechaInicioCorte.setWidth("min-content");

        fechaFinCorte = new DatePicker("Fecha Fin de Corte");
        fechaFinCorte.setRequiredIndicatorVisible(true);
        fechaFinCorte.setWidth("min-content");

        generarPlanillaButton = new Button("Generar Planilla");
        generarPlanillaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generarPlanillaButton.addClickListener(e -> generarPlanilla());

        formLayout.add(fechaInicioCorte, fechaFinCorte, generarPlanillaButton);
        getContent().add(formLayout);
    }

    /**
     * Lógica para generar la planilla cuando se hace clic en el botón.
     */
    private void generarPlanilla() {
        LocalDate inicio = fechaInicioCorte.getValue();
        LocalDate fin = fechaFinCorte.getValue();

        // Validaciones básicas de las fechas
        if (inicio == null || fin == null) {
            Notification.show("Por favor, selecciona ambas fechas para el corte de planilla.", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (inicio.isAfter(fin)) {
            Notification.show("La fecha de inicio no puede ser posterior a la fecha de fin.", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Deshabilitar el botón para evitar múltiples envíos mientras se procesa
        generarPlanillaButton.setEnabled(false);
        try {
            // Llamar al servicio para generar la planilla
            Planilla planillaGenerada = planillaService.generarPlanilla(inicio, fin);

            // Mostrar notificación de éxito al usuario
            Notification.show("Planilla generada exitosamente con ID: " + planillaGenerada.getIdPlanilla(), 3000, Notification.Position.MIDDLE);

            // Limpiar los campos del formulario después de una generación exitosa
            fechaInicioCorte.clear();
            fechaFinCorte.clear();

        } catch (Exception e) {
            // Mostrar notificación de error si ocurre una excepcion
            Notification.show("Error al generar la planilla: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            e.printStackTrace(); // en produccionn usar un logger
        } finally {
            generarPlanillaButton.setEnabled(true);
        }
    }
}
