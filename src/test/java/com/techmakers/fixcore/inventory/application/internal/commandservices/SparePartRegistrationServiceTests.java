package com.techmakers.fixcore.inventory.application.internal.commandservices;

import com.techmakers.fixcore.iam.application.outboundservices.acl.PlantDirectory;
import com.techmakers.fixcore.inventory.domain.model.commands.CreateSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ReceiveStockCommand;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartCategory;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartUnit;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import com.techmakers.fixcore.inventory.domain.repositories.SparePartRepository;
import com.techmakers.fixcore.inventory.domain.services.SparePartRegistrationService;
import com.techmakers.fixcore.inventory.domain.services.StockCommandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "fixcore.seed.demo-plants.enabled=true")
@ActiveProfiles("test")
class SparePartRegistrationServiceTests {

    @Autowired
    private SparePartRegistrationService registrationService;

    @Autowired
    private SparePartRepository partRepository;

    @Autowired
    private PlantDirectory plantDirectory;

    @MockitoBean
    private StockCommandService stockCommandService;

    @Test
    void shouldRollbackPartWhenInitialStockRegistrationFails() {
        Long plantId = plantDirectory.plantIdByName("Planta Ate")
                .orElseThrow();

        String sku = "TXN-ROLLBACK-63";

        var details = new SparePartDetails(
                sku,
                "Transactional Test Part",
                PartCategory.MECHANICAL,
                PartUnit.UNIT,
                1,
                "Z-01"
        );

        when(stockCommandService.handle(any(ReceiveStockCommand.class)))
                .thenThrow(new IllegalStateException("Simulated stock failure"));

        assertThatThrownBy(() -> registrationService.register(
                new CreateSparePartCommand(plantId, details),
                5,
                "test@fixcore.demo"
        )).isInstanceOf(IllegalStateException.class);

        assertThat(partRepository.existsByPlantIdAndSku(plantId, sku))
                .isFalse();
    }
}
