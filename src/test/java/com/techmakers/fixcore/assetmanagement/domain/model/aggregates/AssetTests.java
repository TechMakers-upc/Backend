package com.techmakers.fixcore.assetmanagement.domain.model.aggregates;

import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetCategory;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetDetails;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetStatus;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.Criticality;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetTests {

    private static final Instant REGISTERED = Instant.parse("2026-10-01T10:00:00Z");

    private final Asset asset = new Asset(1L, details(" cmp-01 "), REGISTERED);

    @Test
    void shouldStartOperationalWithNormalizedCode() {
        assertThat(asset.getStatus()).isEqualTo(AssetStatus.OPERATIONAL);
        assertThat(asset.getDetails().code()).isEqualTo("CMP-01");
    }

    @Test
    void shouldOnlyTrackStatusChangesThatHappen() {
        var stopped = Instant.parse("2026-10-05T23:00:00Z");
        asset.markDown(stopped);
        asset.markDown(Instant.parse("2026-10-06T08:00:00Z"));

        assertThat(asset.getStatus()).isEqualTo(AssetStatus.DOWN);
        assertThat(asset.getStatusChangedAt()).isEqualTo(stopped);
    }

    @Test
    void shouldAddAndRemoveManuals() {
        asset.addManual("Manual de servicio", "https://fixcore.demo/manual.pdf", REGISTERED);

        assertThat(asset.getManuals()).hasSize(1);
        assertThatThrownBy(() -> asset.addManual("Sin link", "ftp://fixcore.demo", REGISTERED))
                .isInstanceOf(IllegalArgumentException.class);

        asset.removeManual(0);
        assertThat(asset.getManuals()).isEmpty();
        assertThatThrownBy(() -> asset.removeManual(0)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldRequireCodeAndName() {
        assertThatThrownBy(() -> details(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An asset needs a code and a name");
    }

    private static AssetDetails details(String code) {
        return new AssetDetails(code, "Compresor de tornillo", AssetCategory.COMPRESSOR, Criticality.HIGH,
                "Sala de compresores", "Atlas Copco", "GA 22", "AC-77310", null, "");
    }
}
