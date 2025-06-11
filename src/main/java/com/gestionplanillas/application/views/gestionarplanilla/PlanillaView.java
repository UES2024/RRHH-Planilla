package com.gestionplanillas.application.views.gestionarplanilla;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PermitAll
@PageTitle("Módulo de Planillas")
@Route("planillas")
@Menu(order = 3, icon = LineAwesomeIconUrl.MONEY_BILL_WAVE_SOLID)
@SpringComponent
@UIScope
public class PlanillaView extends Composite<VerticalLayout>{
    public PlanillaView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().addClassName("planilla-main-view");
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H2 title = new H2("Gestión de Planillas");
        title.getStyle().set("margin-bottom", "2em");
        getContent().add(title);

        VerticalLayout buttonsLayout = new VerticalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        RouterLink registrarPlanillaLink = new RouterLink("", RegistrarPlanillaView.class);
        Button registrarPlanillaButton = new Button("Generar Nueva Planilla");
        registrarPlanillaButton.setWidth("250px");
        registrarPlanillaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registrarPlanillaButton.setIcon(new Icon(LineAwesomeIconUrl.PLUS_SOLID));
        registrarPlanillaLink.add(registrarPlanillaButton);

        RouterLink consultarPlanillasLink = new RouterLink("", ConsultarPlanillasView.class);
        Button consultarPlanillasButton = new Button("Consultar Planillas Existentes");
        consultarPlanillasButton.setWidth("250px");
        consultarPlanillasButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        consultarPlanillasButton.setIcon(new Icon(LineAwesomeIconUrl.SEARCH_SOLID));
        consultarPlanillasLink.add(consultarPlanillasButton);

        buttonsLayout.add(registrarPlanillaLink, consultarPlanillasLink);

        getContent().add(buttonsLayout);
    }
}
