package com.opencu.bookit.application.service.news;

import com.opencu.bookit.application.port.out.news.DeleteNewsPort;
import com.opencu.bookit.application.port.out.news.LoadNewsPort;
import com.opencu.bookit.application.port.out.news.SaveNewsPort;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock private LoadNewsPort loadNewsPort;
    @Mock private SaveNewsPort saveNewsPort;
    @Mock private DeleteNewsPort deleteNewsPort;

    @InjectMocks private NewsService service;

    private UUID id1;
    private NewsModel existing;

    @BeforeEach
    void setUp() {
        id1 = UUID.randomUUID();
        existing = new NewsModel();
        existing.setId(id1);
        existing.setTitle("Old title");
        existing.setShortDescription("Old short");
        existing.setFullDescription("Old full");
        existing.setTags(new HashSet<>(List.of(ThemeTags.TECHNOLOGY)));
        existing.setKeys(new ArrayList<>(List.of("k1")));

        ReflectionTestUtils.setField(service, "zoneId", ZoneId.of("UTC"));
    }

    @Test
    @DisplayName("findAll delegates to loadNewsPort")
    void findAll_ok() {
        when(loadNewsPort.findAll()).thenReturn(List.of(existing));
        List<NewsModel> list = service.findAll();
        assertEquals(1, list.size());
        assertEquals(id1, list.get(0).getId());
        verify(loadNewsPort).findAll();
        verifyNoMoreInteractions(loadNewsPort, saveNewsPort, deleteNewsPort);
    }

    @Test
    @DisplayName("findByTags delegates and returns matched items")
    void findByTags_ok() {
        Set<ThemeTags> tags = new HashSet<>(List.of(ThemeTags.TECHNOLOGY));
        when(loadNewsPort.findByTags(tags)).thenReturn(List.of(existing));
        List<NewsModel> list = service.findByTags(tags);
        assertEquals(1, list.size());
        assertEquals(id1, list.get(0).getId());
        verify(loadNewsPort).findByTags(tags);
        verifyNoMoreInteractions(loadNewsPort, saveNewsPort, deleteNewsPort);
    }

    @Test
    @DisplayName("findWithFilters delegates and returns page")
    void findWithFilters_ok() {
        Set<ThemeTags> tags = new HashSet<>(List.of(ThemeTags.TECHNOLOGY));
        PageRequest pageable = PageRequest.of(0, 10);
        Page<NewsModel> page = new PageImpl<>(List.of(existing), pageable, 1);
        when(loadNewsPort.findWithFilters(tags, "search", pageable)).thenReturn(page);
        Page<NewsModel> res = service.findWithFilters(tags, "search", pageable);
        assertEquals(1, res.getTotalElements());
        assertEquals(id1, res.getContent().get(0).getId());
        verify(loadNewsPort).findWithFilters(tags, "search", pageable);
        verifyNoMoreInteractions(loadNewsPort, saveNewsPort, deleteNewsPort);
    }

    @Test
    @DisplayName("findById returns existing or throws when not found")
    void findById_behavior() {
        when(loadNewsPort.findById(id1)).thenReturn(Optional.of(existing));
        NewsModel found = service.findById(id1);
        assertEquals(id1, found.getId());
        verify(loadNewsPort).findById(id1);

        UUID missing = UUID.randomUUID();
        when(loadNewsPort.findById(missing)).thenReturn(Optional.empty());
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> service.findById(missing));
        assertTrue(ex.getMessage().contains(missing.toString()));
        verify(loadNewsPort).findById(missing);
        verifyNoMoreInteractions(loadNewsPort);
    }

    @Test
    @DisplayName("delete delegates to deleteNewsPort")
    void delete_ok() {
        UUID toDelete = UUID.randomUUID();
        service.delete(toDelete);
        verify(deleteNewsPort).delete(toDelete);
        verifyNoMoreInteractions(deleteNewsPort);
    }

    @Test
    @DisplayName("updateNews updates fields and persists")
    void updateNews_ok() {
        when(loadNewsPort.findById(id1)).thenReturn(Optional.of(existing));
        NewsModel updated = new NewsModel();
        updated.setId(id1);
        when(saveNewsPort.save(any(NewsModel.class))).thenReturn(updated);

        List<ThemeTags> newTags = List.of(ThemeTags.MARKETING, ThemeTags.TECHNOLOGY);
        List<String> newKeys = List.of("k2", "k3");

        NewsModel res = service.updateNews(id1, "New title", "New short", "New full", newTags, newKeys);
        assertEquals(id1, res.getId());

        verify(saveNewsPort).save(argThat(n ->
                n.getTitle().equals("New title") &&
                n.getShortDescription().equals("New short") &&
                n.getFullDescription().equals("New full") &&
                n.getTags().equals(new HashSet<>(newTags)) &&
                n.getKeys().equals(new ArrayList<>(newKeys))
        ));
        verify(loadNewsPort).findById(id1);
        verifyNoMoreInteractions(loadNewsPort, saveNewsPort);
    }

    @Test
    @DisplayName("updateNews throws when not found")
    void updateNews_notFound() {
        UUID missing = UUID.randomUUID();
        when(loadNewsPort.findById(missing)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () ->
                service.updateNews(missing, "t", "s", "f", List.of(ThemeTags.TECHNOLOGY), List.of("k"))
        );
        verify(loadNewsPort).findById(missing);
        verifyNoInteractions(saveNewsPort);
    }

    @Test
    @DisplayName("createNews sets fields and persists with createdAt in zoneId")
    void createNews_ok() {
        List<ThemeTags> tags = List.of(ThemeTags.TECHNOLOGY);
        List<String> keys = List.of("k1", "k2");

        when(saveNewsPort.save(any(NewsModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime before = LocalDateTime.now(ZoneId.of("UTC")).minusSeconds(1);
        NewsModel res = service.createNews("Title", "Short", "Full", tags, keys);
        LocalDateTime after = LocalDateTime.now(ZoneId.of("UTC")).plusSeconds(1);

        assertEquals("Title", res.getTitle());
        assertEquals("Short", res.getShortDescription());
        assertEquals("Full", res.getFullDescription());
        assertEquals(new HashSet<>(tags), res.getTags());
        assertEquals(keys, res.getKeys());
        assertNotNull(res.getCreatedAt());
        assertTrue(!res.getCreatedAt().isBefore(before) && !res.getCreatedAt().isAfter(after), "createdAt should be within now window");

        verify(saveNewsPort).save(any(NewsModel.class));
        verifyNoMoreInteractions(saveNewsPort);
    }
}
