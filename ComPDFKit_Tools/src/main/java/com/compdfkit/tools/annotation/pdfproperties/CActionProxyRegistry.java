package com.compdfkit.tools.annotation.pdfproperties;

import com.compdfkit.core.annotation.*;
import com.compdfkit.core.annotation.form.*;
import com.compdfkit.ui.proxy.*;
import com.compdfkit.ui.reader.CPDFReaderView;
import com.compdfkit.tools.annotation.pdfproperties.pdfnote.*;
import com.compdfkit.tools.annotation.pdfproperties.pdflink.*;
import com.compdfkit.tools.forms.pdfproperties.pdftextfield.CustomTextWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfcheckbox.CustomCheckBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfradiobutton.CustomRadioButtonWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfcombobox.CustomComboBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdflistbox.CustomListBoxWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfpushbutton.CPushButtonWidgetImpl;
import com.compdfkit.tools.forms.pdfproperties.pdfsign.SignatureWidgetImpl;

/** Installed once when CPDFViewCtrl creates its reader. Later customer registration wins. */
public final class CActionProxyRegistry {
    private CActionProxyRegistry() {}

    public static void register(CPDFReaderView reader) {
        CPDFAnnotImplRegistry registry = reader.getAnnotImplRegistry();
        registry.registImpl(CPDFTextAnnotation.class, CPDFtextAnnotImpl.class);
        registry.registImpl(CPDFLinkAnnotation.class, CPDFCustomLinkAnnotImpl.class);
        registry.registAttachHelper(CPDFTextAnnotation.class, CPDFtextAnnotAttachHelper.class);
        registry.registAttachHelper(CPDFLinkAnnotation.class, CLinkAnnotAttachHelper.class);
        registry.registImpl(CPDFSquareAnnotation.class, Square.class);
        registry.registImpl(CPDFCircleAnnotation.class, Circle.class);
        registry.registImpl(CPDFLineAnnotation.class, Line.class);
        registry.registImpl(CPDFHighlightAnnotation.class, Highlight.class);
        registry.registImpl(CPDFUnderlineAnnotation.class, Underline.class);
        registry.registImpl(CPDFStrikeoutAnnotation.class, Strikeout.class);
        registry.registImpl(CPDFSquigglyAnnotation.class, Squiggly.class);
        registry.registImpl(CPDFFreetextAnnotation.class, Freetext.class);
        registry.registImpl(CPDFInkAnnotation.class, Ink.class);
        registry.registImpl(CPDFStampAnnotation.class, Stamp.class);
        registry.registImpl(CPDFSoundAnnotation.class, Sound.class);
        registry.registImpl(CPDFRedactAnnotation.class, Redact.class);
        registry.registImpl(CPDFTextWidget.class, CustomTextWidgetImpl.class);
        registry.registImpl(CPDFCheckboxWidget.class, CustomCheckBoxWidgetImpl.class);
        registry.registImpl(CPDFRadiobuttonWidget.class, CustomRadioButtonWidgetImpl.class);
        registry.registImpl(CPDFComboboxWidget.class, CustomComboBoxWidgetImpl.class);
        registry.registImpl(CPDFListboxWidget.class, CustomListBoxWidgetImpl.class);
        registry.registImpl(CPDFPushbuttonWidget.class, CPushButtonWidgetImpl.class);
        registry.registImpl(CPDFSignatureWidget.class, SignatureWidgetImpl.class);
    }

    public static class Square extends CPDFSquareAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Circle extends CPDFCircleAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Line extends CPDFLineAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Highlight extends CPDFHighlightAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Underline extends CPDFUnderlineAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Strikeout extends CPDFStrikeoutAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Squiggly extends CPDFSquigglyAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Freetext extends CPDFFreetextAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Ink extends CPDFInkAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Stamp extends CPDFStampAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }

    public static class Sound extends CPDFSoundAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }
    public static class Redact extends CPDFRedactAnnotImpl {
        @Override protected boolean onInterceptDefaultAction() {
            return CAnnotationActionInterceptor.intercept(readerView, onGetAnnotation());
        }
    }
}
