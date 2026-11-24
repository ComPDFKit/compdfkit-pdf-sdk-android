package com.compdfkit.tools.common.pdf;

import com.compdfkit.core.annotation.CPDFAnnotation;
import com.compdfkit.core.annotation.form.CPDFWidget;
import com.compdfkit.tools.R;
import com.compdfkit.tools.common.interfaces.CPDFCustomEventCallback;
import com.compdfkit.tools.common.pdf.config.CPDFConfiguration;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventCallbackHelper;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventField;
import com.compdfkit.tools.common.utils.customevent.CPDFCustomEventType;
import com.compdfkit.ui.reader.ReaderView;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Reader-owned snapshot of action configuration; never consults global configuration. */
public final class CPDFActionContext {
    private boolean allAnnotations;
    private boolean allWidgets;
    private final Set<CPDFAnnotation.Type> annotations = new HashSet<>();
    private final Set<CPDFWidget.WidgetType> widgets = new HashSet<>();
    private CPDFCustomEventCallback callback;
    private boolean scopedCallback;

    public static CPDFActionContext get(ReaderView reader) {
        return reader == null ? null : (CPDFActionContext) reader.getTag(R.id.tools_action_context);
    }

    public static void bind(ReaderView reader, CPDFConfiguration configuration) {
        CPDFActionContext context = get(reader);
        if (context == null) {
            context = new CPDFActionContext();
            reader.setTag(R.id.tools_action_context, context);
        }
        context.annotations.clear();
        context.widgets.clear();
        context.allAnnotations = false;
        context.allWidgets = false;
        if (configuration == null) return;
        if (configuration.annotationsConfig != null) {
            configuration.annotationsConfig.normalizeActionInterception();
            context.allAnnotations = Boolean.TRUE.equals(configuration.annotationsConfig.interceptAllAnnotationActions);
            context.annotations.addAll(configuration.annotationsConfig.interceptAnnotationActions);
        }
        if (configuration.formsConfig != null) {
            context.allWidgets = configuration.formsConfig.interceptAllFormWidgetActions;
            if (configuration.formsConfig.interceptFormWidgetActions != null) {
                context.widgets.addAll(configuration.formsConfig.interceptFormWidgetActions);
            }
        }
    }

    /** Setting null explicitly enables interception without a business receiver. */
    public void setCallback(CPDFCustomEventCallback callback) {
        this.scopedCallback = true;
        this.callback = callback;
    }

    public boolean intercept(CPDFAnnotation annotation) {
        if (annotation == null) return false;
        boolean widget = annotation instanceof CPDFWidget;
        boolean enabled;
        if (widget) {
            enabled = allWidgets || widgets.contains(((CPDFWidget) annotation).getWidgetType());
        } else if (annotation.getType() == CPDFAnnotation.Type.TEXT
                || annotation.getType() == CPDFAnnotation.Type.LINK) {
            // Annotation interception is reserved for annotations with a default action.
            // Other annotation taps are selection/editing gestures, not business actions.
            enabled = allAnnotations || annotations.contains(annotation.getType());
        } else {
            enabled = false;
        }
        if (!enabled) return false;
        Map<String, Object> event = new HashMap<>();
        event.put(CPDFCustomEventField.IDENTIFIER, "");
        event.put(CPDFCustomEventField.CUSTOM_EVENT_TYPE, widget
                ? CPDFCustomEventType.INTERCEPT_WIDGET_DO_ACTION : CPDFCustomEventType.INTERCEPT_ANNOTATION_DO_ACTION);
        event.put(widget ? CPDFCustomEventField.WIDGET : CPDFCustomEventField.ANNOTATION, annotation);
        // Exclusive routing prevents duplicate delivery during migration from global listeners.
        if (scopedCallback) {
            CPDFCustomEventCallback receiver = callback;
            if (receiver != null) receiver.click(event);
        } else {
            CPDFCustomEventCallbackHelper.getInstance().notifyClick("", event);
        }
        return true;
    }

    public static void release(ReaderView reader) {
        CPDFActionContext context = get(reader);
        if (context != null) context.callback = null;
        reader.setTag(R.id.tools_action_context, null);
    }
}
