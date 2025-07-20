package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.AreaMapper;
import com.opencu.bookit.adapter.out.persistence.repository.AreaRepository;
import com.opencu.bookit.application.port.out.area.DeleteAreaPort;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.area.SaveAreaPort;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AreaPersistenceAdapter implements
        LoadAreaPort, SaveAreaPort, DeleteAreaPort {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    @Override
    public List<AreaModel> findByType(AreaType type) {
        List<AreaEntity> areaEntities = areaRepository.findByType(type);
        return areaMapper.toModelList(areaEntities);
    }

    @Override
    public Optional<AreaModel> findById(UUID areaId) {
        return areaRepository.findById(areaId).map(areaMapper::toModel);
    }

    @Override
    public List<AreaModel> findAll() {
        return areaMapper.toModelList(areaRepository.findAll());
    }

    @Override
    public Page<AreaModel> findWithFilters(
            AreaType type,
            Pageable pageable) {
        Specification<AreaEntity> spec = Specification.where(null);
        if (type != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("type"), type));
        }
        return areaRepository.findAll(spec, pageable)
                .map(areaMapper::toModel);
    }

    @Override
    public List<AreaModel> saveAll(List<AreaModel> areaModels) {
        return areaMapper.toModelList(areaRepository.saveAll(areaMapper.toEntityList(areaModels)));
    }

    @Override
    public AreaModel save(AreaModel model) {
        return areaMapper.toModel(areaRepository.save(areaMapper.toEntity(model)));
    }

    @Override
    public void deleteById(UUID areaId) {
        areaRepository.deleteById(areaId);
    }
}

