package com.techmakers.fixcore.iam.application.internal.eventhandlers;

import com.techmakers.fixcore.iam.application.outboundservices.acl.PlantDirectory;
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

    private record DemoUser(String email, String fullName, Roles role, String plantName, String jobTitle) {
    }

    private static final List<DemoUser> DEMO_USERS = List.of(
            new DemoUser("carla.garcia@fixcore.demo", "Carla García", Roles.PLANT_MANAGER, "Planta Ate", "Jefa de Planta"),
            new DemoUser("victor.salazar@fixcore.demo", "Víctor Salazar", Roles.OPERATIONS_MANAGER, null, "Gerente de Operaciones"),
            new DemoUser("jose.ramirez@fixcore.demo", "José Ramírez", Roles.TECHNICIAN, "Planta Ate", "Técnico electromecánico"),
            new DemoUser("miguel.torres@fixcore.demo", "Miguel Ángel Torres", Roles.TECHNICIAN, "Planta Ate", "Técnico mecánico"),
            new DemoUser("rosa.quispe@fixcore.demo", "Rosa Quispe", Roles.TECHNICIAN, "Planta Callao", "Técnica eléctrica"),
            new DemoUser("jairo.mendoza@fixcore.demo", "Jairo Mendoza", Roles.PLANT_MANAGER, "Planta Callao", "Jefe de Planta"),
            new DemoUser("ana.paredes@fixcore.demo", "Ana Lucía Paredes", Roles.PLANT_MANAGER, "Planta Arequipa", "Jefa de Planta"),
            new DemoUser("luis.huaman@fixcore.demo", "Luis Huamán", Roles.TECHNICIAN, "Planta Arequipa", "Técnico hidráulico"));

    private final UserRepository userRepository;
    private final UserCommandService userCommandService;
    private final PlantDirectory plantDirectory;
    private final String demoPassword;

    public DemoUsersSeeder(UserRepository userRepository, UserCommandService userCommandService,
                           PlantDirectory plantDirectory, @Value("${fixcore.seed.demo-users.password}") String demoPassword) {
        this.userRepository = userRepository;
        this.userCommandService = userCommandService;
        this.plantDirectory = plantDirectory;
        this.demoPassword = demoPassword;
    }

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoUsers() {
        DEMO_USERS.stream()
                .filter(demoUser -> !userRepository.existsByEmail(new EmailAddress(demoUser.email())))
                .filter(demoUser -> demoUser.plantName() == null || plantIdOf(demoUser) != null)
                .forEach(demoUser -> userCommandService.handle(new SignUpCommand(
                        new EmailAddress(demoUser.email()), demoPassword, new FullName(demoUser.fullName()),
                        demoUser.jobTitle(), plantIdOf(demoUser), demoUser.role())));
    }

    private Long plantIdOf(DemoUser demoUser) {
        return demoUser.plantName() == null ? null : plantDirectory.plantIdByName(demoUser.plantName()).orElse(null);
    }
}
