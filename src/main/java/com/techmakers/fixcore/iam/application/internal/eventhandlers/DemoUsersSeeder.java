package com.techmakers.fixcore.iam.application.internal.eventhandlers;

import com.techmakers.fixcore.iam.domain.model.commands.SignUpCommand;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.domain.repositories.UserRepository;
import com.techmakers.fixcore.iam.domain.services.UserCommandService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-users.enabled", havingValue = "true")
public class DemoUsersSeeder {

    private record DemoUser(String email, String fullName, Roles role, Long plantId, String jobTitle) {
    }

    private static final List<DemoUser> DEMO_USERS = List.of(
            new DemoUser("carla.garcia@fixcore.demo", "Carla García", Roles.PLANT_MANAGER, 1L, "Jefa de Planta"),
            new DemoUser("victor.salazar@fixcore.demo", "Víctor Salazar", Roles.OPERATIONS_MANAGER, null, "Gerente de Operaciones"),
            new DemoUser("jose.ramirez@fixcore.demo", "José Ramírez", Roles.TECHNICIAN, 1L, "Técnico electromecánico"),
            new DemoUser("miguel.torres@fixcore.demo", "Miguel Ángel Torres", Roles.TECHNICIAN, 1L, "Técnico mecánico"),
            new DemoUser("rosa.quispe@fixcore.demo", "Rosa Quispe", Roles.TECHNICIAN, 2L, "Técnica eléctrica"),
            new DemoUser("jairo.mendoza@fixcore.demo", "Jairo Mendoza", Roles.PLANT_MANAGER, 2L, "Jefe de Planta"),
            new DemoUser("ana.paredes@fixcore.demo", "Ana Lucía Paredes", Roles.PLANT_MANAGER, 3L, "Jefa de Planta"),
            new DemoUser("luis.huaman@fixcore.demo", "Luis Huamán", Roles.TECHNICIAN, 3L, "Técnico hidráulico"));

    private final UserRepository userRepository;
    private final UserCommandService userCommandService;
    private final String demoPassword;

    public DemoUsersSeeder(UserRepository userRepository, UserCommandService userCommandService,
                           @Value("${fixcore.seed.demo-users.password}") String demoPassword) {
        this.userRepository = userRepository;
        this.userCommandService = userCommandService;
        this.demoPassword = demoPassword;
    }

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoUsers() {
        DEMO_USERS.stream()
                .filter(demoUser -> !userRepository.existsByEmail(new EmailAddress(demoUser.email())))
                .forEach(demoUser -> userCommandService.handle(new SignUpCommand(
                        new EmailAddress(demoUser.email()), demoPassword, new FullName(demoUser.fullName()),
                        demoUser.jobTitle(), demoUser.plantId(), demoUser.role())));
    }
}
