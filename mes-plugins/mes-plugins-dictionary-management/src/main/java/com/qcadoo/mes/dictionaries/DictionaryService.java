package com.qcadoo.mes.dictionaries;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.CustomRestriction;
import com.qcadoo.model.api.search.Restrictions;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.components.grid.GridComponentState;

@Service
public class DictionaryService {

    @Autowired
    com.qcadoo.model.api.DictionaryService dictionaryService;

    public void translateLabel(final ViewDefinitionState state) {
        GridComponentState grid = (GridComponentState) state.getComponentByReference("grid");

        List<Entity> entities = grid.getEntities();

        for (Entity entity : entities) {
            entity.setField("name", dictionaryService.translate(entity.getStringField("name"), state.getLocale()));
        }
    }

    public void addRestrictionToDictionariesGrid(final ViewDefinitionState viewDefinitionState) {
        GridComponentState grid = (GridComponentState) viewDefinitionState.getComponentByReference("grid");
        grid.setCustomRestriction(new CustomRestriction() {

            @Override
            public void addRestriction(final SearchCriteriaBuilder searchCriteriaBuilder) {
                searchCriteriaBuilder.addRestriction(Restrictions.eq("active", true));
            }
        });
    }

}