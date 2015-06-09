package edu.gcsc.vrl.neuro;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.Selection;
import eu.mihosoft.vrl.types.SelectionInputType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import groovy.lang.Script;
import java.awt.Component;
import java.util.ArrayList;

/**
 * <p>
 * Index selection type.
 * Can be used to define selection representations that only return the selected
 * index instead of the selected value.
 * </p>
 */
@TypeInfo(type=Integer.class, input = true, output = false, style="indexSelection")
public class IntegerIndexSelectionInputType extends SelectionInputType {

    public IntegerIndexSelectionInputType() {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.PAGE_AXIS);
        setLayout(layout);

        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        getSelectionView().setAlignmentX(Component.LEFT_ALIGNMENT);

        add(nameLabel);
        add(getSelectionView());

        setValueName("");

        setHideConnector(true);
    }

    @Override
    public Object getViewValue() {
        if (!getMainCanvas().isSavingSession()) {
            if (getSelectionView().getSelectedItem() != null) {
                return getSelectionView().getSelectedIndex();
            }
        } else {
            return super.getViewValue();
        }
        return null;
    }

    @Override
    protected void evaluationRequest(Script script) {
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("value")) {
                property = script.getProperty("value");
            }

            if (property != null) {
                if (getViewValueWithoutValidation() == null) {
                    super.setViewValue(new Selection((ArrayList<?>) property));
                }
            }
        }
    }
    
    @Override()
    public String getValueAsCode() {
        String result = "null";

        Object o = getValue();

        if (o != null) {
            result = o.toString();
        }

        return result;
    }
}
