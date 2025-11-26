package com.opencu.bookit.application.service.area;

import com.opencu.bookit.application.port.out.area.DeleteAreaPort;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.area.SaveAreaPort;
import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @Mock private LoadAreaPort loadAreaPort;
    @Mock private SaveAreaPort saveAreaPort;
    @Mock private DeleteAreaPort deleteAreaPort;

    @InjectMocks private AreaService areaService;

    private UUID areaId;

    @BeforeEach
    void setUp() {
        areaId = UUID.randomUUID();
    }

    @Test
    @DisplayName("findAreaNameById returns name when area exists, else throws")
    void findAreaNameById_happy_and_missing() {
        AreaModel model = new AreaModel();
        model.setId(areaId);
        model.setName("Room A");
        when(loadAreaPort.findById(areaId)).thenReturn(Optional.of(model));

        assertEquals("Room A", areaService.findAreaNameById(areaId));
        verify(loadAreaPort).findById(areaId);

        UUID missing = UUID.randomUUID();
        when(loadAreaPort.findById(missing)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> areaService.findAreaNameById(missing));
    }

    @Test
    @DisplayName("createArea builds model and delegates to save port")
    void createArea_saves() {
        List<AreaFeature> features = List.of();
        List<String> keys = List.of("k1","k2");
        AreaModel saved = new AreaModel();
        saved.setId(areaId);

        when(saveAreaPort.save(any(AreaModel.class))).thenReturn(saved);

        AreaModel result = areaService.createArea(
                "Name","Desc", AreaType.MEETING_ROOM, features, keys, 10, AreaStatus.AVAILABLE
        );

        assertSame(saved, result);
        verify(saveAreaPort).save(argThat(m ->
                m.getName().equals("Name") && m.getDescription().equals("Desc") &&
                m.getType() == AreaType.MEETING_ROOM && m.getCapacity() == 10 &&
                m.getKeys().equals(keys) && m.getStatus() == AreaStatus.AVAILABLE
        ));
    }

    @Test
    @DisplayName("updateById updates fields and saves; throws if missing")
    void updateById_updates_orThrows() {
        AreaModel existing = new AreaModel();
        existing.setId(areaId);
        existing.setName("Old");
        when(loadAreaPort.findById(areaId)).thenReturn(Optional.of(existing));
        when(saveAreaPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AreaModel updated = areaService.updateById(areaId, "New", AreaType.LECTURE_HALL, List.of("k"), 20);

        assertEquals("New", updated.getName());
        assertEquals(AreaType.LECTURE_HALL, updated.getType());
        assertEquals(20, updated.getCapacity());
        assertEquals(List.of("k"), updated.getKeys());

        UUID missing = UUID.randomUUID();
        when(loadAreaPort.findById(missing)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> areaService.updateById(missing, "N", AreaType.WORKPLACE, List.of(), 1));
    }

    @Test
    @DisplayName("findWithFilters delegates to port")
    void findWithFilters_delegates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AreaModel> page = new PageImpl<>(List.of());
        when(loadAreaPort.findWithFilters("A", AreaType.WORKPLACE, pageable)).thenReturn(page);

        assertSame(page, areaService.findWithFilters("A", AreaType.WORKPLACE, pageable));
        verify(loadAreaPort).findWithFilters("A", AreaType.WORKPLACE, pageable);
    }

    @Test
    @DisplayName("deleteById delegates to delete port")
    void deleteById_delegates() {
        areaService.deleteById(areaId);
        verify(deleteAreaPort).deleteById(areaId);
    }
}

