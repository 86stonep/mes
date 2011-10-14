package com.qcadoo.mes.orders.states;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.orders.constants.OrdersConstants;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;

@Service
public class ChangeStateHook {

    @Autowired
    private OrderStatesChangingService orderStatesService;

    @Autowired
    private OrderStateValidationService orderStateChangingService;

    @Autowired
    DataDefinitionService dataDefinitionService;

    public void changedState(final DataDefinition dataDefinition, final Entity newEntity) {
        checkArgument(newEntity != null, "entity is null");
        if (newEntity.getId() == null) {
            return;
        }
        Entity oldEntity = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).get(
                newEntity.getId());

        if (oldEntity == null) {
            return;
        }
        if (oldEntity.getStringField("state").equals(newEntity.getStringField("state"))) {
            return;
        }
        List<ChangeOrderStateMessage> errors = orderStatesService.performChangeState(newEntity, oldEntity);
        if (errors != null && errors.size() > 0) {
            newEntity.setField("state", oldEntity.getStringField("state"));
            for (ChangeOrderStateMessage error : errors) {
                newEntity.addGlobalError(error.getMessage() + "." + error.getReferenceToField());
            }
            return;
        }

        orderStateChangingService.saveLogging(newEntity, oldEntity.getStringField("state"), newEntity.getStringField("state"));
    }
}
